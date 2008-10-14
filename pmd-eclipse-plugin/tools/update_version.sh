#!/bin/bash

#
# Update the version throughout the project to be <version>.vYYYYMMDDHHMM
#

update() {
	sed -i -e "s/$1/$2/g" $3
}

if [ $# != 1 ]; then
  echo "<version> is required"
  exit 1
fi

version=$1.v`date --utc +%Y%m%d%H%M`

echo
echo Updating version to $version
echo

update 'Bundle-Version: .*' "Bundle-Version: $version" plugins/net.sourceforge.pmd.eclipse.plugin/META-INF/MANIFEST.MF
update 'version=\".*\"$' "version=\"$version\"" features/net.sourceforge.pmd.eclipse/feature.xml
update 'version=\".*\"$' "version=\"$version\"" pmd-eclipse-test/plugin.xml
update 'buildId=.*' "buildId=$version" custom_build.properties

