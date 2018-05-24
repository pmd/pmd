---
title: PMD CLI reference
summary: "Full reference for PMD's command-line interface, including options, output formats and supported languages"
tags: [userdocs]
permalink: pmd_userdocs_cli_reference.html
author: Tom Copeland <tom@infoether.com>, Xavier Le Vourch <xlv@users.sourceforge.net>, Juan Martín Sotuyo Dodero <juansotuyo@gmail.com>
---


## Options

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
        <td>Java</td>
    </tr>
    <tr>
        <td>-uri / -u</td>
        <td>Database URI for sources. If this is given, then you don't need to provide `-dir`.</td>
        <td>no</td>
        <td>PLSQL</td>
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
    <tr>
        <td>-no-cache</td>
        <td>Explicitly disable incremental analysis. This switch turns off suggestions to use Incremental Analysis,
            and causes the <i>-cache</i> option to be discarded if it is provided.
        </td>
        <td></td>
    </tr>
</table>


## Exit Status

Please note that if PMD detects any violations, it will exit with status 4 (since 5.3).
This behavior has been introduced to ease PMD integration into scripts or hooks, such as SVN hooks.

<table>
<tr><td>0</td><td>Everything is fine, no violations found</td></tr>
<tr><td>1</td><td>Couldn't understand command line parameters or PMD exited with an exception</td></tr>
<tr><td>4</td><td>At least one violation has been detected, unless '-failOnViolation false' is set.</td></tr>
</table>


## Supported Languages

*   [apex](pmd_rules_apex.html) (Salesforce Apex)
*   [java](pmd_rules_java.html)
*   [ecmascript](pmd_rules_javascript.html) (JavaScript)
*   [jsp](pmd_rules_jsp.html)
*   [plsql](pmd_rules_plsql.html)
*   [vf](pmd_rules_vf.html) (Salesforce VisualForce)
*   [vm](pmd_rules_vm.html) (Apache Velocity)
*   [xml and xsl](/pmd_rules_xml.html)


## Available Report Formats / Renderers

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


## Incremental Analysis

Ever since PMD 5.6.0, PMD has been able to perform Incremental Analysis.

When performing Incremental Analysis for the first time, PMD will cache analysis data and results.
This allows subsequent analysis to only look into those files that are new / have changed. For
a typical development environment, where you are only changing a few files at a time, this can
reduce analysis time dramatically.

The generated report will be *exactly the same* as it would if running without incremental analysis.
Files included in the final report will reflect exactly those files in your filesystem. Even if
untouched, files with violations will be listed with full detail.


### Enabling Incremental Analysis

Incremental analysis is enabled automatically once a location to store the cache has been defined.
From Command Line that is done through the `-cache` argument, but support for the feature is
available for tools integrating PMD such as [Ant](pmd_userdocs_tools_ant.html),
[Maven](pmd_userdocs_tools_maven.html), and Gradle.

