#!/bin/bash

if [ -z "$3" ]; then
    script=`basename $0`
    echo "Usage:"
    echo "    $script <java-src-file> html|xml|text|vbhtml rulesetfile1[,rulesetfile2[,..]]"
    exit 1
fi

# OS specific support.  $var _must_ be set to either true or false.
cygwin=false;
case "`uname`" in
  CYGWIN*) cygwin=true ;;
esac

SCRIPT_DIR=`dirname $0`
CWD="$PWD"

cd "$SCRIPT_DIR/../lib"
LIB_DIR=`pwd -P`

# If cygwin, convert to Unix form before manipulating
if $cygwin ; then
  [ -n "$JAVA_HOME" ] &&
    JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
 [ -n "$CLASSPATH" ] &&
    CLASSPATH=`cygpath --path --unix "$CLASSPATH"`
fi

classpath=$CLASSPATH

build_dir="$SCRIPT_DIR/../build"

if [ -d "$build_dir" ]; then
    cd "$build_dir"
    build_dir=`pwd -P`
    classpath=$classpath:$build_dir
fi

cd "$CWD"

for jarfile in `ls $LIB_DIR/*.jar`; do
    classpath=$classpath:$jarfile
done


FILE=$1
shift
FORMAT=$1
shift
RULESETFILES="$@"

# echo "CLASSPATH: $classpath"

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
  JAVA_HOME=`cygpath --windows "$JAVA_HOME"`
  classpath=`cygpath --path --windows "$classpath"`
  FILE=`cygpath --windows "$FILE"`
fi

java -Xmx512m -cp $classpath net.sourceforge.pmd.PMD $FILE $FORMAT $RULESETFILES
