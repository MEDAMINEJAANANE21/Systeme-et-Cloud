package fr.uga.miage.m1.tp2;

class CompteVersion2 extends Compte {

    protected double solde;

    CompteVersion2(double i) {
        solde = i;
    }

    public synchronized void deposer(double montant) {
        String curNumber = (Thread.currentThread()).getName();
        System.out.println(curNumber + " DEBUT deposer " + solde);
        solde = solde + montant;
        notify();
        System.out.println(curNumber + " |---- deposer ");
    }

    public synchronized void retirer(double montant) {
        String curNumber = (Thread.currentThread()).getName();
        System.out.println(curNumber + " DEBUT retirer " + solde);
        try {
            if (solde < montant) {
                wait();
            }
        } catch (InterruptedException e) {
            UnexpectedSituation.exit("interrupted wait in thread "+ Thread.currentThread().getName(), e);
        }
        solde = solde - montant;
        System.out.println(curNumber + " |---- retirer " + solde);
    }

    public synchronized double consulter() {
        return solde;
    }
}
