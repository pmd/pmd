#!/bin/bash

if [ $# != 2 ]; then
  echo "Two arguments are required: <version> release|snapshot"
  exit 1
fi

if [ $2 = "release" ]; then
    buildtype="release"
else
  if [ $2 = "snapshot" ]; then
    buildtype="snapshot"
  else
    echo "unknown build type: $2"
    exit 1
  fi
fi

version=$1

pmd_top_dir=`pwd`/../../target/release
pmd_bin_dir=$pmd_top_dir/pmd-$version
pmd_src_dir=$pmd_top_dir/pmd-$version
pmd_tmp_dir=$pmd_top_dir/pmd-tmp

mkdir -p $pmd_top_dir

echo
echo "Rebuilding everything"
echo

cd ../..
ant dist

if [ buildtype = "release" ]; then
  echo
  echo "Press [enter] to generate docs"

  read RESP
fi

export MAVEN_OPTS="-Xmx512m -Xms192m"

mvn -q clean site

cd etc

echo
echo "generating binary file $pmd_top_dir/pmd-bin-$version.zip"
echo

rm -rf $pmd_bin_dir
rm -f $pmd_top_dir/pmd-bin-$version.zip
mkdir -p $pmd_bin_dir/etc
mkdir $pmd_bin_dir/bin
mkdir $pmd_bin_dir/lib
cp ../LICENSE.txt changelog.txt $pmd_bin_dir/etc
cd ../bin/
cp pmd.* cpd.sh cpdgui.bat designer.* $pmd_bin_dir/bin
cd ../etc/
cp ../lib/pmd-$version.jar ../lib/asm-3.2.jar ../lib/jaxen-1.1.1.jar ../lib/junit-4.4.jar $pmd_bin_dir/lib/
mkdir $pmd_bin_dir/etc/xslt
cp xslt/*.xslt xslt/*.js xslt/*.gif xslt/*.css $pmd_bin_dir/etc/xslt/
mkdir $pmd_bin_dir/docs
cp -R ../target/site/* $pmd_bin_dir/docs
cd $pmd_top_dir
zip -q -r pmd-bin-$version.zip pmd-$version/
cd -

echo
echo "binary package generated"
echo

if [ buildtype = "release" ]; then
  release_tag=`echo $version|sed -e "s/\./_/g"`

  echo
  echo
  echo "Type \"yes\" to tag svn repository using 'pmd_release_$release_tag'"

  read TAG_RESP;

  if [ "$TAG_RESP" = "yes" ]; then
	echo
	echo "Tagging release using"
	echo "svn copy -m \"$version release tag\" https://pmd.svn.sourceforge.net/svnroot/pmd/trunk/pmd  https://pmd.svn.sourceforge.net/svnroot/pmd/tags/pmd/pmd_release_$release_tag"
	echo
	svn copy -m "$version release tag" https://pmd.svn.sourceforge.net/svnroot/pmd/trunk/pmd  https://pmd.svn.sourceforge.net/svnroot/pmd/tags/pmd/pmd_release_$release_tag
  else
	echo
	echo "Skipping svn tag!!!"
	echo
  fi
fi



echo "generating source file $pmd_top_dir/pmd-src-$version.zip"

rm -rf $pmd_src_dir
rm -f $pmd_top_dir/pmd-src-$version.zip
cd ..
ant jarsrc
if [ "$TAG_RESP" = "yes" ]; then
	svn -q export https://pmd.svn.sourceforge.net/svnroot/pmd/tags/pmd/pmd_release_$release_tag $pmd_src_dir
else
	svn -q export https://pmd.svn.sourceforge.net/svnroot/pmd/trunk/pmd  $pmd_src_dir
fi
cp lib/pmd-src-$version.jar $pmd_src_dir/lib/
cp lib/pmd-$version.jar $pmd_src_dir/lib
mkdir $pmd_src_dir/docs
cp -R target/site/* $pmd_src_dir/docs
rm -f $pmd_src_dir/tools/config/clover2.license
cd $pmd_top_dir
zip -q -r pmd-src-$version.zip pmd-$version/
cd -

echo
echo "source package generated"
echo

if [ buildtype = "release" ]; then
  echo "Use the command below to upload to sourceforge"
  echo
  echo "rsync -avP -e ssh $pmd_top_dir/pmd-src-$version.zip $pmd_top_dir/pmd-bin-$version.zip xlv@frs.sourceforge.net:uploads/"
fi

