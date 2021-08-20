# PMD - source code analyzer

[![Join the chat at https://gitter.im/pmd/pmd](https://badges.gitter.im/pmd/pmd.svg)](https://gitter.im/pmd/pmd?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://github.com/pmd/pmd/workflows/build/badge.svg?branch=master)](https://github.com/pmd/pmd/actions)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.sourceforge.pmd/pmd/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.sourceforge.pmd/pmd)
[![Reproducible Builds](https://img.shields.io/badge/Reproducible_Builds-ok-green?labelColor=blue)](https://github.com/jvm-repo-rebuild/reproducible-central#net.sourceforge.pmd:pmd)
[![Coverage Status](https://coveralls.io/repos/github/pmd/pmd/badge.svg)](https://coveralls.io/github/pmd/pmd)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/a674ee8642ed44c6ba7633626ee95967)](https://www.codacy.com/app/pmd/pmd?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=pmd/pmd&amp;utm_campaign=Badge_Grade)
[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-v2.0%20adopted-ff69b4.svg)](code_of_conduct.md) 
[![Documentation (latest)](https://img.shields.io/badge/docs-latest-green)](https://pmd.github.io/latest/)

**PMD** is a source code analyzer. It finds common programming flaws like unused variables, empty catch blocks,
unnecessary object creation, and so forth. It supports many languages. It can be extended with custom rules.
It uses JavaCC and Antlr for parsing source into AST and runs rules against it to find violations.
Rules can be written in Java or using a XPath query.

It supports Java, JavaScript, Salesforce.com Apex and Visualforce,
Modelica, PLSQL, Apache Velocity, XML, XSL, Scala.

Additionally it includes **CPD**, the copy-paste-detector. CPD finds duplicated code in
C/C++, C#, Dart, Fortran, Go, Groovy, Java, JavaScript, JSP, Kotlin, Lua, Matlab, Modelica,
Objective-C, Perl, PHP, PLSQL, Python, Ruby, Salesforce.com Apex, Scala, Swift, Visualforce and XML.

In the future we hope to add support for data/control flow analysis and automatic (quick) fixes where
it makes sense.

## 🚀 Installation and Usage

Download the latest binary zip from the [releases](https://github.com/pmd/pmd/releases/latest)
and extract it somewhere.

Execute `bin/run.sh pmd` or `bin\pmd.bat`.

See also [Getting Started](https://pmd.github.io/latest/pmd_userdocs_installation.html)

**Demo:**

For this sample file:

```java
import java.util.List;

public class MyClass {
  void loop(List<String> l) {
    for (int i = 0; i < l.size(); i++) {
      System.out.println(l.get(i));
    }
  }
}
```

Run PMD on the command line:

```bash
$ run.sh pmd -d /usr/src -R rulesets/java/quickstart.xml -f xml
<?xml version="1.0" encoding="UTF-8"?>
<pmd xmlns="http://pmd.sourceforge.net/report/2.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://pmd.sourceforge.net/report/2.0.0 http://pmd.sourceforge.net/report_2_0_0.xsd" version="6.37.0" timestamp="2021-08-20T15:26:35.564">
<file name="/home/andreas/temp/pmd-test/MyClass.java">
<violation beginline="3" endline="9" begincolumn="1" endcolumn="1" rule="NoPackage" ruleset="Code Style" class="MyClass" externalInfoUrl="https://pmd.github.io/pmd-6.37.0/pmd_rules_java_codestyle.html#nopackage" priority="3">
All classes, interfaces, enums and annotations must belong to a named package
</violation>
<violation beginline="5" endline="7" begincolumn="5" endcolumn="5" rule="ForLoopCanBeForeach" ruleset="Best Practices" class="MyClass" method="loop" externalInfoUrl="https://pmd.github.io/pmd-6.37.0/pmd_rules_java_bestpractices.html#forloopcanbeforeach" priority="3">
This for loop can be replaced by a foreach loop
</violation>
</file>
</pmd>
```

PMD Eclipse Plugin:

![Screenshot PMD Eclipse Plugin](docs/images/userdocs/screenshot_pmd-eclipse-plugin.png)

There are plugins for Maven and Gradle as well as for various IDEs.
See [Tools / Integrations](https://pmd.github.io/latest/pmd_userdocs_tools.html)

## ℹ️ How to get support?

*   How do I? -- Ask a question on [StackOverflow](https://stackoverflow.com/questions/tagged/pmd)
    or on [discussions](https://github.com/pmd/pmd/discussions).
*   I got this error, why? -- Ask a question on [StackOverflow](https://stackoverflow.com/questions/tagged/pmd)
    or on [discussions](https://github.com/pmd/pmd/discussions).
*   I got this error and I'm sure it's a bug -- file an [issue](https://github.com/pmd/pmd/issues).
*   I have an idea/request/question -- create a new [discussion](https://github.com/pmd/pmd/discussions).
*   I have a quick question -- ask on our [Gitter chat](https://gitter.im/pmd/pmd).
*   Where's your documentation? -- <https://pmd.github.io/latest/>

## 🤝 Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Our latest source of PMD can be found on [GitHub](https://github.com/pmd/pmd). Fork us!

*   [How to build PMD](BUILDING.md)
*   [How to contribute to PMD](CONTRIBUTING.md)

The rule designer is developed over at [pmd/pmd-designer](https://github.com/pmd/pmd-designer).
Please see [its README](https://github.com/pmd/pmd-designer#contributing) for
developer documentation.

## 🪙 Financial Contributors

Become a financial contributor and help us sustain our community. [Contribute](https://opencollective.com/pmd/contribute)

## 📝 License

[BSD Style](LICENSE)
