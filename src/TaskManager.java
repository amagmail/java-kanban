import java.util.ArrayList;

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
    ArrayList<Task> getTasks();
    ArrayList<Epic> getEpics();
    ArrayList<Subtask> getSubtasks();
    Task getTaskByID(int id);
    Epic getEpicByID(int id);
    Subtask getSubtaskByID(int id);
    ArrayList<Subtask> getEpicSubtasksByID(int id);

    // *******************************************************
    // Печать данных
    // *******************************************************
    void printTasks(ArrayList<Task> tasksArr);
    void printEpics(ArrayList<Epic> epicsArr);
    void printSubtasks(ArrayList<Subtask> subtasksArr);

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
