package fr.uga.miage.m1.tp2.MyProdConsV1;

public class Stockage {
    private static final int NBTHREADS = 20;
    private static final int DEFAULT_SIZE = 42;

    private Object[] tableau;
    private int nbElements;

    Stockage(int taille) {
        this.tableau = taille > 0 ? new Object[taille] : new Object[DEFAULT_SIZE];
        nbElements = 0;
    }

    public Object consommer() {
       return null;
    }

    public void produire(Object obj, int numero) {
        try {
            while (nbElements >= tableau.length) {
                System.out.println("Producteur " + numero + " attend : stockage plein");
                wait();
            }
        } catch (InterruptedException e) {
            UnexpectedSituation.exit("interrupted wait in producteur " + numero, e);
        }
        tableau[nbElements] = obj;
        nbElements++;
        System.out.println( "Stockage = " + nbElements );
    }

}
