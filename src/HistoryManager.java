import java.util.List;

public interface HistoryManager {

    void add(Task item);

    void remove(int id);

    List<Task> getHistory();

}
