import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileBackedTaskManager extends InMemoryTaskManager {

    public Path filePath;

    public FileBackedTaskManager(HistoryManager historyManager, String fileSrc) {
        super(historyManager);
        this.filePath = null;
        try {
            Path fp = Paths.get(fileSrc);
            if (!Files.exists(fp)) {
                Files.createFile(fp);
            }
            this.filePath = fp;
            this.sequence = loadFromFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // *******************************************************
    // Создание задач
    // *******************************************************

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        try {
            save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        try {
            save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        try {
            save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // *******************************************************
    // Редактирование задач
    // *******************************************************

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        try {
            save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        try {
            save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        try {
            save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // *******************************************************
    // Удаление задач
    // *******************************************************

    @Override
    public void removeTasks() {
        super.removeTasks();
        try {
            save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        try {
            save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeSubtasks() {
        super.removeSubtasks();
        try {
            save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeTaskByID(int id) {
        super.removeTaskByID(id);
        try {
            save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeEpicByID(int id) {
        super.removeEpicByID(id);
        try {
            save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeSubtaskByID(int id) {
        super.removeSubtaskByID(id);
        try {
            save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // *******************************************************
    // Вспомогательные методы
    // *******************************************************

    private void save() throws IOException {
        try (FileWriter fileWriter = new FileWriter(filePath.toFile())) {
            fileWriter.write("id,type,name,status,description,epic" + "\n");
            for (Task task : getTasks()) {
                fileWriter.write(toString(task) + "\n");
            }
            for (Epic epic : getEpics()) {
                fileWriter.write(toString(epic) + "\n");
            }
            for (Subtask subtask : getSubtasks()) {
                fileWriter.write(toString(subtask) + "\n");
            }
        } catch (FileNotFoundException e) {
            System.out.println("Произошла ошибка во время записи файла");
        }
    }

    private String toString(Task task) {
        TaskTypes taskType;
        try {
            taskType = TaskTypes.valueOf(task.getClass().getName().toUpperCase());
        } catch (IllegalArgumentException e) {
            taskType = null;
        }
        Integer parentId = null;
        if (taskType == TaskTypes.SUBTASK) {
            parentId = subtasks.get(task.id).getEpicId();
        }
        return String.format("%d,%s,%s,%s,%s,%d", task.id, taskType, task.title, task.status, task.description, parentId);
    }

    private int loadFromFile() throws IOException {
        int cnt = 0;
        try (FileReader fileReader = new FileReader(filePath.toFile())) {
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            while (bufferedReader.ready()) {
                line = bufferedReader.readLine();
                restoreTaskFromString(line);
                cnt++;
            }
        } catch (FileNotFoundException e) {
            System.out.println("Произошла ошибка во время чтения файла");
        }
        return cnt;
    }

    private void restoreTaskFromString(String line) {
        String[] data = line.split(",");
        if (data.length == 6) {
            int dataId, parentId;
            TaskTypes taskType;
            StatusTask taskStatus;
            String d1 = data[0].trim(); // id
            String d2 = data[1].trim(); // type
            String d3 = data[2].trim(); // name
            String d4 = data[3].trim(); // status
            String d5 = data[4].trim(); // description
            String d6 = data[5].trim(); // parent
            try {
                dataId = Integer.parseInt(d1);
            } catch (NumberFormatException e) {
                dataId = 0;
            }
            try {
                taskType = TaskTypes.valueOf(d2);
            } catch (IllegalArgumentException e) {
                taskType = null;
            }
            try {
                taskStatus = StatusTask.valueOf(d4);
            } catch (IllegalArgumentException e) {
                taskStatus = null;
            }
            if (taskType == TaskTypes.TASK) {
                Task task = new Task(d3, d5);
                restoreTask(task, dataId, taskStatus);
            } else if (taskType == TaskTypes.EPIC) {
                Epic epic = new Epic(d3, d5);
                restoreEpic(epic, dataId, taskStatus);
            } else if (taskType == TaskTypes.SUBTASK) {
                try {
                    parentId = Integer.parseInt(d6);
                } catch (NumberFormatException e) {
                    parentId = 0;
                }
                Subtask subtask = new Subtask(d3, d5, parentId);
                restoreSubtask(subtask, dataId, taskStatus);
            }
        }
    }

    private void restoreTask(Task task, int id, StatusTask status) {
        task.status = status;
        super.sequence = id - 1;
        super.addTask(task);
    }

    private void restoreSubtask(Subtask subtask, int id, StatusTask status) {
        subtask.status = status;
        super.sequence = id - 1;
        super.addSubtask(subtask);
    }

    private void restoreEpic(Epic epic, int id, StatusTask status) {
        epic.status = status;
        super.sequence = id - 1;
        super.addEpic(epic);
    }
}
