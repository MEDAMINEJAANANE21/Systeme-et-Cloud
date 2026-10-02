#!/usr/bin/env python3

import os
import sys
import threading
import time
import random

NB_THREADS = 100
SOLDE_INITIAL = 0
MONTANT = 20


class MyClass:

    def __init__(self, val):
        self.solde = SOLDE_INITIAL
        self._lock = threading.Lock()
        self._cond = threading.Condition(lock=self._lock)

    def deposer(self, montant):
        self._lock.acquire()
        print("{}: DEBUT deposer {}".format(
            threading.currentThread().name, montant))
        self.solde += montant
        self._cond.notify()
        print("{}: |---- deposer (solde: {})".format(threading.currentThread().name, self.solde))
        self._lock.release()

    def retirer(self, montant):
        self._lock.acquire()
        print("{}: DEBUT retirer {}".format(
            threading.currentThread().name, montant))
        if (self.solde < montant):
            self._cond.wait()
        self.solde -= montant
        assert self.solde >= 0, 'solde negatif'
        print(
            "{}: |---- retirer (solde: {})".format(threading.currentThread().name, self.solde))

        self._lock.release()

    def consulter(self):
        self._lock.acquire()
        return self.solde
        self._lock.release()


def thread_deposer(i, obj, m):
    sleep_time = random.randint(0, 4)
    time.sleep(sleep_time)
    obj.deposer(m)


def thread_retirer(i, obj, m):
    sleep_time = random.randint(0, 4)
    time.sleep(sleep_time)
    obj.retirer(m)


if __name__ == "__main__":

    shared_obj = MyClass(SOLDE_INITIAL)

    td = list()
    tr = list()

    for i in range(NB_THREADS):
        d = threading.Thread(target=thread_deposer,
                             args=(i, shared_obj, MONTANT))
        r = threading.Thread(target=thread_retirer,
                             args=(i, shared_obj, MONTANT))
        td.append(d)
        tr.append(r)
        d.name = "td"+str(i)
        r.name = "tr"+str(i)
        r.start()
        d.start()

    for i in range(NB_THREADS):
        d = td.pop()
        r = tr.pop()
        d.join()
        r.join()

    print("etat final : {}".format(shared_obj.consulter()),)

    sys.exit(0)
