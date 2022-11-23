#!/bin/bash
# BSD-style license; for more info see http://pmd.sourceforge.net/license.html

#
# Simple manual test script
# - code is copied from run.sh to be tested here (so please check, it might be out of sync)
# - mostly the function "determine_java_version" is tested here
# - just run it with "./runtest.sh" and look at the output
# - test cases are at the end of this script
#

export LANG=en_US.UTF-8

FULL_JAVA_VERSION=""

get_full_java_version() {
  #java -version 2>&1
  #echo "openjdk version \"11.0.6\" 2022-08-12"
  echo "$FULL_JAVA_VERSION"
}

determine_java_version() {
    local full_ver=$(get_full_java_version)
    # java_ver is eg "80" for java 1.8, "90" for java 9.0, "100" for java 10.0.x
    java_ver=$(echo "$full_ver" | sed -n '{
        # replace early access versions, e.g. 11-ea with 11.0.0
        s/-ea/.0.0/
        # replace versions such as 10 with 10.0.0
        s/version "\([0-9]\{1,\}\)"/version "\1.0.0"/
        # replace old java versions 1.x.* (java 1.7, java 1.8) with x.*
        s/version "1\.\(.*\)"/version "\1"/
        # extract the major and minor parts of the version
        s/^.* version "\([0-9]\{1,\}\)\.\([0-9]\{1,\}\).*".*$/\1\2/p
    }')
    # java_vendor is either java (oracle) or openjdk
    java_vendor=$(echo "$full_ver" | sed -n -e 's/^\(.*\) version .*$/\1/p')
}

jre_specific_vm_options() {
    options=""
    if [ "$java_ver" -ge 70 ] && [ "$java_ver" -lt 80 ]
    then
      options="detected java 7"
    elif [ "$java_ver" -ge 80 ] && [ "$java_ver" -lt 90 ]
    then
      options="detected java 8"
    elif [ "$java_ver" -ge 90 ] && [ "$java_ver" -lt 110 ] && [ "$java_vendor" = "java" ]
    then
      options="detected java 9 or 10 from oracle"
    elif [ "$java_vendor" = "openjdk" ] || ( [ "$java_vendor" = "java" ] && [ "$java_ver" -ge 110 ] )
    then
      options="detected java 11 from oracle or any openjdk"
    fi
    echo $options
}

run_test() {
  FULL_JAVA_VERSION="$1"
  EXPECTED_VENDOR="$2"
  EXPECTED_VER="$3"
  EXPECTED="$4"
  echo "Testing: '${FULL_JAVA_VERSION}'"
  determine_java_version
  java_opts="$(jre_specific_vm_options)"
  echo -n "java_ver: $java_ver "
  if [ "$EXPECTED_VER" = "$java_ver" ]; then echo -e "\e[32mOK\e[0m"; else echo -e "\e[31mFAILED\e[0m"; fi
  echo -n "java_vendor: $java_vendor "
  if [ "$EXPECTED_VENDOR" = "$java_vendor" ]; then echo -e "\e[32mOK\e[0m"; else echo -e "\e[31mFAILED\e[0m"; fi
  echo -n "java_opts: $java_opts "
  if [ "$EXPECTED" = "$java_opts" ]; then echo -e "\e[32mOK\e[0m"; else echo -e "\e[31mFAILED\e[0m - expected: ${EXPECTED}"; fi
  echo
}

run_test "java version \"1.7.0_80\""                "java"      "70"    "detected java 7"
run_test "openjdk version \"1.7.0_352\""            "openjdk"   "70"    "detected java 7"
run_test "java version \"1.8.0_271\""               "java"      "80"    "detected java 8"
run_test "openjdk version \"1.8.0_345\""            "openjdk"   "80"    "detected java 8"
run_test "java version \"9.0.4\""                   "java"      "90"    "detected java 9 or 10 from oracle"
run_test "openjdk version \"9.0.4\""                "openjdk"   "90"    "detected java 11 from oracle or any openjdk"
run_test "java version \"10.0.2\" 2018-07-17"       "java"      "100"   "detected java 9 or 10 from oracle"
run_test "openjdk version \"11.0.6\" 2022-08-12"    "openjdk"   "110"   "detected java 11 from oracle or any openjdk"
run_test "openjdk version \"11.0.6.1\" 2022-08-12"  "openjdk"   "110"   "detected java 11 from oracle or any openjdk"
run_test "java version \"11.0.13\" 2021-10-19 LTS"  "java"      "110"   "detected java 11 from oracle or any openjdk"
run_test "openjdk version \"17.0.4\" 2022-08-12"    "openjdk"   "170"   "detected java 11 from oracle or any openjdk"
run_test "openjdk version \"17.1.4\" 2022-08-12"    "openjdk"   "171"   "detected java 11 from oracle or any openjdk"
run_test "openjdk version \"17.0.4.1\" 2022-08-12"  "openjdk"   "170"   "detected java 11 from oracle or any openjdk"
run_test "openjdk version \"18.0.2.1\" 2022-08-18"  "openjdk"   "180"   "detected java 11 from oracle or any openjdk"
run_test "openjdk version \"19-ea\" 2022-09-20"     "openjdk"   "190"   "detected java 11 from oracle or any openjdk"
