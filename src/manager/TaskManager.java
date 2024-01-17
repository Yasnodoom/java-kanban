package manager;

import task.Epic;
import task.SubTask;
import task.Task;

import java.util.HashMap;
import java.util.List;

public interface TaskManager {
    Task getTaskByID(int id);

    Epic getEpicByID(int id);

    SubTask getSubTaskByID(int id);

    void deleteTasks();

    void deleteEpics();

    void deleteSubTasks();

    void updateTask(Task task);

    void updateEpic(Epic task);

    void updateSubTask(SubTask task);

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubTask(SubTask subTask);

    void deleteTaskByID(Integer id);

    void deleteEpicByID(Integer id);

    void deleteSubTaskByID(Integer id);

    HashMap<Integer, SubTask> getSubTaskByEpic(Epic epic);

    HashMap<Integer, Task> getTasks();

    HashMap<Integer, Epic> getEpics();

    HashMap<Integer, SubTask> getSubTasks();

    List<Task> getHistory();
}