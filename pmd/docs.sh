#!/bin/bash

option="${1}"

if [ -z $option ]; then
	echo "Generating from pom, regenerating ruleset docs, and transforming"
	maven -qob xdoc:generate-from-pom pmd:ruleset-docs xdoc:transform 
elif [ $option = "all" ]; then
	echo "Running maven pmd-site"
	rm -rf target
	maven -qb pmd-site
elif [ $option = "upload" ]; then
	echo "Generating xdocs and uploading"
	maven -qob xdoc:generate-from-pom pmd:ruleset-docs xdoc:transform 
	DOCS_FILE=docs.tar.gz
	cp xdocs/cpdresults.txt xdocs/cpp_cpdresults.txt target/docs/
	cd target
	rm $DOCS_FILE
	tar zcf $DOCS_FILE docs/
	scp -i ~/.ssh/identity $DOCS_FILE tomcopeland@pmd.sourceforge.net:/home/groups/p/pm/pmd/
	cd ../
	ssh -l tomcopeland pmd.sourceforge.net "cd /home/groups/p/pm/pmd/ && rm -rf htdocs/xref && rm -rf htdocs/apidocs && ./update_docs.sh"
fi
if [ -e velocity.log ]; then
	rm velocity.log
fi
if [ -e maven.log ]; then
	rm maven.log
fi
