# pmd-kotlin -- Design and Implementation Notes

This document captures design decisions, AST structure insights, and implementation
patterns for the `pmd-kotlin` module. It is a living document; update it as new
learnings accumulate.

---

## 1. Kotlin ANTLR AST Structure

Rules are written as XPath expressions evaluated against the Kotlin ANTLR AST.
The grammar lives in:
```
src/main/antlr4/net/sourceforge/pmd/lang/kotlin/ast/Kotlin.g4
```

All grammar rule names become AST node names in PascalCase
(e.g. `postfixUnaryExpression` -> `PostfixUnaryExpression`).
Terminal tokens appear as `T-<TOKEN_NAME>` children (e.g. `T-THROW`, `T-RETURN`).

### 1.1 Method/property call chains

A chained expression like `a.b().c` is represented as a **left-recursive tree**
of `PostfixUnaryExpression` nodes, each adding one `PostfixUnarySuffix`:

```
PostfixUnaryExpression            <- a.b().c  (the whole thing)
  PostfixUnaryExpression          <- a.b()
    PostfixUnaryExpression        <- a.b
      PostfixUnaryExpression      <- a  (primary)
      PostfixUnarySuffix
        NavigationSuffix[@Identifier='b']
    PostfixUnarySuffix
      CallSuffix / ValueArguments  <- ()
  PostfixUnarySuffix
    NavigationSuffix[@Identifier='c']
```

**Key attributes:**
- `NavigationSuffix/@Identifier` -- the method/property name accessed via `.`
- `CallSuffix` -- present when the suffix is a call (has `ValueArguments`, possibly `AnnotatedLambda`)
- `ValueArguments/ValueArgument` -- positional arguments of a call

**XPath patterns for chains:**
- Detect `.methodName` navigation: `PostfixUnarySuffix/NavigationSuffix[@Identifier='methodName']`
- Detect a call to `.methodName()`:
  `PostfixUnarySuffix/NavigationSuffix[@Identifier='methodName']`
  AND a sibling `PostfixUnarySuffix/CallSuffix` (at the parent's level)
- Detect `.trim().length` at the outer node:
  `PostfixUnarySuffix/NavigationSuffix[@Identifier='length']`
  AND `.//PostfixUnarySuffix/NavigationSuffix[@Identifier='trim']` in the subtree

### 1.2 Method arguments

```
PostfixUnaryExpression (the call)
  PostfixUnarySuffix
    CallSuffix
      ValueArguments
        ValueArgument
          ... expression ...       <- the argument
```

For lambda-form calls (`log.debug { ... }`):
```
PostfixUnaryExpression
  PostfixUnarySuffix
    CallSuffix
      AnnotatedLambda
        LambdaLiteral
          ...
```
The lambda form does NOT have `ValueArguments`.

### 1.3 String concatenation

`a + b` as part of an expression:
```
AdditiveExpression
  ...  (left operand)
  AdditiveOperator
    T-ADD
  ...  (right operand)
```
Token is `T-ADD` (not `T-PLUS`). Grammar: `additiveOperator : ADD | SUB`.

### 1.4 Try / catch / finally

```
TryExpression
  Block                          <- try body
  CatchBlock                     <- one per catch clause
    SimpleIdentifier             <- exception variable name (e.g. "e")
    Type                         <- exception type
    Block                        <- catch body
  FinallyBlock                   <- optional
    Block
```

**Empty catch detection:**
`Block[not(Statements/Statement)]` -- `Block` always has `T-LCURL`, `T-RCURL` tokens as
children even when empty, so `Block[not(*)]` is WRONG. Use `Statements/Statement` instead.

`Statements` is always present as a grammar rule child of `Block`; `Statement` children
are only present when the block has actual statements.

**Throw/return in finally:**
```xpath
//FinallyBlock//JumpExpression[T-THROW]
except //FinallyBlock//LambdaLiteral//JumpExpression[T-THROW]
except //FinallyBlock//AnonymousFunction//JumpExpression[T-THROW]
```
Use `except` to exclude throws inside lambdas nested in the finally block
(those control their own exception flow). Both `LambdaLiteral` and `AnonymousFunction`
forms exist (`functionLiteral : lambdaLiteral | anonymousFunction`).

### 1.5 Jump expressions

`JumpExpression` wraps throw/return/continue/break:
```
JumpExpression
  T-THROW         <- for throw
  T-RETURN        <- for return
  T-CONTINUE      <- for continue
  T-BREAK         <- for break
```
Match with `JumpExpression[T-THROW]` etc.

### 1.6 When expressions

```
WhenExpression
  WhenSubject?                    <- the (expr) in  when (expr) { ... }
  WhenEntry*                      <- each branch incl. else
    WhenCondition / ELSE
    ControlStructureBody
```

`WhenEntry` includes the `else` branch. `count(WhenEntry)` counts all branches.

### 1.7 Loops

Grammar rule names -> PMD AST node names:
- `forStatement`    -> `ForStatement`
- `whileStatement`  -> `WhileStatement`
- `doWhileStatement` -> `DoWhileStatement`

Loop body is a `ControlStructureBody` child (which wraps a `Block` or single `Statement`).

Use the `ancestor::` axis to detect context inside a loop:
```xpath
ancestor::ForStatement or ancestor::WhileStatement or ancestor::DoWhileStatement
```

### 1.8 Assignments

```
Assignment
  directlyAssignableExpression / assignableExpression   <- left-hand side
  T-ASSIGNMENT  or  AssignmentAndOperator               <- = or +=, -=, etc.
    T-ADD_ASSIGNMENT   <- +=
    T-SUB_ASSIGNMENT   <- -=
    T-MULT_ASSIGNMENT  <- *=
    T-DIV_ASSIGNMENT   <- /=
    T-MOD_ASSIGNMENT   <- %=
  Expression                                            <- right-hand side
```

### 1.9 Identifier text access

Two patterns depending on node type:

| Node | Pattern |
|------|---------|
| `SimpleIdentifier` (standalone) | `SimpleIdentifier/T-Identifier/@Text` |
| Nodes with synthetic `@Identifier` attr | `NavigationSuffix/@Identifier` |

`NavigationSuffix`, `FunctionDeclaration` and similar nodes expose `@Identifier` as a
synthetic attribute (provided by `KotlinInnerNode.getIdentifier()`).

### 1.10 Empty block detection

`Block[not(Statements/Statement)]` -- detects a block with no statements.
`Block[not(*)]` is INCORRECT because `Block` always has `T-LCURL`/`T-RCURL` tokens.

### 1.11 Modifiers on declarations

Modifiers (`override`, `open`, `abstract`, `private`, `suspend`, `operator`, `inline`, ...) are
represented as a `Modifiers` child of the declaration node.  The path to a specific token is:

```
FunctionDeclaration
  Modifiers
    Modifier
      MemberModifier   (or VisibilityModifier / FunctionModifier / etc.)
        T-OVERRIDE / T-OPEN / T-ABSTRACT / T-SUSPEND / ...
```

Common XPath predicates:

| Goal | XPath |
|---|---|
| Exclude `override fun` | `not(Modifiers/Modifier/MemberModifier/T-OVERRIDE)` |
| Only `open` functions | `Modifiers/Modifier/InheritanceModifier/T-OPEN` |
| Only `abstract` declarations | `Modifiers/Modifier/InheritanceModifier/T-ABSTRACT` |
| Only `private` | `Modifiers/Modifier/VisibilityModifier/T-PRIVATE` |
| Only `suspend` | `Modifiers/Modifier/FunctionModifier/T-SUSPEND` |
| Only `operator` | `Modifiers/Modifier/FunctionModifier/T-OPERATOR` |
| Only `inline` | `Modifiers/Modifier/FunctionModifier/T-INLINE` |

The `@Modifiers` attribute on `FunctionDeclaration` (space-separated string, e.g. `'override'`) is
provided by `pmd-kotlin:modifiers()` -- see Section2.7.  Use the raw `Modifiers/.../T-*` tree path when you
need to check one specific modifier in an XPath rule; use `pmd-kotlin:modifiers()` when you need to
test membership of an arbitrary set.

The same `Modifiers` sub-tree applies to `ClassDeclaration`, `PropertyDeclaration`,
`ObjectDeclaration`, and `SecondaryConstructor`.

---

## 2. Custom XPath Functions

### 2.1 `pmd-kotlin:typeIs(fqcn)`

Returns `true` if the **type** of the context node is `fqcn` or a subtype.
See Section 9 for details on where and how type resolution works.
See also `README-type-analysis.md` for the user-facing function reference and examples.

Most reliable placements:
- `PropertyDeclaration` -- resolves the declared variable type
- `CatchBlock` -- resolves the caught exception type
- `PostfixUnaryExpression` -- resolves the return type of the call

### 2.2 `pmd-kotlin:typeIsExactly(fqcn)`

Like `typeIs` but requires exact type match (no subtype).

### 2.3 `pmd-kotlin:matchesSig(sig)`

Returns `true` when the `PostfixUnaryExpression` context node **contains a call site**
matching the signature. Signature format: `receiverType#methodName(paramType,...)`.

**Range matching behaviour:**
- Single-line node: matches call sites within the column span.
- Multi-line node: matches call sites on lines where a direct `PostfixUnarySuffix` child
  starts. This avoids matching deeply-nested call sites inside lambda/block arguments.
- Block-like nodes with no direct `PostfixUnarySuffix` children (e.g. bare `try`, `when`)
  never match.

**Why `PostfixUnarySuffix` begin-lines — detailed algorithm:**

`matchesSig` maps between type-mapper call sites (recorded as `(line, column)` pairs) and
PMD AST nodes (recorded as `(beginLine, beginCol, endLine, endCol)` rectangles).

The naive "collect all call sites in `[beginLine, endLine]`" over-collects for block expressions:

```kotlin
synchronized(lock) {        // PostfixUnaryExpression for the call: lines 1-4
    lock.notify()           // inner PUE: line 3 — should NOT fire on outer
}
try {                       // PostfixUnaryExpression for the try block: lines 1-5
    doWork()
} catch (e: Exception) {
    e.printStackTrace()     // inner PUE: line 4 — should NOT fire on outer
}
```

Fix: for multi-line nodes, **only accept call sites whose line matches the begin-line of a
direct `PostfixUnarySuffix` child**:

```
PostfixUnaryExpression (lines 1-4)         <- outer, synchronized() call
  simpleIdentifier "synchronized" (line 1)
  PostfixUnarySuffix (line 1)              <- call args, begin-line 1
    CallSuffix
      ValueArguments (line 1) [lock]
      AnnotatedLambda/LambdaBody (lines 1-4)
        PostfixUnaryExpression (line 3)    <- inner lock.notify() — NOT a direct child
```

The outer node's direct `PostfixUnarySuffix` begin-lines: `{1}`. The inner `lock.notify()`
call site is on line 3 — excluded. For a chained call like `xpath\n    .compile("//book")`,
the direct suffixes are on line 2 → the `.compile()` call site on line 2 is included.

`try {}` and `when {}` have **no direct `PostfixUnarySuffix` children** at all → they return
`false` immediately without signature matching.

**Chained-call use case (e.g. `UseStringBuilderLength`):**
```xpath
//PostfixUnaryExpression[
    pmd-kotlin:matchesSig('java.lang.StringBuilder#toString()')
    and PostfixUnarySuffix/NavigationSuffix[@Identifier='length']
]
```
The outer node (`sb.toString().length`) has `toString()` as a call site in its range
AND has `.length` as a direct `NavigationSuffix`.

**Avoiding doubled violations -- the structural anchor pattern:**

Because `matchesSig` matches any `PostfixUnaryExpression` whose column range contains
the target call site, AND because `a.method()` produces **two** nested PUE nodes -- one
for `a.method` (navigation only) and one for `a.method()` (navigation + call) -- a bare
`matchesSig` predicate will fire **twice** on the same method call.

To fire exactly once, combine `matchesSig` with a direct `@Identifier` structural anchor:

```xpath
//PostfixUnaryExpression[
    pmd-kotlin:matchesSig('java.lang.String#valueOf(*)')
    and PostfixUnarySuffix/NavigationSuffix[@Identifier='valueOf']
]
```

This anchors to the inner `a.valueOf` PUE (which has the NavigationSuffix as a direct
child) and excludes the outer `a.valueOf(i)` PUE (which has a CallSuffix as the direct
child, not a NavigationSuffix).

**Key rule: always pair `matchesSig` with a `NavigationSuffix[@Identifier='method']`
structural anchor.** This `@Identifier` check is NOT a type-fallback -- it is a necessary
locator that selects the correct PUE level. Never remove it when refactoring rules.

### 2.4 `pmd-kotlin:matchesSig` -- wildcards

- `_` -- wildcard for a single type (receiver or parameter)
- `*` -- wildcard for the entire parameter list
- `<init>` -- matches constructors

### 2.5 `pmd-kotlin:nodeText()`

Returns the source text of the node as a string.
Useful for simple text-based matching without type analysis.
Example: `pmd-kotlin:nodeText() = '0'` in `UseCollectionIsEmpty`.

### 2.6 `pmd-kotlin:hasAnnotation(fqcn)`

Returns `true` when the context node has a given annotation.

### 2.7 `pmd-kotlin:modifiers()`

Returns the set of modifier keywords on the context node as a space-separated string
(e.g. `'override'`, `'open abstract'`, `'private suspend'`).

Use it when you need set-membership semantics on the declaration node itself:

```xpath
(: fires only on non-override functions :)
//FunctionDeclaration[not(pmd-kotlin:modifiers() = 'override')]

(: fires only on suspend functions :)
//FunctionDeclaration[pmd-kotlin:modifiers() = 'suspend']
```

For a single well-known modifier, the direct tree path (Section1.11) is equally readable and avoids a
function call:

```xpath
(: preferred for a single modifier check :)
//FunctionDeclaration[not(Modifiers/Modifier/MemberModifier/T-OVERRIDE)]
```

### 2.8 `pmd-kotlin:insideLoop()`

Returns `true` when the context node is nested (at any depth) inside a
`ForStatement`, `WhileStatement`, or `DoWhileStatement`.

Replaces the verbose three-way ancestor check in every loop rule:
```xpath
// Before
ancestor::ForStatement or ancestor::WhileStatement or ancestor::DoWhileStatement

// After
pmd-kotlin:insideLoop()
```

### 2.9 `pmd-kotlin:isNullable()`

Returns `true` when the context node's resolved type is **nullable** (i.e. its
type name ends with `?`).

Supported on the same nodes as `typeIs`:
- `PropertyDeclaration` -- checks the property / local variable type
- `FunctionDeclaration` -- checks the return type
- `FunctionValueParameter` / `ClassParameter` -- checks the parameter type
- `CatchBlock` -- checks the caught exception type
- `ForStatement` -- checks the loop variable type

The check is based on the resolved type string stored by kotlin-type-mapper, which
appends `?` for nullable Kotlin types (e.g. `"java.util.List<java.lang.String>?"`
for `List<String>?`). This means it works even when the nullability is not written
explicitly in the source (e.g. inferred nullable locals).

Because `return null` in a non-nullable context is a compile error in Kotlin, rules
that match `return null` do **not** need an extra `isNullable()` guard -- null returns
can only exist when the return type is already nullable. Use `isNullable()` when you
need to explicitly distinguish nullable from non-nullable declarations.

```xpath
(: nullable return type :)
//FunctionDeclaration[pmd-kotlin:isNullable() and pmd-kotlin:typeIs('java.util.Collection')]

(: nullable property :)
//PropertyDeclaration[pmd-kotlin:isNullable() and pmd-kotlin:typeIs('kotlin.String')]

(: nullable parameter :)
//FunctionValueParameter[pmd-kotlin:isNullable()]
```

**Note on `matchesSig` and nullability:** `matchesSig` operates at JVM signature level
where `String?` and `String` are indistinguishable. Kotlin does not allow overloading
by nullability. Therefore `matchesSig` has no nullable awareness -- combine it with
`isNullable()` when needed.

---

## 3. Rule Authoring Patterns

### 3.1 File / node layout for a new rule

1. **Rule entry** in the appropriate category XML:
   `src/main/resources/category/kotlin/{bestpractices,errorprone,performance,multithreading}.xml`

2. **Test data XML** (one per rule):
   `src/test/resources/net/sourceforge/pmd/lang/kotlin/rule/<category>/xml/<RuleName>.xml`

3. **Test class** (one per rule):
   `src/test/java/net/sourceforge/pmd/lang/kotlin/rule/<category>/<RuleName>Test.java`
   Extends `PmdRuleTst` -- the framework auto-discovers the test XML by convention.

4. **Test method naming**: Checkstyle enforces `^[a-z][a-zA-Z0-9]*$`.
   Use camelCase even in test methods; underscores are not allowed.

### 3.2 Java-equivalent rule property

Rules ported from Java should include:
```xml
<property name="javaEquivalentRule" type="String"
          description="Java rule this Kotlin rule is based on; update this rule when the Java rule changes."
          value="category/java/performance.xml/SomeName"/>
```

### 3.3 Kotlin idiomatic alternatives

Always document the Kotlin-idiomatic fix in the rule description.  
Examples:
- `str.trim().length == 0` -> `str.isBlank()`
- `sb.append("x" + y)` -> `sb.append("x").append(y)` or `buildString { append("x"); append(y) }`
- `when` with 1-2 branches -> `if`/`if-else`

### 3.4 `except` clause in XPath

PMD supports the `except` set-difference operator:
```xpath
//FinallyBlock//JumpExpression[T-RETURN]
except //FinallyBlock//AnonymousFunction//JumpExpression[T-RETURN]
```
Use it to exclude matched nodes that fall inside an inner scope
(lambda, anonymous function, nested try, etc.).

---

## 4. Import Ordering (Checkstyle)

PMD enforces strict lexicographic import grouping.
Correct order for `pmd-kotlin` source files:

```java
import java.*
import javax.*

import org.*

import net.sourceforge.pmd.*

import nl.stokpop.*   // comes AFTER net.sourceforge.pmd, not before
```

A blank line between non-adjacent groups is required, but extra blank lines
**within** a group are Checkstyle errors.

---

## 5. Aux Classpath Filtering

Kotlin's `FastJarHandler` / `LargeDynamicMappedBuffer` throws
`IllegalArgumentException` when given a `.pom` file (empty / non-ZIP).
Maven's Surefire and `URLClassLoader` hierarchies can include `.pom` entries.

**Fix location:** `KotlinAuxClasspathResolver.filterEntries()` -- retains
only entries that are existing directories or `.jar` files; logs `WARN` for anything
skipped. Applied at all three classpath-source points (string property, URLClassLoader,
`java.class.path`).

---

## 6. CPD / Duplication Avoidance

CPD flags structurally similar methods. Use the **template-method pattern** to
factor out shared call flow into an abstract base class:

- `AbstractKotlinTypeIsFunctionCall` -- shared `call()` for `typeIs`/`typeIsExactly`
- Subclasses implement `matchesType()` hook (`isSubtypeOf` vs `isTypeEquivalent`)

Avoid dead private static methods that share a name with instance methods --
this was a source of false CPD positives.

---

## 7. `KotlinInnerNode.getIdentifier()`

Returns the text of the first `KtSimpleIdentifier` direct child.
Exposed as `@Identifier` attribute in XPath on any node that has a single leading
`simpleIdentifier` child (e.g. `NavigationSuffix`, `FunctionDeclaration`,
`PrimaryExpression`, `VariableDeclaration`).

**Critical gotcha:** `@Identifier` works on the **parent** of a `SimpleIdentifier`,
NOT on `SimpleIdentifier` itself. `SimpleIdentifier[@Identifier='foo']` always
returns null because `SimpleIdentifier`'s own direct children are `T-Identifier`
terminal tokens, not another `SimpleIdentifier`.

| [OK] Correct | [X] Wrong |
|-----------|---------|
| `PrimaryExpression[@Identifier='foo']` | `SimpleIdentifier[@Identifier='foo']` |
| `VariableDeclaration/@Identifier` | `SimpleIdentifier/@Identifier` |
| `NavigationSuffix[@Identifier='bar']` | `NavigationSuffix/SimpleIdentifier/@Identifier` |

For standalone `simpleIdentifier` children, use `SimpleIdentifier/T-Identifier/@Text`
instead.

### 7.1 `getImage()` and XPath rule violation messages (`{0}`)

PMD's `XPathRule.apply()` always tries `node.getImage()` first and uses the result
as the `{0}` argument in the rule's `message` attribute (MessageFormat syntax).
Fallback: it probes attributes `Name`, `SimpleName`, `MethodName`, `Value` in order.
This mechanism was already in place in PMD core — it just needs the node to return a
meaningful string from `getImage()`.

`KotlinInnerNode.getImage()` is `null` by default, so any XPath rule pointing at a
Kotlin AST node would render `{0}` as the literal string `"null"` in its message.

To include dynamic text in a rule message, override `getImage()` in `KotlinInnerNode`
for the relevant `RULE_*` index and return the desired string.  The `ImportHeader`
node uses this to expose the fully-qualified import name:

```java
@Override
public @Nullable String getImage() {
    if (getRuleIndex() == KotlinParser.RULE_importHeader) {
        return buildImportFqn(); // walks KtIdentifier children, joins with '.'
    }
    return null;
}
```

The rule message then uses `{0}` (MessageFormat — single quotes must be doubled):

```xml
message="Type ''{0}'' could not be resolved -- add the missing dependency..."
```

---

## 8. Type Analysis in Unit Tests

The Kotlin language processor's `launchAnalysis()` invokes the kotlin-type-mapper
**before** rules are evaluated, even in `PmdRuleTst`-based unit tests. The test
framework runs the full analysis pipeline on the CDATA snippet.

Basic JVM types (`java.lang.String`, `java.lang.StringBuilder`, `java.util.*`, etc.)
resolve without an explicit aux classpath because the Kotlin compiler includes the
standard JDK classes automatically.

**Consequence:** You can use `pmd-kotlin:typeIs(...)` and `pmd-kotlin:matchesSig(...)`
in test cases without any extra configuration -- they just work.  
If type analysis were unavailable, the `UnresolvedType` rule would fire as a signal.

### 8.1 Requirements for typed test cases

For type resolution to work in a test case CDATA snippet:

1. **Add imports.** The snippet must `import` every type it references, just like real
   Kotlin code. Without imports, the Kotlin compiler cannot resolve symbols and
   `matchesSig` / `typeIs` will return false.

   ```kotlin
   import java.util.LinkedList

   class Example {
       fun bad() {
           val list = LinkedList<String>()   // type resolves to java.util.LinkedList
       }
   }
   ```

2. **Define referenced classes** if they are not imported from a jar. For test-only
   types (e.g. a custom interface that the rule targets), declare a stub inside the
   same CDATA snippet or in a companion `<code-fragment>`.

3. **Add a third-party jar as a Maven test dependency** when the rule targets a library
   type not available on the Kotlin compiler's default classpath (e.g. `kotlinx.coroutines`,
   Spring, OkHttp, etc.).

   `KotlinAuxClasspathResolver.resolve()` has three fallback sources it checks
   in order:
   1. The `--aux-classpath` CLI property (used in production)
   2. A `URLClassLoader` hierarchy (used by PMD Designer)
   3. **`java.class.path` system property** -- Maven Surefire puts all test-scoped
      dependencies here automatically

   This means: adding the library as `<scope>test</scope>` in `pom.xml` is all that is
   needed.  The language processor picks it up from `java.class.path` and feeds it to
   `KotlinTypeMapper`.  No code changes to tests are required.

   ```xml
   <!-- pmd-kotlin/pom.xml -->
   <dependency>
       <groupId>org.jetbrains.kotlinx</groupId>
       <artifactId>kotlinx-coroutines-core-jvm</artifactId>
       <version>1.8.0</version>
       <scope>test</scope>
   </dependency>
   ```

   After adding the dependency, any XML test case `<code>` snippet that imports the
   library's types will resolve correctly and `typeIs` / `typeIsExactly` / `matchesSig`
   will work as expected -- no extra Java test code is needed.

   **Type alias expansion:** `KotlinTypeMapper` always expands `typealias` chains down
   to the final concrete JVM type.  This means the FQN you provide to `typeIsExactly()`
   must be the fully-expanded JVM type, NOT the alias name.  For example:

   | Import in Kotlin code | FQN stored by type-mapper |
   |---|---|
   | `kotlinx.coroutines.CancellationException` | `java.util.concurrent.CancellationException` |

   (Chain: `kotlinx.coroutines.CancellationException` → `kotlin.coroutines.cancellation.CancellationException` → `java.util.concurrent.CancellationException`)

   Use the diagnostic pattern below to discover the actual FQN when in doubt:
   ```java
   TypedAst ast = new KotlinTypeMapper(tmpDir, classpathJars, false).analyze();
   ast.getFiles().forEach(f -> f.getDeclarations().forEach(d ->
       System.out.println(d.getKind() + " " + d.getName() + " type=" + d.getType())));
   ```

4. **Always prefer typed checks** (`matchesSig`, `typeIs`) over AST-shape checks.
   Type info is always expected to be present in production (the `UnresolvedType` rule
   must be resolved first); no AST-only fallback is needed.

5. **Java static methods on Kotlin-mapped types require fully-qualified names.**
   `kotlin.String` has no companion `valueOf`, so `String.valueOf(i)` does not produce
   a call site in kotlin-type-mapper and `matchesSig` will return false.  
   Use the fully-qualified `java.lang.String.valueOf(i)` instead -- the K1 compiler
   resolves this as a Java interop static call and a call site IS generated.

   ```kotlin
   // Wrong -- String maps to kotlin.String; no valueOf companion -> no call site
   return String.valueOf(i)

   // Correct -- java.lang.String resolves via Java interop -> call site generated
   return java.lang.String.valueOf(i)
   ```

   This also applies to other methods whose Kotlin-mapped name differs from the Java name.


---

## 9. `pmd-kotlin:typeIs()` -- Where It Works and How

`typeIs(fqcn)` and `typeIsExactly(fqcn)` on a node resolve the type in this priority order:

1. **Node attribute (`TYPE_NAME_KEY`)** -- if `KotlinTypeAnnotationVisitor` has set a type
   annotation on the node (e.g. `PropertyDeclaration`, `CatchBlock`, `DelegationSpecifier`,
   `FunctionValueParameter`), that annotation is checked. If it does not match, the function
   returns `false` immediately; it does NOT fall through to the call-site index. This prevents
   the RHS initializer of a property from being checked when only the declared type matters
   (e.g. `val items: List<String> = ArrayList()` must not match `typeIsExactly('ArrayList')`).

2. **Declaration index at `node.beginLine`** -- if the node has no type annotation, the
   declaration index for that line is consulted. A declaration's `type` and `returnType` fields
   are checked.

3. **Call-site index at `node.beginLine`** -- checked REGARDLESS of whether step 2 found a
   declaration. The `declarationsAt()` API uses +/-1 line tolerance, so a function header on
   the preceding line can be returned as a "declaration" for an expression on the next line.
   Stopping at step 2 on a non-matching declaration would prevent expression nodes (e.g. a
   `PostfixUnaryExpression` constructor call inside a `throw`) from being resolved correctly.

**Reliable placement:**
- On `PropertyDeclaration` -> resolves the declared variable type. Most reliable:
  `//PropertyDeclaration[pmd-kotlin:typeIs('java.lang.String')]`
- On `CatchBlock` -> resolves the caught exception type
- On `DelegationSpecifier` -> resolves the supertype
- On `PostfixUnaryExpression` (constructor call) -> resolves via call-site return type.
  Use `typeIsExactly` here because `matchesSig` is polymorphic (subtype-aware), so if exact
  type checking is needed (e.g. `throw Exception(...)` must not match `throw MyException(...)`),
  `typeIsExactly` is the correct choice.

**When not to use `typeIs` -- use `@Identifier` name matching instead:**
- Checking the name of a rethrown variable (`ancestor::CatchBlock/@Identifier`) -- this is
  variable identity, not type checking.
- Checking the SUPERTYPE of a class declaration (e.g. `DoNotExtendJavaLangError`):
  `typeIs` on `DelegationSpecifier` looks up the `@TypeName` attribute (the supertype FQN),
  but subtype checking then requires the user-defined subclass to be loadable via reflection
  so BFS can traverse from it up to the target type. User-defined classes are typically NOT
  on the aux classpath, so BFS fails silently. In this case an explicit name list
  (`T-Identifier[@Text = 'Error' or @Text = 'AssertionError' or ...]`) is more reliable.

**Connecting a variable declaration to its usage with `let`:**

When a rule needs to link an `Assignment` LHS back to the type of the declared
variable, use a `let` expression (supported by PMD's Saxon XPath engine):

```xpath
//Assignment[
    AssignmentAndOperator/T-ADD_ASSIGNMENT
    and (ancestor::ForStatement or ancestor::WhileStatement or ancestor::DoWhileStatement)
    and (
        let $lhsName := AssignableExpression//PrimaryExpression/@Identifier
        return ancestor::FunctionBody//PropertyDeclaration[
            pmd-kotlin:typeIs('java.lang.String')
            and VariableDeclaration/@Identifier = $lhsName
        ]
    )
]
```

`let $var := expr return ...` is valid XPath 2.0 inside predicates.
Existing Java PMD rules use this pattern (e.g. `LabeledStatement` in bestpractices).

---

## 10. Constructor Detection with `matchesSig`

To detect **any** constructor call on a `PostfixUnaryExpression`:
```xpath
pmd-kotlin:matchesSig('_#<init>(*)')
```
- `_` -- wildcard matching any receiver type
- `<init>` -- matches constructor calls
- `*` -- wildcard matching any parameter list

This is preferred over the heuristic `PrimaryExpression[matches(@Identifier, '^[A-Z]')]`
(capital letter naming convention) because:
- The heuristic falsely fires on top-level functions like `Regex(...)` that are
  actually factory functions and NOT constructors in some contexts
- `matchesSig` uses actual resolved type information

Note: `matchesSig` only works on `PostfixUnaryExpression` nodes; it returns `false`
on all other node types.

---

## 10a. `AvoidInstantiatingObjectsInLoops` -- Loop-Variable Suppression

The rule suppresses violations when the constructor's arguments directly reference
the for-loop's iteration variable(s), i.e. the object is different each iteration.

### Loop variable collection

```xpath
let $loopVars := (
    ancestor::ForStatement/VariableDeclaration
    | ancestor::ForStatement/MultiVariableDeclaration/VariableDeclaration
)/@Identifier
```

`ancestor::ForStatement` returns ALL enclosing for-loops, so nested loops
contribute all their loop vars (e.g. `(dir, achievementName)`).

### Checking for loop-var use -- use `.//ValueArguments`, NOT `PostfixUnarySuffix/CallSuffix/ValueArguments`

**Key finding:** for a regular constructor call `File(dir, "x")`, the args live under
`PostfixUnarySuffix/CallSuffix/ValueArguments`. However, for an **anonymous class
delegation** -- `object : FileObserver(dir, CLOSE_WRITE)` -- the args live under
`ObjectLiteral/DelegationSpecifiers/.../ConstructorInvocation/ValueArguments`, which
is **not** under a `PostfixUnarySuffix`. Using a narrow path misses this case.

The fix is to use `.//ValueArguments` (any descendant) instead:

```xpath
not(.//ValueArguments//PrimaryExpression[@Identifier = $loopVars])
```

This covers both patterns:
- `File(dir, "x")` -> `PostfixUnarySuffix/CallSuffix/ValueArguments`
- `object : SomeClass(dir)` -> `DelegationSpecifiers/.../ConstructorInvocation/ValueArguments`

Also keep the constructor search scoped to the initializer expression, not nested method
bodies inside anonymous objects. For `val watcher = object : FileWatcher(dir) { ... }`,
constructors or string literals inside `override fun onEvent(...)` are descendants of the
property initializer in the raw AST, but should not make the `watcher` declaration a loop
allocation violation. Exclude those with:

```xpath
not(ancestor::FunctionDeclaration[ancestor::PropertyDeclaration])
```

---

## 11. `AvoidArrayLoops` Rule Notes

The rule uses structural detection (no type analysis needed):
```xpath
//ForStatement[
    not(.//ForStatement) and not(.//WhileStatement) and not(.//DoWhileStatement)
    and count(.//Assignment) = 1
]//Assignment[
    DirectlyAssignableExpression/AssignableSuffix/IndexingSuffix
    and Expression//PostfixUnarySuffix/IndexingSuffix
]
```

**Known behaviour:**
- `not(.//ForStatement)` correctly ignores outer loops when nested -- only the
  innermost simple loop is checked
- Transform patterns (`dst[i] = src[i] * 2`) ARE flagged: the RHS still contains
  `PostfixUnarySuffix/IndexingSuffix` for `src[i]`
- 2D array copy inner loops ARE flagged: `dst[i][j] = matrix[i][j]` matches because
  `DirectlyAssignableExpression/AssignableSuffix/IndexingSuffix` matches the second
  index `[j]`

Both of the above are accepted rule behaviour -- the developer should use `copyInto`
or functional alternatives; suppress with `// NOPMD` where intentional.



---

## 13. Design Rules -- Boolean and If Patterns

### 13.1 `SimplifyBooleanExpressions`

Flags redundant boolean identity expressions using `&&` or `||` with a literal:
`x && true`, `true && x`, `x || false`, `false || x`.

**Note:** `x == true`, `x != true`, `x == false`, `x != false` are intentionally **not** flagged.
In Kotlin these are idiomatic for nullable `Boolean?` values (safe null-aware checks) and
produce too many false positives to be useful.

**Identity boolean conjunctions/disjunctions** (Kotlin-specific):

```
Conjunction          <- x && true / true && x
  Equality           <- one operand
  T-CONJ
  Equality           <- other operand; nodeText = "true"

Disjunction          <- x || false / false || x
  Conjunction        <- one operand
  T-DISJ
  Conjunction        <- other operand; nodeText = "false"
```

`T-CONJ`/`T-DISJ` presence distinguishes real two-operand nodes from single-operand wrapper nodes
(e.g. a lone `Conjunction` wrapping one `Equality`).

```xpath
(: x && true, true && x :)
//Conjunction[T-CONJ and Equality[pmd-kotlin:nodeText() = 'true']]
|
(: x || false, false || x :)
//Disjunction[T-DISJ and Conjunction[pmd-kotlin:nodeText() = 'false']]
```

`x && false` and `x || true` are intentionally excluded -- they change semantics (absorbing element).

### 13.2 `SimplifyBooleanReturns`

Matches `if/else` or `when` expressions where both branches yield a bare boolean literal.

**Return form** (`return true`/`return false`):

```
IfExpression
  ControlStructureBody [1st -- then]
    Block or Statement containing JumpExpression[T-RETURN]
  T-ELSE
  ControlStructureBody [2nd -- else]
    Block or Statement containing JumpExpression[T-RETURN]
```

Both brace and braceless forms are covered. Block form is constrained to
`Block[count(Statements/Statement) = 1]` to exclude multi-statement branches.
`pmd-kotlin:nodeText()` on the `Expression` child of `JumpExpression` detects bare `true`/`false`,
avoiding false positives for `return computedBoolean(true)`.

**Expression/assignment form** (Kotlin-specific): `fun f() = if (cond) true else false`
and `val x = if (cond) true else false`. Same `IfExpression` shape but branches contain a plain
`Expression` (no `JumpExpression`) with `nodeText()` of `"true"` or `"false"`.

**`when` form** (Kotlin-specific): `when { cond -> true; else -> false }`:

```
WhenExpression
  WhenEntry[1]  <- condition branch (no T-ELSE): ControlStructureBody/Statement/Expression = "true"/"false"
  WhenEntry[2]  <- else branch (T-ELSE):          ControlStructureBody/Statement/Expression = "true"/"false"
```

Constrained to exactly `count(WhenEntry) = 2` (one condition + one else). Multi-branch `when`
and `when` with non-boolean branches are not flagged.

### 13.3 `CollapsibleIfStatements`

Matches a top-level `if` (no else) whose single body statement is another `if` (no else):

```
IfExpression[not(T-ELSE)]
  ControlStructureBody
    Block (optional, single statement)
    Statement
      Expression
        ...
        PrimaryExpression
          IfExpression[not(T-ELSE)]   <- violation reported here (inner if)
```

The path `Statement/Expression//PrimaryExpression/IfExpression` selects expression-statement ifs,
distinguishing them from declaration contexts (`Statement/Declaration/PropertyDeclaration/...`).
`not(ancestor::CallSuffix)` prevents false positives for `if (a) someCall(if (b) foo())`.

Both braces and no-braces forms are handled via a union XPath. The violation is reported
at the **inner** `IfExpression` node.

---

## 12. Future Improvement Backlog

Items deferred until more rules need them:

### 12.1 Extend `typeIs()` to resolve Assignment LHS

Currently `typeIs()` works on `PropertyDeclaration` and `PostfixUnaryExpression` nodes.
When checking the type of an assignment target (e.g. `result += item`) the workaround
is a `let $lhsName` expression that walks back to the declaration:

```xpath
let $lhsName := AssignableExpression//PrimaryExpression/@Identifier
return ancestor::FunctionBody//PropertyDeclaration[
    pmd-kotlin:typeIs('java.lang.String')
    and VariableDeclaration/@Identifier = $lhsName
]
```

Desired (not yet implemented):
```xpath
AssignableExpression[pmd-kotlin:typeIs('java.lang.String')]
```

Implementation: `typeIs()` would walk up from `AssignableExpression` to find the
corresponding `PropertyDeclaration` and resolve its type.
Java XPath rules do not have this either -- Java class-based rules use the type API directly.

**Defer until** a second rule needs the same `let $lhsName` workaround.

### 12.2 Implement `KotlinAnnotationSuppressor` and `KotlinSuppressionComment`

PMD currently has **no suppression support for Kotlin** at all. Both mechanisms that work in Java are absent:

- **`@Suppress("PMD.RuleName")`** — silently ignored. Kotlin's `@Suppress` is not processed
  because there is no `KotlinAnnotationSuppressor` (contrast with `JavaAnnotationSuppressor`).
- **`// NOPMD`** — silently ignored. The NOPMD comment mechanism is implemented in
  `AbstractTokenManager` for JavaCC-based languages. Kotlin uses ANTLR; `PmdKotlinParser`
  never calls `withSuppressionComments(...)` on the `AstInfo`, so no comment suppression is
  wired up.

**Verified:** Running PMD on a Kotlin file with `// NOPMD` on a violation line or `@Suppress`
on the enclosing declaration still produces the violation. The only working suppression
mechanisms are ruleset-level:

- `violationSuppressRegex` — regex matched against the violation message
- `violationSuppressXPath` — XPath expression matched against the violation node

**To implement full suppression support:**

1. **Annotation suppressor:** Extend `AbstractAnnotationSuppressor<KotlinNode>`, walk the
   `@Suppress(...)` arguments, match against `"PMD"` / `"PMD.RuleName"`, register via
   `KotlinLanguageProcessor`.

2. **Comment suppressor:** In `PmdKotlinParser`, after building the ANTLR token stream,
   scan for `NOPMD` in comment tokens and populate `AstInfo.withSuppressionComments(...)`.

Until both are implemented, direct users to use `violationSuppressRegex` or
`violationSuppressXPath` in a ruleset override.

## 14. Java-Based Design Rules

Some rules are too stateful or too complex to express cleanly in XPath; they are implemented
as Java classes extending `AbstractKotlinRule`. This section documents the shared infrastructure
and design conventions for those rules.

### 14.1 `KotlinAstUtil` -- Shared AST Navigation Helpers

`net.sourceforge.pmd.lang.kotlin.util.KotlinAstUtil` is a utility class (adapted from the
jPinpoint project) that provides reusable AST traversal methods used across multiple Java-based
rules. Key method:

```java
isDirectDescendantOf(node, KtFunctionDeclaration funcDecl)
```

Walks up the ancestor chain of `node` and returns `true` only if the first
`KtFunctionDeclaration` ancestor is exactly `funcDecl`. This is critical for complexity
rules: it ensures that nodes inside **nested function declarations** are not counted
towards the enclosing function's complexity.

**Lambda caveat:** `KtFunctionLiteral` (a lambda) is NOT a `KtFunctionDeclaration`, so
lambda bodies ARE counted in the enclosing function's complexity. This is intentional --
a lambda is logically part of the function that contains it.

### 14.2 `ExcessiveParameterList`

Counts the number of declared parameters for every function/constructor and reports when
the count exceeds a configurable threshold (default: 10, same as the Java rule).

**AST differences between function types:**

| Function type | Parameter node |
|---|---|
| Top-level / member functions | `functionValueParameters() → functionValueParameter()` |
| Primary constructor | `classParameters() → classParameter()` |
| Secondary constructor | `functionValueParameters() → functionValueParameter()` |

All three are handled by visiting `KtFunctionDeclaration`, `KtPrimaryConstructor`, and
`KtSecondaryConstructor` separately.

**Property:** `threshold` (int, default 10). Violation fires when count **strictly exceeds**
the threshold (`count > threshold`).

### 14.3 `CyclomaticComplexity`

Counts decision points in each function and reports when the total exceeds a configurable
threshold (default: `methodReportLevel` = 10, same as the Java rule).

**Counting rules (base 1 + 1 per):**

| Construct | AST node | Notes |
|---|---|---|
| `if` | `KtIfExpression` | always +1 |
| `when` branch | `KtWhenEntry` | +1 only when `ELSE() == null` (conditional branches) |
| `for` | `KtForStatement` | |
| `while` | `KtWhileStatement` | |
| `do..while` | `KtDoWhileStatement` | |
| `catch` | `KtCatchBlock` | |
| `\|\|` | `KtDisjunction` | +`DISJ().size()` (one per operator) |
| `&&` | `KtConjunction` | +`CONJ().size()` (one per operator) |
| `?:` (Elvis) | `KtElvisExpression` | +`elvis().size()` (one per operator) |

Elvis is counted the same as Java's ternary `?:`. `NodeStream` does not support
`mapToInt()` directly; use `.toList().stream().mapToInt(...).sum()` when summing
operator counts.

All nodes are filtered with `KotlinAstUtil.isDirectDescendantOf(n, functionNode)` to
exclude nested function declarations from the enclosing function's count.

The violation message uses `{0}` (function name via `getImage()`) with `{1}` (actual
complexity) and `{2}` (threshold), matching the Java rule's message format convention.

**Omitted vs Java:** The Java rule also has `classReportLevel` (default 80) for class-level
complexity rollup. The Kotlin rule is method-only; class-level aggregation is deferred.