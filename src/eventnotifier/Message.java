package eventnotifier;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Message implements Serializable {

    private String login;
    private String message;
    private String[] users;
    private LocalDateTime time;

    //Конструктор, которым будет пользоваться клиент
    public Message(String login, String message){
        this.login = login;
        this.message = message;
        this.time = LocalDateTime.now();
    }

    //Конструктор, которым будет пользоваться сервер
    public Message(String login, String message, String[] users){
        this.login = login;
        this.message = message;
        this.time = LocalDateTime.now();
        this.users = users;
    }

    public void setOnlineUsers(String[] users) {
        this.users = users;
    }

    public String getLogin() {
        return this.login;
    }

    public String getMessage() {
        return this.message;
    }

    public String[] getUsers() {
        return this.users;
    }

    public LocalTime getTime(){
        LocalTime tm = time.toLocalTime();
        return tm;
    }
}
