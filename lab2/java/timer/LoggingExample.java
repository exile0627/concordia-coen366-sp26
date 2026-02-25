package lab2.java.timer;
import java.util.Timer;
import java.util.TimerTask;

public class LoggingExample {
    public static void main(String[] args) {
        Timer timer = new Timer(true); // Daemon thread

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Logging status at " + System.currentTimeMillis());
            }
        }, 0, 1000); // Execute every 2 seconds

        try {
            Thread.sleep(5000); // sleep 5000 ms = 5 seconds
        } catch (InterruptedException e) {
            e.printStackTrace(); // handle interruption
        }
        System.out.println("Main thread finished");
        // When the main thread ends, the daemon Timer stops automatically
    }
}
