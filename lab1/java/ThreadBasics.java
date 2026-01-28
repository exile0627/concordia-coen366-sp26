package java;

class HelloRunnable implements Runnable {
    private int a;

    public HelloRunnable(int a) {
        this.a = a;
    }

    @Override
    public void run() {
        int c = 0;
        while (c < this.a) {
            c++;
            System.out.println("Hello Concurrent World");
        }
    }
}

public class ThreadBasics {
    public static void main(String[] args) {
        int limit = 100;
        Thread threadFromClass = new Thread(new HelloRunnable(limit));
        Thread threadFromLambda = new Thread(() -> {
            int c = 0;
            while (c < limit) {
                c++;
                System.out.println("Hello from Lambda");
            }
        });

        threadFromClass.start();
        threadFromLambda.start();

        try {
            threadFromClass.join();
            threadFromLambda.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
