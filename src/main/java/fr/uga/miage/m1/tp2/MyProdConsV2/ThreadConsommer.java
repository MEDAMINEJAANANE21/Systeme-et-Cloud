package fr.uga.miage.m1.tp2.MyProdConsV2;


public class ThreadConsommer implements Runnable {
    private final Storage storage;
    private final int id;
    private Object lastConsumedObj;
    private final int iterations;
    private final int sleeptime;

    public ThreadConsommer(Storage storage, int threadId, int iterations, int sleeptime) {
        this.storage = storage;
        this.id = threadId;
        this.lastConsumedObj = null;
        this.iterations = iterations;
        this.sleeptime = sleeptime > 0 ? sleeptime : 2000;
    }

    @Override
    public void run() {
        for (int i=0; i < iterations; i++) {
            try {
                setLastConsumedObj(this.storage.consommer());
                System.out.println("-CONS["+id+"] consumed, Objects in memory= "+ storage.getOccupiedSpace());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("-CONS["+id+"] is is going to eep zzz...");
            sleep(sleeptime);
            System.out.println("-CONS["+id+"] just woke up :P");
        }
    }

    public void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            UnexpectedSituation.exit("interrupted wait in consumer thread ["+id+"]", e);
        }
    }

    private void setLastConsumedObj(Object obj) {
        this.lastConsumedObj = obj;
    }

    public Object getLastConsumedObj() {
        return lastConsumedObj;
    }
}
