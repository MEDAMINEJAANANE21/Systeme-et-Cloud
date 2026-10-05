package fr.uga.miage.m1.tp2.MyProdConsV1;


public class ThreadConsommer implements Runnable {
    private final Stockage stockage;
    private final int id;
    private Object lastConsumedObj;
    private final int iterations;
    private final int sleeptime;

    public ThreadConsommer(Stockage stockage, int threadId, int iterations, int sleeptime) {
        this.stockage = stockage;
        this.id = threadId;
        this.lastConsumedObj = null;
        this.iterations = iterations;
        this.sleeptime = sleeptime > 0 ? sleeptime : 2000;
    }

    @Override
    public void run() {
        for (int i=0; i < iterations; i++) {
            setLastConsumedObj(this.stockage.consommer(this));
            System.out.println("-CONS["+id+"] is is going to eep zzz...");
            sleep(sleeptime);
            System.out.println("-CONS["+id+"] just woke up :P");
        }
    }

    public void sleep(int millis) {
        try {
            java.lang.Thread.sleep(millis);
        } catch (InterruptedException e) {
            UnexpectedSituation.exit("interrupted wait in consumer thread ["+id+"]", e);
        }
    }

    private void setLastConsumedObj(Object obj) {
        this.lastConsumedObj = obj;
    }

    public int getId() { return this.id; }
}
