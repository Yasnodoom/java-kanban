package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskMangerTest {
    private TaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void testTaskManager() {
        Task task1 = new Task("task1", "desc", Duration.ofSeconds(100), LocalDateTime.now());
        Task task2 = new Task("task2", "desc", Duration.ofSeconds(200), LocalDateTime.now());
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        task1.setName("changed name");
        task1.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task1);

        Epic epic1 = new Epic("Epic1", "desc");
        Epic epic2 = new Epic("Epic2", "desc");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        SubTask subTask = new SubTask(
                "subTaskDone", "desc", epic2.getId(), Duration.ofSeconds(1000), LocalDateTime.now()
        );
        SubTask subTask2 = new SubTask(
                "subTask2", "desc", epic2.getId(), Duration.ofSeconds(500), LocalDateTime.now()
        );
        taskManager.addSubTask(subTask);
        taskManager.addSubTask(subTask2);
        subTask.setStatus(Status.DONE);
        subTask2.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask);
        taskManager.updateSubTask(subTask2);

        System.out.println("Таски:");
        for (Task task : taskManager.getTasks()) {
            System.out.println(taskManager.getTaskByID(task.getId()));
        }
        System.out.println("Эпики:");
        for (Epic task : taskManager.getEpics()) {
            System.out.println(taskManager.getEpicByID(task.getId()));
        }
        System.out.println("Подзадачи:");
        for (SubTask task : taskManager.getSubTasks()) {
            System.out.println(taskManager.getSubTaskByID(task.getId()));
        }
        System.out.println("История:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }

    @Test
    public void addNewTask() {
        Task task = new Task(
                "Test addNewTask", "Test addNewTask description", Duration.ofSeconds(700), LocalDateTime.now()
        );
        taskManager.addTask(task);
        final int taskId = task.getId();
        final Task savedTask = taskManager.getTaskByID(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void taskEqualsIfIdEquals() {
        Task task = new Task("task", "desc", Duration.ofSeconds(100), LocalDateTime.now());
        taskManager.addTask(task);
        int id = task.getId();
        assertEquals(taskManager.getTaskByID(id), taskManager.getTaskByID(id));
    }

    @Test
    public void epicEqualsIfIdEquals() {
        Epic task = new Epic("epic", "desc");
        taskManager.addEpic(task);
        int id = task.getId();
        assertEquals(taskManager.getTaskByID(id), taskManager.getTaskByID(id));
    }

    @Test
    public void subTaskEqualsIfIdEquals() {
        Epic epic = new Epic("epic", "desc");
        taskManager.addEpic(epic);
        SubTask task = new SubTask("subTask", "desc", epic.getId(), Duration.ofSeconds(100), LocalDateTime.now());
        taskManager.addSubTask(task);
        int id = task.getId();
        assertEquals(taskManager.getTaskByID(id), taskManager.getTaskByID(id));
    }

    @Test
    public void errorIfEpicAddLikeSubTaskToHimself() {
        Epic epic = new Epic("epic", "desc");
        taskManager.addEpic(epic);
        epic.setSubTaskID(epic.getId());
        assertEquals(epic.getSubTaskIDs().size(), 0);
    }

    @Test
    public void errorIfSubTaskIsEpicToHimself() {
        Epic epic = new Epic("epic", "desc");
        taskManager.addEpic(epic);
        int epicId = taskManager.getEpics().getLast().getId();
        SubTask subtask = new SubTask("subTask", "desc", epicId, Duration.ofSeconds(100), LocalDateTime.now());
        taskManager.addSubTask(subtask);

        int subTaskId = taskManager.getSubTasks().getLast().getId();
        subtask.setEpicID(subTaskId);
        taskManager.updateSubTask(subtask);

        assertNotEquals(subtask.getEpicID(), subTaskId);
    }

    @Test
    public void searchById() {
        Epic epic = new Epic("epic", "desc");
        taskManager.addEpic(epic);
        assertNotNull(taskManager.getEpicByID(epic.getId()));
    }

    @Test
    public void conflictIfHardId() {
        Epic epic = new Epic("epic", "desc");
        taskManager.addEpic(epic);
        Epic epic2 = new Epic("epic2", "desc");
        epic2.setId(999);
        taskManager.addEpic(epic2);
        taskManager.updateEpic(epic2);
        assertEquals(taskManager.getEpics().size(), 2);
        assertNull(taskManager.getEpicByID(999));
        assertNotNull(taskManager.getEpicByID(epic.getId()));
    }

}
