#!/bin/bash

echo "Starting lookup service - reggie"
rm -rf /var/log/jini/reggie_log
java -Djava.security.policy=/home/tom/projects/jini/sdk/policy/policy.all -jar /home/tom/projects/jini/sdk/lib/reggie.jar http://mordor:8081/reggie-dl.jar /home/tom/projects/jini/sdk/policy/policy.all /var/log/jini/reggie_log public

