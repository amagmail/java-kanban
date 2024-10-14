import java.util.ArrayList;

public interface HistoryManager {

    void add(Task item);
    void printHistory();
    ArrayList<Task> getHistory();

}
