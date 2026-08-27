# Kotlin Type Analysis — `pmd-kotlin` Custom XPath Functions

---

## Running PMD on a Kotlin Project

### CLI (standalone)

```bash
pmd check \
  --dir src/main/kotlin \
  --rulesets category/kotlin/bestpractices.xml,category/kotlin/errorprone.xml,category/kotlin/performance.xml,category/kotlin/multithreading.xml \
  --format text
```

The `typeIs`/`matchesSig` rules require type-resolution data from `kotlin-type-mapper`.
This runs **automatically** inside `KotlinLanguageProcessor` on each analysis — no extra
configuration is needed.

If your code uses external libraries (Spring, JPA, etc.) and you need subtype hierarchy
across those types, provide the compiled JARs:

For a **Maven project**, build the classpath automatically:

```bash
pmd check \
  --dir src/main/kotlin \
  --rulesets category/kotlin/bestpractices.xml \
  --aux-classpath "$(mvn -q dependency:build-classpath -DforceStdout)"
```

For a **Gradle project**, add a helper task to expose the compile classpath:

```kotlin
// build.gradle.kts
tasks.register("printClasspath") {
    doLast {
        println(configurations.compileClasspath.get().asPath)
    }
}
```

Then:

```bash
pmd check \
  --dir src/main/kotlin \
  --rulesets category/kotlin/bestpractices.xml \
  --aux-classpath "$(./gradlew -q printClasspath)"
```

---

### Maven Plugin

> **Compatibility note:** Each `maven-pmd-plugin` version bundles a fixed PMD version
> internally. `pmd-kotlin` must match that PMD version exactly. Check the plugin's release
> notes to find which PMD version it bundles, and use the matching `pmd-kotlin` version.
> For example, when a `maven-pmd-plugin` release bundles PMD 7.25.0, use
> `pmd-kotlin:7.25.0`.

The `maven-pmd-plugin` sets up the aux-classpath automatically from the project's
resolved dependencies. Add `pmd-kotlin` as a plugin dependency so PMD can discover
the Kotlin language module. Also point the plugin at your Kotlin sources via
`compileSourceRoots`:

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-pmd-plugin</artifactId>
  <version><!-- use the version that bundles your target PMD version --></version>
  <dependencies>
    <!-- Required: makes the Kotlin language module available to PMD.
         Version must match the PMD version bundled in the plugin above. -->
    <dependency>
      <groupId>net.sourceforge.pmd</groupId>
      <artifactId>pmd-kotlin</artifactId>
      <version>7.25.0</version>
    </dependency>
  </dependencies>
  <configuration>
    <rulesets>
      <ruleset>category/kotlin/bestpractices.xml</ruleset>
      <ruleset>category/kotlin/errorprone.xml</ruleset>
      <ruleset>category/kotlin/performance.xml</ruleset>
      <ruleset>category/kotlin/multithreading.xml</ruleset>
    </rulesets>
    <!-- Kotlin sources live under src/main/kotlin, not src/main/java -->
    <compileSourceRoots>
      <compileSourceRoot>${project.basedir}/src/main/kotlin</compileSourceRoot>
    </compileSourceRoots>
  </configuration>
</plugin>
```

Run with:

```bash
mvn pmd:check        # fails the build on violations
mvn pmd:pmd          # generates report only
```

---

### Gradle Plugin

Add `pmd-ant` and `pmd-kotlin` to the `pmd` dependency configuration. When you add
any dependency to the `pmd` configuration, Gradle skips its default `pmd-ant` resolution,
so both must be declared explicitly. `pmd-ant` provides the PMD entry point;
`pmd-kotlin` adds the Kotlin language module.

Use `source = fileTree(...)` (assignment, not the additive `source(...)` method) on each
PMD task to replace the default Java source with the Kotlin source directories.

```kotlin
// build.gradle.kts
plugins {
    id("pmd")
}

dependencies {
    // Required: pmd-ant is the entry point; pmd-kotlin adds Kotlin language support
    pmd("net.sourceforge.pmd:pmd-ant:7.25.0")
    pmd("net.sourceforge.pmd:pmd-kotlin:7.25.0")
}

pmd {
    toolVersion = "7.25.0"
    ruleSetFiles = files("config/pmd/kotlin-rules.xml")
    ruleSets = listOf()          // clear the default Java ruleset
    isConsoleOutput = true
}

tasks.named<Pmd>("pmdMain") {
    source = fileTree("src/main/kotlin")   // replace, not add
}
tasks.named<Pmd>("pmdTest") {
    source = fileTree("src/test/kotlin")
}
```

Where `config/pmd/kotlin-rules.xml` references the rule categories:

```xml
<?xml version="1.0"?>
<ruleset name="Kotlin Rules"
    xmlns="http://pmd.sourceforge.net/ruleset/2.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://pmd.sourceforge.net/ruleset/2.0.0 https://pmd.sourceforge.io/ruleset_2_0_0.xsd">
  <description>Kotlin rules for this project</description>

  <rule ref="category/kotlin/bestpractices.xml"/>
  <rule ref="category/kotlin/errorprone.xml"/>
  <rule ref="category/kotlin/performance.xml"/>
  <rule ref="category/kotlin/multithreading.xml"/>
</ruleset>
```

Run with:

```bash
./gradlew pmdMain    # checks src/main/kotlin
./gradlew pmdTest    # checks src/test/kotlin
```

The Gradle PMD plugin passes the compile classpath to PMD automatically, so
`typeIs`/`matchesSig` resolve external types without extra configuration.

---

## Functions

### `pmd-kotlin:typeIs(typeName)`

Returns `true` when the context node is a declaration whose resolved type matches `typeName`.
Type inference is supported — the type does not need to be written explicitly in source.

| Context node | Checked field |
|---|---|
| `PropertyDeclaration` | declared or inferred property type |
| `ClassParameter` | primary constructor `val`/`var` parameter type |
| `FunctionDeclaration` | return type |
| `FunctionValueParameter` | function parameter type |
| `CatchBlock` | caught exception type |
| `ForStatement` | loop variable type |

```xpath
//PropertyDeclaration[pmd-kotlin:typeIs('java.text.DecimalFormat')]
//FunctionDeclaration[pmd-kotlin:typeIs('java.util.Calendar')]
//ClassParameter[pmd-kotlin:typeIs('kotlin.String')]
//FunctionValueParameter[pmd-kotlin:typeIs('java.util.Calendar')]
//CatchBlock[pmd-kotlin:typeIs('java.io.IOException')]
//ForStatement[pmd-kotlin:typeIs('kotlin.String')]
```

### `pmd-kotlin:matchesSig(sig)`

Returns `true` when the context node is an expression that is a call site matching the given
signature pattern. Use on `PostfixUnaryExpression` nodes (method calls, property reads,
and constructor calls).

```xpath
//PostfixUnaryExpression[pmd-kotlin:matchesSig('java.util.regex.Pattern#compile(_)')]
// Constructor call (method name is always <init>):
//PostfixUnaryExpression[pmd-kotlin:matchesSig('nl.example.Foo#<init>(*)')]
```

---

## Type Name Formats

Both functions accept **Java FQCNs and Kotlin FQNs interchangeably** for well-known types.
The mapping is defined in `TypeNameUtils.kt` in `kotlin-type-mapper:model`.

Examples of equivalent pairs:

| Java FQCN | Kotlin FQN |
|---|---|
| `java.lang.String` | `kotlin.String` |
| `java.lang.Object` | `kotlin.Any` |
| `java.util.List` | `kotlin.collections.List` |
| `java.util.Map` | `kotlin.collections.Map` |
| `java.lang.Iterable` | `kotlin.collections.Iterable` |

Non-mapped types (e.g. `java.util.Calendar`, `java.util.regex.Pattern`) are matched exactly.

---

## Signature Format (`matchesSig`)

```
[receiverType "#"] methodName "(" [paramList] ")"
```

| Part | Value | Meaning |
|---|---|---|
| `receiverType` | FQN or `_` | Omit or use `_` to match any receiver |
| `methodName` | identifier or `_` | `_` matches any name |
| `paramList` | comma-separated types | Use `_` per-param wildcard, `*` for any count |

```
java.util.regex.Pattern#matches(java.lang.String,java.lang.CharSequence)
java.util.regex.Pattern#compile(_)           -- one param, any type
_#trim()                                     -- trim() on any receiver
kotlin.collections.List#size()               -- property access on List
_#_(*) 	                                     -- any call with any params
```

**Java↔Kotlin equivalence applies to receiver and parameter types.** So
`java.util.regex.Pattern#matches(java.lang.String,java.lang.CharSequence)` also matches a call
whose argument types are `kotlin.String` and `kotlin.CharSequence`.

**Static Java methods** (no dispatch receiver, e.g. `Pattern.matches(...)`) are matched by
the class prefix of the fully-qualified callee name.

---

## `@TypeName` Coverage — which nodes get a type and when

Not every node in the Kotlin AST has a `@TypeName` attribute. The table below shows
exactly which nodes are annotated and the right XPath function for each pattern.

### Nodes that receive `@TypeName`

> **Prefer `pmd-kotlin:typeIs()` over a raw `@TypeName='...'` comparison** for most rules.
> `typeIs()` handles subtype hierarchy and Kotlin/Java type equivalences
> (e.g. `kotlin.String` ↔ `java.lang.String`), while `@TypeName='...'` is a plain string
> match with no subtype widening.
> Use `@TypeName` directly only when you explicitly want an exact FQN match
> (e.g. on `ClassDeclaration` or `DelegationSpecifier` to identify a specific class or supertype).

| Node | `@TypeName` value | Preferred XPath |
|---|---|---|
| `PropertyDeclaration` | declared or **inferred** property type | `//PropertyDeclaration[pmd-kotlin:typeIs('nl.example.Foo')]` |
| `ClassParameter` | primary constructor `val`/`var` type | `//ClassParameter[pmd-kotlin:typeIs('kotlin.String')]` |
| `FunctionValueParameter` | function parameter type | `//FunctionValueParameter[pmd-kotlin:typeIs('java.util.Calendar')]` |
| `CatchBlock` | caught exception type | `//CatchBlock[pmd-kotlin:typeIs('java.io.IOException')]` |
| `ForStatement` | loop variable type | `//ForStatement[pmd-kotlin:typeIs('kotlin.String')]` |
| `ClassDeclaration` | the class's **own FQN** | `//ClassDeclaration[@TypeName='nl.example.Foo']` *(exact FQN — `@TypeName` preferred here)* |
| `DelegationSpecifier` | supertype FQN (e.g. `: Serializable`) | `//DelegationSpecifier[@TypeName='java.io.Serializable']` *(exact FQN — `@TypeName` preferred here)* |
| `UnescapedAnnotation` / `SingleAnnotation` | annotation FQN | `//SingleAnnotation[@TypeName='org.example.Ann']` *(exact FQN — `@TypeName` preferred here)* |

### Nodes that do NOT receive `@TypeName`

| Node | How to query instead |
|---|---|
| `PostfixUnaryExpression` (method/constructor call) | `pmd-kotlin:matchesSig(...)` |
| `FunctionDeclaration` | `@ReturnTypeName` / `pmd-kotlin:typeIs(...)` |
| `UserType` inside an expression (e.g. `Simple` in `Simple("Hello")`) | use `matchesSig` on the enclosing `PostfixUnaryExpression` |
| `SimpleIdentifier` / `T-Identifier` | `@Identifier` attribute (text only, no type) |

### Decision guide: `@TypeName` vs `typeIs` vs `matchesSig`

```
Q: Does the node represent a *declaration* (variable, parameter, field, catch, loop var)?
   → use pmd-kotlin:typeIs() — handles subtypes + Kotlin/Java equivalences
     e.g. //PropertyDeclaration[pmd-kotlin:typeIs('java.util.Calendar')]

Q: Do you need an exact FQN identity check (ClassDeclaration, DelegationSpecifier, annotation)?
   → use @TypeName='...' directly — no subtype widening wanted
     e.g. //ClassDeclaration[@TypeName='nl.example.Foo']

Q: Does the node represent a *call* (method call, constructor call, property read)?
   → use pmd-kotlin:matchesSig() on PostfixUnaryExpression
     e.g. pmd-kotlin:matchesSig('com.example.Foo#<init>(*)')
          method name is always <init> for constructor calls
```

### Example — inferred type matching a subtype

```kotlin
class Simple(val value: String) : Serializable   // type inferred from source

val myValue = Simple("Hello")   // @TypeName = "nl.example.Simple" (inferred by K1)
```

```xpath
// Matches val myValue (inferred type Simple, which implements Serializable):
//PropertyDeclaration[pmd-kotlin:typeIs('java.io.Serializable')]

// Matches the class declaration of Simple itself:
//ClassDeclaration[@TypeName='nl.example.Simple']

// Matches the Serializable supertype reference in the class header:
//DelegationSpecifier[@TypeName='java.io.Serializable']

// Matches the constructor call Simple("Hello"):
//PostfixUnaryExpression[pmd-kotlin:matchesSig('nl.example.Simple#<init>(kotlin.String)')]
```

> **Note:** kotlin-type-mapper analyzes Kotlin **source files** (`.kt`) directly using the
> K1 compiler pipeline. Your own project classes do **not** need to be pre-compiled — the
> source text is sufficient.
> However, the **dependency JARs** of your project (e.g. Spring, JPA, any library your
> classes extend or implement) must be available in `extraClasspath` so that K1 can resolve
> external type references and build the full transitive subtype hierarchy.

---

`KotlinLanguageProcessor` runs type analysis automatically in `launchAnalysis()` before any
rules are evaluated — no extra wiring is needed. The context holds a pre-computed index of all
resolved declarations and call sites, produced by
[kotlin-type-mapper](https://github.com/stokpop/kotlin-type-mapper).

### In tests

Use `KotlinTypeXPathTestHelper` to run the analyzer and inject the context:

```java
@BeforeEach
void setUp() {
    helper = KotlinTypeXPathTestHelper.forDirectory(new File(resourceDir));
    helper.injectContext();   // runs kotlin-type-mapper, sets global context
}

@AfterEach
void tearDown() {
    KotlinTypeAnalysisContextHolder.clearGlobal();
}
```

---

## Example Rules

### Avoid `DecimalFormat` as a field (`errorprone.xml`)

```xml
<rule name="AvoidDecimalFormatAsField"
      language="kotlin"
      message="DecimalFormat is not thread-safe; avoid storing it as a field."
      class="net.sourceforge.pmd.lang.rule.xpath.XPathRule">
  <description>...</description>
  <properties>
    <property name="xpath">
      <value><![CDATA[
        //PropertyDeclaration[
            not(ancestor::FunctionBody)
            and (pmd-kotlin:typeIs('java.text.DecimalFormat')
              or pmd-kotlin:typeIs('java.text.ChoiceFormat')
              or pmd-kotlin:typeIs('java.text.NumberFormat'))
        ]
      ]]></value>
    </property>
  </properties>
</rule>
```

### Avoid recompiling `Pattern` inside a function (`bestpractices.xml`)

```xml
<rule name="AvoidRecompilingPatterns"
      language="kotlin"
      message="Avoid compiling Pattern inside a function; use a companion object or top-level val."
      class="net.sourceforge.pmd.lang.rule.xpath.XPathRule">
  <description>...</description>
  <properties>
    <property name="xpath">
      <value><![CDATA[
        //FunctionBody//PostfixUnaryExpression[
            pmd-kotlin:matchesSig('java.util.regex.Pattern#compile(_)')
        ]
      ]]></value>
    </property>
  </properties>
</rule>
```

---

## Using Type Data from Java-Based Rules

All the type data that the XPath functions use is stored directly on each AST node and
accessible from Java-based rules via the `KotlinNode` interface. XPath rules are convenient
for simple structural checks, but Java rules are preferable for complex logic.

### Node attributes available directly

| XPath attribute / function | Java method on `KotlinNode` |
|---|---|
| `@TypeName` | `node.getTypeName()` — returns `null` when not set |
| `@ReturnTypeName` | `node.getReturnTypeName()` |
| `@Modifiers` | `node.getModifiers()` — space-separated keyword string |
| `@Identifier` | `node.getIdentifier()` — first `SimpleIdentifier` child text |
| `pmd-kotlin:typeIs(x)` | `ctx.isSubtypeOf(node.getTypeName(), x)` |
| `pmd-kotlin:typeIsExactly(x)` | `ctx.isTypeEquivalent(node.getTypeName(), x)` |
| `pmd-kotlin:hasAnnotation(x)` | `node.getAnnotationFqNames()` — list of FQNs |
| `pmd-kotlin:modifiers()` | `node.getModifiers()` — parse the space-separated string |

`KotlinTypeAnalysisContext` is obtained via `KotlinTypeAnalysisContextHolder.get()`.
It may return `null` in unusual scenarios (e.g. running without the language processor) — always null-check it.

### Why a ContextHolder? (comparison with Java PMD)

In **Java PMD**, type resolution is built into the core analysis pass: each `TypeNode` already
carries a `JTypeMirror` populated by PMD's own Java type resolver. `TypeTestUtil.isA(node)`
just reads that mirror — no external context is needed.

In **Kotlin PMD**, the ANTLR-based AST has no built-in type resolver. Type data comes from a
separate pre-analysis step run by `kotlin-type-mapper` (using the Kotlin K1 compiler). The
result is stored in a JSON file and loaded once at PMD startup into `KotlinTypeAnalysisContext`.

Because PMD's plugin architecture does not give XPath functions a channel for injected
dependencies, a global/thread-local singleton (`KotlinTypeAnalysisContextHolder`) is used to
share the context — the same pattern as Spring's `SecurityContextHolder`. Thread-local override
support makes test isolation clean.

A deeper integration (storing context in `LanguageVersionHandler` or `RuleContext`) would be
cleaner but would require changes to PMD core. The `ContextHolder` is a pragmatic solution
that works reliably for both XPath and Java rules.

### Example: Java rule checking parameter type

The following rule detects method parameters of type `java.util.Calendar` (and subtypes),
equivalent to `//FunctionValueParameter[pmd-kotlin:typeIs('java.util.Calendar')]`:

```java
package com.example.pmd.kotlin.rules;

import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFunctionValueParameter;
import net.sourceforge.pmd.lang.kotlin.rule.AbstractKotlinRule;
import net.sourceforge.pmd.lang.kotlin.rule.xpath.internal.KotlinTypeAnalysisContext;
import net.sourceforge.pmd.lang.kotlin.rule.xpath.internal.KotlinTypeAnalysisContextHolder;

public class AvoidCalendarParameterRule extends AbstractKotlinRule {

    @Override
    public Object visitFunctionValueParameter(KtFunctionValueParameter node, Object data) {
        String typeName = node.getTypeName();
        if (typeName != null) {
            KotlinTypeAnalysisContext ctx = KotlinTypeAnalysisContextHolder.get();
            // isSubtypeOf matches the type and all its supertypes (like pmd-kotlin:typeIs)
            // isTypeEquivalent matches only the exact type (like pmd-kotlin:typeIsExactly)
            if (ctx != null && ctx.isSubtypeOf(typeName, "java.util.Calendar")) {
                asCtx(data).addViolation(node);
            }
        }
        return visitChildren(node, data);
    }
}
```

### Example: Java rule checking annotation

```java
@Override
public Object visitClassDeclaration(KtClassDeclaration node, Object data) {
    boolean hasService = node.getAnnotationFqNames().stream()
        .anyMatch(fqn -> fqn.endsWith(".Service") || fqn.equals("Service"));
    if (hasService) {
        asCtx(data).addViolation(node);
    }
    return visitChildren(node, data);
}
```

### Example: Java rule checking modifiers

```java
@Override
public Object visitFunctionDeclaration(KtFunctionDeclaration node, Object data) {
    String mods = node.getModifiers();          // e.g. "override suspend" or null
    if (mods != null && mods.contains("suspend")) {
        asCtx(data).addViolation(node);
    }
    return visitChildren(node, data);
}
```


---

## Design Notes

For the detailed algorithm behind `matchesSig` multi-line matching (why `PostfixUnarySuffix`
begin-lines are used, and how `synchronized`/`try` blocks are excluded), see **DESIGN.md §2.3**.

---

## Known Limitations

- Requires pre-computed type analysis. Without it, both functions return `false` (safe default).
- Analysis is based on kotlin-type-mapper's K1 PSI; line numbers may differ by ±1 from PMD's
  ANTLR parser for some property declarations. A ±1 fallback is applied automatically.
- `typeIs` works on both class-level and local (function-body) `PropertyDeclaration` nodes,
  as well as `FunctionDeclaration` nodes. Kotlin uses the same grammar rule for both, and
  kotlin-type-mapper indexes all `KtProperty` nodes regardless of scope.
- Generic type arguments are supported in signatures (e.g. `kotlin.collections.List<kotlin.String>`),
  but omitting the type argument matches the raw (erased) type.
- **`java.lang.Object`-only methods (`finalize`, `notify`, `notifyAll`, `wait`) cannot be detected
  on a `kotlin.Any` receiver.** The `kotlin.Any ↔ java.lang.Object` name mapping is fully implemented
  in `TypeNameUtils.kt`, and `typeNamesEquivalent("java.lang.Object", "kotlin.Any")` returns `true`.
  However, Kotlin's type system intentionally hides these JVM-only methods from the `kotlin.Any`
  interface — they are not accessible in valid Kotlin code on a plain `Any` reference, so the Kotlin
  compiler (used by kotlin-type-mapper's `CallSiteExtractor`) never resolves such calls and no call
  site is recorded. **This is not a bug in kotlin-type-mapper; it is a fundamental Kotlin language
  constraint.** The practical implication is that rules like `AvoidCallingFinalize` can only detect
  `finalize()` calls when a class explicitly overrides the method (receiver type is that class, which
  IS a resolved subtype of `java.lang.Object`) — not on bare `Any` references.

---

## Future Rule Ideas

### `KotlinNonIdiomaticCollectionInit` (not yet implemented)

Kotlin has idiomatic factory functions for creating collections:

| Idiomatic (Kotlin)         | Non-idiomatic (Java-style)              |
|----------------------------|-----------------------------------------|
| `listOf()` / `mutableListOf()` | `ArrayList()` / `LinkedList()`      |
| `setOf()` / `mutableSetOf()`   | `HashSet()` / `TreeSet()`           |
| `mapOf()` / `mutableMapOf()`   | `HashMap()` / `TreeMap()`           |

A future rule could flag non-idiomatic initializer expressions regardless of the declared type:

```kotlin
val list: List<String> = ArrayList()        // flag: use listOf() or mutableListOf()
val list: MutableList<String> = ArrayList() // flag: use mutableListOf()
val list = ArrayList<String>()              // also covered by LooseCoupling (declared type = ArrayList)
```

This is distinct from `LooseCoupling` (which checks the *declared* type).
The XPath would check the initializer call site via `matchesSig`, e.g.:
```
//PropertyDeclaration[
    pmd-kotlin:matchesSig('java.util.ArrayList#<init>(*)')
    or pmd-kotlin:matchesSig('java.util.LinkedList#<init>(*)')
    or pmd-kotlin:matchesSig('java.util.HashSet#<init>(*)')
    or ...
]
```
