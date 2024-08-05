---
title: Ant Task Usage
tags: [userdocs, tools]
permalink: pmd_userdocs_tools_ant.html
author: >
    David Dixon-Peugh <dpeugh@users.sourceforge.net>,
    Tom Copeland <tom@infoether.com>,
    Xavier Le Vourch <xlv@users.sourceforge.net>
last_updated: June 2024 (7.3.0)
---

## PMD

### Description

Runs a set of static code analysis rules on some source code files and generates a list of problems found.

### Installation

Before you can use the `pmd` task in your ant `build.xml` file, you need to install PMD and its libraries into
ant's classpath, as described in [Optional Tasks](https://ant.apache.org/manual/install.html#optionalTasks).

First you need to download PMD's binary distribution zip file.
Then you can either copy all "*.jar" files from PMD's lib folder into one of ANT's library folders
(`ANT_HOME/lib`, `${user.home}/.ant/lib`) or using the `-lib` command line parameter.

However, the preferred way is to define a `<classpath>` for pmd itself and use this classpath when
adding the PMD Task. Assuming, you have extracted the PMD zip file to `/home/joe/pmd-bin-{{site.pmd.version}}`,
then you can make use of the PMD Task like this:

    <taskdef name="pmd" classname="net.sourceforge.pmd.ant.PMDTask">
        <classpath>
            <fileset dir="/home/joe/pmd-bin-{{site.pmd.version}}/lib">
                <include name="*.jar"/>
            </fileset>
        </classpath>
    </taskdef>

Alternatively, a path can be defined and used via `classpathref`:

    <path id="pmd.classpath">
        <fileset dir="/home/joe/pmd-bin-{{site.pmd.version}}/lib">
            <include name="*.jar"/>
        </fileset>
    </path>
    <taskdef name="pmd" classname="net.sourceforge.pmd.ant.PMDTask" classpathref="pmd.classpath" />

The examples below won't repeat this taskdef element, as this is always required.

### Parameters

<table>
  <tr>
    <th>Attribute</th>
    <th>Description</th>
    <th>Required</th>
  </tr>
  <tr>
    <td>rulesetfiles</td>
    <td>
        A comma delimited list of ruleset files ('rulesets/java/quickstart.xml,config/my-ruleset.xml').
        If you write your own ruleset files, you can put them on the classpath and plug them in here.
    </td>
    <td>Yes, unless the ruleset nested element is used</td>
  </tr>
  <tr>
    <td>failOnError</td>
    <td>Whether or not to fail the build if any recoverable errors occurred while analyzing files.</td>
    <td>No</td>
  </tr>
    <tr>
      <td>failOnRuleViolation</td>
      <td>Whether or not to fail the build if PMD finds any problems</td>
      <td>No</td>
    </tr>
    <tr>
      <td>minimumPriority</td>
      <td>The rule priority threshold; rules with lower priority than they will not be used</td>
      <td>No</td>
    </tr>
    <tr>
      <td>failuresPropertyName</td>
      <td>A property name to plug the number of rule violations into when the task finishes</td>
      <td>No</td>
    </tr>
    <tr>
      <td>encoding</td>
      <td>The character set encoding (e.g. UTF-8) to use when reading the source code files</td>
      <td>No</td>
    </tr>
    <tr>
      <td>suppressMarker</td>
      <td>The series of characters to use to tell PMD to skip lines - the default is NOPMD.</td>
      <td>No</td>
    </tr>
    <tr>
      <td>maxRuleViolations</td>
      <td>
        Whether or not to fail the build if PMD finds more than the value of this attribute.
        Note that setting this attribute does not require to set the failOnRuleViolation to true.
      </td>
      <td>No</td>
    </tr>
    <tr>
      <td>cacheLocation</td>
      <td>
        The location of the analysis cache file to be used.
        Setting this property enables Incremental Analysis, which can greatly improve analysis time without loosing analysis quality.
        <b>Its use is strongly recommended.</b>
      </td>
      <td>No</td>
    </tr>
    <tr>
      <td>noCache</td>
      <td>
        Setting this property to true disables Incremental Analysis, even if <i>cacheLocation</i> is provided.
        You can use this to explicitly turn off suggestions to use incremental analysis, or for testing purposes.
      </td>
      <td>No</td>
    </tr>
    <tr>
      <td>threads</td>
      <td>
        Sets the number of threads used by PMD. Set threads to <code>0</code> to disable multi-threading processing.
        Default: 1
      </td>
      <td>No</td>
    </tr>
</table>


`formatter` nested element - specifies the format of and the files to which the report is written. You can
configure multiple formatters.

<table>
<tr><th>Name</th><th>Values</th></tr>
<tr>
   <td>type</td>
   <td>xml,ideaj,textcolor,text,textpad,emacs,csv,html,xslt,yahtml,summaryhtml,vbhtml,codeclimate</td>
</tr>
<tr>
 <td>showSuppressed</td>
 <td>Whether to show suppressed warnings; "false" is the default.</td>
</tr>
<tr>
   <td>toFile</td>
   <td>A filename to which to write the report</td>
</tr>
<tr>
   <td>toConsole</td>
   <td>Whether to output the report to the console; "false" is the default.</td>
</tr>
<tr>
   <td colspan="2">
       <p>The <code>formatter</code> element can contain nested <code>param</code> elements to configure the formatter in detail, e.g.</p>
       <dl>
       <dt>encoding</dt>
       <dd>Specifies the encoding to be used in the generated report (only honored when used with `toFile`). When rendering `toConsole` PMD will automatically detect the terminal's encoding and use it, unless the output is being redirected / piped, in which case `file.encoding` is used. See example below.</dd>
       <dt>linkPrefix</dt>
       <dd>Used for linking to online HTMLized source (like <a href="https://maven.apache.org/plugins/maven-pmd-plugin/xref/org/apache/maven/plugins/pmd/PmdReport.html">this</a>). See example below. Note, this only works with
       <a href="https://maven.apache.org/jxr/maven-jxr-plugin/index.html">maven-jxr-plugin</a>.</dd>
       <dt>linePrefix</dt>
       <dd>Used for linking to online HTMLized source (like <a href="https://maven.apache.org/plugins/maven-pmd-plugin/xref/org/apache/maven/plugins/pmd/PmdReport.html#L375">this</a>). See example below. Note, this only works with <a href="https://maven.apache.org/jxr/maven-jxr-plugin/index.html">maven-jxr-plugin</a>.</dd>
       </dl>
   </td>
</tr>
</table>


`classpath` nested element - useful for specifying custom rules. More details on the `classpath`
element are in the Ant documentation for [path-like structures](https://ant.apache.org/manual/using.html#path) and there's
an example below.

`auxclasspath` nested element - extra classpath used for type resolution. Some rules make use of type resolution
in order to avoid false positives. The `auxclasspath` is configured also with [path-like structures](https://ant.apache.org/manual/using.html#path). It should contain the compiled classes of the project that is being analyzed and all the compile time
dependencies.

`sourceLanguage` nested element - specify which language (Java, Ecmascript, XML,...)
and the associated version (1.5, 1.6,...). This element is optional. The language is determined by file extension
automatically and the latest language version is used.

`ruleset` nested element - another way to specify rulesets. You can specify multiple elements. Here's an example:

    <target name="pmd">
        <pmd>
            <ruleset>rulesets/java/quickstart.xml</ruleset>
            <ruleset>config/my-ruleset.xml</ruleset>
            <fileset dir="/usr/local/j2sdk1.4.1_01/src/">
                <include name="java/lang/*.java"/>
            </fileset>
        </pmd>
    </target>

`fileset` nested element - specify the actual java source files, that PMD should analyze. You can use multiple
fileset elements. See [FileSet](https://ant.apache.org/manual/Types/fileset.html) for the syntax and usage.

`relativizePathsWith` nested element - configures the paths relative to which directories are rendered in the report.
This option allows shortening directories in the report; without it, paths are rendered as absolute paths.
The option can be repeated, in which case the shortest relative path will be used.
It is a [path-like structure](https://ant.apache.org/manual/using.html#path).

### Language version selection

PMD selects the language automatically using the file extension. If multiple versions of a language are
supported, PMD uses the latest version as default. This is currently the case for Java only, which has
support for multiple versions.

If a languages supports multiple versions, you can select a specific version here, so that e.g. rules, that only apply
to specific versions, are not executed. E.g. the rule {% rule "java/bestpractices/UseTryWithResources" %} only makes
sense with Java 1.7 and later. If your project uses Java 1.5, then you should configure the `sourceLanguage`
accordingly and this rule won't be executed.

The specific version of a language to be used is selected via the `sourceLanguage`
nested element. Example:

    <sourceLanguage name="java" version="23"/>

The available versions depend on the language. You can get a list of the currently supported language versions
via the CLI option `--help`.

### Postprocessing the report file with XSLT

Several folks (most recently, Wouter Zelle) have written XSLT scripts
which you can use to transform the XML report into nifty HTML.  To do this,
make sure you use the XML formatter in the PMD task invocation, i.e.:

    <formatter type="xml" toFile="${tempbuild}/report_pmd.xml">
        <param name="encoding" value="UTF-8" /> <!-- enforce UTF-8 encoding for the XML -->
    </formatter>

Then, after the end of the PMD task, do this:

    <xslt in="${tempbuild}/report_pmd.xml" style="${pmdConfig}/wz-pmd-report.xslt" out="${pmdOutput}/report_pmd.html" />

### Examples

#### One ruleset

Running one ruleset to produce a HTML report (and printing the report to the console as well) using a file cache

    <target name="pmd">
        <pmd rulesetfiles="rulesets/java/quickstart.xml" cacheLocation="build/pmd/pmd.cache">
            <formatter type="html" toFile="pmd_report.html" toConsole="true"/>
            <fileset dir="C:\j2sdk1.4.1_01\src\java\lang\">
                <include name="**/*.java"/>
            </fileset>
        </pmd>
    </target>

#### Multiple rulesets

Running multiple rulesets to produce an XML report with the same analysis cache

    <target name="pmd">
        <pmd rulesetfiles="rulesets/java/quickstart.xml,config/my-ruleset.xml" cacheLocation="build/pmd/pmd.cache">
            <formatter type="xml" toFile="c:\pmd_report.xml"/>
            <fileset dir="C:\j2sdk1.4.1_01\src\java\lang\">
                <include name="**/*.java"/>
            </fileset>
        </pmd>
    </target>

#### Custom renderer

Using a custom renderer. For this to work, you need to add you custom renderer to the classpath of PMD. This
need to be configured when defining the task:

    <path id="pmd.classpath">
        <fileset dir="/home/joe/pmd-bin-{{site.pmd.version}}/lib">
            <include name="*.jar"/>
        </fileset>
        <!-- the custom renderer is expected to be in /home/joe/pmd-addons/com/company/MyRenderer.class -->
        <pathelement location="/home/joe/pmd-addons" />
    </path>
    <taskdef name="pmd" classname="net.sourceforge.pmd.ant.PMDTask" classpathref="pmd.classpath" />

    <target name="pmd">
        <pmd rulesetfiles="rulesets/java/quickstart.xml">
            <formatter type="com.mycompany.MyRenderer" toFile="foo.html"/>
            <fileset dir="/path/to/java/src">
                <include name="**/*.java"/>
            </fileset>
        </pmd>
    </target>

#### Full example with auxclasspath

Full build file example using the correct auxclasspath configuration.
Your project needs to be compiled first which happens in the target "compile":

    <project name="MyProject" default="pmd" basedir=".">
        <property name="src" location="src"/>
        <property name="build" location="build"/>
        <path id="project.dependencies">
            <pathelement location="lib/third-party.jar"/>
            <pathelement location="lib/xyz.jar"/>
        </path>
        <path id="pmd.classpath">
            <fileset dir="/home/joe/pmd-bin-{{site.pmd.version}}/lib">
                <include name="*.jar"/>
            </fileset>
        </path>
        <taskdef name="pmd" classname="net.sourceforge.pmd.ant.PMDTask" classpathref="pmd.classpath" />
        
        <target name="init">
            <mkdir dir="${build}"/>
        </target>
        
        <target name="compile" depends="init">
            <javac srcdir="${src}" destdir="${build}" classpathref="project.dependencies"
                source="1.8" target="1.8" />
        </target>
        
        <target name="pmd" depends="compile">
            <pmd cacheLocation="${build}/pmd.cache">
                <auxclasspath>
                    <pathelement location="${build}"/>
                    <path refid="project.dependencies"/>
                </auxclasspath>
                <ruleset>rulesets/java/quickstart.xml</ruleset>
                <formatter type="html" toFile="${build}/pmd_report.html"/>
                <sourceLanguage name="java" version="1.8"/>
                <fileset dir="${src}">
                    <include name="**/*.java"/>
                </fileset>
            </pmd>
        </target>
        
        <target name="clean">
            <delete dir="${build}"/>
        </target>
    </project>

You can run pmd then with `ant pmd`.

#### Getting verbose output

    [tom@hal bin]$ ant -v pmd
    Apache Ant version 1.6.2 compiled on July 16 2004
    Buildfile: build.xml
    Detected Java version: 1.4 in: /usr/local/j2sdk1.4.2_03/jre
    Detected OS: Linux
    parsing buildfile build.xml with URI = file:/home/tom/data/pmd/pmd/bin/build.xml
    Project base dir set to: /home/tom/data/pmd/pmd
    Build sequence for target `pmd' is [pmd]
    Complete build sequence is [pmd, copy, cppjavacc, cpd, delete,
     compile, clean, jar, dist, cpdjnlp, jjtree, javadoc, test, tomserver]

    pmd:
          [pmd] Using the normal ClassLoader
          [pmd] Using these rulesets: rulesets/java/quickstart.xml
          [pmd] Using rule AvoidMessageDigestField
          [pmd] Using rule AvoidStringBufferField
          [pmd] Using rule AvoidUsingHardCodedIP
          [pmd] Using rule CheckResultSet
          [pmd] Using rule ConstantsInInterface
          ...
          [pmd] Processing file /usr/local/java/src/java/lang/ref/Finalizer.java
          [pmd] Processing file /usr/local/java/src/java/lang/ref/FinalReference.java
          [pmd] Processing file /usr/local/java/src/java/lang/ref/PhantomReference.java
          [pmd] Processing file /usr/local/java/src/java/lang/ref/Reference.java
          [pmd] Processing file /usr/local/java/src/java/lang/ref/ReferenceQueue.java
          [pmd] Processing file /usr/local/java/src/java/lang/ref/SoftReference.java
          [pmd] Processing file /usr/local/java/src/java/lang/ref/WeakReference.java
          [pmd] 0 problems found

    BUILD SUCCESSFUL
    Total time: 2 seconds
    [tom@hal bin]$

#### HTML report with linkPrefix

An HTML report with the "linkPrefix" and "linePrefix" properties:

    <target name="pmd">
        <pmd rulesetfiles="rulesets/java/quickstart.xml">
            <formatter type="html" toFile="pmd_report.html">
                <param name="linkPrefix" value="https://maven.apache.org/plugins/maven-pmd-plugin/xref/"/>
                <param name="linePrefix" value="L"/>
            </formatter>
            <fileset dir="/usr/local/j2sdk1.4.1_01/src/">
                <include name="java/lang/*.java"/>
            </fileset>
            <relativizePathsWith>
                <pathelement location="/usr/local/j2sdk1.4.1_01/src/"/>
            </relativizePathsWith>
        </pmd>
    </target>

### Memory Usage

Memory usage has been reduced significantly starting with the PMD 4.0 release.
When testing all Java rules on the jdk 1.6 source code (about 7000 classes),
the allocated heap space does not go over 60M.

However, on very large projects, the Ant task may still fail with a OutOfMemoryError.
To prevent this from happening, increase the maximum memory usable by ant using the ANT_OPTS variable
(adjust the size according to your available memory):

On Windows:

    set ANT_OPTS=-Xmx1024m -Xms512m

On Linux

    export ANT_OPTS="-Xmx1024m -Xms512m"
