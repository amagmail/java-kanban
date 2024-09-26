import java.util.ArrayList;
import java.util.HashMap;
public class TaskManager {

    public HashMap<Integer, Task> tasks = new HashMap<>();
    public HashMap<Integer, Subtask> subtasks = new HashMap<>();
    public HashMap<Integer, Epic> epics = new HashMap<>();
    public int sequence = 0;

    // *******************************************************
    // Создание задач
    // *******************************************************
    public void addTask(Task task) {
        task.id = getNextVal();
        tasks.put(task.id, task);
    }

    public void addEpic(Epic epic) {
        epic.id = getNextVal();
        epics.put(epic.id, epic);
    }

    public void addSubtask(Subtask subtask) {
        subtask.id = getNextVal();
        subtasks.put(subtask.id, subtask);

        Epic epic = epics.get(subtask.epicId);
        epic.subtaskIds.add(subtask.id);

        actualizeEpicStatus(epic);
    }

    // *******************************************************
    // Редактирование задач
    // *******************************************************
    public void updateTask(Task task) {
        tasks.put(task.id, task);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.id, epic);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.id, subtask);
        Epic epic = epics.get(subtask.epicId);
        actualizeEpicStatus(epic);
    }

    // *******************************************************
    // Получение задач
    // *******************************************************
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void printTasks(ArrayList<Task> tasksArr) {
        for (Task task : tasksArr) {
            System.out.println(task);
        }
    }

    public void printEpics(ArrayList<Epic> epicsArr) {
        for (Epic epic : epicsArr) {
            System.out.println(epic);
        }
    }

    public void printSubtasks(ArrayList<Subtask> subtasksArr) {
        for (Subtask subtask : subtasksArr) {
            System.out.println(subtask);
        }
    }

    public Task getTaskByID(int id) {
        Task item = null;
        item = tasks.get(id);
        if (item == null) {
            System.out.println("WARNING: Не удалось найти элемент типа Task по идентификатору " + id);
        }
        return item;
    }

    public Epic getEpicByID(int id) {
        Epic item = null;
        item = epics.get(id);
        if (item == null) {
            System.out.println("WARNING: Не удалось найти элемент типа Epic по идентификатору " + id);
        }
        return item;
    }

    public Subtask getSubtaskByID(int id) {
        Subtask item = null;
        item = subtasks.get(id);
        if (item == null) {
            System.out.println("WARNING: Не удалось найти элемент типа Subtask по идентификатору " + id);
        }
        return item;
    }

    public ArrayList<Subtask> getEpicSubtasksByID(int id) {
        ArrayList<Subtask> items = new ArrayList<>();
        Epic epic = getEpicByID(id);
        if (epic == null) {
            System.out.println("WARNING: Не удалось найти элемент типа Epic по идентификатору " + id);
            return items;
        }
        for (Integer subtaskId : epic.subtaskIds) {
            items.add(subtasks.get(subtaskId));
        }
        return items;
    }

    // *******************************************************
    // Удаление задач
    // *******************************************************
    public void removeTasks() {
        tasks.clear();
    }

    public void removeEpics() {
        epics.clear();
        removeSubtasks();
    }

    public void removeSubtasks() {
        subtasks.clear();
        for (Epic epic : getEpics())  {
            epic.subtaskIds.clear();
        }
    }

    public void removeTaskByID(int id) {
        Task task = getTaskByID(id);
        if (task == null) {
            return;
        }
        tasks.remove(task.id);
    }

    public void removeEpicByID(int id) {
        Epic epic = getEpicByID(id);
        if (epic == null) {
            return;
        }
        for (Integer subtaskId : epic.subtaskIds) {
            subtasks.remove(subtaskId);
            System.out.println("Элемент типа Subtask c идентификатором " + subtaskId + " успешно удален");
        }
        epics.remove(epic.id);
        System.out.println("Элемент типа Epic c идентификатором " + epic.id + " успешно удален");
    }

    public void removeSubtaskByID(int id) {
        Subtask subtask = getSubtaskByID(id);
        if (subtask == null) {
            return;
        }
        Epic epic = epics.get(subtask.epicId);
        ArrayList<Integer> subtaskIds = epics.get(epic.id).subtaskIds;
        subtaskIds.remove(Integer.valueOf(subtask.id));
        System.out.println("Элемент типа Epic c идентификатором " + epic.id + " успешно обновлен");
        subtasks.remove(subtask.id);
        System.out.println("Элемент типа Subtask c идентификатором " + subtask.id + " успешно удален");
        actualizeEpicStatus(epic);
    }

    // *******************************************************
    // Вспомогательные методы
    // *******************************************************
    private void actualizeEpicStatus(Epic epic) {
        boolean isNew = true;
        boolean isDone = true;
        for (Integer subtaskId : epic.subtaskIds) {
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
