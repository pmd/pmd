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

mkdir target/release/${version}-build-${buildnumber}
mv target/release/pmd-bin-${version}.zip target/release/${version}-build-${buildnumber}/pmd-bin-${version}-build-${buildnumber}.zip
mv target/release/pmd-src-${version}.zip target/release/${version}-build-${buildnumber}/pmd-src-${version}-build-${buildnumber}.zip
scp -r target/release/${version}-build-${buildnumber} ${remote_host}:${remote_dir_home}/files

echo "Generating and uploading maven artifacts"

mvn -q -DskipTests source:jar javadoc:jar deploy

echo "Uploading xdocs"

rsync -a -e ssh target/site/ ${remote_host}:${remote_dir_home}

echo "Setting permissions on uploaded files @ sf.net"

ssh ${remote_host} "chgrp -R pmd ${remote_dir_home}"
ssh ${remote_host} "chmod -R g+wX ${remote_dir_home}"

echo "Cleaning up"

svn revert src/net/sourceforge/pmd/PMD.java

