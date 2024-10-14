import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    protected ArrayList<Task> history = new ArrayList<>();
    protected final int HIST_ITEMS_COUNT = 10;

    public void add(Task item) {
        history.add(item);
        while (history.size() > HIST_ITEMS_COUNT) {
            history.removeFirst();
        }
    }

    public void printHistory() {
        System.out.println("История просмотра последних " + HIST_ITEMS_COUNT + " задач: ");
        for (Task item: history) {
            System.out.println(item);
        }
    }

    public ArrayList<Task> getHistory() {
        return history;
    }
}
