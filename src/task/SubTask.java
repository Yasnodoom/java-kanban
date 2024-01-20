package task;

public class SubTask extends Task {
    private Integer epicID;

    public SubTask(String name, String description, Integer id, Integer epicID) {
        super(name, description, id);
        this.epicID = epicID;
    }

    public Integer getEpicID() {
        return epicID;
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
