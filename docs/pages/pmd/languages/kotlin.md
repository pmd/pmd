---
title: Kotlin Support
permalink: pmd_languages_kotlin.html
last_updated: September 2023 (7.0.0)
tags: [languages, PmdCapableLanguage, CpdCapableLanguage]
summary: "Kotlin-specific features and guidance"
---

{% include language_info.html name='Kotlin' id='kotlin' implementation='kotlin::lang.kotlin.JspLanguageModule' supports_pmd=true supports_cpd=true %}

Kotlin support in PMD is based on the official grammar from <https://github.com/Kotlin/kotlin-spec>.

Java-based rules and XPath-based rules are supported.

{% include note.html content="Kotlin support has **experimental** stability level, meaning no compatibility should
be expected between even incremental releases. Any functionality can be added, removed or changed without
warning." %}
