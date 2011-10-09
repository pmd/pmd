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

remote_user=$USER,pmd
remote_host=shell.sourceforge.net
remote_dir_home=/home/project-web/pmd/htdocs/snapshot

echo "Uploading src and bin archives"

mkdir target/release/${version}-build-${buildnumber}
mv target/release/pmd-bin-${version}.zip target/release/${version}-build-${buildnumber}/pmd-bin-${version}-build-${buildnumber}.zip
mv target/release/pmd-src-${version}.zip target/release/${version}-build-${buildnumber}/pmd-src-${version}-build-${buildnumber}.zip

# src and bin packages are available in hudson setup, no need to upload to sf.net
#scp -r target/release/${version}-build-${buildnumber} ${remote_host}:${remote_dir_home}/files

echo "Generating and uploading maven artifacts to sourceforge"

mvn -q -Psf-snapshot -DskipTests source:jar javadoc:jar deploy

echo "Generating and uploading site"

mvn -q -Psf-snapshot site site:deploy

echo "Cleaning up"

svn revert src/net/sourceforge/pmd/PMD.java

