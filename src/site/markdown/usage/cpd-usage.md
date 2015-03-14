<!--
    <author email="tom@infoether.com">Tom Copeland</author>
-->

# Finding duplicated code

Or - Finding copied and pasted code

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

Here's a [screenshot](images/screenshot_cpd.png) of CPD after running on the JDK java.lang package.

Note that CPD works with Java, JSP, C, C++, C#, Fortran and PHP code. Your own language is missing?
See how to add it [here](../customizing/cpd-parser-howto.html).

CPD is included with PMD, which you can download [here](http://sourceforge.net/projects/pmd/files/pmd/).
Or, if you have [Java Web Start](http://java.sun.com/products/javawebstart/),
you can [run CPD by clicking here](http://pmd.sourceforge.net/cpd.jnlp).

[Here](./cpdresults.txt) are the duplicates CPD found in the JDK 1.4 source code.

[Here](./cpp_cpdresults.txt) are the duplicates CPD found in the APACHE_2_0_BRANCH branch of Apache
(just the `httpd-2.0/server/` directory).

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

## Command line usage

To run CPD from the command line, just give it the minimum duplicate size and the source directory:

    $ java net.sourceforge.pmd.cpd.CPD --minimum-tokens 100 --files /usr/local/java/src/java

You can also specify the language:

    $ java net.sourceforge.pmd.cpd.CPD --minimum-tokens 100 --files /path/to/c/source --language cpp

You may wish to check sources that are stored in different directories:

    $ java net.sourceforge.pmd.cpd.CPD --minimum-tokens 100 --files /path/to/other/source  --files /path/to/other/source --files /path/to/other/source --language fortran

<em>There should be no limit to the number of '--files', you may add... But if you stumble one, please tell us !</em>

And if you're checking a C source tree with duplicate files in different architecture directories
you can skip those using --skip-duplicate-files:

    $ java net.sourceforge.pmd.cpd.CPD --minimum-tokens 100 --files /path/to/c/source --language cpp --skip-duplicate-files

You can also the encoding to use when parsing files:

    $ java net.sourceforge.pmd.cpd.CPD --minimum-tokens 100 --files /usr/local/java/src/java --encoding utf-16le

You can also specify a report format - here we're using the XML report:

    $ java net.sourceforge.pmd.cpd.CPD --minimum-tokens 100 --files /usr/local/java/src/java --format net.sourceforge.pmd.cpd.XMLRenderer

The default format is a text report, and there's also a `net.sourceforge.pmd.cpd.CSVRenderer` report.

Note that CPD is pretty memory-hungry; you may need to give Java more memory to run it, like this:

    $ java -Xmx512m net.sourceforge.pmd.cpd.CPD --minimum-tokens 100 --files /usr/local/java/src/java

If you specify a source directory but don't want to scan the sub-directories, you can use the non-recursive option:

    $ java net.sourceforge.pmd.cpd.CPD --minimum-tokens 100 --non-recursive --files /usr/local/java/src/java

Please note that if CPD detects duplicated source code, it will exit with status 4 (since 5.0).
This behavior has been introduced to ease CPD integration into scrips or hook, such as SVN hooks.

## Suppression

By adding the annotations **@SuppressWarnings("CPD-START")** and **@SuppressWarnings("CPD-END")**
all code within will be ignored by CPD - thus you can avoid false positivs.
This provides the ability to ignore sections of source code, such as switch/case statements or parameterized factories.

    //enable suppression
    @SuppressWarnings("CPD-START")
    public Object someParameterizedFactoryMethod(int x) throws Exception {
        // any code here will be ignored for the duplication detection
    }
    //disable suppression
    @SuppressWarnings("CPD-END)
    public void nextMethod() {
    }

