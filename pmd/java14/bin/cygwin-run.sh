#! /bin/sh
cygwin=false;
case "`uname`" in
  CYGWIN*) cygwin=true ;;
esac
FILE=$1
FORMAT=$2
RULESETFILES=$3
TOPDIR=../..
VERSION=4.0
PMDJAR=$TOPDIR/java14/lib/pmd14-$VERSION.jar
JARPATH=$TOPDIR/lib/asm-3.0.jar:$TOPDIR/lib/jaxen-1.1.jar
RWPATH=$TOPDIR/java14/lib/retroweaver-rt-2.0Beta4.jar:$TOPDIR/java14/lib/backport-util-concurrent.jar
PMD_CLASSPATH=$JARPATH:$RWPATH
if $cygwin; then
  FILE=`cygpath --windows "$FILE"`
  PMD_CLASSPATH=`cygpath --path --windows "$PMD_CLASSPATH"`
  echo since they are comma delimited the RULESETFILES "$RULESETFILES"
  echo will need an iteration loop to convert properly
fi
java -cp ${PMD_CLASSPATH} net.sourceforge.pmd.PMD $FILE $FORMAT $RULESETFILES
