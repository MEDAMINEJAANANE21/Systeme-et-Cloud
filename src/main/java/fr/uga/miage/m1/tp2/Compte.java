package fr.uga.miage.m1.tp2;

abstract class Compte {

    public abstract void deposer(double montant);

    public abstract void retirer(double montant);

    public abstract double consulter();
}
