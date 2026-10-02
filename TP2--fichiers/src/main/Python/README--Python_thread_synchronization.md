# Quelques informations utiles pour la synchronisation de threads en Python 3:

## Notion essentielles

Les classes suivantes implémentent les notions essentielles vues en cours et en Java:

* `Lock` : verrou à exclusion mutuelle simple [[documentation](https://docs.python.org/3/library/threading.html#lock-objects)]
* `RLock` : verrou à exclusion mutuelle réentrant [[documentation](https://docs.python.org/3/library/threading.html#rlock-objects)]
* `Condition` : variable de condition (associée à un verrou) [[documentation](https://docs.python.org/3/library/threading.html#condition-objects)]
* `Semaphore` : sémaphore classique [[documentation](https://docs.python.org/3/library/threading.html#semaphore-objects)]

***

## Liens utiles:

* Documentation du module `threading` https://docs.python.org/3/library/threading.html


* Documentations concernant l'instruction `with` et les gestionnaires de contexte. Ces notions ne sont pas spécifiques à la synchronisation de threads mais sont souvent utilisées pour structurer du code Python et notamment gérer automatiquement la libération de certaines ressources (comme des verrous).
	* https://docs.python.org/3/reference/compound_stmts.html#with
	* https://docs.python.org/3/reference/datamodel.html#context-managers
	* https://docs.python.org/3/library/threading.html#using-locks-conditions-and-semaphores-in-the-with-statement
