package manager;

import exception.ManagerLoadException;
import task.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CSVTaskFormatter {
    static public String toString(Task task) {
        return switch (task.getClass().getName().split("\\.")[1]) {
            case "Task" -> String.format("%d,%S,%s,%s,%s,%d,%s\n",
                    task.getId(), "Task", task.getName(), task.getStatus(), task.getDescription(),
                    task.getDuration().getSeconds(), task.getStartTime());
            case "Epic" -> {
                Epic epic = (Epic) task;
                yield String.format("%d,%S,%s,%s,%s,%s,%d,%s,%s\n", epic.getId(), "Epic",
                        epic.getName(), epic.getStatus(), epic.getDescription(), epic.subtaskIDsToString(),
                        epic.getDuration().getSeconds(), epic.getStartTime(),
                        epic.getEndTime());
            }
            case "SubTask" -> {
                SubTask subTask = (SubTask) task;
                yield String.format("%d,%S,%s,%s,%s,%d,%d,%s\n", subTask.getId(), "SubTask",
                        subTask.getName(), subTask.getStatus(), subTask.getDescription(), subTask.getEpicID(),
                        subTask.getDuration().getSeconds(), subTask.getStartTime());
            }
            default -> null;
        };
    }

    static public Task fromString(String value) {
        String[] task = value.split(",");

        switch (TypeTask.valueOf(task[1])) {
            case TASK -> {
                Task task0 = new Task(
                        task[2], task[4], Duration.ofSeconds(Long.parseLong(task[5])),
                        LocalDateTime.parse(task[6])
                );
                task0.setId(Integer.valueOf(task[0]));
                task0.setStatus(Status.valueOf(task[3]));
                return task0;
            }
            case EPIC -> {
                Epic epic = new Epic(task[2], task[4]);
                epic.setId(Integer.valueOf(task[0]));
                epic.setStatus(Status.valueOf(task[3]));
                epic.setDuration(Duration.ofSeconds(Long.parseLong(task[6])));
                if (! task[7].equals("null"))
                    epic.setStartTime(LocalDateTime.parse(task[7]));

                if (! task[8].equals("null"))
                    epic.setEndTime(LocalDateTime.parse(task[8]));
                return epic;
            }
            case SUBTASK -> {
                SubTask subTask = new SubTask(
                        task[2], task[4], Integer.valueOf(task[5]), Duration.ofSeconds(Long.parseLong(task[6])),
                        LocalDateTime.parse(task[7])
                );
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
        return String.join(",", manager.getHistory().stream()
                .map(Task::getId).map(String::valueOf).toArray(String[]::new));
    }

    public static List<Integer> historyFromString(String value) {
        return Arrays.stream(value.split(",")).map(Integer::valueOf).collect(Collectors.toList());
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
