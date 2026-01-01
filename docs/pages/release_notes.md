---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

{% if is_release_notes_processor %}
{% comment %}
This allows to use links e.g. [Basic CLI usage]({{ baseurl }}pmd_userdocs_installation.html) that work both
in the release notes on GitHub (as an absolute url) and on the rendered documentation page (as a relative url).
{% endcomment %}
{% capture baseurl %}https://docs.pmd-code.org/pmd-doc-{{ site.pmd.version }}/{% endcapture %}
{% else %}
{% assign baseurl = "" %}
{% endif %}

## {{ site.pmd.date | date: "%d-%B-%Y" }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### 🚀️ New and noteworthy

### 🌟️ Changed Rules
* The Java rule {%rule java/codestyle/OnlyOneReturn %} has a new property `ignoredMethodNames`. This property by
  default is set to `compareTo` and `equals`, thus this rule now by default allows multiple return statements
  for these methods. To restore the old behavior, simply set this property to an empty value.

### 🐛️ Fixed Issues
* core
  * [#6330](https://github.com/pmd/pmd/issues/6330): \[core] "Unable to create ValueRepresentation" when using @<!-- -->LiteralText (XPath)
* java
  * [#6234](https://github.com/pmd/pmd/issues/6234): \[java] Parser fails to parse switch expressions in super() constructor calls
  * [#6299](https://github.com/pmd/pmd/issues/6299): \[java] Fix grammar of switch label
* java-bestpractices
  * [#4282](https://github.com/pmd/pmd/issues/4282): \[java] GuardLogStatement: False positive when guard is not a direct parent
  * [#6028](https://github.com/pmd/pmd/issues/6028): \[java] UnusedPrivateMethod: False positive with raw type for generic method
  * [#6257](https://github.com/pmd/pmd/issues/6257): \[java] UnusedLocalVariable: False positive with instanceof pattern guard
  * [#6291](https://github.com/pmd/pmd/issues/6291): \[java] EnumComparison: False positive for any object when object.equals(null)
  * [#6328](https://github.com/pmd/pmd/issues/6328): \[java] UnusedLocalVariable: False positive for pattern variable in for-each without braces
* java-codestyle
  * [#4257](https://github.com/pmd/pmd/issues/4257): \[java] OnlyOneReturn: False positive with equals method
  * [#5043](https://github.com/pmd/pmd/issues/5043): \[java] LambdaCanBeMethodReference: False positive on overloaded methods
  * [#6237](https://github.com/pmd/pmd/issues/6237): \[java] UnnecessaryCast: ContextedRuntimeException when parsing switch expression with lambdas
  * [#6279](https://github.com/pmd/pmd/issues/6279): \[java] EmptyMethodInAbstractClassShouldBeAbstract: False positive for final empty methods
  * [#6284](https://github.com/pmd/pmd/issues/6284): \[java] UnnecessaryConstructor: False positive for JavaDoc-bearing constructor
* java-errorprone
  * [#6276](https://github.com/pmd/pmd/issues/6276): \[java] NullAssignment: False positive when assigning null to a final field in a constructor
  * [#6343](https://github.com/pmd/pmd/issues/6343): \[java] MissingStaticMethodInNonInstantiatableClass: False negative when method in nested class returns null
* java-performance
  * [#4158](https://github.com/pmd/pmd/issues/4158): \[java] BigIntegerInstantiation: False negative with compile-time constant
  * [#4910](https://github.com/pmd/pmd/issues/4910): \[java] ConsecutiveAppendsShouldReuse: False positive within if-statement without curly braces
  * [#5877](https://github.com/pmd/pmd/issues/5877): \[java] AvoidArrayLoops: False negative when break inside switch statement
* maintenance
  * [#6230](https://github.com/pmd/pmd/issues/6230): \[core] Single module snapshot build fails

### 🚨️ API Changes

#### Experimental API
* pmd-java: {%jdoc !!java::lang.java.types.OverloadSelectionResult#hadSeveralApplicableOverloads()%}

#### Deprecated API
* The methods {% jdoc core::PMDConfiguration#getClassLoader() %} and
 {% jdoc core::PMDConfiguration#setClassLoader(java.lang.ClassLoader) %}
 have been deprecated for removal. Use the new method {% jdoc core::PMDConfiguration#setAnalysisClasspath(core::util.PmdClasspathConfig) %}
 instead. It is still possible to use a custom ClassLoader to find resources
 during PMD analysis, however, a custom ClassLoader will not be closed by PMD.
 The new API makes that clearer.
  - A similar change has been done to {% jdoc core::lang.JvmLanguagePropertyBundle#getAnalysisClassLoader() %} and
  its setter.
* The methods {% jdoc java::lang.java.types.TypeSystem#usingClasspath(net.sourceforge.pmd.lang.java.symbols.internal.asm.Classpath) %}
  and {% jdoc java::lang.java.types.TypeSystem#usingClassLoaderClasspath(java.lang.ClassLoader) %} have been deprecated.
  See Javadoc for the replacement.


### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}
