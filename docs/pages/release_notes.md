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

### New GPG Release Signing Key

Since January 2025, we switched the GPG Key we use for signing releases in Maven Central to be
[A0B5CA1A4E086838](https://keyserver.ubuntu.com/pks/lookup?search=0x2EFA55D0785C31F956F2F87EA0B5CA1A4E086838&fingerprint=on&op=index).
The full fingerprint is `2EFA 55D0 785C 31F9 56F2  F87E A0B5 CA1A 4E08 6838`.

This step was necessary, as the passphrase of the old key has been compromised and therefore the key is not
safe to use anymore. While the key itself is not compromised as far as we know, we still decided to generate a
new key, just to be safe. As until now (January 2025) we are not aware, that the key actually has been misused.
The previous releases of PMD in Maven Central can still be considered untampered, as Maven Central is read-only.

### 🐛 Fixed Issues

### 🚨 API Changes

### ✨ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦 Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈 Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

