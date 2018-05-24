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

*   [Java JRE](http://www.oracle.com/technetwork/java/javase/downloads/index.html) 1.7 or higher
*   A zip archiver, e.g.:
    
    * For Windows: [Winzip](http://winzip.com) or the free [7-zip](http://www.7-zip.org/)
    * For Linux / Unix: [InfoZip](http://www.info-zip.org/pub/infozip/)

### Installation

PMD is distributed as a zip archive, which includes both [PMD](#running-pmd-via-command-line) and [CPD](/pmd_userdocs_cpd.html). 
You can download the latest binary distribution from [the github releases page](https://github.com/pmd/pmd/releases).

Unzip it into any directory, optionally add the `bin` subdirectory in your `PATH`, and you're good to go!         

 

## Running PMD via command line

{% include callout.html type="primary"
   content="PMD comes with several command line utilities, like CPD, the rule designer or PMD itself.
            On Unix, you can run any of them using the script `run.sh`, located inside the `bin/`
            directory of the PMD distribution. The first argument is the name of the utility you want
            to execute ('pmd', 'designer', ...), e.g. PMD is launched via `run.sh pmd`. The rest of
            the arguments are specific to the utility used.<br/><br/>
            On Windows, each utility has its own startup script, e.g. `pmd.bat`, `cpd.bat`." %}

The PMD command (`pmd.bat` or `run.sh pmd`) requires two options:

* `-d <path>`: path to the sources to analyse. This can be a file name, a directory, or a jar or zip file containing the
sources.
* `-R <path>`: the ruleset file you want to use. PMD uses xml configuration files, called *rulesets*, which specify 
which rules to execute on your sources. You can also run a single rule by referencing it using its *category* and
name (more details [here](pmd_userdocs_making_rulesets.html#referencing-a-single-rule)). For example, you can check for unnecessary
modifiers on Java sources with `-R category/java/codestyle.xml/UnnecessaryModifier`.

{% include note.html
   content="At the moment the formerly provided rulesets (eg `rulesets/java/basic.xml`) are deprecated,
   though you can still use them. PMD will soon include standard rulesets as default configurations,
   but you're strongly encouraged to [create your own ruleset](pmd_userdocs_making_rulesets.html) from
   the start." %}

Additionnally, the following options, are specified most of the time even though they're not required:
* `-f <format>`: report format. PMD supports many report formats out of the box, you may want to start with the basic
`text` format or `xml` format.
* `-auxclasspath <classpath>`: class path containing the compiled class files of the analysed Java sources, if any.
  Setting this up correctly allows PMD to do much deeper analysis using reflection. Some rules, such as [MissingOverride](http://localhost:4005/pmd_rules_java_bestpractices.html#missingoverride),
  require it to function properly.

{%include tip.html content="A full CLI reference, including report formats, is available under [PMD CLI Reference](pmd_userdocs_cli_reference.html)" %}



### Sample usage

 The following shows a sample run of with the `text` format:


<div class="text-left">
  <ul class="nav nav-tabs" role="tablist">
    <li role="presentation" class="active"><a href="#linux" aria-controls="linux / unix" role="tab" data-toggle="tab">Linux / Unix</a></li>
    <li role="presentation"><a href="#windows" aria-controls="windows" role="tab" data-toggle="tab">Windows</a></li>
  </ul>
 
  <div class="tab-content">
    <div role="tabpanel" class="tab-pane active" id="linux">
<figure class="highlight"><pre><code class="language-bash" data-lang="bash"><span class="gp">~ $ </span><span class="s2">cd</span> ~/bin/pmd-bin-{{site.pmd.version}}/bin
<span class="gp">~/.../bin $ </span><span class="s2">./run.sh</span> pmd -d ../../../src/main/java/ -f text -R rulesets/java/basic.xml
  
  .../src/main/java/com/me/RuleSet.java:123  These nested if statements could be combined
  .../src/main/java/com/me/RuleSet.java:231  Useless parentheses.
  .../src/main/java/com/me/RuleSet.java:232  Useless parentheses.
  .../src/main/java/com/me/RuleSet.java:357  These nested if statements could be combined
  .../src/main/java/com/me/RuleSetWriter.java:66     Avoid empty catch blocks</code></pre></figure>
    </div>
    <div role="tabpanel" class="tab-pane" id="windows">
<figure class="highlight"><pre><code class="language-bash" data-lang="bash"><span class="gp">C:\ &gt; </span><span class="s2">cd</span> C:\pmd-bin-{{site.pmd.version}}\bin
<span class="gp">C:\...\bin > </span><span class="s2">.\pmd.bat</span> -d ..\..\src\main\java\ -f text -R rulesets/java/basic.xml
      
  .../src/main/java/com/me/RuleSet.java:123  These nested if statements could be combined
  .../src/main/java/com/me/RuleSet.java:231  Useless parentheses.
  .../src/main/java/com/me/RuleSet.java:232  Useless parentheses.
  .../src/main/java/com/me/RuleSet.java:357  These nested if statements could be combined
  .../src/main/java/com/me/RuleSetWriter.java:66     Avoid empty catch blocks</code></pre></figure>
    </div>
  </div>
</div>


## Running CPD via command line

Like for PMD, CPD is started on Unix by `run.sh cpd` and on Windows by `cpd.bat`.

There are two required parameters:
* `--files <path>`: path to the sources to analyse. This can be a file name, a
  directory or a jar or zip file containing the sources.
* `--minimum-tokens <number>`: the minimum token length which should be reported as a duplicate.
