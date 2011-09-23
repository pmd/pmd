#/bin/sh

readonly TOPDIR=.
readonly VERSION=5.0
readonly PMDJAR="${TOPDIR}/lib/pmd-${VERSION}.jar"
readonly JARPATH="${TOPDIR}/lib/asm-3.2.jar:${TOPDIR}/lib/jaxen-1.1.1.jar;${TOPDIR}/lib/saxon9.jar;${TOPDIR}/lib/js-cvs-12122009.jar"
readonly OPTS="${JAVA_OPTS}"
readonly MAIN_CLASS="net.sourceforge.pmd.util.viewer.Viewer"

java ${OPTS} -cp "${PMDJAR}:${JARPATH}:${TOPDIR}/build" ${MAIN_CLASS} ${*}
