import java.util.ArrayList;
public class ElementEpic extends ElementTask {

    public ArrayList<Integer> subtaskIds = new ArrayList<>();
    private StatusTask status;

    public ElementEpic(String title, String description) {
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
}
