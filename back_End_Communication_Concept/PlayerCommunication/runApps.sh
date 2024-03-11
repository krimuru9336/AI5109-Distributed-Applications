#!/bin/bash

# Compile Java files
javac src/main/java/com/org/_360T/InitiatorPlayer.java
javac src/main/java/com/org/_360T/NormalPlayer.java

# Run NormalPlayer in the background
java -cp src/main/java com.org._360T.NormalPlayer &

# Give some time for NormalPlayer to start
sleep 2

# Run InitiatorPlayer
java -cp src/main/java com.org._360T.InitiatorPlayer

# Cleanup: Kill the background NormalPlayer process
pkill -f "java -cp src/main/java com.org._360T.NormalPlayer"
