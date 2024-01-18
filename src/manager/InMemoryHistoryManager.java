package manager;

import task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final byte COUNT_HISTORY_ELEMENTS = 10;
    private final ArrayList<Task> history = new ArrayList<>(COUNT_HISTORY_ELEMENTS);

    @Override
    public void add(Task task) {
        if (history.size() <= COUNT_HISTORY_ELEMENTS-1) {
            history.add(0, task);
        } else {
            history.remove(COUNT_HISTORY_ELEMENTS - 1);
            history.add(0, task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}


