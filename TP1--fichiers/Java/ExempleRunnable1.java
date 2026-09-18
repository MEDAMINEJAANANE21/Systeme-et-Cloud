public class ExempleRunnable1 implements Runnable {

    private String toSay;
    private int toWait;
    private int number;

    public ExempleRunnable1(String toSay) {
        this.toSay=toSay;
    }

    @Override
    public void run() {
        for (int i =1; i< 1000; i++)
            System.out.print(i + " " + toSay );
    }

    public static void  main(String args[]) {

        ExempleRunnable1 runnable1, runnable2, runnable3;

        runnable1=new ExempleRunnable1("Hello ");
        runnable2=new ExempleRunnable1("World ");
        runnable3=new ExempleRunnable1("and Everybody ");
        
        
        new Thread(runnable1).start();
        new Thread(runnable2).start();
        new Thread(runnable3).start();
       

        System.exit(0);
    }

}
