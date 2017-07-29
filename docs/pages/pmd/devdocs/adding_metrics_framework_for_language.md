---
title: How to implement a metrics framework for an existing language
short_title: Implement a metrics framework
tags: [customizing]
summary: "PMD's Java module has an extensive framework for the calculation of metrics, which allows rule developers 
to implement and use new code metrics very simply. Most of the functionality of this framework is abstracted in such 
a way that any PMD supported language can implement such a framework without too much trouble. Here's how."
last_updated: July 3, 2016
sidebar: pmd_sidebar
permalink: pmd_devdocs_adding_new_cpd_language.html
folder: pmd/devdocs
---

## Basic steps
* Implement the interface `QualifiedName` in a class. This implementation must be tailored to the target language so 
that it can indentify unambiguously any class and operation in the analysed project (see JavaQualifiedName).
* Determine the AST nodes that correspond to class and method declaration in your language. Both these types must 
implement the interface `QualifiableNode`, which means they must provide a `getQualifiedName` method to find their 
qualified name.
* Implement the interface `Signature<N>`, parameterized with the type of the method AST nodes. Method signatures 
describe basic information about a method, which typically includes most of the modifiers they declare (eg 
visibility, abstract or virtual, etc.). It's up to you to define a 