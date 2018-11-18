package reminder.server;

public class Task {
    private int taskId;
    private String description;
    private int time;

    public Task(int taskId, String description, int time) {
        this.taskId = taskId;
        this.description = description;
        this.time = time;
    }

    public int getTaskId() {
        return taskId;
    }

    public String getDescription() {
        return description;
    }

    public String toString() {
        return description;
    }

    public int getTime() {
        return time;
    }

}
