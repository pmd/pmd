#!/bin/bash
DOCS_FILE="docs.tar.gz"
currentVersion=5.0
option="${1}"

function tarball-docs() {
	cd target
	rm -f ${DOCS_FILE}
	tar zcf ${DOCS_FILE} site
    	echo "Starting secure copy to ${1}"
	scp ${DOCS_FILE} ${1}
	cd ../
}

if [ -z $option ]; then
	echo "Generating from pom, regenerating ruleset docs, and transforming"
	mvn clean site 
elif [ $option = "uploadcurrent" ]; then
	echo "Generating and uploading maven artifacts"
	mvn -q source:jar javadoc:jar deploy
	ant -f tools/ant/generate-jdk4-pom.xml
	mvn -q deploy:deploy-file -Durl=scp://pmd.sourceforge.net/home/groups/p/pm/pmd/htdocs/maven2 -DrepositoryId=pmd-repo -Dfile=java14/lib/pmd14-${currentVersion}.jar -DpomFile=target/pmd-jdk14-pom.xml
  	echo "Generating xdocs and uploading"
	mvn site 
	tarball-docs 'pmd.sourceforge.net:/home/groups/p/pm/pmd/htdocs/current/'
	ssh pmd.sourceforge.net "cd /home/groups/p/pm/pmd/htdocs/current/ && tar -zxf ${DOCS_FILE} && cp -R site/* . && rm -rf site/ && rm ${DOCS_FILE}"
elif [ $option = "upload" ]; then
	echo "Generating and uploading maven artifacts"
	mvn -q source:jar javadoc:jar deploy
	mkdir -p target
	ant -f tools/ant/generate-jdk4-pom.xml
	mvn -q deploy:deploy-file -Durl=scp://pmd.sourceforge.net/home/groups/p/pm/pmd/htdocs/maven2 -DrepositoryId=pmd-repo -Dfile=java14/lib/pmd14-${currentVersion}.jar -DpomFile=target/pmd-jdk14-pom.xml
  	echo "Uploading xdocs"
	tarball-docs 'pmd.sourceforge.net:/home/groups/p/pm/pmd/'
	ssh pmd.sourceforge.net "cd /home/groups/p/pm/pmd/ &&  rm -rf xref && rm -rf apidocs && ./update_docs.sh"
fi
