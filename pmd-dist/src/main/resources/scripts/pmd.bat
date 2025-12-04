@echo off

SETLOCAL EnableDelayedExpansion
rem use unicode codepage to properly support UTF-8
chcp 65001>nul

rem make all variables local to not add new global environment variables to the current cmd session
setlocal
set "TOPDIR=%~dp0.."
set "OPTS="
set "COMMAND=%1"
set "MAIN_CLASS=net.sourceforge.pmd.cli.PmdCli"

rem check whether java is available at all
java -version > nul 2>&1 || (
    echo No java executable found in PATH
    exit /b 1
)

rem sets the java_version_feature variable. This is e.g. "8" for java 1.8, "9" for java 9, "10" for java 10, ...
rem sets the java_version_update variable. This is the update release counter: e.g. "17" for java 11.0.17

set java_version_prop=
set java_home_property=
FOR /F tokens^=1^,3^  %%j IN ('java -XshowSettings:properties 2^>^&1') DO (
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

Set "jreopts="
IF [%COMMAND%] == [designer] (
    IF %java_version_feature% GEQ 9 (
        IF %java_has_javafx% EQU 1 (
            rem java9 and java10 from oracle contain javafx as a module
            rem Azul provides builds which include javafx as well
            rem Since Java 9, it is included as a module. But we run on the classpath (aka unnamed module) and
            rem want to access the classes.
            rem
            rem for PMD Designer
            rem in net.sourceforge.pmd.util.fxdesigner.util.controls.TreeViewWrapper.getVirtualFlow
            set "jreopts=--add-opens javafx.controls/javafx.scene.control.skin=ALL-UNNAMED"
            rem in net.sourceforge.pmd.util.fxdesigner.util.DesignerUtil.customBuilderFactory
            set "jreopts=!jreopts! --add-opens javafx.fxml/com.sun.javafx.fxml.builder=ALL-UNNAMED"
            rem
            rem for RichtextFX
            set "jreopts=!jreopts! --add-opens javafx.graphics/javafx.scene.text=ALL-UNNAMED"
            set "jreopts=!jreopts! --add-opens javafx.graphics/com.sun.javafx.scene.text=ALL-UNNAMED"
            set "jreopts=!jreopts! --add-opens javafx.graphics/com.sun.javafx.text=ALL-UNNAMED"
            set "jreopts=!jreopts! --add-opens javafx.graphics/com.sun.javafx.geom=ALL-UNNAMED"
            rem
            rem for controlsfx
            set "jreopts=!jreopts! --add-opens javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED"
            set "jreopts=!jreopts! --add-opens javafx.base/com.sun.javafx.runtime=ALL-UNNAMED"
            set "jreopts=!jreopts! --add-opens javafx.base/com.sun.javafx.event=ALL-UNNAMED"
            set "jreopts=!jreopts! --add-opens javafx.graphics/com.sun.javafx.scene.traversal=ALL-UNNAMED"

            IF %java_version_feature% LSS 17 (
                rem Warn of remaining illegal accesses - only possible until java 16.
                rem With Java 17+ this option has no effect anymore (https://openjdk.org/jeps/403).
                set "jreopts=!jreopts! --illegal-access=warn"
            )
        )
    )
)

IF [%COMMAND%] == [designer] (
    IF %java_has_javafx% EQU 1 (
      rem almost nothing to do, javafx is bundled (either an old Oracle Java < 8u451 or an OpenJDK 8 Build from Azul)
      rem still add the path where javafx.properties is located to the classpath, so that Designer's
      rem net.sourceforge.pmd.util.fxdesigner.util.JavaFxUtil#getJavaFxVersion can find it.
      set "pmd_classpath=%TOPDIR%\conf;%TOPDIR%\lib\*;%java_javafx_properties_path%"
    ) ELSE (
        IF %java_version_feature% EQU 8 (
            IF %java_version_update% GEQ 451 (
                echo     JavaFX has been removed from Oracle Java 8 since Java 8u451. See https://www.oracle.com/javase/javafx.
                echo     Use Java 11 or later with OpenJFX separately from https://openjfx.io/.
                echo     Alternatively use an OpenJDK build from Azul with JavaFX bundled ^(JDK FX^).
                EXIT /B 1
            )
        )
        IF %java_version_feature% LSS 10 (
            echo For OpenJFX at least Java 10 is required.
            EXIT /B 1
        )
        IF not defined JAVAFX_HOME (
            echo The environment variable JAVAFX_HOME is missing.
            EXIT /B 1
        )
        rem The wildcard will include only jar files, but we need to access also
        rem property files such as javafx.properties that lay bare in the dir
        rem note: no trailing backslash, as this would escape a following quote when %pmd_classpath% is used later
        set "pmd_classpath=%TOPDIR%\conf;%TOPDIR%\lib\*;%JAVAFX_HOME%\lib\*;%JAVAFX_HOME%\lib"
    )
) else (
    rem note: no trailing backslash, as this would escape a following quote when %pmd_classpath% is used later
    set "pmd_classpath=%TOPDIR%\conf;%TOPDIR%\lib\*"
)

if defined CLASSPATH (
    set "pmd_classpath=%CLASSPATH%;%pmd_classpath%"
)

java %PMD_JAVA_OPTS% %jreopts% -classpath "%pmd_classpath%" %OPTS% %MAIN_CLASS% %*
