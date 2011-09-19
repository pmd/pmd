#!/bin/bash

update_VERSION_var() {
	sed -i -e "s/VERSION=.*/VERSION=$1/g" $2
}

update_regexp() {
	sed -i -e "s/$1/$2/g" $3
}

# update pmd-<version>.jar
update_jar() {
	update_regexp "pmd-[0-9][^; \\]*\.jar" "pmd-$1.jar" $2
}

update_dir() {
	update_regexp "pmd-[0-9][^ <;\/\\\\]*\/" "pmd-$1\/" $2
	update_regexp "pmd-[0-9][^ <;\/\\\\]*\\\\" "pmd-$1\\\\" $2
}

echo "Updating version to $1"

update_VERSION_var $1 bin/bgastviewer.sh
update_VERSION_var $1 bin/designer.bat
update_VERSION_var $1 bin/bgastviewer.bat
update_VERSION_var $1 bin/cpdgui.bat
update_VERSION_var $1 bin/pmd.bat
update_VERSION_var $1 java14/bin/bgastviewer.sh
update_VERSION_var $1 java14/bin/designer.bat
update_VERSION_var $1 java14/bin/bgastviewer.bat
update_VERSION_var $1 java14/bin/cpdgui.bat
update_VERSION_var $1 java14/bin/pmd.bat

update_regexp "property name=\\\"version\\\" value=\".*\"" "property name=\\\"version\\\" value=\\\"$1\\\"" bin/build.xml
update_regexp "VERSION = \".*\"" "VERSION = \\\"$1\\\"" src/net/sourceforge/pmd/PMD.java
update_regexp "pmd-src-.*.zip" "pmd-src-$1.zip" xdocs/compiling.xml
update_regexp "pmd-bin-[^x]*.zip" "pmd-bin-$1.zip" xdocs/installing.xml
update_regexp "pmd-[0-9].*" "pmd-$1" xdocs/installing.xml
update_regexp "PMD [0-9][^\"]*\"" "PMD $1\"" src/site/site.xml
update_regexp "PMD [0-9][^\"]*\"" "PMD $1\"" xdocs/navigation.xml
update_regexp "PMD version .* exists" "PMD version $1 exists" xdocs/integrations.xml

update_regexp "currentVersion.*" "currentVersion>$1<\/currentVersion>" project.xml
update_regexp "id>[0-9].*<" "id>$1<" project.xml
update_regexp "name>[0-9].*<" "name>$1<" project.xml
update_regexp "id>[0-9].*<" "id>$1<" project.xml
release_tag=`echo $1|sed -e "s/\./_/g"`
update_regexp "tag>pmd_release_[0-9].*<" "tag>pmd_release_$release_tag<" project.xml

update_regexp "^  <version>[0-9].*<" "  <version>$1<" pom.xml
update_regexp "^  <version>[0-9].*<" "  <version>$1<" pmd-jdk14-pom.xml
update_regexp "pmd14-[0-9\.]*.jar" "pmd14-$1.jar" docs.sh

update_jar $1 etc/cpd.jnlp
update_jar $1 xdocs/running.xml
update_jar $1 xdocs/integrations.xml

update_dir $1 xdocs/compiling.xml
update_dir $1 xdocs/integrations.xml
update_dir $1 xdocs/running.xml

date=`date +"%B %d, %Y"`
update_regexp "^????.*" "$date - $1:" etc/changelog.txt




