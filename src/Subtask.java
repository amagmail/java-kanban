public class Subtask extends Task {

    private int epicId;

    public Subtask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public Subtask(String title, String description, int epicId, int minutes, String date) {
        super(title, description, minutes, date);
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        String result = "Subtask{";
        result += "id=" + id + ",title='" + title + "',description='" + description + ",epic=" + epicId;
        result += "}, status=" + status;
        if (duration != null && startTime != null) {
            result +=  ", duration=[" + startTime.format(dateTimeFormatter) + ", " + getEndTime().format(dateTimeFormatter) + "]";
        }
        return result;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}
