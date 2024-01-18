package manager;

import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TaskMangerTest {
    private static final HistoryManager historyManager = Managers.getDefaultHistory();
    private static final TaskManager taskManager = Managers.getDefault();

    @Test
    public void testTaskManager() {
        Task task1 = new Task("task1", "desc", taskManager.getNewId());
        Task task2 = new Task("task2", "desc", taskManager.getNewId());
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        task1.setName("changed name");
        task1.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task1);

        Epic epic1 = new Epic("Epic1", "desc", taskManager.getNewId());
        Epic epic2 = new Epic("Epic2", "desc", taskManager.getNewId());
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        SubTask subTask = new SubTask("subTaskDone", "desc", taskManager.getNewId(), epic2.getId());
        SubTask subTask2 = new SubTask("subTask2", "desc", taskManager.getNewId(), epic2.getId());
        taskManager.addSubTask(subTask);
        taskManager.addSubTask(subTask2);
        subTask.setStatus(Status.DONE);
        subTask2.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask);
        taskManager.updateSubTask(subTask2);

        System.out.println("Задачи:");
        for (int id : taskManager.getTasks().keySet()) {
            System.out.println(taskManager.getTaskByID(id));
        }
        System.out.println("Эпики:");
        for (int id :taskManager.getEpics().keySet()) {
            System.out.println(taskManager.getEpicByID(id));
        }
        System.out.println("Подзадачи:");
        for (int id : taskManager.getSubTasks().keySet()) {
            System.out.println(taskManager.getSubTaskByID(id));
        }

        System.out.println("История:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", taskManager.getNewId());
        taskManager.addTask(task);
        final int taskId = task.getId();

        final Task savedTask = taskManager.getTaskByID(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final HashMap<Integer, Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }
}
