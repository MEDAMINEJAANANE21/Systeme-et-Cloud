#!/usr/bin/env python3

import sys
import os
import threading

MAX = 1000


def thread_function(text):
    for i in range(MAX):
        print("{} {}".format(i, text), end=" ", flush=True)


if __name__ == "__main__":
    t1 = threading.Thread(target=thread_function, args=("Hello",))
    t2 = threading.Thread(target=thread_function, args=("World",))
    t3 = threading.Thread(target=thread_function, args=("and Everybody",))

    t1.start()
    t2.start()
    t3.start()
