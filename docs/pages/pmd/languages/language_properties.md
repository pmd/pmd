---
title: Language configuration
permalink: pmd_languages_configuration.html
author: Clément Fournier
last_updated: July 2022 (7.0.0)
tags: [languages]
keywords: [pmd, cpd, options, command, auxclasspath, language, properties]
summary: "Summary of language configuration options and properties"
---

# Language properties

Since PMD 7.0.0, languages may be directly configured via properties.

TODO describe CLI syntax
TODO describe env var syntax

As a convention, properties whose name start with an *x* are internal and may be
removed or changed without notice.

## Common language properties

All languages support the following properties:
- `suppressMarker`: A string to detect suppression comments. The default is `NOPMD`,
so e.g. in Java, a comment `// NOPMD` will suppress warnings on the same line.

## Java language properties

The Java language can be configured with the following properties:
- `auxClasspath`: Classpath on which to find compiled classes for the language
- `xTypeInferenceLogging`: Verbosity of type inference logging, possible values `DISABLED`, `SIMPLE`, `VERBOSE`

## Apex language properties

- `rootDirectory`:

## VisualForce language properties

- `apexDirectories`: Comma separated list of directories for Apex classes. Absolute
 or relative to the Visualforce directory. Default is `../classes`. Specifying an
 empty string will disable data type resolution for Apex Controller properties.
- `objectsDirectories`: Comma separated list of directories for Custom Objects.
Absolute or relative to the Visualforce directory. Default is `../objects`.
Specifying an empty string will disable data type resolution for Custom Object fields.

