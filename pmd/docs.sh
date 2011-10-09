#!/bin/bash
DOCS_FILE="docs.tar.gz"
currentVersion=5.0
option="${1}"
sourceforge_server="shell.sourceforge.net"
sf_pmd_basepath="/home/project-web/pmd"

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
	echo "Using maven to upload snapshots - please make sure your settings.xml has all the required credentials"
	echo "  Note: you might need to add this to the maven commandline:  -Dmaven.test.failure.ignore=true"
	echo "Generating and uploading maven artifacts to snapshots area at sonatype"
	mvn -q clean source:jar javadoc:jar deploy
	echo "Generating and uploading maven artifacts to sourceforge"
	mvn -q -Psf-snapshot clean source:jar javadoc:jar deploy
	echo "Generating xdocs and uploading"
	mvn -q -Psf-snapshot site site:deploy
elif [ $option = "upload" ]; then
	echo "Using maven to upload the release - please make sure your settings.xml has all the required credentials"
	echo "Generating and uploading maven artifacts to release staging area at sonatype"
	mvn -q clean source:jar javadoc:jar deploy
	echo "Generating and uploading maven artifacts to sourceforge"
	mvn -q -Psf-release clean source:jar javadoc:jar deploy
	echo "Generating xdocs and uploading"
	mvn -q -Psf-release site site:deploy
fi
