---
title: Import Statements
summary: These rules deal with different problems that can occur with import statements.
permalink: pmd_rules_java_imports.html
folder: pmd/rules/java
sidebaractiveurl: /pmd_rules_java.html
editmepath: ../pmd-java/src/main/resources/rulesets/java/imports.xml
---
## DontImportJavaLang

**Since:** 0.5

**Priority:** Medium Low (4)

Avoid importing anything from the package 'java.lang'.  These classes are automatically imported (JLS 7.5.3).

**Example(s):**

```
import java.lang.String;	// this is unnecessary

public class Foo {}

// --- in another source code file...

import java.lang.*;	// this is bad

public class Foo {}
```

## DuplicateImports

**Since:** 0.5

**Priority:** Medium Low (4)

Duplicate or overlapping import statements should be avoided.

**Example(s):**

```
import java.lang.String;
import java.lang.*;
public class Foo {}
```

## ImportFromSamePackage

**Since:** 1.02

**Priority:** Medium (3)

There is no need to import a type that lives in the same package.

**Example(s):**

```
package foo;
 
 import foo.Buz; // no need for this
 import foo.*; // or this
 
 public class Bar{}
```

## TooManyStaticImports

**Since:** 4.1

**Priority:** Medium (3)

If you overuse the static import feature, it can make your program unreadable and 
unmaintainable, polluting its namespace with all the static members you import. 
Readers of your code (including you, a few months after you wrote it) will not know 
which class a static member comes from (Sun 1.5 Language Guide).

**Example(s):**

```
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

## UnnecessaryFullyQualifiedName

**Since:** 5.0

**Priority:** Medium Low (4)

Import statements allow the use of non-fully qualified names.  The use of a fully qualified name
which is covered by an import statement is redundant.  Consider using the non-fully qualified name.

**Example(s):**

```
import java.util.List;

public class Foo {
   private java.util.List list1; // Unnecessary FQN
   private List list2; // More appropriate given import of 'java.util.List'
}
```

## UnusedImports

**Since:** 1.0

**Priority:** Medium Low (4)

Avoid the use of unused import statements to prevent unwanted dependencies.

**Example(s):**

```
// this is bad
import java.io.File;
public class Foo {}
```

