---
title: Error Prone
summary: Rules to detect constructs that are either broken, extremely confusing or prone to runtime errors.
permalink: pmd_rules_pom_errorprone.html
folder: pmd/rules/pom
sidebaractiveurl: /pmd_rules_pom.html
editmepath: ../pmd-xml/src/main/resources/category/pom/errorprone.xml
keywords: Error Prone, InvalidDependencyTypes, ProjectVersionAsDependencyVersion
---
## InvalidDependencyTypes

**Since:** PMD 5.4

**Priority:** Medium (3)

If you use an invalid dependency type in the dependency management section, Maven doesn't fail. Instead,
the entry is just ignored, which might have the effect, that the wrong version of the dependency is used.

The following types are considered valid: pom, jar, maven-plugin, ejb, war, ear, rar, par.

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
<rule ref="category/pom/errorprone.xml/InvalidDependencyTypes" />
```

## ProjectVersionAsDependencyVersion

**Since:** PMD 5.4

**Priority:** Medium (3)

Using that expression in dependency declarations seems like a shortcut, but it can go wrong.
By far the most common problem is the use of 6.1.0-SNAPSHOT in a BOM or parent POM.

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
<rule ref="category/pom/errorprone.xml/ProjectVersionAsDependencyVersion" />
```

