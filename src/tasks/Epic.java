package tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import enums.StatusTask;

public class Epic extends Task {

    private List<Integer> subtaskIds = new ArrayList<>();
    protected LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description);
        setStatus(StatusTask.NEW);
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String result = "Epic{";
        result += "id=" + id + ",title='" + title + "',description='" + description + ",subtasks=" + subtaskIds.toString();
        result += "}, status=" + getStatus();
        if (duration != null && startTime != null) {
            result +=  ", duration=[" + startTime.format(dateTimeFormatter) + ", " + getEndTime().format(dateTimeFormatter) + "]";
        }
        return result;
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(List<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }
}
