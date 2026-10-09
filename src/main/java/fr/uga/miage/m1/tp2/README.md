# Rendue TP2 - Système & Cloud

##### Mohamed-Amine JAANANE - Omar BARKOK | 02-10-2026

##### [Repo GitHub de notre solution du TP1](https://github.com/MEDAMINEJAANANE21/Systeme-et-Cloud/tree/main)

---

### 3.1 `ExempleThread6.java`  

Non après plusieurs exécutions je n'ai jamais obtenu de solde négatif, cela s'explique par le faite que les opérations de
retrait son faites par un thread à la fois et uniquement si le solde est >= la valeur à retirer.

### 3.2 `ExempleThread7.java`  

Dans `ExempleThread6`, le test `while (solde < montant)` réévalue la condition après chaque retour de wait(). Un thread ne
retire donc de l’argent que si le solde est réellement suffisant. Dans `ExempleThread7`, le if ne teste la condition qu’avant
l’attente : après son réveil, le thread retire directement sans vérifier de nouveau le solde. En cas de réveil parasite ou
de concurrence entre plusieurs threads, le solde peut alors devenir négatif.

### 3.3 Retour sur `ExempleThread6.java`  

Exemple de cas problématique de l'exemple 6 est, immaginons les 3 threads suivants :

T1 : retire 30 du compte  
T2 : retire 20 du compte  
T3 : ajoute 50 au compte

Immaginon la séquence d'éxécution suivante, le compte comment en solde 0:

1. T1 essaye de retirer 30 du compte : il se met en attente (wait)
2. T2 essaye de retirer 20 du compte : il se met en attente (wait)
3. T3 ajoute 50 au compte : le notify va réveiller un des deux threads en attente (T1 ou T2) supposon que se soit T1.
4. T1 va retirer 30 du compte, le compte est maintenant à 20 (50 - 30) < 30, donc T1 va se mettre en attente (wait) à nouveau.
5. T2 reste endormi.
6. T3 ajoute 50, le solde et à 70 et notify T1.
7. T1 retire 30, solde=40.

Si on continue avec cette «mal chance» on constate que T2 risque de ne jamais être choisi.

### 3.4 Le problème des producteurs/consommateurs — version avec Moniteurs Java

Voici mon implémentation de la class `Stockage`

```java
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
                System.out.println("+PROD[" + producerId + "] is waiting: memory full");
                wait();
            }
        } catch (InterruptedException e) {
            UnexpectedSituation.exit("interrupted wait in producteur " + producerId, e);
        }
        buffer.put(obj);
        System.out.println("+PROD[" + producerId + "] produced, Objects in memory= " + buffer.size());

        // Notify all other threads
        notifyAll();
    }

    public synchronized Object consommer(ThreadConsommer consumer) {
        int consumerId = consumer.getId();
        Object consumedObj;

        try {
            while (buffer.isEmpty()) {
                System.out.println("-CONS[" + consumerId + "] is waiting : memory empty");
                wait();
            }
        } catch (InterruptedException e) {
            UnexpectedSituation.exit("interrupted wait in consumer " + consumerId, e);
        }
        consumedObj = this.buffer.pick();
        System.out.println("-CONS[" + consumerId + "] consumed, Objects in memory= " + this.buffer.size());

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
```

### 3.5 Le problème des producteurs/consommateurs — version avec sémaphores n°1

Voici mon implémentation de Stockage (`Storage` dans cette version) qui résoud le problème consommateur/producteur avec
Les sémaphores de la libraire `java.util.concurrent.Semaphore;`:  

```java
package fr.uga.miage.m1.tp2.MyProdConsV2;

import java.util.concurrent.Semaphore;

public class Storage {
    private final int STORAGE_SIZE;
    private final Semaphore empty;
    private final Semaphore full;
    private final Semaphore mutex;
    private final Buffer buffer;

    Storage(int taille) {
        this.STORAGE_SIZE = taille > 0
                ? taille
                : 100;
        this.buffer = new Buffer(this.STORAGE_SIZE);
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
```

### 3.6 Sémaphores en Java