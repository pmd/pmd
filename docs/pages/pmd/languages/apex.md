---
title: Apex support
permalink: pmd_languages_apex.html
author: Clément Fournier
last_updated: September 2023 (7.0.0)
tags: [languages]
summary: "Apex-specific features and guidance"
---

{% include language_info.html name='Apex' id='apex' implementation='apex::lang.apex.ApexLanguageModule' supports_pmd='✔️' supports_cpd='✔️' %}

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

