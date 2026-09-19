import java.util.concurrent.TimeUnit;

public class SleepingThread extends Thread {
    public static final int TWOMINUTES = 120000;

    public SleepingThread() {
    }

    @Override
    public void run() {
        try {
            System.out.println("I am sleeping zzz...");
            TimeUnit.MILLISECONDS.sleep(TWOMINUTES);
        } catch (InterruptedException e) {
            UnexpectedSituation.exit("interrupted sleep in thread "+ Thread.currentThread().getName(), e);
        }
    }

    public static void main(String[] argv) {
        if (argv.length < 1) {
            System.err.println("Il faut un nombre de threads en argument !");
            System.exit(1);
        }

        final int nbThreads = Integer.parseInt(argv[0]);
        SleepingThread[] sleepingThreads = new SleepingThread[nbThreads];

        for (int i = 0; i < nbThreads; i++) {
            sleepingThreads[i] = new SleepingThread();
            sleepingThreads[i].start();
        }

        System.out.println("I have started all my children :p");

        for (int i = 0; i < nbThreads; i++) {
            try {
                sleepingThreads[i].join();
            } catch (InterruptedException e) {
                UnexpectedSituation.exit("interrupted join in thread "+ Thread.currentThread().getName(), e);
            }
        }

        System.exit(0);
    }
}
