#!/bin/bash

option="${1}"

if [ -z $option ]; then
  echo "Generating from pom, regenerating ruleset docs, and transforming"
  `./munge_rulesets.rb`
  maven -qb pmd:rulesets-index xdoc:generate-from-pom 
  maven -qb pmd:ruleset-docs 
  rm -f rulesets/*.xml
  cvs -q up rulesets
  maven -qb xdoc:transform 
elif [ $option = "all" ]; then
  echo "Running maven site"
  rm -rf target
  `./munge_rulesets.rb`
  maven -qb site
  rm -f rulesets/*.xml
  cvs -q up rulesets
elif [ $option = "uploadcurrent" ]; then
  echo "Generating xdocs and uploading"
  `./munge_rulesets.rb`
  maven -qb pmd:rulesets-index xdoc:generate-from-pom 
  maven -qb pmd:ruleset-docs 
  rm -f rulesets/*.xml
  cvs -q up rulesets
  maven -qb xdoc:transform 
  DOCS_FILE=docs.tar.gz
  cp xdocs/cpdresults.txt xdocs/cpp_cpdresults.txt target/docs/
  cd target
  rm -f $DOCS_FILE
  tar zcf $DOCS_FILE docs/
  scp -i ~/.ssh/identity $DOCS_FILE tomcopeland@pmd.sourceforge.net:/home/groups/p/pm/pmd/htdocs/current/
  cd ../
  ssh -l tomcopeland pmd.sourceforge.net "cd /home/groups/p/pm/pmd/htdocs/current/ && tar -zxf docs.tar.gz && cp -R docs/* . && rm -rf docs && rm docs.tar.gz"
elif [ $option = "upload" ]; then
  echo "Uploading xdocs"
  DOCS_FILE=docs.tar.gz
  cp xdocs/cpdresults.txt xdocs/cpp_cpdresults.txt target/docs/
  cd target
  rm -f $DOCS_FILE
  tar zcf $DOCS_FILE docs/
    echo "Starting secure copy"
  scp -i ~/.ssh/identity $DOCS_FILE tomcopeland@pmd.sourceforge.net:/home/groups/p/pm/pmd/
  cd ../
  ssh -l tomcopeland pmd.sourceforge.net "cd /home/groups/p/pm/pmd/ &&  rm -rf xref && rm -rf apidocs && ./update_docs.sh"
fi
if [ -e velocity.log ]; then
  rm velocity.log
fi
if [ -e maven.log ]; then
  rm maven.log
fi
