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
        for j in range(NB_ITEMS):
            self.tab.append(val)

    def incr(self, step):
        for j in range(NB_ITEMS):
            self.tab[j] += step

    def decr(self, step):
        for j in range(NB_ITEMS):
            self.tab[j] -= step

    def get_current(self):
        return self.tab


def thread_deposer(i, obj, m):
    for j in range(NB_ITER):
        #print("D{} : avant depot s={}".format(i, obj.get_current()))
        obj.incr(m)
        #print("D{} : apres depot s={}".format(i, obj.get_current()))


def thread_retirer(i, obj, m):
    for j in range(NB_ITER):
        #print("R{} : avant retrait s={}".format(i, obj.get_current()))
        obj.decr(m)
        #print("R{} : apres retrait s={}".format(i, obj.get_current()))


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
