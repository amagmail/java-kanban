import java.util.List;

public interface HistoryManager {

    void add(Task item);
    void printHistory();
    List<Task> getHistory();

}
