public class SubTask extends Task{
    private final Epic epic;

    public SubTask(String name, String description, Integer id, Epic epic) {
        super(name, description, id);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", epicID=" + epic.getId() +
                '}';
    }
}
