---
title: Language configuration
permalink: pmd_languages_configuration.html
author: Clément Fournier
last_updated: February 2024 (7.0.0)
tags: [languages]
keywords: [pmd, cpd, options, command, auxclasspath, language, properties]
summary: "Summary of language configuration options and properties"
---

# Language properties

Since PMD 7.0.0, languages may be directly configured via properties.
The properties can be specified via environment variables or programmatically.

The name of the environment variables follow the following pattern,
completely in uppercase:

    PMD_<LanguageId>_<PropertyName>

LanguageId is the short name of the language, which is being configured. This is e.g. "JAVA" or "APEX".

PropertyName is the name of the property converted to SCREAMING_SNAKE_CASE, that is set to a specific value, e.g. "SUPPRESS_MARKER" for "suppressMarker".

As a convention, properties whose name start with an *x* are internal and may be removed or changed without notice.

Properties whose name start with **CPD** are used to configure CPD CpdLexer options.

Programmatically, the language properties can be set on `PMDConfiguration` (or `CPDConfiguration`) before using the
{%jdoc core::PmdAnalysis %} (or {%jdoc core::cpd.CpdAnalysis %}) instance
to start the analysis:

```java
PMDConfiguration configuration = new PMDConfiguration();
LanguagePropertyBundle properties = configuration.getLanguageProperties(LanguageRegistry.PMD.getLanguageById("java"));
properties.setProperty(LanguagePropertyBundle.SUPPRESS_MARKER, "PMD");
```

## Common language properties

All languages support the following properties:

- `suppressMarker`: A string to detect suppression comments. The default is `NOPMD`, so e.g. in Java, a
  comment `// NOPMD` will suppress warnings on the same line.

  This property can also be set via the CLI option `--suppress-marker`. The CLI option applies for all languages
  and overrides any language property.

- `version`: The language version PMD should use when parsing source code. If not specified, the default
  version of the language will be used.

  This property can also be set via the CLI option `--use-version`.

## CPD language properties

Many languages support the following properties, which are centrally defined in {% jdoc core::cpd.CpdLanguageProperties %}:

- `cpdIgnoreLiteralSequences`: Ignore sequences of literals, eg `0, 0, 0, 0`.

  This property can also be set via the CLI option `--ignore-literal-sequences`.

- `cpdIgnoreLiteralAndIdentifierSequences`: Ignore sequences of literals and identifiers, eg `a, b, 0, 0`.

  This property can also be set via the CLI option `--ignore-sequences`.

- `cpdAnonymizeLiterals`: Anonymize literals. They are still part of the token stream but all literals appear to have
  the same value.

  This property can also be set via the CLI option `--ignore-literals`.

- `cpdAnonymizeIdentifiers`: Anonymize identifiers. They are still part of the token stream but all identifiers
  appear to have the same value.

  This property can also be set via the CLI option `--ignore-identifiers`.

- `cpdIgnoreImports`: Ignore import statements and equivalent (eg using statements in C#).

  This property can also be set via the CLI option `--ignore-usings`.

- `cpdIgnoreMetadata`: Ignore metadata such as Java annotations or C# attributes.

  This property can also be set via the CLI option `--ignore-annotations`.

Note: {% jdoc core::cpd.CPDConfiguration %} has convenience methods to control these options, e.g.
{% jdoc core::cpd.CPDConfiguration#setIgnoreAnnotations(boolean) %}.

## Java language properties

The Java language can be configured with the following properties:

- `auxClasspath`: Classpath on which to find compiled classes for the language

  This property can also be set via the CLI option `--aux-classpath`.

  Environment variable: `PMD_JAVA_AUX_CLASSPATH`

- `xTypeInferenceLogging`: Verbosity of type inference logging, possible values `DISABLED`, `SIMPLE`, `VERBOSE`.

  Environment variable: `PMD_JAVA_X_TYPE_INFERENCE_LOGGING`

## Apex language properties

- `rootDirectory`: With this property the root directory of the Salesforce metadata, where `sfdx-project.json`
  resides, is specified. [ApexLink](https://github.com/nawforce/ApexLink) can then load all the classes
  in the project and figure out, whether a method is used or not.

  This property is needed for {% rule apex/design/UnusedMethod %}.

  Environment variable: `PMD_APEX_ROOT_DIRECTORY`

## Visualforce language properties

- `apexDirectories`: Comma separated list of directories for Apex classes. Absolute
  or relative to the Visualforce directory. Default is `../classes`. Specifying an
  empty string will disable data type resolution for Apex Controller properties.

  Environment variable: `PMD_VISUALFORCE_APEX_DIRECTORIES`

- `objectsDirectories`: Comma separated list of directories for Custom Objects.
  Absolute or relative to the Visualforce directory. Default is `../objects`.
  Specifying an empty string will disable data type resolution for Custom Object fields.

  Environment variable: `PMD_VISUALFORCE_OBJECTS_DIRECTORIES`

## CPP language properties

- `cpdSkipBlocksPattern`: Specifies a start and end delimiter for CPD to completely ignore.
  The delimiters are separated by a pipe `|`. The default skips code
  that is conditionally compiled out. Set this property to empty to disable this.

  This property can also be set via the CLI option `--skip-blocks-pattern`.
