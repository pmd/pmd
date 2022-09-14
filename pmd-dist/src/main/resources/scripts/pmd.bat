@echo off
set TOPDIR="%~dp0.."
set OPTS=
set COMMAND=%1
set MAIN_CLASS=net.sourceforge.pmd.cli.PmdCli


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
            SETLOCAL EnableDelayedExpansion
            rem java9 and java10 from oracle contain javafx as a module
            rem open internal module of javafx to reflection (for our TreeViewWrapper)
            set "jreopts=--add-opens javafx.controls/javafx.scene.control.skin=ALL-UNNAMED"
            rem The rest here is for RichtextFX
            set "jreopts=!jreopts! --add-opens javafx.graphics/javafx.scene.text=ALL-UNNAMED"
            set "jreopts=!jreopts! --add-opens javafx.graphics/com.sun.javafx.scene.text=ALL-UNNAMED"
            set "jreopts=!jreopts! --add-opens javafx.graphics/com.sun.javafx.text=ALL-UNNAMED"
            set "jreopts=!jreopts! --add-opens javafx.graphics/com.sun.javafx.geom=ALL-UNNAMED"
            rem Warn of remaining illegal accesses
            set "jreopts=!jreopts! --illegal-access=warn"

        )
    )
)

set "_needjfxlib=0"
if %COMMAND% EQU "designer" (
    if /I "%jvendor%" EQU "openjdk" set _needjfxlib=1
    if /I "%jvendor%" EQU "java" (
        if %jver% GEQ 1100 set _needjfxlib=1
    )
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
    set classpath=%TOPDIR%\conf;%TOPDIR%\lib\*;%JAVAFX_HOME%\lib\*
) else (
    set classpath=%TOPDIR%\conf;%TOPDIR%\lib\*
)


java %PMD_JAVA_OPTS% %jreopts% -classpath %classpath% %OPTS% %MAIN_CLASS% %*
