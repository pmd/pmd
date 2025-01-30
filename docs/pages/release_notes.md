---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## {{ site.pmd.date | date: "%d-%B-%Y" }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### 🚀 New and noteworthy

#### New GPG Release Signing Key

Since January 2025, we switched the GPG Key we use for signing releases in Maven Central to be
[A0B5CA1A4E086838](https://keyserver.ubuntu.com/pks/lookup?search=0x2EFA55D0785C31F956F2F87EA0B5CA1A4E086838&fingerprint=on&op=index).
The full fingerprint is `2EFA 55D0 785C 31F9 56F2  F87E A0B5 CA1A 4E08 6838`.

This step was necessary, as the passphrase of the old key has been compromised and therefore the key is not
safe to use anymore. While the key itself is not compromised as far as we know, we still decided to generate a
new key, just to be safe. As until now (January 2025) we are not aware, that the key actually has been misused.
The previous releases of PMD in Maven Central can still be considered untampered, as Maven Central is read-only.

This unexpected issue was discovered while checking [Reproducible Builds](https://reproducible-builds.org/) by a
third party.

The compromised passphrase is tracked as [GHSA-88m4-h43f-wx84](https://github.com/pmd/pmd/security/advisories/GHSA-88m4-h43f-wx84)
and [CVE-2025-23215](https://www.cve.org/CVERecord?id=CVE-2025-23215).

#### Updated PMD Designer

This PMD release ships a new version of the pmd-designer.
For the changes, see [PMD Designer Changelog (7.10.0)](https://github.com/pmd/pmd-designer/releases/tag/7.10.0).

### 🌟 New and changed rules

#### New Rules

* The new Java rule {%rule java/bestpractices/ExhaustiveSwitchHasDefault %} finds switch statements and
  expressions, that cover already all cases but still have a default case. This default case is unnecessary
  and prevents getting compiler errors when e.g. new enum constants are added without extending the switch.

### 🐛 Fixed Issues
* apex
  * [#5388](https://github.com/pmd/pmd/issues/5388): \[apex] Parse error with time literal in SOQL query
  * [#5456](https://github.com/pmd/pmd/issues/5456): \[apex] Issue with java dependency apex-parser-4.3.1 but apex-parser-4.3.0 works
* apex-security
  * [#3158](https://github.com/pmd/pmd/issues/3158): \[apex] ApexSuggestUsingNamedCred false positive with Named Credential merge fields
* documentation
  * [#2492](https://github.com/pmd/pmd/issues/2492): \[doc] Promote wiki pages to standard doc pages
* java-performance
  * [#5311](https://github.com/pmd/pmd/issues/5311): \[java] TooFewBranchesForSwitch false positive for exhaustive switches over enums without default case

### 🚨 API Changes

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#5412](https://github.com/pmd/pmd/pull/5412): \[java] Support exhaustive switches - [Andreas Dangel](https://github.com/adangel) (@adangel)
* [#5488](https://github.com/pmd/pmd/pull/5488): \[apex] Fix #3158: Recognize Named Credentials merge fields in ApexSuggestUsingNamedCredRule - [William Brockhus](https://github.com/YodaDaCoda) (@YodaDaCoda)

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

