class HelloRunnable implements Runnable {

    private int limit;
    private String name;

    public HelloRunnable(int limit, String name) {
        this.limit = limit;
        this.name = name;
    }

    @Override
    public void run() {
        int c = 0;
        while (c < limit) {
            c++;
            System.out.println(name + " -> " +
                    Thread.currentThread().getName() +
                    " : " + c);
        }
    }
}

public class ThreadBasics {

    public static void main(String[] args) {

        int limit = 10;

        /*
         * Thread 1: Class-based Runnable
         */
        HelloRunnable task = new HelloRunnable(limit, "ClassRunnable");
        Thread threadFromClass = new Thread(task, "Thread-Class");

        /*
         * Thread 2: Anonymous class
         */
        Thread threadFromAnonymousClass = new Thread(new Runnable() {
            public void run() {
                int c = 0;
                while (c < limit) {
                    c++;
                    System.out.println("AnonymousClass -> " +
                            Thread.currentThread().getName() +
                            " : " + c);
                }
            }
        }, "Thread-Anonymous");

        /*
         * Thread 3: Lambda
         */
        Thread threadFromLambda = new Thread(() -> {
            int c = 0;
            while (c < limit) {
                c++;
                System.out.println("Lambda -> " +
                        Thread.currentThread().getName() +
                        " : " + c);
            }
        }, "Thread-Lambda");

        /*
         * Thread 4: Method Reference
         */
        Thread threadFromMethodReference =
                new Thread(task::run, "Thread-MethodRef");

        threadFromClass.start();
        threadFromAnonymousClass.start();
        threadFromLambda.start();
        threadFromMethodReference.start();

        try {
            threadFromClass.join();
            threadFromAnonymousClass.join();
            threadFromLambda.join();
            threadFromMethodReference.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
