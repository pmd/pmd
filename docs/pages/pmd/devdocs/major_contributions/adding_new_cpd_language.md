---
title: How to add a new CPD language
short_title: Add a new CPD language
tags: [devdocs, extending]
summary: How to add a new CPD language
last_updated: July 3, 2016
permalink: pmd_devdocs_major_adding_new_cpd_language.html
author: Romain PELISSE <belaran@gmail.com>
---

If you wish CPD to parse a unsupported language, you can easily develop a new parser for CPD. All you need to is implements the following interface:

*   net.sourceforge.pmd.cpd.Language
*   net.sourceforge.pmd.cpd.Tokenizer

Do not forget to the follow the proper naming convention, as the CPD parser factory use this convention:

*   Language Name + "Language"
*   Tokenizer Name + "Tokenizer"

For instance, if you develop a python parser, you should have two classes named PythonLanguage and PythonTokenizer.

To test your parser, just package it in a jar and add your jar to the classpath.
