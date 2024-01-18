package manager;

public class Managers {
    private static final InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
    private static final InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

    public static TaskManager getDefault() {
        return inMemoryTaskManager;
    }

    public static HistoryManager getDefaultHistory() {
        return inMemoryHistoryManager;
    }
}
