package reminder;

import reminder.client.EventClient;
import reminder.server.EventServer;

public class EventDemo {


    public static void main(String[] args) {

        EventServer eventServer = new EventServer(8080);
        Thread t1 = new Thread(eventServer);
        t1.start();
        EventClient c1 = new EventClient("localhost",8080,"user1");
        EventClient c2 = new EventClient("localhost",8080,"user2");
        EventClient c3 = new EventClient("localhost",8080,"user3");
        Thread tc1 = new Thread(c1);
        Thread tc2 = new Thread(c2);
        Thread tc3 = new Thread(c3);
        tc1.start();
        tc2.start();
        tc3.start();
    }

}
