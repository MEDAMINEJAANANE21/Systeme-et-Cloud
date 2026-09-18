#!/bin/bash

for ((i=1; i<10;i++)) 
do
echo -e "\n***** iteration $i *****\n"; java $1
done