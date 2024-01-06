import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private static int idCounter = 0;

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();

    private Integer getNewID(){
        return idCounter++;
    }

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

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public void addTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void addEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void addSubTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic task) {
        epics.put(task.getId(), task);
    }

    public void updateSubTask(SubTask task) {
        subTasks.put(task.getId(), task);
        Epic.autoUpdateEpicStatus(task.getEpic());
    }

    public Task createTask(String name, String description) {
        return new Task(name, description, getNewID());
    }

    public Epic createEpic(String name, String description) {
        return new Epic(name, description, getNewID());
    }

    public SubTask createSubTask(String name, String description, Epic epic) {
        SubTask subTask = new SubTask(name, description, getNewID(), epic);
        epic.addSubTask(subTask);
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

    public ArrayList<SubTask> getSubTaskByEpic(Epic epic) {
        return epic.getSubTasks();
    }
}
