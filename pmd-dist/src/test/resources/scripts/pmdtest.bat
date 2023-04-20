@echo off

rem BSD-style license; for more info see http://pmd.sourceforge.net/license.html

rem
rem Simple manual test script
rem - code is copied from pmd.bat to be tested here (so please check, it might be out of sync)
rem - mostly the function "determine_java_version" is tested here
rem - just run it with "pmd.bat" and look at the output
rem - test cases are at the end of this script
rem

GOTO :main

:determine_java_version
rem sets the jver variable to the java version, eg 90 for 9.0.1+x or 80 for 1.8.0_171-b11 or 110 for 11.0.6.1
rem sets the jvendor variable to either java (oracle) or openjdk
for /f tokens^=1^,3^,4^,5^ delims^=.-_+^"^  %%j in (%full_version%) do (
  set jvendor=%%j
  if %%l EQU ea (
    set /A "jver=%%k0"
  ) else (
    if %%k EQU 1 (
      rem for java version 1.7.x, 1.8.x, ignore the first 1.
      set /A "jver=%%l%%m"
    ) else (
      set /A "jver=%%k%%l"
    )
  )
)

set detection=
if %jver% GEQ 70 (
    if %jver% LSS 80 (
        set detection="detected java 7"
    )
)
if [%detection%] == [] (
    if %jver% GEQ 80 (
        if %jver% LSS 90 (
            set detection="detected java 8"
        )
    )
)
if [%detection%] == [] (
    if %jver% GEQ 90 (
        if %jver% LSS 110 (
          if %jvendor% == java (
              set detection="detected java 9 or 10 from oracle"
          )
        )
    )
)
if [%detection%] == [] (
    if %jvendor% == openjdk (
        set detection="detected java 11 from oracle or any openjdk"
    )
)
if [%detection%] == [] (
    if %jvendor% == java (
        if %jver% GEQ 110 (
            set detection="detected java 11 from oracle or any openjdk"
        )
    )
)

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

:run_test
set full_version=%1
set expected_vendor=%2
set expected_version=%3
set expected_detection=%4

CALL :determine_java_version

echo full_version: %full_version%
if %jver% == %expected_version% ( echo jver: %jver% [32mOK[0m ) ELSE ( echo jver: %jver% [31mEXPECTED: %expected_version% [0m )
if %jvendor% == %expected_vendor% ( echo jvendor: %jvendor% [32mOK[0m ) ELSE ( echo jvendor: %jvendor% [31mEXPECTED: %expected_vendor% [0m )
if [%detection%] == [%expected_detection%] ( echo detection: %detection% [32mOK[0m ) ELSE ( echo detection: %detection% [31mEXPECTED: %expected_detection% [0m )
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

CALL :run_test "java version ""1.7.0_80"""                java      70    "detected java 7"
CALL :run_test "openjdk version ""1.7.0_352"""            openjdk   70    "detected java 7"
CALL :run_test "java version ""1.8.0_271"""               java      80    "detected java 8"
CALL :run_test "openjdk version ""1.8.0_345"""            openjdk   80    "detected java 8"
CALL :run_test "java version ""9.0.4"""                   java      90    "detected java 9 or 10 from oracle"
CALL :run_test "openjdk version ""9.0.4"""                openjdk   90    "detected java 11 from oracle or any openjdk"
CALL :run_test "java version ""10.0.2"" 2018-07-17"       java      100   "detected java 9 or 10 from oracle"
CALL :run_test "openjdk version ""11.0.6"" 2022-08-12"    openjdk   110   "detected java 11 from oracle or any openjdk"
CALL :run_test "openjdk version ""11.0.6.1"" 2022-08-12"  openjdk   110   "detected java 11 from oracle or any openjdk"
CALL :run_test "java version ""11.0.13"" 2021-10-19 LTS"  java      110   "detected java 11 from oracle or any openjdk"
CALL :run_test "openjdk version ""17.0.4"" 2022-08-12"    openjdk   170   "detected java 11 from oracle or any openjdk"
CALL :run_test "openjdk version ""17.1.4"" 2022-08-12"    openjdk   171   "detected java 11 from oracle or any openjdk"
CALL :run_test "openjdk version ""17.0.4.1"" 2022-08-12"  openjdk   170   "detected java 11 from oracle or any openjdk"
CALL :run_test "openjdk version ""18.0.2.1"" 2022-08-18"  openjdk   180   "detected java 11 from oracle or any openjdk"
CALL :run_test "openjdk version ""19-ea"" 2022-09-20"     openjdk   190   "detected java 11 from oracle or any openjdk"
