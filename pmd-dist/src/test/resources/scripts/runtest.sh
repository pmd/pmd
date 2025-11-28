#!/bin/sh
# BSD-style license; for more info see http://pmd.sourceforge.net/license.html

#
# Simple manual test script
# - code is copied from run.sh to be tested here (so please check, it might be out of sync)
# - only the function "determine_java_version" is tested here
# - just run it with "./runtest.sh" and look at the output
# - test cases are at the end of this script
#

export LANG=en_US.UTF-8

mock_show_settings_properties() {
  # instead of: java -XshowSettings:properties 2>&1
  echo "Property settings:
    some.other.property1 = value
    java.version = $MOCKED_JAVA_VERSION
    some.other.property2 = value
    java.home = $MOCKED_JAVA_HOME
    some.other.property3 = value
"
}

# See also
# https://openjdk.org/jeps/223 JEP 223: New Version-String Scheme (Since Java 9)
# https://openjdk.org/jeps/322 JEP 322: Time-Based Release Versioning (Since Java 10)
determine_java_version() {
    #local all_props=$(java -XshowSettings:properties 2>&1)
    all_props="$(mock_show_settings_properties)"
    java_ver_normalized=$(echo "$all_props" | grep "java.version" | sed -n -e '{
      s/^.*java\.version *= *//
      # replace java versions java 1.7 and java 1.8 with 7 and 8
      s/^1\.\([78]\)/\1/
      # print what is left
      p
    }')
    # 1. component: java_version_feature is e.g. "8" for java 1.8, "9" for java 9, "10" for java 10, ...
    java_version_feature=$(echo "$java_ver_normalized" | sed -n -e 's/^\([0-9]\{1,\}\).*$/\1/p')
    # 3. component: update release counter
    java_version_update=$(echo "$java_ver_normalized" | sed -n -e 's/^\([0-9]\{1,\}\)\.\([0-9]\{1,\}\).\([0-9]\{1,\}\).*$/\3/p')
    # if there was no 3rd component, use "0"
    java_version_update=${java_version_update:="0"}

    java_home_property=$(echo "$all_props" | grep "java.home" | sed -n -e 's/^.*java\.home *= *\(.*\)$/\1/; p')
    java_has_javafx=0
    java_javafx_properties=
    if [ -e "$java_home_property/lib/javafx.properties" ]; then
      java_javafx_properties="$java_home_property/lib/javafx.properties"
      java_has_javafx=1
    elif [ -e "$java_home_property/jre/lib/javafx.properties" ]; then
      java_javafx_properties="$java_home_property/jre/lib/javafx.properties"
      java_has_javafx=1
    fi
}

pass() {
  printf "\e[32mOK\e[0m%s\n" "$1"
}

fail() {
  printf "\e[31mFAILED\e[0m\n"
  exit 1
}

run_test() {
  MOCKED_JAVA_VERSION="$1"
  MOCKED_JAVA_HOME="$2"
  EXPECTED_FEATURE="$3"
  EXPECTED_UPDATE="$4"
  EXPECTED_HAS_JAVAFX="$5"
  echo "Testing: '${MOCKED_JAVA_VERSION}' in '${MOCKED_JAVA_HOME}"
  # use mocked path relative to the test script
  MOCKED_JAVA_HOME="$(dirname "$0")/$MOCKED_JAVA_HOME"
  determine_java_version
  printf "java_version_feature: %s " "$java_version_feature"
  if [ "$EXPECTED_FEATURE" = "$java_version_feature" ]; then pass; else fail; fi
  printf "java_version_update: %s " "$java_version_update"
  if [ "$EXPECTED_UPDATE" = "$java_version_update" ]; then pass; else fail; fi
  printf "java_has_javafx: %s " "$java_has_javafx"
  if [ "$EXPECTED_HAS_JAVAFX" = "yes" ] && [ "$java_has_javafx" = 1 ] && [ -n "$java_javafx_properties" ]; then pass;
  elif [ "$EXPECTED_HAS_JAVAFX" = "no" ] && [ "$java_has_javafx" = 0 ] && [ -z "$java_javafx_properties" ]; then pass;
  else fail; fi
  echo
}


# Some predefined variants. We only check for the existence of a javafx.properties file
# at lib/ or jre/lib. java.home sometimes points already to jre (oracle), but openjdk
# builds don't have a jre folder anymore. Anyway, both should work.
JH_WITH_FX=java-with-fx
JH_WITH_FX2=java-with-fx/jre
JH_WITHOUT_FX=java-without-fx

#exit

run_test "1.7.0_80"  "$JH_WITH_FX"       "7"   "80" "yes"
run_test "1.7.0_352" "$JH_WITH_FX"       "7"  "352" "yes"
run_test "1.8.0_271" "$JH_WITH_FX"       "8"  "271" "yes"
run_test "1.8.0_345" "$JH_WITH_FX"       "8"  "345" "yes"
run_test "1.8.0_441" "$JH_WITH_FX"       "8"  "441" "yes"
run_test "1.8.0_441" "$JH_WITH_FX2"      "8"  "441" "yes"
run_test "1.8.0_471" "$JH_WITHOUT_FX"    "8"  "471" "no" # e.g. oracle
run_test "1.8.0_471" "$JH_WITH_FX2"      "8"  "471" "yes" # e.g. azul
run_test "9.0.4"     "$JH_WITHOUT_FX"    "9"    "4" "no"
run_test "10.0.2"    "$JH_WITHOUT_FX"   "10"    "2" "no"
run_test "11.0.6"    "$JH_WITHOUT_FX"   "11"    "6" "no"
run_test "11.0.6.1"  "$JH_WITHOUT_FX"   "11"    "6" "no"
run_test "11.0.13"   "$JH_WITHOUT_FX"   "11"   "13" "no"
run_test '11.0.19'   "$JH_WITHOUT_FX"   "11"   "19" "no"
run_test "17.0.4"    "$JH_WITHOUT_FX"   "17"    "4" "no"
run_test "17.0.4.1"  "$JH_WITHOUT_FX"   "17"    "4" "no"
run_test "17.0.17"   "$JH_WITH_FX"      "17"   "17" "yes"
run_test "18.0.2.1"  "$JH_WITHOUT_FX"   "18"    "2" "no"
run_test "19-ea"     "$JH_WITHOUT_FX"   "19"    "0" "no"
run_test "25"        "$JH_WITHOUT_FX"   "25"    "0" "no"
run_test '25.0.1'    "$JH_WITHOUT_FX"   "25"    "1" "no"

pass " All tests passed. ✔️"
