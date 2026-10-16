# Rendue TP3 - Synchronisation de Threads

##### Mohamed-Amine JAANANE - Omar BARKOK | 02-10-2026 | Système & Cloud

---

### 1 - Erreur IllegalMonitorStateException

Les méthodes `deposer` et `retirer` de `CompteVersion3` ne sont pas `synchronized`, alors qu'elles appellent `notify()` et wait().
Or ces méthodes ne peuvent être appelées que par un thread qui possède le moniteur de l'objet (c’est à dire un seul
thread à la fois) sinon la machine virtuelle lève une IllegalMonitorStateException. Cette exception hérite de
RuntimeException : elle n'est pas contrôlée, donc le code compile sans erreur et le problème n'apparaît qu'à l'exécution du code.

### 2.1 - Le problème des lecteurs-rédacteurs :


### 2.2 - Le problème des lecteurs-rédacteurs :


### 3.1 - Tampon producteurs-consommateurs avec Lock et Condition :

### 3.2 - Lecteurs-rédacteurs avec Lock et Condition :

### 4 - Compléments sur les lecteurs-rédacteurs :

### 4.1 -  Priorité aux lecteurs :

### 4.2 - Priorité aux rédacteurs : 

### 4.3 Ordre FIFO garanti : 



















