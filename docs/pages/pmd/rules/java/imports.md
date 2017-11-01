---
title: Import Statements
summary: These rules deal with different problems that can occur with import statements.
permalink: pmd_rules_java_imports.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/imports.xml
keywords: Import Statements, DuplicateImports, DontImportJavaLang, UnusedImports, ImportFromSamePackage, TooManyStaticImports, UnnecessaryFullyQualifiedName
---
## DontImportJavaLang

**Since:** PMD 0.5

**Priority:** Medium Low (4)

Avoid importing anything from the package 'java.lang'.  These classes are automatically imported (JLS 7.5.3).

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.imports.DontImportJavaLangRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/imports/DontImportJavaLangRule.java)

**Example(s):**

``` java
import java.lang.String;    // this is unnecessary

public class Foo {}

// --- in another source code file...

import java.lang.*;         // this is bad

public class Foo {}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/imports.xml/DontImportJavaLang" />
```

## DuplicateImports

**Since:** PMD 0.5

**Priority:** Medium Low (4)

Duplicate or overlapping import statements should be avoided.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.imports.DuplicateImportsRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/imports/DuplicateImportsRule.java)

**Example(s):**

``` java
import java.lang.String;
import java.lang.*;
public class Foo {}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/imports.xml/DuplicateImports" />
```

## ImportFromSamePackage

**Since:** PMD 1.02

**Priority:** Medium (3)

There is no need to import a type that lives in the same package.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.imports.ImportFromSamePackageRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/imports/ImportFromSamePackageRule.java)

**Example(s):**

``` java
package foo;

import foo.Buz;     // no need for this
import foo.*;       // or this

public class Bar{}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/imports.xml/ImportFromSamePackage" />
```

## TooManyStaticImports

**Since:** PMD 4.1

**Priority:** Medium (3)

If you overuse the static import feature, it can make your program unreadable and 
unmaintainable, polluting its namespace with all the static members you import. 
Readers of your code (including you, a few months after you wrote it) will not know 
which class a static member comes from (Sun 1.5 Language Guide).

```
.[count(ImportDeclaration[@Static = 'true']) > $maximumStaticImports]
```

**Example(s):**

``` java
import static Lennon;
import static Ringo;
import static George;
import static Paul;
import static Yoko; // Too much !
```

**This rule has the following properties:**

|Name|Default Value|Description|
|----|-------------|-----------|
|maximumStaticImports|4|All static imports can be disallowed by setting this to 0|

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/imports.xml/TooManyStaticImports" />
```

## UnnecessaryFullyQualifiedName

**Since:** PMD 5.0

**Priority:** Medium Low (4)

Import statements allow the use of non-fully qualified names.  The use of a fully qualified name
which is covered by an import statement is redundant.  Consider using the non-fully qualified name.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.imports.UnnecessaryFullyQualifiedNameRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/imports/UnnecessaryFullyQualifiedNameRule.java)

**Example(s):**

``` java
import java.util.List;

public class Foo {
    private java.util.List list1;   // Unnecessary FQN
    private List list2;             // More appropriate given import of 'java.util.List'
}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/imports.xml/UnnecessaryFullyQualifiedName" />
```

## UnusedImports

**Since:** PMD 1.0

**Priority:** Medium Low (4)

Avoid unused import statements to prevent unwanted dependencies.
This rule will also find unused on demand imports, i.e. import com.foo.*.

**This rule is defined by the following Java class:** [net.sourceforge.pmd.lang.java.rule.imports.UnusedImportsRule](https://github.com/pmd/pmd/blob/master/pmd-java/src/main/java/net/sourceforge/pmd/lang/java/rule/imports/UnusedImportsRule.java)

**Example(s):**

``` java
import java.io.File;  // not referenced or required
import java.util.*;   // not referenced or required

public class Foo {}
```

**Use this rule by referencing it:**
``` xml
<rule ref="rulesets/java/imports.xml/UnusedImports" />
```

