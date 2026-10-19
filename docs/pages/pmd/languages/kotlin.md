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

> **Note for rule testing (rule test XML, PMD Designer):** these typically analyze one isolated
> Kotlin snippet/file at a time with no `auxClasspath` configured. In that setup, cross-package
> `import`-based type resolution to another **Kotlin source file** does not work yet -- only
> same-package cross-file resolution and same-file resolution do. If a test snippet imports a
> type from a different package, resolve it either by putting both types in the same package, or
> by configuring `auxClasspath` to include the type as a **compiled class** (a jar, or another
> module's `target/classes`), which resolves correctly regardless of package.

Type resolution for Kotlin is powered by [kotlin-type-mapper](https://github.com/stokpop/kotlin-type-mapper),
which is bundled with PMD.

## XPath rule support

Kotlin provides XPath-queryable attributes and helper functions for type-aware rules.

### Type-info attributes

The following attributes depend on type resolution: they are only available when `auxClasspath`
is configured and the kotlin-type-mapper analysis has resolved the types.

| Attribute | Nodes | Meaning |
|-----------|-------|---------|
| `@TypeName` | `PropertyDeclaration`, `ClassParameter`, `FunctionValueParameter`, `CatchBlock`, `ForStatement`, `ClassDeclaration`, `DelegationSpecifier`, `UnescapedAnnotation`, `SingleAnnotation` | Fully-qualified type name (including generic type arguments and nullable marker, e.g. `kotlin.collections.List<kotlin.String>?`) |
| `@ReturnTypeName` | `FunctionDeclaration` | Fully-qualified return type name (including generic type arguments and nullable marker) |
| `@AnnotationFqNames` | `FunctionDeclaration`, `ClassDeclaration`, `PropertyDeclaration`, `ClassParameter` | Sequence of FQNs of all annotations on the declaration |

A type-info attribute is **absent** (not present with a null value) whenever its value is unavailable.

> **Note:** `@TypeName` and `@ReturnTypeName` use fully-qualified names for generic type arguments:
> `kotlin.collections.List<kotlin.String>?`, not `List<String>?`. When comparing in XPath, use
> the full FQN form: `@TypeName = 'kotlin.collections.List<kotlin.String>'`. For type checks that
> ignore generics, prefer `pmd-kotlin:typeIs()` or `pmd-kotlin:typeIsExactly()`.

### General attributes

These attributes are structural: they come directly from the parsed source and don't depend on
type resolution, so they're always present regardless of `auxClasspath`.

| Attribute | Nodes | Meaning |
|-----------|-------|---------|
| `@Mutable` | `PropertyDeclaration` | `true` for `var`, `false` for `val`. |
| `@Identifier` | `ClassDeclaration`, `FunctionDeclaration`, `ClassParameter`, `CompanionObject`, `VariableDeclaration`, `ImportAlias` | Simple name of the declared identifier |
| `@Name` | `ImportHeader` | Fully-qualified imported name (e.g. `kotlin.collections.listOf`). |

> **Note:** `VariableDeclaration` carries only `@Identifier` (the variable name). Modifiers like
> `private`, `lateinit`, or `const` are on the parent `PropertyDeclaration` node.
> Use `//PropertyDeclaration[pmd-kotlin:modifiers() = 'private']` rather than querying
> `VariableDeclaration`.

> **Note:** Boolean attributes like `@Mutable` require XPath's `true()` / `false()` functions:
> `//PropertyDeclaration[@Mutable=false()]` (immutable `val` declarations).
> Using `false` without parentheses matches a node name, not a boolean value.

#### Absent value: unresolved vs genuinely none

An absent attribute is ambiguous on its own — the type could be unresolved, or genuinely not
present. Note that a configured `auxClasspath` does not guarantee every type resolves (incomplete
classpath, generated/preprocessed code, missing annotation processors, etc.).
`pmd-kotlin:hasUnresolvedReference()` — whether the node has an unresolved reference (the Kotlin
analog of pmd-java's `isUnresolved()`) — disambiguates the two cases:

| `hasUnresolvedReference()` | Attribute absent means |
|-----------------------------|------------------------|
| true | **unresolved** — analysis ran but the type could not be resolved |
| false | **genuinely none** — analysis ran and resolved; there is no such value |

```xpath
(: property definitely without a resolvable declared type (not merely unresolved) :)
//PropertyDeclaration[not(@TypeName) and not(pmd-kotlin:hasUnresolvedReference())]
```

> **Limitation:** `hasUnresolvedReference()` only checks the context node's *begin line* and
> only returns a boolean — it does not tell you *which* symbol failed to resolve, and it won't
> see an unresolved reference on a different line of a multi-line declaration. Exposing the
> unresolved reference name(s) is tracked as a possible follow-up function.

### XPath functions

There are a number of XPath functions available in the `pmd-kotlin` namespace, e.g.
`typeIs()`, `isNullable()`, `hasAnnotation()`, and more. For the full list with descriptions,
parameters, and examples, see the
[PMD extension functions]({{ baseurl }}pmd_userdocs_extending_writing_xpath_rules.html#pmd-extension-functions)
page.

### Java-based rules

The same type information is accessible from Java-based rules via public interfaces and
`KotlinNodeTypeData`:

```java
import net.sourceforge.pmd.lang.kotlin.ast.HasTypeName;
import net.sourceforge.pmd.lang.kotlin.ast.HasModifiers;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtKotlinFile;
import net.sourceforge.pmd.lang.kotlin.types.KotlinNodeTypeData;
import net.sourceforge.pmd.lang.kotlin.types.KotlinTypeName;

// Check type on a node that implements HasTypeName
// (PropertyDeclaration, FunctionDeclaration, ClassParameter, etc.)
if (node instanceof HasTypeName) {
    KotlinTypeName type = ((HasTypeName) node).getType(); // null if unresolved
    if (type != null) {
        String fqName = type.getFqName();       // e.g. "kotlin.collections.List"
        boolean nullable = type.isNullable();    // true if declared as nullable (?)
        String display = type.toDisplayString(); // e.g. "kotlin.collections.List<kotlin.String>?"
    }
}

// Get modifiers on declaration nodes implementing HasModifiers
// (Java-only API: not exposed as @Modifiers XPath attribute; use
// pmd-kotlin:modifiers() from XPath instead)
if (node instanceof HasModifiers) {
    List<String> mods = ((HasModifiers) node).getModifiers(); // e.g. ["override", "suspend"]
}

// Static helpers on KotlinNodeTypeData (work on any KotlinNode)
KotlinTypeName type = KotlinNodeTypeData.getType(node);
KotlinTypeName returnType = KotlinNodeTypeData.getReturnType(node);
List<String> annotations = KotlinNodeTypeData.getAnnotationFqNames(node);
```

All type getters return `null` when type analysis has not run or the type could not be resolved.
The `KotlinTypeName` class provides structured access to the FQ name, nullability, resolution
status, and full display string (including generic type arguments).

> **Note:** Unlike pmd-java's `TypeNode.getTypeMirror()` which returns rich `JTypeMirror` objects
> (never null, using `UNKNOWN` for unresolved types), pmd-kotlin exposes `KotlinTypeName` with
> string-based type names — reflecting the simpler output of kotlin-type-mapper.
> A richer type model may be added in a future version.

## Language Properties

See [Kotlin language properties](pmd_languages_configuration.html#kotlin-language-properties)
