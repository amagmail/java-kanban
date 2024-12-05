import java.util.Objects;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {

    protected String title;
    protected String description;
    protected int id;
    protected StatusTask status;
    protected Duration duration;
    protected LocalDateTime startTime;

    public final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = StatusTask.NEW;
    }

    public Task(String title, String description, int minutes, String date) {
        this.title = title;
        this.description = description;
        this.status = StatusTask.NEW;
        this.duration = Duration.ofMinutes(minutes);
        try {
            this.startTime = LocalDateTime.parse(date, DATE_TIME_FORMATTER);
        } catch (Exception e) {
            this.startTime = null;
        }
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    @Override
    public String toString() {
        String result = "Task{";
        result += "id=" + id + ",title='" + title + "',description='" + description;
        result += "}, status=" + status;
        if (duration != null && startTime != null) {
            result +=  ", duration=[" + startTime.format(DATE_TIME_FORMATTER) + ", " + getEndTime().format(DATE_TIME_FORMATTER) + "]";
        }
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

