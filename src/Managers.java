public class Managers {

    public static TaskManager getDefault(HistoryManager historyManager) {
        return new InMemoryTaskManager(historyManager);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getFileBackedTaskManager(HistoryManager historyManager, String filePath) {
        return new FileBackedTaskManager(historyManager, filePath);
    }

}
