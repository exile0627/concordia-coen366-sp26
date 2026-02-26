public class MutexExample {

    // Shared resource
    private static int counter = 0;

    // Shared lock object
    private static final Object GLOBAL_LOCK = new Object();

    /**
     * Safely increment counter using synchronized block.
     * All threads must share the same lock object.
     */
    public static void safeIncrement(String threadName) {
        for (int i = 0; i < 10000; i++) {
            int localCounter;
            synchronized (GLOBAL_LOCK) {
                counter++;
                localCounter = counter;
            }
            
            // IO operation outside lock (VERY IMPORTANT)
            if (i % 2000 == 0) {
                System.out.println(threadName +
                        " -> counter = " + localCounter);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        counter = 0;

        // Create threads with descriptive names
        Thread t1 = new Thread(
                () -> safeIncrement("Thread-1"),
                "Thread-1"
        );

        Thread t2 = new Thread(
                () -> safeIncrement("Thread-2"),
                "Thread-2"
        );

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Final result (Expected 20000): " + counter);
    }
}
