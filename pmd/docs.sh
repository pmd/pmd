#!/bin/bash

option="${1}"

if [ -z $option ]; then
	echo "Generating from pom and transforming xdocs"
	maven -f maven-build.xml -b xdoc:generate-from-pom xdoc:transform
elif [ $option = "all" ]; then
	echo "Running maven pmd-site"
	rm -rf target
	maven -f maven-build.xml pmd-site
elif [ $option = "upload" ]; then
	echo "Generating xdocs and uploading"
	maven -f maven-build.xml -b xdoc:transform
	DOCS_FILE=docs.tar.gz
	cp xdocs/cpdresults.txt xdocs/cpp_cpdresults.txt target/docs/
	cd target
	rm $DOCS_FILE
	tar zcf $DOCS_FILE docs/
	scp -i ~/.ssh/identity $DOCS_FILE tomcopeland@pmd.sourceforge.net:/home/groups/p/pm/pmd/
	cd ../
	ssh -l tomcopeland pmd.sourceforge.net "cd /home/groups/p/pm/pmd/ && rm -rf htdocs/xref && rm -rf htdocs/apidocs && ./update_docs.sh"
	rm velocity.log maven.log
fi

