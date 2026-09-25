public class ExempleThread6 {

    private static final int NBTHREADS = 20;
    private static final int ITERATIONS = 1000;

    public static void  main(String args[]) {
        Thread threadsD[] = new Thread[NBTHREADS];
        Thread threadsR[] = new Thread[NBTHREADS];

        Compte compte = new CompteVersion1(0);

        for (int j = 0; j < NBTHREADS; j++) {
            threadsD[j]=new Thread(new ThreadDeposer(j, compte, ITERATIONS));
            threadsD[j].setName(j+"");
            threadsR[j]=new Thread(new ThreadRetirer(j+NBTHREADS, compte, ITERATIONS));
            threadsR[j].setName((j+NBTHREADS)+"");
        }

        for (int j = 0; j < NBTHREADS; j++) {
            threadsD[j].start();
            threadsR[j].start();
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
