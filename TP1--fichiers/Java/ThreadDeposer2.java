/**
 * Thread qui depose
 */
class ThreadDeposer2 extends Thread {

    private int number;
    private Tableau2 t;
    private int nbIter;

    public ThreadDeposer2(int number, Tableau2 i, int nbIterations) {
        this.number = number;
        this.t = i;
        this.nbIter = nbIterations;
    }

    public void run() {
        for (int j = 0; j <nbIter; j++) {
            t.incTab();
        }
    }
}
