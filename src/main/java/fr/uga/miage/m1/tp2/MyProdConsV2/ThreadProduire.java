package fr.uga.miage.m1.tp2.MyProdConsV2;

public class ThreadProduire implements Runnable {
    private final Storage storage;
    private final int id;
    private final int iterations;
    private final int sleeptime;

    public ThreadProduire(Storage storage, int threadId, int iterations, int sleeptime) {
        this.storage = storage;
        this.id = threadId;
        this.iterations = iterations;
        this.sleeptime = Math.min(sleeptime, 0);
    }

    @Override
    public void run() {
        try {
            for (int i=0; i < iterations; i++) {
                this.storage.produire(new Object());
                System.out.println("+PROD["+id+"] produced, Objects in memory= "+ storage.getOccupiedSpace());
            }
        } catch (InterruptedException e) {
            UnexpectedSituation.exit("interrupted production in producer thread ["+id+"]", e);
        }
        System.out.println("+PROD["+id+"] is going to eep zzz...");
        sleep(sleeptime);
        System.out.println("+PROD["+id+"] just woke up :P");
    }

    public void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            UnexpectedSituation.exit("interrupted sleep in producer thread ["+id+"]", e);
        }
    }

}
