
### 2.2

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