/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.component6
import net.sourceforge.pmd.lang.ast.test.component7
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldMatchN
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.types.*
import java.util.*
import java.util.function.*
import java.util.stream.Collector
import java.util.function.Function as JavaFunction

@Suppress("UNUSED_VARIABLE")
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
                it shouldHaveType it.typeSystem.BOOLEAN

                methodCall("filter") {

                    it shouldHaveType with(it.typeDsl) { gen.t_Stream[t_Archive] }

                    methodCall("flatMap") {

                        it shouldHaveType with(it.typeDsl) { gen.t_Stream[t_Archive] }

                        methodCall("of") {
                            skipQualifier()

                            it shouldHaveType with(it.typeDsl) { gen.t_Stream[gen.t_List[t_Archive]] }

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
                    it shouldHaveType with(it.typeDsl) { gen.t_List[boolean.box()] }
                    it::getQualifier shouldBe child<ASTMethodCall> {
                        it::getMethodName shouldBe "map"
                        it shouldHaveType with(it.typeDsl) { gen.t_Stream[boolean.box()] }
                        it::getQualifier shouldBe child<ASTMethodCall> {
                            it::getMethodName shouldBe "of"
                            it shouldHaveType with(it.typeDsl) { gen.t_Stream[gen.t_String] }
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

                                    it shouldHaveType `t_Function{String, Boolean}`
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

                    it shouldHaveType with(it.typeDsl) { gen.t_List[int.toArray()] } // List<int[]>

                    it::getQualifier shouldBe methodCall("map") {
                        it shouldHaveType with(it.typeDsl) { gen.t_Stream[int.toArray()] } // Stream<int[]>

                        it::getQualifier shouldBe methodCall("of") {
                            it shouldHaveType with(it.typeDsl) { gen.t_Stream[int.box()] } // Stream<Integer>

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
                                it shouldHaveType `t_Function{Integer, Array{int}}`
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

                    it shouldHaveType with(it.typeDsl) { gen.t_Stream[int.toArray()] }

                    it::getQualifier shouldBe methodCall("of") {
                        it shouldHaveType with(it.typeDsl) { gen.t_Stream[int.toArray()] }

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

                                it shouldHaveType `t_Function{Array{int}, Array{int}}`
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

        val chain = "Stream.of(\"\", 4).reduce(new StringBuilder(), StringBuilder::append, StringBuilder::append)"

        inContext(ExpressionParsingCtx) {


            chain should parseAs {
                methodCall("reduce") {


                    // we can't hardcode the lub because it is jdk specific
                    val serialLub = with(it.typeDsl) {
                        ts.lub(gen.t_String, gen.t_Integer)
                    }

                    val t_BiFunction = with(it.typeDsl) { ts.getClassSymbol(BiFunction::class.java)!! }
                    val t_BinaryOperator = with(it.typeDsl) { ts.getClassSymbol(BinaryOperator::class.java)!! }
                    val t_Sb = with(it.typeDsl) { gen.t_StringBuilder }

                    with(it.typeDsl) {

                        it shouldHaveType t_Sb
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
                        it shouldHaveType with(it.typeDsl) { gen.t_Stream[serialLub] }

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
                            with(it.typeDsl) {
                                val myBifunction = t_BiFunction[t_Sb, serialLub, t_Sb]

                                it shouldHaveType myBifunction
                                it.referencedMethod.shouldMatchMethod(named = "append", declaredIn = t_Sb, withFormals = listOf(ts.OBJECT), returning = t_Sb)
                                it.functionalMethod.shouldMatchMethod(named = "apply", declaredIn = myBifunction, withFormals = listOf(t_Sb, serialLub), returning = t_Sb)
                            }

                            typeExpr {
                                classType("StringBuilder")
                            }
                        }

                        methodRef("append") {

                            with(it.typeDsl) {
                                val myBifunction = t_BiFunction[t_Sb, t_Sb, t_Sb]

                                it shouldHaveType t_BinaryOperator[t_Sb]
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



    parserTest("Test failing method ref with this as LHS") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""

            package scratch;

            import java.util.Comparator;

            class Archive {

                private String getName() { return "foo"; }

                private Comparator<Archive> comparator() {
                    return Comparator.comparing(this::getName); // this should fail
                }

            }
        """.trimIndent())

        val t_Archive = acu.firstTypeSignature()
        val mref = acu.descendants(ASTMethodReference::class.java).firstOrThrow()
        val call = acu.firstMethodCall()

        spy.shouldHaveMissingCtDecl(call)

        acu.withTypeDsl {
            mref.referencedMethod shouldBe ts.UNRESOLVED_METHOD
            mref shouldHaveType ts.UNKNOWN
            call.methodType shouldBe ts.UNRESOLVED_METHOD
            call.overloadSelectionInfo.apply {
                isFailed shouldBe true
            }
        }
    }

    parserTest("Test method ref with void return type") {


        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
            import java.util.Optional;
            class Archive {

                private String getName() {
                    Optional.of(this).ifPresent(Archive::getName);
                    return "foo";
                }
            }
        """.trimIndent())


        val t_Archive = acu.firstTypeSignature()
        val getName = acu.methodDeclarations().firstOrThrow()
        val ifPresentCall = acu.firstMethodCall()

        spy.shouldBeOk {
            ifPresentCall.shouldMatchN {
                methodCall("ifPresent") {
                    unspecifiedChild()
                    argList {
                        methodRef("getName") {
                            it.functionalMethod.shouldMatchMethod(
                                    named = "accept",
                                    declaredIn = Consumer::class[t_Archive],
                                    withFormals = listOf(t_Archive),
                                    returning = ts.NO_TYPE
                            )

                            it.referencedMethod.symbol shouldBe getName.symbol

                            typeExpr {
                                classType("Archive")
                            }
                        }
                    }
                }
            }
        }
    }


    // disabled for now
    parserTest("Test inference var inst substitution in enclosing ctx") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Function;

abstract class NodeStream<T> implements Iterable<T> {

    public <R> NodeStream<R> flatMap(Function<? super T, ? extends NodeStream<? extends R>> mapper) {
        Function<? super T, Iterator<? extends R>> mapped = mapper.andThen(NodeStream::safeMap);
        return mapIter(iter -> doFlatMap(iter, mapped));
    }

    private static <K> Iterator<? extends K> safeMap(NodeStream<? extends K> ns) {
        return null;
    }

    abstract <I, O> Iterator<O> doFlatMap(Iterator<? extends I> iter, Function<? super I, ? extends Iterator<? extends O>> f);

    protected abstract <Q> NodeStream<Q> mapIter(Function<Iterator<T>, Iterator<Q>> fun);
}

        """.trimIndent())

        val (t_NodeStream) = acu.descendants(ASTClassOrInterfaceDeclaration::class.java).toList { it.typeMirror }
        val (tvar, rvar, kvar) = acu.descendants(ASTTypeParameter::class.java).toList { it.typeMirror }

        val call = acu.firstMethodCall()

        spy.shouldBeOk {
            call shouldHaveType gen.t_Function[captureMatcher(`?` `super` tvar), gen.t_Iterator[`?` extends rvar]]
            call.arguments[0].shouldMatchN {
                methodRef("safeMap") {
                    with(it.typeDsl) {
                        // safeMap#K must have been instantiated to some variation of R
                        it.referencedMethod.shouldMatchMethod(
                                named = "safeMap",
                                declaredIn = t_NodeStream.erasure,
                                withFormals = listOf(t_NodeStream[`?` extends rvar]),
                                returning = gen.t_Iterator[captureMatcher(`?` extends rvar)]
                        )
                    }

                    skipQualifier()
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

        val collectCall = acu.descendants(ASTMethodCall::class.java).first()!!

        collectCall.shouldMatchN {
            methodCall("collect") {
                with(it.typeDsl) {
                    it shouldHaveType gen.`t_List{String}`
                }

                methodCall("distinct") {
                    with(it.typeDsl) {
                        it shouldHaveType gen.t_Stream[gen.t_String]
                    }

                    unspecifiedChildren(2)
                }

                argList {
                    methodCall("collectingAndThen") {
                        with(it.typeDsl) {
                            it shouldHaveType Collector::class[gen.t_String, ts.OBJECT, gen.`t_List{String}`]
                        }

                        argList {
                            methodCall("toList")
                            methodRef("unmodifiableList") {
                                unspecifiedChild()
                                with(it.typeDsl) {
                                    it shouldHaveType gen.t_Function[gen.`t_List{String}`, gen.`t_List{String}`]
                                    it.functionalMethod shouldBe it.typeMirror.streamMethods { it.simpleName == "apply" }.findFirst().get()
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
        val (_, acceptInt, acceptLong) = acu.descendants(ASTMethodDeclaration::class.java).crossFindBoundaries().toList()
        val (castRef, returnRef) = acu.descendants(ASTMethodReference::class.java).toList()

        doTest("In cast context") {

            castRef.shouldMatchN {
                methodRef("accept") {
                    unspecifiedChild()
                    with(it.typeDsl) {
                        it shouldHaveType LongConsumer::class.decl
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
                        it shouldHaveType IntConsumer::class.decl
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

        val t_IntConsumer = with(acu.typeDsl) { IntConsumer::class.decl }

        fooRef.shouldMatchN {
            methodRef("foo") {
                it shouldHaveType t_IntConsumer
                it.referencedMethod.arity shouldBe 1

                unspecifiedChild()
            }
        }

        barRef.shouldMatchN {
            methodRef("bar") {
                it shouldHaveType t_IntConsumer
                it.referencedMethod.arity shouldBe 1

                unspecifiedChild()
            }
        }

        bazRef.shouldMatchN {
            methodRef("baz") {
                it shouldHaveType t_IntConsumer
                it.referencedMethod.arity shouldBe 1

                unspecifiedChild()
            }
        }
    }

    parserTest("Test inexact method ref conflict between static and non-static for primitive type") {
        // this is related to the test below, but this works for inexact methods

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
            import java.util.stream.*;
            class Archive {

                private String getName(int[] certIds) {
                    return IntStream.of(certIds)
                            // both static Integer::toString(int) and non-static Integer::toString() are applicable
                            // the determining factor is that Integer::toString() would require boxing
                            .mapToObj(Integer::toString)
                            .collect(Collectors.joining(", "));
                }
            }
        """.trimIndent())

        val collectCall = acu.descendants(ASTMethodCall::class.java).first()!!


        spy.shouldBeOk {
            collectCall.shouldMatchN {
                methodCall("collect") {
                    it shouldHaveType gen.t_String

                    methodCall("mapToObj") {
                        it shouldHaveType gen.t_Stream[gen.t_String]

                        unspecifiedChildren(2)
                    }

                    argList(1)
                }
            }
        }
    }


    parserTest("Exact method ref with primitive receiver cannot select instance methods of wrapper type") {

        val acu = parser.parse("""
            import java.util.stream.IntStream;

            class Scratch {

                public static void main(String[] args) {
                    IntStream.of(1,2).mapToObj(Integer::doubleValue);
                }
            }
        """.trimIndent())

        val collectCall = acu.descendants(ASTMethodCall::class.java).first()!!

        collectCall.shouldMatchN {
            methodCall("mapToObj") {
                with(it.typeDsl) {
                    it shouldHaveType ts.UNKNOWN
                }

                unspecifiedChildren(2)
            }
        }
    }


    parserTest("Missing compile-time decl") {

        val acu = parser.parse("""
interface IntConsumer { void accept(int i); }

class Scratch {

    static  {
        IntConsumer ic = Scratch::foo;
    }
}
        """.trimIndent())

        val (t_IntConsumer) = acu.declaredTypeSignatures()
        val (fooRef) = acu.descendants(ASTMethodReference::class.java).toList()

        fooRef.shouldMatchN {
            methodRef("foo") {
                it shouldHaveType t_IntConsumer
                it.referencedMethod shouldBe it.typeSystem.UNRESOLVED_METHOD

                unspecifiedChild()
            }
        }
    }



    parserTest("Method ref inside poly conditional, conditional type is fetched first") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
import java.util.Objects;

interface Predicate<Q> {
    boolean test(Q q);

    static <T> Predicate<T> isEqual(Object targetRef) {
        return (null == targetRef)
                ? Objects::isNull
                : object -> targetRef.equals(object);
    }
}
        """.trimIndent())


        val t_Predicate = acu.firstTypeSignature()
        val testMethod = acu.methodDeclarations().get(0)!!
        val tvar = acu.typeVar("T")
        val (ternary) = acu.descendants(ASTConditionalExpression::class.java).toList()
        val (fooRef) = acu.descendants(ASTMethodReference::class.java).toList()

        spy.shouldBeOk {
            ternary shouldHaveType t_Predicate[tvar]
            fooRef.functionalMethod.shouldBeSomeInstantiationOf(testMethod.genericSignature)
            fooRef shouldHaveType t_Predicate[tvar]
        }
    }


    parserTest("Method ref on static class") {

        val acu = parser.parse("""
            import java.util.Arrays;
            import java.util.Objects;
            import java.util.stream.Stream;

            class Scratch {

                static {
                    Object value = null;
                    Stream<String> comps = Arrays.stream((Object[])value).map(Objects::toString);
                }
            }

        """.trimIndent())

        val call = acu.descendants(ASTMethodCall::class.java).firstOrThrow()

        call.shouldMatchN {
            methodCall("map") {
                it shouldHaveType with(it.typeDsl) {
                    gen.t_Stream[gen.t_String]
                }

                it::getQualifier shouldBe methodCall("stream") {
                    it shouldHaveType with(it.typeDsl) {
                        gen.t_Stream[ts.OBJECT]
                    }

                    unspecifiedChildren(2)
                }

                argList {

                    methodRef("toString") {
                        with(it.typeDsl) {
                            it.referencedMethod.shouldMatchMethod(
                                    named = "toString",
                                    declaredIn = java.util.Objects::class.raw,
                                    withFormals = listOf(ts.OBJECT),
                                    returning = ts.STRING
                            )
                        }
                        unspecifiedChild()
                    }
                }
            }
        }
    }



    parserTest("Method ref where target type is fully unknown (is an ivar)") {

        val acu = parser.parse("""
            import java.util.Map;
            import java.util.Map.Entry;
            import java.util.function.Function;

            class Scratch {

                private static final Map<String, Function<Integer, Integer>> canonicalMap
                    = ofEntries(entry("c1", Scratch::add),
                                entry("c2", Scratch::add));

                @SafeVarargs
                static <K, V> Map<K, V> ofEntries(Entry<? extends K, ? extends V>... entries) {
                    return null;
                }

                static <K, V> Entry<K, V> entry(K k, V v) {
                    return null;
                }

                public static int add(int args) {
                    return args;
                }
            }
        """.trimIndent())

        val call = acu.descendants(ASTMethodCall::class.java).firstOrThrow()

        call.shouldMatchN {
            methodCall("ofEntries") {
                with(it.typeDsl) {
                    it.methodType.shouldMatchMethod(
                            named = "ofEntries",
                            declaredIn = call.enclosingType.typeMirror,
                            withFormals = listOf(gen.t_MapEntry[`?` extends gen.t_String, `?` extends gen.t_Function[int.box(), int.box()]].toArray()),
                            returning = gen.t_Map[gen.t_String, gen.t_Function[int.box(), int.box()]]
                    )
                }

                argList {

                    methodCall("entry") {
                        argList {
                            unspecifiedChild()

                            methodRef("add") {
                                with(it.typeDsl) {
                                    it.referencedMethod.shouldMatchMethod(
                                            named = "add",
                                            declaredIn = call.enclosingType.typeMirror,
                                            withFormals = listOf(int),
                                            returning = int
                                    )
                                }
                                unspecifiedChild()
                            }
                        }
                    }

                    methodCall("entry") {
                        argList {
                            unspecifiedChild()

                            methodRef("add") {
                                with(it.typeDsl) {
                                    it.referencedMethod.shouldMatchMethod(
                                            named = "add",
                                            declaredIn = call.enclosingType.typeMirror,
                                            withFormals = listOf(int),
                                            returning = int
                                    )
                                }
                                unspecifiedChild()
                            }
                        }
                    }
                }
            }
        }
    }


    parserTest("Method ref with explicit type parameters") {

        val acu = parser.parse("""
import java.util.Optional;

class Scratch {

    interface NodeStream<T extends Number> {}

    static <T extends Number> NodeStream<T> ofOptional(Optional<? extends T> optNode) {
        return optNode.map(Scratch::<T>singleton).orElseGet(Scratch::empty);
    }

    public static <P extends Number> NodeStream<P> singleton(P node) {
        return null;
    }

    public static <K extends Number> NodeStream<K> empty() {
        return null;
    }

}
        """.trimIndent())

        val (_, t_NodeStream) = acu.descendants(ASTClassOrInterfaceDeclaration::class.java).toList { it.typeMirror }
        val (_, tvar) = acu.descendants(ASTTypeParameter::class.java).crossFindBoundaries().toList { it.typeMirror }
        val call = acu.descendants(ASTMethodCall::class.java).firstOrThrow()

        call.shouldMatchN {
            methodCall("orElseGet") {
                with(it.typeDsl) {
                    it.methodType.shouldMatchMethod(
                            named = "orElseGet",
                            declaredIn = Optional::class[t_NodeStream[tvar]],
                            withFormals = listOf(Supplier::class[`?` extends t_NodeStream[tvar]]),
                            returning = t_NodeStream[tvar]
                    )
                }

                methodCall("map") {
                    with(it.typeDsl) {
                        val capture = captureMatcher(`?` extends tvar)
                        it.methodType.shouldMatchMethod(
                                named = "map",
                                declaredIn = Optional::class[capture],
                                withFormals = listOf(JavaFunction::class[`?` `super` capture, `?` extends t_NodeStream[tvar]]),
                                returning = Optional::class[t_NodeStream[tvar]]
                        )
                    }

                    variableAccess("optNode")

                    argList {
                        methodRef("singleton")
                    }
                }

                argList {
                    methodRef("empty")
                }
            }
        }
    }



    parserTest("Test incompatibility with formal interface") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""

            package scratch;

            import static java.util.stream.Collectors.joining;

            import java.util.List;
            import java.util.function.*;

            class Archive {

                interface Comparator<T> { // subset of java.util.Comparator

                     int compare(T o1, T o2);

                     // Both Comparator and Function are functional interfaces, but the method ref
                     // is exact and not compatible with Comparator

                     default Comparator<T>
                        thenComparing(Comparator<? super T> other) { return null; }

                     default <U extends Comparable<? super U>> Comparator<T>
                        thenComparing(Function<? super T, ? extends U> keyExtractor) { return null; }

                     static <U> Comparator<U> comparingInt(ToIntFunction<? super U> fun) { return null; }
                }

                private String getName() { return "foo"; }

                private String hashList(List<Archive> path) { return null; }

                private Comparator<List<Archive>> comparator() {
                    return Comparator.<List<Archive>>comparingInt(List::size)
                                     .thenComparing(this::hashList);            // we test this one
                }

            }
        """.trimIndent())

        val t_Archive = acu.firstTypeSignature()

        val mref = acu.descendants(ASTMethodReference::class.java)[1]!!

        spy.shouldBeOk {
            mref.functionalMethod.shouldMatchMethod(
                    named = "apply",
                    declaredIn = gen.t_Function[gen.t_List[t_Archive], gen.t_String],
                    withFormals = listOf(gen.t_List[t_Archive]),
                    returning = gen.t_String
            )
        }
    }



    parserTest("Exact mref with this as lhs, referencing generic instance method, with type params mentioned in the return type") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""

            class Scratch {

                private final MapMaker<String> mapMaker = this::copyToMutable;

                protected <V> Map<String, V> copyToMutable(Map<String, V> m) {
                    return null;
                }


                public interface Map<K0, V0> {}

                public interface MapMaker<K> {

                    <R> Map<K, R> copy(Map<K, R> m);
                }
            }
        """.trimIndent())

        val (t_Scratch, t_Map, t_MapMaker) = acu.declaredTypeSignatures()
        val (copyToMutable, copy) = acu.declaredMethodSignatures()

        val mref = acu.descendants(ASTMethodReference::class.java).firstOrThrow()

        spy.shouldBeOk {
            mref.functionalMethod shouldBe t_MapMaker[gen.t_String].getDeclaredMethod(copy.symbol)
            mref.referencedMethod shouldBe copyToMutable // exactly, ie V was not substituted
        }
    }


    parserTest("Inexact mref which must differentiate two overridden overloads") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
// reproduces what's in java.util, to not depend on JDK version

interface Collection<E> {
    boolean addAll(Collection<? extends E> c); // 0
}

interface Set<E> extends Collection<E> {
    @Override
    boolean addAll(Collection<? extends E> c); // 1
}

abstract class AbstractCollection<E> implements Collection<E> {

    @Override
    public boolean addAll(Collection<? extends E> c) { // 2
        return false;
    }
}

abstract class AbstractSet<E> extends AbstractCollection<E> implements Set<E> {

}

class MySet<E> extends AbstractSet<E> implements Set<E> { }


class Scratch {

    private final Additioner adder = MySet::addAll;

    public interface Additioner {
        <R> boolean plus(MySet<R> m, Collection<? extends R> other); // 3
    }
}


        """.trimIndent())

        val (_, _, abstractColl, _, _, _, t_Additioner) = acu.declaredTypeSignatures()
        val (_, _, inAbstractColl, plus) = acu.declaredMethodSignatures()

        val mref = acu.descendants(ASTMethodReference::class.java).firstOrThrow()

        spy.shouldBeOk {
            mref.functionalMethod shouldBe plus
            val rvar = plus.typeParameters[0]!!
            mref.referencedMethod shouldBe abstractColl[rvar].getDeclaredMethod(inAbstractColl.symbol)
            mref shouldHaveType t_Additioner
        }
    }


})
