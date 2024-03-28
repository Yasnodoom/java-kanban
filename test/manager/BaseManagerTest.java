package manager;

import org.junit.jupiter.api.Test;
import task.Epic;
import task.Status;
import task.SubTask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public abstract class BaseManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Map<Integer, Task> tasks = Map.of(
            1, new Task("task1", "desc", Duration.ofSeconds(10), LocalDateTime.now()),
            2, new Task("task2", "desc", Duration.ofSeconds(10), LocalDateTime.now().plusMinutes(1)),
            3, new Task("task3", "desc", Duration.ofSeconds(10), LocalDateTime.now().plusMinutes(2))
    );
    protected Map<Integer, Epic> epics = Map.of(
            1, new Epic("epic1", "desc"),
            2, new Epic("epic2", "desc")
    );

    @Test
    public void addNewTask() {
        taskManager.addTask(tasks.get(1));
        final int taskId = tasks.get(1).getId();
        final Task savedTask = taskManager.getTaskByID(taskId);

        assertNotNull(savedTask);
        assertEquals(tasks.get(1), savedTask);
        assertNotNull(taskManager.getTasks());
        assertEquals(1, taskManager.getTasks().size());
        assertEquals(tasks.get(1), taskManager.getTasks().get(0));
    }
    @Test
    public void taskEqualsIfIdEquals() {
        taskManager.addTask(tasks.get(1));
        int id = tasks.get(1).getId();
        assertEquals(taskManager.getTaskByID(id), taskManager.getTaskByID(id));
    }

    @Test
    public void epicEqualsIfIdEquals() {
        taskManager.addEpic(epics.get(1));
        int id = epics.get(1).getId();
        assertEquals(taskManager.getTaskByID(id), taskManager.getTaskByID(id));
    }

    @Test
    public void subTaskEqualsIfIdEquals() {
        taskManager.addEpic(epics.get(1));
        SubTask task = new SubTask("subTask", "desc", epics.get(1).getId(), Duration.ofSeconds(100), LocalDateTime.now());
        taskManager.addSubTask(task);
        int id = task.getId();
        assertEquals(taskManager.getTaskByID(id), taskManager.getTaskByID(id));
    }

    @Test
    public void errorIfEpicAddLikeSubTaskToHimself() {
        taskManager.addEpic(epics.get(1));
        epics.get(1).setSubTaskID(epics.get(1).getId());
        assertEquals(0, epics.get(1).getSubTaskIDs().size());
    }

    @Test
    public void errorIfSubTaskIsEpicToHimself() {
        taskManager.addEpic(epics.get(1));
        int epicId = taskManager.getEpics().getLast().getId();
        SubTask subtask = new SubTask("subTask", "desc", epicId, Duration.ofSeconds(100), LocalDateTime.now());
        taskManager.addSubTask(subtask);
        int subTaskId = taskManager.getSubTasks().getLast().getId();
        subtask.setEpicID(subTaskId);
        taskManager.updateSubTask(subtask);

        assertNotEquals(subtask.getEpicID(), subTaskId);
    }

    @Test
    public void searchById() {
        taskManager.addEpic(epics.get(1));
        assertNotNull(taskManager.getEpicByID(epics.get(1).getId()));
    }

    @Test
    public void conflictIfHardId() {
        taskManager.addEpic(epics.get(1));
        epics.get(2).setId(999);
        taskManager.addEpic(epics.get(2));
        taskManager.updateEpic(epics.get(2));
        assertEquals(2, taskManager.getEpics().size());
        assertNull(taskManager.getEpicByID(999));
        assertNotNull(taskManager.getEpicByID(epics.get(1).getId()));
        assertNotNull(taskManager.getEpicByID(epics.get(2).getId()));
    }

    @Test
    public void epicStatusIfAllSubtaskIsNEWShouldBeNew() {
        taskManager.addEpic(epics.get(1));
        SubTask subTask1 = new SubTask("sub1", "desc", epics.get(1).getId(),
                Duration.ofSeconds(10), LocalDateTime.now().plusMinutes(1));
        SubTask subTask2 = new SubTask("sub2", "desc", epics.get(1).getId(),
                Duration.ofSeconds(10), LocalDateTime.now().plusMinutes(2));
        SubTask subTask3 = new SubTask("sub3", "desc", epics.get(1).getId(),
                Duration.ofSeconds(10), LocalDateTime.now().plusMinutes(3));
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);
        assertEquals(Status.NEW, epics.get(1).getStatus());
    }

    @Test
    public void epicStatusIfAllSubtaskIsDoneShouldBeDone() {
        taskManager.addEpic(epics.get(1));
        SubTask subTask1 = new SubTask("sub1", "desc", epics.get(1).getId(),
                Duration.ofSeconds(10), LocalDateTime.now().plusMinutes(1));
        SubTask subTask2 = new SubTask("sub2", "desc", epics.get(1).getId(),
                Duration.ofSeconds(10), LocalDateTime.now().plusMinutes(2));
        SubTask subTask3 = new SubTask("sub3", "desc", epics.get(1).getId(),
                Duration.ofSeconds(10), LocalDateTime.now().plusMinutes(3));
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);
        subTask1.setStatus(Status.DONE);
        subTask2.setStatus(Status.DONE);
        subTask3.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);
        taskManager.updateSubTask(subTask3);

        assertEquals(Status.DONE, epics.get(1).getStatus());
    }

    @Test
    public void epicStatusIfAllSubtaskIsInProgressShouldBeInProgress() {
        taskManager.addEpic(epics.get(1));
        SubTask subTask1 = new SubTask("sub1", "desc", epics.get(1).getId(),
                Duration.ofSeconds(10), LocalDateTime.now().plusMinutes(1));
        SubTask subTask2 = new SubTask("sub2", "desc", epics.get(1).getId(),
                Duration.ofSeconds(10), LocalDateTime.now().plusMinutes(2));
        SubTask subTask3 = new SubTask("sub3", "desc", epics.get(1).getId(),
                Duration.ofSeconds(10), LocalDateTime.now().plusMinutes(3));
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);
        subTask1.setStatus(Status.IN_PROGRESS);
        subTask2.setStatus(Status.IN_PROGRESS);
        subTask3.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);
        taskManager.updateSubTask(subTask3);

        assertEquals(Status.IN_PROGRESS, epics.get(1).getStatus());
    }

    @Test
    public void epicStatusIfAllSubtaskIsNewAndDOneShouldBeInProgress() {
        taskManager.addEpic(epics.get(1));
        SubTask subTask1 = new SubTask("sub1", "desc", epics.get(1).getId(),
                Duration.ofSeconds(10), LocalDateTime.now().plusMinutes(1));
        SubTask subTask2 = new SubTask("sub2", "desc", epics.get(1).getId(),
                Duration.ofSeconds(10), LocalDateTime.now().plusMinutes(2));
        SubTask subTask3 = new SubTask("sub3", "desc", epics.get(1).getId(),
                Duration.ofSeconds(10), LocalDateTime.now().plusMinutes(3));
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);
        subTask1.setStatus(Status.NEW);
        subTask2.setStatus(Status.DONE);
        subTask3.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);
        taskManager.updateSubTask(subTask3);

        assertEquals(Status.IN_PROGRESS, epics.get(1).getStatus());
    }

    @Test
    public void errorIfStartTimeIntersectForTask(){
        Task task1 =  new Task("task1", "desc", Duration.ofSeconds(50), LocalDateTime.now());
        Task task2 =  new Task("task2", "desc", Duration.ofSeconds(100), LocalDateTime.now());
        taskManager.addTask(task1);
        assertThrows(RuntimeException.class, () ->  taskManager.addTask(task2));
    }

    @Test
    public void errorIfStartTimeIntersectForSubtask(){
        taskManager.addEpic(epics.get(1));
        SubTask task1 =  new SubTask("task1", "desc", epics.get(1).getId(),
                Duration.ofSeconds(50), LocalDateTime.now());
        SubTask task2 =  new SubTask("task2", "desc", epics.get(1).getId(),
                Duration.ofSeconds(100), LocalDateTime.now());
        taskManager.addSubTask(task1);
        assertThrows(RuntimeException.class, () ->  taskManager.addSubTask(task2));
    }

    @Test
    public void errorIfStartTimeIntersectForSubtaskAndTask(){
        Task task1 =  new Task("task1", "desc", Duration.ofSeconds(50), LocalDateTime.now());
        SubTask task2 =  new SubTask("task2", "desc", 1, Duration.ofSeconds(100), LocalDateTime.now());
        taskManager.addTask(task1);
        assertThrows(RuntimeException.class, () ->  taskManager.addSubTask(task2));
    }

    @Test
    public void getPrioritizedTasksShouldReturnSortedTaskByTime() {
        taskManager.addEpic(epics.get(1));
        Task task1 =  new Task("task1", "4", Duration.ofSeconds(10), LocalDateTime.now().plusMinutes(40));
        Task task2 =  new Task("task2", "1", Duration.ofSeconds(10), LocalDateTime.now());
        SubTask subtask1 =  new SubTask("subtask1", "3", epics.get(1).getId(),
                Duration.ofSeconds(10), LocalDateTime.now().plusMinutes(30));
        SubTask subtask2 =  new SubTask("subtask2", "2", epics.get(1).getId(),
                Duration.ofSeconds(10), LocalDateTime.now().plusMinutes(10));
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addSubTask(subtask1);
        taskManager.addSubTask(subtask2);

        final Iterator<Task> it =  taskManager.getPrioritizedTasks().iterator();
        for (int i = 1; i <= 4; i++) {
            assertEquals(it.next().getDescription(), String.valueOf(i));
        }
        taskManager.deleteTaskByID(task2.getId());
        assertEquals(3, taskManager.getPrioritizedTasks().size());
    }

    @Test
    public void ifDeleteTaskPrioritizedDeleteToo() {
        taskManager.addTask(tasks.get(1));
        taskManager.addTask(tasks.get(2));
        assertEquals(2, taskManager.getPrioritizedTasks().size());
        taskManager.deleteTaskByID(tasks.get(2).getId());
        assertEquals(1, taskManager.getPrioritizedTasks().size());
    }
}
