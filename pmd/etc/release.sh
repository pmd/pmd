#!/bin/bash

usage() {
    echo "$(basename ${0}) [-v version-number] [-d] [-s] [-S]"
    echo ""
    echo "-v override the release's version number provided in pom.xml"
    echo "-d no docs generation"
    echo "-s no SVN tags"
    echo "-S generate a snapshot"
    echo ""
    echo "This script MUST BE executed from the 'etc' folder of the PMD projet."
}

check_dependency() {
    local binary="${1}"

    which "${binary}" > /dev/null
    local status="${?}"
    if [ ${status} -ne 0 ]; then
        echo "Missing dependency:${binary}"
        exit ${status}
    fi
}

make_tree_structure() {
    local root_dir="${1}"

    mkdir -p "${root_dir}/etc"
    mkdir -p "${root_dir}/bin"
    mkdir -p "${root_dir}/lib"
}

while getopts v:dSsh OPT; do
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
        S)
            readonly snapshot="true"
            ;;
	    *)
	        echo "Unrecognized options:${OPTARG}"
	        exit 1;
	        ;;
    esac
done

current_dir=$(pwd | sed -e 's/^.*\///')
if [ "${current_dir}" != "etc" ]; then
    echo "Release script MUST be executed from the 'etc' folder"
    exit 3
fi

if [ -z ${version} ]; then
    check_dependency "xsltproc"
    readonly version=$(xsltproc extract_release_number.xslt ../pom.xml  | grep VERSION | cut -f2 -d: | sed -e 's/-SNAPSHOT//')
fi

echo "Building release version ${version}"

pmd_top_dir="$(mktemp -d)"
echo "Working directory is:${pmd_top_dir}"
pmd_bin_dir="${pmd_top_dir}/pmd-bin-${version}"
pmd_src_dir="${pmd_top_dir}/pmd-src-${version}"

echo "Rebuilding everything"

set -e

cd ..
ant -f bin/build.xml dist
status="${?}"
if [ ${status} -ne 0 ]; then
    echo "Build failed - aborting release"
    exit 2
fi
cd etc

if [ -z ${no_docs} ]; then
    cd ../
    ./docs.sh all
    cd etc
else
    echo "No documentation generation"
fi

echo "Generating binary file ${pmd_top_dir}/pmd-bin-${version}.zip"

make_tree_structure "${pmd_bin_dir}"
cp ../LICENSE.txt changelog.txt "${pmd_bin_dir}/etc"
cd ../bin/
cp pmd.* build.xml cpd.sh cpdgui.bat designer.* "${pmd_bin_dir}/bin"
cd ../etc/
cp ../lib/pmd-$version.jar ../lib/asm-3.2.jar ../lib/jaxen-1.1.1.jar ../lib/junit-4.4.jar "${pmd_bin_dir}/lib/"
mkdir "${pmd_bin_dir}/etc/xslt"
cp xslt/*.xslt xslt/*.js xslt/*.gif xslt/*.css "${pmd_bin_dir}/etc/xslt/"
if [ -z ${no_docs} ] ; then
    cp -R ../target/docs "${pmd_bin_dir}"
fi
cd "${pmd_top_dir}"
zip -q -r "pmd-bin-${version}.zip" "pmd-bin-${version}"
cd -

echo "Binary package generated"

release_tag=$(echo ${version} | sed -e 's/\./_/g' )

if [ -z ${no_tags} ]; then
    echo "Tagging svn repository using 'pmd_release_${release_tag}'"
	echo "running 'svn copy -m \"$version release tag\"
    https://pmd.svn.sourceforge.net/svnroot/pmd/branches/pmd/4.2.x https://pmd.svn.sourceforge.net/svnroot/pmd/tags/pmd/pmd_release_${release_tag}'"
	svn copy -m "${version} release tag" https://pmd.svn.sourceforge.net/svnroot/pmd/branches/pmd/4.2.x https://pmd.svn.sourceforge.net/svnroot/pmd/tags/pmd/pmd_release_$release_tag
else
	echo "Skipping svn tag!!!"
fi

echo "Generating source file ${pmd_top_dir}/pmd-src-${version}.zip"

ant -f ../bin/build.xml jarsrc
if [ -z ${no_tags} ]; then
    svn -q export "https://pmd.svn.sourceforge.net/svnroot/pmd/tags/pmd/pmd_release_${release_tag}" "${pmd_src_dir}"
else
    if [ "${snapshot}" = "true" ] ; then
        svn -q export https://pmd.svn.sourceforge.net/svnroot/pmd/branches/pmd/4.2.x "${pmd_src_dir}"
    fi
fi

make_tree_structure "${pmd_src_dir}"
cp "../lib/pmd-src-${version}.jar" "${pmd_src_dir}/lib/"
cp "../lib/pmd-${version}.jar" "${pmd_src_dir}/lib"
if [ -z ${no_docs} ] ; then
    cp -R ../target/docs "${pmd_src_dir}"
fi
rm -f "${pmd_src_dir}/etc/clover.license"
cd "${pmd_top_dir}"
zip -q -r "pmd-src-${version}.zip" "pmd-src-${version}"
cd -

echo "Source package generated"

echo "Use the command below to upload to sourceforge"
echo "rsync -avP -e ssh ${pmd_top_dir}/pmd-src-${version}.zip ${pmd_top_dir}/pmd-bin-${version}.zip xlv@frs.sourceforge.net:uploads/"
