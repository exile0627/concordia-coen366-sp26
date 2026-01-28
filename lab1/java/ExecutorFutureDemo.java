package java;
import java.util.concurrent.*;

public class ExecutorFutureDemo {
    public static void do_other_stuff() {
        System.out.println("Doing other stuff...");
    }

    public static void main(String[] args) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        HelloCallable task = new HelloCallable();
        Future<Integer> future = service.submit(task);
        
        do_other_stuff();

        try {
            // 获取任务结果，此操作会阻塞直到任务完成
            Integer result = future.get();
            System.out.println("Result from thread: " + result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            // 重要：必须关闭执行器服务，否则 JVM 进程可能不会退出
            service.shutdown();
        }
    }
}

// 实现了 Callable 接口的任务类，可以返回结果并抛出异常
class HelloCallable implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        // 模拟耗时处理
        Thread.sleep(1000); 
        return Integer.valueOf(0);
    }
}
