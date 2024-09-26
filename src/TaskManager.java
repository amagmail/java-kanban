import java.util.ArrayList;
import java.util.HashMap;
public class TaskManager {

    public HashMap<Integer, ElementTask> tasks = new HashMap<>();
    public HashMap<Integer, ElementSubtask> subtasks = new HashMap<>();
    public HashMap<Integer, ElementEpic> epics = new HashMap<>();
    public int sequence = 1;

    // *******************************************************
    // Создание задач
    // *******************************************************
    public void addTask(ElementTask task) {
        task.id = getNextVal();
        tasks.put(task.id, task);
    }

    public void addEpic(ElementEpic epic) {
        epic.id = getNextVal();
        epics.put(epic.id, epic);
    }

    public void addSubtask(ElementSubtask subtask) {
        subtask.id = getNextVal();
        subtasks.put(subtask.id, subtask);

        ElementEpic epic = epics.get(subtask.epicId);
        epic.subtaskIds.add(subtask.id);
    }

    // *******************************************************
    // Обновление задач
    // *******************************************************
    public void updateTask(ElementTask task) {
        tasks.put(task.id, task);
    }

    public void updateEpic(ElementEpic epic) {
        epics.put(epic.id, epic);
    }

    public void updateSubtask(ElementSubtask subtask) {
        subtasks.put(subtask.id, subtask);
        ElementEpic epic = epics.get(subtask.epicId);
        actualizeEpicStatus(epic);
    }

    // *******************************************************
    // Получение задач
    // *******************************************************
    public void getTasks() {
        for (ElementTask task : tasks.values()) {
            System.out.println(task);
        }
    }

    public void getEpics() {
        for (ElementEpic epic : epics.values()) {
            System.out.println(epic);
        }
    }

    public void getSubtasks() {
        for (ElementSubtask subtask : subtasks.values()) {
            System.out.println(subtask);
        }
    }

    public void getTaskByID(int id) {
        for (Integer taskId : tasks.keySet()) {
            if (taskId == id) {
                System.out.println(tasks.get(taskId));
                return;
            }
        }
        System.out.println("Не удалось найти ElementTask по идентификатору " + id);
    }

    public void getEpicByID(int id) {
        for (Integer epicId : epics.keySet()) {
            if (epicId == id) {
                System.out.println(epics.get(epicId));
                return;
            }
        }
        System.out.println("Не удалось найти ElementEpic по идентификатору " + id);
    }

    public void getSubtaskByID(int id) {
        for (Integer subtaskId : subtasks.keySet()) {
            if (subtaskId == id) {
                System.out.println(subtasks.get(subtaskId));
                return;
            }
        }
        System.out.println("Не удалось найти ElementSubtask по идентификатору " + id);
    }

    public void getEpicSubtaskByID(int id) {
        for (Integer epicId : epics.keySet()) {
            if (epicId == id) {
                ArrayList<Integer> subtaskIds = epics.get(epicId).subtaskIds;
                for (Integer subtaskId : subtaskIds) {
                    getSubtaskByID(subtaskId);
                }
                return;
            }
        }
        System.out.println("Не удалось найти ElementEpic по идентификатору " + id);
    }

    // *******************************************************
    // Удаление задач
    // *******************************************************
    public void removeTasks() {
        tasks = new HashMap<>();
    }

    public void removeEpics() {
        epics = new HashMap<>();
    }

    public void removeSubtasks() {
        subtasks = new HashMap<>();
    }

    public void removeTaskByID(int id) {
        for (Integer taskId : tasks.keySet()) {
            if (taskId == id) {
                tasks.remove(taskId);
                System.out.println("ElementTask c идентификатором " + taskId + " успешно удален");
                return;
            }
        }
        System.out.println("Не удалось найти ElementTask по идентификатору " + id);
    }

    public void removeEpicByID(int id) {
        for (Integer epicId : epics.keySet()) {
            if (epicId == id) {
                for (Integer subtaskId : epics.get(epicId).subtaskIds) {
                    subtasks.remove(subtaskId);
                    System.out.println("ElementSubtask c идентификатором " + subtaskId + " успешно удален");
                }
                epics.remove(epicId);
                System.out.println("ElementEpic c идентификатором " + epicId + " успешно удален");
                return;
            }
        }
        System.out.println("Не удалось найти ElementEpic по идентификатору " + id);
    }

    public void removeSubtaskByID(int id) {
        for (Integer subtaskId : subtasks.keySet()) {
            if (subtaskId == id) {
                ElementSubtask subtask = subtasks.get(subtaskId);
                ElementEpic epic = epics.get(subtask.epicId);
                ArrayList<Integer> subtaskIds = epics.get(epic.id).subtaskIds;
                subtaskIds.remove(subtaskId);
                System.out.println("ElementEpic c идентификатором " + epic.id + " успешно обновлен");
                subtasks.remove(subtaskId);
                System.out.println("ElementSubtask c идентификатором " + subtaskId + " успешно удален");
                actualizeEpicStatus(epic);
                return;
            }
        }
        System.out.println("Не удалось найти ElementSubtask по идентификатору " + id);
    }

    // *******************************************************
    // Вспомогательные методы
    // *******************************************************
    public void actualizeEpicStatus(ElementEpic epic) {
        boolean isNew = true;
        boolean isDone = true;
        for (Integer subtaskId : epic.subtaskIds) {
            ElementSubtask otherSubtask = subtasks.get(subtaskId);
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
        return sequence++;
    }
}
