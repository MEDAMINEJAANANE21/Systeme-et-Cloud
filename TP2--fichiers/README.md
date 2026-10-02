
### ExempleThread6

Le problème de l'exemple 6 est :

Immaginons qu'on ai 3 threads :

1. T1 : retire 30 du compte
2. T2 : retire 20 du compte
3. T3 : ajoute 50 au compte

Immaginon la séquence d'éxécution suivante :

1. T1 essaye de retirer 30 du compte : il se met en attente (wait)
2. T2 essaye de retirer 20 du compte : il se met en attente (wait)
3. T3 ajoute 50 au compte : le `notify` va réveiller un des deux threads en attente (T1 ou T2) supposon que se soit T1.
4. T1 va retirer 30 du compte, mais le compte est maintenant à 20 (50 - 30), donc T1 va se mettre en attente (wait) à nouveau.
5. T2 reste endormi.