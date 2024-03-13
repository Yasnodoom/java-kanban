package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HistoryManagerTest {
    private HistoryManager historyManager;
    private TaskManager taskManager;

    @BeforeEach
    public void setUp() {
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault();
    }

    @Test
    public void add() {
        Task task1 = new Task("task1", "desc");
        historyManager.add(task1);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    public void save12Elements() {
        for (int i = 0; i < 12; i++) {
            Task task = new Task("task" + i, "desc");
            taskManager.addTask(task);
            historyManager.add(task);
        }
        assertEquals(12, historyManager.getHistory().size());
        assertEquals(historyManager.getHistory().getFirst().getName(), "task0");
        assertEquals(historyManager.getHistory().getLast().getName(), "task11");
    }

    @Test
    public void savePrevisionVersion() {
        Task task = new Task("task", "desc");
        taskManager.addTask(task);
        historyManager.add(task);
        task.setDescription("changed");
        taskManager.updateTask(task);
        assertEquals(taskManager.getTaskByID(task.getId()).getDescription(), "changed");
        assertEquals(historyManager.getHistory().getFirst().getDescription(), "desc");
    }

    @Test
    public void saveOneTaskTwoTimesInHistoryManager() {
        Task firstTask = new Task("firstTask", "firstTask");
        Task secondTask = new Task("secondTask", "secondTask");
        taskManager.addTask(firstTask);
        taskManager.addTask(secondTask);
        historyManager.add(firstTask);
        historyManager.add(secondTask);
        assertEquals(2, historyManager.getHistory().size());
        historyManager.add(firstTask);
        assertEquals(2, historyManager.getHistory().size());
        assertEquals("secondTask", historyManager.getHistory().getFirst().getName());
        assertEquals("firstTask", historyManager.getHistory().getLast().getName());
    }
}
