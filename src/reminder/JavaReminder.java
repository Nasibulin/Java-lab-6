package reminder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class JavaReminder {
    public Timer timer;
    static int i;

    public JavaReminder(int seconds) {
        timer = new Timer();  //At this line a new Thread will be created
        //timer.schedule(new RemindTask(), seconds*1000); //delay in milliseconds
        RemindTask remindTask = new RemindTask();
        timer.schedule(remindTask, Date.from(
                Instant.from(LocalDateTime.now().plusSeconds(seconds).atZone(ZoneId.systemDefault())))); //delay in seconds
    }

    class RemindTask extends TimerTask {
        int id=i;
        RemindTask(){
            i++;
        }
        @Override
        public void run() {
            System.out.println(LocalDateTime.now()+" ReminderTask#"+id+" is completed by Java timer");
            timer.cancel(); //Not necessary because we call System.exit
            //System.exit(0); //Stops the AWT thread (and everything else)
        }
    }
    public static void main(String args[]) {
        System.out.println("Java timer is about to start");
        JavaReminder reminderBeep = new JavaReminder(4);
        JavaReminder reminderBeep1 = new JavaReminder(2);
        System.out.println(LocalDateTime.now()+" Remindertask's are scheduled with Java timer.");
    }
}