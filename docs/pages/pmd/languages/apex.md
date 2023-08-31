---
title: Apex support
permalink: pmd_languages_apex.html
author: Clément Fournier
last_updated: September 2023 (7.0.0)
tags: [languages]
summary: "Apex-specific features and guidance"
---

Implementation: {% jdoc apex::lang.apex.ApexLanguageModule %}
Name: Apex
id: apex
PMD: yes
CPD: yes


{% include warning.html content="Todo for pmd 7" %}

## Metrics framework

In order to use code metrics in Apex, use the metrics constants in {% jdoc apex::lang.apex.metrics.ApexMetrics %},
together with {% jdoc core::lang.metrics.MetricsUtil %}.

## Multifile Analysis

See {% jdoc apex::lang.apex.multifile.ApexMultifileAnalysis %}
Uses [ApexLink](https://github.com/nawforce/apex-link), see also [Apexlink POC #2830](https://github.com/pmd/pmd/pull/2830).

Note: ApexLink new home: https://github.com/apex-dev-tools 

Used for rule {% rule apex/design/UnusedMethod %}

## Language Properties

See [Apex language properties](pmd_languages_configuration.html#apex-language-properties)

## Parser

We use Jorje...

## Limitations

