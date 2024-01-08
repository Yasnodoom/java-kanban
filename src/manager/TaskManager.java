package manager;

import task.Epic;
import task.SubTask;
import task.Task;

import java.util.HashMap;

public class TaskManager {

    private static int idCounter = 0;

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();

    public void printTasks() {
        for (Integer id : tasks.keySet()) {
            System.out.println(tasks.get(id));
        }
    }

    public void printEpics() {
        for (Integer id : epics.keySet()) {
            System.out.println(epics.get(id));
        }
    }

    public void printSubTasks() {
        for (Integer id : subTasks.keySet()) {
            System.out.println(subTasks.get(id));
        }
    }

    public void printAllTasks() {
        printTasks();
        printEpics();
        printSubTasks();
    }

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
    }

    public void deleteSubTasks() {
        subTasks.clear();
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

    public Task createTask(String name, String description) {
        return new Task(name, description, getNewID());
    }

    public Epic createEpic(String name, String description) {
        return new Epic(name, description, getNewID());
    }

    public SubTask createSubTask(String name, String description, Integer epicID) {
        int id = getNewID();

        SubTask subTask = new SubTask(name, description, id, epicID);
        epics.get(epicID).setSubTaskID(id);
        return subTask;
    }

    public void deleteTaskByID(Integer id) {
        tasks.remove(id);
    }

    public void deleteEpicByID(Integer id) {
        epics.remove(id);
    }

    public void deleteSubTaskByID(Integer id) {
        subTasks.remove(id);
    }

    public HashMap<Integer, SubTask> getSubTaskByEpic(Epic epic) {
        HashMap<Integer, SubTask> subTaskByEpic = new HashMap<>();

        for (Integer id : epic.getSubTaskIDs()) {
            subTaskByEpic.put(id, subTasks.get(id));
        }
        return subTaskByEpic;
    }

    private Integer getNewID() {
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

    public void setTasks(Task task) {
        tasks.put(task.getId(), task);
    }

    public void setEpics(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void setSubTasks(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
    }
}
