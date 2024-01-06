import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<SubTask> subTasks = new ArrayList<>();

    public Epic(String name, String description, Integer id) {
        super(name, description, id);
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void addSubTask(SubTask subTask) {
        subTasks.add(subTask);
    }

    @Override
    public String toString() {
        String toString = "Epic{" +
                "name='" + super.name + '\'' +
                ", description='" + super.description + '\'' +
                ", id=" + id +
                ", status=" + super.status;
        if (subTasks.isEmpty()) {
            toString += ", no subtasks";
        } else {
            toString += ", subTask=" + subTasks;
        }
        return toString + '}';
    }


    public static void autoUpdateEpicStatus(Epic epic) {
        if (epic.getSubTasks().isEmpty()) {
            return;
        }
        boolean hasNew = false;
        boolean hasDone = false;
        for (SubTask subTask : epic.getSubTasks()) {
            if (subTask.getStatus() == Status.IN_PROGRESS) {
                epic.setStatus(Status.IN_PROGRESS);
                return;
            }
            if (subTask.getStatus() == Status.NEW) {
                hasNew = true;
                if (hasNew && hasDone) {
                    epic.setStatus(Status.IN_PROGRESS);
                    return;
                }
            }
            if (subTask.getStatus() == Status.DONE) {
                hasDone = true;
                if (hasNew && hasDone) {
                    epic.setStatus(Status.IN_PROGRESS);
                    return;
                }
            }
        }
        if (!hasDone) {
            return;
        }
        epic.setStatus(Status.DONE);
    }
}
