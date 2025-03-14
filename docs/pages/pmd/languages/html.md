---
title: HTML support
permalink: pmd_languages_html.html
last_updated: February 2024 (7.0.0)
tags: [languages, PmdCapableLanguage, CpdCapableLanguage]
summary: "HTML-specific features and guidance"
---

{% include language_info.html name='HTML' id='html' implementation='html::lang.html.HtmlLanguageModule' supports_pmd=true supports_cpd=true since='6.45.0' %}

The HTML language module uses [jsoup](https://jsoup.org/) for parsing.

XPath rules are supported, but the DOM is not always a typical XML/XPath DOM.
In the Designer, text nodes appear as nodes with name "#text", but they can
be selected as usual using `text()`.

XML Namespaces are not supported. The local name of attributes includes the prefix,
so that you have to select attributes by e.g. `//*[@*[local-name() = 'if:true']]`.
