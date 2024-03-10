package manager;

import task.*;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static task.TypeTask.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final String BACKED_FILE;

    public FileBackedTaskManager(String file) {
        BACKED_FILE = file;
    }

    public void save() {

    }

    public String toString(Task task) {
        return switch (TypeTask.valueOf(task.getClass().getName())) {
            case TASK -> String.format("%d,%S,$s,%s,%s", task.getId(), TASK, task.getName(), task.getDescription());
            case EPIC -> {
                Epic epic = (Epic) task;
                yield String.format("%d,%S,$s,%s,%s,%s", epic.getId(), EPIC, epic.getName(),
                        epic.getDescription(), epic.getSubTaskIDs());
            }
            case SUBTASK -> {
                SubTask subTask = (SubTask) task;
                yield String.format("%d,%S,$s,%s,%s,%d", subTask.getId(), SUBTASK, subTask.getName(),
                        subTask.getDescription(), subTask.getEpicID());
            }
            default -> null;
        };
    }

    public Task fromString(String value) {
        String[] task = value.split(",");

        switch (TypeTask.valueOf(task[1])) {
            case TASK:
                Task task0 = new Task(task[2], task[4]);
                task0.setId(Integer.valueOf(task[0]));
                task0.setStatus(Status.valueOf(task[3]));
                return task0;
            case EPIC:
                Epic epic = new Epic(task[2], task[4]);
                epic.setId(Integer.valueOf(task[0]));
                epic.setStatus(Status.valueOf(task[3]));
                return epic;
            case SUBTASK:
                SubTask subTask = new SubTask(task[2], task[4], Integer.valueOf(task[5]));
                subTask.setId(Integer.valueOf(task[0]));
                subTask.setStatus(Status.valueOf(task[3]));
                return subTask;
            default:
                return null;
        }

    }

    public static String historyToString(HistoryManager manager) {
        List<String> iDs = new ArrayList<>();

        for (Task task : manager.getHistory()) {
            iDs.add(String.valueOf(task.getId()));
        }
        return String.join(", ", iDs.toArray(new String[manager.getHistory().size()])) ;
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> hfs =  new ArrayList<>();

        for (String split : value.split(", ")) {
            hfs.add(Integer.valueOf(split));
        }
        return hfs;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        Files.readString(file.toPath());
    }

}
