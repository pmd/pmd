---
title: PMD Release Notes
permalink: pmd_release_notes.html
keywords: changelog, release notes
---

{% if is_release_notes_processor %}
{% comment %}
This allows to use links e.g. [Basic CLI usage]({{ baseurl }}pmd_userdocs_installation.html) that work both
in the release notes on GitHub (as an absolute url) and on the rendered documentation page (as a relative url).
{% endcomment %}
{% capture baseurl %}https://docs.pmd-code.org/pmd-doc-{{ site.pmd.version }}/{% endcapture %}
{% else %}
{% assign baseurl = "" %}
{% endif %}

## {{ site.pmd.date | date: "%d-%B-%Y" }} - {{ site.pmd.version }}

The PMD team is pleased to announce PMD {{ site.pmd.version }}.

This is a {{ site.pmd.release_type }} release.

{% tocmaker is_release_notes_processor %}

### 🚀️ New and noteworthy

#### New Kotlin rules and XPath helper

New `pmd-kotlin:nodeText()` XPath function that returns the raw source text of the
context node. This enables literal value checks in XPath rules (e.g. single-char string
arguments, numeric literals) where no typed AST accessor exists.

New rules for Kotlin:

**Best practices**
* [`LooseCoupling`]({{ baseurl }}pmd_rules_kotlin_bestpractices.html#loosecoupling): Avoid
  declaring class-level fields with concrete collection types (ArrayList, HashMap, HashSet, etc.).
  Prefer MutableList, MutableMap, or MutableSet interfaces for better flexibility.

**Error prone**
* [`AvoidCatchingNPE`]({{ baseurl }}pmd_rules_kotlin_errorprone.html#avoidcatchingnpe): Catching
  NullPointerException is a code smell in Kotlin. Use null-safety operators (?., ?:, !!) instead.
* [`DoNotTerminateVM`]({{ baseurl }}pmd_rules_kotlin_errorprone.html#donotterminatevm): Avoid
  calling System.exit() or Runtime.halt(). Throw an exception or use a shutdown hook instead.
* [`DoNotExtendJavaLangError`]({{ baseurl }}pmd_rules_kotlin_errorprone.html#donotextendjavalang): Do
  not extend Error or its standard subclasses; extend Exception or RuntimeException instead.
* [`UseLocaleWithCaseConversions`]({{ baseurl }}pmd_rules_kotlin_errorprone.html#uselocalewithcaseconversions):
  String.toLowerCase()/toUpperCase() use the default system Locale. Use Kotlin's locale-safe
  lowercase()/uppercase() extensions instead.

**Performance**
* [`AppendCharacterWithChar`]({{ baseurl }}pmd_rules_kotlin_performance.html#appendcharacterwithchar):
  Replace single-char String literals in StringBuilder.append() with Char literals to avoid
  unnecessary String allocation.
* [`AvoidCallingGcExplicitly`]({{ baseurl }}pmd_rules_kotlin_performance.html#avoidcallinggcexplicitly):
  Avoid calling System.gc() or Runtime.getRuntime().gc(). These are unreliable and degrade throughput.
* [`BigIntegerInstantiation`]({{ baseurl }}pmd_rules_kotlin_performance.html#bigintegerinstantiation):
  Use BigInteger.ZERO, BigInteger.ONE, or BigInteger.TEN instead of BigInteger.valueOf(0/1/10).
* [`UseIndexOfChar`]({{ baseurl }}pmd_rules_kotlin_performance.html#useindexofchar): Use
  indexOf(Char) / lastIndexOf(Char) instead of indexOf(String) / lastIndexOf(String) for
  single-character arguments.
* [`UseStringBuilderLength`]({{ baseurl }}pmd_rules_kotlin_performance.html#usestringbuilderlength):
  Use StringBuilder.length directly instead of StringBuilder.toString().length to avoid
  allocating a temporary String.

#### More new Kotlin rules (second batch)

**Best practices**
* [`UseCollectionIsEmpty`]({{ baseurl }}pmd_rules_kotlin_bestpractices.html#usecollectionisempty): Replace
  `collection.size == 0` (or `!= 0`) with `collection.isEmpty()` / `collection.isNotEmpty()`.
* [`UseStandardCharsets`]({{ baseurl }}pmd_rules_kotlin_bestpractices.html#usestandardcharsets): Use
  `StandardCharsets.UTF_8` (or similar constants) instead of `Charset.forName("UTF-8")`.

**Error prone**
* [`EqualsNull`]({{ baseurl }}pmd_rules_kotlin_errorprone.html#equalsnull): Replace `x.equals(null)` with
  a direct null check (`x == null`). `equals(null)` always returns false and is likely a bug.
* [`ReplaceJavaUtilDate`]({{ baseurl }}pmd_rules_kotlin_errorprone.html#replacejavautildate): Avoid using
  `java.util.Date`; prefer `java.time.LocalDate`, `LocalDateTime`, or `Instant` instead.
* [`ReturnFromFinallyBlock`]({{ baseurl }}pmd_rules_kotlin_errorprone.html#returnfromfinallyblock): A
  `return` inside a `finally` block silently swallows any exception thrown in the `try` block.
* [`SimpleDateFormatNeedsLocale`]({{ baseurl }}pmd_rules_kotlin_errorprone.html#simpledateformatneedslocale):
  `SimpleDateFormat` constructed without a Locale uses the default system Locale and can produce
  locale-sensitive output unexpectedly.
* [`UnnecessaryCaseChange`]({{ baseurl }}pmd_rules_kotlin_errorprone.html#unnecessarycasechange): Calling
  `.toLowerCase().equals(...)` or `.toUpperCase().equals(...)` is redundant; use
  `.equals(..., ignoreCase = true)` instead.

**Performance**
* [`AddEmptyString`]({{ baseurl }}pmd_rules_kotlin_performance.html#addemptystring): Avoid appending an
  empty string literal `""` to convert a value to String; use `.toString()` or string templates instead.
* [`ConsecutiveLiteralAppends`]({{ baseurl }}pmd_rules_kotlin_performance.html#consecutiveliteralappends):
  Multiple consecutive `StringBuilder.append()` calls with string literals should be combined into a
  single `append()` call to reduce method-call overhead.
* [`UselessStringValueOf`]({{ baseurl }}pmd_rules_kotlin_performance.html#uselessstringvalueof): Avoid
  wrapping a value in `String.valueOf()`; use a string template or `.toString()` instead.

#### Type-aware XPath functions for Kotlin

New XPath helper functions using `kotlin-type-mapper` for fully type-resolved rule matching:

* `pmd-kotlin:typeIs(typeName)` — true if the type of the context node matches the given fully-qualified type name.
* `pmd-kotlin:typeIsExactly(typeName)` — like `typeIs` but without subtype matching.
* `pmd-kotlin:matchesSig(signature)` — true if the context call expression matches the given method/constructor signature.
* `pmd-kotlin:isNullable()` — true if the type of the context node is a nullable Kotlin type.

#### Kotlin rules migrated from Java (type-aware, third batch)

**Best practices**
* [`AvoidMessageDigestField`]({{ baseurl }}pmd_rules_kotlin_bestpractices.html#avoidmessagedigestfield):
  `java.security.MessageDigest` is not thread-safe. Declaring it as a class field exposes it to concurrent access issues.
* [`AvoidPrintStackTrace`]({{ baseurl }}pmd_rules_kotlin_bestpractices.html#avoidprintstacktrace):
  Avoid calling `Throwable.printStackTrace()`; use a proper logger instead.
* [`AvoidStringBufferField`]({{ baseurl }}pmd_rules_kotlin_bestpractices.html#avoidstringbufferfield):
  `StringBuffer` and `StringBuilder` instances can grow considerably. Holding them as class fields may cause memory issues.
* [`FunctionNameTooShort`]({{ baseurl }}pmd_rules_kotlin_bestpractices.html#functionnametooshort):
  Function names should be descriptive and easy to understand. Very short names obscure intent.
* [`PreserveStackTrace`]({{ baseurl }}pmd_rules_kotlin_bestpractices.html#preservestacktrace):
  When re-throwing a new exception from a catch block, pass the original exception as the cause to preserve the stack trace.
* [`SystemPrintln`]({{ baseurl }}pmd_rules_kotlin_bestpractices.html#systemprintln):
  Calls to `System.out.print*`, `System.err.print*`, or `System.out.printf` are typically debug code; use a logger instead.

**Design**
* [`CognitiveComplexity`]({{ baseurl }}pmd_rules_kotlin_design.html#cognitivecomplexity):
  Cognitive complexity measures how difficult a function is to understand by penalising nested control structures.
* [`CollapsibleIfStatements`]({{ baseurl }}pmd_rules_kotlin_design.html#collapsibleifstatements):
  Nested `if` statements with no intervening `else` can be collapsed into a single `if` with `&&`.
* [`CyclomaticComplexity`]({{ baseurl }}pmd_rules_kotlin_design.html#cyclomaticcomplexity):
  Reports functions whose cyclomatic complexity exceeds a configurable threshold.
* [`ExcessiveParameterList`]({{ baseurl }}pmd_rules_kotlin_design.html#excessiveparameterlist):
  Functions and constructors with too many parameters are harder to maintain and call correctly.
* [`SimplifyBooleanExpressions`]({{ baseurl }}pmd_rules_kotlin_design.html#simplifybooleanexpressions):
  Avoid redundant boolean identity comparisons such as `x == true` or `x && true`.
* [`SimplifyBooleanReturns`]({{ baseurl }}pmd_rules_kotlin_design.html#simplifybooleanreturns):
  Avoid `if`/`else` blocks that return a bare `true` or `false`; return the condition directly.

**Error prone**
* [`AvoidBranchingStatementAsLastInLoop`]({{ baseurl }}pmd_rules_kotlin_errorprone.html#avoidbranchingstatementaslastinloop):
  A `break`, `continue`, or `return` as the unconditional last statement in a loop body makes the loop execute at most once.
* [`AvoidCallingFinalize`]({{ baseurl }}pmd_rules_kotlin_errorprone.html#avoidcallingfinalize):
  `finalize()` is called by the garbage collector automatically; calling it explicitly causes double-finalisation.
* [`AvoidCatchingGenericException`]({{ baseurl }}pmd_rules_kotlin_errorprone.html#avoidcatchinggenericexception):
  Catching `Exception` or `RuntimeException` hides real error types and makes error handling imprecise.
* [`AvoidCatchingThrowable`]({{ baseurl }}pmd_rules_kotlin_errorprone.html#avoidcatchingthrowable):
  `Throwable` is the root of both `Exception` and `Error`. Catching it can swallow virtual machine errors.
* [`AvoidDecimalFormatAsField`]({{ baseurl }}pmd_rules_kotlin_errorprone.html#avoiddecimalformatasfield):
  `DecimalFormat` and `ChoiceFormat` are thread-unsafe. Declaring them as fields can cause data corruption in concurrent use.
* [`AvoidDecimalLiteralsInBigDecimalConstructor`]({{ baseurl }}pmd_rules_kotlin_errorprone.html#avoiddecimalliteralsinbigdecimalconstructor):
  `BigDecimal(0.1)` does not equal `0.1` exactly due to floating-point representation. Use `BigDecimal("0.1")` instead.
* [`AvoidRethrowingException`]({{ baseurl }}pmd_rules_kotlin_errorprone.html#avoidrethrowingexception):
  A catch block that only rethrows the caught exception adds no value; remove the try/catch.
* [`AvoidThrowingNullPointerException`]({{ baseurl }}pmd_rules_kotlin_errorprone.html#avoidthrowingnullpointerexception):
  Explicitly throwing `NullPointerException` is misleading; throw a more specific exception instead.
* [`AvoidThrowingRawExceptionTypes`]({{ baseurl }}pmd_rules_kotlin_errorprone.html#avoidthrowingrawexceptiontypes):
  Throwing root types like `RuntimeException` or `Exception` loses semantic information; use or create a specific subtype.
* [`OverrideBothEqualsAndHashcode`]({{ baseurl }}pmd_rules_kotlin_errorprone.html#overridebothequalsandhashcode):
  Override both `equals()` and `hashCode()`, or neither. Overriding only one breaks the contract.
* [`ReturnEmptyCollectionRatherThanNull`]({{ baseurl }}pmd_rules_kotlin_errorprone.html#returnemptycollectionratherthannull):
  Returning `null` from a collection-typed function forces callers to do null checks; return an empty collection instead.
* [`UnresolvedType`]({{ baseurl }}pmd_rules_kotlin_errorprone.html#unresolvedtype):
  Reports import declarations whose type could not be resolved. Add the missing dependency to the PMD analysis classpath.

**Multithreading**
* [`DontCallThreadRun`]({{ baseurl }}pmd_rules_kotlin_multithreading.html#dontcallthreadrun):
  Calling `thread.run()` executes the `run()` method on the current thread, not a new one. Use `thread.start()` instead.
* [`UseConcurrentHashMap`]({{ baseurl }}pmd_rules_kotlin_multithreading.html#useconcurrenthashmap):
  `HashMap` is not thread-safe. Use `ConcurrentHashMap` in multi-threaded contexts.
* [`UseNotifyAllInsteadOfNotify`]({{ baseurl }}pmd_rules_kotlin_multithreading.html#usenotifyallinsteadofnotify):
  `notify()` wakes only one waiting thread, chosen arbitrarily. Use `notifyAll()` unless only one thread can proceed.

**Performance**
* [`AvoidArrayLoops`]({{ baseurl }}pmd_rules_kotlin_performance.html#avoidarrayloops):
  Manual element-by-element array copying is less efficient and less clear than `System.arraycopy()` or `copyOf()`.
* [`AvoidCalendarDateCreation`]({{ baseurl }}pmd_rules_kotlin_performance.html#avoidcalendardatecreation):
  `java.util.Calendar` is heavyweight. Creating it just to get the current date/time is wasteful; use `java.time` instead.
* [`AvoidFileStream`]({{ baseurl }}pmd_rules_kotlin_performance.html#avoidfilestream):
  `FileInputStream`/`FileOutputStream` contain a finalizer that degrades GC performance. Use `Files.newInputStream()` instead.
* [`AvoidInstantiatingObjectsInLoops`]({{ baseurl }}pmd_rules_kotlin_performance.html#avoidinstantiatingobjectsinloops):
  Creating objects inside a loop may cause unnecessary garbage-collection pressure. Hoist creation outside the loop where possible.
* [`InefficientEmptyStringCheck`]({{ baseurl }}pmd_rules_kotlin_performance.html#inefficientemptystringcheck):
  Calling `str.trim().isEmpty()` creates a temporary trimmed string. Use `str.isBlank()` instead.
* [`InefficientStringBuffering`]({{ baseurl }}pmd_rules_kotlin_performance.html#inefficientstringbuffering):
  Passing a `+` string concatenation to `StringBuilder.append()` creates a hidden intermediate `String`; chain `append()` calls instead.
* [`StringInstantiation`]({{ baseurl }}pmd_rules_kotlin_performance.html#stringinstantiation):
  `String(someString)` simply copies the string; the original reference can be used directly.
* [`StringToString`]({{ baseurl }}pmd_rules_kotlin_performance.html#stringtostring):
  Calling `.toString()` on a `String` is a no-op; the same instance is returned.
* [`TooFewBranchesForWhenExpression`]({{ baseurl }}pmd_rules_kotlin_performance.html#toofewbranchesforwhenexpression):
  A condition-only `when` expression with very few branches is less clear than a simple `if`/`else`.
* [`UseStringBuilderForStringAppends`]({{ baseurl }}pmd_rules_kotlin_performance.html#usestringbuilderforstringappends):
  Using `+=` to concatenate strings inside a loop creates a hidden temporary on each iteration; use `StringBuilder` instead.

#### Kotlin ANTLR parse stability

* A fresh `ParserATNSimulator` is now created per file, preventing unbounded ANTLR DFA state
  accumulation across files which caused exponential slowdowns on large codebases.
* A configurable per-file parse timeout (default 30 s, system property `pmd.kotlin.parseTimeoutSeconds`)
  interrupts files that trigger ANTLR LL-prediction explosion. Timed-out files are reported as
  `ParseException` processing errors in the PMD output, addressing [#6608](https://github.com/pmd/pmd/issues/6608).

### 🐛️ Fixed Issues

### 🚨️ API Changes

### ✨️ Merged pull requests
<!-- content will be automatically generated, see /do-release.sh -->

### 📦️ Dependency updates
<!-- content will be automatically generated, see /do-release.sh -->

### 📈️ Stats
<!-- content will be automatically generated, see /do-release.sh -->

{% endtocmaker %}

