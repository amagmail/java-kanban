import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> history = new ArrayList<>();
    private final int HIST_ITEMS_COUNT = 10;

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

    public List<Task> getHistory() {
        // Возвращаем копию коллекции, чтобы нельзя было ее модернизировать
        return new ArrayList<>(history);
    }
}
