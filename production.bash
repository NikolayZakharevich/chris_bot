#!/bin/bash

mvn clean compile assembly:single
scp target/chris-1.0-SNAPSHOT-jar-with-dependencies.jar root@157.230.232.210:/chris_server_new.jar

ssh root@157.230.232.210 'bash -s && exit' < run_new_chris.bash
