@echo off

rem main script
rem make all variables local to not add new global environment variables to the current cmd session
SETLOCAL
rem Use delayed expansion, required e.g. within IF blocks
SETLOCAL EnableDelayedExpansion
rem use unicode codepage to properly support UTF-8
chcp 65001>nul

SET "APPNAME=%1"
CALL :check_java
IF %ERRORLEVEL% NEQ 0 EXIT /B %ERRORLEVEL%

CALL :set_pmd_home_dir
CALL :set_lib_dir
CALL :check_lib_dir
IF %ERRORLEVEL% NEQ 0 EXIT /B %ERRORLEVEL%
CALL :set_conf_dir
CALL :check_conf_dir
IF %ERRORLEVEL% NEQ 0 EXIT /B %ERRORLEVEL%

CALL :add_pmd_classpath
CALL :determine_java_version
CALL :add_openjfx_classpath
IF %ERRORLEVEL% NEQ 0 EXIT /B %ERRORLEVEL%
CALL :determine_additional_java_opts

java ^
  %PMD_JAVA_OPTS% %PMD_ADDITIONAL_JAVA_OPTS% ^
  %PMD_OPENJFX_MODULE_PATH% ^
  -cp "%CLASSPATH%" ^
  net.sourceforge.pmd.cli.PmdCli %*
ENDLOCAL
EXIT /B %ERRORLEVEL%




:check_java
rem check whether java is available at all
java -version > nul 2>&1 || (
  echo No java executable found in PATH
  EXIT /B 2
)
EXIT /B

:set_pmd_home_dir
SET "PMD_HOME=%~dp0.."
EXIT /B

:set_lib_dir
SET "LIB_DIR=%PMD_HOME%/lib"
EXIT /B

:check_lib_dir
IF NOT EXIST "%LIB_DIR%" (
  echo The jar directory [%LIB_DIR%] does not exist
  EXIT /B 2
)
EXIT /B

:set_conf_dir
SET "CONF_DIR=%PMD_HOME%/conf"
EXIT /B

:check_conf_dir
IF NOT EXIST "%CONF_DIR%" (
  echo The configuration directory [%CONF_DIR%] does not exist
  EXIT /B 2
)
EXIT /B

:append_classpath
IF DEFINED CLASSPATH (
  SET "CLASSPATH=%CLASSPATH%;%~1"
) ELSE (
  SET "CLASSPATH=%~1"
)
EXIT /B

:add_pmd_classpath
CALL :append_classpath "%CONF_DIR%;%LIB_DIR%/*"
EXIT /B

:determine_java_version
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
IF EXIST "%java_home_property%/lib/javafx.properties" (
    set java_has_javafx=1
)
IF EXIST "%java_home_property%/jre/lib/javafx.properties" (
    set java_has_javafx=1
)
EXIT /B

:add_openjfx_classpath
SET "PMD_OPENJFX_MODULE_PATH="
IF [%APPNAME%] == [designer] (
  IF %java_has_javafx% EQU 1 (
    rem No additional options needed as JavaFX is bundled.
    rem We are running either an old Oracle Java < 8u451 or an OpenJDK 8 Build from Azul/Bellsoft which
    rem have JavaFX bundled as modules.
    SET "PMD_OPENJFX_MODULE_PATH="
  ) ELSE (

    IF %java_version_feature% EQU 8 (
      IF %java_version_update% GEQ 451 (
        echo.
        echo    JavaFX has been removed from Oracle Java 8 since Java 8u451.
        echo    See https://www.oracle.com/javase/javafx.
        echo    Use Java 11 or later with OpenJFX separately from https://openjfx.io/.
        echo    Alternatively use an OpenJDK build from Azul with JavaFX bundled ^(JDK FX^)
        echo    or from Bellsoft with JavaFX bundled ^(Full JDK^).
        EXIT /B 2
      )
    )

    IF %java_version_feature% LSS 10 (
      echo.
      echo    For OpenJFX at least Java 10 is required. Newer OpenJFX version
      echo    might require newer Java versions.
      EXIT /B 2
    )

    rem openjfx is required for any openjdk builds which don't include JavaFX
    IF NOT DEFINED JAVAFX_HOME (
      echo    The environment variable JAVAFX_HOME is missing.
      echo    See https://docs.pmd-code.org/latest/pmd_userdocs_extending_designer_reference.html#installing-running-updating
      echo    for instructions.
      EXIT /B 2
    )

    SET "PMD_OPENJFX_MODULE_PATH=--module-path %JAVAFX_HOME%/lib --add-modules javafx.controls,javafx.fxml"
  )
)
EXIT /B

:determine_additional_java_opts
SET "PMD_ADDITIONAL_JAVA_OPTS="
IF [%APPNAME%] == [designer] (
  IF %java_version_feature% GEQ 9 (
    rem Since Java 9, Java uses the module system. If JavaFX is bundled, this is also
    rem included as modules. PMD however is run on the classpath and will run as the
    rem "unnamed module". To allow reflection to some JavaFX classes, we need to allow
    rem this access explicitly by opening specific javafx packages.
    rem
    rem This applies to Java 9/10 builds from Oracle and to Java 9+ builds from
    rem Azul (Zulu) or Bellsoft (Liberica).
    rem
    rem It does not apply, if we run Java 8+ without bundled JavaFX and we
    rem put JavaFX on the classpath. Then we don't use the module system at all
    rem and all classes on the classpath are allowed to access all other classes
    rem on the classpath by reflection without explicitly opening packages.
    rem
    rem Reflective access used by PMD Designer
    rem in net.sourceforge.pmd.util.fxdesigner.util.controls.TreeViewWrapper.getVirtualFlow
    SET "PMD_ADDITIONAL_JAVA_OPTS=!PMD_ADDITIONAL_JAVA_OPTS! --add-opens javafx.controls/javafx.scene.control.skin=ALL-UNNAMED"
    rem in net.sourceforge.pmd.util.fxdesigner.util.DesignerUtil.customBuilderFactory
    SET "PMD_ADDITIONAL_JAVA_OPTS=!PMD_ADDITIONAL_JAVA_OPTS! --add-opens javafx.fxml/com.sun.javafx.fxml.builder=ALL-UNNAMED"
    rem
    rem Reflective access used by RichtextFX
    SET "PMD_ADDITIONAL_JAVA_OPTS=!PMD_ADDITIONAL_JAVA_OPTS! --add-opens javafx.graphics/javafx.scene.text=ALL-UNNAMED"
    SET "PMD_ADDITIONAL_JAVA_OPTS=!PMD_ADDITIONAL_JAVA_OPTS! --add-opens javafx.graphics/com.sun.javafx.scene.text=ALL-UNNAMED"
    SET "PMD_ADDITIONAL_JAVA_OPTS=!PMD_ADDITIONAL_JAVA_OPTS! --add-opens javafx.graphics/com.sun.javafx.text=ALL-UNNAMED"
    SET "PMD_ADDITIONAL_JAVA_OPTS=!PMD_ADDITIONAL_JAVA_OPTS! --add-opens javafx.graphics/com.sun.javafx.geom=ALL-UNNAMED"
    rem
    rem Reflective access used by controlsfx
    SET "PMD_ADDITIONAL_JAVA_OPTS=!PMD_ADDITIONAL_JAVA_OPTS! --add-opens javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED"
    SET "PMD_ADDITIONAL_JAVA_OPTS=!PMD_ADDITIONAL_JAVA_OPTS! --add-opens javafx.base/com.sun.javafx.runtime=ALL-UNNAMED"
    SET "PMD_ADDITIONAL_JAVA_OPTS=!PMD_ADDITIONAL_JAVA_OPTS! --add-opens javafx.base/com.sun.javafx.event=ALL-UNNAMED"
    SET "PMD_ADDITIONAL_JAVA_OPTS=!PMD_ADDITIONAL_JAVA_OPTS! --add-opens javafx.graphics/com.sun.javafx.scene.traversal=ALL-UNNAMED"
  )
)

IF %java_version_feature% GEQ 9 (
  IF %java_version_feature% LEQ 16 (
    rem Warn of illegal accesses in general - only possible for Java 9 until Java 16 (inclusive).
    rem With Java 17+ this option has no effect anymore (https://openjdk.org/jeps/403) and is deprecated.
    SET "PMD_ADDITIONAL_JAVA_OPTS=!PMD_ADDITIONAL_JAVA_OPTS! --illegal-access=warn"
  )
)
IF [%APPNAME%] == [designer] (
  IF %java_version_feature% GEQ 24 (
    rem Allow native access to javafx.graphics
    SET "PMD_ADDITIONAL_JAVA_OPTS=!PMD_ADDITIONAL_JAVA_OPTS! --enable-native-access=javafx.graphics"
    rem Don't warn about sun misc unsafe (used by javafx.graphics). Needed until JavaFX 25. See https://bugs.openjdk.org/browse/JDK-8359264.
    SET "PMD_ADDITIONAL_JAVA_OPTS=!PMD_ADDITIONAL_JAVA_OPTS! --sun-misc-unsafe-memory-access=allow"
  )
)

EXIT /B
