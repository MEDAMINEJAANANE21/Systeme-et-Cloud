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

La classe Stockage utilise un ReentrantLock et deux Condition : pasPlein (où attendent les producteurs) et pasVide
(où attendent les consommateurs). Après une production on appelle pasVide.signal(), après une consommation pasPlein.signal().
notifyAll() n'est plus utilisé. Les classes Producteur, Consommateur et Main sont celles de la version avec moniteurs (paquetage MyProdConsV4).

`Stockage.java`
```java
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Stockage {
    private final Object[] tampon;
    private int tete = 0;
    private int queue = 0;
    private int nbElements = 0;

    private final ReentrantLock verrou = new ReentrantLock();
    private final Condition pasPlein = verrou.newCondition();
    private final Condition pasVide = verrou.newCondition();

    public Stockage(int capacite) {
        tampon = new Object[capacite];
    }

    public void produire(Object o) throws InterruptedException {
        String nom = Thread.currentThread().getName();
        verrou.lock();
        try {
            while (nbElements == tampon.length) {
                System.out.println(nom + " : stockage PLEIN, je m'endors");
                pasPlein.await();
                System.out.println(nom + " : je me reveille (producteur)");
            }
            tampon[queue] = o;
            queue = (queue + 1) % tampon.length;
            nbElements++;
            System.out.println(nom + " produit " + o + "  [" + nbElements + "/" + tampon.length + "]");
            pasVide.signal();
        } finally {
            verrou.unlock();
        }
    }

    public Object consommer() throws InterruptedException {
        String nom = Thread.currentThread().getName();
        verrou.lock();
        try {
            while (nbElements == 0) {
                System.out.println(nom + " : stockage VIDE, je m'endors");
                pasVide.await();
                System.out.println(nom + " : je me reveille (consommateur)");
            }
            Object o = tampon[tete];
            tampon[tete] = null;
            tete = (tete + 1) % tampon.length;
            nbElements--;
            System.out.println(nom + " consomme " + o + "  [" + nbElements + "/" + tampon.length + "]");
            pasPlein.signal();
            return o;
        } finally {
            verrou.unlock();
        }
    }
}
```

### 3.2 - Lecteurs-rédacteurs avec Lock et Condition :

La classe SharedRsc utilise deux Condition : peutLire et peutEcrire. Quand le dernier lecteur sort, un seul rédacteur
est réveillé (peutEcrire.signal()). Quand un rédacteur termine, tous les lecteurs sont réveillés (peutLire.signalAll())
ainsi qu'un rédacteur. Les autres classes sont celles de la question 2 (paquetage RWLock).

`SharedRsc.java`
```java
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SharedRsc {
    private final ReentrantLock verrou = new ReentrantLock();
    private final Condition peutLire = verrou.newCondition();
    private final Condition peutEcrire = verrou.newCondition();

    private int lecteurs = 0;
    private boolean ecriture = false;

    public void beginRead() throws InterruptedException {
        String nom = Thread.currentThread().getName();
        verrou.lock();
        try {
            while (ecriture) {
                System.out.println(nom + " (lecteur) attend : un redacteur ecrit");
                peutLire.await();
                System.out.println(nom + " (lecteur) se reveille");
            }
            lecteurs++;
        } finally {
            verrou.unlock();
        }
    }
    
     public void endRead() {
        verrou.lock();
        try {
            lecteurs--;
            if (lecteurs == 0) {
                peutEcrire.signal();
            }
        } finally {
            verrou.unlock();
        }
    }

    public void beginWrite() throws InterruptedException {
        String nom = Thread.currentThread().getName();
        verrou.lock();
        try {
            while (ecriture || lecteurs > 0) {
                System.out.println(nom + " (redacteur) attend : la ressource est utilisee");
                peutEcrire.await();
                System.out.println(nom + " (redacteur) se reveille");
            }
            ecriture = true;
        } finally {
            verrou.unlock();
        }
    }

    public void endWrite() {
        verrou.lock();
        try {
            ecriture = false;
            peutLire.signalAll();
            peutEcrire.signal();
        } finally {
            verrou.unlock();
        }
    }
}
```

### 4 - Compléments sur les lecteurs-rédacteurs :
Les trois variantes sont testées avec le même scénario (classe Scenario) : L1 lit de 0 à 600 ms ; R1 demande à écrire à 100 ms ; L2, L3, R2 et L4 arrivent à 200, 300, 400 et 500 ms. Dans chaque paquetage (RWPrioLecteurs, RWPrioRedacteurs, RWFifo), seule la classe SharedRsc change ; les autres classes sont celles de la question 2.


`Scenario.java`
```java
public class Scenario {
    private static final SharedRsc ressource = new SharedRsc();
    private static final Fichier fichier = new Fichier();

    private static Thread lancer(String nom, boolean ecriture, int debutMs, int dureeMs) {
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(debutMs);
                System.out.println(nom + " DEMANDE " + (ecriture ? "l'ecriture" : "la lecture"));
                if (ecriture) {
                    ressource.beginWrite();
                    try { fichier.ecrire(dureeMs); } finally { ressource.endWrite(); }
                } else {
                    ressource.beginRead();
                    try { fichier.lire(dureeMs); } finally { ressource.endRead(); }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, nom);
        t.start();
        return t;
    }

    public static void main(String[] args) throws InterruptedException {
        Thread[] ts = {
            lancer("L1", false,   0, 600),   // L1 lit de 0 a 600 ms
            lancer("R1", true,  100, 300),   // R1 demande a ecrire a 100 ms
            lancer("L2", false, 200, 300),   // L2 arrive a 200 ms
            lancer("L3", false, 300, 300),   // L3 arrive a 300 ms
            lancer("R2", true,  400, 300),   // R2 demande a ecrire a 400 ms
            lancer("L4", false, 500, 300)    // L4 arrive a 500 ms
        };
        for (Thread t : ts) t.join();
        System.out.println("Fin : " + fichier.getViolations() + " violation(s)");
    }
}

```
### 4.1 -  Priorité aux lecteurs :
Un compteur lecteursEnAttente est ajouté. Un lecteur n'attend que si un rédacteur écrit. Un rédacteur attend tant que ecriture, lecteurs > 0 ou lecteursEnAttente > 0. À la fin d'une écriture, les lecteurs en attente sont réveillés en priorité.

`SharedRsc.java`
```java
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SharedRsc {
    private final ReentrantLock verrou = new ReentrantLock();
    private final Condition peutLire = verrou.newCondition();
    private final Condition peutEcrire = verrou.newCondition();

    private int lecteurs = 0;
    private int lecteursEnAttente = 0;
    private boolean ecriture = false;

    public void beginRead() throws InterruptedException {
        String nom = Thread.currentThread().getName();
        verrou.lock();
        try {
            lecteursEnAttente++;
            try {
                while (ecriture) {       // seul un redacteur en train d'ecrire bloque un lecteur
                    System.out.println(nom + " (lecteur) attend : un redacteur ecrit");
                    peutLire.await();
                    System.out.println(nom + " (lecteur) se reveille");
                }
            } finally {
                lecteursEnAttente--;
            }
            lecteurs++;
        } finally {
            verrou.unlock();
        }
    }

    public void endRead() {
        verrou.lock();
        try {
            lecteurs--;
            if (lecteurs == 0) {
                peutEcrire.signal();
            }
        } finally {
            verrou.unlock();
        }
    }

    public void beginWrite() throws InterruptedException {
        String nom = Thread.currentThread().getName();
        verrou.lock();
        try {
            while (ecriture || lecteurs > 0 || lecteursEnAttente > 0) {
                System.out.println(nom + " (redacteur) attend : des lecteurs lisent ou attendent");
                peutEcrire.await();
                System.out.println(nom + " (redacteur) se reveille");
            }
            ecriture = true;
        } finally {
            verrou.unlock();
        }
    }

    public void endWrite() {
        verrou.lock();
        try {
            ecriture = false;
            if (lecteursEnAttente > 0) {
                peutLire.signalAll();    // les lecteurs passent avant les autres redacteurs
            } else {
                peutEcrire.signal();
            }
        } finally {
            verrou.unlock();
        }
    }
}

```

### 4.2 - Priorité aux rédacteurs :
Un compteur redacteursEnAttente est ajouté. Un lecteur attend tant qu'un rédacteur écrit ou attend. À la fin d'une écriture, un rédacteur en attente est réveillé en priorité ; les lecteurs ne le sont que s'il n'y en a plus.

`SharedRsc.java`
```java
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SharedRsc {
    private final ReentrantLock verrou = new ReentrantLock();
    private final Condition peutLire = verrou.newCondition();
    private final Condition peutEcrire = verrou.newCondition();

    private int lecteurs = 0;
    private int redacteursEnAttente = 0;
    private boolean ecriture = false;

    public void beginRead() throws InterruptedException {
        String nom = Thread.currentThread().getName();
        verrou.lock();
        try {
            while (ecriture || redacteursEnAttente > 0) {
                System.out.println(nom + " (lecteur) attend : un redacteur ecrit ou attend");
                peutLire.await();
                System.out.println(nom + " (lecteur) se reveille");
            }
            lecteurs++;
        } finally {
            verrou.unlock();
        }
    }

    public void endRead() {
        verrou.lock();
        try {
            lecteurs--;
            if (lecteurs == 0) {
                peutEcrire.signal();
            }
        } finally {
            verrou.unlock();
        }
    }

    public void beginWrite() throws InterruptedException {
        String nom = Thread.currentThread().getName();
        verrou.lock();
        try {
            redacteursEnAttente++;
            try {
                while (ecriture || lecteurs > 0) {
                    System.out.println(nom + " (redacteur) attend : la ressource est utilisee");
                    peutEcrire.await();
                    System.out.println(nom + " (redacteur) se reveille");
                }
            } finally {
                redacteursEnAttente--;
            }
            ecriture = true;
        } finally {
            verrou.unlock();
        }
    }

    public void endWrite() {
        verrou.lock();
        try {
            ecriture = false;
            if (redacteursEnAttente > 0) {
                peutEcrire.signal();     // le redacteur suivant passe avant tous les lecteurs
            } else {
                peutLire.signalAll();
            }
        } finally {
            verrou.unlock();
        }
    }
}
```

### 4.3 Ordre FIFO garanti :

Les demandes qui ne peuvent pas entrer immédiatement sont mises dans une file d'attente, et chaque thread attend sur sa
propre Condition. La méthode admettre() fait entrer, dans l'ordre, les demandes de tête de file qui le peuvent : des lecteurs
consécutifs entrent ensemble, un rédacteur entre seul, et on ne dépasse jamais une demande qui attend.

`SharedRsc.java`
```java
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SharedRsc {

    private static class Demande {
        final boolean ecrivain;
        final Condition condition;
        boolean admise = false;

        Demande(boolean ecrivain, Condition condition) {
            this.ecrivain = ecrivain;
            this.condition = condition;
        }
    }

    private final ReentrantLock verrou = new ReentrantLock();
    private final Deque<Demande> file = new ArrayDeque<>();
    private int lecteurs = 0;
    private boolean ecriture = false;

    private void admettre() {
        while (!file.isEmpty()) {
            Demande d = file.peekFirst();
            if (d.ecrivain) {
                if (ecriture || lecteurs > 0) return;
                ecriture = true;
            } else {
                if (ecriture) return;
                lecteurs++;
            }
            file.pollFirst();
            d.admise = true;
            d.condition.signal();
        }
    }

    private void attendre(Demande d, String type) throws InterruptedException {
        String nom = Thread.currentThread().getName();
        System.out.println(nom + " (" + type + ") attend son tour");
        try {
            while (!d.admise) {
                d.condition.await();
            }
        } catch (InterruptedException e) {
            if (d.admise) {
                if (d.ecrivain) ecriture = false; else lecteurs--;
            } else {
                file.remove(d);
            }
            admettre();
            throw e;
        }
        System.out.println(nom + " (" + type + ") se reveille : c'est son tour");
    }

    public void beginRead() throws InterruptedException {
        verrou.lock();
        try {
            if (file.isEmpty() && !ecriture) {
                lecteurs++;
                return;
            }
            Demande d = new Demande(false, verrou.newCondition());
            file.addLast(d);
            attendre(d, "lecteur");
        } finally {
            verrou.unlock();
        }
    }

    public void endRead() {
        verrou.lock();
        try {
            lecteurs--;
            admettre();
        } finally {
            verrou.unlock();
        }
    }

    public void beginWrite() throws InterruptedException {
        verrou.lock();
        try {
            if (file.isEmpty() && !ecriture && lecteurs == 0) {
                ecriture = true;
                return;
            }
            Demande d = new Demande(true, verrou.newCondition());
            file.addLast(d);
            attendre(d, "redacteur");
        } finally {
            verrou.unlock();
        }
    }

    public void endWrite() {
        verrou.lock();
        try {
            ecriture = false;
            admettre();
        } finally {
            verrou.unlock();
        }
    }
}
```

















