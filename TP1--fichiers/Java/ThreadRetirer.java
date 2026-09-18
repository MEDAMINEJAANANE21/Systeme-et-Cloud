/**
 * Thread qui retire
 */
class ThreadRetirer extends Thread {

    private int number;
    private Tableau t;
    private int nbIter;

    public ThreadRetirer(int number, Tableau i, int nbIterations) {
        this.number = number;
        this.t = i;
        this.nbIter = nbIterations;
    }

    public void run() {
        for (int j = 0; j < nbIter; j++)
            t.decTab();
    }
}
