#!/bin/sh

## This will work in the majority of shells out there...

## This will parse a directory named on the command line and produce a
## cut and paste report for c++ files in that directory.

## Note that other rules are only for Java code not C source.

DIRECTORY=$1

if [ -z "$1" ]; then
				script=`basename $0`
				echo "Usage:"
				echo " $script <directory>"
				exit 1
fi


SCRIPT_DIR=`dirname $0`
CWD="$PWD"

cd "$SCRIPT_DIR/../lib"
LIB_DIR=`pwd -P`

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


MINIMUM_SIZE=100

# echo "CLASSPATH: $classpath"

java -cp $classpath net.sourceforge.pmd.cpd.CPD $MINIMUM_SIZE $DIRECTORY cpp
