import task.Status;
import manager.TaskManager;
import task.Epic;
import task.SubTask;
import task.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task = new Task("task1", "desc", taskManager.getNewID());
        Task task2 = new Task("task2", "desc", taskManager.getNewID());

        taskManager.createTask(task);
        taskManager.createTask(task2);

        task.setName("changed name");
        task.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task);
        taskManager.deleteTaskByID(task2.getId());

        Epic epic1 = new Epic("Epic1", "desc", taskManager.getNewID());
        Epic epic2 = new Epic("Epic2", "desc", taskManager.getNewID());

        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        SubTask subTask = new SubTask("subTaskDone", "desc", taskManager.getNewID(), epic2.getId());
        SubTask subTask2 = new SubTask("subTask2", "desc", taskManager.getNewID(), epic2.getId());

        taskManager.createSubTask(subTask);
        taskManager.createSubTask(subTask2);
        subTask.setStatus(Status.DONE);
        subTask2.setStatus(Status.DONE);

        taskManager.updateSubTask(subTask);

        taskManager.getSubTaskByEpic(epic2);
        taskManager.deleteSubTasks();

    }
}
