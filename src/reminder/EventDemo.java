package reminder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import reminder.client.EventClient;
import reminder.server.EventServer;
import reminder.server.Task;
import reminder.server.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class EventDemo {
    private static final String PACKAGE_PREFIX = "";
    private static final String TASKS_FILE = "tasks.json";
    private static final String TASKS_PATH = System.getProperty("user.dir") + "/db/" + TASKS_FILE;
    private static HashMap<Integer, User> idToUser = new HashMap<Integer, User>();
    private static HashMap<Integer, Task> idToTasks = new HashMap<Integer, Task>();
    private static List<EventClient> clients = new ArrayList<EventClient>();

    public static void main(String[] args) {

        importUsers();
        importTasks();

        EventServer eventServer = new EventServer(8080, idToUser);
        Thread t = new Thread(eventServer);
        t.start();

        clients.forEach(runnable -> {
            Thread thread = new Thread(runnable);
            thread.start();
        });
    }

    public static String jsonRead() {
        StringBuilder jsonStrBuilder = new StringBuilder();
        Scanner inputScanner;
        try {
            inputScanner = new Scanner(new File(TASKS_PATH), "UTF-8");
            while (inputScanner.hasNext())
                jsonStrBuilder.append(inputScanner.nextLine());
            inputScanner.close();
        } catch (FileNotFoundException e) {
            System.err.println(e.toString());
        }
        return jsonStrBuilder.toString();
    }

    public static void importUsers() {
        idToUser.clear();
        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(jsonRead());
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray userArray = (JSONArray) jsonObject.get("Users");

            Iterator<JSONArray> iterator = userArray.iterator();
            int i = 0;
            while (iterator.hasNext()) {
                iterator.next();
                JSONObject userData = (JSONObject) userArray.get(i);
                int id = ((Long) userData.get("UserID")).intValue();
                String name = (String) userData.get("User");
                User tmpuser = new User(id, name);
                idToUser.put(id, tmpuser);
                clients.add(new EventClient("localhost", 8080, id));
                i++;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public static void importTasks() {
        idToTasks.clear();
        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(jsonRead());
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray taskArray = (JSONArray) jsonObject.get("Tasks");

            Iterator<JSONArray> iterator = taskArray.iterator();
            int i = 0;
            while (iterator.hasNext()) {
                iterator.next();
                JSONObject taskData = (JSONObject) taskArray.get(i);
                int id = ((Long) taskData.get("TaskID")).intValue();
                String description = (String) taskData.get("Description");
                int time = ((Long) taskData.get("Time")).intValue();
                int user = ((Long) taskData.get("User")).intValue();
                Task tmptask = new Task(id, description, time);
                idToTasks.put(id, tmptask);
                idToUser.get(user).addTask(tmptask);
                i++;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

}
