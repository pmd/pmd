#!/bin/bash

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


FILE=$1
shift
FORMAT=$1
shift
RULESETFILES="$@"

# echo "CLASSPATH: $classpath"

java -cp $classpath net.sourceforge.pmd.util.designer.Designer
