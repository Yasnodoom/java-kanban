package manager;

import task.Epic;
import task.SubTask;
import task.Task;

import java.util.*;
import java.util.stream.Collectors;

import static task.Status.*;

public class InMemoryTaskManager implements TaskManager {
    protected int idCounter = 0;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, SubTask> subTasks = new HashMap<>();
    protected final Set<Task> prioritizedTasks = new TreeSet<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    private static final String ERROR_MESSAGE =
            "%s %s невозможно из за пересечения по времени с уже существующими тасками";

    @Override
    public Task getTaskByID(int id) {
        historyManager.add(tasks.get(id));
        return tasks.getOrDefault(id, null);
    }

    @Override
    public Epic getEpicByID(int id) {
        historyManager.add(epics.get(id));
        return epics.getOrDefault(id, null);
    }

    @Override
    public SubTask getSubTaskByID(int id) {
        historyManager.add(subTasks.get(id));
        return subTasks.getOrDefault(id, null);
    }

    @Override
    public void deleteTasks() {
        if (tasks.isEmpty())
            return;

        tasks.values().forEach(prioritizedTasks::remove);
        tasks.keySet().stream().peek(historyManager::remove).forEach(tasks::remove);
    }

    @Override
    public void deleteEpics() {
        epics.keySet().forEach(historyManager::remove);
        subTasks.keySet().forEach(historyManager::remove);
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void deleteSubTasks() {
        subTasks.keySet().forEach(this::deleteSubTaskByID);
        subTasks.values().forEach(prioritizedTasks::remove);
    }

    @Override
    public void updateTask(Task task) {
        if (isIntersectWithAllTask(task))
            throw new UnsupportedOperationException(String.format(ERROR_MESSAGE, "Update", task.getName()));

        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic task) {
        epics.put(task.getId(), task);
    }

    @Override
    public void updateSubTask(SubTask task) {
        if (isIntersectWithAllTask(task))
            throw new UnsupportedOperationException(String.format(ERROR_MESSAGE, "Update subtask", task.getName()));

        subTasks.put(task.getId(), task);
        autoUpdateEpicStatus(task.getEpicID());
        autoUpdateEpicDuration(task.getEpicID());
    }

    @Override
    public void addTask(Task task) {
        if (isIntersectWithAllTask(task))
            throw new UnsupportedOperationException(String.format(ERROR_MESSAGE, "Добавление", task.getName()));

        task.setId(getNewId());
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(getNewId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubTask(SubTask subTask) {
        if (isIntersectWithAllTask(subTask))
            throw new UnsupportedOperationException(String.format(ERROR_MESSAGE, "Добавление сабтаска", subTask.getName()));

        subTask.setId(getNewId());
        epics.get(subTask.getEpicID()).setSubTaskID(subTask.getId());
        subTasks.put(subTask.getId(), subTask);
        autoUpdateEpicDuration(subTask.getEpicID());
        prioritizedTasks.add(subTask);
    }

    @Override
    public void deleteTaskByID(Integer id) {
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);

    }

    @Override
    public void deleteEpicByID(Integer id) {
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubTaskByID(Integer id) {
        int epicID = subTasks.get(id).getEpicID();

        prioritizedTasks.remove(subTasks.get(id));
        epics.get(epicID).getSubTaskIDs().remove(id);
        subTasks.remove(id);
        historyManager.remove(id);
        autoUpdateEpicStatus(epicID);
    }

    @Override
    public ArrayList<SubTask> getSubTaskByEpic(Epic epic) {
        return epic.getSubTaskIDs()
                .stream()
                .map(subTasks::get)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(this.tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    private Integer getNewId() {
        return idCounter++;
    }

    private void autoUpdateEpicStatus(Integer epicID) {
        Epic epic = epics.get(epicID);
        boolean hasNew = false;
        boolean hasDone = false;

        if (epic.getSubTaskIDs().isEmpty()) {
            return;
        }

        for (Integer subTaskID : epic.getSubTaskIDs()) {
            SubTask subTask = subTasks.get(subTaskID);

            if (subTask.getStatus().equals(IN_PROGRESS)) {
                epic.setStatus(IN_PROGRESS);
                return;
            }
            if (subTask.getStatus().equals(NEW)) {
                hasNew = true;
                if (hasDone) {
                    epic.setStatus(IN_PROGRESS);
                    return;
                }
            }
            if (subTask.getStatus().equals(DONE)) {
                hasDone = true;
                if (hasNew) {
                    epic.setStatus(IN_PROGRESS);
                    return;
                }
            }
        }
        if (!hasDone) {
            return;
        }
        epic.setStatus(DONE);
    }

    private void autoUpdateEpicDuration(Integer epicID) {
        final Epic epic = epics.get(epicID);
        if (epic.getSubTaskIDs().isEmpty())
            return;

        epic.getSubTaskIDs()
                .stream()
                .map(subTasks::get)
                .forEach(subTask -> updateEpicTiming(epic.getId(), subTask.getId()));
    }

    private void updateEpicTiming(Integer epicID, Integer subTaskID) {
        Epic epic = epics.get(epicID);
        SubTask subTask = subTasks.get(subTaskID);
        if (epic.getTimedSubTask().contains(subTaskID))
            return;

        if (epic.getStartTime() == null || epic.getStartTime().isAfter(subTask.getStartTime()))
            epic.setStartTime(subTask.getStartTime());
        if (epic.getEndTime() == null || epic.getEndTime().isBefore(subTask.getEndTime()))
            epic.setEndTime(subTask.getEndTime());
        epic.setDuration(epic.getDuration().plus(subTask.getDuration()));
        epic.setTimedSubTask(subTaskID);
    }

    private Boolean isIntersect(Task left, Task right) {
        return left.getEndTime().isAfter(right.getStartTime())
                && left.getEndTime().isBefore(right.getEndTime());
    }

    private boolean isIntersectWithAllTask(Task task) {
        return getPrioritizedTasks().stream().anyMatch(pTask -> isIntersect(pTask, task));
    }

}
