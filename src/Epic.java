import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<Integer> subtaskIds = new ArrayList<>();

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

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(List<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }
}
