# Rendue TP2 - Système & Cloud

##### Mohamed-Amine JAANANE - Omar BARKOK | 02-10-2026

##### [Repo GitHub de notre solution du TP1](https://github.com/MEDAMINEJAANANE21/Systeme-et-Cloud/tree/main)

---

### 3.1 `ExempleThread6.java`  

Non après plusieurs exécutions je n'ai jamais obtenu de solde négatif, cela s'explique par le faite que les opérations de
retrait son faites par un thread à la fois et uniquement si le solde est >= la valeur à retirer.
Mais on peut tomber sur un autre problème :

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

### 3.2 `ExempleThread7.java`  
