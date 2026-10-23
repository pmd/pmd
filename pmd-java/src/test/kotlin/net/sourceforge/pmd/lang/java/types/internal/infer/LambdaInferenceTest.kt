/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.types.*
import net.sourceforge.pmd.lang.java.types.internal.infer.ast.JavaExprMirrors
import net.sourceforge.pmd.lang.java.types.testdata.TypeInferenceTestCases
import net.sourceforge.pmd.lang.test.ast.component6
import net.sourceforge.pmd.lang.test.ast.component7
import net.sourceforge.pmd.lang.test.ast.shouldBe
import net.sourceforge.pmd.lang.test.ast.shouldBeA
import net.sourceforge.pmd.lang.test.ast.shouldMatchN
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.function.DoubleConsumer
import java.util.function.Supplier
import kotlin.test.assertEquals

@Suppress("UNUSED_VARIABLE")
class LambdaInferenceTest : ProcessorTestSpec({

    parserTestContainer("Test dangling method parameter - ok") {
        importedTypes += java.util.List::class.java
        importedTypes += TypeInferenceTestCases::class.java
        genClassHeader = "class TypeInferenceTestCases"
        packageName = "net.sourceforge.pmd.types.testdata.typeinference"

        val chain = """
            // public static <T, K> T wild(K t)

            // OK - no obvious <T> for wild but since functional method is void it's ok
            java.util.stream.Stream.of(1)
                                   .peek(i -> wild(i))
                                   .collect(java.util.stream.Collectors.toList())

                    """

        inContext(ExpressionParsingCtx) {
            chain should parseAs {
                methodCall("collect") {
                    it shouldHaveType with(it.typeDsl) { gen.t_List[int.box()] } // List<Integer>

                    it::getQualifier shouldBe methodCall("peek") {
                        it shouldHaveType with(it.typeDsl) { gen.t_Stream[int.box()] } // Stream<Integer>

                        it::getQualifier shouldBe methodCall("of") {
                            it shouldHaveType with(it.typeDsl) { gen.t_Stream[int.box()] } // Stream<Integer>
                            it::getQualifier shouldBe typeExpr {
                                qualClassType("java.util.stream.Stream")
                            }

                            it::getArguments shouldBe child {
                                int(1)
                            }
                        }

                        it::getArguments shouldBe child {

                            child<ASTLambdaExpression> {
                                unspecifiedChild() // params

                                methodCall("wild") {
                                    argList {
                                        variableAccess("i")
                                    }
                                }
                            }
                        }
                    }
                    it::getArguments shouldBe child {
                        unspecifiedChild()
                    }
                }
            }
        }
    }

    parserTestContainer("Test dangling method parameter recovery") {
        importedTypes += java.util.List::class.java
        importedTypes += TypeInferenceTestCases::class.java
        genClassHeader = "class TypeInferenceTestCases"
        packageName = "net.sourceforge.pmd.typeresolution.testdata.typeinference"

        val chain = """
            // public static <T, K> T wild(K t)

             // Javac error - <R> of map cannot be bound
             // we infer it as Object to recover
            java.util.stream.Stream.of(1)
                                   .map(i -> wild(i))
                                   .collect(java.util.stream.Collectors.toList())

                    """

        inContext(ExpressionParsingCtx) {
            chain should parseAs {
                methodCall("collect") {
                    it shouldHaveType with(it.typeDsl) { gen.t_List[ts.UNKNOWN] } // List</*unresolved*/>

                    it::getQualifier shouldBe methodCall("map") {
                        it shouldHaveType with(it.typeDsl) { gen.t_Stream[ts.UNKNOWN] } // Stream</*unresolved*/>

                        it::getQualifier shouldBe methodCall("of") {
                            it shouldHaveType with(it.typeDsl) { gen.t_Stream[int.box()] } // Stream<Integer>
                            it::getQualifier shouldBe typeExpr {
                                qualClassType("java.util.stream.Stream")
                            }

                            it::getArguments shouldBe child {
                                int(1)
                            }
                        }

                        it::getArguments shouldBe child {

                            child<ASTLambdaExpression> {
                                unspecifiedChild() // params

                                methodCall("wild") {
                                    argList {
                                        variableAccess("i")
                                    }
                                }
                            }
                        }
                    }
                    it::getArguments shouldBe child {
                        unspecifiedChild()
                    }
                }
            }
        }
    }

    parserTest("Test functional interface induced by intersection") {
        val acu = parser.parse(
            """
            import java.io.Serializable;
            import java.util.function.Function;

            class Scratch {

                public static <T extends Function<String, Integer> & Serializable>
                T f(T k) {
                    return k;
                }

                public static void main(String... args) {
                    f(s -> s.length());
                }
            }
        """
        )

        val (t_Scratch) = acu.descendants(ASTClassDeclaration::class.java).toList { it.typeMirror }
        val (f) = acu.descendants(ASTMethodDeclaration::class.java).toList()
        val (fCall) = acu.descendants(ASTMethodCall::class.java).toList()

        fCall.shouldMatchN {
            methodCall("f") {
                it.methodType.symbol shouldBe f.symbol

                with(it.typeDsl) {
                    // Function<String, Integer> & java.io.Serializable
                    val serialFun = gen.t_Function[gen.t_String, int.box()] * ts.SERIALIZABLE

                    it.methodType.shouldMatchMethod(
                        named = "f",
                        declaredIn = t_Scratch,
                        withFormals = listOf(serialFun),
                        returning = serialFun
                    )
                }

                argList {
                    exprLambda {
                        lambdaFormals(1)
                        methodCall("length") {
                            variableAccess("s") {
                                it shouldHaveType it.typeSystem.STRING
                            }
                            argList(0)
                        }
                    }
                }
            }
        }
    }

    parserTest("Test functional interface induced by intersection 2") {
        val acu = parser.parse(
            """
            import java.io.Serializable;
            import java.util.function.Function;

            class Scratch {

                public static <R, T extends Function<String, R> & Serializable>
                T f(T k) {
                    return k;
                }

                public static void main(String... args) {
                    // Note that R is dangling until we add the return constraint R >: Integer
                    f(s -> s.length());
                }
            }
        """
        )

        val (t_Scratch) = acu.descendants(ASTClassDeclaration::class.java).toList { it.typeMirror }
        val (f) = acu.descendants(ASTMethodDeclaration::class.java).toList()
        val (fCall) = acu.descendants(ASTMethodCall::class.java).toList()

        fCall.shouldMatchN {
            methodCall("f") {
                it.methodType.symbol shouldBe f.symbol

                with(it.typeDsl) {
                    // Function<String, Integer> & java.io.Serializable
                    val serialFun = gen.t_Function[gen.t_String, int.box()] * ts.SERIALIZABLE

                    it.methodType.shouldMatchMethod(
                        named = "f",
                        declaredIn = t_Scratch,
                        withFormals = listOf(serialFun),
                        returning = serialFun
                    )
                }

                argList {
                    exprLambda {
                        lambdaFormals(1)
                        methodCall("length") {
                            variableAccess("s") {
                                it shouldHaveType it.typeSystem.STRING
                            }
                            argList(0)
                        }
                    }
                }
            }
        }
    }

    parserTest("Test lambda with field access in return expression (inner ctor call)") {
        val acu = parser.parse(
            """
            import java.util.function.Function;

            class Scratch {

                class WithField {
                    int i;
                }

                static void foo(Function<Scratch, Integer> f) { }

                void main(String[] args) {
                    // Symbol resolution for .i must not fail, even though its
                    // LHS depends on the lambda parameter, and it is a return
                    // expression of the lambda, so is used by compatibility check

                    foo(s -> s.new WithField().i);
                }
            }
        """
        )

        val (t_Scratch, t_WithField) = acu.descendants(ASTClassDeclaration::class.java).toList { it.typeMirror }
        val (foo) = acu.descendants(ASTMethodDeclaration::class.java).toList()
        val (fooCall) = acu.descendants(ASTMethodCall::class.java).toList()

        fooCall.shouldMatchN {
            methodCall("foo") {
                argList {
                    exprLambda {
                        lambdaFormals(1)

                        fieldAccess("i") {
                            it shouldHaveType it.typeSystem.INT
                            constructorCall {
                                variableAccess("s") {
                                    it shouldHaveType t_Scratch
                                }
                                classType("WithField") {
                                    it shouldHaveType t_WithField
                                }
                                argList(0)
                            }
                        }
                    }
                }
                it.methodType.symbol shouldBe foo.symbol // ask after asking for type of inner
            }
        }
    }

    parserTest("Test lambda with field access in return expression (method call)") {
        val acu = parser.parse(
            """
            import java.util.function.Function;

            class Scratch {

                static class WithField {
                    int i;
                }

                WithField fetch() { return new WithField(); }

                static void foo(Function<Scratch, Integer> f) { }

                void main(String[] args) {
                    // Symbol resolution for .i must not fail, even though its
                    // LHS depends on the lambda parameter, and it is a return
                    // expression of the lambda, so is used by compatibility check

                    foo(s -> s.fetch().i);
                }
            }
        """
        )

        val (t_Scratch, t_WithField) = acu.descendants(ASTClassDeclaration::class.java).toList { it.typeMirror }
        val (fetch, foo) = acu.descendants(ASTMethodDeclaration::class.java).toList()
        val (fooCall) = acu.descendants(ASTMethodCall::class.java).toList()

        fooCall.shouldMatchN {
            methodCall("foo") {
                it.methodType.symbol shouldBe foo.symbol
                argList {
                    exprLambda {
                        lambdaFormals(1)

                        fieldAccess("i") {
                            it shouldHaveType it.typeSystem.INT
                            methodCall("fetch") {
                                variableAccess("s") {
                                    it shouldHaveType t_Scratch
                                }
                                argList(0)
                            }
                        }
                    }
                }
            }
        }
    }

    parserTest("Method invocation selection in lambda return") {
        val acu = parser.parse(
            """
class Scratch {

    interface Foo<T, R> {

        R accept(T t);
    }

    static <R> R ctx(Foo<G<R>, R> t) { return null; }

    interface G<I> {

        I fetch();
    }

    static {
        String r = ctx(g -> g.fetch());
    }
}

        """.trimIndent()
        )

        val (_, _, t_G) = acu.descendants(ASTTypeDeclaration::class.java).toList { it.typeMirror }

        val call = acu.descendants(ASTMethodCall::class.java).firstOrThrow()

        call.shouldMatchN {
            methodCall("ctx") {

                argList {
                    exprLambda {
                        lambdaFormals(1)
                        methodCall("fetch") {
                            variableAccess("g")

                            with(it.typeDsl) {
                                it.methodType.shouldMatchMethod(
                                    named = "fetch",
                                    declaredIn = t_G[gen.t_String],
                                    withFormals = emptyList(),
                                    returning = gen.t_String
                                )
                            }

                            argList(0)
                        }
                    }
                }
            }
        }
    }

    parserTest("Block lambda") {
        val acu = parser.parse(
            """
class Scratch {

    interface Foo<T, R> {

        R accept(T t);
    }

    static <R> R ctx(Foo<G<R>, R> t) { return null; }

    interface G<I> {

        I fetch();
    }

    static {
        String r = ctx(g -> { return g.fetch(); });
    }
}

        """.trimIndent()
        )

        val (_, _, t_G) = acu.descendants(ASTTypeDeclaration::class.java).toList { it.typeMirror }

        val call = acu.descendants(ASTMethodCall::class.java).firstOrThrow()

        call.shouldMatchN {
            methodCall("ctx") {

                argList {
                    blockLambda {
                        lambdaFormals(1)
                        block {
                            returnStatement {
                                methodCall("fetch") {
                                    variableAccess("g")

                                    with(it.typeDsl) {
                                        it.methodType.shouldMatchMethod(
                                            named = "fetch",
                                            declaredIn = t_G[gen.t_String],
                                            withFormals = emptyList(),
                                            returning = gen.t_String
                                        )
                                    }

                                    argList(0)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    parserTestContainer("Lambda body compatibility", javaVersions = listOf(JavaVersion.J1_8, JavaVersion.Latest)) {
        suspend fun testBody(body: String, void: Boolean = false, value: Boolean = false) {
            doTest(body) {
                val acu = parser.parse(
                    """
                    import java.util.function.Supplier;
                    class Scratch {
                        static final boolean FOREVER = 1 < 2;
                        int i;
                        static String str() { return ""; }
                        void test(boolean b) {
                            final boolean forever = 1 < 2;
                            Object lambda = () -> $body;
                        }
                    }
                    """.trimIndent()
                )

                val infer = Infer(testTypeSystem, 8, TypeInferenceLogger.noop())
                val mirrors = JavaExprMirrors.forTypeResolution(infer)
                val lambda = acu.descendants(ASTLambdaExpression::class.java).firstOrThrow()
                val mirror = mirrors.getTopLevelFunctionalMirror(lambda) as ExprMirror.LambdaExprMirror
                withClue(lambda) {
                    assertEquals(void, mirror.isVoidCompatible, "void compatible")
                    assertEquals(value, mirror.isValueCompatible, "value compatible")
                }
            }
        }

        testBody("{ return str(); }", value = true)
        testBody("{ return; }", void = true)
        testBody("{ }", void = true)
        testBody("i + 2", value = true)
        testBody("str()", void = true, value = true)
        testBody("new Scratch()", void = true, value = true)
        testBody("{ throw new IllegalStateException(); }", void = true, value = true)
        testBody("{ if (b) throw new IllegalStateException(); }", void = true)
        testBody("{ if (b) return str(); }")
        testBody("{ if (true) return str(); }")
        testBody("{ if (b) return str(); else return str(); }", value = true)
        testBody("{ if (b) return str(); else return; }")
        testBody("{ if (b) return; throw new IllegalStateException(); }", void = true)

        testBody("{ while (true) { } }", void = true, value = true)
        testBody("{ do { } while (true); }", void = true, value = true)
        testBody("{ for (;;) { } }", void = true, value = true)
        testBody("{ for (; true;) { } }", void = true, value = true)
        testBody("{ while (1 < 2) { } }", void = true, value = true)
        testBody("{ do { } while (1 < 2); }", void = true, value = true)
        testBody("{ for (; 1 < 2;) { } }", void = true, value = true)
        testBody("{ while (forever) { } }", void = true, value = true)
        testBody("{ while (FOREVER) { } }", void = true, value = true)
        testBody("{ while (b) { } }", void = true)
        testBody("{ while (true || b) { } }", void = true)

        testBody("{ while (true) { if (b) break; } }", void = true)
        testBody("{ outer: while (true) { while (true) { break outer; } } }", void = true)
        testBody("{ while (true) { while (true) { break; } } }", void = true, value = true)
        testBody("{ while (true) { continue; } }", void = true, value = true)
        testBody("{ do { continue; } while (b); }", void = true)
        testBody("{ outer: do { while (true) { continue outer; } } while (b); }", void = true)
        testBody("{ do { while (true) { continue; } } while (b); }", void = true, value = true)
        testBody("{ while (true) { try { break; } finally { throw new IllegalStateException(); } } }", void = true, value = true)
        testBody("{ do { try { continue; } finally { while (true) { } } } while (b); }", void = true, value = true)

        testBody("{ switch (i) { default: case 1: return 1; } }", value = true)
        testBody("{ switch (i) { case 1: i++; default: return 1; } }", value = true)
        testBody("{ switch (i) { case 1: return 1; default: } }")
        testBody("{ switch (i) { case 1: break; default: return 1; } }")

        testBody("{ Runnable nested = () -> { return; }; return str(); }", value = true)
        testBody("{ Supplier<String> nested = () -> { return str(); }; }", void = true)
        testBody("{ class Nested { void run() { return; } } return str(); }", value = true)
        testBody("{ class Nested { String get() { return str(); } } }", void = true)
        testBody("{ Object nested = new Object() { String get() { return str(); } }; }", void = true)
    }

    parserTestContainer("Block lambda selects submit overload", javaVersions = listOf(JavaVersion.J1_8, JavaVersion.Latest)) {
        for ((body, value) in listOf(
            "while (true) { }" to true,
            "if (flag) { throw new IllegalStateException(); }" to false,
            "while (1 < 2) { }" to true,
            "do { } while (forever);" to true,
            "for (; FOREVER;) { }" to true,
            "for (;;) { }" to true
        )) {
            doTest(body) {
                val (acu, spy) = parser.parseWithTypeInferenceSpy(
                    """
                    import java.util.concurrent.ExecutorService;
                    class Scratch {
                        static final boolean FOREVER = 1 < 2;
                        void test(ExecutorService pool, boolean flag) {
                            final boolean forever = 1 < 2;
                            pool.submit(() -> { $body });
                        }
                    }
                    """.trimIndent()
                )

                val call = acu.descendants(ASTMethodCall::class.java).firstOrThrow()
                spy.shouldBeOk {
                    call.methodType.shouldMatchMethod(
                        named = "submit",
                        declaredIn = ExecutorService::class.raw,
                        withFormals = listOf(if (value) Callable::class[ts.OBJECT] else Runnable::class.raw),
                        returning = if (value) Future::class[ts.OBJECT] else Future::class[`?`]
                    )
                    call.overloadSelectionInfo.isFailed shouldBe false
                }
            }
        }
    }

    parserTest("Loop condition uses implicitly typed lambda parameter", javaVersions = listOf(JavaVersion.J1_8, JavaVersion.Latest)) {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            import java.util.function.Consumer;
            import java.util.function.Function;
            class Scratch {
                static class Holder { boolean flag; }
                static void foo(Function<Holder, Integer> f) { }
                static void foo(Consumer<Holder> f) { }
                void test() {
                    Function<Holder, Integer> direct = s -> { while (s.flag) { } return 1; };
                    foo(s -> { while (s.flag) { } return 1; });
                }
            }
            """.trimIndent()
        )

        val (t_Scratch, t_Holder) = acu.declaredTypeSignatures()
        val call = acu.descendants(ASTMethodCall::class.java).firstOrThrow()
        val lambdas = acu.descendants(ASTLambdaExpression::class.java).toList()
        val fields = acu.descendants(ASTFieldAccess::class.java).crossFindBoundaries().toList()
        val flagSymbol = acu.varId("flag").symbol
        spy.shouldBeOk {
            val functionType = java.util.function.Function::class[t_Holder, ts.INT.box()]
            lambdas.forEach { it shouldHaveType functionType }
            call.methodType.shouldMatchMethod(
                named = "foo",
                declaredIn = t_Scratch,
                withFormals = listOf(functionType),
                returning = ts.NO_TYPE
            )
            call.overloadSelectionInfo.isFailed shouldBe false
            fields.size shouldBe 2
            fields.forEach {
                it shouldHaveType ts.BOOLEAN
                it.referencedSym shouldBe flagSymbol
                it.qualifier shouldHaveType t_Holder
            }
        }
    }

    parserTest("Exhaustive pattern switch lambda without default") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            import java.util.function.Function;
            class Scratch {
                Function<Object, Integer> f = o -> {
                    switch (o) {
                        case String s -> { return 1; }
                        case Object x -> { return 2; }
                    }
                };
            }
            """.trimIndent()
        )

        val lambda = acu.descendants(ASTLambdaExpression::class.java).firstOrThrow()
        val infer = Infer(testTypeSystem, 21, TypeInferenceLogger.noop())
        val mirror = JavaExprMirrors.forTypeResolution(infer)
            .getTopLevelFunctionalMirror(lambda) as ExprMirror.LambdaExprMirror
        mirror.isVoidCompatible shouldBe false
        mirror.isValueCompatible shouldBe true
        spy.shouldBeOk {
            lambda shouldHaveType java.util.function.Function::class[ts.OBJECT, ts.INT.box()]
        }
    }

    parserTestContainer("Exhaustive boolean switch lambda without default") {
        for ((trueLabel, falseLabel) in listOf("true" to "false", "YES" to "NO")) {
            doTest("$trueLabel / $falseLabel") {
                val (acu, spy) = parser.parseWithTypeInferenceSpy(
                    """
                    import java.util.function.Supplier;
                    class Scratch {
                        static final boolean YES = true, NO = false;
                        void test(boolean b) {
                            Supplier<Integer> f = () -> {
                                switch (b) {
                                    case $trueLabel -> { return 1; }
                                    case $falseLabel -> { return 2; }
                                }
                            };
                        }
                    }
                    """.trimIndent()
                )

                val lambda = acu.descendants(ASTLambdaExpression::class.java).firstOrThrow()
                val infer = Infer(testTypeSystem, 27, TypeInferenceLogger.noop())
                val mirror = JavaExprMirrors.forTypeResolution(infer)
                    .getTopLevelFunctionalMirror(lambda) as ExprMirror.LambdaExprMirror
                mirror.isVoidCompatible shouldBe false
                mirror.isValueCompatible shouldBe true
                spy.shouldBeOk {
                    lambda shouldHaveType Supplier::class[ts.INT.box()]
                }
            }
        }
    }

    parserTestContainer("Parenthesized expression lambda compatibility", javaVersions = listOf(JavaVersion.J1_8, JavaVersion.Latest)) {
        for (body in listOf("str(i)", "new Scratch(i)", "i = 1", "++i", "--i", "i++", "i--",
                            "str((i))", "(this).str(i)", "new Scratch((i))", "i = (i + 1)")) {
            doTest(body) {
                val acu = parser.parse(
                    """
                    import java.util.function.Supplier;
                    class Scratch {
                        int i;
                        Scratch(int i) { }
                        String str(int i) { return ""; }
                        void test() {
                            Supplier<Object> bare = () -> $body;
                            Supplier<Object> parenthesized = () -> ($body);
                            Supplier<Object> nested = () -> (($body));
                        }
                    }
                    """.trimIndent()
                )

                val infer = Infer(testTypeSystem, 8, TypeInferenceLogger.noop())
                val mirrors = JavaExprMirrors.forTypeResolution(infer)
                acu.descendants(ASTLambdaExpression::class.java).toList().forEachIndexed { index, lambda ->
                    val mirror = mirrors.getTopLevelFunctionalMirror(lambda) as ExprMirror.LambdaExprMirror
                    withClue(lambda) {
                        mirror.isVoidCompatible shouldBe (index == 0)
                        mirror.isValueCompatible shouldBe true
                    }
                }
            }
        }
    }

    parserTest("Parenthesized lambda body selects value compatible overload", javaVersions = listOf(JavaVersion.J1_8, JavaVersion.Latest)) {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            import java.util.function.Supplier;
            class Scratch {
                static void g(int i, Supplier<String> supplier) { }
                static void g(Object o, Runnable runnable) { }
                static String str() { return ""; }
                static void test(Integer i) {
                    g(i, () -> (str()));
                    g(i, () -> str());
                    g(i, () -> ((str())));
                }
            }
            """.trimIndent()
        )

        val (t_Scratch) = acu.declaredTypeSignatures()
        val calls = acu.descendants(ASTMethodCall::class.java).filter { it.methodName == "g" }.toList()

        spy.shouldBeOk {
            calls.forEachIndexed { index, call ->
                val formals = if (index == 1) {
                    listOf(ts.OBJECT, java.lang.Runnable::class.raw)
                } else {
                    listOf(ts.INT, Supplier::class[gen.t_String])
                }
                call.methodType.shouldMatchMethod(
                    named = "g",
                    declaredIn = t_Scratch,
                    withFormals = formals,
                    returning = ts.NO_TYPE
                )
                call.overloadSelectionInfo.isFailed shouldBe false
            }
        }
    }

    parserTest("Parenthesized implicitly typed lambda selects value compatible overload", javaVersions = listOf(JavaVersion.J1_8, JavaVersion.Latest)) {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            import java.util.function.Consumer;
            import java.util.function.Function;
            class Scratch {
                static void g(int i, Function<String, String> function) { }
                static void g(Object o, Consumer<String> consumer) { }
                static String str(String s) { return s; }
                static void test(Integer i) {
                    g(i, s -> (str(s)));
                    g(i, s -> str(s));
                    g(i, s -> ((str(s))));
                }
            }
            """.trimIndent()
        )

        val (t_Scratch) = acu.declaredTypeSignatures()
        val calls = acu.descendants(ASTMethodCall::class.java).filter { it.methodName == "g" }.toList()

        spy.shouldBeOk {
            calls.forEachIndexed { index, call ->
                val formals = if (index == 1) {
                    listOf(ts.OBJECT, java.util.function.Consumer::class[gen.t_String])
                } else {
                    listOf(ts.INT, java.util.function.Function::class[gen.t_String, gen.t_String])
                }
                call.methodType.shouldMatchMethod(
                    named = "g",
                    declaredIn = t_Scratch,
                    withFormals = formals,
                    returning = ts.NO_TYPE
                )
                call.overloadSelectionInfo.isFailed shouldBe false
            }
        }
    }

    parserTest("Test void compatible lambda with value compatible body") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class Foo {{
                 final Runnable pr = 0 == null ? null : () -> id(true);
            }}
            """.trimIndent()
        )

        val lambda = acu.descendants(ASTLambdaExpression::class.java).firstOrThrow()

        spy.shouldBeOk {
            lambda shouldHaveType java.lang.Runnable::class.raw
        }
    }

    parserTest("Test void compatible lambda with void body") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            import java.util.function.DoubleConsumer;
            class Foo {
                protected DoubleConsumer emptyConsumer() {
                    return e -> {};
                }
            }
            """.trimIndent()
        )

        val lambda = acu.descendants(ASTLambdaExpression::class.java).firstOrThrow()

        spy.shouldBeOk {
            lambda shouldHaveType DoubleConsumer::class.raw
        }
    }

    parserTest("Test early solved functional interface") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            import java.util.function.DoubleConsumer;
            import java.util.List;
            class Foo {
                public static <T> List<T> singletonList(T o) { return null; }
                static void ok(List<DoubleConsumer> cs) { }
                static {
                    // T must be instantiated to DoubleConsumer during the argument checks,
                    // otherwise the param `d` has no type and we can't process the body
                    ok(singletonList(d -> { }));
                }
            }
            """.trimIndent()
        )

        val lambda = acu.descendants(ASTLambdaExpression::class.java).firstOrThrow()

        spy.shouldBeOk {
            lambda shouldHaveType DoubleConsumer::class.raw
        }
    }

    parserTest("Test explicitly typed lambda with zero params, ground-target type inference") {
        // note that this is not actually implemented, we just special-case the case "zero parameters"
        // this is todo for future, doesn't appear too useful

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            import java.util.function.Supplier;
            class Foo<S> {

                public static <S> Foo<S> withInitial(Supplier<? extends S> supplier) { return null; }

                public static final Foo<String> FILENAME = Foo.withInitial(() -> "/*unknown*/");

            }
            """.trimIndent()
        )

        val lambda = acu.descendants(ASTLambdaExpression::class.java).firstOrThrow()

        spy.shouldBeOk {
            lambda shouldHaveType Supplier::class[gen.t_String]
        }
    }

    parserTest("Nested lambda param resolution (in to out)") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """

    interface Foreachable<T> {
    
        interface Action<T> {
    
            void consume(T t);
        }

        void foreach(Action<? super T> action);
    
        static <E> Foreachable<E> flatmapForeach(Foreachable<Foreachable<E>> f0) {
            return action -> f0.foreach(f1 -> f1.foreach(f2 -> action.consume(f2)));
        }
    }
    
            """
        )

        val (t_Foreachable, t_Action) = acu.declaredTypeSignatures()
        val (action, f1, f2) = acu.descendants(ASTLambdaParameter::class.java).crossFindBoundaries().toList()
        val evar = acu.typeVar("E")

        spy.shouldBeOk {
            f2 shouldHaveType evar
            f1 shouldHaveType t_Foreachable[evar]
            action shouldHaveType t_Action[`?` `super` evar]
        }
    }

    parserTest("Nested lambda param resolution, when there are several overloads to give ctx") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """

interface Runnable { void run(); }
interface Supplier<E> { E get(); }
interface Action<T> { void consume(T t); }
interface Foreachable<T> { void foreach(Action<? super T> action); }

class Scratch {

    static void bench(String label, Runnable runnable) {  }
    static <T> T bench(String label, Supplier<T> runnable) { return null; }

    static void foo(Foreachable<String> foreachable) {
        bench("label", () -> foreachable.foreach(s -> s.toString()));
    }
}
            """
        )

        val (t_Runnable, t_Supplier, t_Action, t_Foreachable) = acu.declaredTypeSignatures()
        val (run, action) = acu.descendants(ASTLambdaExpression::class.java).crossFindBoundaries().toList()
        val (s) = acu.descendants(ASTLambdaParameter::class.java).crossFindBoundaries().toList()

        spy.shouldBeOk {
            run shouldHaveType t_Runnable
            action shouldHaveType t_Action[gen.t_String]
            s shouldHaveType gen.t_String
        }
    }

    parserTest("Body expression should be ground") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """

interface Iterator<Q> {}
interface Function<U,V> { 
    V apply(U u);
}

class NodeStream<T> {

    public <R> NodeStream<R> map(Function<? super T, ? extends R> mapper) {
        return mapIter(iter -> mapNotNull(iter, mapper));
    }

    protected <P> NodeStream<P> mapIter(Function<Iterator<T>, Iterator<P>> fun) {return null;}

    public static <Q, V> Iterator<V> mapNotNull(Iterator<? extends Q> it, Function<? super Q, ? extends V> mapper) { return null; }

}
            """
        )

        val (t_Iterator, t_Function, t_NodeStream) = acu.declaredTypeSignatures()
        val (lambda) = acu.descendants(ASTLambdaExpression::class.java).crossFindBoundaries().toList()
        val (iter) = acu.descendants(ASTLambdaParameter::class.java).crossFindBoundaries().toList()
        val (_, _, _, tvar, rvar) = acu.typeVariables()

        spy.shouldBeOk {
            lambda shouldHaveType t_Function[t_Iterator[tvar], t_Iterator[rvar]]
            iter shouldHaveType t_Iterator[tvar]
            lambda.expressionBody!!.shouldBeA<ASTMethodCall> {
                it.methodType.shouldMatchMethod(
                    named = "mapNotNull",
                    withFormals = listOf(
                        t_Iterator[`?` extends tvar],
                        t_Function[`?` `super` tvar, `?` extends rvar]
                    )
                )
                it shouldHaveType t_Iterator[rvar]
            }
        }
    }

    parserTestContainer("Lambda bug with nested lambdas") {
        fun makeTest(insideOut: Boolean) {
            val (acu, spy) = parser.parseWithTypeInferenceSpy(
                """
                        interface Function<U,V> {
                            V apply(U u);
                        }

                        class Scratch {

                            <T> void chainingWithLambda(Function<?, ? extends T> f) {
                                this.<Function<Scratch, String>>chainingWithLambda(x -> y -> y.contains(0));
                            }
                        }
                """.trimIndent()
            )

            val (t_Function, t_Scratch) = acu.declaredTypeSignatures()
            val (lambdaX, lambdaY) = acu.descendants(ASTLambdaExpression::class.java).crossFindBoundaries()
                .toList()

            spy.shouldBeOk {
                val t_lambdaY = t_Function[t_Scratch, ts.STRING]
                val t_lambdaX = t_Function[ts.OBJECT, t_lambdaY]

                if (insideOut) {
                    lambdaY shouldHaveType t_lambdaY
                    lambdaX shouldHaveType t_lambdaX
                } else {
                    lambdaX shouldHaveType t_lambdaX
                    lambdaY shouldHaveType t_lambdaY
                }
            }
        }

        doTest("Outside in") {
            makeTest(false)
        }

        doTest("Inside out") {
            makeTest(true)
        }
    }

    parserTest("inference of call within lambda fails") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """

interface Iterator<Q> {}
interface Function<U,V> { 
    V apply(U u);
}
interface Iterable<Q> {
    Iterator<Q> iterator();
}

class NodeStream {
    
    public static <T, R> Iterable<R> mapIterator(Iterable<? extends T> iter, Function<? super Iterator<? extends T>, ? extends Iterator<R>> mapper) {
        return () -> mapper.apply(iter.iterator());
    }

}
            """
        )

        val (t_Iterator, t_Function, t_Iterable) = acu.declaredTypeSignatures()
        val (lambda) = acu.descendants(ASTLambdaExpression::class.java).crossFindBoundaries().toList()
        val (_, _, _, _, tvar, rvar) = acu.typeVariables()

        spy.shouldBeOk {
            lambda shouldHaveType t_Iterable[rvar]
            lambda.expressionBody!!.shouldBeA<ASTMethodCall> {
                it.methodType.shouldMatchMethod(
                    named = "apply",
                    withFormals = listOf(captureMatcher(`?` `super` t_Iterator[`?` extends tvar]))
                )
                it.overloadSelectionInfo::isFailed shouldBe false
            }
        }
    }
})
