---
title: Kotlin Support
permalink: pmd_languages_kotlin.html
last_updated: June 2026 (7.26.0)
tags: [languages, PmdCapableLanguage, CpdCapableLanguage]
summary: "Kotlin-specific features and guidance"
---

[Kotlin](https://kotlinlang.org/) support in PMD is based on the official grammar from <https://github.com/Kotlin/kotlin-spec>.

{% include language_info.html name='Kotlin' id='kotlin' implementation='kotlin::lang.kotlin.KotlinLanguageModule' supports_pmd=true supports_cpd=true since=7.0.0 %}

Java-based rules and XPath-based rules are supported.

## Kotlin language versions / feature support

PMD's Kotlin parser may lag behind the latest Kotlin compiler and does not aim to implement all new language features immediately.

The only Kotlin 2.x feature currently supported is **"Multidollar interpolation: improved handling of $ in string literals"**.
This feature was preview in Kotlin 2.1.0 and stabilized in Kotlin 2.2.0.
See [Multi-dollar string interpolation](https://kotlinlang.org/docs/strings.html#multi-dollar-string-interpolation).

For informational purposes, PMD advertises Kotlin 2.2.0 as the highest supported version.
Selecting a different Kotlin language version does not currently change the parser behavior (unlike Java).

Other Kotlin 2.0.0 / 2.1.0 / 2.2.0 / 2.3.0 language features are not supported at this time.
PRs to improve Kotlin parser coverage are welcome.

## Providing the auxiliary classpath

The auxiliary classpath (or short "auxClasspath") is configured via the
[Language Property "auxClasspath"](pmd_languages_configuration.html#kotlin-language-properties).
It is a string containing multiple paths separated by either a colon (`:`) under Linux/MacOS
or a semicolon (`;`) under Windows. This property can be provided on the CLI with parameter
[`--aux-classpath`](pmd_userdocs_cli_reference.html#-aux-classpath).

Not providing the correct auxClasspath might result in false positives or negatives for some rules, that depend on resolved types.

Type resolution for Kotlin is powered by [kotlin-type-mapper](https://github.com/stokpop/kotlin-type-mapper),
which is bundled with PMD.

## Language Properties

See [Kotlin language properties](pmd_languages_configuration.html#kotlin-language-properties)
