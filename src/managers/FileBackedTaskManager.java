package managers;

import enums.StatusTask;
import enums.TaskTypes;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import exceptions.ManagerSaveException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;

public class FileBackedTaskManager extends InMemoryTaskManager {

    public Path filePath;

    public FileBackedTaskManager(HistoryManager historyManager, Path filePath) {
        super(historyManager);
        this.filePath = filePath;
        this.sequence = loadFromFile();
    }

    // *******************************************************
    // Создание задач
    // *******************************************************

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    // *******************************************************
    // Редактирование задач
    // *******************************************************

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    // *******************************************************
    // Удаление задач
    // *******************************************************

    @Override
    public void removeTasks() {
        super.removeTasks();
        save();
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        save();
    }

    @Override
    public void removeSubtasks() {
        super.removeSubtasks();
        save();
    }

    @Override
    public void removeTaskByID(int id) {
        super.removeTaskByID(id);
        save();
    }

    @Override
    public void removeEpicByID(int id) {
        super.removeEpicByID(id);
        save();
    }

    @Override
    public void removeSubtaskByID(int id) {
        super.removeSubtaskByID(id);
        save();
    }

    // *******************************************************
    // Вспомогательные методы
    // *******************************************************

    private void save() {
        try (FileWriter fileWriter = new FileWriter(filePath.toFile())) {
            String line = "id,type,name,status,description,epic,duration,startTime" + "\n";
            fileWriter.write(line);
            for (Task task : getTasks()) {
                line = toString(task);
                line += "\n";
                fileWriter.write(line);
            }
            for (Epic epic : getEpics()) {
                line = toString(epic);
                line += "\n";
                fileWriter.write(line);
            }
            for (Subtask subtask : getSubtasks()) {
                line = toString(subtask);
                line += "\n";
                fileWriter.write(line);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ManagerSaveException("Произошла ошибка во время записи файла");
        }
    }

    private String toString(Task task) {
        TaskTypes taskType;
        try {
            taskType = TaskTypes.valueOf(task.getClass().getName().toUpperCase());
        } catch (Exception e) {
            taskType = null;
        }
        Integer parentId = null;
        if (taskType == TaskTypes.SUBTASK) {
            parentId = subtasks.get(task.getId()).getEpicId();
        }
        Integer durMin = null;
        if (task.getDuration() != null) {
            durMin = (int) task.getDuration().toMinutes();
        }
        String startTime = null;
        if (task.getStartTime() != null) {
            startTime = task.getStartTime().format(Managers.dateTimeFormatter);
        }
        return String.format("%d,%s,%s,%s,%s,%d,%d,%s", task.getId(), taskType, task.getTitle(), task.getStatus(), task.getDescription(), parentId, durMin, startTime);
    }

    private int loadFromFile() {
        int cnt = 0;
        try (FileReader fileReader = new FileReader(filePath.toFile())) {
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            while (bufferedReader.ready()) {
                line = bufferedReader.readLine();
                restoreTaskFromString(line);
                cnt++;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ManagerSaveException("Произошла ошибка во время чтения файла");
        }
        return cnt;
    }

    private void restoreTaskFromString(String line) {
        try {
            String[] data = line.split(",");
            if (data.length == 8) {
                int dataId, parentId;
                TaskTypes taskType;
                StatusTask taskStatus;

                String d1 = data[0].trim(); // id
                String d2 = data[1].trim(); // type
                String d3 = data[2].trim(); // name
                String d4 = data[3].trim(); // status
                String d5 = data[4].trim(); // description
                String d6 = data[5].trim(); // parent
                String d7 = data[6].trim(); // duration
                String d8 = data[7].trim(); // startTime

                dataId = Integer.parseInt(d1);
                if (dataId <= 0) {
                    throw new ManagerSaveException("Идентификатор объекта не может быть отрицательным: " + line);
                }
                if (d2.equals("null") || d2.isBlank()) {
                    throw new ManagerSaveException("Тип объекта не может быть пустым: " + line);
                }
                if (d4.equals("null") || d4.isBlank()) {
                    throw new ManagerSaveException("Статус объекта не может быть пустым: " + line);
                }
                taskType = TaskTypes.valueOf(d2);
                taskStatus = StatusTask.valueOf(d4);

                if (taskType == TaskTypes.TASK) {
                    Task task;
                    if (!d7.equals("null") && !d8.equals("null")) {
                        task = new Task(d3, d5, Integer.parseInt(d7), Managers.stringToDate(d8));
                    } else {
                        task = new Task(d3, d5);
                    }
                    restoreTask(task, dataId, taskStatus);
                } else if (taskType == TaskTypes.EPIC) {
                    Epic epic = new Epic(d3, d5);
                    restoreEpic(epic, dataId, taskStatus);
                } else if (taskType == TaskTypes.SUBTASK) {
                    parentId = Integer.parseInt(d6);
                    Subtask subtask;
                    if (!d7.equals("null") && !d8.equals("null")) {
                        subtask = new Subtask(d3, d5, parentId, Integer.parseInt(d7), Managers.stringToDate(d8));
                    } else {
                        subtask = new Subtask(d3, d5, parentId);
                    }
                    restoreSubtask(subtask, dataId, taskStatus);
                } else {
                    throw new ManagerSaveException("Не удалось идентифицировать тип объекта: " + line);
                }
            } else {
                throw new ManagerSaveException("В строке неверное число столбцов: " + line);
            }
        } catch (Exception e) {
            throw new ManagerSaveException("Невозможно создать объект из строки: " + line);
        }
    }

    private void restoreTask(Task task, int id, StatusTask status) {
        task.setStatus(status);
        super.sequence = id - 1;
        super.addTask(task);
    }

    private void restoreSubtask(Subtask subtask, int id, StatusTask status) {
        subtask.setStatus(status);
        super.sequence = id - 1;
        super.addSubtask(subtask);
    }

    private void restoreEpic(Epic epic, int id, StatusTask status) {
        epic.setStatus(status);
        super.sequence = id - 1;
        super.addEpic(epic);
    }

}
