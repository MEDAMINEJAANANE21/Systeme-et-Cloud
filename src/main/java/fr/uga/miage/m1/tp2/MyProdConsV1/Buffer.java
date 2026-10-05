package fr.uga.miage.m1.tp2.MyProdConsV1;


import java.util.Arrays;

public class Buffer {
    private static final int DEFAULT_BUFFER_SIZE = 42;
    private final Object[] buffer;
    private int numberOfElements;

    Buffer(int taille) {
        buffer = taille > 0
                ? new Object[taille]
                : new Object[DEFAULT_BUFFER_SIZE];

        numberOfElements = 0;
        // Arrays.fill(buffer, null);
    }

    public boolean isFull() { return numberOfElements >= buffer.length; }
    public boolean isEmpty() { return numberOfElements <= 0; }

    public void put(Object obj) {
        if (this.numberOfElements >= this.buffer.length) throw new RuntimeException("Some is trying to put in a full buffer >:(");
        this.buffer[numberOfElements++] = obj;
    }

    public Object pick() {
        if (this.numberOfElements <= 0) throw new RuntimeException("Some is trying to pick form an empty buffer >:(");
        Object picked = this.buffer[numberOfElements-1];
        this.buffer[--numberOfElements] = null;
        return picked;
    }

    public int size() { return numberOfElements; }

    @Override
    public String toString() {
        return "Buffer=" + Arrays.toString(buffer);
    }
}
