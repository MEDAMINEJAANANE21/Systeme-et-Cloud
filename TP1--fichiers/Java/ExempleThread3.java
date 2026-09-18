import java.util.concurrent.TimeUnit;

public class ExempleThread3 extends Thread {

    private String toSay;
    private int toWait;
    private int number;

    public ExempleThread3(int number, String toSay, int toWait) {
        this.toSay=toSay;
        this.toWait= toWait;
    }

    public void run() {
        try {
            TimeUnit.MILLISECONDS.sleep(toWait);
        } catch (InterruptedException e) {
            UnexpectedSituation.exit("interrupted sleep in thread "+ Thread.currentThread().getName(), e);
        }
        System.out.print(toSay);
    }

    public static void  main(String args[]) {
        ExempleThread3 thread1, thread2, thread3;
        thread1=new ExempleThread3(1, "Hello ", 50);
        thread2=new ExempleThread3(3, "World ",1);
        thread3=new ExempleThread3(2, "and Everybody ",2);

        thread1.start();
        thread2.start();
        thread3.start();

        // ici c'est juste le pere qui attend la terminaison de tous les threads
        try {
            thread1.join();
            thread2.join();
            thread3.join();
        } catch (InterruptedException e) {
            UnexpectedSituation.exit("interrupted join in thread "+ Thread.currentThread().getName(), e);
        }

        System.exit(0);
    }

}
