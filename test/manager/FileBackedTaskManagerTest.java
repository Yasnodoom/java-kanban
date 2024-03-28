package manager;

import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileBackedTaskManagerTest extends BaseManagerTest<TaskManager> {

    @BeforeEach
    public void setUp() throws IOException {
        Path file = Files.createTempFile("test", "csv");
        taskManager = new FileBackedTaskManager(file);
    }
}
