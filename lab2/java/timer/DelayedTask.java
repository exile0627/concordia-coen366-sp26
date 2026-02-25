package lab2.java.timer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;

public class DelayedTask {

    public static void main(String[] args) {

        int interval = 1000; // 10 sec
        Date timeToRun = new Date(System.currentTimeMillis() + interval);
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Task here ...
                System.out.println("Task executed at " + new Date());
                // timer.cancel(); // Stop the timer after task execution
            }
        }, timeToRun);

        // JVM won't exit since Timer thread is non-daemon, and we haven't cancelled it.
    }
}
