package fr.uga.miage.m1.tp2.MyProdConsV2;

import java.util.concurrent.Semaphore;

public class Storage {
    protected final Semaphore empty;
    private final Semaphore full;
    private final Semaphore mutex;
    private final Buffer buffer;

    Storage(int taille) {
        int STORAGE_SIZE = taille > 0
                ? taille
                : 100;
        this.buffer = new Buffer(STORAGE_SIZE);
        this.empty = new Semaphore(STORAGE_SIZE, true); // N cases vides au début
        this.full = new Semaphore(0, true); // 0 cases pleines au début
        this.mutex = new Semaphore(1, true);
    }

    public void produire(Object obj) throws InterruptedException {
        putItem(obj);
    }

    public Object consommer() throws InterruptedException {
        return getItem();
    }


    private Object getItem() throws InterruptedException {
        Object consumed;
        full.acquire();
        mutex.acquire();
        consumed = buffer.pick();
        mutex.release();
        empty.release();
        return consumed;
    }

    private void putItem(Object obj) throws InterruptedException {
        empty.acquire();
        mutex.acquire();
        buffer.put(obj);
        mutex.release();
        full.release();
    }

    public int getOccupiedSpace() throws InterruptedException {
        mutex.acquire();
        int size = this.buffer.size();
        mutex.release();
        return size;
    }

    @Override
    public synchronized String toString() {
        return "Stockage{" + this.buffer + '}';
    }
}
