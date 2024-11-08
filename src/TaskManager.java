import java.util.List;

public interface TaskManager {

    // *******************************************************
    // Создание задач
    // *******************************************************
    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(Subtask subtask);

    // *******************************************************
    // Редактирование задач
    // *******************************************************
    void updateTask(Task task);
    void updateEpic(Epic epic);
    void updateSubtask(Subtask subtask);

    // *******************************************************
    // Получение задач
    // *******************************************************
    List<Task> getTasks();
    List<Epic> getEpics();
    List<Subtask> getSubtasks();
    Task getTaskByID(int id);
    Epic getEpicByID(int id);
    Subtask getSubtaskByID(int id);
    List<Subtask> getEpicSubtasksByID(int id);
    List<Task> getHistory();

    // *******************************************************
    // Удаление задач
    // *******************************************************
    void removeTasks();
    void removeEpics();
    void removeSubtasks();
    void removeTaskByID(int id);
    void removeEpicByID(int id);
    void removeSubtaskByID(int id);

}
