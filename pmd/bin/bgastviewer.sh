TOPDIR=..
VERSION=5.0
PMDJAR=$TOPDIR/lib/pmd-$VERSION.jar
JARPATH=$TOPDIR/lib/asm-3.2.jar:$TOPDIR/lib/jaxen-1.1.1.jar;$TOPDIR/lib/saxon9.jar;$TOPDIR/lib/js-cvs-12122009.jar
OPTS=
MAIN_CLASS=net.sourceforge.pmd.util.viewer.Viewer

java $OPTS -cp $PMDJAR:$JARPATH:$TOPDIR/build $MAIN_CLASS $*

