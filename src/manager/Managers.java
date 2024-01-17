package manager;

public class Managers<T extends TaskManager> {
    private T taskManager;

    public T getDefault() {
        return T;
    }
}
