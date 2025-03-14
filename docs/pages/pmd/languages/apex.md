---
title: Apex support
permalink: pmd_languages_apex.html
last_updated: September 2023 (7.0.0)
author: Clément Fournier
tags: [languages, PmdCapableLanguage, CpdCapableLanguage]
summary: "Apex-specific features and guidance"
---

> [Apex](https://developer.salesforce.com/docs/atlas.en-us.apexcode.meta/apexcode/apex_dev_guide.htm) is a strongly
> typed, object-oriented programming language that allows developers to execute flow and
> transaction control statements on the Salesforce Platform server, in conjunction with calls to the API.

{% include language_info.html name='Apex' id='apex' implementation='apex::lang.apex.ApexLanguageModule' supports_pmd=true supports_cpd=true since='5.5.0' %}

## Metrics framework

In order to use code metrics in Apex, use the metrics constants in {% jdoc apex::lang.apex.metrics.ApexMetrics %},
together with {% jdoc core::lang.metrics.MetricsUtil %}.

## Multifile Analysis

Integration happens in {% jdoc apex::lang.apex.multifile.ApexMultifileAnalysis %}. It uses
[ApexLink](https://github.com/nawforce/apex-link). For detailed information, see also [Apexlink POC #2830](https://github.com/pmd/pmd/pull/2830).

{% include note.html content="ApexLink's new home: <https://github.com/apex-dev-tools>" %}

Used for rule {% rule apex/design/UnusedMethod %}

## Language Properties

See [Apex language properties](pmd_languages_configuration.html#apex-language-properties)

## Parser

Since PMD 7.0.0 we use the open source [apex-parser](https://github.com/apex-dev-tools/apex-parser),
together with [Summit AST](https://github.com/google/summit-ast) which translates the ANTLR parse tree
into an AST.

When PMD added Apex support with version 5.5.0, it utilized the Apex Jorje library to parse Apex source
and generate an AST. This library is however a binary-blob provided as part of the
[Salesforce Extensions for VS Code](https://github.com/forcedotcom/salesforcedx-vscode), and it is closed-source.
