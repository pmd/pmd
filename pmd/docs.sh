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
  mvn -q deploy:deploy-file -Durl=scp://web.sourceforge.net/home/groups/p/pm/pmd/htdocs/maven2 -DrepositoryId=pmd-repo -Dfile=java14/lib/pmd14-4.2.5.jar -DpomFile=pmd-jdk14-pom.xml
  echo "Generating xdocs and uploading"
  ruby munge_rulesets.rb
  maven -qb pmd:rulesets-index xdoc:generate-from-pom 
  maven -qb pmd:ruleset-docs 
  rm -f rulesets/*.xml
  svn up rulesets
  maven -qb xdoc:transform 
  DOCS_FILE=docs.tar.gz
  cp xdocs/cpdresults.txt xdocs/cpp_cpdresults.txt target/docs/
  cd target/docs
  rsync -a -e ssh * $USER,pmd@web.sourceforge.net:/home/groups/p/pm/pmd/htdocs
  cd ../..
elif [ $option = "upload" ]; then
  echo "Generating and uploading maven artifacts"
  mvn -q source:jar javadoc:jar deploy
  mvn -q deploy:deploy-file -Durl=scp://web.sourceforge.net/home/groups/p/pm/pmd/htdocs/maven2 -DrepositoryId=pmd-repo -Dfile=java14/lib/pmd14-4.2.5.jar -DpomFile=pmd-jdk14-pom.xml
  echo "Uploading xdocs"
  DOCS_FILE=docs.tar.gz
  cp xdocs/cpdresults.txt xdocs/cpp_cpdresults.txt target/docs/
  cd target/docs
  rsync -a -e ssh * $USER,pmd@web.sourceforge.net:/home/groups/p/pm/pmd/htdocs
  cd ../..
fi
if [ -e velocity.log ]; then
  rm velocity.log
fi
if [ -e maven.log ]; then
  rm maven.log
fi
