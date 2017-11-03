---
title: Errorprone
summary: Rules to detect constructs that are either broken, extremely confusing or prone to runtime errors.
permalink: pmd_rules_pom_errorprone.html
folder: pmd/rules/pom
sidebaractiveurl: /pmd_rules_pom.html
editmepath: ../pmd-xml/src/main/resources/category/pom/errorprone.xml
keywords: Errorprone, InvalidDependencyTypes
---
## InvalidDependencyTypes

**Since:** PMD 5.4

**Priority:** Medium (3)

While Maven will not failed if you use an invalid type for a dependency in the
dependency management section, it will not also uses the dependency.

```
//dependencyManagement/dependency/type/text[not(contains('pom, jar, maven-plugin, ejb, war, ear, rar, par',@Image))]
```

**Example(s):**

``` xml
<project...>
  ...
  <dependencyManagement>
      ...
    <dependency>
      <groupId>org.jboss.arquillian</groupId>
      <artifactId>arquillian-bom</artifactId>
      <version>${arquillian.version}</version>
      <type>bom</type> <!-- not a valid type ! 'pom' is ! -->
      <scope>import</scope>
    </dependency>
    ...
  </dependencyManagement>
</project>
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/pom/errorprone.xml/InvalidDependencyTypes" />
```

