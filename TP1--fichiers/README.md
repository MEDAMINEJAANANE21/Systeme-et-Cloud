# Rendue TP1 - Système & Cloud

##### Mohamed-Amine JAANANE - Omar BARKOK | 18-09-2026

##### [Repo GitHub de notre solution du TP1](https://github.com/MEDAMINEJAANANE21/Systeme-et-Cloud/tree/9d1e08edc97f78dd87597adf89e355b74adee567/TP1--fichiers)

---

### 2.2 `ExempleThread1`

> Que pouvez vous dire à propos de l’ordre d’aﬃchage? Expliquer. Que pouvez
vous dire à propos du nombre d’aﬃchages? Expliquer.

On remarque qu'on n'a pas deux exécutions qui donnent le même résultat.
Ça s'explique du fait que c'est l'ordonnenceur qui choisi l'ordre des exécutions des threads.

On remarque aussi qu'on n'a pas toujours le même nombre d'affichages.
Ça s'explique du fait que dans l'exemple on a 4 threads au total : 

1. Le «père» qui est le thread exécutant le main.
2. Et les 3 threads créé par père.

Sauf qu'après que le père a crée ses fils il tue le processus courant directement (`exit(0)`) ce qui entraine la mort
de tous les autres threads du processus. Et donc des fois les fils on le temps de s'éxécuter avant leur mort.

En utilisant l'interface `Runnable` on ne remarque pas de différence.

### 2.3 `ExempleThread2`

> Voyez vous tous les aﬃchages? Essayez de diminuer et d’augmenter les valeurs d’attente (les arguments passés lors de
> la création des threads qui sont utilisés pour les appels à sleep). Si vous mettez de trop grandes valeurs,
> vous risquez de ne plus voir aucun aﬃchage. Pourquoi?

Non, on ne voit pas tous les affichages.

En diminuant la valeur d'attente même à `1` le thread main se termine trop rapidement.

Si on met de trop grandes valeurs pour le sleep d'un thread ce dernier met trop de temps à afficher le message que le
père l'a déjà tué.

### 2.4 `ExempleThread3`

> Étudier ensuite le code et le comportement des programmes ExempleThread3bis ExempleThread3ter.
> Expliquer les diﬀérences observées à l’aide de la documentation Java. 

Un thread attend ses thread fils dans deux cas : 

1. En utilisant `.join()`.
2. S'il n'y a pas de `exit()` dans le thread père.

Sinon ce thread se termine et tout ses threads fils non daemon se terminent aussi.

Donc la différence entre `ExempleThread3bis` et `ExempleThread3ter` :

- `ExempleThread3ter` :
    - Le processus qui contient les 4 threads attends la terminaison de ses threads car ils ne sont pas daemon et
      il n'y a ni de `exit()` ni de `join()`.

- `ExempleThread3bis` :
    - Le processus qui contient les 4 threads n'attends pas ses threads fils car ils sont des `daemon`.

Voici un extrait de la documentation de java sur les Thread (des fils d'exécution dans un processus) : [Class Thread](https://docs.oracle.com/javase/8/docs/api/java/lang/Thread.html#:~:text=When%20a%20Java,the%20run%20method.)

> When a Java Virtual Machine starts up, there is usually a single non-daemon thread (which typically calls the method named main of some designated class). The Java Virtual Machine continues to execute threads until either of the following occurs:
>
> - The exit method of class Runtime has been called and the security manager has permitted the exit operation to take place.
> - All threads that are not daemon threads have died, either by returning from the call to the run method or by throwing an exception that propagates beyond the run method.

### 2.5 `ExempleThread4`

En éxécutant plusieurs fois le programme, il y a plusieurs cas où le résultat est différent, cela s'explique du fait que 
le tableau est partagé par plusieurs threads qui font des opérations de lecture et écriture en concurrence, et donc sans 
système de syncronization on tombe sur des cas de «**perte de mise à jour**» où un thread écrit une valeur et un autre
l'écrase tout de suite après.

### 2.6 `ExempleThread5`

En comparant les deux version, on remarque l'ajout du mot clès `syncronized` dans le prototype des méthodes `synchronized void incTab()` et `synchronized void decTab()`
ce qui permet de «privatiser» l'accès à ces méthodes à 1 seul thread à la fois.

## 3 TP1 deuxième partie : lien avec le système d’exploitation

### 3.1 Chronométrage

> Est-il possible d’avoir `usr + sys > real` ? Si oui, comment peut-on l’expliquer ? Sinon pourquoi?

Oui, il est possible que `usr + sys > real` pour comprendre pourquoi cela est possible on a cherché la définition de chaque valeur :

- **`usr`** : temps CPU consommé par le processus pour exécuter le code en mode utilisateur (calculs, boucles, logique du programme).
- **`sys`** : temps CPU consommé par le noyau du système d'exploitation pour le compte du processus (appels système, gestion mémoire, entrées/sorties, création de threads, etc.).
- **`real`** : temps horloge total écoulé (*wall-clock time*) entre le lancement et la fin de la commande, incluant le calcul, l'attente et la concurrence avec d'autres processus.

> Définitions extraites depuis *Perplexitiy* : [prompt chat](https://www.perplexity.ai/search/db817c33-bf1f-4979-b795-f3289e498e79)

#### Les deux cas de `real`

- **`real` > `usr + sys`** : le programme passe du temps à attendre (disque, réseau, verrou concurrent, autre processus) sans consommer de CPU pendant cette attente.
- **`real` < `usr + sys`** : le programme exploite plusieurs cœurs CPU en parallèle (multithreading), donc le temps CPU cumulé dépasse le temps horloge réel.

### 3.2 Niveau d’implémentation des threads

Voici l'implémentation demandé : [SleepingThread](Java/SleepingThread.java)

```java
import java.util.concurrent.TimeUnit;

public class SleepingThread extends Thread {
    public static final int TWOMINUTES = 120000;

    public SleepingThread() {
    }

    @Override
    public void run() {
        try {
            System.out.println("I am sleeping zzz...");
            TimeUnit.MILLISECONDS.sleep(TWOMINUTES);
        } catch (InterruptedException e) {
            UnexpectedSituation.exit("interrupted sleep in thread "+ Thread.currentThread().getName(), e);
        }
    }

    public static void main(String[] argv) {
        if (argv.length < 1) {
            System.err.println("Il faut un nombre de threads en argument !");
            System.exit(1);
        }

        final int nbThreads = Integer.parseInt(argv[0]);
        SleepingThread[] sleepingThreads = new SleepingThread[nbThreads];

        for (int i = 0; i < nbThreads; i++) {
            sleepingThreads[i] = new SleepingThread();
            sleepingThreads[i].start();
        }

        System.out.println("I have started all my children :p");

        for (int i = 0; i < nbThreads; i++) {
            try {
                sleepingThreads[i].join();
            } catch (InterruptedException e) {
                UnexpectedSituation.exit("interrupted join in thread "+ Thread.currentThread().getName(), e);
            }
        }

        System.exit(0);
    }
}
```

> En déduire le type d’implémentation utilisé par la machine virtuelle Java
> pour la gestion des threads de l’application (threads “utilisateur” ou threads “noyau”).

Pour l'instant on n'a pas réussi à bien comprendre le type d'implémentation utilisé par la JVM pour la gestion des threads.