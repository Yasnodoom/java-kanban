package task;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private Integer epicID;

    public SubTask(String name, String description, Integer epicID, Duration duration, LocalDateTime startTime) {
        super(name, description, duration, startTime);
        this.epicID = epicID;
    }

    public Integer getEpicID() {
        return epicID;
    }

    public void setEpicID(Integer epicID) {
        if (this.getId().equals(epicID)) return;
        this.epicID = epicID;
    }

    @Override
    public String toString() {
        return "task.SubTask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", epicID=" + epicID +
                '}';
    }
}
