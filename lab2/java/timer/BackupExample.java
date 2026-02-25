package lab2.java.timer;
import java.util.Timer;
import java.util.TimerTask;

public class BackupExample {
    public static void main(String[] args) {
        Timer timer = new Timer(); // Default is non-daemon thread

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Backing up data at " + System.currentTimeMillis());
                // Simulate a time-consuming operation
                try { Thread.sleep(5000); } catch (InterruptedException e) {}
                System.out.println("Backup finished");
            }
        }, 0, 10000); // Execute every 1 seconds

        System.out.println("Main thread finished");
        // Even though the main thread ends, the Timer thread keeps running to ensure backup completes
    }
}
