#!/bin/bash
input="syscallj/src/main/java/com/syscallj/Bridge.java"
javac $input -h native/src/main/public/
rm `echo $input | cut -d'.' -f1`.class
