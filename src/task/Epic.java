package task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final ArrayList<Integer> subTaskIDs = new ArrayList<>();
    private LocalDateTime endTime;
    private final List<Integer> timedSubTask = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public ArrayList<Integer> getSubTaskIDs() {
        return subTaskIDs;
    }

    public List<Integer> getTimedSubTask() {
        return timedSubTask;
    }

    public void setSubTaskID(Integer subTaskID) {
        if (this.getId().equals(subTaskID)) return;
        subTaskIDs.add(subTaskID);
    }

    public void setTimedSubTask(Integer id) {
        if (timedSubTask.contains(id))
            return;
        timedSubTask.add(id);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        String toString = "task.Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus();
        if (subTaskIDs.isEmpty()) {
            toString += ", no subtasks";
        } else {
            toString += ", subTask=" + subTaskIDs;
        }
        return toString + '}';
    }
}
