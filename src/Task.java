import java.util.Objects;

public class Task {

    protected String title;
    protected String description;
    protected int id;
    protected StatusTask status;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = StatusTask.NEW;
    }

    @Override
    public String toString() {
        String result = "Task{";
        result += "id=" + id + ",title='" + title + "',description='" + description;
        result += "}, status=" + status;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task item = (Task) obj;
        return Objects.equals(title, item.title) && Objects.equals(description, item.description) && (id == item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, id);
    }
}

