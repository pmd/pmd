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
               description="The minimum token length which should be reported as a duplicate."
               required="yes"
    %}
    {% include custom/cli_option_row.html options="--files"
               description="List of files and directories to process"
               required="yes"
    %}
    {% include custom/cli_option_row.html options="--filelist"
               description="Path to file containing a comma delimited list of files to analyze. If this is given, then you don't need to provide `--files`."
    %}
    {% include custom/cli_option_row.html options="--language"
               description="Sources code language."
               default="java"
    %}
    {% include custom/cli_option_row.html options="--encoding"
               description="Character encoding to use when processing files. If not specified, CPD uses the system default encoding."
    %}
    {% include custom/cli_option_row.html options="--skip-duplicate-files"
               description="Ignore multiple copies of files of the same name and length in comparison."
               default="false"
    %}
    {% include custom/cli_option_row.html options="--exclude"
               description="Files to be excluded from CPD check"
    %}
    {% include custom/cli_option_row.html options="--non-recursive"
               description="Don't scan subdirectories"
               default="false"
    %}
    {% include custom/cli_option_row.html options="--skip-lexical-errors"
               description="Skip files which can't be tokenized due to invalid characters instead of aborting CPD"
               default="false"
    %}
    {% include custom/cli_option_row.html options="--format"
               description="Report format."
               default="text"
    %}
    {% include custom/cli_option_row.html options="--failOnViolation"
               option_arg="bool"
               description="By default CPD exits with status 4 if code duplications are found.
                            Disable this option with `--failOnViolation false` to exit with 0 instead and just write the report."
               default="true"
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
               description="Ignore language annotations when comparing text"
               default="false"
               languages="Java"
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
    {% include custom/cli_option_row.html options="--uri"
               description="URI to process"
               languages="PLSQL"
    %}
    {% include custom/cli_option_row.html options="--help,-h"
               default="false"
               description="Print help text"
    %}
</table>

### Examples

_Note:_ The following example use the Linux start script. For Windows, just replace "./run.sh cpd" by "cpd.bat".


Minimum required options: Just give it the minimum duplicate size and the source directory:

    $ ./run.sh cpd --minimum-tokens 100 --files /usr/local/java/src/java

You can also specify the language:

    $ ./run.sh cpd --minimum-tokens 100 --files /path/to/c/source --language cpp

You may wish to check sources that are stored in different directories:

    $ ./run.sh cpd --minimum-tokens 100 --files /path/to/other/source  --files /path/to/other/source --files /path/to/other/source --language fortran

<em>There should be no limit to the number of '--files', you may add... But if you stumble one, please tell us !</em>

And if you're checking a C source tree with duplicate files in different architecture directories
you can skip those using --skip-duplicate-files:

    $ ./run.sh cpd --minimum-tokens 100 --files /path/to/c/source --language cpp --skip-duplicate-files

You can also specify the encoding to use when parsing files:

    $ ./run.sh cpd --minimum-tokens 100 --files /usr/local/java/src/java --encoding utf-16le

You can also specify a report format - here we're using the XML report:

    $ ./run.sh cpd --minimum-tokens 100 --files /usr/local/java/src/java --format xml

The default format is a text report, and there's also a `csv` report.

Note that CPD is pretty memory-hungry; you may need to give Java more memory to run it, like this:

    $ export PMD_JAVA_OPTS=-Xmx512m
    $ ./run.sh cpd --minimum-tokens 100 --files /usr/local/java/src/java

In order to change the heap size under Windows, you'll need to edit the batch file `cpd.bat` or
set the environment variable `PMD_JAVA_OPTS` prior to starting CPD:

    C:\ > cd C:\pmd-bin-{{site.pmd.version}}\bin
    C:\...\bin > set PMD_JAVA_OPTS=-Xmx512m
    C:\...\bin > .\cpd.bat --minimum-tokens 100 --files c:\temp\src


If you specify a source directory but don't want to scan the sub-directories, you can use the non-recursive option:

    $ ./run.sh cpd --minimum-tokens 100 --non-recursive --files /usr/local/java/src/java

### Exit status

Please note that if CPD detects duplicated source code, it will exit with status 4 (since 5.0).
This behavior has been introduced to ease CPD integration into scripts or hooks, such as SVN hooks.

<table>
<tr><td>0</td><td>Everything is fine, no code duplications found</td></tr>
<tr><td>1</td><td>Couldn't understand command line parameters or CPD exited with an exception</td></tr>
<tr><td>4</td><td>At least one code duplication has been detected unless '--failOnViolation false' is used.</td></tr>
</table>


## Supported Languages

* C#
* C/C++
* Dart
* EcmaScript (JavaScript)
* Fortran
* Go
* Groovy
* Java
* Jsp
* Kotlin
* Lua
* Matlab
* Modelica
* Objective-C
* Perl
* PHP
* PL/SQL
* Python
* Ruby
* Salesforce.com Apex
* Scala
* Swift
* Visualforce
* XML


## Available report formats

* text : Default format
* xml
* csv
* csv_with_linecount_per_file
* vs

For details, see [CPD Report Formats](pmd_userdocs_cpd_report_formats.html).

## Ant task

Andy Glover wrote an Ant task for CPD; here's how to use it:

```xml
    <target name="cpd">
        <taskdef name="cpd" classname="net.sourceforge.pmd.cpd.CPDTask" />
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

## GUI

CPD also comes with a simple GUI. You can start it via some scripts in the `bin` folder:

For Windows:

    cpdgui.bat

For Linux:

    ./run.sh cpdgui

Here's a screenshot of CPD after running on the JDK 8 java.lang package:

{% include image.html file="userdocs/screenshot_cpd.png" alt="CPD Screenshot after running on the JDK 8 java.lang package" %}


## Suppression

Arbitrary blocks of code can be ignored through comments on **Java**, **C/C++**, **Dart**, **Go**, **Javascript**,
**Kotlin**, **Lua**, **Matlab**, **Objective-C**, **PL/SQL**, **Python** and **Swift** by including the keywords `CPD-OFF` and `CPD-ON`.

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
It's legacy and the new comment's based approach should be favored.

```java
    //enable suppression
    @SuppressWarnings("CPD-START")
    public Object someParameterizedFactoryMethod(int x) throws Exception {
        // any code here will be ignored for the duplication detection
    }
    //disable suppression
    @SuppressWarnings("CPD-END)
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
