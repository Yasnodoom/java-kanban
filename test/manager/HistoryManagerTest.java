package manager;

import org.junit.jupiter.api.Test;
import task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HistoryManagerTest {
    private static final HistoryManager historyManager = Managers.getDefaultHistory();
    private static final TaskManager taskManager = Managers.getDefault();

    @Test
    void add() {
        Task task1 = new Task("task1", "desc", taskManager.getNewId());
        historyManager.add(task1);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void addMore10HistoryElements() {
        Task task1 = new Task("task1", "desc", taskManager.getNewId());
        Task task2 = new Task("task2", "desc", taskManager.getNewId());
        Task task3 = new Task("task3", "desc", taskManager.getNewId());
        Task task4 = new Task("task4", "desc", taskManager.getNewId());
        Task task5 = new Task("task5", "desc", taskManager.getNewId());
        Task task6 = new Task("task6", "desc", taskManager.getNewId());
        Task task7 = new Task("task7", "desc", taskManager.getNewId());
        Task task8 = new Task("task8", "desc", taskManager.getNewId());
        Task task9 = new Task("task9", "desc", taskManager.getNewId());
        Task task10 = new Task("task10", "desc", taskManager.getNewId());
        Task task11 = new Task("task11", "desc", taskManager.getNewId());
        Task task12 = new Task("task12", "desc", taskManager.getNewId());
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);
        historyManager.add(task5);
        historyManager.add(task6);
        historyManager.add(task7);
        historyManager.add(task8);
        historyManager.add(task9);
        historyManager.add(task10);
        historyManager.add(task11);
        historyManager.add(task12);
        assertEquals(10, historyManager.getHistory().size());
    }
}
