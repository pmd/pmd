#!/bin/bash

usage() {
    echo "$(basename ${0}) [-v version-number] [-d] [-s]"
    echo ""
    echo "-v override the release's version number provided in pom.xml"
    echo "-d no docs generation"
    echo "-s no SVN tags"
    echo ""
    echo "This script MUST BE executed from the 'etc' folder of the PMD projet."
}

check_dependency() {
    local binary="${1}"

    which "${binary}" > /dev/null
    local status="${?}"
    if [ ${status} -ne 0 ]; then
        echo "missing dependency:${binary}"
        exit ${status}
    fi
}

while getopts v:dsh OPT; do
    case "$OPT" in
	    h)
            usage
            exit 0
            ;;
        v)
            readonly version=${OPTARG}
            ;;
        d)
            readonly no_docs="true"
            ;;
        s)
            readonly no_tags="true"
            ;;
	    *)
	        echo "Unrecognized options:${OPTARG}"
	        exit 1;
	        ;;
    esac
done

if [ -z ${version} ]; then
    check_dependency "xsltproc"
    readonly version=$(xsltproc extract_release_number.xslt ../pom.xml  | grep VERSION | cut -f2 -d: | sed -e 's/-SNAPSHOT//')
fi

check_dependency "ant"
check_dependency "maven"
check_dependency "mvn"

current_dir=$(pwd | sed -e 's/^.*\///')
if [ "${current_dir}" -ne "etc" ], then
    echo "release script MUST be executed from the 'etc' folder"
    exit 3
fi

echo "building release version ${version}"

pmd_top_dir="$(mktemp -d)"
echo "working directory is:${pmd_top_dir}"
pmd_bin_dir="${pmd_top_dir}/pmd-${version}"
pmd_src_dir="${pmd_top_dir}/pmd-${version}"
pmd_tmp_dir="${pmd_top_dir}/pmd-tmp"

echo
echo "Rebuilding everything"
echo

set -e

cd ..
ant -f bin/build.xml dist
status="${?}"
if [ ${status} -ne 0 ]; then
    echo "build failed - aborting release"
    exit 2
fi
cd etc

if [ -z ${no_docs} ]; then
    cd ../
    ./docs.sh all
    cd etc
else
    echo "no documentation generation"
fi

echo "generating binary file ${pmd_top_dir}/pmd-bin-${version}.zip"

rm -rf "${pmd_bin_dir}"
rm -f "${pmd_top_dir}/pmd-bin-${version}.zip"
mkdir -p "${pmd_bin_dir}/etc"
mkdir "${pmd_bin_dir}/bin"
mkdir "${pmd_bin_dir}/lib"
cp ../LICENSE.txt changelog.txt "${pmd_bin_dir}/etc"
cd ../bin/
cp pmd.* build.xml cpd.sh cpdgui.bat designer.* "${pmd_bin_dir}/bin"
cd ../etc/
cp ../lib/pmd-$version.jar ../lib/asm-3.2.jar ../lib/jaxen-1.1.1.jar ../lib/junit-4.4.jar "${pmd_bin_dir}/lib/"
mkdir "${pmd_bin_dir}/etc/xslt"
cp xslt/*.xslt xslt/*.js xslt/*.gif xslt/*.css "${pmd_bin_dir}/etc/xslt/"
cp -R ../target/docs "${pmd_bin_dir}"
cd "${pmd_top_dir}"
zip -q -r "pmd-bin-${version}.zip pmd-${version}/"
cd -

echo "binary package generated"

release_tag=$(echo ${version} | sed -e 's/\./_/g' )

if [ -z ${no_tags} ]; then
    echo "tagging svn repository using 'pmd_release_${release_tag}'"
	echo "svn copy -m \"$version release tag\" https://pmd.svn.sourceforge.net/svnroot/pmd/branches/pmd/4.2.x https://pmd.svn.sourceforge.net/svnroot/pmd/tags/pmd/pmd_release_$release_tag"
	svn copy -m "${version} release tag" https://pmd.svn.sourceforge.net/svnroot/pmd/branches/pmd/4.2.x https://pmd.svn.sourceforge.net/svnroot/pmd/tags/pmd/pmd_release_$release_tag
else
	echo "Skipping svn tag!!!"
fi

echo "generating source file ${pmd_top_dir}/pmd-src-${version}.zip"

rm -rf "${pmd_src_dir}"
rm -f "${pmd_top_dir}/pmd-src-${version}.zip"
cd ../bin/
ant jarsrc
cd ..
svn -q export "https://pmd.svn.sourceforge.net/svnroot/pmd/tags/pmd/pmd_release_${release_tag}" "${pmd_src_dir}"
cp "lib/pmd-src-${version}.jar" "${pmd_src_dir}/lib/"
cp "lib/pmd-${version}.jar" "${pmd_src_dir}/lib"
cp -R target/docs "${pmd_src_dir}"
rm -f "${pmd_src_dir}/etc/clover.license"
cd "${pmd_top_dir}"
zip -q -r "pmd-src-${version}.zip" "pmd-${version}/"
cd -

echo "source package generated"

echo "Use the command below to upload to sourceforge"
echo "rsync -avP -e ssh ${pmd_top_dir}/pmd-src-${version}.zip ${pmd_top_dir}/pmd-bin-${version}.zip xlv@frs.sourceforge.net:uploads/"
