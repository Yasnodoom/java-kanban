package manager;

import exception.ManagerLoadException;
import task.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CSVTaskFormatter {
    static public String toString(Task task) {
        return switch (task.getClass().getName().split("\\.")[1]) {
            case "Task" -> String.format("%d,%S,%s,%s,%s\n", task.getId(), "Task", task.getName(),
                    task.getStatus(), task.getDescription());
            case "Epic" -> {
                Epic epic = (Epic) task;
                yield String.format("%d,%S,%s,%s,%s,%s\n", epic.getId(), "Epic", epic.getName(),
                        epic.getStatus(), epic.getDescription(), epic.getSubTaskIDs());
            }
            case "SubTask" -> {
                SubTask subTask = (SubTask) task;
                yield String.format("%d,%S,%s,%s,%s,%d\n", subTask.getId(), "SubTask", subTask.getName(),
                        subTask.getStatus(), subTask.getDescription(), subTask.getEpicID());
            }
            default -> null;
        };
    }

    static public Task fromString(String value) {
        String[] task = value.split(",");

        switch (TypeTask.valueOf(task[1])) {
            case TASK -> {
                Task task0 = new Task(task[2], task[4]);
                task0.setId(Integer.valueOf(task[0]));
                task0.setStatus(Status.valueOf(task[3]));
                return task0;
            }
            case EPIC -> {
                Epic epic = new Epic(task[2], task[4]);
                epic.setId(Integer.valueOf(task[0]));
                epic.setStatus(Status.valueOf(task[3]));
                return epic;
            }
            case SUBTASK -> {
                SubTask subTask = new SubTask(task[2], task[4], Integer.valueOf(task[5]));
                subTask.setId(Integer.valueOf(task[0]));
                subTask.setStatus(Status.valueOf(task[3]));
                return subTask;
            }
            default -> {
                return null;
            }
        }
    }

    public static String historyToString(HistoryManager manager) {
        List<String> iDs = new ArrayList<>();

        for (Task task : manager.getHistory()) {
            iDs.add(String.valueOf(task.getId()));
        }
        return String.join(",", iDs.toArray(new String[manager.getHistory().size()]));
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> hfs = new ArrayList<>();

        for (String split : value.split(",")) {
            hfs.add(Integer.valueOf(split));
        }
        return hfs;
    }

    public static FileBackedTaskManager loadFromFile(Path file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        try {
            String[] lines = Files.readString(file).split("\n");

            for (int i = 1; i < lines.length - 1; i++) {
                Task task = fromString(lines[i]);
                if (task == null) continue;
                if (task instanceof Epic) {
                    fileBackedTaskManager.addEpic((Epic) task);
                    continue;
                }
                if (task instanceof SubTask) {
                    fileBackedTaskManager.addSubTask((SubTask) task);
                    continue;
                }
                fileBackedTaskManager.addTask(task);
            }
            for (int id : historyFromString(lines[lines.length - 1])) {
                if (fileBackedTaskManager.getTaskByID(id) != null) {
                    fileBackedTaskManager.historyManager.add(fileBackedTaskManager.getTaskByID(id));
                    continue;
                }
                if (fileBackedTaskManager.getEpicByID(id) != null) {
                    fileBackedTaskManager.historyManager.add(fileBackedTaskManager.getEpicByID(id));
                    continue;
                }
                if (fileBackedTaskManager.getSubTaskByID(id) != null) {
                    fileBackedTaskManager.historyManager.add(fileBackedTaskManager.getSubTaskByID(id));
                }
            }
            fileBackedTaskManager.save();
        } catch (IOException e) {
            throw new ManagerLoadException(e.toString());
        }
        return fileBackedTaskManager;
    }

}
