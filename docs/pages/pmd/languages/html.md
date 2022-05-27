---
title: Processing HTML files
permalink: pmd_languages_html.html
last_updated: April 2022 (6.45.0)
---

## The HTML language module

**Since:** 6.45.0

**Minimum Java Runtime:** Java 8

{% include warning.html content="This language module is experimental and may change any time." %}

The HTML language module uses [jsoup](https://jsoup.org/) for parsing.

XPath 2.0 rules are supported, but the DOM is not always a typical XML/XPath DOM.
In the Designer, text nodes appear as nodes with name "#text", but they can
be selected as usual using `text()`.

XML Namespaces are not supported. The local name of attributes include the prefix,
so that you have to select attributes by e.g. `//*[@*[local-name() = 'if:true']]`.

Only XPath 1.0 rules are not supported.
