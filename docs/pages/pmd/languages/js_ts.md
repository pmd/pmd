---
title: JavaScript and TypeScript support
permalink: pmd_languages_js_ts.html
last_updated: September 2023 (7.0.0)
tags: [languages, PmdCapableLanguage, CpdCapableLanguage]
summary: "JavaScript- and TypeScript-specific features and guidance"
---

{% include language_info.html name='JavaScript' id='ecmascript' implementation='javascript::lang.ecmascript.EcmascriptLanguageModule' supports_pmd=true supports_cpd=true %}
{% include language_info.html name='TypeScript' id='ts' implementation='javascript::lang.typescript.TsLanguageModule' supports_cpd=true since='7.0.0' %}

## JavaScript

**JavaScript** support is using [Rhino](https://github.com/mozilla/rhino) for parsing and supports CPD as well as
PMD with rules.

See [Compatibility Table](https://mozilla.github.io/rhino/compat/engines.html) for supported language features.

## TypeScript

**TypeScript** is supported for Copy-Paste-Detection only and uses the ANTLR grammar from
[antlr/grammars-v4](https://github.com/antlr/grammars-v4/tree/master/javascript/typescript).
This grammar is published under the [MIT](https://opensource.org/licenses/MIT) license.
