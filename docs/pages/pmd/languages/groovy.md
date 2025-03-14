---
title: Groovy support
permalink: pmd_languages_groovy.html
last_updated: September 2023 (7.0.0)
tags: [languages, CpdCapableLanguage]
summary: "Groovy-specific features and guidance"
---

> [Apache Groovy](https://groovy-lang.org/) is a powerful, optionally typed and dynamic language, with static-typing and
> static compilation capabilities, for the Java platform aimed at improving developer productivity thanks to a concise,
> familiar and easy to learn syntax.

{% include language_info.html name='Groovy' id='groovy' implementation='groovy::lang.groovy.GroovyLanguageModule' supports_cpd=true since='5.5.2' %}

## Support in PMD
Groovy support was added with PMD 5.5.2. With PMD 7.0.0, support for Groovy 3 and 4 was added.

Since PMD 7.0.0, the Groovy module supports [suppression](pmd_userdocs_cpd.html#suppression) through `CPD-ON`/`CPD-OFF` comment pairs.

### Limitations
- Support for Groovy only extends to CPD to detect code duplication.
