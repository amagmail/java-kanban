import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

    public Map<Integer, Task> tasks = new HashMap<>();
    public Map<Integer, Subtask> subtasks = new HashMap<>();
    public Map<Integer, Epic> epics = new HashMap<>();
    public int sequence = 0;

    public HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    // *******************************************************
    // Создание задач
    // *******************************************************

    @Override
    public void addTask(Task task) {
        if (task == null) {
            return;
        }
        task.id = getNextVal();
        tasks.put(task.id, task);
    }

    @Override
    public void addEpic(Epic epic) {
        if (epic == null) {
            return;
        }
        epic.id = getNextVal();
        epics.put(epic.id, epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (subtask == null) {
            return;
        }
        subtask.id = getNextVal();
        subtasks.put(subtask.id, subtask);

        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        List<Integer> subtaskIds = epic.getSubtaskIds();
        subtaskIds.add(subtask.id);

        actualizeEpicStatus(epic);
    }

    // *******************************************************
    // Редактирование задач
    // *******************************************************

    @Override
    public void updateTask(Task task) {
        tasks.put(task.id, task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.id, epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.id, subtask);
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        actualizeEpicStatus(epic);
    }

    // *******************************************************
    // Получение задач
    // *******************************************************

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Task getTaskByID(int id) {
        Task item = tasks.get(id);
        if (item == null) {
            System.out.println("WARNING: Не удалось найти элемент типа Task по идентификатору " + id);
        } else {
            historyManager.add(item);
        }
        return item;
    }

    @Override
    public Epic getEpicByID(int id) {
        Epic item = epics.get(id);
        if (item == null) {
            System.out.println("WARNING: Не удалось найти элемент типа Epic по идентификатору " + id);
        } else {
            historyManager.add(item);
        }
        return item;
    }

    @Override
    public Subtask getSubtaskByID(int id) {
        Subtask item = subtasks.get(id);
        if (item == null) {
            System.out.println("WARNING: Не удалось найти элемент типа Subtask по идентификатору " + id);
        } else {
            historyManager.add(item);
        }
        return item;
    }

    @Override
    public List<Subtask> getEpicSubtasksByID(int id) {
        List<Subtask> items = new ArrayList<>();
        Epic epic = getEpicByID(id);
        if (epic == null) {
            System.out.println("WARNING: Не удалось найти элемент типа Epic по идентификатору " + id);
            return items;
        }
        List<Integer> subtaskIds = epic.getSubtaskIds();
        for (Integer subtaskId : subtaskIds) {
            items.add(subtasks.get(subtaskId));
        }
        return items;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // *******************************************************
    // Удаление задач
    // *******************************************************

    @Override
    public void removeTasks() {
        tasks.clear();
    }

    @Override
    public void removeEpics() {
        epics.clear();
        removeSubtasks();
    }

    @Override
    public void removeSubtasks() {
        subtasks.clear();
        for (Epic epic : getEpics())  {
            List<Integer> subtaskIds = epic.getSubtaskIds();
            subtaskIds.clear();
        }
    }

    @Override
    public void removeTaskByID(int id) {
        Task task = getTaskByID(id);
        if (task == null) {
            return;
        }
        tasks.remove(task.id);
        historyManager.remove(task.id);
    }

    @Override
    public void removeEpicByID(int id) {
        Epic epic = getEpicByID(id);
        if (epic == null) {
            return;
        }
        List<Integer> subtaskIds = epic.getSubtaskIds();
        for (Integer subtaskId : subtaskIds) {
            subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
            System.out.println("Элемент типа Subtask c идентификатором " + subtaskId + " успешно удален");
        }
        epics.remove(epic.id);
        historyManager.remove(epic.id);
        System.out.println("Элемент типа Epic c идентификатором " + epic.id + " успешно удален");
    }

    @Override
    public void removeSubtaskByID(int id) {
        Subtask subtask = getSubtaskByID(id);
        if (subtask == null) {
            return;
        }
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        List<Integer> subtaskIds = epic.getSubtaskIds();
        subtaskIds.remove(Integer.valueOf(subtask.id));
        System.out.println("Элемент типа Epic c идентификатором " + epic.id + " успешно обновлен");
        subtasks.remove(subtask.id);
        historyManager.remove(subtask.id);
        System.out.println("Элемент типа Subtask c идентификатором " + subtask.id + " успешно удален");
        actualizeEpicStatus(epic);
    }

    // *******************************************************
    // Вспомогательные методы
    // *******************************************************

    private void actualizeEpicStatus(Epic epic) {
        if (epic == null) {
            return;
        }
        boolean isNew = true;
        boolean isDone = true;
        List<Integer> subtaskIds = epic.getSubtaskIds();
        for (Integer subtaskId : subtaskIds) {
            Subtask otherSubtask = subtasks.get(subtaskId);
            if (isNew && otherSubtask.status == StatusTask.NEW) {
                isDone = false;
            } else if (isDone && otherSubtask.status == StatusTask.DONE) {
                isNew = false;
            } else {
                isDone = false;
                isNew = false;
                break;
            }
        }
        if (isNew) {
            epic.setStatus(StatusTask.NEW);
        } else if (isDone) {
            epic.setStatus(StatusTask.DONE);
        } else {
            epic.setStatus(StatusTask.IN_PROGRESS);
        }
    }

    public int getNextVal() {
        return ++sequence;
    }

}
