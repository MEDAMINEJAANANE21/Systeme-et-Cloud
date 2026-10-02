package fr.uga.miage.m1.tp2.MyProdConsV1;

public class ThreadProduire implements Runnable {
    private final Stockage stockage;

    public ThreadProduire(Stockage stockage) {
        this.stockage = stockage;
    }

    @Override
    public void run() {
        stockage.consommer();
    }
}
