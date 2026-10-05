package fr.uga.miage.m1.tp2.MyProdConsV1;


public class ThreadProduire implements Runnable {
    private final Stockage stockage;
    private final int id;
    private final int iterations;
    private final int sleeptime;

    public ThreadProduire(Stockage stockage, int threadId, int iterations, int sleeptime) {
        this.stockage = stockage;
        this.id = threadId;
        this.iterations = iterations;
        this.sleeptime = Math.min(sleeptime, 0);
    }

    @Override
    public void run() {
        for (int i=0; i < iterations; i++) {
            this.stockage.produire(new Object(), this);
            System.out.println("+PROD["+id+"] is going to eep zzz...");
            sleep(sleeptime);
            System.out.println("+PROD["+id+"] just woke up :P");
        }
    }

    public void sleep(int millis) {
        try {
            java.lang.Thread.sleep(millis);
        } catch (InterruptedException e) {
            UnexpectedSituation.exit("interrupted wait in producer thread ["+id+"]", e);
        }
    }

    public int getId() { return this.id; }
}
