/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.*
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.types.*
import net.sourceforge.pmd.lang.java.types.internal.infer.ast.JavaExprMirrors
import net.sourceforge.pmd.lang.java.types.testdata.TypeInferenceTestCases
import java.util.function.DoubleConsumer
import java.util.function.Supplier
import kotlin.test.assertEquals

@Suppress("UNUSED_VARIABLE")
class LambdaInferenceTest : ProcessorTestSpec({


    parserTest("Test dangling method parameter - ok") {

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


        val node = ExpressionParsingCtx.parseNode(chain, this)

        node.shouldMatchN {
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

    parserTest("Test dangling method parameter recovery") {

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


        val node = ExpressionParsingCtx.parseNode(chain, this)

        node.shouldMatchN {
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

    parserTest("Test functional interface induced by intersection") {

        val acu = parser.parse("""
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
        """)

        val (t_Scratch) = acu.descendants(ASTClassOrInterfaceDeclaration::class.java).toList { it.typeMirror }
        val (f) = acu.descendants(ASTMethodDeclaration::class.java).toList()
        val (fCall) = acu.descendants(ASTMethodCall::class.java).toList()

        fCall.shouldMatchN {
            methodCall("f") {
                it.methodType.symbol shouldBe f.symbol

                with(it.typeDsl) {
                    // Function<String, Integer> & java.io.Serializable
                    val serialFun = gen.t_Function[gen.t_String, int.box()] * ts.SERIALIZABLE

                    it.methodType.shouldMatchMethod(named = "f", declaredIn = t_Scratch, withFormals = listOf(serialFun), returning = serialFun)
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
        val acu = parser.parse("""
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
        """)

        val (t_Scratch) = acu.descendants(ASTClassOrInterfaceDeclaration::class.java).toList { it.typeMirror }
        val (f) = acu.descendants(ASTMethodDeclaration::class.java).toList()
        val (fCall) = acu.descendants(ASTMethodCall::class.java).toList()

        fCall.shouldMatchN {
            methodCall("f") {
                it.methodType.symbol shouldBe f.symbol

                with(it.typeDsl) {
                    // Function<String, Integer> & java.io.Serializable
                    val serialFun = gen.t_Function[gen.t_String, int.box()] * ts.SERIALIZABLE

                    it.methodType.shouldMatchMethod(named = "f", declaredIn = t_Scratch, withFormals = listOf(serialFun), returning = serialFun)
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
        val acu = parser.parse("""
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
        """)

        val (t_Scratch, t_WithField) = acu.descendants(ASTClassOrInterfaceDeclaration::class.java).toList { it.typeMirror }
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
        val acu = parser.parse("""
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
        """)

        val (t_Scratch, t_WithField) = acu.descendants(ASTClassOrInterfaceDeclaration::class.java).toList { it.typeMirror }
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

        val acu = parser.parse("""
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

        """.trimIndent())

        val (_, _, t_G) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }

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

        val acu = parser.parse("""
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

        """.trimIndent())

        val (_, _, t_G) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }

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


    parserTest("Value compatibility unit tests") {

        val acu = parser.parse("""
class Scratch {

    static {
        Object a = g -> { return g.fetch(); };  // val
        Object b = g -> { return; };            // void
        Object c = g -> { };                    // void
        Object d = g -> g + 2;                  // val
        Object e = g -> o(g + 2);               // val + void
        Object f = g -> new O(g + 2);           // val + void
        Object h = g -> { throw new E(); };     // val + void 
    }
}

        """.trimIndent())

        val infer = Infer(testTypeSystem, 8, TypeInferenceLogger.noop())
        val mirrors = JavaExprMirrors.forTypeResolution(infer)
        val (a, b, c, d, e, f, h) = acu.descendants(ASTLambdaExpression::class.java).toList { mirrors.getTopLevelFunctionalMirror(it) as ExprMirror.LambdaExprMirror }

        fun ExprMirror.LambdaExprMirror.shouldBeCompat(void: Boolean = false, value: Boolean = false) {
            withClue(this) {
                assertEquals(void, this.isVoidCompatible, "void compatible")
                assertEquals(value, this.isValueCompatible, "value compatible")
            }
        }


        a.shouldBeCompat(value = true)
        b.shouldBeCompat(void = true)
        c.shouldBeCompat(void = true)
        d.shouldBeCompat(value = true)
        e.shouldBeCompat(value = true, void = true)
        f.shouldBeCompat(value = true, void = true)
        h.shouldBeCompat(value = true, void = true)

    }



    parserTest("Test void compatible lambda with value compatible body") {


        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
            class Foo {{
                 final Runnable pr = 0 == null ? null : () -> id(true);
            }}
        """.trimIndent())

        val lambda = acu.descendants(ASTLambdaExpression::class.java).firstOrThrow()

        spy.shouldBeOk {
            lambda shouldHaveType java.lang.Runnable::class.raw
        }
    }

    parserTest("Test void compatible lambda with void body") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
            import java.util.function.DoubleConsumer;
            class Foo {
                protected DoubleConsumer emptyConsumer() {
                    return e -> {};
                }
            }
        """.trimIndent())

        val lambda = acu.descendants(ASTLambdaExpression::class.java).firstOrThrow()

        spy.shouldBeOk {
            lambda shouldHaveType DoubleConsumer::class.raw
        }
    }

    parserTest("Test early solved functional interface") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
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
        """.trimIndent())

        val lambda = acu.descendants(ASTLambdaExpression::class.java).firstOrThrow()

        spy.shouldBeOk {
            lambda shouldHaveType DoubleConsumer::class.raw
        }
    }

    parserTest("Test explicitly typed lambda with zero params, ground-target type inference") {
        // note that this is not actually implemented, we just special-case the case "zero parameters"
        // this is todo for future, doesn't appear too useful

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
            import java.util.function.Supplier;
            class Foo<S> {

                public static <S> Foo<S> withInitial(Supplier<? extends S> supplier) { return null; }

                public static final Foo<String> FILENAME = Foo.withInitial(() -> "/*unknown*/");

            }
        """.trimIndent())

        val lambda = acu.descendants(ASTLambdaExpression::class.java).firstOrThrow()

        spy.shouldBeOk {
            lambda shouldHaveType Supplier::class[gen.t_String]
        }
    }

    parserTest("Nested lambda param resolution (in to out)") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""

    interface Foreachable<T> {
    
        interface Action<T> {
    
            void consume(T t);
        }

        void foreach(Action<? super T> action);
    
        static <E> Foreachable<E> flatmapForeach(Foreachable<Foreachable<E>> f0) {
            return action -> f0.foreach(f1 -> f1.foreach(f2 -> action.consume(f2)));
        }
    }
    
            """)

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

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""

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
        

    
            """)

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

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""

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
            """)

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
                        withFormals = listOf(t_Iterator[`?` extends tvar], t_Function[`?` `super` tvar, `?` extends rvar])
                )
                it shouldHaveType t_Iterator[rvar]
            }
        }
    }

    parserTest("Lambda bug with nested lambdas") {

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

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""

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
            """)

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
