#! /bin/sh
cygwin=false;
case "`uname`" in
  CYGWIN*) cygwin=true ;;
esac
FILE=$1
FORMAT=$2
RULESETFILES=$3
PMD_CLASSPATH=../lib/pmd-4.0.jar:../lib/jaxen-1.1.jar:../lib/asm-3.0.jar
if $cygwin; then
  FILE=`cygpath --windows "$FILE"`
  PMD_CLASSPATH=`cygpath --path --windows "$PMD_CLASSPATH"`
  echo since they are comma delimited the RULESETFILES "$RULESETFILES"
  echo will need an iteration loop to convert properly
fi
java -cp ${PMD_CLASSPATH} net.sourceforge.pmd.PMD $FILE $FORMAT $RULESETFILES
