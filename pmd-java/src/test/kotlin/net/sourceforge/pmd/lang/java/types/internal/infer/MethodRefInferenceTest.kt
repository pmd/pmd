/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldMatchN
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.types.*
import net.sourceforge.pmd.lang.java.types.testdata.TypeInferenceTestCases
import java.util.function.*
import java.util.stream.Collector

/**
 *
 */
class MethodRefInferenceTest : ProcessorTestSpec({


    parserTest("Test inexact method ref of generic type") {

        val acu = parser.parse("""
            import java.util.Optional;
            import java.util.List;
            import java.util.stream.Stream;
            import java.util.Objects;

            class Archive {

                private static void loopChecks2(List<Archive> step, List<Archive> pred, List<Archive> fini) {
                    Stream.of(step, pred, fini).flatMap(List::stream).filter(Objects::nonNull).anyMatch(it -> true);
                }

            }
        """.trimIndent())


        val t_Archive = acu.descendants(ASTClassOrInterfaceDeclaration::class.java).firstOrThrow().typeMirror
        val anyMatch = acu.descendants(ASTMethodCall::class.java).first()!!

        anyMatch.shouldMatchN {
            methodCall("anyMatch") {
                it::getTypeMirror shouldBe it.typeSystem.BOOLEAN

                methodCall("filter") {

                    it::getTypeMirror shouldBe with (it.typeDsl) { gen.t_Stream[t_Archive] }

                    methodCall("flatMap") {

                        it::getTypeMirror shouldBe with (it.typeDsl) { gen.t_Stream[t_Archive] }

                        methodCall("of") {
                            it::getQualifier shouldBe unspecifiedChild()

                            it::getTypeMirror shouldBe with (it.typeDsl) { gen.t_Stream[gen.t_List[t_Archive]] }

                            argList(3)
                        }

                        argList(1)
                    }

                    argList(1)
                }

                argList(1)
            }
        }
    }



    parserTest("Test call chain with method reference") {

        otherImports += "java.util.stream.*"

        val chain = "Stream.of(\"\").map(String::isEmpty).collect(Collectors.toList())"

        inContext(ExpressionParsingCtx) {


            chain should parseAs {
                methodCall("collect") {
                    it.typeMirror shouldBe with(it.typeDsl) { gen.t_List[boolean.box()] }
                    it::getQualifier shouldBe child<ASTMethodCall> {
                        it::getMethodName shouldBe "map"
                        it.typeMirror shouldBe with(it.typeDsl) { gen.t_Stream[boolean.box()] }
                        it::getQualifier shouldBe child<ASTMethodCall> {
                            it::getMethodName shouldBe "of"
                            it.typeMirror shouldBe with(it.typeDsl) { gen.t_Stream[gen.t_String] }
                            it::getQualifier shouldBe typeExpr {
                                classType("Stream")
                            }

                            it::getArguments shouldBe child {
                                stringLit("\"\"")
                            }
                        }

                        it::getArguments shouldBe child {

                            methodRef("isEmpty") {

                                with(it.typeDsl) {
                                    val `t_Function{String, Boolean}` = gen.t_Function[gen.t_String, boolean.box()]

                                    it.typeMirror shouldBe `t_Function{String, Boolean}`
                                    it.referencedMethod.shouldMatchMethod(named = "isEmpty", declaredIn = gen.t_String, withFormals = emptyList(), returning = boolean)
                                    // Function<String, Boolean>.apply(String) -> Boolean
                                    it.functionalMethod.shouldMatchMethod(named = "apply", declaredIn = `t_Function{String, Boolean}`, withFormals = listOf(gen.t_String), returning = boolean.box())
                                }

                                typeExpr {
                                    classType("String")
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

    parserTest("Test call chain with constructor reference") {

        otherImports += "java.util.stream.*"

        val chain = "Stream.of(1, 2).map(int[]::new).collect(Collectors.toList())"

        inContext(ExpressionParsingCtx) {

            chain should parseAs {
                methodCall("collect") {

                    it.typeMirror shouldBe with (it.typeDsl) { gen.t_List[int.toArray() ]} // List<int[]>

                    it::getQualifier shouldBe methodCall("map") {
                        it.typeMirror shouldBe with (it.typeDsl) { gen.t_Stream[int.toArray() ]} // Stream<int[]>

                        it::getQualifier shouldBe methodCall("of") {
                            it.typeMirror shouldBe with (it.typeDsl) { gen.t_Stream[int.box()]} // Stream<Integer>

                            it::getQualifier shouldBe typeExpr {
                                classType("Stream")
                            }

                            it::getArguments shouldBe child {
                                int(1)
                                int(2)
                            }
                        }

                        it::getArguments shouldBe child {

                            // Function<Integer, int[]>
                            val `t_Function{Integer, Array{int}}` = with(it.typeDsl) { gen.t_Function[int.box(), int.toArray()] }

                            constructorRef {
                                it.typeMirror shouldBe `t_Function{Integer, Array{int}}`
                                with(it.typeDsl) {
                                    it.referencedMethod.shouldMatchMethod(named = "new", declaredIn = int.toArray(), /* int[]*/ withFormals = listOf(int), returning = int.toArray())
                                    it.functionalMethod.shouldMatchMethod(named = "apply", declaredIn = `t_Function{Integer, Array{int}}`, withFormals = listOf(int.box()), returning = int.toArray())
                                }

                                typeExpr {
                                    arrayType {
                                        primitiveType(JPrimitiveType.PrimitiveTypeKind.INT)
                                        arrayDimList()
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


    parserTest("Test call chain with array method reference") {

        otherImports += "java.util.stream.*"

        val chain = "Stream.<int[]>of(new int[0]).map(int[]::clone)"


        inContext(ExpressionParsingCtx) {
            chain should parseAs {
                methodCall("map") {

                    it.typeMirror shouldBe with(it.typeDsl) { gen.t_Stream[int.toArray()] }

                    it::getQualifier shouldBe methodCall("of") {
                        it.typeMirror shouldBe with(it.typeDsl) { gen.t_Stream[int.toArray()] }

                        it::getQualifier shouldBe typeExpr {
                            classType("Stream")
                        }

                        it::getExplicitTypeArguments shouldBe child {
                            arrayType()
                        }

                        it::getArguments shouldBe child {
                            unspecifiedChild()
                        }
                    }

                    it::getArguments shouldBe argList {

                        methodRef("clone") {

                            with(it.typeDsl) {
                                // Function<int[], int[]>
                                val `t_Function{Array{int}, Array{int}}` = gen.t_Function[int.toArray(), int.toArray()]

                                it.typeMirror shouldBe `t_Function{Array{int}, Array{int}}`
                                it.referencedMethod.shouldMatchMethod(named = "clone", declaredIn = int.toArray(), withFormals = emptyList(), returning = int.toArray())
                                it.functionalMethod.shouldMatchMethod(named = "apply", declaredIn = `t_Function{Array{int}, Array{int}}`, withFormals = listOf(int.toArray()), returning = int.toArray())
                            }

                            typeExpr {
                                arrayType {
                                    primitiveType(JPrimitiveType.PrimitiveTypeKind.INT)
                                    arrayDimList()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    parserTest("Test method reference overload resolution") {

        otherImports += "java.util.stream.*"

        val stringBuilder = "java.lang.StringBuilder"

        val chain = "Stream.of(\"\", 4).reduce(new StringBuilder(), StringBuilder::append, StringBuilder::append)"

        inContext(ExpressionParsingCtx) {


            chain should parseAs {
                methodCall("reduce") {


                    // we can't hardcode the lub because it is jdk specific
                    val serialLub = with (it.typeDsl) {
                        ts.lub(gen.t_String, gen.t_Integer)
                    }

                    val t_BiFunction = with (it.typeDsl) { ts.getClassSymbol(BiFunction::class.java)!! }
                    val t_BinaryOperator = with (it.typeDsl) { ts.getClassSymbol(BinaryOperator::class.java)!! }
                    val t_Sb = with (it.typeDsl) { gen.t_StringBuilder }

                    with (it.typeDsl) {

                        it.typeMirror shouldBe t_Sb
                        it.methodType.shouldMatchMethod(
                                named = "reduce",
                                declaredIn = gen.t_Stream[serialLub],
                                withFormals = listOf(
                                        t_Sb,
                                        t_BiFunction[t_Sb, `?` `super` serialLub, t_Sb],
                                        t_BinaryOperator[t_Sb]
                                ),
                                returning = t_Sb
                        )
                    }

                    it::getQualifier shouldBe child<ASTMethodCall> {
                        it::getMethodName shouldBe "of"
                        it.typeMirror shouldBe with(it.typeDsl) { gen.t_Stream[serialLub] }

                        it::getQualifier shouldBe typeExpr {
                            classType("Stream")
                        }

                        it::getArguments shouldBe child {
                            unspecifiedChildren(2)
                        }
                    }

                    it::getArguments shouldBe child {
                        child<ASTConstructorCall>(ignoreChildren = true) {
                        }

                        methodRef("append") {
                            with (it.typeDsl) {
                                val myBifunction = t_BiFunction[t_Sb, serialLub, t_Sb]

                                it.typeMirror shouldBe myBifunction
                                it.referencedMethod.shouldMatchMethod(named = "append", declaredIn = t_Sb, withFormals = listOf(ts.OBJECT), returning = t_Sb)
                                it.functionalMethod.shouldMatchMethod(named = "apply", declaredIn = myBifunction, withFormals = listOf(t_Sb, serialLub), returning = t_Sb)
                            }

                            typeExpr {
                                classType("StringBuilder")
                            }
                        }

                        methodRef("append") {

                            with (it.typeDsl) {
                                val myBifunction = t_BiFunction[t_Sb, t_Sb, t_Sb]

                                it.typeMirror shouldBe t_BinaryOperator[t_Sb]
                                // notice it's more specific than the first append (CharSequence formal)
                                it.referencedMethod.shouldMatchMethod(named = "append", declaredIn = t_Sb, withFormals = listOf(gen.t_CharSequence), returning = t_Sb)
                                // notice the owner of the function is BiFunction and not BinaryOperator. It's inherited by BinaryOperator
                                it.functionalMethod.shouldMatchMethod(named = "apply", declaredIn = myBifunction, withFormals = listOf(t_Sb, t_Sb), returning = t_Sb)
                            }

                            typeExpr {
                                classType("StringBuilder")
                            }
                        }
                    }
                }
            }
        }
    }



    parserTest("Test method ref with this as LHS") {


        val acu = parser.parse("""
            
            package scratch;

            import static java.util.stream.Collectors.joining;

            import java.util.Comparator;
            import java.util.Deque;

            class Archive {
                
                private String getName() {
                    return "foo";
                }

                private String toInversePath(Deque<Archive> path) {
                    return path.stream()
                               .map(Archive::getName)
                               .collect(joining(" <- "));
                }

                private Comparator<Deque<Archive>> comparator() {
                    return Comparator.<Deque<Archive>, String>
                        comparing(deque -> deque.peekFirst().getName())
                        .thenComparingInt(Deque::size)
                        .thenComparing(this::toInversePath);
                }

            }
        """.trimIndent())

        val thisToInversePath = acu.descendants(ASTMethodReference::class.java)[2]!!

        thisToInversePath.shouldMatchN {
            methodRef("toInversePath") {
                it.functionalMethod.toString() shouldBe "java.util.function.Function<java.util.Deque<scratch.Archive>, java.lang.String>.apply(java.util.Deque<scratch.Archive>) -> java.lang.String"
                thisExpr()
            }
        }
    }

    parserTest("Test method ref with void return type") {


        val acu = parser.parse("""
            import java.util.Optional;
            class Archive {

                private String getName() {
                    Optional.of(this).ifPresent(Archive::getName);
                    return "foo";
                }
            }
        """.trimIndent())


        val t_Archive = acu.descendants(ASTClassOrInterfaceDeclaration::class.java).firstOrThrow().typeMirror
        val getName = acu.descendants(ASTMethodDeclaration::class.java).first()!!
        val thisToInversePath = acu.descendants(ASTMethodCall::class.java).first()!!

        thisToInversePath.shouldMatchN {
            methodCall("ifPresent") {
                unspecifiedChild()
                argList {
                    methodRef("getName") {
                        with (it.typeDsl) {
                            it.functionalMethod.shouldMatchMethod(
                                    named = "accept",
                                    declaredIn = Consumer::class[t_Archive],
                                    withFormals = listOf(t_Archive),
                                    returning = ts.NO_TYPE
                            )

                            it.referencedMethod.symbol shouldBe getName.symbol
                        }

                        typeExpr {
                            classType("Archive")
                        }
                    }
                }
            }
        }
    }


    // disabled for now
    parserTest("!Test inference var inst substitution in enclosing ctx") {

        logTypeInference(true)

        val acu = parser.parse("""
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Function;

abstract class NodeStream<T> implements Iterable<T> {

    public <R> NodeStream<R> flatMap(Function<? super T, ? extends NodeStream<? extends R>> mapper) {
        Function<? super T, Iterator<? extends R>> mapped = mapper.andThen(NodeStream::safeMap);
        return mapIter(iter -> doFlatMap(iter, mapped));
    }

    private static <K> Iterator<? extends K> safeMap(NodeStream<? extends K> ns) {
        return ns == null ? Collections.emptyIterator() : ns.iterator();
    }

    abstract <I, O> Iterator<O> doFlatMap(Iterator<? extends I> iter, Function<? super I, ? extends Iterator<? extends O>> f);

    protected abstract <Q> NodeStream<Q> mapIter(Function<Iterator<T>, Iterator<Q>> fun);
}

        """.trimIndent())

        val (t_NodeStream) = acu.descendants(ASTClassOrInterfaceDeclaration::class.java).toList { it.typeMirror }
        val (tvar, rvar, kvar) = acu.descendants(ASTTypeParameter::class.java).toList { it.typeMirror }

        acu.descendants(ASTMethodCall::class.java)
                .firstOrThrow()
                .shouldMatchN {
                    methodCall("andThen") {
                        val captureOfT: JTypeVar
                        with(it.typeDsl) {
                            captureOfT = captureMatcher(`?` `super` tvar)
                            it.typeMirror shouldBe gen.t_Function[captureOfT, gen.t_Iterator[`?` extends rvar]]
                        }

                        it::getQualifier shouldBe unspecifiedChild()

                        argList {
                            methodRef("safeMap") {
                                with(it.typeDsl) {
//                                    val captureOfR: JTypeVar
//                                    captureOfR = captureMatcher(`?` extends rvar)

                                    // safeMap#K must have been instantiated to some variation of R
                                    it.referencedMethod.shouldMatchMethod(
                                            named = "safeMap",
                                            declaredIn = t_NodeStream.erasure,
                                            withFormals = listOf(t_NodeStream[`?` extends rvar]),
                                            returning = gen.t_Iterator[`?` extends rvar]
                                    )
                                }

                                it::getQualifier shouldBe unspecifiedChild()

//                                it.typeMirror shouldBe with(it.typeDsl) { gen.t_String }
                                argList {
                                    variableAccess("t")
                                }
                            }
                        }
                    }
                }

    }

    parserTest("Fix method ref non-wildcard parameterization not being ground in listener") {

        val acu = parser.parse("""
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

class Scratch {


    public List<String> main(String[] args) {
        return Stream.of(args)
                     .distinct()
                     .collect(collectingAndThen(toList(), Collections::unmodifiableList));
    }
}
        """.trimIndent())

        val (t_NodeStream) = acu.descendants(ASTClassOrInterfaceDeclaration::class.java).toList { it.typeMirror }
        val collectCall = acu.descendants(ASTMethodCall::class.java).first()!!

        collectCall.shouldMatchN {
            methodCall("collect") {
                with(it.typeDsl) {
                    it.typeMirror shouldBe gen.`t_List{String}`
                }

                methodCall("distinct") {
                    with(it.typeDsl) {
                        it.typeMirror shouldBe gen.t_Stream[gen.t_String]
                    }

                    unspecifiedChildren(2)
                }

                argList {
                    methodCall("collectingAndThen") {
                        with(it.typeDsl) {
                            it.typeMirror shouldBe Collector::class[gen.t_String, ts.OBJECT, gen.`t_List{String}`]
                        }

                        argList {
                            methodCall("toList")
                            methodRef("unmodifiableList") {
                                unspecifiedChild()
                                with(it.typeDsl) {
                                    it.typeMirror shouldBe gen.t_Function[gen.`t_List{String}`, gen.`t_List{String}`]
                                    it.functionalMethod shouldBe it.typeMirror.streamMethods {  it.simpleName == "apply" }.findFirst().get()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    parserTest("Method refs targeting a void function in unambiguous context must still be assigned a type") {

        val acu = parser.parse("""
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

class Scratch {

    private static IntConsumer adapt(Sink<Integer> sink) {
        LongConsumer l = ((LongConsumer) sink::accept);
        return sink::accept;
    }

    interface Sink<T> {

        default void accept(int value) {}

        default void accept(long value) { }

        default void accept(double value) {}

    }

}
        """.trimIndent())

        val (_, t_Sink) = acu.descendants(ASTClassOrInterfaceDeclaration::class.java).toList { it.typeMirror }
        val (_, acceptInt, acceptLong) = acu.descendants(ASTMethodDeclaration::class.java).toList()
        val (castRef, returnRef) = acu.descendants(ASTMethodReference::class.java).toList()

        doTest("In cast context") {

            castRef.shouldMatchN {
                methodRef("accept") {
                    unspecifiedChild()
                    with(it.typeDsl) {
                        it.typeMirror shouldBe LongConsumer::class.decl
                        it.functionalMethod shouldBe it.typeMirror.streamMethods { it.simpleName == "accept" }.findFirst().get()
                        it.referencedMethod.shouldMatchMethod(
                                named = "accept",
                                declaredIn = t_Sink[int.box()],
                                withFormals = listOf(long),
                                returning = void
                        ).also {
                            it.symbol.tryGetNode() shouldBe acceptLong
                        }
                    }
                }
            }
        }

        doTest("In return context") {

            returnRef.shouldMatchN {
                methodRef("accept") {
                    unspecifiedChild()
                    with(it.typeDsl) {
                        it.typeMirror shouldBe IntConsumer::class.decl
                        it.functionalMethod shouldBe it.typeMirror.streamMethods { it.simpleName == "accept" }.findFirst().get()
                        it.referencedMethod.shouldMatchMethod(
                                named = "accept",
                                declaredIn = t_Sink[int.box()],
                                withFormals = listOf(int),
                                returning = void
                        ).also {
                            it.symbol.tryGetNode() shouldBe acceptInt
                        }
                    }
                }
            }
        }
    }

    parserTest("Method refs disambiguation between static methods") {

        val acu = parser.parse("""
import java.util.function.IntConsumer;

class Scratch {
    
    static void foo(int i) {}        // this one
    static void foo() {}

    static void bar(int i) {}        // this one
    static void bar(int a, int b) {}
    
    static void baz(int i) {}        // this one
    void baz(int a, int b) {}
    

    static  {
        IntConsumer ic = Scratch::foo;
        ic = Scratch::bar;
        ic = Scratch::baz;
    }
}
        """.trimIndent())

        val (fooRef, barRef, bazRef) = acu.descendants(ASTMethodReference::class.java).toList()

        val t_IntConsumer = with (acu.typeDsl) { IntConsumer::class.decl }

        fooRef.shouldMatchN {
            methodRef("foo") {
                it.typeMirror shouldBe t_IntConsumer
                it.referencedMethod.arity shouldBe 1

                unspecifiedChild()
            }
        }

        barRef.shouldMatchN {
            methodRef("bar") {
                it.typeMirror shouldBe t_IntConsumer
                it.referencedMethod.arity shouldBe 1

                unspecifiedChild()
            }
        }

        bazRef.shouldMatchN {
            methodRef("baz") {
                it.typeMirror shouldBe t_IntConsumer
                it.referencedMethod.arity shouldBe 1

                unspecifiedChild()
            }
        }
    }


    parserTest("!Test inexact method ref conflict between static and non-static") {

        val acu = parser.parse("""
            import java.util.stream.*;
            class Archive {

                private String getName(int[] certIds) {
                    return IntStream.of(certIds)
                            // both static Integer::toString(int) and non-static Integer::toString() are applicable
                            .mapToObj(Integer::toString)
                            .collect(Collectors.joining(", "));
                }
            }
        """.trimIndent())

        val collectCall = acu.descendants(ASTMethodCall::class.java).first()!!

        collectCall.shouldMatchN {
            methodCall("collect") {
                with (it.typeDsl) {
                    it.typeMirror shouldBe gen.t_String
                }

                methodCall("mapToObj") {
                    with (it.typeDsl) {
                        it.typeMirror shouldBe gen.t_Stream[gen.t_String]
                    }

                    unspecifiedChildren(2)
                }

                argList(1)
            }
        }
    }

    // TODO
    parserTest("Test inexact method ref conflict between static and non-static") {

        val acu = parser.parse("""
            import java.util.stream.*;
            class Archive {

                private String getName(int[] certIds) {
                    return IntStream.of(certIds)
                            // both static Integer::toString(int) and non-static Integer::toString() are applicable
                            // the determining factor is that Integer::toString() requires boxing
                            .mapToObj(Integer::toString)
                            .collect(Collectors.joining(", "));
                }
            }
        """.trimIndent())

        val collectCall = acu.descendants(ASTMethodCall::class.java).first()!!

        collectCall.shouldMatchN {
            methodCall("collect") {
                with (it.typeDsl) {
                    it.typeMirror shouldBe gen.t_String
                }

                methodCall("mapToObj") {
                    with (it.typeDsl) {
                        it.typeMirror shouldBe gen.t_Stream[gen.t_String]
                    }

                    unspecifiedChildren(2)
                }

                argList(1)
            }
        }
    }


    parserTest("Missing compile-time decl") {

        val acu = parser.parse("""
import java.util.function.IntConsumer;

class Scratch {

    static  {
        IntConsumer ic = Scratch::foo;
    }
}
        """.trimIndent())

        val (fooRef) = acu.descendants(ASTMethodReference::class.java).toList()

        fooRef.shouldMatchN {
            methodRef("foo") {
                it.typeMirror shouldBe it.typeSystem.UNRESOLVED_TYPE
                it.referencedMethod shouldBe it.typeSystem.UNRESOLVED_METHOD

                unspecifiedChild()
            }
        }
    }

})
