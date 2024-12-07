import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Managers {

    public static TaskManager getDefault(HistoryManager historyManager) {
        return new InMemoryTaskManager(historyManager);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getFileBackedTaskManager(HistoryManager historyManager, String fileSrc) {
        Path filePath = null;
        try {
            filePath = Paths.get(fileSrc);
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new FileBackedTaskManager(historyManager, filePath);
    }

    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static LocalDateTime stringToDate(String date) {
        try {
            return LocalDateTime.parse(date, dateTimeFormatter);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return LocalDateTime.now();
        }
    }

}
