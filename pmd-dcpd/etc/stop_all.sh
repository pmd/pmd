#!/bin/sh

rmid -stop
rm -rf log
ps -ef | grep javaspace | grep -v grep | cut -c9-15 | xargs kill -9
ps -ef | grep httpd_go | grep -v grep | cut -c9-15 | xargs kill -9
ps -ef -U tom | grep java | grep -v grep | cut -c9-15 | xargs kill -9
 
