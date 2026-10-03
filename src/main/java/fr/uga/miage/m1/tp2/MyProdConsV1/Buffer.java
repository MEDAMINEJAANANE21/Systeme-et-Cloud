package fr.uga.miage.m1.tp2.MyProdConsV1;


import java.util.Arrays;

public class Buffer {
    private static final int DEFAULT_BUFFER_SIZE = 42;
    private final Object[] buffer;
    private int numberOfElements;

    Buffer(int taille) {
        this.buffer = taille > 0 ? new Object[taille] : new Object[DEFAULT_BUFFER_SIZE];
        this.numberOfElements = 0;
        Arrays.fill(buffer, null);
    }

    public boolean isFull() { return numberOfElements >= buffer.length; }
    public boolean isEmpty() { return numberOfElements <= 0; }

    public void put(Object obj) {
        this.buffer[numberOfElements++] = obj;
    }

    public Object pick() {
        Object picked = this.buffer[numberOfElements-1];
        this.buffer[--numberOfElements] = null;
        return picked;
    }

    public int size() { return numberOfElements; }

    @Override
    public String toString() {
        return "Buffer{" +
                "buffer=" + Arrays.toString(buffer) +
                '}';
    }
}
