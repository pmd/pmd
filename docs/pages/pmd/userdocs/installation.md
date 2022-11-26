---
title: Installation and basic CLI usage
keywords: [pmd, cpd, options, command, auxclasspath]
tags: [getting_started, userdocs]
summary: "Sums up the first steps to set up a CLI installation and get started using PMD"
permalink: pmd_userdocs_installation.html
sidebar: pmd_sidebar
---

## How to install PMD and CPD

### Requirements

*   [Java JRE](http://www.oracle.com/technetwork/java/javase/downloads/index.html),
    OpenJDK from [Azul](https://www.azul.com/downloads/zulu-community/)
    or [AdoptOpenJDK](https://adoptopenjdk.net/) 1.7 or higher.
    
    **Note:** For analyzing Apex, HTML, JavaScript, Scala or VisualForce or running the [Designer](pmd_userdocs_extending_designer_reference.html)
    at least Java 8 is required.
    
*   A zip archiver, e.g.:
    
    * For Windows: [Winzip](http://winzip.com) or the free [7-zip](http://www.7-zip.org/)
    * For Linux / Unix: [InfoZip](http://infozip.sourceforge.net/)

{% include note.html content="For executing the Designer (`pmd designer`) using [OpenJDK](http://jdk.java.net) or Java 11+, you need additionally [JavaFX](https://gluonhq.com/products/javafx/). Download it, extract it and set the environment variable JAVAFX_HOME pointing at that directory." %}


### Installation

PMD is distributed as a zip archive, which includes both [PMD](#running-pmd-via-command-line) and [CPD](pmd_userdocs_cpd.html). 
You can download the latest binary distribution from [the github releases page](https://github.com/pmd/pmd/releases).

It's highly recommended (but not required) to include it to your `PATH`.

On Linux you can do this by adding `PATH=$PATH:*path_to_pmd*/bin/` to your `~/.bashrc` / `~/.zshrc` file.

On Windows this is achieved by:
1. On the **Start menu**, right-click **Computer**.
2. On the context menu, click **Properties**.
3. In the **System** dialog box, click **Advanced system settings**.
4. On the **Advanced** tab of the **System Properties** dialog box, click **Environment Variables**
5. In the **System Variables** box of the **Environment Variables** dialog box, scroll to **Path** and select it.
6. Click the lower of the two **Edit** buttons in the dialog box.
7. In the **Edit System Variable** dialog box, scroll to the end of the string in the **Variable value** box and add a semicolon (;).
8. Add the proper value for `*path_to_pmd*/bin/` after the semicolon.
9. Click **OK** in three successive dialog boxes, and then close the **System** dialog box.

#### Shell completion

PMD ships with built-in completion support for Bash / Zsh.

To enable it, simply add `source *path_to_pmd*/shell/pmd-completion.sh` to your `~/.bashrc` / `~/.zshrc` file.

## Running PMD via command line

{% include callout.html type="primary"
   content="PMD comes with several command line utilities, like CPD, the rule designer or PMD itself.
            You can run any of them using the script `pmd` (`pmd.bat` under Windows), located inside the `bin/`
            directory of the PMD distribution. The first argument is the name of the utility you want
            to execute ('check', 'designer', ...), e.g. PMD is launched via `pmd check`. The rest of
            the arguments are specific to the utility used.<br/><br/>" %}

Running a PMD analysis (`pmd check` or `pmd.bat check`) requires at least one option and a list of sources:

* `-R <path>`: the ruleset file you want to use. PMD uses xml configuration files, called *rulesets*, which specify 
which rules to execute on your sources. You can also run a single rule by referencing it using its *category* and
name (more details [here](pmd_userdocs_making_rulesets.html#referencing-a-single-rule)). For example, you can check for unnecessary
modifiers on Java sources with `-R category/java/codestyle.xml/UnnecessaryModifier`.
* `<source> …`: path to the sources to analyse. This can be a file name, a directory, or a jar or zip file containing the
sources. Alternatively You can use the `-d` or `--dir` flag, which is equivalent.

{% include note.html
   content="At the moment the formerly provided rulesets (eg `rulesets/java/basic.xml`) are deprecated,
   though you can still use them. PMD includes a quickstart ruleset for some languages (currently, Java)
   as base configurations, which you can reference as e.g. `rulesets/java/quickstart.xml`. You're strongly
   encouraged to [create your own ruleset](pmd_userdocs_making_rulesets.html) from the start though." %}

Additionally, the following options, are specified most of the time even though they're not required:
* `-f <format>`: report format. PMD supports many report formats out of the box. You may want to start with the basic
`text` format (default) or `xml` format. The supported formats are [documented here](pmd_userdocs_cli_reference.html#available-report-formats).
* `--aux-classpath <classpath>`: class path containing the compiled class files of the analysed Java sources, if any.
  Setting this up correctly allows PMD to do much deeper analysis using reflection. Some rules, such as [MissingOverride](pmd_rules_java_bestpractices.html#missingoverride),
  require it to function properly.

{%include tip.html content="A full CLI reference, including report formats, is available under [PMD CLI Reference](pmd_userdocs_cli_reference.html)" %}



### Sample usage

 The following shows a sample run of PMD with the `text` format:

{% include cli_example.html
   id="pmd"
   linux="pmd check -f text -R rulesets/java/quickstart.xml src/main/java

  .../src/main/java/com/me/RuleSet.java:123  These nested if statements could be combined
  .../src/main/java/com/me/RuleSet.java:231  Useless parentheses.
  .../src/main/java/com/me/RuleSet.java:232  Useless parentheses.
  .../src/main/java/com/me/RuleSet.java:357  These nested if statements could be combined
  .../src/main/java/com/me/RuleSetWriter.java:66     Avoid empty catch blocks"
   windows="pmd.bat check -f text -R rulesets/java/quickstart.xml ..\..\src\main\java

  .../src/main/java/com/me/RuleSet.java:123  These nested if statements could be combined
  .../src/main/java/com/me/RuleSet.java:231  Useless parentheses.
  .../src/main/java/com/me/RuleSet.java:232  Useless parentheses.
  .../src/main/java/com/me/RuleSet.java:357  These nested if statements could be combined
  .../src/main/java/com/me/RuleSetWriter.java:66     Avoid empty catch blocks" %}

## Running CPD via command line

{% include note.html
   content="CPD supports Java, JSP, C, C++, C#, Fortran and PHP source code, among other languages.
            For the full list, see [Supported Languages](pmd_userdocs_cpd.html#supported-languages)." %}

Like for PMD, CPD is started on Unix by `pmd cpd` and on Windows by `pmd.bat cpd`, and it requires one option and a list of sources:

* `--minimum-tokens <number>`: the minimum token length which should be reported as a duplicate.
* `<source> …`: path to the sources to analyse. This can be a file name, a directory, or a jar or zip file containing the
sources. Alternatively You can use the `-d` or `--dir` flag, which is equivalent.

{% include tip.html
   content="CPD's command-line reference, Ant task usage, and many examples are documented in the
            [CPD documentation page](pmd_userdocs_cpd.html)" %}

### Sample usage

 The following shows a sample run of CPD with the `text` format:

{% include cli_example.html
   id="cpd"
   linux="pmd cpd --minimum-tokens 100 /home/me/src

  Found a 7 line (110 tokens) duplication in the following files:
  Starting at line 579 of /home/me/src/test/java/foo/FooTypeTest.java
  Starting at line 586 of /home/me/src/test/java/foo/FooTypeTest.java

          assertEquals(Boolean.TYPE, expressions.get(index++).getType());
          assertEquals(Boolean.TYPE, expressions.get(index++).getType());
          assertEquals(Boolean.TYPE, expressions.get(index++).getType());
          assertEquals(Boolean.TYPE, expressions.get(index++).getType());
          assertEquals(Boolean.TYPE, expressions.get(index++).getType());
          assertEquals(Boolean.TYPE, expressions.get(index++).getType());
          assertEquals(Boolean.TYPE, expressions.get(index++).getType());"
    windows="pmd.bat cpd --minimum-tokens 100 /home/me/src

  Found a 7 line (110 tokens) duplication in the following files:
  Starting at line 579 of c:\temp\src\test\java\foo\FooTypeTest.java
  Starting at line 586 of c:\temp\src\test\java\foo\FooTypeTest.java

          assertEquals(Boolean.TYPE, expressions.get(index++).getType());
          assertEquals(Boolean.TYPE, expressions.get(index++).getType());
          assertEquals(Boolean.TYPE, expressions.get(index++).getType());
          assertEquals(Boolean.TYPE, expressions.get(index++).getType());
          assertEquals(Boolean.TYPE, expressions.get(index++).getType());
          assertEquals(Boolean.TYPE, expressions.get(index++).getType());
          assertEquals(Boolean.TYPE, expressions.get(index++).getType());" %}

