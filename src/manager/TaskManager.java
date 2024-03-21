package manager;

import task.Epic;
import task.SubTask;
import task.Task;

import java.util.List;
import java.util.Set;

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

    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubTask(SubTask subTask);

    void deleteTaskByID(Integer id);

    void deleteEpicByID(Integer id);

    void deleteSubTaskByID(Integer id);

    List<SubTask> getSubTaskByEpic(Epic epic);

    List<Task> getTasks();

    List<Epic> getEpics();

    List<SubTask> getSubTasks();

    List<Task> getHistory();

    Set<Task> getPrioritizedTasks();
}
