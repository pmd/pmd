---
title: Codestyle
summary: Rules which enforce a specific coding style.
permalink: pmd_rules_pom_codestyle.html
folder: pmd/rules/pom
sidebaractiveurl: /pmd_rules_pom.html
editmepath: ../pmd-xml/src/main/resources/category/pom/codestyle.xml
keywords: Codestyle, ProjectVersionAsDependencyVersion
---
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
<rule ref="rulesets/pom/codestyle.xml/ProjectVersionAsDependencyVersion" />
```

