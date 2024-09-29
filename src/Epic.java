import java.util.ArrayList;
public class Epic extends Task {

    private ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String title, String description) {
        super(title, description);
        setStatus(StatusTask.NEW);
    }

    public StatusTask getStatus() {
        return status;
    }

    public void setStatus(StatusTask status) {
        this.status = status;
    }

    @Override
    public String toString() {
        String result = "Epic{";
        result += "id=" + id + ",title='" + title + "',description='" + description + ",subtasks=" + subtaskIds.toString();
        result += "}, status=" + getStatus();
        return result;
    }

    public ArrayList<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(ArrayList<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }
}
