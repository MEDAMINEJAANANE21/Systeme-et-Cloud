package fr.uga.miage.m1.tp2.MyProdConsV1;

import java.util.Arrays;
import java.util.Random;

public class Stockage {
    private final Random numGenerator;
    private final Buffer buffer;

    Stockage(int taille) {
        this.buffer = new Buffer(taille);
        this.numGenerator = new Random(System.currentTimeMillis());
    }

    public synchronized void produire(Object obj, ThreadProduire producer) {
        int producerId = producer.getId();
        try {
            while (buffer.isFull()) {
                System.out.println("PRODUCER "+producerId+" is waiting: memory full");
                wait();
            }
        } catch (InterruptedException e) {
            UnexpectedSituation.exit("interrupted wait in producteur " + producerId, e);
        }
        buffer.put(obj);
        System.out.println( "Object produced by thread ["+producerId+"], Stockage= "+buffer.size());

        // Notify all other threads
        notifyAll();
    }

    public synchronized Object consommer(ThreadConsommer consumer) {
        int consumerId = consumer.getId();
        Object consumedObj = null;

        try {
            while (buffer.isEmpty()) {
                System.out.println("CONSUMER ["+consumerId+"] is waiting : memory empty");
                wait();
            }
            System.out.println("CONSUMER ["+consumerId+"] is waiting : memory empty");
        } catch (InterruptedException e) {
            UnexpectedSituation.exit("interrupted wait in producteur "+consumerId, e);
        }
        consumedObj = this.buffer.pick();
        System.out.println( "Object consumed by thread ["+consumerId+"], Stockage= "+this.buffer.size());

        // Notify all other threads
        notifyAll();

        return consumedObj;
    }

    /** randint
     *  Return random integer between 0 and max
     */
    public int randint(int max) {
        return this.numGenerator.nextInt(max);
    }

    public int getOccupiedSpace() {
        return this.buffer.size();
    }

    @Override
    public String toString() {
        return "Stockage{" +
                "buffer=" + this.buffer +
                '}';
    }
}
