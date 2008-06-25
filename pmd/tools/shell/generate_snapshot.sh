#!/bin/bash

if [ ! -e tools/shell/generate_snapshot.sh ]; then
  echo "Script must be run from top directory"
  exit 1
fi

if [ $# != 1 ]; then
  echo "<version> is required"
  exit 1
fi

version=$1

# update snapshot version

svn revert src/net/sourceforge/pmd/PMD.java
buildnumber=`svnversion .`
sed -i -e "s/VERSION = .*/VERSION = \"$version-SNAPSHOT-build-$buildnumber\";/" src/net/sourceforge/pmd/PMD.java

# call the release script

cd tools/shell
./generate_release.sh $version snapshot
cd ../..

# upload data
# adapted from docs.sh "upload"

remote_host=pmd.sourceforge.net
remote_dir_home=/home/groups/p/pm/pmd/htdocs/snapshot

echo "Uploading src and bin archives"

scp target/release/pmd-bin-${version}.zip ${remote_host}:${remote_dir_home}/files/pmd-bin-${version}-build-${buildnumber}.zip
scp target/release/pmd-src-${version}.zip ${remote_host}:${remote_dir_home}/files/pmd-src-${version}-build-${buildnumber}.zip

echo "Generating and uploading maven artifacts"

mvn -q -DskipTests source:jar javadoc:jar deploy
ant -f tools/ant/generate-jdk4-pom.xml
mvn -q deploy:deploy-file -Durl=scp://${remote_host}${remote_dir_home}/maven2 -DrepositoryId=pmd-snapshot-repo -Dfile=java14/lib/pmd14-${version}.jar -DpomFile=pmd-jdk14-pom.xml

echo "Uploading xdocs"

rsync -a -e ssh target/site/ ${remote_host}:${remote_dir_home}

