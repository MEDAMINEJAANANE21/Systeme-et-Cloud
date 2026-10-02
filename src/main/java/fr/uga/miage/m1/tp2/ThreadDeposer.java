package fr.uga.miage.m1.tp2;

/**
 * Thread qui depose
 */
class ThreadDeposer implements Runnable {
    public int number;
    private Compte exI;
    private int nbIter;

    public ThreadDeposer(int number, Compte i, int nbIterations) {
        this.number = number;
        this.exI = i;
        this.nbIter = nbIterations;
    }

    public void run() {
        for (int j = 0; j < nbIter; j++) {
            exI.deposer(10.0);
        }
    }
}

