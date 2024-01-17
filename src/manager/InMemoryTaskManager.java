package manager;

import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class InMemoryTaskManager implements TaskManager {

    private static int idCounter = 0;
    private static final byte COUNT_HISTORY_ELEMENTS = 10;

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final ArrayList<Integer> history = new ArrayList<>(COUNT_HISTORY_ELEMENTS);


    @Override
    public Task getTaskByID(int id) {
        addHistory(id);
        return tasks.getOrDefault(id, null);
    }

    @Override
    public Epic getEpicByID(int id) {
        addHistory(id);
        return epics.getOrDefault(id, null);
    }

    @Override
    public SubTask getSubTaskByID(int id) {
        addHistory(id);
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
    public void createTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubTask(SubTask subTask) {
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
    public ArrayList<Task> getHistory() {
        ArrayList<Task> taskHistory = new ArrayList<>(COUNT_HISTORY_ELEMENTS);
        for(int id : history){
            taskHistory.add(getTaskByID(id));
        }
        return taskHistory;
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

    private void addHistory(int id) {
        if (history.size() <= COUNT_HISTORY_ELEMENTS) {
            history.add(0,id);
        } else {
            history.remove(COUNT_HISTORY_ELEMENTS-1);
            history.add(0,id);
        }
    }

}
