@echo off

rem BSD-style license; for more info see http://pmd.sourceforge.net/license.html

rem
rem Simple manual test script
rem - code is copied from pmd.bat to be tested here (so please check, it might be out of sync)
rem - mostly the function "determine_java_version" is tested here
rem - just run it with "pmdtest.bat" and look at the output
rem - test cases are at the end of this script
rem

setlocal EnableDelayedExpansion
rem use unicode codepage to properly support UTF-8
chcp 65001>nul
rem make all variables local to not add new global environment variables to the current cmd session
setlocal

GOTO :main

:mock_show_settings_properties
rem instead of: java -XshowSettings:properties 2>&1

set nl=^


rem two empty lines required
set "MOCKED_JAVA_PROPERTIES=Property settings:!nl!"
set "MOCKED_JAVA_PROPERTIES=!MOCKED_JAVA_PROPERTIES!    some.other.property1 = value!nl!"
set "MOCKED_JAVA_PROPERTIES=!MOCKED_JAVA_PROPERTIES!    java.version = !MOCKED_JAVA_VERSION!!nl!"
set "MOCKED_JAVA_PROPERTIES=!MOCKED_JAVA_PROPERTIES!    some.other.property2 = value!nl!"
set "MOCKED_JAVA_PROPERTIES=!MOCKED_JAVA_PROPERTIES!    java.home = !MOCKED_JAVA_HOME!!nl!"
set "MOCKED_JAVA_PROPERTIES=!MOCKED_JAVA_PROPERTIES!    some.other.property3 = value!nl!"
EXIT /B

:determine_java_version
CALL :mock_show_settings_properties

rem sets the java_version_feature variable. This is e.g. "8" for java 1.8, "9" for java 9, "10" for java 10, ...
rem sets the java_version_update variable. This is the update release counter: e.g. "17" for java 11.0.17

set java_version_prop=
set java_home_property=
FOR /F tokens^=1^,3^  %%j IN ("!MOCKED_JAVA_PROPERTIES!") DO (
    IF "%%j" == "java.version" set java_version_prop=%%k
    IF "%%j" == "java.home" set java_home_property=%%k
)

set java_version_feature=
set java_version_update=
FOR /f tokens^=1^,2^,3^,4^ delims^=.-_+^"^  %%j IN ("%java_version_prop%") DO (
  IF %%j EQU 1 (
    set java_version_feature=%%k
    set java_version_update=%%m
  ) ELSE (
    set java_version_feature=%%j
    set java_version_update=%%l
  )
)
rem if there was no 3rd component (eg. -ea or ga version), use "0"
IF "%java_version_update%" == "" set java_version_update=0

set java_has_javafx=0
set java_javafx_properties=
set java_javafx_properties_path=
IF EXIST "%java_home_property%/lib/javafx.properties" (
    set "java_javafx_properties=%java_home_property%/lib/javafx.properties"
    set java_has_javafx=1
)
IF EXIST "%java_home_property%/jre/lib/javafx.properties" (
    set "java_javafx_properties=%java_home_property%/jre/lib/javafx.properties"
    set java_has_javafx=1
)
rem resolve dirname
IF %java_has_javafx% EQU 1 FOR %%F IN (%java_javafx_properties%) DO set "java_javafx_properties_path=%%~dpF"
rem remove trailing backslash
IF %java_has_javafx% EQU 1 set "java_javafx_properties_path=%java_javafx_properties_path:~0,-1%"

EXIT /B


:check_for_java_command
set cmd=%1
echo cmd=%1
%cmd% -version > nul 2>&1 || (
    echo No java executable not found in PATH
    EXIT /B 1
)
EXIT /B 0

:check_classpath_extension
setlocal
set cp=%1
echo testing cp=%1
if defined cp (
    echo classpath is not empty
)
endlocal
EXIT /B

:pass
echo  [32mOK[0m %~1
EXIT /B

:fail
echo  [31mFAILED[0m
set failed=1
EXIT /B

:run_test
IF %failed% EQU 1 EXIT /B
set MOCKED_JAVA_VERSION=%~1
set MOCKED_JAVA_HOME=%~2
set EXPECTED_FEATURE=%3
set EXPECTED_UPDATE=%4
set EXPECTED_HAS_JAVAFX=%5

echo Testing: '%MOCKED_JAVA_VERSION%' in '%MOCKED_JAVA_HOME%'

rem use mocked path relative to the test script
set MOCKED_JAVA_HOME=%~dp0%MOCKED_JAVA_HOME%
CALL :determine_java_version

echo.|set /p =java_version_feature: %java_version_feature%
IF %EXPECTED_FEATURE% == %java_version_feature% ( CALL :pass ) ELSE ( CALL :fail )
echo.|set /p =java_version_update: %java_version_update%
IF %EXPECTED_UPDATE% == %java_version_update% ( CALL :pass ) ELSE ( CALL :fail )
echo.|set /p =java_has_javafx: %java_has_javafx%
set "_temp_javafx_passed=0"
IF "%EXPECTED_HAS_JAVAFX%" == "yes" (
    IF %java_has_javafx% EQU 1 (
        IF NOT "%java_javafx_properties%" == "" (
            set "_temp_javafx_passed=1"
            CALL :pass
        )
    )
)
IF "%EXPECTED_HAS_JAVAFX%" == "no" (
    IF %java_has_javafx% EQU 0 (
        IF "%java_javafx_properties%" == "" (
            set "_temp_javafx_passed=1"
            CALL :pass
        )
    )
)
IF %_temp_javafx_passed% EQU 0 ( CALL :fail )
IF %java_has_javafx% EQU 1 (
    echo.|set /p =java_javafx_properties_path:
    IF NOT "%java_javafx_properties_path%" == "" ( CALL :pass ) ELSE ( CALL :fail )
)
echo.

EXIT /B

:main

CALL :check_for_java_command javanotfound
echo errorlevel: %ERRORLEVEL%

CALL :check_classpath_extension
CALL :check_classpath_extension a\b.jar
CALL :check_classpath_extension "a\b.jar;c\d.jar"
CALL :check_classpath_extension "a\b.jar"
CALL :check_classpath_extension "a"\b.jar
CALL :check_classpath_extension "a"\b.jar;"c"\d.jar

rem Some predefined variants. We only check for the existence of a javafx.properties file
rem at lib/ or jre/lib. java.home sometimes points already to jre (oracle), but openjdk
rem builds don't have a jre folder anymore. Anyway, both should work.
set JH_WITH_FX=java-with-fx
set JH_WITH_FX2=java-with-fx/jre
set JH_WITHOUT_FX=java-without-fx

set failed=0
CALL :run_test "1.7.0_80"  "%JH_WITH_FX%"       7     80  yes
CALL :run_test "1.7.0_352" "%JH_WITH_FX%"       7    352  yes
CALL :run_test "1.8.0_271" "%JH_WITH_FX%"       8    271  yes
CALL :run_test "1.8.0_345" "%JH_WITH_FX%"       8    345  yes
CALL :run_test "1.8.0_441" "%JH_WITH_FX%"       8    441  yes
CALL :run_test "1.8.0_441" "%JH_WITH_FX2%"      8    441  yes
rem e.g. oracle
CALL :run_test "1.8.0_471" "%JH_WITHOUT_FX%"    8    471  no
rem # e.g. azul
CALL :run_test "1.8.0_471" "%JH_WITH_FX2%"      8    471  yes
CALL :run_test "9.0.4"     "%JH_WITHOUT_FX%"    9      4  no
CALL :run_test "10.0.2"    "%JH_WITHOUT_FX%"   10      2  no
CALL :run_test "11.0.6"    "%JH_WITHOUT_FX%"   11      6  no
CALL :run_test "11.0.6.1"  "%JH_WITHOUT_FX%"   11      6  no
CALL :run_test "11.0.13"   "%JH_WITHOUT_FX%"   11     13  no
CALL :run_test "11.0.19"   "%JH_WITHOUT_FX%"   11     19  no
CALL :run_test "17.0.4"    "%JH_WITHOUT_FX%"   17      4  no
CALL :run_test "17.0.4.1"  "%JH_WITHOUT_FX%"   17      4  no
CALL :run_test "17.0.17"   "%JH_WITH_FX%"      17     17  yes
CALL :run_test "18.0.2.1"  "%JH_WITHOUT_FX%"   18      2  no
CALL :run_test "19-ea"     "%JH_WITHOUT_FX%"   19      0  no
CALL :run_test "25"        "%JH_WITHOUT_FX%"   25      0  no
CALL :run_test "25.0.1"    "%JH_WITHOUT_FX%"   25      1  no


IF %failed% EQU 0 CALL :pass "All tests passed. ✔️"

EXIT /B
