#!/bin/sh

nohup ./rmid_go.sh&
sleep 10
nohup ./httpd_go.sh&
nohup ./lookupd_go.sh&
nohup ./txnd_go.sh&
nohup ./javaspace_go.sh&
