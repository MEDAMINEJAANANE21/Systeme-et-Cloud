#!/usr/bin/env python3

import os
import sys
import threading
import time


def thread_function(text, sleep_time):
    time.sleep(sleep_time)
    print("{}".format(text), end=" ", flush=True)


if __name__ == "__main__":
    t1 = threading.Thread(target=thread_function, args=("Hello", 5,))
    t2 = threading.Thread(target=thread_function, args=("World", 2,))
    t3 = threading.Thread(target=thread_function, args=("and Everybody", 4,))

    t1.start()
    t2.start()
    t3.start()

    sys.exit(0)
