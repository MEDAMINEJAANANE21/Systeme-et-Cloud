/**
 * Thread qui depose
 */
class ThreadDeposer implements Runnable {

    private int number;
    private Tableau t;
    private int nbIter;

    public ThreadDeposer(int number, Tableau i, int nbIterations) {
        this.number = number;
        this.t = i;
        this.nbIter = nbIterations;
    }

    @Override
    public void run() {
        for (int j = 0; j < nbIter; j++) {
            t.incTab();
        }
    }
}
