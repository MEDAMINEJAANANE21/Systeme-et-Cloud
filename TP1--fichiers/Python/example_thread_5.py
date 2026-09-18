#!/usr/bin/env python3

import os
import sys
import threading
import time

NB_THREADS = 100
SOLDE_INITIAL = 0
MONTANT = 20
NB_ITEMS = 10
NB_ITER = 100000


class MyClass:

    def __init__(self, val):
        self.tab = []
        self._lock = threading.Lock()
        for j in range(NB_ITEMS):
            self.tab.append(val)

    def incr(self, step):
        self._lock.acquire()
        for j in range(NB_ITEMS):
            self.tab[j] += step
        self._lock.release()

    def decr(self, step):
        self._lock.acquire()
        for j in range(NB_ITEMS):
            self.tab[j] -= step
        self._lock.release()

    def get_current(self):
        self._lock.acquire()
        return self.tab
        self._lock.release()

def thread_deposer(i, obj, m):
    for j in range(NB_ITER):
        obj.incr(m)


def thread_retirer(i, obj, m):
    for j in range(NB_ITER):
        obj.decr(m)


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
        d.start()
        r.start()

    for i in range(NB_THREADS):
        d = td.pop()
        r = tr.pop()
        d.join()
        r.join()

    print("etat final : {}".format(shared_obj.get_current()),)

    sys.exit(0)
