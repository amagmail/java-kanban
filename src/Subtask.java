public class Subtask extends Task {

    protected int epicId;

    public Subtask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        String result = "Subtask{";
        result += "id=" + id + ",title='" + title + "',description='" + description + ",epic=" + epicId;
        result += "}, status=" + status;
        return result;
    }
}
