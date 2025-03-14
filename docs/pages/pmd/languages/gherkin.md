---
title: Gherkin support
permalink: pmd_languages_gherkin.html
last_updated: September 2023 (7.0.0)
tags: [languages, CpdCapableLanguage]
summary: "Gherkin features and guidance"
---

The [Gherkin](https://cucumber.io/docs/gherkin/) language is used to define test cases for the
[Cucumber](https://cucumber.io/) testing tool for behavior-driven development.
The Gherkin syntax is designed to be non-technical, making it human-readable for a wide audience.

{% include language_info.html name='Gherkin' id='gherkin' implementation='gherkin::lang.gherkin.GherkinLanguageModule' supports_cpd=true since='6.48.0' %}

## Support in PMD
Starting from version 6.48.0, Gherkin support was added to CPD.

### Limitations
- Support for Gherkin only extends to CPD to detect code duplication in Cucumber test cases. 
- While Gherkin keywords have been translated into various
languages, CPD currently supports only the English version of the Gherkin language.
