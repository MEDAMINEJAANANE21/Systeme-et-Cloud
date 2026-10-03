package fr.uga.miage.m1.tp2.MyProdConsV1;


public class ThreadConsommer implements Runnable {
    private final Stockage stockage;
    private final int id;
    private Object lastConsumedObj;
    private final int iterations;

    public ThreadConsommer(Stockage stockage, int threadId, int iterations) {
        this.stockage = stockage;
        this.id = threadId;
        this.lastConsumedObj = null;
        this.iterations = iterations;
    }

    @Override
    public void run() {
        for (int i=0; i < iterations; i++) {
            setLastConsumedObj(this.stockage.consommer(this));
        }
    }

    public void sleep(int millis) {
        try {
            System.out.println("sleep time====="+millis);
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
