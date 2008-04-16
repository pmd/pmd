#!/bin/bash

option="${1}"

if [ -z $option ]; then
  echo "Generating from pom, regenerating ruleset docs, and transforming"
  ruby munge_rulesets.rb
  maven -qb pmd:rulesets-index xdoc:generate-from-pom 
  maven -qb pmd:ruleset-docs 
  rm -f rulesets/*.xml
  svn up rulesets
  maven -qb xdoc:transform 
elif [ $option = "all" ]; then
  echo "Running maven site"
  rm -rf target
  ruby munge_rulesets.rb
  maven -qb site
  rm -f rulesets/*.xml
  svn up rulesets
elif [ $option = "uploadcurrent" ]; then
  echo "Generating and uploading maven artifacts"
  mvn -q source:jar javadoc:jar deploy
  mvn -q deploy:deploy-file -Durl=scp://pmd.sourceforge.net/home/groups/p/pm/pmd/htdocs/maven2.beta -DrepositoryId=pmd-repo -Dfile=java14/lib/pmd14-5.0.jar -DpomFile=pmd-jdk14-pom.xml
  echo "Generating xdocs and uploading"
  ruby munge_rulesets.rb
  maven -qb pmd:rulesets-index xdoc:generate-from-pom 
  maven -qb pmd:ruleset-docs 
  rm -f rulesets/*.xml
  svn up rulesets
  maven -qb xdoc:transform 
  DOCS_FILE=docs.tar.gz
  cp xdocs/cpdresults.txt xdocs/cpp_cpdresults.txt target/docs/
  cd target
  rm -f $DOCS_FILE
  tar zcf $DOCS_FILE docs/
  scp $DOCS_FILE pmd.sourceforge.net:/home/groups/p/pm/pmd/htdocs/current/
  cd ../
  ssh pmd.sourceforge.net "cd /home/groups/p/pm/pmd/htdocs/current/ && tar -zxf docs.tar.gz && cp -R docs/* . && rm -rf docs && rm docs.tar.gz"
elif [ $option = "upload" ]; then
  echo "Generating and uploading maven artifacts"
  mvn -q source:jar javadoc:jar deploy
  mvn -q deploy:deploy-file -Durl=scp://pmd.sourceforge.net/home/groups/p/pm/pmd/htdocs/maven2.beta -DrepositoryId=pmd-repo -Dfile=java14/lib/pmd14-5.0.jar -DpomFile=pmd-jdk14-pom.xml
  echo "Uploading xdocs"
  DOCS_FILE=docs.tar.gz
  cp xdocs/cpdresults.txt xdocs/cpp_cpdresults.txt target/docs/
  cd target
  rm -f $DOCS_FILE
  tar zcf $DOCS_FILE docs/
    echo "Starting secure copy"
  scp $DOCS_FILE pmd.sourceforge.net:/home/groups/p/pm/pmd/
  cd ../
  ssh pmd.sourceforge.net "cd /home/groups/p/pm/pmd/ &&  rm -rf xref && rm -rf apidocs && ./update_docs.sh"
fi
if [ -e velocity.log ]; then
  rm velocity.log
fi
if [ -e maven.log ]; then
  rm maven.log
fi
