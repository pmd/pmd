---
title: Kotlin Support
permalink: pmd_languages_kotlin.html
last_updated: June 2026 (7.26.0)
tags: [languages, PmdCapableLanguage, CpdCapableLanguage]
summary: "Kotlin-specific features and guidance"
---

[Kotlin](https://kotlinlang.org/) support in PMD is based on the official grammar from <https://github.com/Kotlin/kotlin-spec>.

{% include language_info.html name='Kotlin' id='kotlin' implementation='kotlin::lang.kotlin.KotlinLanguageModule' supports_pmd=true supports_cpd=true since=7.0.0 %}

Java-based rules and XPath-based rules are supported.

## Kotlin language versions / feature support

PMD's Kotlin parser may lag behind the latest Kotlin compiler and does not aim to implement all new language features immediately.

The only Kotlin 2.x feature currently supported is **"Multidollar interpolation: improved handling of $ in string literals"**.
This feature was preview in Kotlin 2.1.0 and stabilized in Kotlin 2.2.0.
See [Multi-dollar string interpolation](https://kotlinlang.org/docs/strings.html#multi-dollar-string-interpolation).

For informational purposes, PMD advertises Kotlin 2.2.0 as the highest supported version.
Selecting a different Kotlin language version does not currently change the parser behavior (unlike Java).

Other Kotlin 2.0.0 / 2.1.0 / 2.2.0 / 2.3.0 language features are not supported at this time.
PRs to improve Kotlin parser coverage are welcome.

## Providing the auxiliary classpath

The auxiliary classpath (or short "auxClasspath") is configured via the
[Language Property "auxClasspath"](pmd_languages_configuration.html#kotlin-language-properties).
It is a string containing multiple paths separated by either a colon (`:`) under Linux/MacOS
or a semicolon (`;`) under Windows. This property can be provided on the CLI with parameter
[`--aux-classpath`](pmd_userdocs_cli_reference.html#-aux-classpath).

Not providing the correct auxClasspath might result in false positives or negatives for some rules, that depend on resolved types.

Type resolution for Kotlin is powered by [kotlin-type-mapper](https://github.com/stokpop/kotlin-type-mapper),
which is bundled with PMD.

## XPath rule support

Kotlin now provides XPath-queryable attributes and helper functions for type-aware rules.

### Type-info attributes

The following attributes are available on declaration nodes when `auxClasspath` is configured
and the kotlin-type-mapper analysis has resolved the types:

| Attribute | Nodes | Meaning |
|-----------|-------|---------|
| `@TypeName` | `PropertyDeclaration`, `ClassParameter`, `FunctionValueParameter`, `CatchBlock`, `ForStatement`, `ClassDeclaration`, `DelegationSpecifier`, `UnescapedAnnotation`, `SingleAnnotation` | Fully-qualified type name |
| `@ReturnTypeName` | `FunctionDeclaration` | Fully-qualified return type name |
| `@AnnotationFqNames` | `FunctionDeclaration`, `ClassDeclaration`, `PropertyDeclaration`, `ClassParameter` | Sequence of FQNs of all annotations on the declaration |
| `@Modifiers` | `ClassDeclaration`, `FunctionDeclaration`, `ClassParameter`, `CompanionObject`, `VariableDeclaration`, `ImportAlias` | Space-separated modifier keywords (e.g. `"override suspend"`). For arbitrary nodes use the `pmd-kotlin:modifiers()` function. |
| `@Identifier` | `ClassDeclaration`, `FunctionDeclaration`, `ClassParameter`, `CompanionObject`, `VariableDeclaration`, `ImportAlias` | Simple name of the declared identifier |

A type-info attribute is **absent** (not present with a null value) whenever its value is unavailable.

#### Absent value: unknown vs unresolved vs genuinely none

An absent attribute is ambiguous on its own — the type could be unresolved, or genuinely not present,
or the analysis may not have run. Note that a configured `auxClasspath` does not guarantee every type
resolves (incomplete classpath, generated/preprocessed code, missing annotation processors, etc.).
Two existing signals disambiguate the three cases:

* `@TypeInfoAvailable` on the `KotlinFile` root — whether kotlin-type-mapper analysis ran at all.
* `pmd-kotlin:hasUnresolvedReference()` — whether the node has an unresolved reference (the Kotlin
  analog of pmd-java's `isUnresolved()`).

| Root `@TypeInfoAvailable` | `hasUnresolvedReference()` | Attribute absent means |
|---------------------------|----------------------------|------------------------|
| absent | – | **unknown** — analysis did not run (no usable `auxClasspath`) |
| present | true | **unresolved** — analysis ran but the type could not be resolved |
| present | false | **genuinely none** — analysis ran and resolved; there is no such value |

```xml
<!-- property definitely without a resolvable declared type (not merely unresolved) -->
//PropertyDeclaration[not(@TypeName)
    and not(pmd-kotlin:hasUnresolvedReference())
    and ancestor::KotlinFile/@TypeInfoAvailable]

<!-- files where type resolution was unavailable -->
//KotlinFile[not(@TypeInfoAvailable)]
```

### XPath functions

The following XPath 2.0 functions are available in the `pmd-kotlin` namespace:

**`pmd-kotlin:typeIs(typeName)`** — Returns `true` if the context node's resolved type is `typeName`
or a subtype of it (uses the type hierarchy from kotlin-type-mapper).
Works on `PropertyDeclaration`, `FunctionDeclaration`, `ClassParameter`, `FunctionValueParameter`,
`CatchBlock`, `ForStatement`, and `DelegationSpecifier` nodes.
Both Kotlin names (`kotlin.String`) and Java names (`java.lang.String`) are accepted.

```xml
<rule ...>
  <properties>
    <property name="xpath"><![CDATA[
      //PropertyDeclaration[pmd-kotlin:typeIs('java.util.Calendar')]
    ]]></property>
  </properties>
</rule>
```

**`pmd-kotlin:typeIsExactly(typeName)`** — Like `typeIs`, but only matches the exact declared type,
not subtypes. Useful for detecting a specific class without matching subclasses.

**`pmd-kotlin:hasAnnotation(name)`** — Returns `true` if the context node is annotated with
an annotation whose simple name or fully-qualified name matches `name`.
Works on `FunctionDeclaration`, `ClassDeclaration`, `PropertyDeclaration`, and `ClassParameter`.

```xml
//ClassDeclaration[pmd-kotlin:hasAnnotation('Entity')]
//FunctionDeclaration[pmd-kotlin:hasAnnotation('kotlin.Deprecated')]
```

**`pmd-kotlin:modifiers()`** — Returns a sequence of modifier keyword strings for the context node.
Use the XPath `=` operator (which tests sequence membership) to check for a specific modifier.

```xml
//FunctionDeclaration[pmd-kotlin:modifiers() = 'suspend']
//ClassDeclaration[pmd-kotlin:modifiers() = ('internal', 'abstract')]
```

**`pmd-kotlin:isNullable()`** — Returns `true` if the context node's declared type is nullable
(i.e. has a `?` suffix in the Kotlin source). Works on `PropertyDeclaration`, `FunctionDeclaration`,
`ClassParameter`, and `FunctionValueParameter` nodes.

```xml
//PropertyDeclaration[pmd-kotlin:isNullable()]
//FunctionDeclaration[pmd-kotlin:isNullable()]
```

**`pmd-kotlin:hasUnresolvedReference()`** — Returns `true` if the context node contains an
unresolved reference (i.e. a symbol that kotlin-type-mapper could not resolve). Useful for
filtering out false positives in rules that depend on fully-resolved types.

```xml
//PostfixUnaryExpression[not(pmd-kotlin:hasUnresolvedReference())]
```

**`pmd-kotlin:matchesSig(signature)`** — Returns `true` if the context node represents a call site
whose method signature matches `signature`. Uses the same signature pattern syntax as the Java
`pmd-java:matchesSig()` function: `TypeName#methodName(paramType, ...)` with `_` as wildcard
for any single parameter and `..` as wildcard for any number of parameters.

```xml
//PostfixUnaryExpression[pmd-kotlin:matchesSig('java.util.Calendar#getInstance()')]
//PostfixUnaryExpression[pmd-kotlin:matchesSig('java.util.regex.Pattern#compile(_)')]
```

### Java-based rules

The same type information is accessible from Java-based rules via public interfaces and
`KotlinNodeTypeData`:

```java
import net.sourceforge.pmd.lang.kotlin.ast.HasTypeName;
import net.sourceforge.pmd.lang.kotlin.ast.HasModifiers;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtKotlinFile;
import net.sourceforge.pmd.lang.kotlin.types.KotlinNodeTypeData;

// Check type name on a node that implements HasTypeName
// (PropertyDeclaration, FunctionDeclaration, ClassParameter, etc.)
if (node instanceof HasTypeName) {
    String typeName = ((HasTypeName) node).getTypeName(); // null if unresolved
}

// Get modifiers on declaration nodes implementing HasModifiers
if (node instanceof HasModifiers) {
    String mods = ((HasModifiers) node).getModifiers(); // e.g. "override suspend"
}

// Static helpers on KotlinNodeTypeData (work on any KotlinNode)
String typeName = KotlinNodeTypeData.getTypeName(node);
String returnType = KotlinNodeTypeData.getReturnTypeName(node);
List<String> annotations = KotlinNodeTypeData.getAnnotationFqNames(node);

// Check if type analysis ran (on root node)
KtKotlinFile root = (KtKotlinFile) node.getRoot();
boolean available = KotlinNodeTypeData.isTypeInfoAvailable(root);
```

All type getters return `null` when type analysis has not run or the type could not be resolved.

> **Note:** Unlike pmd-java's `TypeNode.getTypeMirror()` which returns rich `JTypeMirror` objects
> (never null, using `UNKNOWN` for unresolved types), pmd-kotlin exposes string-based type names
> via `KotlinNodeTypeData` — reflecting the simpler output of kotlin-type-mapper.
> A richer type model may be added in a future version.

## Language Properties

See [Kotlin language properties](pmd_languages_configuration.html#kotlin-language-properties)
