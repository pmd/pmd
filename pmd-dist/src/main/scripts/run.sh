#!/bin/bash

usage() {
    echo "Usage:"
    echo "    $(basename $0) <application-name> [-h|-v] ..."
    echo ""
    echo "application-name: valid options are: $(valid_app_options)"
    echo "-h print this help"
    echo "-v display PMD's version"
}

valid_app_options () {
    echo "pmd, cpd, cpdgui, designer, bgastviewer, designerold"
}

is_cygwin() {
    case "$(uname)" in
        CYGWIN*|MINGW*)
            readonly cygwin=true
            ;;
    esac
    # OS specific support.  $var _must_ be set to either true or false.
    if [ -z ${cygwin} ] ; then
        readonly cygwin=false
    fi
}

cygwin_paths() {
    # For Cygwin, switch paths to Windows format before running java
    if ${cygwin} ; then
        [ -n "${JAVA_HOME}" ] && JAVA_HOME=$(cygpath --windows "${JAVA_HOME}")
        [ -n "${DIRECTORY}" ] && DIRECTORY=$(cygpath --windows "${DIRECTORY}")
        classpath=$(cygpath --path --windows "${classpath}")
    fi
}

convert_cygwin_vars() {
    # If cygwin, convert to Unix form before manipulating
    if ${cygwin} ; then
        [ -n "${JAVA_HOME}" ] && JAVA_HOME=$(cygpath --unix "${JAVA_HOME}")
        [ -n "${CLASSPATH}" ] && CLASSPATH=$(cygpath --path --unix "${CLASSPATH}")
    fi
}

java_heapsize_settings() {
    local heapsize=${HEAPSIZE}
    case "${heapsize}" in
        [1-9]*[mgMG])
            readonly HEAPSIZE="-Xmx${heapsize}"
            ;;
        '')
            ;;
        *)
            echo "HEAPSIZE '${HEAPSIZE}' unknown (try: 1024m)"
            exit 1
    esac
}


set_lib_dir() {
  if [ -z ${LIB_DIR} ]; then
    local script_dir=$(dirname "${0}")
    local cwd="${PWD}"

    cd "${script_dir}/../lib"
    readonly LIB_DIR=$(pwd -P)
    cd "${cwd}"
  fi
}

check_lib_dir() {
  if [ ! -e "${LIB_DIR}" ]; then
    echo "The jar directory [${LIB_DIR}] does not exist"
  fi
}

jre_specific_vm_options() {
  full_ver=$(java -version 2>&1)
  # java_ver is eg "18" for java 1.8, "90" for java 9.0, "100" for java 10.0.x
  java_ver=$(echo $full_ver | sed -n '{
      # replace early access versions, e.g. 11-ea with 11.0.0
      s/-ea/.0.0/
      # replace versions such as 10 with 10.0.0
      s/version "\([0-9]\{1,\}\)"/version "\1.0.0"/
      # extract the major and minor parts of the version
      s/^.* version "\(.*\)\.\(.*\)\..*".*$/\1\2/p
  }')
  options=""

  if [ "$java_ver" -ge 90 ] && [ "${APPNAME}" = "designer" ]
  then # open internal module of javafx to reflection (for our TreeViewWrapper)
    options="--add-opens javafx.controls/javafx.scene.control.skin=ALL-UNNAMED"
    # The rest here is for RichtextFX
    options+=" --add-opens javafx.graphics/javafx.scene.text=ALL-UNNAMED"
    options+=" --add-opens javafx.graphics/com.sun.javafx.scene.text=ALL-UNNAMED"
    options+=" --add-opens javafx.graphics/com.sun.javafx.text=ALL-UNNAMED"
    options+=" --add-opens javafx.graphics/com.sun.javafx.geom=ALL-UNNAMED"
    # Warn of remaining illegal accesses
    options+=" --illegal-access=warn"
  fi

  echo $options
}

readonly APPNAME="${1}"
if [ -z "${APPNAME}" ]; then
    usage
    exit 1
fi
shift

case "${APPNAME}" in
  "pmd")
    readonly CLASSNAME="net.sourceforge.pmd.PMD"
    ;;
  "cpd")
    readonly CLASSNAME="net.sourceforge.pmd.cpd.CPD"
    ;;
  "designer")
    readonly CLASSNAME="net.sourceforge.pmd.util.fxdesigner.DesignerStarter"
    ;;
  "designerold")
    readonly CLASSNAME="net.sourceforge.pmd.util.designer.Designer"
    ;;
  "bgastviewer")
    readonly CLASSNAME="net.sourceforge.pmd.util.viewer.Viewer"
    ;;
  "cpdgui")
    readonly CLASSNAME="net.sourceforge.pmd.cpd.GUI"
    ;;
  *)
    echo "${APPNAME} is NOT a valid application name, valid options are:$(valid_app_options)"
    ;;
esac

is_cygwin

set_lib_dir
check_lib_dir

convert_cygwin_vars

classpath=$CLASSPATH

cd "${CWD}"

for jarfile in "${LIB_DIR}"/*.jar; do
    if [ -n "$classpath" ]; then
        classpath=$classpath:$jarfile
    else
        classpath=$jarfile
    fi
done

cygwin_paths

java_heapsize_settings

java ${HEAPSIZE} $(jre_specific_vm_options) -cp "${classpath}" "${CLASSNAME}" "$@"

