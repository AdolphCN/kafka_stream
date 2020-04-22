package com.cpic.spark.executor;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ExecutorDemo {

    private volatile ExecutorService executorService;

    public void testMethod() throws ExecutionException, InterruptedException {
        executorService = Executors.newFixedThreadPool(5);

        List<Future> list = new ArrayList<Future>();
        for (int i = 0; i < 10; i++) {
            Future future = executorService.submit(new MyRun(i));

            list.add(future);
//            System.out.println(future.get());
        }

        System.out.println("is i am last?");

        for (Future f : list) {
            System.out.println(f.get());
        }

        System.out.println("am i last~~~~~~~~~~?");
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }

    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorDemo demo = new ExecutorDemo();

        demo.testMethod();

    }

    class MyRun implements Callable<String> {

        private Integer age;

        MyRun(int i) {
            age = i;
        }

        @Override
        public String call() throws Exception {

            Thread.sleep(1000);
            System.out.println("i am jack,age is " + age);

            return "this is my test,age:" + age;
        }
    }
}
