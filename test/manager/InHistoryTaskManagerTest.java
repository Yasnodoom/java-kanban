package manager;

import org.junit.jupiter.api.BeforeEach;

public class InHistoryTaskManagerTest extends BaseManagerTest<TaskManager> {

    @BeforeEach
    public void setUp() {
        taskManager = Managers.getDefault();
    }
}
