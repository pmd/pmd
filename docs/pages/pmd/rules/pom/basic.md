---
title: Basic POM
summary: The Basic POM Ruleset contains a collection of good practices regarding Maven's POM files.
permalink: pmd_rules_pom_basic.html
folder: pmd/rules/pom
sidebaractiveurl: /pmd_rules_pom.html
editmepath: ../pmd-xml/src/main/resources/rulesets/pom/basic.xml
keywords: Basic POM, ProjectVersionAsDependencyVersion, InvalidDependencyTypes
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
<rule ref="rulesets/pom/basic.xml/InvalidDependencyTypes" />
```

## ProjectVersionAsDependencyVersion

**Since:** PMD 5.4

**Priority:** Medium (3)

Using that expression in dependency declarations seems like a shortcut, but it can go wrong.
By far the most common problem is the use of 6.0.0-SNAPSHOT in a BOM or parent POM.

```
//dependency/version/text[contains(@Image,'{project.version}')]
```

**Example(s):**

``` xml
<project...>
  ...
  <dependency>
    ...
    <version>${project.dependency}</version>
  </dependency>
</project>
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/pom/basic.xml/ProjectVersionAsDependencyVersion" />
```

