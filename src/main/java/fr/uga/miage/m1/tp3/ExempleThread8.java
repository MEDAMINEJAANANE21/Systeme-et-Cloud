import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ExempleThread8 {

    private static final int NBTHREADS = 1;
    private static final int ITERATIONS = 1;

    public static void  main(String args[]) {
        Thread threadsD[] = new Thread[NBTHREADS];
        Thread threadsR[] = new Thread[NBTHREADS];

        Compte compte = new CompteVersion3(0);
        
        Random r = new Random();
        int t; 

        for (int j = 0; j < NBTHREADS; j++) {
            threadsD[j]=new Thread(new ThreadDeposer(j, compte, ITERATIONS));
            threadsD[j].setName(j+"");
            threadsR[j]=new Thread(new ThreadRetirer(j+NBTHREADS, compte, ITERATIONS));
            threadsR[j].setName((j+NBTHREADS)+"");
        }

        try {
            for (int j = 0; j < NBTHREADS; j++) {
                t = r.nextInt(100);
                if (t%2 == 0) {
                    threadsD[j].start();
                    t = r.nextInt(100);
                    TimeUnit.MILLISECONDS.sleep(t);
                    threadsR[j].start();
                } else {
                    threadsR[j].start();
                    t = r.nextInt(100);
                    TimeUnit.MILLISECONDS.sleep(t);
                    threadsD[j].start();            
                }
            }
        } catch (InterruptedException e ) {
            UnexpectedSituation.exit("interrupted sleep in thread "+ Thread.currentThread().getName(), e);
        }

        try {
            for (int j = 0; j < NBTHREADS; j++) {
                threadsD[j].join();
                threadsR[j].join();
            }
        } catch (InterruptedException e ) {
            UnexpectedSituation.exit("interrupted join in thread "+ Thread.currentThread().getName(), e);
        }

        System.out.print(compte.consulter() +" ");
        System.exit(0);

    }

}
