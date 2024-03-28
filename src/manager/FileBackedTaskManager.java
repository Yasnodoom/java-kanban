package manager;

import exception.ManagerSaveException;
import task.Epic;
import task.SubTask;
import task.Task;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;

import static manager.CSVTaskFormatter.historyToString;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path backedFile;

    public FileBackedTaskManager(Path file) {
        backedFile = file;
    }

    public void save() {
        if (this.getTasks().isEmpty() && this.getEpics().isEmpty() && this.getSubTasks().isEmpty())
            return;

        try (Writer fileWriter = new FileWriter(backedFile.toFile(), false)) {
            fileWriter.write("id,type,name,status,description,[epic/subtasks],duration,start-time \n");
            for (Task task : this.getTasks()) {
                fileWriter.write(CSVTaskFormatter.toString(task));
            }
            for (Epic epic : this.getEpics()) {
                fileWriter.write(CSVTaskFormatter.toString(epic));
            }
            for (SubTask subTask : this.getSubTasks()) {
                fileWriter.write(CSVTaskFormatter.toString(subTask));
            }
            fileWriter.write(historyToString(this.historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubTasks() {
        super.deleteSubTasks();
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic task) {
        super.updateEpic(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask task) {
        super.updateSubTask(task);
        save();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic task) {
        super.addEpic(task);
        save();
    }

    @Override
    public void addSubTask(SubTask task) {
        super.addSubTask(task);
        save();
    }

    @Override
    public void deleteTaskByID(Integer id) {
        super.deleteTaskByID(id);
        save();
    }

    @Override
    public void deleteEpicByID(Integer id) {
        super.deleteEpicByID(id);
        save();
    }

    @Override
    public void deleteSubTaskByID(Integer id) {
        super.deleteSubTaskByID(id);
        save();
    }

}