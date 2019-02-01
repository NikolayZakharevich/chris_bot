#!/bin/bash

cd ..

kill $(ps -e | grep "java" | awk '{print $1}')
rm chris_server_old.jar
mv chris_server.jar chris_server_old.jar
mv chris_server_new.jar chris_server.jar
java -jar chris_server.jar &
