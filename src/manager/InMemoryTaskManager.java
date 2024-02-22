package manager;

import task.Epic;
import task.SubTask;
import task.Task;

import java.util.*;

import static task.Status.*;

public class InMemoryTaskManager implements TaskManager {
    private int idCounter = 0;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();


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
        for(int id : tasks.keySet())
            historyManager.remove(id);
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        for (int id : epics.keySet())
            historyManager.remove(id);

        for (int id : subTasks.keySet())
            historyManager.remove(id);

        epics.clear();
        subTasks.clear();
    }

    @Override
    public void deleteSubTasks() {
        Set<Integer> IDs = new HashSet<>(subTasks.keySet());
        for (Integer id : IDs) {
            deleteSubTaskByID(id);
        }
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic task) {
        epics.put(task.getId(), task);
    }

    @Override
    public void updateSubTask(SubTask task) {
        subTasks.put(task.getId(), task);
        autoUpdateEpicStatus(task.getEpicID());
    }

    @Override
    public void addTask(Task task) {
        task.setId(getNewId());
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {

        epic.setId(getNewId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubTask(SubTask subTask) {
        subTask.setId(getNewId());
        epics.get(subTask.getEpicID()).setSubTaskID(subTask.getId());
        subTasks.put(subTask.getId(), subTask);
    }

    @Override
    public void deleteTaskByID(Integer id) {
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

        epics.get(epicID).getSubTaskIDs().remove(id);
        subTasks.remove(id);
        historyManager.remove(id);
        autoUpdateEpicStatus(epicID);
    }

    @Override
    public ArrayList<SubTask> getSubTaskByEpic(Epic epic) {
        ArrayList<SubTask> subTaskByEpic = new ArrayList<>();

        for (Integer id : epic.getSubTaskIDs()) {
            subTaskByEpic.add(subTasks.get(id));
        }
        return subTaskByEpic;
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
}
