#!/bin/sh

## This will work in the majority of shells out there...

## This will parse a directory named on the command line and produce a
## cut and paste report for c++ files in that directory (or 'c', if you
## set the environment variable LANGUAGE to 'c').

## Note that other rules are only for Java code not C source.

## If you run into java.lang.OutOfMemoryError, try setting the environment
## variable HEAPSIZE to e.g. 512m

DIRECTORY=$1

if [ -z "$1" ]; then
				script=`basename $0`
				echo "Usage:"
				echo " $script <directory>"
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

HEAPSIZE=${HEAPSIZE:-512m}
LANGUAGE=${LANGUAGE:-cpp}
MINIMUM_TOKENS=${MINIMUM_TOKENS:-100}

case "$HEAPSIZE" in
[1-9]*[mgMG]) HEAPSIZE=-Xmx$HEAPSIZE ;;
'') ;;
*) echo "HEAPSIZE '$HEAPSIZE' unknown (try: 512m)"
   exit 1
esac

case "$LANGUAGE" in
c|cpp|fortran|java|jsp|php|ruby) ;;
*) echo "Language '$LANGUAGE' unknown (try: c, cpp, fortran, java, jsp, php, ruby)"
   exit 1
esac

# echo "CLASSPATH: $classpath"

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
  JAVA_HOME=`cygpath --windows "$JAVA_HOME"`
  classpath=`cygpath --path --windows "$classpath"`
  DIRECTORY=`cygpath --windows "$DIRECTORY"`
fi

java $HEAPSIZE -cp $classpath net.sourceforge.pmd.cpd.CPD --minimum-tokens $MINIMUM_TOKENS --files $DIRECTORY --language $LANGUAGE
