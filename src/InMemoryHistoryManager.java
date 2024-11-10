import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomList<Task> history = new CustomList<>();

    public void add(Task data) {
        Node<Task> newNode = new Node<>(data);
        if (history.structure.containsKey(data.id)) {
            remove(data.id);
        }
        history.linkLast(newNode);
        history.structure.put(data.id, newNode);
    }

    public void remove(int id) {
        Node<Task> node = history.structure.get(id);
        history.removeNode(node);
    }

    public List<Task> getHistory() {

        return history.getTasks();
    }

    private static class CustomList<T> {

        private Node<T> head;
        private Node<T> tail;
        private final Map<Integer, Node<Task>> structure = new HashMap<>();

        public void linkLast(Node<T> newNode) {
            if (head == null) {
                head = newNode;
                tail = newNode;
                head.setPrev(null);
                tail.setNext(null);
            } else {
                tail.setNext(newNode);
                newNode.setPrev(tail);
                tail = newNode;
                tail.setNext(null);
            }
        }

        public ArrayList<T> getTasks() {
            ArrayList<T> tasks = new ArrayList<>();
            Node<T> curr = head;
            if (head == null) {
                System.out.println("Список пуст");
            } else {
                while (curr != null) {
                    tasks.add(curr.getData());
                    curr = curr.getNext();
                }
            }
            return tasks;
        }

        public void removeNode(Node<T> curr) {
            if (curr == null) {
                return;
            }
            Node<T> currPrev = curr.getPrev();
            Node<T> currNext = curr.getNext();
            if (currPrev != null) {
                currPrev.setNext(curr.getNext());
            } else {
                head = currNext;
            }
            if (currNext != null) {
                currNext.setPrev(curr.getPrev());
            } else {
                tail = currPrev;
            }
        }
    }
}
