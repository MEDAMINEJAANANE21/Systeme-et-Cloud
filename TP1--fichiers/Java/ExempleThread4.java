public class ExempleThread4  {

    private static final int NBTHREADS = 100;
    private static final int ITERATIONS = 10000;

    public static void  main(String args[]) {
        ThreadDeposer threadsD[] = new ThreadDeposer[NBTHREADS];
        ThreadRetirer threadsR[] = new ThreadRetirer[NBTHREADS];

        Tableau t = new Tableau();


        for (int j = 0; j < NBTHREADS; j++) {
            threadsD[j]=new ThreadDeposer(j, t, ITERATIONS);
            threadsR[j]=new ThreadRetirer(j, t, ITERATIONS);
        }

        for (int j = 0; j < NBTHREADS; j++) {
            new Thread(threadsD[j]).start();
            new Thread(threadsR[j]).start();
        }

        try {
            for (int j = 0; j < NBTHREADS; j++) {
                new Thread(threadsD[j]).join();
                new Thread(threadsR[j]).join();
            }
        } catch (InterruptedException e) {
            UnexpectedSituation.exit("interrupted join in thread "+ Thread.currentThread().getName(), e);
        }


        t.printTab();
        System.exit(0);

    }

}
