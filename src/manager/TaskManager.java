package manager;

import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.util.*;

public class TaskManager {

    private static int idCounter = 0;

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();

    public Task getTaskByID(Integer taskID) {
        return tasks.getOrDefault(taskID, null);
    }

    public Epic getEpicByID(Integer epicID) {
        return epics.getOrDefault(epicID, null);
    }

    public SubTask getSubTaskByID(Integer subTaskID) {
        return subTasks.getOrDefault(subTaskID, null);
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteEpics() {
        epics.clear();
        subTasks.clear();
    }

    public void deleteSubTasks() {
        Set<Integer> IDs = new HashSet<>(subTasks.keySet());
        for (Integer id : IDs) {
            deleteSubTaskByID(id);
        }
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic task) {
        epics.put(task.getId(), task);
    }

    public void updateSubTask(SubTask task) {
        subTasks.put(task.getId(), task);
        autoUpdateEpicStatus(task.getEpicID());
    }

    public void createTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void createSubTask(SubTask subTask) {
        epics.get(subTask.getEpicID()).setSubTaskID(subTask.getId());
        subTasks.put(subTask.getId(), subTask);
    }

    public void deleteTaskByID(Integer id) {
        tasks.remove(id);
    }

    public void deleteEpicByID(Integer id) {
        epics.remove(id);
    }

    public void deleteSubTaskByID(Integer id) {
        int epicID = subTasks.get(id).getEpicID();

        epics.get(epicID).getSubTaskIDs().remove(id);
        subTasks.remove(id);
        autoUpdateEpicStatus(epicID);
    }

    public HashMap<Integer, SubTask> getSubTaskByEpic(Epic epic) {
        HashMap<Integer, SubTask> subTaskByEpic = new HashMap<>();

        for (Integer id : epic.getSubTaskIDs()) {
            subTaskByEpic.put(id, subTasks.get(id));
        }
        return subTaskByEpic;
    }

    public Integer getNewID() {
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

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

}
