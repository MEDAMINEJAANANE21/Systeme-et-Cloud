package fr.uga.miage.m1.tp2.MyProdConsV2;

import java.util.Random;

public final class MyProdConsV2 {
    public static final int NBTHREADS = 10;
    public static final int ITERATIONS = 50;
    public static final int MAXSLEEPTIME = 42;
    private static final Random numGenerator = new Random(System.currentTimeMillis());

    private static int getSleepTime() {
        return numGenerator.nextInt(MAXSLEEPTIME);
    }

    public static void main() {

        Storage memory = new Storage(10);

        Thread[] threadsProd = new Thread[NBTHREADS];
        Thread[] threadsCons = new Thread[NBTHREADS];

        for (int id=0; id < NBTHREADS; id++) {
            threadsProd[id] = new Thread(new ThreadProduire(memory, id, ITERATIONS, getSleepTime()));
            threadsCons[id] = new Thread(new ThreadConsommer(memory, id, ITERATIONS, getSleepTime()));
        }

        for (int i = 0; i < NBTHREADS; i++) {
            threadsProd[i].start();
            threadsCons[i].start();
        }

        try {
            for (int i = 0; i < NBTHREADS; i++) {
                threadsProd[i].join();
                threadsCons[i].join();
            }
            System.out.println("All threads have finished. " + "Number of objects in memory="+memory.getOccupiedSpace());
            System.out.println("Memory Content : "+memory);
        } catch (InterruptedException e ) {
             UnexpectedSituation.exit("interrupted join in thread "+ Thread.currentThread().getName(), e);
        }

        System.exit(0);
     }
}
