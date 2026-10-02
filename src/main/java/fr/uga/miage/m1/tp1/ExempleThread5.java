public class ExempleThread5  {

    private static final int NBTHREADS = 100;
    private static final int ITERATIONS = 10000;

    public static void  main(String args[]) {
        ThreadDeposer2 threadsD[] = new ThreadDeposer2[NBTHREADS];
        ThreadRetirer2 threadsR[] = new ThreadRetirer2[NBTHREADS];

        Tableau2 t = new Tableau2();


        for (int j = 0; j < NBTHREADS; j++) {
            threadsD[j]=new ThreadDeposer2(j, t, ITERATIONS);
            threadsR[j]=new ThreadRetirer2(j, t, ITERATIONS);
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
        } catch (InterruptedException e) {
            UnexpectedSituation.exit("interrupted sleep in thread "+ Thread.currentThread().getName(), e);
        }


        t.printTab();
        System.exit(0);

    }

}
