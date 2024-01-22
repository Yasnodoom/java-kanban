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
    void setUp() {
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault();
    }

    @Test
    void add() {
        Task task1 = new Task("task1", "desc");
        historyManager.add(task1);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void addMore10HistoryElements() {
        for (int i = 0; i < 13; i++) {
            Task task1 = new Task("task" + i, "desc");
            historyManager.add(task1);
        }
        assertEquals(10, historyManager.getHistory().size());
    }

    @Test
    void savePrevisionVersion() {
        Task task = new Task("task", "desc");
        taskManager.addTask(task);
        historyManager.add(task);
        task.setDescription("changed");
        assertEquals(taskManager.getTaskByID(task.getId()).getDescription(), "changed");
        assertEquals(historyManager.getHistory().getFirst().getDescription(), "desc");
    }
}
