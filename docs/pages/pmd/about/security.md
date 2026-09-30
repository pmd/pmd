---
title: PMD Security
permalink: pmd_about_security.html
author: Andreas Dangel <andreas.dangel@pmd-code.org>
last_updated: October 2026 (7.29.0)
---

For reporting security issues, see [SECURITY.md](https://github.com/pmd/pmd/blob/main/SECURITY.md).

## Security Vulnerabilities
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

1. Stage a secret outside the project and two symlinks inside it

```shell
mkdir -p project OUTSIDE_THE_REPO
printf 'AWS_SECRET_ACCESS_KEY = wJalrXUtnFEMI...EXAMPLEKEY ;\nDATABASE_PASSWORD = super_secret_prod_password_12345 ;\nGITHUB_TOKEN = ghp_fake_token_for_poc_000 ;\nSTRIPE_KEY = sk_live_fake_key_for_poc_000 ;\nMORE = padding to exceed the token threshold ;\n' > OUTSIDE_THE_REPO/ci_secret.env
ln -s ../OUTSIDE_THE_REPO/ci_secret.env project/copy_a.java
ln -s ../OUTSIDE_THE_REPO/ci_secret.env project/copy_b.java
```

The project directory contains only two symlinks. The secret lives outside it.

2. Run CPD the way a CI job does

```shell
pmd cpd --minimum-tokens 10 --language java --dir project --format text
```

3. Read the target file's contents back out of the report

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
*   See [#PR TODO](https://github.com/pmd/pmd/pull/TODO) \[core] Don't follow symlinks in FileCollector

**References:**
*   [GHSA-wj6q-xw9m-26p6](https://github.com/pmd/pmd/security/advisories/GHSA-wj6q-xw9m-26p6)



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

**Fixes**
* See [#6475](https://github.com/pmd/pmd/issues/6475): \[core] Fix stored XSS in VBHTMLRenderer and YAHTMLRenderer

**References:**
* [GHSA-8rr6-2qw5-pc7r](https://github.com/pmd/pmd/security/advisories/GHSA-8rr6-2qw5-pc7r)
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

**Details**
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

**PoC**
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

**Fixes**
* Reworked build script in PMD Designer to not include all system properties
    * https://github.com/pmd/pmd-designer/commit/1548f5f27ba2981b890827fecbd0612fa70a0362
    * https://github.com/pmd/pmd-designer/commit/e87a45312753ec46b3e5576c6f6ac1f7de2f5891


**References:**
* [GHSA-88m4-h43f-wx84](https://github.com/pmd/pmd/security/advisories/GHSA-88m4-h43f-wx84)
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
