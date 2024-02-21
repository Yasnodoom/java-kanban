package task;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subTaskIDs = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public ArrayList<Integer> getSubTaskIDs() {
        return subTaskIDs;
    }

    public void setSubTaskID(Integer subTaskID) {
        if (this.getId().equals(subTaskID)) return;
        subTaskIDs.add(subTaskID);
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
