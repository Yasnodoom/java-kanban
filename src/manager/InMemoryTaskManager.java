package manager;

import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InMemoryTaskManager implements TaskManager {
    private static int idCounter = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
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
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
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
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubTask(SubTask subTask) {
        epics.get(subTask.getEpicID()).setSubTaskID(subTask.getId());
        subTasks.put(subTask.getId(), subTask);
    }

    @Override
    public void deleteTaskByID(Integer id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpicByID(Integer id) {
        epics.remove(id);
    }

    @Override
    public void deleteSubTaskByID(Integer id) {
        int epicID = subTasks.get(id).getEpicID();

        epics.get(epicID).getSubTaskIDs().remove(id);
        subTasks.remove(id);
        autoUpdateEpicStatus(epicID);
    }

    @Override
    public HashMap<Integer, SubTask> getSubTaskByEpic(Epic epic) {
        HashMap<Integer, SubTask> subTaskByEpic = new HashMap<>();

        for (Integer id : epic.getSubTaskIDs()) {
            subTaskByEpic.put(id, subTasks.get(id));
        }
        return subTaskByEpic;
    }

    @Override
    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    @Override
    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Integer getNewId() {
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

            if (subTask.getStatus() == Status.IN_PROGRESS) {
                epic.setStatus(Status.IN_PROGRESS);
                return;
            }
            if (subTask.getStatus() == Status.NEW) {
                hasNew = true;
                if (hasNew && hasDone) {
                    epic.setStatus(Status.IN_PROGRESS);
                    return;
                }
            }
            if (subTask.getStatus() == Status.DONE) {
                hasDone = true;
                if (hasNew && hasDone) {
                    epic.setStatus(Status.IN_PROGRESS);
                    return;
                }
            }
        }
        if (!hasDone) {
            return;
        }
        epic.setStatus(Status.DONE);
    }
}
