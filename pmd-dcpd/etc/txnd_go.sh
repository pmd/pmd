#!/bin/bash

echo "Starting txn manager - mahalo"
rm -rf /var/log/jini/mahalo_log
java -Djava.security.policy=/home/tom/projects/jini/sdk/policy/policy.all -Dcom.sun.jini.mahalo.managerName=TransactionManager -jar /home/tom/projects/jini/sdk/lib/mahalo.jar http://mordor:8081/mahalo.jar /home/tom/projects/jini/sdk/policy/policy.all /var/log/jini/mahalo_log public

