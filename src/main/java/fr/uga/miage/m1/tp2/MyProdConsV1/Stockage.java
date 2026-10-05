package fr.uga.miage.m1.tp2.MyProdConsV1;

public class Stockage {
    private final Buffer buffer;

    Stockage(int taille) {
        this.buffer = new Buffer(taille);
    }

    public synchronized void produire(Object obj, ThreadProduire producer) {
        int producerId = producer.getId();
        try {
            while (buffer.isFull()) {
                System.out.println("+PROD["+producerId+"] is waiting: memory full");
                wait();
            }
        } catch (InterruptedException e) {
            UnexpectedSituation.exit("interrupted wait in producteur " + producerId, e);
        }
        buffer.put(obj);
        System.out.println("+PROD["+producerId+"] produced, Objects in memory= "+buffer.size());

        // Notify all other threads
        notifyAll();
    }

    public synchronized Object consommer(ThreadConsommer consumer) {
        int consumerId = consumer.getId();
        Object consumedObj;

        try {
            while (buffer.isEmpty()) {
                System.out.println("-CONS["+consumerId+"] is waiting : memory empty");
                wait();
            }
        } catch (InterruptedException e) {
            UnexpectedSituation.exit("interrupted wait in consumer "+consumerId, e);
        }
        consumedObj = this.buffer.pick();
        System.out.println("-CONS["+consumerId+"] consumed, Objects in memory= "+this.buffer.size());

        // Notify all other threads
        notifyAll();

        return consumedObj;
    }

    public synchronized int getOccupiedSpace() {
        return this.buffer.size();
    }

    @Override
    public synchronized String toString() {
        return "Stockage{" + this.buffer + '}';
    }
}
