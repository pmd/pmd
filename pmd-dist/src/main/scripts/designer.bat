@echo off
set TOPDIR=%~dp0..
set OPTS=
set MAIN_CLASS=net.sourceforge.pmd.util.fxdesigner.Designer


:: sets the jver variable to the java version, eg 901 for 9.0.1+x or 180 for 1.8.0_171-b11
for /f tokens^=2-4^ delims^=.-_+^" %%j in ('java -fullversion 2^>^&1') do set /A jver="%%j%%k%%l"

if "%jver%" GEQ "900" (
    :: enable reflection    
    Set jreopts=--add-opens javafx.controls/javafx.scene.control.skin=ALL-UNNAMED
) else (
    Set jreopts=
)


java %jreopts% -classpath "%TOPDIR%\lib\*" %OPTS% %MAIN_CLASS% %*
