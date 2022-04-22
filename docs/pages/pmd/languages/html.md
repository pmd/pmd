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

XPath rules are supported, but the DOM is not a typical XML/XPath DOM. E.g.
text nodes are normal nodes. This might change in the future.
