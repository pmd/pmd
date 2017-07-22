---
title: Finding duplicated code
summary: Or how to find copied and pasted code
sidebar: pmd_sidebar
permalink: pmd_userdocs_cpd.html
folder: pmd/userdocs
---

## Overview

Duplicate code can be hard to find, especially in a large project.
But PMD's Copy/Paste Detector (CPD) can find it for you!
CPD has been through three major incarnations:

*   First we wrote it using a variant of Michael Wise's Greedy String Tiling algorithm (our variant is described
    [here](http://www.onjava.com/pub/a/onjava/2003/03/12/pmd_cpd.html)).

*   Then it was completely rewritten by Brian Ewins using the
    [Burrows-Wheeler transform](http://dogma.net/markn/articles/bwt/bwt.htm).

*   Finally, it was rewritten by Steve Hawkins to use the
    [Karp-Rabin](http://www.nist.gov/dads/HTML/karpRabin.html) string matching algorithm.

Each rewrite made it much faster, and now it can process the JDK 1.4 java.* packages in about 4 seconds
(on my workstation, at least).

Note that CPD works with Java, JSP, C, C++, C#, Fortran and PHP code and some more languages. For the
full list, see below [Supported Languages](#Supported_Languages).

Your own language is missing?
See how to add it [here](../customizing/cpd-parser-howto.html).

CPD is included with PMD, which you can download [here](http://sourceforge.net/projects/pmd/files/pmd/).
Or, if you have [Java Web Start](http://java.sun.com/products/javawebstart/),
you can [run CPD by clicking here](http://pmd.sourceforge.net/cpd.jnlp).

[Here](./cpdresults.txt) are the duplicates CPD found in the JDK 1.4 source code.

[Here](./cpp_cpdresults.txt) are the duplicates CPD found in the APACHE_2_0_BRANCH branch of Apache
(just the `httpd-2.0/server/` directory).


## Command line usage

### Windows

CPD comes with its own starter batch file: `cpd.bat`. It's located in the `bin` subdirectory in the PMD
binary distribution zip-file. Let's assume, you are in this directory, then you can start CPD this way:

    cpd.bat --minimum-tokens 100 --files c:\temp\src\java

The options "minimum-tokens" and "files" are the two required options; there are more options, see below.


### Linux

For Linux, there is since PMD 5.0 a combined start script for all command line tools. This includes CPD.
The start script is called `run.sh` and is located in the `bin` subdirectory in the PMD binary distribution
zip-file. Let's assume, you are in this directory, then you can start CPD this way:

    ./run.sh cpd --minimum-tokens 100 --files /tmp/src/java

The options "minimum-tokens" and "files" are the two required options; there are more options, see below.

### Options

<table>
    <tr>
        <th>Option</th>
        <th>Description</th>
        <th>Required</th>
        <th>Applies for language</th>
    </tr>
    <tr>
        <td>--minimum-tokens</td>
        <td>The minimum token length which should be reported as a duplicate.</td>
        <td>yes</td>
        <td></td>
    </tr>
    <tr>
        <td>--files</td>
        <td>List of files and directories to process</td>
        <td>yes</td>
        <td></td>
    </tr>
    <tr>
        <td>--filelist</td>
        <td>Path to file containing a comma delimited list of files to analyze. If this is given, then you don't need to provide `--files`.</td>
        <td>no</td>
        <td></td>
    </tr>
    <tr>
        <td>--language</td>
        <td>Sources code language. Default value is `java`</td>
        <td>no</td>
        <td></td>
    </tr>
    <tr>
        <td>--encoding</td>
        <td>Character encoding to use when processing files</td>
        <td>no</td>
        <td></td>
    </tr>
    <tr>
        <td>--skip-duplicate-files</td>
        <td>Ignore multiple copies of files of the same name and length in comparison.</td>
        <td>no</td>
        <td></td>
    </tr>
    <tr>
        <td>--exclude</td>
        <td>Files to be excluded from CPD check</td>
        <td>no</td>
        <td></td>
    </tr>
    <tr>
        <td>--non-recursive</td>
        <td>Don't scan subdirectiories</td>
        <td>no</td>
        <td></td>
    </tr>
    <tr>
        <td>--skip-lexical-errors</td>
        <td>Skip files which can't be tokenized due to invalid characters instead of aborting CPD</td>
        <td>no</td>
        <td></td>
    </tr>
    <tr>
        <td>--format</td>
        <td>Report format. Default value is `text`.</td>
        <td>no</td>
        <td></td>
    </tr>
    <tr>
        <td>--failOnViolation {true|false}</td>
        <td>By default CPD exits with status 4 if code duplications are found.
            Disable this option with '--failOnViolation false' to exit with 0 instead and just write the report.</td>
        <td>no</td>
        <td></td>
    </tr>
    <tr>
        <td>--ignore-literals</td>
        <td>Ignore number values and string contents when comparing text</td>
        <td>no</td>
        <td>java</td>
    </tr>
    <tr>
        <td>--ignore-identifiers</td>
        <td>Ignore constant and variable names when comparing text</td>
        <td>no</td>
        <td>java</td>
    </tr>
    <tr>
        <td>--ignore-annotations</td>
        <td>Ignore language annotations when comparing text</td>
        <td>no</td>
        <td>java</td>
    </tr>
    <tr>
        <td>--ignore-usings</td>
        <td>Ignore using directives in C# when comparing text</td>
        <td>no</td>
        <td>C#</td>
    </tr>
    <tr>
        <td>--no-skip-blocks</td>
        <td>Do not skip code blocks marked with --skip-blocks-pattern (e.g. #if 0 until #endif)</td>
        <td>no</td>
        <td>cpp</td>
    </tr>
    <tr>
        <td>--skip-blocks-pattern</td>
        <td>
            Pattern to find the blocks to skip. Start and End pattern separated by |.
            Default is `#if 0|#endif`.
        </td>
        <td>no</td>
        <td>cpp</td>
    </tr>
    <tr>
        <td>--uri</td>
        <td>URI to process</td>
        <td>no</td>
        <td>plsql</td>
    </tr>
    <tr>
        <td>--help / -h</td>
        <td>Print help text</td>
        <td>no</td>
        <td></td>
    </tr>
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

    $ export HEAPSIZE=512m
    $ ./run.sh cpd --minimum-tokens 100 --files /usr/local/java/src/java

In order to change the heap size under Windows, you'll need to edit the batch file `cpd.bat` set the "OPTS"
variable to `-Xmx512m`.


If you specify a source directory but don't want to scan the sub-directories, you can use the non-recursive option:

    $ ./run.sh cpd --minimum-tokens 100 --non-recursive --files /usr/local/java/src/java

### Exit status

Please note that if CPD detects duplicated source code, it will exit with status 4 (since 5.0).
This behavior has been introduced to ease CPD integration into scripts or hooks, such as SVN hooks.

<table>
<tr><td>0</td><td>Everything is fine, now code duplications found</td></tr>
<tr><td>1</td><td>Couldn't understand command line parameters or CPD exited with an exception</td></tr>
<tr><td>4</td><td>At least one code duplication has been detected unless '--failOnViolation false' is used.</td></tr>
</table>


### Supported Languages

* cs
* cpp
* ecmascript (JavaScript)
* fortran
* go
* java
* jsp
* matlab
* objectivec
* php
* plsql
* python
* ruby
* scala
* swift


### Available formats

* text : Default format
* xml
* csv
* csv_with_linecount_per_file
* vs


## Ant task

Andy Glover wrote an Ant task for CPD; here's how to use it:

    <target name="cpd">
        <taskdef name="cpd" classname="net.sourceforge.pmd.cpd.CPDTask" />
        <cpd minimumTokenCount="100" outputFile="/home/tom/cpd.txt">
            <fileset dir="/home/tom/tmp/ant">
                <include name="**/*.java"/>
            </fileset>
        </cpd>
    </target>

<table border="1" cellpadding="2" cellspacing="0">
  <tr>
    <td valign="top"><b>Attribute</b></td>
    <td valign="top"><b>Description</b></td>
    <td valign="top"><b>Applies for language</b></td>
    <td align="center" valign="top"><b>Required</b></td>
  </tr>
  <tr>
    <td valign="top">encoding</td>
    <td valign="top">
        The character set encoding (e.g., UTF-8) to use when reading the source code files, but also when
        producing the report. A piece of warning, even if you set properly the encoding value,
        let's say to UTF-8, but you are running CPD encoded with CP1252, you may end up with not UTF-8 file.
        Indeed, CPD copy piece of source code in its report directly, therefore, the source files
        keep their encoding.<br />
        If not specified, CPD uses the system default encoding.
    </td>
    <td valign="top"></td>
    <td valign="top" align="center">No</td>
  </tr>
  <tr>
    <td valign="top">format</td>
    <td valign="top">The format of the report (e.g. `csv`, `text`, `xml`); defaults to `text`.</td>
    <td valign="top"></td>
    <td valign="top" align="center">No</td>
  </tr>
  <tr>
    <td valign="top">ignoreLiterals</td>
    <td valign="top">
        if `true`, CPD ignores literal
        value differences when evaluating a duplicate block. This means that `foo=42;` and `foo=43;`
        will be seen as equivalent. You may want to run PMD with this option off to start with and
        then switch it on to see what it turns up; defaults to `false`.
    </td>
    <td valign="top">java</td>
    <td valign="top" align="center">No</td>
  </tr>
  <tr>
    <td valign="top">ignoreIdentifiers</td>
    <td valign="top">
        Similar to `ignoreLiterals` but for identifiers; i.e., variable names, methods names,
        and so forth; defaults to `false`.
    </td>
    <td valign="top">java</td>
    <td valign="top" align="center">No</td>
  </tr>
  <tr>
    <td valign="top">ignoreAnnotations</td>
    <td valign="top">
        Ignore annotations. More and more modern frameworks use annotations on classes and methods,
        which can be very redundant and trigger CPD matches. With J2EE (CDI, Transaction Handling, etc)
        and Spring (everything) annotations become very redundant. Often classes or methods have the
        same 5-6 lines of annotations. This causes false positives; defaults to `false`.
    </td>
    <td valign="top">java</td>
    <td valign="top" align="center">No</td>
  </tr>
  <tr>
    <td valign="top">ignoreUsings</td>
    <td valign="top">
        Ignore using directives in C#.
    </td>
    <td valign="top">C#</td>
    <td valign="top" align="center">No</td>
  </tr>
  <tr>
    <td valign="top">skipDuplicateFiles</td>
    <td valign="top">
        Ignore multiple copies of files of the same name and length in comparison; defaults to `false`.
    </td>
    <td valign="top"></td>
    <td valign="top" align="center">No</td>
  </tr>
  <tr>
    <td valign="top">skipLexicalErrors</td>
    <td valign="top">
        Skip files which can't be tokenized due to invalid characters instead of aborting CPD; defaults to `false`.
    </td>
    <td valign="top"></td>
    <td valign="top" align="center">No</td>
  </tr>
  <tr>
    <td valign="top">skipBlocks</td>
    <td valign="top">
        Enables or disabled skipping of blocks like a pre-processor; defaults to `true`.
        See also option skipBlocksPattern.
    </td>
    <td valign="top">cpp</td>
    <td valign="top">No</td>
  </tr>
  <tr>
    <td valign="top">skipBlocksPattern</td>
    <td valign="top">
        Configures the pattern, to find the blocks to skip. It is a string property and contains of two parts,
        separated by `|`. The first part is the start pattern, the second part is the ending pattern.
        The default value is `#if 0|#endif`.
    </td>
    <td valign="top">cpp</td>
    <td valign="top">no</td>
  </tr>
  <tr>
    <td valign="top">language</td>
    <td valign="top">
        Flag to select the appropriate language (e.g. `c`, `cpp`, `cs`, `java`, `jsp`, `php`, `ruby`, `fortran`
        `ecmascript`, and `plsql`); defaults to `java`.
    </td>
    <td valign="top"></td>
    <td valign="top" align="center">No</td>
  </tr>
  <tr>
    <td valign="top">minimumtokencount</td>
    <td valign="top">A positive integer indicating the minimum duplicate size.</td>
    <td valign="top"></td>
    <td valign="top" align="center">Yes</td>
  </tr>
  <tr>
    <td valign="top">outputfile</td>
    <td valign="top">The destination file for the report. If not specified the console will be used instead.</td>
    <td valign="top"></td>
    <td valign="top" align="center">No</td>
  </tr>
</table>

Also, you can get verbose output from this task by running ant with the `-v` flag; i.e.:

    ant -v -f mybuildfile.xml cpd

Also, you can get an HTML report from CPD by using the XSLT script in pmd/etc/xslt/cpdhtml.xslt.  Just run
the CPD task as usual and right after it invoke the Ant XSLT script like this:

    <xslt in="cpd.xml" style="etc/xslt/cpdhtml.xslt" out="cpd.html" />

## GUI

CPD also comes with a simple GUI. You can start it via some scripts in the `bin` folder:

For Windows:

    cpdgui.bat

For Linux:

    ./run.sh cpdgui

Here's a screenshot of CPD after running on the JDK 8 java.lang package:

![CPD Screenshot after running on the JDK 8 java.lang package](/images/userdocs/screenshot_cpd.png)


## Suppression

Arbitrary blocks of code can be ignored through comments on **Java** by including the keywords `CPD-OFF` and `CPD-ON`.

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


Additionally, **Java** allows to toggle suppression by adding the annotations
**`@SuppressWarnings("CPD-START")`** and **`@SuppressWarnings("CPD-END")`**
all code within will be ignored by CPD.

This approach however, is limited to the locations were `@SuppressWarnings` is accepted.
It's legacy and the new comment's based approch should be favored.

    //enable suppression
    @SuppressWarnings("CPD-START")
    public Object someParameterizedFactoryMethod(int x) throws Exception {
        // any code here will be ignored for the duplication detection
    }
    //disable suppression
    @SuppressWarnings("CPD-END)
    public void nextMethod() {
    }


Other languages currently have no support to suppress CPD reports. In the future,
the comment based approach will be extended to those of them that can support it.
