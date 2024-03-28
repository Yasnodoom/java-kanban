package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HistoryManagerTest {
    private HistoryManager historyManager;
    private TaskManager taskManager;
    private Map<String, Task> tasks;

    @BeforeEach
    public void setUp() {
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault();
        tasks = Map.of(
                "Task1", new Task("task1", "desc", Duration.ofSeconds(10), LocalDateTime.now()),
                "Task2", new Task("task2", "desc", Duration.ofSeconds(10), LocalDateTime.now().plusMinutes(1)),
                "Task3", new Task("task3", "desc", Duration.ofSeconds(10), LocalDateTime.now().plusMinutes(2))
        );
    }

    @Test
    public void add() {
        historyManager.add(tasks.get("Task1"));
        final List<Task> history = historyManager.getHistory();

        assertNotNull(history);
        assertEquals(1, history.size());
    }

    @Test
    public void save12Elements() {
        for (int i = 0; i < 12; i++) {
            Task task = new Task("task" + i, "desc", Duration.ofSeconds(10), LocalDateTime.now()
                    .plus(Duration.ofSeconds(20 * i)));
            taskManager.addTask(task);
            historyManager.add(task);
        }
        assertEquals(12, historyManager.getHistory().size());
        assertEquals(historyManager.getHistory().getFirst().getName(), "task0");
        assertEquals(historyManager.getHistory().getLast().getName(), "task11");
    }

    @Test
    public void savePrevisionVersion() {
        taskManager.addTask(tasks.get("Task1"));
        historyManager.add(tasks.get("Task1"));
        tasks.get("Task1").setDescription("changed");
        taskManager.updateTask(tasks.get("Task1"));

        assertEquals(taskManager.getTaskByID(tasks.get("Task1").getId()).getDescription(), "changed");
        assertEquals(historyManager.getHistory().getFirst().getDescription(), "desc");
    }

    @Test
    public void saveOneTaskTwoTimesInHistoryManager() {
        taskManager.addTask(tasks.get("Task1"));
        taskManager.addTask(tasks.get("Task2"));
        historyManager.add(tasks.get("Task1"));
        historyManager.add(tasks.get("Task2"));

        assertEquals(2, historyManager.getHistory().size());

        historyManager.add(tasks.get("Task1"));

        assertEquals(2, historyManager.getHistory().size());
        assertEquals(tasks.get("Task2").getName(), historyManager.getHistory().getFirst().getName());
        assertEquals(tasks.get("Task1").getName(), historyManager.getHistory().getLast().getName());
    }

    @Test
    public void getEmptyHistoryManager() {
        assertNotNull(historyManager.getHistory());
        assertEquals(0, historyManager.getHistory().size());
    }

    @Test
    public void duplicatedHistoryShouldNotExist() {
        historyManager.add(tasks.get("Task1"));
        historyManager.add(tasks.get("Task1"));
        historyManager.add(tasks.get("Task1"));
        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    public void removeFromHistory() {
        taskManager.addTask(tasks.get("Task1"));
        taskManager.addTask(tasks.get("Task2"));
        taskManager.addTask(tasks.get("Task3"));

        historyManager.add(tasks.get("Task1"));
        historyManager.add(tasks.get("Task2"));
        historyManager.add(tasks.get("Task3"));
        assertEquals(3, historyManager.getHistory().size());

        historyManager.remove(tasks.get("Task2").getId());
        assertEquals(2, historyManager.getHistory().size());

        historyManager.remove(tasks.get("Task1").getId());
        assertEquals(1, historyManager.getHistory().size());

        historyManager.remove(tasks.get("Task3").getId());
        assertEquals(0, historyManager.getHistory().size());
    }
}
