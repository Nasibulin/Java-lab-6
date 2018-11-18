package reminder.server;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int userId;
    private String name;
    private List<Task> tasks = new ArrayList();

    public User(int userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public String toString() {
        return name;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void addTask(Task task) {
        this.tasks.add(task) ;
    }
}
