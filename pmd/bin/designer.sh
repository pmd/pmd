#!/bin/bash

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

SCRIPT_DIR=$(dirname $0)
CWD="$(PWD)"

is_cygwin

cd "${SCRIPT_DIR}/../lib"
LIB_DIR=$(pwd -P)

convert_cygwin_vars

classpath=$CLASSPATH

cd "${CWD}"

for jarfile in ${LIB_DIR}/*.jar; do
    classpath=$classpath:$jarfile
done

FILE="${1}"
shift
FORMAT="${1}"
shift
RULESETFILES="$@"

cygwin_paths

java -cp ${classpath} net.sourceforge.pmd.util.designer.Designer
