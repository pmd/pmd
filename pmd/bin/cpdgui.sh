#!/bin/sh

# Shell script to launch PMD's Copy Paste Detector GUI on a
# UNIX system, based on the cpdgui.bat file used to do the
# same on MS Windows systems. This requires that the "java"
# command be in your executable PATH.

# Version number for this PMD release
readonly VERSION=5.0

readonly BINDIR=$(dirname $0)
readonly ROOTDIR=${BINDIR}/..
readonly PMDJAR=${ROOTDIR}/lib/pmd-${VERSION}.jar
readonly JARPATH=${TOPDIR}/lib/asm-3.2.jar:${TOPDIR}/lib/jaxen-1.1.1.jar
readonly JVMOPTS=-Xmx512m
readonly MAIN_CLASS=net.sourceforge.pmd.cpd.GUI

java ${JVMOPTS} -cp "${PMDJAR}:${JARPATH}" "${MAIN_CLASS}" $*
