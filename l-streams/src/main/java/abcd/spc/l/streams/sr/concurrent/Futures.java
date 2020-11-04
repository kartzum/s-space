package abcd.spc.l.streams.sr.concurrent;

import java.util.concurrent.*;
import java.util.Random;

public class Futures {
    public static void main(String[] args) {
        futures();
    }

    static void futures() {
        Random random = new Random();
        GcdFinder finder1 = new GcdFinder(random.nextInt(), random.nextInt());
        GcdFinder finder2 = new GcdFinder(random.nextInt(), random.nextInt());
        ExecutorService service = Executors.newFixedThreadPool(2);
        Future<Integer> future1 = service.submit(finder1);
        Future<Integer> future2 = service.submit(finder2);
        try {
            System.out.println(future1.get() + ", " + future2.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        service.shutdown();
    }

    static class GcdFinder implements Callable<Integer> {
        int a;
        int b;

        GcdFinder(int a, int b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public Integer call() {
            while (b > 0) {
                int c = a % b;
                a = b;
                b = c;
            }
            return a;
        }
    }
}
