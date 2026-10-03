package fr.uga.miage.m1.tp2.MyProdConsV1;

import fr.uga.miage.m1.tp2.UnexpectedSituation;

public class MyProdConsV1 {
    public static final int NBTHREADS = 20;
    public static final int ITERATIONS = 100;

     static final void main(String[] args) {

        Stockage memory = new Stockage(10);

        Thread[] threadsProd = new Thread[NBTHREADS];
        Thread[] threadsCons = new Thread[NBTHREADS];

        for (int id=0; id < NBTHREADS; id++) {
            threadsProd[id] = new Thread(new ThreadProduire(memory, id, ITERATIONS));
            threadsProd[id].setName(id+"");
            threadsCons[id] = new Thread(new ThreadConsommer(memory, id, ITERATIONS));
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

        System.out.println("All threads have finished. Memory="+memory.getOccupiedSpace());
        System.out.println("Memory Content : "+memory);
        System.exit(0);
     }
}
