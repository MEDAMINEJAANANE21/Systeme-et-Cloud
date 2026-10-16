# Rendue TP3 - Synchronisation de Threads

##### Mohamed-Amine JAANANE - Omar BARKOK | 02-10-2026 | Système & Cloud

---

### 1 - Erreur IllegalMonitorStateException

Les méthodes `deposer` et `retirer` de `CompteVersion3` ne sont pas `synchronized`, alors qu'elles appellent `notify()` et wait().
Or ces méthodes ne peuvent être appelées que par un thread qui possède le moniteur de l'objet (c’est à dire un seul
thread à la fois) sinon la machine virtuelle lève une IllegalMonitorStateException. Cette exception hérite de
RuntimeException : elle n'est pas contrôlée, donc le code compile sans erreur et le problème n'apparaît qu'à l'exécution du code.

La Correction possible c’est de rendre deposer et retirer synchronized, et utiliser un while à la place du if, et notifyAll().

### 2.1 - Le problème des lecteurs-rédacteurs :

La classe SharedRsc mémorise le nombre de lecteurs en train de lire (lecteurs) et un booléen qui indique si un rédacteur
écrit (ecriture). Un lecteur attend tant qu'un rédacteur écrit. Un rédacteur attend tant qu'un lecteur lit ou qu'un rédacteur écrit.
Les lecteurs et rédacteurs partageant la même salle d'attente, notifyAll() est utilisé à la fin d'une lecture, seulement
si c'est le dernier lecteur, et à la fin d'une écriture, toujours.

### 2.2 - Le problème des lecteurs-rédacteurs :

`SharedRsc.java`
```java
public class SharedRsc {
    private int lecteurs = 0; // nombre de lecteurs en train de lire
    private boolean ecriture = false; // true si un redacteur est en train d'ecrire

    public synchronized void beginRead() throws InterruptedException {
        String nom = Thread.currentThread().getName();
        while (ecriture) {
            System.out.println(nom + " (lecteur) attend : un redacteur ecrit");
            wait();
            System.out.println(nom + " (lecteur) se reveille");
        }
        lecteurs++;
    }

    public synchronized void endRead() {
        lecteurs--;
        if (lecteurs == 0) {
            notifyAll();
        }
    }

    public synchronized void beginWrite() throws InterruptedException {
        String nom = Thread.currentThread().getName();
        while (ecriture || lecteurs > 0) {
            System.out.println(nom + " (redacteur) attend : la ressource est utilisee");
            wait();
            System.out.println(nom + " (redacteur) se reveille");
        }
        ecriture = true;
    }

    public synchronized void endWrite() {
        ecriture = false;
        notifyAll();
    }
}
```

`Fichier.java`
```java
import java.util.concurrent.atomic.AtomicInteger;

public class Fichier {
    private final AtomicInteger lecteursActifs = new AtomicInteger();
    private final AtomicInteger redacteursActifs = new AtomicInteger();
    private final AtomicInteger violations = new AtomicInteger();
    private final AtomicInteger nbLectures = new AtomicInteger();
    private final AtomicInteger nbEcritures = new AtomicInteger();

    public void lire(int dureeMs) throws InterruptedException {
        String nom = Thread.currentThread().getName();
        int n = lecteursActifs.incrementAndGet();
        if (redacteursActifs.get() > 0) {
            violations.incrementAndGet();
            System.out.println("!!! VIOLATION : " + nom + " lit pendant une ecriture");
        }
        System.out.println(nom + " LIT        (lecteurs simultanes : " + n + ")");
        Thread.sleep(dureeMs);
        System.out.println(nom + " a fini de lire");
        nbLectures.incrementAndGet();
        lecteursActifs.decrementAndGet();
    }

    public void ecrire(int dureeMs) throws InterruptedException {
        String nom = Thread.currentThread().getName();
        int w = redacteursActifs.incrementAndGet();
        if (w > 1 || lecteursActifs.get() > 0) {
            violations.incrementAndGet();
            System.out.println("!!! VIOLATION : " + nom + " ecrit alors que la ressource n'est pas libre");
        }
        System.out.println(nom + " ECRIT");
        Thread.sleep(dureeMs);
        System.out.println(nom + " a fini d'ecrire");
        nbEcritures.incrementAndGet();
        redacteursActifs.decrementAndGet();
    }

    public int getViolations() { return violations.get(); }
    public int getNbLectures() { return nbLectures.get(); }
    public int getNbEcritures() { return nbEcritures.get(); }
}
```

`Lecture.java`
```java
import java.util.Random;

public class Lecteur implements Runnable {
    private final SharedRsc acces;
    private final Fichier fichier;
    private final int nbIter;
    private final int pauseMax;
    private final Random rnd = new Random();

    public Lecteur(SharedRsc acces, Fichier fichier, int nbIter, int pauseMax) {
        this.acces = acces;
        this.fichier = fichier;
        this.nbIter = nbIter;
        this.pauseMax = pauseMax;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < nbIter; i++) {
                acces.beginRead();
                try {
                    fichier.lire(20);
                } finally {
                    acces.endRead();
                }
                Thread.sleep(rnd.nextInt(pauseMax + 1));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

`Redacteur.java`
```java
import java.util.Random;

public class Redacteur implements Runnable {
    private final SharedRsc acces;
    private final Fichier fichier;
    private final int nbIter;
    private final int pauseMax;
    private final Random rnd = new Random();

    public Redacteur(SharedRsc acces, Fichier fichier, int nbIter, int pauseMax) {
        this.acces = acces;
        this.fichier = fichier;
        this.nbIter = nbIter;
        this.pauseMax = pauseMax;
    }
    
    @Override
    public void run() {
        try {
            for (int i = 0; i < nbIter; i++) {
                acces.beginWrite();
                try {
                    fichier.ecrire(30);
                } finally {
                    acces.endWrite();
                }
                Thread.sleep(rnd.nextInt(pauseMax + 1));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

`Main.java`
```java
public class Main {
    public static void main(String[] args) throws InterruptedException {
        final int NB_LECTEURS = 5;
        final int NB_REDACTEURS = 3;
        final int ITERATIONS = 3;
        final int PAUSE_MAX = 40;

        SharedRsc acces = new SharedRsc();
        Fichier fichier = new Fichier();
        Thread[] threads = new Thread[NB_LECTEURS + NB_REDACTEURS];

        for (int i = 0; i < NB_LECTEURS; i++) {
            threads[i] = new Thread(new Lecteur(acces, fichier, ITERATIONS, PAUSE_MAX), "L" + i);
        }
        for (int i = 0; i < NB_REDACTEURS; i++) {
            threads[NB_LECTEURS + i] = new Thread(new Redacteur(acces, fichier, ITERATIONS, PAUSE_MAX), "R" + i);
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        System.out.println("Fin : " + fichier.getNbLectures() + " lectures, "
                + fichier.getNbEcritures() + " ecritures, "
                + fichier.getViolations() + " violation(s)");
    }
}
```

Sortie du terminal (extrait) : les rédacteurs R2 et R1 sont réveillés mais se rendorment car R0 est entré avant eux ; pendant l'écriture de R0, les lecteurs attendent.
```
R1 (redacteur) attend : la ressource est utilisee
L3 LIT        (lecteurs simultanes : 5)
L2 LIT        (lecteurs simultanes : 3)
L0 LIT        (lecteurs simultanes : 1)
L4 LIT        (lecteurs simultanes : 4)
R2 (redacteur) attend : la ressource est utilisee
L1 a fini de lire
L3 a fini de lire
L2 a fini de lire
L0 a fini de lire
L4 a fini de lire
R0 (redacteur) se reveille
R2 (redacteur) se reveille
R2 (redacteur) attend : la ressource est utilisee
R1 (redacteur) se reveille
R1 (redacteur) attend : la ressource est utilisee
R0 ECRIT
L3 (lecteur) attend : un redacteur ecrit
L1 (lecteur) attend : un redacteur ecrit
L0 (lecteur) attend : un redacteur ecrit
L2 (lecteur) attend : un redacteur ecrit
L4 (lecteur) attend : un redacteur ecrit
R0 a fini d'ecrire
R2 (redacteur) se reveille
R2 ECRIT
```



### 3.1 - Tampon producteurs-consommateurs avec Lock et Condition :

### 3.2 - Lecteurs-rédacteurs avec Lock et Condition :

### 4 - Compléments sur les lecteurs-rédacteurs :

### 4.1 -  Priorité aux lecteurs :

### 4.2 - Priorité aux rédacteurs : 

### 4.3 Ordre FIFO garanti : 



















