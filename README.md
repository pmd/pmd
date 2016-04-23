# PMD

[![Build Status](https://travis-ci.org/pmd/pmd.svg?branch=master)](https://travis-ci.org/pmd/pmd)

## About

PMD is a source code analyzer. It finds common programming flaws like unused variables, empty catch blocks,
unnecessary object creation, and so forth. It supports Java, JavaScript, Salesforce.com Apex, XML, XSL.
Additionally it includes CPD, the copy-paste-detector. CPD finds duplicated code in
Java, C, C++, C#, PHP, Ruby, Fortran, JavaScript, Salesforce.com Apex, Perl, Swift.

## Source

Our latest source of PMD can be found on [GitHub]. Fork us!

### How to build PMD?

You'll need to have a `~/.m2/toolchains.xml` file setup with jdk 1.6 (for pmd 5.3.x), jdk 1.7 (for pmd 5.4.x and pmd 5.5.x)
and jdk 1.8 (for some features in pmd 5.5.x). See [maven toolchains](https://maven.apache.org/guides/mini/guide-using-toolchains.html).
A example file can be found here: [example-toolchains.xml](https://github.com/pmd/pmd/blob/master/example-toolchains.xml).

Use maven in the top-level directory:

    mvn clean package

This will create the zip files in the directory `pmd-dist/target`:

    cd pmd-dist/target
    ls *.zip

That's all !

### How to build the documentation (maven site)?

Building the maven site is done with the following commands:

    mvn clean install -DskipTests=true
    mvn install site site:stage -Psite

You'll find the built site in the directory `target/staging/`.

### Bug Reports

We are using Sourceforge for bug tracking. Please file your bugs at <https://sourceforge.net/p/pmd/bugs/>.

### Pull Requests

Pull requests are always welcome: <https://github.com/pmd/pmd/pulls>


## News and Website

More information can be found on our [Website] and on [SourceForge].


[GitHub]: https://github.com/pmd/pmd
[Website]: https://pmd.github.io
[SourceForge]: https://sourceforge.net/projects/pmd/
