@echo off
set TOPDIR=%~dp0..
set OPTS=
set MAIN_CLASS=net.sourceforge.pmd.util.fxdesigner.DesignerStarter


:: sets the jver variable to the java version, eg 901 for 9.0.1+x or 180 for 1.8.0_171-b11
:: sets the jvendor variable to either java (oracle) or openjdk
for /f tokens^=1^,3^,4^,5^ delims^=.-_+^"^  %%j in ('java -version 2^>^&1 ^| find "version"') do (
  set jvendor=%%j
  if %%l EQU ea (
    set /A "jver=%%k00"
  ) else (
    set /A jver=%%k%%l%%m
  )
)

Set "jreopts="
:: oracle java 9 and 10 has javafx included as a module
if /I "%jvendor%" EQU "java" (
    if %jver% GEQ 900 (
        if %jver% LSS 1100 (
            :: enable reflection
            Set jreopts=--add-opens javafx.controls/javafx.scene.control.skin=ALL-UNNAMED
        )
    )
)

set "_needjfxlib=0"
if /I "%jvendor%" EQU "openjdk" set _needjfxlib=1
if /I "%jvendor%" EQU "java" (
    if %jver% GEQ 1100 set _needjfxlib=1
)
if %_needjfxlib% EQU 1 (
    if %jver% LSS 1000 (
        echo For openjfx at least java 10 is required.
        pause
        exit
    )
    if [%JAVAFX_HOME%] EQU [] (
        echo The environment variable JAVAFX_HOME is missing.
        pause
        exit
    )
    set "classpath=%TOPDIR%\lib\*;%JAVAFX_HOME%\lib\*"
) else (
    set "classpath=%TOPDIR%\lib\*"
)


java %PMD_JAVA_OPTS% %jreopts% -classpath "%classpath%" %OPTS% %MAIN_CLASS% %*
