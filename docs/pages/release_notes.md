---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

## {{ site.pmd.date }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### New and noteworthy

#### New rules

* The new Java rule {% rule java/design/InvalidJavaBean %} identifies beans, that don't follow the [JavaBeans API specification](https://download.oracle.com/otndocs/jcp/7224-javabeans-1.01-fr-spec-oth-JSpec/),
  like beans with missing getters or setters.

```xml
<rule ref="category/java/design.xml/InvalidJavaBean"/>
```


### Fixed Issues
* java-design
    * [#4177](https://github.com/pmd/pmd/issues/4177): \[java] New Rule InvalidJavaBean

### API Changes

### External Contributions
* [#4184](https://github.com/pmd/pmd/pull/4184): \[java]\[doc] TestClassWithoutTestCases - fix small typo in description - [Valery Yatsynovich](https://github.com/valfirst) (@valfirst)
* [#4198](https://github.com/pmd/pmd/pull/4198): \[doc] Add supported CPD languages - [Jeroen van Wilgenburg](https://github.com/jvwilge) (@jvwilge)

{% endtocmaker %}

