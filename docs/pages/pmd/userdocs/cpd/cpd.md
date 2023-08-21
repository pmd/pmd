---
title: Finding duplicated code with CPD
tags: [cpd, userdocs]
summary: "Learn how to use CPD, the copy-paste detector shipped with PMD."
permalink: pmd_userdocs_cpd.html
author: Tom Copeland <tom@infoether.com>
---

## Overview

Duplicate code can be hard to find, especially in a large project.
But PMD's **Copy/Paste Detector (CPD)** can find it for you!

CPD works with Java, JSP, C/C++, C#, Go, Kotlin, Ruby, Swift and [many more languages](#supported-languages).
It can be used via [command-line](#cli-usage), or via an [Ant task](#ant-task).
It can also be run with Maven by using the `cpd-check` goal on the [Maven PMD Plugin](pmd_userdocs_tools_maven.html).


Your own language is missing?
See how to add it [here](pmd_devdocs_major_adding_new_cpd_language.html).


### Why should you care about duplicates?

It's certainly important to know where to get CPD, and how to call it, but it's worth stepping back for a moment and asking yourself why you should care about this, being the occurrence of duplicate code blocks.

Assuming duplicated blocks of code are supposed to do the same thing, any refactoring, even simple, must be duplicated too -- which is unrewarding grunt work, and puts pressure on the developer to find every place in which to perform the refactoring. Automated tools like CPD can help with that to some extent.

However, failure to keep the code in sync may mean automated tools will no longer recognise these blocks as duplicates. This means the task of finding duplicates to keep them in sync when doing subsequent refactorings can no longer be entrusted to an automated tool -- adding more burden on the maintainer. Segments of code initially supposed to do the same thing may grow apart undetected upon further refactoring.

Now, if the code may never change in the future, then this is not a problem.

Otherwise, the most viable solution is to not duplicate. If the duplicates are already there, then they should be refactored out. We thus advise developers to use CPD to **help remove duplicates**, not to help keep duplicates in sync.

### Refactoring duplicates

Once you have located some duplicates, several refactoring strategies may apply depending of the scope and extent of the duplication. Here's a quick summary:

* If the duplication is local to a method or single class:
    * Extract a local variable if the duplicated logic is not prohibitively long
    * Extract the duplicated logic into a private method
* If the duplication occurs in siblings within a class hierarchy:
    * Extract a method and pull it up in the class hierarchy, along with common fields
    * Use the [Template Method](https://sourcemaking.com/design_patterns/template_method) design pattern
* If the duplication occurs consistently in unrelated hierarchies:
    * Introduce a common ancestor to those class hierarchies

Novice as much as advanced readers may want to [read on on Refactoring Guru](https://refactoring.guru/smells/duplicate-code) for more in-depth strategies, use cases and explanations.

## CLI Usage

### CLI options reference

<table>
    <tr>
        <th>Option</th>
        <th>Description</th>
        <th>Default</th>
        <th>Applies to</th>
    </tr>
    {% include custom/cli_option_row.html options="--minimum-tokens"
               option_arg="count"
               description="The minimum token length which should be reported as a duplicate."
               required="yes"
    %}
    {% include custom/cli_option_row.html options="--dir,-d"
               option_arg="path"
               description="Path to a source file, or directory containing
                               source files to analyze. Zip and Jar files are
                               also supported, if they are specified directly
                               (archive files found while exploring a directory
                               are not recursively expanded). This option can
                               be repeated, and multiple arguments can be
                               provided to a single occurrence of the option.
                               One of `--dir`, `--file-list` or `--uri` must be
                               provided."
    %}
    {% include custom/cli_option_row.html options="--file-list"
               option_arg="filepath"
               description="Path to a file containing a list of files to
                               analyze, one path per line. One of `--dir`,
                               `--file-list` or `--uri` must be provided."
    %}
    {% include custom/cli_option_row.html options="--language,-l"
               option_arg="lang"
               description="The source code language.
                            <p>See also [Supported Languages](#supported-languages).
                            Using `--help` will display a full list of supported languages.</p>"
               default="java"
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
    {% include custom/cli_option_row.html options="--skip-duplicate-files"
               description="Ignore multiple copies of files of the same name and length in comparison."
               default="false"
    %}
    {% include custom/cli_option_row.html options="--exclude"
               option_arg="path"
               description="Files to be excluded from the analysis"
    %}
    {% include custom/cli_option_row.html options="--non-recursive"
               description="Don't scan subdirectories"
               default="false"
    %}
    {% include custom/cli_option_row.html options="--skip-lexical-errors"
               description="Skip files which can't be tokenized due to invalid characters instead of aborting CPD"
               default="false"
    %}
    {% include custom/cli_option_row.html options="--format,-f"
               option_arg="format"
               description="Output format of the analysis report. The available formats
                            are described [here](#available-report-formats)."
               default="text"
    %}
    {% include custom/cli_option_row.html options="--[no-]fail-on-violation"
               description="Specifies whether CPD exits with non-zero status if violations are found.
                            By default CPD exits with status 4 if violations are found.
                            Disable this feature with `--no-fail-on-violation` to exit with 0 instead and just output the report."
    %}
    {% include custom/cli_option_row.html options="--ignore-literals"
               description="Ignore number values and string contents when comparing text"
               default="false"
               languages="Java"
    %}
    {% include custom/cli_option_row.html options="--ignore-identifiers"
               description="Ignore constant and variable names when comparing text"
               default="false"
               languages="Java"
    %}
    {% include custom/cli_option_row.html options="--ignore-annotations"
               description="Ignore language annotations (Java) or attributes (C#) when comparing text"
               default="false"
               languages="C#, Java"
    %}
    {% include custom/cli_option_row.html options="--ignore-literal-sequences"
               description="Ignore sequences of literals (common e.g. in list initializers)"
               default="false"
               languages="C#, C++, Lua"
    %}
    {% include custom/cli_option_row.html options="--ignore-sequences"
               description="Ignore sequences of identifier and literals"
               default="false"
               languages="C++"
    %}
    {% include custom/cli_option_row.html options="--ignore-usings"
               description="Ignore `using` directives in C# when comparing text"
               default="false"
               languages="C#"
    %}
    {% include custom/cli_option_row.html options="--no-skip-blocks"
               description="Do not skip code blocks matched by `--skip-blocks-pattern`"
               default="false"
               languages="C++"
    %}
    {% include custom/cli_option_row.html options="--skip-blocks-pattern"
               description="Pattern to find the blocks to skip. It is a string property and contains of two parts,
                            separated by `|`. The first part is the start pattern, the second part is the ending pattern."
               default="#if&nbsp;0|#endif"
               languages="C++"
    %}
    {% include custom/cli_option_row.html options="--uri,-u"
               option_arg="uri"
               description="Database URI for sources. One of `--dir`,
                               `--file-list` or `--uri` must be provided."
               languages="PLSQL"
    %}
    {% include custom/cli_option_row.html options="--help,-h"
               description="Print help text"
    %}
</table>

### Examples

Minimum required options: Just give it the minimum duplicate size and the source directory:

{% include cli_example.html
   id="basic"
   linux="pmd cpd --minimum-tokens 100 --dir src/main/java"
   windows="pmd.bat cpd --minimum-tokens 100 --dir src\main\java" %}

You can also specify the language:

{% include cli_example.html
   id="lang"
   linux="pmd cpd --minimum-tokens 100 --dir src/main/cpp --language cpp"
   windows="pmd.bat cpd --minimum-tokens 100 --dir src\main\cpp --language cpp" %}

You may wish to check sources that are stored in different directories:

{% include cli_example.html
   id="multiple"
   linux="pmd cpd --minimum-tokens 100 --dir src/main/java --dir src/test/java"
   windows="pmd.bat cpd --minimum-tokens 100 --dir src\main\java --dir src\test\java" %}

<em>There is no limit to the number of `--dir`, you may add.</em>

And if you're checking a C source tree with duplicate files in different architecture directories
you can skip those using `--skip-duplicate-files`:

{% include cli_example.html
   id="duplicates"
   linux="pmd cpd --minimum-tokens 100 --dir src/main/cpp --language cpp --skip-duplicate-files"
   windows="pmd.bat cpd --minimum-tokens 100 --dir src\main\cpp --language cpp --skip-duplicate-files" %}

You can also specify the encoding to use when parsing files:

{% include cli_example.html
   id="encoding"
   linux="pmd cpd --minimum-tokens 100 --dir src/main/java --encoding utf-16le"
   windows="pmd.bat cpd --minimum-tokens 100 --dir src\main\java --encoding utf-16le" %}

You can also specify a report format - here we're using the XML report:

{% include cli_example.html
   id="report"
   linux="pmd cpd --minimum-tokens 100 --dir src/main/java --format xml"
   windows="pmd.bat cpd --minimum-tokens 100 --dir src\main\java --format xml" %}

The default format is a text report, but there are [other supported formats](#available-report-formats)

Note that CPD's memory usage increases linearly with the size of the analyzed source code; you may need to give Java more memory to run it, like this:
{% include cli_example.html
   id="memchange"
   linux="export PMD_JAVA_OPTS=-Xmx512m
          pmd cpd --minimum-tokens 100 --dir src/main/java"
   windows="set PMD_JAVA_OPTS=-Xmx512m
            pmd.bat cpd --minimum-tokens 100 --dir src\main\java" %}

If you specify a source directory but don't want to scan the sub-directories, you can use the non-recursive option:

{% include cli_example.html
   id="nonrecursive"
   linux="pmd cpd --minimum-tokens 100 --dir src/main/java --non-recursive"
   windows="pmd.bat cpd --minimum-tokens 100 --dir src\main\java --non-recursive" %}

### Exit status

Please note that if CPD detects duplicated source code, it will exit with status 4 (since 5.0).
This behavior has been introduced to ease CPD integration into scripts or hooks, such as SVN hooks.

<table>
<tr><td>0</td><td>Everything is fine, no code duplications found.</td></tr>
<tr><td>1</td><td>CPD exited with an exception.</td></tr>
<tr><td>2</td><td>Usage error. Command-line parameters are invalid or missing.</td></tr>
<tr><td>4</td><td>At least one code duplication has been detected unless <code>--no-fail-on-violation</code> is set.</td></tr>
</table>

## Logging

PMD internally uses [slf4j](https://www.slf4j.org/) and ships with slf4j-simple as the logging implementation.
Logging messages are printed to System.err.

The configuration for slf4j-simple is in the file `conf/simplelogger.properties`. There you can enable
logging of specific classes if needed. The `--debug` command line option configures the default log level
to be "debug".


## Supported Languages

* C#
* C/C++
* [Coco](pmd_languages_coco.html)
* Dart
* EcmaScript (JavaScript)
* Fortran
* [Gherkin](pmd_languages_gherkin.html) (Cucumber)
* Go
* Groovy
* [Html](pmd_languages_html.html)
* [Java](pmd_languages_java.html)
* [Jsp](pmd_languages_jsp.html)
* [Julia](pmd_languages_julia.html)
* [Kotlin](pmd_languages_kotlin.html)
* Lua
* Matlab
* Modelica
* Objective-C
* Perl
* PHP
* [PL/SQL](pmd_languages_plsql.html)
* Python
* Ruby
* [Salesforce.com Apex](pmd_languages_apex.html)
* Scala
* Swift
* T-SQL
* [TypeScript](pmd_languages_js_ts.html)
* [Visualforce](pmd_languages_visualforce.html)
* [XML](pmd_languages_xml.html)


## Available report formats

* text : Default format
* xml (and xslt)
* csv
* csv_with_linecount_per_file
* vs

For details, see [CPD Report Formats](pmd_userdocs_cpd_report_formats.html).

## Ant task

Andy Glover wrote an Ant task for CPD; here's how to use it:

```xml
<target name="cpd">
    <taskdef name="cpd" classname="net.sourceforge.pmd.ant.CPDTask" />
    <cpd minimumTokenCount="100" outputFile="/home/tom/cpd.txt">
        <fileset dir="/home/tom/tmp/ant">
            <include name="**/*.java"/>
        </fileset>
    </cpd>
</target>
```

<!--  TODO avoid duplicating the descriptions! -->

### Attribute reference

<table>
    <tr>
        <th>Attribute</th>
        <th>Description</th>
        <th>Default</th>
        <th>Applies to</th>
    </tr>
    {% include custom/cli_option_row.html options="minimumtokencount"
                 description="A positive integer indicating the minimum duplicate size."
                 required="yes"
    %}
    {% include custom/cli_option_row.html options="encoding"
               description="The character set encoding (e.g., UTF-8) to use when reading the source code files, but also when
                            producing the report. A piece of warning, even if you set properly the encoding value,
                            let's say to UTF-8, but you are running CPD encoded with CP1252, you may end up with not UTF-8 file.
                            Indeed, CPD copy piece of source code in its report directly, therefore, the source files
                            keep their encoding.<br />
                            If not specified, CPD uses the system default encoding."
    %}
    {% include custom/cli_option_row.html options="format"
               description="The format of the report (e.g. `csv`, `text`, `xml`)."
               default="text"
    %}
    {% include custom/cli_option_row.html options="ignoreLiterals"
               description="if `true`, CPD ignores literal value differences when evaluating a duplicate
                           block. This means that `foo=42;` and `foo=43;` will be seen as equivalent. You may want
                           to run PMD with this option off to start with and then switch it on to see what it turns up."
               default="false"
               languages="Java"
    %}
    {% include custom/cli_option_row.html options="ignoreIdentifiers"
               description="Similar to `ignoreLiterals` but for identifiers; i.e., variable names, methods names, and so forth."
               default="false"
               languages="Java"
    %}
    {% include custom/cli_option_row.html options="ignoreAnnotations"
               description="Ignore annotations. More and more modern frameworks use annotations on classes and methods,
                            which can be very redundant and trigger CPD matches. With J2EE (CDI, Transaction Handling, etc)
                            and Spring (everything) annotations become very redundant. Often classes or methods have the
                            same 5-6 lines of annotations. This causes false positives."
               default="false"
               languages="Java"
    %}
    {% include custom/cli_option_row.html options="ignoreUsings"
               description="Ignore using directives in C#."
               default="false"
               languages="C#"
    %}
    {% include custom/cli_option_row.html options="skipDuplicateFiles"
               description="Ignore multiple copies of files of the same name and length in comparison."
               default="false"
    %}
    {% include custom/cli_option_row.html options="skipLexicalErrors"
               description="Skip files which can't be tokenized due to invalid characters instead of aborting CPD."
               default="false"
    %}
    {% include custom/cli_option_row.html options="skipBlocks"
               description="Enables or disabled skipping of blocks like a pre-processor. See also option skipBlocksPattern."
               default="true"
               languages="C++"
    %}
    {% include custom/cli_option_row.html options="skipBlocksPattern"
               description="Configures the pattern, to find the blocks to skip. It is a string property and contains of two parts,
                            separated by `|`. The first part is the start pattern, the second part is the ending pattern."
               default="#if&nbsp;0|#endif"
               languages="C++"
    %}
    {% include custom/cli_option_row.html options="language"
               description="Flag to select the appropriate language (e.g. `c`, `cpp`, `cs`, `java`, `jsp`, `php`, `ruby`, `fortran`
                            `ecmascript`, and `plsql`)."
               default="java"
    %}
    {% include custom/cli_option_row.html options="outputfile"
               description="The destination file for the report. If not specified the console will be used instead."
    %}
</table>

Also, you can get verbose output from this task by running ant with the `-v` flag; i.e.:

    ant -v -f mybuildfile.xml cpd

Also, you can get an HTML report from CPD by using the XSLT script in pmd/etc/xslt/cpdhtml.xslt.  Just run
the CPD task as usual and right after it invoke the Ant XSLT script like this:

```xml
<xslt in="cpd.xml" style="etc/xslt/cpdhtml.xslt" out="cpd.html" />
```

See [section "xslt" in CPD Report Formats](pmd_userdocs_cpd_report_formats.html#xslt) for more examples.

## GUI

CPD also comes with a simple GUI. You can start it through the unified CLI interface provided in the `bin` folder:

{% include cli_example.html
   id="gui"
   linux="pmd cpd-gui"
   windows="pmd.bat cpd-gui" %}

Here's a screenshot of CPD after running on the JDK 8 java.lang package:

{% include image.html file="userdocs/screenshot_cpd.png" alt="CPD Screenshot after running on the JDK 8 java.lang package" %}


## Suppression

Arbitrary blocks of code can be ignored through comments on **Java**, **C/C++**, **Dart**, **Go**, **Javascript**,
**Kotlin**, **Lua**, **Matlab**, **Objective-C**, **PL/SQL**, **Python**, **Scala**, **Swift** and **C#** by including the keywords `CPD-OFF` and `CPD-ON`.

```java
public Object someParameterizedFactoryMethod(int x) throws Exception {
    // some unignored code

    // tell cpd to start ignoring code - CPD-OFF

    // mission critical code, manually loop unroll
    goDoSomethingAwesome(x + x / 2);
    goDoSomethingAwesome(x + x / 2);
    goDoSomethingAwesome(x + x / 2);
    goDoSomethingAwesome(x + x / 2);
    goDoSomethingAwesome(x + x / 2);
    goDoSomethingAwesome(x + x / 2);

    // resume CPD analysis - CPD-ON

    // further code will *not* be ignored
}
```

Additionally, **Java** allows to toggle suppression by adding the annotations
**`@SuppressWarnings("CPD-START")`** and **`@SuppressWarnings("CPD-END")`**
all code within will be ignored by CPD.

This approach however, is limited to the locations were `@SuppressWarnings` is accepted.
It is legacy and the new comment based approach should be favored.

```java
//enable suppression
@SuppressWarnings("CPD-START")
public Object someParameterizedFactoryMethod(int x) throws Exception {
    // any code here will be ignored for the duplication detection
}
//disable suppression
@SuppressWarnings("CPD-END")
public void nextMethod() {
}
```

Other languages currently have no support to suppress CPD reports. In the future,
the comment based approach will be extended to those of them that can support it.

## Credits
CPD has been through three major incarnations:

*   First we wrote it using a variant of Michael Wise's Greedy String Tiling algorithm (our variant is described
    [here](http://www.onjava.com/pub/a/onjava/2003/03/12/pmd_cpd.html)).

*   Then it was completely rewritten by Brian Ewins using the
    [Burrows-Wheeler transform](http://dogma.net/markn/articles/bwt/bwt.htm).

*   Finally, it was rewritten by Steve Hawkins to use the
    [Karp-Rabin](http://www.nist.gov/dads/HTML/karpRabin.html) string matching algorithm.
