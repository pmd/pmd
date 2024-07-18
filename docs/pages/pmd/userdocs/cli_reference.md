---
title: PMD CLI reference
summary: "Full reference for PMD's command-line interface, including options, output formats and supported languages"
tags: [userdocs]
keywords: [command, line, options, help, formats, renderers]
permalink: pmd_userdocs_cli_reference.html
author: Tom Copeland <tom@infoether.com>, Xavier Le Vourch <xlv@users.sourceforge.net>, Juan Martín Sotuyo Dodero <juansotuyo@gmail.com>
last_updated: June 2024 (7.3.0)
---


## Options

The tool comes with a rather extensive help text, simply running with `--help`!

<table>
    <tr>
        <th>Option</th>
        <th>Description</th>
        <th>Default value</th>
        <th>Applies to</th>
    </tr>
    {% include custom/cli_option_row.html options="--rulesets,-R"
               option_arg="refs"
               description="Path to a ruleset xml file. The path may reference
                              a resource on the classpath of the application,
                              be a local file system path, or a URL. The option
                              can be repeated, and multiple arguments separated
                              by comma can be provided to a single occurrence
                              of the option."
               required="yes"
    %}
    {% include custom/cli_option_row.html options="--dir,-d"
               option_arg="path"
               description="Path to a source file, or directory containing
                              source files to analyze. Zip and Jar files are
                              also supported, if they are specified directly
                              (archive files found while exploring a directory
                              are not recursively expanded). This option can be
                              repeated, and multiple arguments can be provided
                              to a single occurrence of the option. One of
                              `--dir`, `--file-list` or `--uri` must be provided."
    %}
    {% include custom/cli_option_row.html options="--format,-f"
               option_arg="format"
               description="Output format of the analysis report. The available formats
                            are described [here](#available-report-formats)."
               default="text"
    %}
     <tr><td/><td/><td/><td/></tr>
    {% include custom/cli_option_row.html options="--aux-classpath"
               option_arg="cp"
               description="Specifies the classpath for libraries used by the source code.
               This is used to resolve types in source files. The platform specific path delimiter
               (\":\" on Linux, \";\" on Windows) is used to separate the entries.
               Alternatively, a single `file:` URL
               to a text file containing path elements on consecutive lines can be specified.
               <p>See also [Providing the auxiliary classpath](pmd_languages_java.html#providing-the-auxiliary-classpath).</p>"
               languages="Java"
    %}
    {% include custom/cli_option_row.html options="--benchmark,-b"
               description="Enables benchmark mode, which outputs a benchmark report upon completion.
                            The report is sent to standard error."
    %}
    {% include custom/cli_option_row.html options="--cache"
               option_arg="filepath"
               description="Specify the location of the cache file for incremental analysis.
                            This should be the full path to the file, including the desired file name (not just the parent directory).
                            If the file doesn't exist, it will be created on the first run. The file will be overwritten on each run
                            with the most up-to-date rule violations.
                            This can greatly improve analysis performance and is **highly recommended**."
    %}
    {% include custom/cli_option_row.html options="--debug,--verbose,-D,-v"
               description="Debug mode. Prints more log output. See also [Logging](#logging)."
    %}
    {% include custom/cli_option_row.html options="--encoding,-e"
               option_arg="charset"
               description="Specifies the character set encoding of the source code files PMD is reading.
                            The valid values are the standard character sets of `java.nio.charset.Charset`."
               default="UTF-8"
    %}
    {% include custom/cli_option_row.html options="--[no-]fail-on-error"
               description="Specifies whether PMD exits with non-zero status if recoverable errors occurred.
                            By default PMD exits with status 5 if recoverable errors occurred (whether there are violations or not).
                            Disable this option with `--no-fail-on-error` to exit with 0 instead. In any case, a report with the found violations will be written."
    %}
    {% include custom/cli_option_row.html options="--[no-]fail-on-violation"
               description="Specifies whether PMD exits with non-zero status if violations are found.
                            By default PMD exits with status 4 if violations are found.
                            Disable this feature with `--no-fail-on-violation` to exit with 0 instead. In any case a report with the found violations will be written."
    %}
    {% include custom/cli_option_row.html options="--file-list"
               option_arg="filepath"
               description="Path to a file containing a list of files to
                              analyze, one path per line. One of `--dir`,
                              `--file-list` or `--uri` must be provided."
    %}
    {% include custom/cli_option_row.html options="--force-language"
               option_arg="lang"
               description="Force a language to be used for all input files, irrespective of
                            file names. When using this option, the automatic language selection
                            by extension is disabled and PMD tries to parse all files with
                            the given language `&lt;lang&gt;`. Parsing errors are ignored and unparsable files
                            are skipped.
                            <p>Use `--use-version` to specify the language version to use, if it is not the default.</p>
                            <p>This option allows to use the xml language for files, that don't
                            use xml as extension. See [example](#analyze-other-xml-formats) below.</p>"
    %}
    {% include custom/cli_option_row.html options="--ignore-list"
               option_arg="filepath"
               description="Path to file containing a list of files to ignore, one path per line.
                            This option overrides files included by any of `--dir`, `--file-list` and `--uri`."
    %}
    {% include custom/cli_option_row.html options="--help,-h"
               description="Display help on usage."
    %}
    {% include custom/cli_option_row.html options="--use-version"
               option_arg="lang-version"
               description="The specific language version PMD should use when parsing source code for a given language.
                            <p>Values are in the format of *language-version*.</p>
                            <p>This option can be repeated to configure several languages for the same run.</p>
                            <p>Note that this option does not change how languages are assigned to files.
                            It only changes something if the project you analyze contains some files that PMD detects as the given language.
                            Language detection is only influenced by file extensions and the `--force-language` option.</p>
                            <p>See also [Supported Languages](#supported-languages).</p>"
    %}
    {% include custom/cli_option_row.html options="--minimum-priority"
               option_arg="priority"
               description="Rule priority threshold; rules with lower priority than configured here won't be used.
                            Valid values (case-insensitive): High, Medium_High, Medium, Medium_Low, Low.
                            An integer between 1 (High) and 5 (Low) is also supported. See [Configuring rules](pmd_userdocs_configuring_rules.html)
                            on how to override priorities in custom rulesets."
               default="Low"
    %}
    {% include custom/cli_option_row.html options="--no-ruleset-compatibility"
               description='Disable automatic fixing of invalid rule references. Without the switch, PMD tries to automatically replace rule references that point to moved or renamed rules with the newer location if possible. Disabling it is not recommended.'
    %}
    {% include custom/cli_option_row.html options="--no-cache"
               description="Explicitly disables incremental analysis. This switch turns off suggestions to use Incremental Analysis,
               and causes the `--cache` option to be discarded if it is provided."
    %}
    {% include custom/cli_option_row.html options="--[no-]progress"
               description="Enables / disable progress bar indicator of live analysis progress. This ie enabled by default."
    %}
    {% include custom/cli_option_row.html options="--property,-P"
               option_arg="name&gt;=&lt;value"
               description="Specifies a property for the report renderer. The option can be specified several times.
                           <p>Using `--help` will provide a complete list of supported properties for each report format</p>"
    %}
    {% include custom/cli_option_row.html options="--relativize-paths-with,-z"
               option_arg="path"
               description="Path relative to which directories are rendered in the report. This option allows
                    shortening directories in the report; without it, paths are rendered as mentioned in the source directory (option \"--dir\").
                    The option can be repeated, in which case the shortest relative path will be used.
                    If the root path is mentioned (e.g. \"/\" or \"C:\\\"), then the paths will be rendered as absolute."
    %}
    {% include custom/cli_option_row.html options="--report-file,-r"
               option_arg="path"
               description="Path to a file to which report output is written. The file is created if it does not exist. If this option is not specified, the report is rendered to standard output."
    %}
    {% include custom/cli_option_row.html options="--show-suppressed"
               description="Causes the suppressed rule violations to be added to the report."
    %}
    {% include custom/cli_option_row.html options="--suppress-marker"
               option_arg="marker"
               description="Specifies the comment token that marks lines which PMD should ignore."
               default="NOPMD"
    %}
    {% include custom/cli_option_row.html options="--threads,-t"
               option_arg="num"
               description="Sets the number of threads used by PMD.
                            Set threads to `0` to disable multi-threading processing."
               default="1"
    %}
    {% include custom/cli_option_row.html options="--uri,-u"
                   option_arg="uri"
                   description="Database URI for sources.  One of `--dir`, `--file-list` or `--uri` must be provided."
                   languages="PLSQL"
    %}
</table>

## Additional Java Runtime Options

PMD is executed via a Java runtime. In some cases, you might need to set additional runtime options, e.g.
if you want to analyze a project, that uses one of OpenJDK's [JEP 12: Preview Language Features](https://openjdk.org/jeps/12).

Just set the environment variable `PMD_JAVA_OPTS` before executing PMD, e.g.

{% include cli_example.html
   id="preview"
   linux="export PMD_JAVA_OPTS=\"--enable-preview\"
    pmd check -d src/main/java/ -f text -R rulesets/java/quickstart.xml"
   windows="set \"PMD_JAVA_OPTS=--enable-preview\"
    pmd.bat check -d src\main\java\ -f text -R rulesets/java/quickstart.xml" %}

## Additional runtime classpath

If you develop custom rules and package them as a jar file, you need to add it to PMD's runtime classpath.
You can either copy the jar file into the `lib/` subfolder alongside the other jar files, that are in PMD's
standard distribution.

Or you can set the environment variable `CLASSPATH` before starting PMD, e.g.

{% include cli_example.html
   id="preview_classpath"
   linux="export CLASSPATH=custom-rule-example.jar
    pmd check -d ../../../src/main/java/ -f text -R myrule.xml"
   windows="set CLASSPATH=custom-rule-example.jar
    pmd.bat check -d ..\..\..\src\main\java\ -f text -R myrule.xml" %}

## Exit Status

Please note that if PMD detects any violations, it will exit with status 4 (since 5.3) or 5 (since 7.3.0).
This behavior has been introduced to ease PMD integration into scripts or hooks, such as SVN hooks.

<table>
<tr><td>0</td><td>Everything is fine, no violations found and no recoverable error occurred.</td></tr>
<tr><td>1</td><td>PMD exited with an exception.</td></tr>
<tr><td>2</td><td>Usage error. Command-line parameters are invalid or missing.</td></tr>
<tr><td>4</td><td>At least one violation has been detected, unless <code>--no-fail-on-violation</code> is set.<p>Since PMD 5.3.</p></td></tr>
<tr><td>5</td><td>At least one recoverable error has occurred. There might be additionally zero or more violations detected.
    To ignore recoverable errors, use <code>--no-fail-on-error</code>.<p>Since PMD 7.3.0.</p></td></tr>
</table>

{%include note.html content="If PMD exits with 5, then PMD had either trouble parsing one or more files or a rule failed with an exception.
That means, that either no violations for the entire file or for that rule are reported. These cases can be considered as false-negatives.
In any case, the root cause should be investigated. If it's a problem in PMD itself, please create a bug report. Recoverable errors
are usually part of the generated PMD report." %}

## Logging

PMD internally uses [slf4j](https://www.slf4j.org/) and ships with slf4j-simple as the logging implementation.
Logging messages are printed to System.err, that's why you should use `--report-file` to specify an output for
the report and not rely on redirecting the console output.

The configuration for slf4j-simple is in the file `conf/simplelogger.properties`. There you can enable
logging of specific classes if needed. The `--debug` command line option configures the default log level
to be "debug".

## Supported Languages

The language is determined automatically by PMD from the file extensions. Some languages such as "Java"
however support multiple versions. The default version will be used, which is usually the latest supported
non-preview version. If you want to use an older version, so that e.g. rules that suggest usage of language features
that are not available yet won't be executed, you need to specify a specific version via the `--use-version`
parameter.

The selected language version can also influence which rules are applied. Some rules might be relevant for
just a specific version of the language. Such rules are marked with either `minimumLanguageVersion` or
`maximumLanguageVersion` or both. Most rules apply for all language versions.

These parameters are most of the time irrelevant, if the rules apply for all versions.

The available versions depend on the language. You can get a list of the currently supported language versions
via the CLI option `--help`.

Example:

{% include cli_example.html
   id="lang-ver"
   linux="pmd check -d src/main/java -f text -R rulesets/java/quickstart.xml --use-version java-1.8"
   windows="pmd.bat check -d src\main\java -f text -R rulesets/java/quickstart.xml --use-version java-1.8" %}

*   [apex](pmd_rules_apex.html) (Salesforce Apex)
*   [ecmascript](pmd_rules_ecmascript.html) (JavaScript)
*   [html](pmd_rules_html.html)
*   [java](pmd_rules_java.html)
    *   [Supported Versions](pmd_languages_java.html)
*   [ecmascript](pmd_rules_ecmascript.html) (JavaScript)
*   [jsp](pmd_rules_jsp.html)
*   [kotlin](pmd_rules_kotlin.html)
*   [modelica](pmd_rules_modelica.html)
*   [plsql](pmd_rules_plsql.html)
*   [pom](pmd_rules_pom.html) (Maven POM)
*   [scala](pmd_rules_scala.html)
*   [swift](pmd_rules_swift.html)
*   [velocity](pmd_rules_velocity.html) (Apache Velocity Template Language)
*   [visualforce](pmd_rules_visualforce.html) (Salesforce VisualForce)
*   [xml](pmd_rules_xml.html)
*   [xsl](pmd_rules_xsl.html)

## Available Report Formats

PMD comes with many different renderers.
All formats are described at [PMD Report formats](pmd_userdocs_report_formats.html)

## Examples

### Analyze other xml formats

If your xml language doesn't use `xml` as file extension, you can still use PMD with `--force-language`:

{% include cli_example.html
   id="force"
   linux="pmd check -d src/xml-file.ext -f text -R ruleset.xml --force-language xml"
   windows="pmd.bat check -d src\xml-file.ext -f text -R ruleset.xml --force-language xml" %}

You can also specify a directory instead of a single file. Then all files are analyzed. In that case,
parse errors are suppressed in order to reduce irrelevant noise:

{% include cli_example.html
   id="force-dir"
   linux="pmd check -d src/ -f text -R ruleset.xml --force-language xml"
   windows="pmd.bat check -d src\ -f text -R ruleset.xml --force-language xml" %}

Alternatively, you can create a filelist to only analyze files with a given extension:

{% include cli_example.html
   id="file-list"
   linux="find src/ -name \"*.ext\" > filelist.txt
     pmd check --file-list filelist.txt -f text -R ruleset.xml --force-language xml"
   windows="for /r src\ %i in (*.ext) do echo %i >> filelist.txt
     pmd.bat check --file-list filelist.txt -f text -R ruleset.xml --force-language xml" %}

