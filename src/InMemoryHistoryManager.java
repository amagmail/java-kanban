import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomList<Task> history = new CustomList<>();
    private final HashMap<Integer, Node<Task>> structure = new HashMap<>();

    public void add(Task data) {
        Node<Task> newNode = new Node<>(data);
        if (structure.containsKey(data.id)) {
            remove(data.id);
        }
        history.linkLast(newNode);
        structure.put(data.id, newNode);
    }

    public void remove(int id) {
        Node<Task> curr = structure.get(id);
        if (curr == null) {
            return;
        }
        Node<Task> currPrev = curr.prev;
        Node<Task> currNext = curr.next;
        if (currPrev != null) {
            currPrev.next = curr.next;
        } else {
            history.head = currNext;
        }
        if (currNext != null) {
            currNext.prev = curr.prev;
        } else {
            history.tail = currPrev;
        }
    }

    public List<Task> getHistory() {

        return history.getTasks();
    }

    private static class CustomList<Task> {

        private Node<Task> head;
        private Node<Task> tail;

        public void linkLast(Node<Task> newNode) {
            if (head == null) {
                head = newNode;
                tail = newNode;
                head.prev = null;
                tail.next = null;
            } else {
                tail.next = newNode;
                newNode.prev = tail;
                tail = newNode;
                tail.next = null;
            }
        }

        public ArrayList<Task> getTasks() {
            ArrayList<Task> tasks = new ArrayList<>();
            Node<Task> curr = head;
            if (head == null) {
                System.out.println("Список пуст");
            } else {
                while (curr != null) {
                    tasks.add(curr.data);
                    curr = curr.next;
                }
            }
            return tasks;
        }
    }
}
