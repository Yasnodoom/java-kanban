import manager.Status;
import manager.TaskManager;
import task.Epic;
import task.Task;
import task.SubTask;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task = taskManager.createTask("task1", "desc");
        Task task2 = taskManager.createTask("task2", "desc");
        taskManager.setTasks(task);
        taskManager.setTasks(task2);
        task.setName("changed name");
        task.setStatus(Status.IN_PROGRESS);
        taskManager.printTasks();
        System.out.println("---");
        taskManager.updateTask(task);
        taskManager.deleteTaskByID(task2.getId());

        Epic epic1 = taskManager.createEpic("Epic1", "desc");
        Epic epic2 = taskManager.createEpic("Epic2", "desc");
        taskManager.setEpics(epic1);
        taskManager.setEpics(epic2);

        SubTask subTask = taskManager.createSubTask("subTaskDone", "desc", epic2.getId());
        subTask.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask);
        taskManager.setSubTasks(taskManager.createSubTask("sub1", "desc", epic1.getId()));
        taskManager.setSubTasks(taskManager.createSubTask("sub2", "desc", epic1.getId()));
        taskManager.setSubTasks(subTask);
        taskManager.printAllTasks();

    }
}
