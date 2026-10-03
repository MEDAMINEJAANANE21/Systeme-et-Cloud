package fr.uga.miage.m1.tp2.MyProdConsV1;

import fr.uga.miage.m1.tp2.UnexpectedSituation;
import java.util.Random;

public final class MyProdConsV1 {
    public static final int NBTHREADS = 10;
    public static final int ITERATIONS = 50;
    private static final Random numGenerator = new Random(System.currentTimeMillis());

    static void main(String[] args) {

        Stockage memory = new Stockage(10);

        Thread[] threadsProd = new Thread[NBTHREADS];
        Thread[] threadsCons = new Thread[NBTHREADS];

        for (int id=0; id < NBTHREADS; id++) {
            threadsProd[id] = new Thread(new ThreadProduire(memory, id, ITERATIONS, numGenerator.nextInt(7000)));
            threadsProd[id].setName(id+"");
            threadsCons[id] = new Thread(new ThreadConsommer(memory, id, ITERATIONS, numGenerator.nextInt(7000)));
            threadsCons[id].setName((id+NBTHREADS)+"");
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
        } catch (InterruptedException e ) {
             UnexpectedSituation.exit("interrupted join in thread "+ Thread.currentThread().getName(), e);
        }

        System.out.println("All threads have finished." +
                "Number of objects in memory="+memory.getOccupiedSpace());
        System.out.println("Memory Content : "+memory);
        System.exit(0);
     }
}
