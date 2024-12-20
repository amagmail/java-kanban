package managers;

import enums.StatusTask;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.*;
import java.time.Duration;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    public Map<Integer, Task> tasks = new HashMap<>();
    public Map<Integer, Subtask> subtasks = new HashMap<>();
    public Map<Integer, Epic> epics = new HashMap<>();
    public int sequence = 0;

    public HistoryManager historyManager;
    public Set<Task> prioritizedTasks;

    public InMemoryTaskManager(HistoryManager historyManager) {
        Comparator<Task> comparator = (t1, t2) -> {
            if (t1.getId() == t2.getId()) {
                return 0;
            }
            if (t1.getStartTime() != null && t2.getStartTime() != null) {
                if (t1.getStartTime().isBefore(t2.getStartTime())) {
                    return -1;
                } else {
                    return 1;
                }
            }
            return 0;
        };
        this.prioritizedTasks = new TreeSet<>(comparator);
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
        if (!isValid(task)) {
            System.out.println("WARNING: Ошибка валидации: " + task);
            return;
        }
        task.setId(getNextVal());
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void addEpic(Epic epic) {
        if (epic == null) {
            return;
        }
        epic.setId(getNextVal());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (subtask == null) {
            return;
        }
        if (!isValid(subtask)) {
            System.out.println("WARNING: Ошибка валидации: " + subtask);
            return;
        }
        subtask.setId(getNextVal());
        subtasks.put(subtask.getId(), subtask);
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }

        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        List<Integer> subtaskIds = epic.getSubtaskIds();
        subtaskIds.add(subtask.getId());

        actualizeEpicStatus(epic);
        actualizeEpicDuration(epic);
    }

    // *******************************************************
    // Редактирование задач
    // *******************************************************

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        actualizeEpicStatus(epic);
        actualizeEpicDuration(epic);
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
            throw new NoSuchElementException("Не удалось найти элемент типа Task по идентификатору " + id);
        } else {
            historyManager.add(item);
        }
        return item;
    }

    @Override
    public Epic getEpicByID(int id) {
        Epic item = epics.get(id);
        if (item == null) {
            throw new NoSuchElementException("Не удалось найти элемент типа Epic по идентификатору " + id);
        } else {
            historyManager.add(item);
        }
        return item;
    }

    @Override
    public Subtask getSubtaskByID(int id) {
        Subtask item = subtasks.get(id);
        if (item == null) {
            throw new NoSuchElementException("Не удалось найти элемент типа Subtask по идентификатору " + id);
        } else {
            historyManager.add(item);
        }
        return item;
    }

    @Override
    public List<Subtask> getEpicSubtasksByID(int id) {
        /*
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
        return items; */

        return getEpicByID(id).getSubtaskIds().stream()
                .map(subtaskId -> subtasks.get(subtaskId))
                .collect(Collectors.toList());
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
        /*
        subtasks.clear();
        for (Epic epic : getEpics())  {
            List<Integer> subtaskIds = epic.getSubtaskIds();
            subtaskIds.clear();
        } */

        subtasks.clear();
        getEpics().forEach(epic -> epic.getSubtaskIds().clear());
    }

    @Override
    public void removeTaskByID(int id) {
        Task task = getTaskByID(id);
        if (task == null) {
            return;
        }
        prioritizedTasks.remove(task);
        tasks.remove(task.getId());
        historyManager.remove(task.getId());
    }

    @Override
    public void removeEpicByID(int id) {
        /*
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
        System.out.println("Элемент типа Epic c идентификатором " + epic.id + " успешно удален"); */

        Epic epic = getEpicByID(id);
        if (epic == null) {
            return;
        }
        epic.getSubtaskIds().forEach(subtaskId -> {
            prioritizedTasks.remove(getSubtaskByID(subtaskId));
            subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
            System.out.println("Элемент типа Subtask c идентификатором " + subtaskId + " успешно удален");
        });

        epics.remove(epic.getId());
        historyManager.remove(epic.getId());
        System.out.println("Элемент типа Epic c идентификатором " + epic.getId() + " успешно удален");
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
        subtaskIds.remove(Integer.valueOf(subtask.getId()));
        System.out.println("Элемент типа Epic c идентификатором " + epic.getId() + " успешно обновлен");
        prioritizedTasks.remove(subtask);
        subtasks.remove(subtask.getId());
        historyManager.remove(subtask.getId());
        System.out.println("Элемент типа Subtask c идентификатором " + subtask.getId() + " успешно удален");
        actualizeEpicStatus(epic);
        actualizeEpicDuration(epic);
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
            if (isNew && otherSubtask.getStatus() == StatusTask.NEW) {
                isDone = false;
            } else if (isDone && otherSubtask.getStatus() == StatusTask.DONE) {
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

    private void actualizeEpicDuration(Epic epic) {
        if (epic == null) {
            return;
        }
        epic.setStartTime(null);
        epic.setEndTime(null);
        epic.setDuration(null);
        List<Integer> subtaskIds = epic.getSubtaskIds();
        for (Integer subtaskId : subtaskIds) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask.getStartTime() != null && (epic.getStartTime() == null || epic.getStartTime().isAfter(subtask.getStartTime()))) {
                epic.setStartTime(subtask.getStartTime());
            }
            if (subtask.getStartTime() != null && subtask.getDuration() != null && (epic.getEndTime() == null || epic.getEndTime().isBefore(subtask.getEndTime()))) {
                epic.setEndTime(subtask.getEndTime());
            }
            if (epic.getStartTime() != null && epic.getEndTime() != null) {
                epic.setDuration(Duration.between(epic.getStartTime(), epic.getEndTime()));
            }
        }
    }

    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    public boolean isValid(Task task) {
        return prioritizedTasks.stream()
                .noneMatch(priorTask -> ((task.getStartTime().isAfter(priorTask.getStartTime()) && task.getStartTime().isBefore(priorTask.getEndTime())))
                        || (task.getEndTime().isAfter(priorTask.getStartTime()) && task.getEndTime().isBefore(priorTask.getEndTime()))
                );
    }

    public int getNextVal() {
        return ++sequence;
    }

}
