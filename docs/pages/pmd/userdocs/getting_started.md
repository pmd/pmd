---
title: Getting Started
permalink: pmd_userdocs_getting_started.html
author: Tom Copeland <tom@infoether.com>, Xavier Le Vourch <xlv@users.sourceforge.net>, Juan Martín Sotuyo Dodero <juansotuyo@gmail.com>
---

## How to install PMD and CPD

### Windows

Requirements:

*   [Java JRE](http://www.oracle.com/technetwork/java/javase/downloads/index.html) 1.7 or higher
*   [Winzip](http://winzip.com) or the free [7-zip](http://www.7-zip.org/)

Download the latest binary distribution from [the github releases page](https://github.com/pmd/pmd/releases).

Unzip it into any directory, i.e., c:\pmd\

### Linux / Unix

Requirements:

*   [Java JRE](http://www.oracle.com/technetwork/java/javase/downloads/index.html) 1.7 or higher
*   The Unix "zip" utility [InfoZip](http://www.info-zip.org/pub/infozip/)

Download the latest binary distribution from [the github releases page](https://github.com/pmd/pmd/releases).

Unzip it into any directory:

    [tom@hal tmp]$ unzip -q pmd-bin-{{site.pmd.version}}.zip
    [tom@hal tmp]$ ls -l
    total 4640
    drwxrwxr-x    5 tom      tom          4096 Apr 17 16:38 pmd-bin-{{site.pmd.version}}
    -rw-rw-r--    1 tom      tom       4733312 Jun  9 15:44 pmd-bin-{{site.pmd.version}}.zip
    [tom@hal tmp]$


Note that the PMD binary distribution file contains both [PMD](#running-pmd-via-command-line) and [CPD](pmd_userdocs_cpd.html).


## Running PMD via command line

### On Linux and other UNIX based operating system...

PMD comes with several command line utilities. Previously, each of them had its own start up script, but this has been
greatly simplified since PMD 5.0. ... at least for Unix systems. There is now only one script, called "run.sh", inside
the `bin/` directory of PMD distribution.

The first argument is the name of the utility you want to execute ('pmd', 'designer', ...) and the other arguments are
specific to the utility used.

    $ ./bin/run.sh pmd -d ../../../src/main/java/ -f text -R rulesets/java/basic.xml  -version 1.7 -language java
    .../src/main/java/net/sourceforge/pmd/RuleSet.java:123  These nested if statements could be combined
    .../src/main/java/net/sourceforge/pmd/RuleSet.java:231  Useless parentheses.
    .../src/main/java/net/sourceforge/pmd/RuleSet.java:232  Useless parentheses.
    .../src/main/java/net/sourceforge/pmd/RuleSet.java:357  These nested if statements could be combined
    .../src/main/java/net/sourceforge/pmd/RuleSetWriter.java:66     Avoid empty catch blocks
    .../src/main/java/net/sourceforge/pmd/RuleSetWriter.java:269    Useless parentheses.

Type "./run.sh pmd -d \[filename\|jar or zip file containing source code\|directory] -f \[report format] -R \[ruleset file]", i.e:

    /home/user/tmp/pmd-bin-{{site.pmd.version}}/pmd/bin>./run.sh pmd -d /home/user/data/pmd/pmd/test-data/Unused1.java -f xml -R rulesets/java/unusedcode.xml
    <?xml version="1.0"?><pmd>
    <file name="/home/user/data/pmd/pmd/test-data/Unused1.java">
    <violation line="5" rule="UnusedLocalVariable">
    Avoid unused local variables such as 'fr'
    </violation>
    </file></pmd>

    /home/user/tmp/pmd-bin-{{site.pmd.version}}/pmd/bin>


### Basic usage for Windows

You can find PMD's starter batch file `pmd.bat` in the `bin` subdirectory.

Type "pmd -d \[filename\|jar or zip file containing source code\|directory] -f \[report format] -R \[ruleset file]", i.e:

    C:\tmp\pmd-bin-{{site.pmd.version}}<\pmd\bin>pmd -d c:\data\pmd\pmd\test-data\Unused1.java -f xml -R rulesets/java/unusedcode.xml
    <?xml version="1.0"?><pmd>
    <file name="c:\data\pmd\pmd\test-data\Unused1.java">
    <violation line="5" rule="UnusedLocalVariable">
    Avoid unused local variables such as 'fr'
    </violation>
    </file></pmd>

    C:\tmp\pmd-bin-{{site.pmd.version}}\pmd\bin>

You can pass a file name, a directory name, or a jar or zip file name containing Java source code to PMD.

Also, the PMD binary distribution includes the ruleset files
inside the jar file - even though the "rulesets/java/unusedcode.xml" parameter
above looks like a filesystem reference, it's really being used by a getResourceAsStream() call
to load it out of the PMD jar file.

### Options

The tool comes with a rather extensive help text, simply running with `-help`!

<table>
    <tr>
        <th>Option</th>
        <th>Description</th>
        <th>Required</th>
        <th>Applies for language</th>
    </tr>
    <tr>
        <td>-rulesets / -R</td>
        <td>Comma separated list of ruleset names to use</td>
        <td>yes</td>
        <td></td>
    </tr>
    <tr>
        <td>-dir / -d</td>
        <td>Root directory for sources</td>
        <td>yes</td>
        <td></td>
    </tr>
    <tr>
        <td>-format / -f</td>
        <td>Report format type. Default format is `text`.</td>
        <td>no</td>
        <td></td>
    </tr>
    <tr>
        <td>-auxclasspath</td>
        <td>
            Specifies the classpath for libraries used by the source code. This is used by the type resolution.
            Alternatively a `file://` URL to a text file containing path elements on consecutive lines can be
            specified.
        </td>
        <td>no</td>
        <td></td>
    </tr>
    <tr>
        <td>-uri / -u</td>
        <td>Database URI for sources. If this is given, then you don't need to provide `-dir`.</td>
        <td>no</td>
        <td>plsql</td>
    </tr>
    <tr>
        <td>-filelist</td>
        <td>Path to file containing a comma delimited list of files to analyze. If this is given, then you don't need to provide `-dir`.</td>
        <td>no</td>
        <td></td>
    </tr>
    <tr>
        <td>-debug / -verbose / -D / -V</td>
        <td>Debug mode. Prints more log output.</td>
        <td>no</td>
        <td></td>
    </tr>
    <tr>
        <td>-help / -h / -H</td>
        <td>Display help on usage.</td>
        <td>no</td>
        <td></td>
    </tr>
    <tr>
        <td>-encoding / -e</td>
        <td>Specifies the character set encoding of the source code files PMD is reading (i.e. UTF-8). Default is `UTF-8`.</td>
        <td>no</td>
        <td></td>
    </tr>
    <tr>
        <td>-threads / -t</td>
        <td>Sets the number of threads used by PMD. Default is `1`. Set threads to '0' to disable multi-threading processing.</td>
        <td>no</td>
        <td></td>
    </tr>
    <tr>
        <td>-benchmark / -b</td>
        <td>Benchmark mode - output a benchmark report upon completion; defaults to System.err</td>
        <td>no</td>
        <td></td>
    </tr>
    <tr>
        <td>-stress / -S</td>
        <td>Performs a stress test.</td>
        <td>no</td>
        <td></td>
    </tr>
    <tr>
        <td>-shortnames</td>
        <td>Prints shortened filenames in the report.</td>
        <td>no</td>
        <td></td>
    </tr>
    <tr>
        <td>-showsuppressed</td>
        <td>Report should show suppressed rule violations.</td>
        <td>no</td>
        <td></td>
    </tr>
    <tr>
        <td>-suppressmarker</td>
        <td>Specifies the string that marks the line which PMD should ignore; default is `NOPMD`.</td>
        <td>no</td>
        <td></td>
    </tr>
    <tr>
        <td>-minimumpriority / -min</td>
        <td>Rule priority threshold; rules with lower priority than configured here won't be used. Default is `5` - which is the lowest priority.</td>
        <td>no</td>
        <td></td>
    </tr>
    <tr>
        <td>-property / -P</td>
        <td>`{name}={value}`: Define a property for a report format.</td>
        <td>no</td>
        <td></td>
    </tr>
    <tr>
        <td>-reportfile / -r</td>
        <td>Send report output to a file; default to System.out</td>
        <td>no</td>
        <td></td>
    </tr>
    <tr>
        <td>-version / -v</td>
        <td>Specify version of a language PMD should use.</td>
        <td>no</td>
        <td></td>
    </tr>
    <tr>
        <td>-language / -l</td>
        <td>Specify a language PMD should use.</td>
        <td>no</td>
        <td></td>
    </tr>
    <tr>
        <td>-failOnViolation {true|false}</td>
        <td>By default PMD exits with status 4 if violations are found.
            Disable this option with '-failOnViolation false' to exit with 0 instead and just write the report.
        </td>
        <td>no</td>
        <td></td>
    </tr>
    <tr>
        <td>-cache</td>
        <td>Specify a location for the analysis cache file to use.
            This can greatly improve analysis performance and is <b>highly recommended</b>.</td>
        <td>no</td>
        <td></td>
    </tr>
</table>


### Exit Status

Please note that if PMD detects any violations, it will exit with status 4 (since 5.3).
This behavior has been introduced to ease PMD integration into scripts or hooks, such as SVN hooks.

<table>
<tr><td>0</td><td>Everything is fine, now violations found</td></tr>
<tr><td>1</td><td>Couldn't understand command line parameters or PMD exited with an exception</td></tr>
<tr><td>4</td><td>At least one violation has been detected unless '-failOnViolation false' is set.</td></tr>
</table>


### Supported Languages

*   [apex](pmd_rules_apex.html) (Salesforce Apex)
*   [java](pmd_rules_java.html)
*   [ecmascript](pmd_rules_javascript.html) (JavaScript)
*   [jsp](pmd_rules_jsp.html)
*   [plsql](pmd_rules_plsql.html)
*   [vf](pmd_rules_vf.html) (Salesforce VisualForce)
*   [vm](pmd_rules_vm.html) (Apache Velocity)
*   [xml and xsl](/pmd_rules_xml.html)


### Available Report Formats / Renderers

PMD comes with many different renderer types:

*   **codeclimate**: Renderer for Code Climate JSON format.

*   **csv**: Comma-separated values tabular format.

    Properties:

    *   problem: Include problem column. Default: true.
    *   package: Include package column. Default: true.
    *   file: Include file column. Default: true.
    *   priority: Include priority column. Default: true.
    *   line: Include line column. Default: true.
    *   desc: Include description column. Default: true.
    *   ruleSet: Include Rule set column. Default: true.
    *   rule: Include Rule column. Default: true.

*   **emacs**: GNU Emacs integration.

*   **html**: HTML format.

    Properties:

    *   linePrefix: Prefix for line number anchor in the source file.
    *   linkPrefix: Path to HTML source.

*   **ideaj**: IntelliJ IDEA integration.

    Properties:

    *   classAndMethodName: Class and method name, pass '.method' when processing a directory.
    *   sourcePath:
    *   fileName:

*   **summaryhtml**: Summary HTML format.

    Properties:

    *   linePrefix: Prefix for line number anchor in the source file.
    *   linkPrefix: Path to HTML source.

*   **text**: Text format.

*   **textcolor**: Text format, with color support (requires ANSI console support, e.g. xterm, rxvt, etc.).

    Properties:

    *   color: Enables colors with anything other than 'false' or '0'. Default: yes.

*   **textpad**: TextPad integration.

*   **vbhtml**: Vladimir Bossicard HTML format.

*   **xml**: XML format.

    Properties:

    *   encoding: XML encoding format, defaults to UTF-8.

*   **xslt**: XML with a XSL transformation applied.

    Properties:

    *   encoding: XML encoding format, defaults to UTF-8.
    *   xsltFilename: The XSLT file name.

*   **yahtml**: Yet Another HTML format.

    Properties:

    *   outputDir: Output directory.


### Incremental Analysis

Ever since PMD 5.6.0, PMD has been able to perform Incremental Analysis.

When performing Incremental Analysis for the first time, PMD will cache analysis data and results.
This allows subsequent analysis to only look into those files that are new / have changed. For
a typical development environment, where you are only changing a few files at a time, this can
reduce analysis time dramatically.

The generated report will be *exactly the same* as it would if running without incremental analysis.
Files included in the final report will reflect exactly those files in your filesystem. Even if
untouched, files with violations will be listed with full detail.


#### Enabling Incremental Analysis

Incremental analysis is enabled automatically once a location to store the cache has been defined.
From Command Line that is done through the `-cache` argument, but support for the feature is
available for tools integrating PMD such as [Ant](pmd_userdocs_tools_ant.html),
[Maven](pmd_userdocs_tools_maven.html), and Gradle.

