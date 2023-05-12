---
title: JavaScript and TypeScript
permalink: pmd_languages_js_ts.html
tags: [languages]
summary: "JavaScript and TypeScript infos"
---

**JavaScript** support is using [Rhino](https://github.com/mozilla/rhino) for parsing and supports CPD as well as
PMD with rules.

See [Compatibility Table](https://mozilla.github.io/rhino/compat/engines.html) for supported language features.


**TypeScript** is supported for Copy-Paste-Detection only and uses the ANTLR grammar from
[antlr/grammars-v4](https://github.com/antlr/grammars-v4/tree/master/javascript/typescript).
This grammar is published under the [MIT](https://opensource.org/licenses/MIT) license.
