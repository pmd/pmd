---
title: Julia support
permalink: pmd_languages_julia.html
last_updated: September 2023 (7.0.0)
tags: [languages, CpdCapableLanguage]
summary: Julia-specific features and guidance
---

> The [Julia](https://julialang.org/) language is dynamically typed, like a scripting language,
> and has good support for interactive use.
> Julia was designed from the beginning for high performance.
> Julia programs compile to efficient native code for multiple platforms via LLVM.

{% include language_info.html name='Julia' id='julia' implementation='julia::lang.julia.JuliaLanguageModule' supports_cpd=true since='7.0.0' %}

## Support in PMD
Starting from version 7.0.0, Julia support was added to CPD.

## Limitations
- Support for Julia only extends to CPD to detect code duplication in Julia source files.
