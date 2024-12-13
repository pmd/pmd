---
title: Tools / Integrations
tags: [userdocs, tools]
permalink: pmd_userdocs_tools.html
author: David Dixon-Peugh <dpeugh@users.sourceforge.net>
---

## Automated Code Review

{% include note.html content="The tools are listed in alphabetical order without rating." %}

### Codacy

[Codacy](https://www.codacy.com/) automates code reviews and monitors code quality on every commit and pull request.
It gives visibility into the technical debt and can track code style and security issues, code coverage, code
duplication, cyclomatic complexity and enforce best practices.
Codacy is static analysis without the hassle.

With Codacy you have PMDJava analysis out-of-the-box, and it is free for open source projects.

* Homepage: <https://www.codacy.com/>
* Source code: <https://github.com/codacy/codacy-pmdjava>
* Maintainer: Codacy

### Codety

[Codety](https://www.codety.io/) provides comprehensive code scanning solution designed to detect code issues for
30+ programming languages and IaC frameworks. It embeds more than 6,000 code analysis rules (including pmd rules)
and can detect code smells, vulnerable code, secrets in the code, performance issues, style violations, and more.

Codety Scanner provides out-of-the-box PMD integration, it is free for personal and commercial use.

* Homepage: <https://www.codety.io/>
* Source code: <https://github.com/codetyio/codety-scanner>

### Codiga

[Codiga](https://www.codiga.io) automates code review, check your code quality and helps you manage your
technical debt. It is integrated with GitHub, GitLab and Bitbucket. The platform also analyzes code directly in
your IDE using its integration plugins for VS Code and IntelliJ, providing a consistent analysis along your
development cycle (from the IDE to the CI/CD pipeline).

Codiga uses PMD to check Java and Apex code.

* Homepage: <https://www.codiga.io>
* Documentation: <https://doc.codiga.io>

### GitHub Actions

PMD provides its own GitHub Action, that can be integrated in custom workflows.

It can execute PMD with your own ruleset against your project. It creates a
[SARIF](https://docs.oasis-open.org/sarif/sarif/v2.1.0/sarif-v2.1.0.html) report which is uploaded as a
build artifact. Furthermore, the build can be failed based on the number of violations.

The action can also be used as a code scanner to create "Code scanning alerts".

* Homepage: <https://github.com/pmd/pmd-github-action>

### TCA

[Tencent Cloud Code Analysis](http://tca.tencent.com/) (TCA for short, code-named CodeDog inside the company early)
is a comprehensive platform for code analysis and issue tracking. TCA consist of three components, server, web and
client. It integrates of a number of self-developed tools, and also supports dynamic integration of code analysis
tools in various programming languages.

Using TCA can help team find normative, structural, security vulnerabilities and other issues in the code,
continuously monitor the quality of the project code and issue alerts. At the same time, TCA opens up APIs to
support connection with upstream and downstream systems, so as to integrate code analysis capabilities, ensure
code quality, and be more conducive to inheriting an excellent team code culture.

With TCA you have PMD analysis out-of-the-box, and it is open source under the MIT license.

* Homepage: <https://tca.tencent.com/>
* Source code: <https://github.com/Tencent/CodeAnalysis>
* Documentation: <https://tencent.github.io/CodeAnalysis>
* Maintainer: TCA
