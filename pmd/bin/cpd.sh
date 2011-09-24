#!/bin/sh

## This will work in the majority of shells out there...

## This will parse a directory named on the command line and produce a
## cut and paste report for c++ files in that directory (or 'c', if you
## set the environment variable LANGUAGE to 'c').

## Note that other rules are only for Java code not C source.

## If you run into java.lang.OutOfMemoryError, try setting the environment
## variable HEAPSIZE to e.g. 512m

usage() {
    echo "Usage:"
    echo "$(basename ${0})  <source-directory> [--ignore-literals] [--ignore-identifiers]"
    echo ""
    echo "Set language with environment variable LANGUAGE ($(supported_languages))"
}

supported_languages() {
    echo "c, cpp, fortran, java, jsp, php, ruby,cs"
}

is_cygwin() {
    case "$(uname)" in
        CYGWIN*)
            readonly cygwin=true
            ;;
    esac
    # OS specific support.  $var _must_ be set to either true or false.
    if [ -z ${cygwin} ] ; then
        readonly cygwin=false
    fi
}

convert_cygwin_vars() {
    # If cygwin, convert to Unix form before manipulating
    if $cygwin ; then
        [ -n "${JAVA_HOME}" ] &&
            JAVA_HOME=$(cygpath --unix "${JAVA_HOME}")
        [ -n "${CLASSPATH}" ] &&
            CLASSPATH=$(cygpath --path --unix "${CLASSPATH}")
    fi
}

cygwin_paths() {
    # For Cygwin, switch paths to Windows format before running java
    if ${cygwin} ; then
        JAVA_HOME=$(cygpath --windows "${JAVA_HOME}")
        classpath=$(cygpath --path --windows "${classpath}")
        DIRECTORY=$(cygpath --windows "${DIRECTORY}")
    fi
}

language_settings() {
    readonly LANGUAGE=${LANGUAGE:-cpp}
    case "${LANGUAGE}" in
        c|cs|cpp|fortran|java|jsp|php|ruby)
            echo "Language is set to ${LANGUAGE}"
            ;;
        *)
            echo "Language '${LANGUAGE}' unknown (try: $(supported_languages))"
            exit 1
    esac
}

java_heapsize_settings() {
    local heapsize=${HEAPSIZE:-512m}
    case "${heapsize}" in
        [1-9]*[mgMG])
            readonly HEAPSIZE="-Xmx${heapsize}"
            ;;
        '')
            ;;
        *)
            echo "HEAPSIZE '${HEAPSIZE}' unknown (try: 512m)"
            exit 1
    esac
}

DIRECTORY=${1}

if [ -z "${DIRECTORY}" ]; then
	usage
    exit 1
fi
shift

is_cygwin

readonly SCRIPT_DIR=$(dirname ${0})
CWD="${PWD}"

cd "${SCRIPT_DIR}/../lib"
readonly LIB_DIR=$(pwd -P)

convert_cygwin_vars

classpath="${CLASSPATH}"

cd "${CWD}"
for jarfile in ${LIB_DIR}/*.jar; do
    classpath=${classpath}:${jarfile}
done

java_heapsize_settings

language_settings

cygwin_paths

java "${HEAPSIZE}" -cp "${classpath}" net.sourceforge.pmd.cpd.CPD \
    --minimum-tokens "${MINIMUM_TOKENS:-100}"\
    --files "${DIRECTORY}" \
    --language "${LANGUAGE}" \
    ${@}
