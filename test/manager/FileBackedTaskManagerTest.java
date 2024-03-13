package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    private static final Path empty = Path.of("test", "resources", "empty");
    private static final Path forWriteData = Path.of("test", "resources", "forWriteData.csv");
    private static final Path withData = Path.of("test", "resources", "withData.csv");

    @Test
    public void backManagerLoadEmptyFile() {
        FileBackedTaskManager emptyManager = Managers.getFileBackedTaskManager(empty);
        assertNotNull(emptyManager);
    }

    @Test
    public void backManagerSaveEmptyFile() throws IOException {
        FileBackedTaskManager emptyManager = Managers.getFileBackedTaskManager(empty);
        emptyManager.save();
        assertTrue(Files.readString(empty).isEmpty());
    }

    @Test
    public void backManagerSaveTaskToFile() throws IOException {
        FileBackedTaskManager fileBackedTaskManager = Managers.getFileBackedTaskManager(forWriteData);

        Task task1 = new Task("task1", "desc");
        Task task2 = new Task("task2", "desc");
        fileBackedTaskManager.addTask(task1);
        fileBackedTaskManager.addTask(task2);
        task1.setName("changed name");
        task1.setStatus(Status.IN_PROGRESS);
        fileBackedTaskManager.updateTask(task1);
        Epic epic1 = new Epic("Epic1", "desc");
        Epic epic2 = new Epic("Epic2", "desc");
        fileBackedTaskManager.addEpic(epic1);
        fileBackedTaskManager.addEpic(epic2);
        SubTask subTask = new SubTask("subTaskDone", "desc", epic2.getId());
        SubTask subTask2 = new SubTask("subTask2", "desc", epic2.getId());
        fileBackedTaskManager.addSubTask(subTask);
        fileBackedTaskManager.addSubTask(subTask2);
        subTask.setStatus(Status.DONE);
        subTask2.setStatus(Status.DONE);
        fileBackedTaskManager.updateSubTask(subTask);
        fileBackedTaskManager.updateSubTask(subTask2);

        fileBackedTaskManager.getTaskByID(task1.getId());
        fileBackedTaskManager.getEpicByID(epic1.getId());
        fileBackedTaskManager.getSubTaskByID(subTask.getId());

        fileBackedTaskManager.save();

        String file = Files.readString(forWriteData);
        Assertions.assertTrue(file.contains("id,type,name,status,description,epic"));
        Assertions.assertTrue(file.contains("0,TASK,changed name,IN_PROGRESS,desc"));
        Assertions.assertTrue(file.contains("3,EPIC,Epic2,DONE,desc,[4, 5]"));
        Assertions.assertTrue(file.contains("4,SUBTASK,subTaskDone,DONE,desc,3"));
        Assertions.assertTrue(file.contains("0,2,4"));
    }

    @Test
    public void backManagerLoadFromFile() {
        FileBackedTaskManager fileBackedTaskManager = CSVTaskFormatter.loadFromFile(withData);
        assertNotNull(fileBackedTaskManager);
        assertEquals(fileBackedTaskManager.getHistory().size(), 3);
        assertEquals(fileBackedTaskManager.getTaskByID(0).getName(), "changed name");
        assertEquals(fileBackedTaskManager.getEpicByID(3).getStatus(), Status.DONE);
        assertEquals(fileBackedTaskManager.getSubTaskByID(4).getEpicID(), 3);
    }

}