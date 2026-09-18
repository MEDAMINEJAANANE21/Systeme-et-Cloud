import java.util.concurrent.TimeUnit;

public class ExempleThread2 extends Thread {

    private String toSay;
    private int toWait;
    private int number;

    public ExempleThread2(int number, String toSay, int toWait) {
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
        ExempleThread2 thread1, thread2, thread3;

        thread1=new ExempleThread2(1, "Hello ", 50);
        thread2=new ExempleThread2(2, "World ",25);
        thread3=new ExempleThread2(3, "and Everybody ",40);

        thread1.start();
        thread2.start();
        thread3.start();

        System.exit(0);
    }

}
