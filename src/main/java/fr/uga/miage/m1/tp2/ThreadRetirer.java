package fr.uga.miage.m1.tp2;

/**
 * Thread qui retire
 */
class ThreadRetirer implements Runnable {
    private int number;
    private Compte exI;
    private int nbIter;

    public ThreadRetirer(int number, Compte i, int nbIterations) {
        this.number = number;
        this.exI = i;
        this.nbIter = nbIterations;
    }

    public void run() {
        for (int j = 0; j < nbIter; j++) {
            exI.retirer(10.0);
        }
    }
}