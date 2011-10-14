#!/bin/sh

# Shell script to launch PMD's Copy Paste Detector GUI on a 
# UNIX system, based on the cpdgui.bat file used to do the
# same on MS Windows systems. This requires that the "java"
# command be in your executable PATH.

# Version number for this PMD release
VERSION=4.2.6

BINDIR=`dirname $0`
ROOTDIR=${BINDIR}/..
PMDJAR=${ROOTDIR}/lib/pmd-${VERSION}.jar
JARPATH=${TOPDIR}/lib/asm-3.2.jar:${TOPDIR}/lib/jaxen-1.1.1.jar
JVMOPTS=-Xmx512m
MAIN_CLASS=net.sourceforge.pmd.cpd.GUI

java ${JVMOPTS} -cp "${PMDJAR}:${JARPATH}" ${MAIN_CLASS} $*
