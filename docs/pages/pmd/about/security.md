---
title: PMD Security
permalink: pmd_about_security.html
author: Andreas Dangel <andreas.dangel@pmd-code.org>
last_updated: January 2025 (7.11.0)
---

For reporting security issues, see [SECURITY.md](https://github.com/pmd/pmd/blob/main/SECURITY.md).

## Security Vulnerabilities

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
