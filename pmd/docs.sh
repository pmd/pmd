#!/bin/bash

restore_from_repository() {
    local dir="${1}"

    git status "${dir}"
    local status="${?}"
    if [ ${status} -eq 0 ]; then
        git reset HEAD "${dir}"
        git checkout "${dir}"
    else
        svn up "${dir}"
    fi
}

option="${1}"

if [ -z $option ]; then
  echo "Generating from pom, regenerating ruleset docs, and transforming"
  ruby munge_rulesets.rb
  maven -qb pmd:rulesets-index xdoc:generate-from-pom
  maven -qb pmd:ruleset-docs
  rm -f rulesets/*.xml
  restore_from_repository rulesets
  maven -qb xdoc:transform
elif [ $option = "all" ]; then
  echo "Running maven site"
  rm -rf target
  ruby munge_rulesets.rb
  maven -qb site
  rm -f rulesets/*.xml
  restore_from_repository rulesets
  maven -qb xdoc:transform
elif [ $option = "uploadcurrent" ]; then
  echo "Generating and uploading maven artifacts"
  mvn -q source:jar javadoc:jar deploy
  mvn -q deploy:deploy-file -Durl=scp://shell.sourceforge.net/home/groups/p/pm/pmd/htdocs/maven2 -DrepositoryId=pmd-repo -Dfile=java14/lib/pmd14-4.2.6.jar -DpomFile=pmd-jdk14-pom.xml
  echo "Generating xdocs and uploading"
  ruby munge_rulesets.rb
  maven -qb pmd:rulesets-index xdoc:generate-from-pom
  maven -qb pmd:ruleset-docs
  rm -f rulesets/*.xml
  restore_from_repository rulesets
  maven -qb xdoc:transform
  DOCS_FILE=docs.tar.gz
  cp xdocs/cpdresults.txt xdocs/cpp_cpdresults.txt target/docs/
  cd target/docs
  rsync -a -e ssh * $USER,pmd@web.sourceforge.net:/home/groups/p/pm/pmd/htdocs
  cd ../..
elif [ $option = "upload" ]; then
  echo "Generating and uploading maven artifacts"
  ssh $USER,pmd@shell.sf.net create
  mvn -q source:jar javadoc:jar deploy
  echo "Uploading xdocs"
  DOCS_FILE=docs.tar.gz
  cp xdocs/cpdresults.txt xdocs/cpp_cpdresults.txt target/docs/
  cd target/docs
  rsync -aRvz -e ssh * "${USER}@web.sourceforge.net:/home/project-web/pmd/htdocs"
  cd ../..
fi
if [ -e velocity.log ]; then
  rm velocity.log
fi
if [ -e maven.log ]; then
  rm maven.log
fi
