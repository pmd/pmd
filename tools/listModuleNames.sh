#!/bin/bash
# lists automatic module names for all modules, using the built jars
for jar in $(find -name '*.jar'|grep target/pmd|egrep -v -- "-sources.jar|-javadoc.jar")
do
	project=$(echo $jar|sed 'sA.*target/AA;sX-[0-9].*XX')
	unzip  -q -o -d /tmp $jar META-INF/MANIFEST.MF
	modulename=$(grep Auto /tmp/META-INF/MANIFEST.MF|sed 's/.*://')
	rm -rf /tmp/META-INF
	echo '*' $project:$modulename
done
