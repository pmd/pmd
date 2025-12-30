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

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->
* [#6262](https://github.com/pmd/pmd/pull/6262): \[java] UnusedLocalVariable: fix false positive with guard in switch - [Zbynek Konecny](https://github.com/zbynek) (@zbynek)
* [#6285](https://github.com/pmd/pmd/pull/6285): \[java] Fix #5043: FP in LambdaCanBeMethodReference when method ref would be ambiguous - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#6287](https://github.com/pmd/pmd/pull/6287): \[doc] Explain how to build or pull snapshot dependencies for single module builds - [Marcel](https://github.com/mrclmh) (@mrclmh)
* [#6288](https://github.com/pmd/pmd/pull/6288): \[java] Fix #6279: EmptyMethodInAbstractClassShouldBeAbstract should ignore final methods - [Marcel](https://github.com/mrclmh) (@mrclmh)
* [#6292](https://github.com/pmd/pmd/pull/6292): \[java] Fix #6291: EnumComparison FP when comparing with null - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#6293](https://github.com/pmd/pmd/pull/6293): \[java] Fix #6276: NullAssignment should not report assigning null to a final field in a constructor - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#6294](https://github.com/pmd/pmd/pull/6294): \[java] Fix #6028: UnusedPrivateMethod FP - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#6295](https://github.com/pmd/pmd/pull/6295): \[java] Fix #6237: UnnecessaryCast error with switch expr returning lambdas - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#6296](https://github.com/pmd/pmd/pull/6296): \[java] Fix #4282: GuardLogStatement only detects guard methods immediately around it - [Marcel](https://github.com/mrclmh) (@mrclmh)
* [#6299](https://github.com/pmd/pmd/pull/6299): \[java] Fix grammar of switch label - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#6309](https://github.com/pmd/pmd/pull/6309): \[java] Fix #4257: Allow ignoring methods in OnlyOneReturn - [Marcel](https://github.com/mrclmh) (@mrclmh)
* [#6311](https://github.com/pmd/pmd/pull/6311): \[java] Fix #6284: UnnecessaryConstructor reporting false-positive on JavaDoc-bearing constructor - [Marcel](https://github.com/mrclmh) (@mrclmh)
* [#6313](https://github.com/pmd/pmd/pull/6313): \[java] Fix #4910: if-statement triggers ConsecutiveAppendsShouldReuse - [Marcel](https://github.com/mrclmh) (@mrclmh)
* [#6316](https://github.com/pmd/pmd/pull/6316): \[java] Fix #5877: AvoidArrayLoops false-negative when break inside switch statement - [Marcel](https://github.com/mrclmh) (@mrclmh)
* [#6342](https://github.com/pmd/pmd/pull/6342): \[core] Fix #6330: Cannot access Chars attribute from XPath - [Clément Fournier](https://github.com/oowekyala) (@oowekyala)
* [#6344](https://github.com/pmd/pmd/pull/6344): \[java] Fix #6328: UnusedLocalVariable should consider pattern variable in for-each without curly braces - [Mohamed Hamed](https://github.com/mdhamed238) (@mdhamed238)
* [#6348](https://github.com/pmd/pmd/pull/6348): \[jsp] Fix malformed Javadoc HTML in JspDocStyleTest - [Gianmarco](https://github.com/gianmarcoschifone) (@gianmarcoschifone)
* [#6359](https://github.com/pmd/pmd/pull/6359): \[java] Fix #6234: Parser fails to parse switch expressions in super() constructor calls - [Mohamed Hamed](https://github.com/mdhamed238) (@mdhamed238)
* [#6360](https://github.com/pmd/pmd/pull/6360): \[java] Fix #4158: BigIntegerInstantiation false-negative with compile-time constant - [Lukas Gräf](https://github.com/lukasgraef) (@lukasgraef)
* [#6361](https://github.com/pmd/pmd/pull/6361): \[vf] Fix invalid Javadoc syntax in VfDocStyleTest - [Gianmarco](https://github.com/gianmarcoschifone) (@gianmarcoschifone)
* [#6363](https://github.com/pmd/pmd/pull/6363): \[apex] Add sca-extra ruleset for Salesforce Apex testing - [Beech Horn](https://github.com/metalshark) (@metalshark)

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->
* [#6286](https://github.com/pmd/pmd/pull/6286): Bump PMD from 7.18.0 to 7.19.0
* [#6300](https://github.com/pmd/pmd/pull/6300): chore(deps): bump actions/checkout from 6.0.0 to 6.0.1
* [#6301](https://github.com/pmd/pmd/pull/6301): chore(deps): bump org.checkerframework:checker-qual from 3.52.0 to 3.52.1
* [#6302](https://github.com/pmd/pmd/pull/6302): chore(deps): bump org.apache.maven.plugins:maven-resources-plugin from 3.3.1 to 3.4.0
* [#6303](https://github.com/pmd/pmd/pull/6303): chore(deps-dev): bump net.bytebuddy:byte-buddy from 1.18.1 to 1.18.2
* [#6304](https://github.com/pmd/pmd/pull/6304): chore(deps): bump com.puppycrawl.tools:checkstyle from 12.1.2 to 12.2.0
* [#6305](https://github.com/pmd/pmd/pull/6305): chore(deps): bump org.sonarsource.scanner.maven:sonar-maven-plugin from 5.3.0.6276 to 5.4.0.6343
* [#6306](https://github.com/pmd/pmd/pull/6306): chore(deps): bump webrick from 1.9.1 to 1.9.2 in /docs
* [#6318](https://github.com/pmd/pmd/pull/6318): chore(deps): bump actions/create-github-app-token from 2.2.0 to 2.2.1
* [#6319](https://github.com/pmd/pmd/pull/6319): chore(deps): bump actions/setup-java from 5.0.0 to 5.1.0
* [#6320](https://github.com/pmd/pmd/pull/6320): chore(deps): bump ruby/setup-ruby from 1.268.0 to 1.269.0
* [#6321](https://github.com/pmd/pmd/pull/6321): chore(deps-dev): bump net.bytebuddy:byte-buddy-agent from 1.18.1 to 1.18.2
* [#6323](https://github.com/pmd/pmd/pull/6323): chore(deps): bump com.google.protobuf:protobuf-java from 4.33.1 to 4.33.2
* [#6324](https://github.com/pmd/pmd/pull/6324): chore(deps): bump io.github.apex-dev-tools:apex-ls_2.13 from 6.0.1 to 6.0.2
* [#6325](https://github.com/pmd/pmd/pull/6325): chore(deps): bump org.apache.maven.plugins:maven-assembly-plugin from 3.7.1 to 3.8.0
* [#6329](https://github.com/pmd/pmd/pull/6329): chore(deps): bump org.mozilla:rhino from 1.7.15 to 1.7.15.1
* [#6331](https://github.com/pmd/pmd/pull/6331): chore(deps): bump actions/upload-artifact from 5.0.0 to 6.0.0
* [#6332](https://github.com/pmd/pmd/pull/6332): chore(deps): bump org.mockito:mockito-core from 5.20.0 to 5.21.0
* [#6333](https://github.com/pmd/pmd/pull/6333): chore(deps): bump actions/download-artifact from 6.0.0 to 7.0.0
* [#6334](https://github.com/pmd/pmd/pull/6334): chore(deps): bump ruby/setup-ruby from 1.269.0 to 1.270.0
* [#6335](https://github.com/pmd/pmd/pull/6335): chore(deps): bump com.puppycrawl.tools:checkstyle from 12.2.0 to 12.3.0
* [#6336](https://github.com/pmd/pmd/pull/6336): chore(deps): bump actions/cache from 4.3.0 to 5.0.1
* [#6337](https://github.com/pmd/pmd/pull/6337): chore(deps): bump bigdecimal from 3.3.1 to 4.0.0 in /docs
* [#6339](https://github.com/pmd/pmd/pull/6339): chore(deps): bump org.apache.maven.plugins:maven-release-plugin from 3.2.0 to 3.3.1
* [#6341](https://github.com/pmd/pmd/pull/6341): chore(deps): bump org.apache.maven.plugins:maven-source-plugin from 3.3.1 to 3.4.0
* [#6347](https://github.com/pmd/pmd/pull/6347): chore(deps-dev): bump org.apache.logging.log4j:log4j-core from 2.25.2 to 2.25.3 in /pmd-java
* [#6350](https://github.com/pmd/pmd/pull/6350): chore(deps): bump com.github.siom79.japicmp:japicmp-maven-plugin from 0.24.2 to 0.25.1
* [#6352](https://github.com/pmd/pmd/pull/6352): chore(deps): bump ruby/setup-ruby from 1.270.0 to 1.275.0
* [#6353](https://github.com/pmd/pmd/pull/6353): chore(deps): bump org.ow2.asm:asm from 9.9 to 9.9.1
* [#6354](https://github.com/pmd/pmd/pull/6354): chore(deps-dev): bump net.bytebuddy:byte-buddy-agent from 1.18.2 to 1.18.3
* [#6356](https://github.com/pmd/pmd/pull/6356): chore(deps): bump org.sonarsource.scanner.maven:sonar-maven-plugin from 5.4.0.6343 to 5.5.0.6356
* [#6357](https://github.com/pmd/pmd/pull/6357): chore(deps): bump org.apache.commons:commons-text from 1.14.0 to 1.15.0
* [#6358](https://github.com/pmd/pmd/pull/6358): chore(deps): bump bigdecimal from 4.0.0 to 4.0.1 in /docs

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->
* 102 commits
* 39 closed tickets & PRs
* Days since last release: 32

{% endtocmaker %}
