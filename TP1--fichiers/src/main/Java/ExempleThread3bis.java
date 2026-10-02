import java.util.concurrent.TimeUnit;

public class ExempleThread3bis extends Thread {

    private String toSay;
    private int toWait;
    private int number;

    public ExempleThread3bis(int number, String toSay, int toWait) {
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
        ExempleThread3bis thread1, thread2, thread3;
        thread1=new ExempleThread3bis(1, "Hello ", 50);
        thread2=new ExempleThread3bis(2, "World ",25);
        thread3=new ExempleThread3bis(3, "and Everybody ",40);

        thread1.setDaemon(true);
        thread2.setDaemon(true);
        thread3.setDaemon(true);

        thread1.start();
        thread2.start();
        thread3.start();
    }
    // c'est une boucle infinie donc les threads ne termine jamais, pour les tuer il faut tuer tous le processus

}
