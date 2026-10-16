class CompteVersion3 extends Compte {

    protected double solde;

    CompteVersion3(double i) {
        solde = i;
    }

    public void deposer(double montant) {
        String curNumber = (Thread.currentThread()).getName();
        System.out.println(curNumber + " DEBUT deposer " + solde);
        solde = solde + montant;
        notify();
        System.out.println(curNumber + " |---- deposer ");
    }

    public void retirer(double montant) {
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
