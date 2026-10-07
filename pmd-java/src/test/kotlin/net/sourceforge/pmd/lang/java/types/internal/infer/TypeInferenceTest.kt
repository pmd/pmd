/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

@file:Suppress("LocalVariableName")

package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol
import net.sourceforge.pmd.lang.java.types.*
import net.sourceforge.pmd.lang.test.ast.NodeSpec
import net.sourceforge.pmd.lang.test.ast.shouldBe
import net.sourceforge.pmd.lang.test.ast.shouldBeA
import net.sourceforge.pmd.lang.test.ast.shouldMatchN
import java.util.*

/**
 */
class TypeInferenceTest : ProcessorTestSpec({

    val jutil = "java.util"
    val juf = "$jutil.function"
    val justream = "$jutil.stream"

    parserTestContainer("Test method invoc resolution") {
        importedTypes += Arrays::class.java

        inContext(ExpressionParsingCtx) {
            "Arrays.asList(\"a\")" should parseAs {
                methodCall("asList") {
                    val arraysClass = with(it.typeDsl) { java.util.Arrays::class.decl }
                    val asList = arraysClass.getMethodsByName("asList")[0]

                    it.methodType.also {
                        it::getName shouldBe "asList"
                        it::isVarargs shouldBe true
                        it.formalParameters[0].shouldBeA<JArrayType> {
                            it.componentType shouldBe it.typeSystem.STRING
                        }
                        it::getReturnType shouldBe RefTypeConstants(it.typeSystem).`t_List{String}`
                        it::getTypeParameters shouldBe asList.typeParameters // not substituted
                    }

                    skipQualifier()
                    argList(1)
                }
            }
        }
    }

    parserTest("Test method invoc lub of params") {
        importedTypes += Arrays::class.java

        val call = ExpressionParsingCtx.parseNode("Arrays.asList(1, 2.0)", ctx = this) as ASTMethodCall

        val arraysClass = with(call.typeDsl) { Arrays::class.decl }
        val asList = arraysClass.getMethodsByName("asList")[0]

        call.overloadSelectionInfo.isVarargsCall shouldBe true
        call.methodType.also {
            it.isVarargs shouldBe true
            val (formal, ret) = with(TypeDslOf(it.typeSystem)) {
                // we can't hardcode the lub result because it is JDK specific
                val `t_lub(Double, Integer)` = ts.lub(double.box(), int.box())

                Pair(
                    `t_lub(Double, Integer)`,
                    gen.t_List[`t_lub(Double, Integer)`]
                )
            }

            it.formalParameters[0].shouldBeA<JArrayType> {
                it.componentType shouldBe formal
            }

            it.returnType shouldBe ret
            it.typeParameters shouldBe asList.typeParameters // not substituted
        }
    }


    val stream =
            """Stream.of("a", "b")
                     .map(it -> it.isEmpty())
                     .collect(Collectors.toList())
                """.trimIndent()

    val streamSpec: NodeSpec<ASTMethodCall> = {

        it::getMethodName shouldBe "collect"
        it shouldHaveType with(it.typeDsl) { gen.t_List[boolean.box()] } // List<Boolean>
        it::getQualifier shouldBe child<ASTMethodCall> {
            it::getMethodName shouldBe "map"
            it shouldHaveType with(it.typeDsl) { gen.t_Stream[boolean.box()] } // Stream<Boolean>
            it::getQualifier shouldBe child<ASTMethodCall> {
                it::getMethodName shouldBe "of"
                it shouldHaveType with(it.typeDsl) { gen.t_Stream[gen.t_String] } // Stream<String>
                it::getQualifier shouldBe typeExpr {
                    classType("Stream")
                }

                it::getArguments shouldBe child {
                    stringLit("\"a\"") {
                        it shouldHaveType it.typeSystem.STRING
                    }
                    stringLit("\"b\"") {
                        it shouldHaveType it.typeSystem.STRING
                    }
                }
            }

            it::getArguments shouldBe child {
                child<ASTLambdaExpression> {

                    val `t_Function{String, Boolean}` = with(it.typeDsl) { gen.t_Function[gen.t_String, boolean.box()] }

                    it shouldHaveType `t_Function{String, Boolean}`
                    with(it.typeDsl) {
                        it.functionalMethod.shouldMatchMethod(
                                named = "apply",
                                declaredIn = `t_Function{String, Boolean}`,
                                withFormals = listOf(gen.t_String),
                                returning = boolean.box()
                        )
                    }

                    child<ASTLambdaParameterList> {
                        child<ASTLambdaParameter> {
                            localVarModifiers { }
                            variableId("it")
                        }
                    }
                    it::getExpressionBody shouldBe child<ASTMethodCall> {
                        it shouldHaveType it.typeSystem.BOOLEAN
                        it::getQualifier shouldBe variableAccess("it") {
                            it shouldHaveType it.typeSystem.STRING
                        }
                        it::getArguments shouldBe child {}
                    }
                }
            }
        }
        it::getArguments shouldBe child {
            unspecifiedChild()
        }
    }

    parserTestContainer("Test method call chain") {
        otherImports += "java.util.stream.*"

        inContext(ExpressionParsingCtx) {
            stream should parseAs {
                child(nodeSpec = streamSpec)
            }
        }
    }

    parserTestContainer("Test method call chain as var initializer") {
        otherImports += "java.util.stream.*"

        inContext(StatementParsingCtx) {
            "var foo = $stream;" should parseAs {
                localVarDecl {
                    modifiers { }

                    it::isTypeInferred shouldBe true
                    varDeclarator {
                        variableId("foo") {
                            it shouldHaveType with(it.typeDsl) { gen.t_List[boolean.box()] }
                        }

                        child(nodeSpec = streamSpec)
                    }
                }
            }
        }
    }

    parserTestContainer("Test many dependencies") {
        inContext(StatementParsingCtx) {

            """
             final $jutil.Map<String, String> map = $justream.Stream.of("de", "").collect($justream.Collectors.toMap($juf.Function.identity(), $juf.Function.identity()));
            """ should parseAs {
                localVarDecl {
                    localVarModifiers { }
                    unspecifiedChild()
                    variableDeclarator("map") {
                        methodCall("collect") {
                            it shouldHaveType with(it.typeDsl) {
                                java.util.Map::class[ts.STRING, ts.STRING]
                            }
                            unspecifiedChild()
                            argList {
                                methodCall("toMap") {
                                    unspecifiedChild()
                                    argList {
                                        methodCall("identity") {
                                            unspecifiedChildren(2)
                                            it shouldHaveType with(it.typeDsl) {
                                                java.util.function.Function::class[ts.STRING, ts.STRING]
                                            }
                                        }
                                        methodCall("identity") {
                                            unspecifiedChildren(2)
                                            it shouldHaveType with(it.typeDsl) {
                                                java.util.function.Function::class[ts.STRING, ts.STRING]
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    parserTest("Test type var bound substitution in inherited members") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
interface I<S> {}
class C<Q> implements I<Q> {}

class Scratch<O> {

    <K extends I<O>> K inherited(K k) { return k; }
    
    static class Inner<T> extends Scratch<T> {
        {
            C<T> t = new C<>();
            I<T> res = inherited(t);
        }
    }
}
            """.trimIndent()
        )

        val (_, t_C) = acu.declaredTypeSignatures()
        val tParam = acu.typeVariables().first { it.name == "T" }

        spy.shouldBeOk {
            // fixme this test could be better
            acu.firstMethodCall() shouldHaveType t_C[tParam] // of T, not of O
        }
    }

    parserTest("Test inference var inst substitution in enclosing ctx") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
import java.util.ArrayList;
import java.util.List;

class Scratch {

    static <K> K m(List<? extends K> k) { return null; }

    static <T> List<T> of(T k) { return null; }

    {
        List<String> t = new ArrayList<>();
        Object res = of(m(t));
    }
}
            """.trimIndent()
        )

        val (ofCall, mCall) = acu.methodCalls().toList()
        val (m, of) = acu.methodDeclarations().toList { it.genericSignature }

        spy.shouldBeOk {
            ofCall shouldHaveType gen.`t_List{String}`
            ofCall.methodType shouldBeSomeInstantiationOf of

            mCall shouldHaveType gen.t_String
            mCall.methodType shouldBeSomeInstantiationOf m
        }
    }

    parserTest("Constructor with inner class") {
        val acu = parser.parse(
            """
import java.util.Iterator;
import java.util.Map;

class MyMap<K, V> {


    Iterator<K> descendingKeyIterator() {
        return new KeyIter(lo(), hi());
    }

    Entry lo() {return null;}

    Entry hi() {return null;}

    class Entry implements Map.Entry<K,V> { }

    class KeyIter implements Iterator<K> {

        <E extends Map.Entry<? extends K, ? extends V>>

        KeyIter(E lo, E hi) {}

        @Override
        public boolean hasNext() {return false;}

        @Override
        public K next() {return null;}
    }
}
            """.trimIndent()
        )

        val (t_MyMap, t_MyMapEntry, t_KeyIter) = acu.descendants(ASTTypeDeclaration::class.java)
            .toList { it.typeMirror }
        val (kvar, vvar) = acu.descendants(ASTTypeParameter::class.java).toList { it.typeMirror }

        val ctorCall = acu.descendants(ASTConstructorCall::class.java).firstOrThrow()

        ctorCall.shouldMatchN {
            constructorCall {
                val `t_MyMap{K,V}KeyIter`: JClassType
                val `t_MyMap{K,V}Entry`: JClassType

                with(it.typeDsl) {
                    `t_MyMap{K,V}KeyIter` = t_MyMap[kvar, vvar].selectInner(t_KeyIter.symbol, emptyList())
                    `t_MyMap{K,V}Entry` = t_MyMap[kvar, vvar].selectInner(t_MyMapEntry.symbol, emptyList())

                    it.methodType.shouldMatchMethod(
                        named = JConstructorSymbol.CTOR_NAME,
                        declaredIn = `t_MyMap{K,V}KeyIter`,
                        withFormals = listOf(`t_MyMap{K,V}Entry`, `t_MyMap{K,V}Entry`),
                        returning = `t_MyMap{K,V}KeyIter`
                    )
                }

                it::getTypeNode shouldBe classType("KeyIter") {
                    it shouldHaveType `t_MyMap{K,V}KeyIter`
                }

                argList(2)
            }
        }
    }

    parserTest("Concurrent modification exception when propagating bounds modifies self var") {
        // problem is the ivar for E has Enum as upper bound and no Object,
        // so Object is backpropagated to it during its own propagateAllBounds action

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
class Foo {

    void descendingKeyIterator() { 
        assertThat(caseInsensitiveValueOf(Tropes.values(), "foo"), is(Tropes.FOO));
    }

    public static <T> void assertThat(T actual, Matcher<? super T> matcher) { }
    public static <T> Matcher<T> is(Class<T> matcher) {}
    public static <T> Matcher<T> is(T value) {return null;}
    public static <E extends Enum<?>> E caseInsensitiveValueOf(E[] enumValues, String constant) {return null;}
    interface Matcher<T> {}
    enum Tropes { FOO, BAR, baz }
}

            """.trimIndent()
        )

        spy.shouldBeOk {
            acu.firstMethodCall() shouldHaveType void
        }
    }

    parserTest("#4902 bad intersection") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BadIntersection {

  interface Animal { }
  enum Bird implements Animal { PARROT, CHICKEN }
  enum Fish implements Animal { GOLDFISH, MACKEREL }

  private static List<Animal> combineAnimals() {
    return Stream.of(
            Bird.values(),
            Fish.values()
        )
        .flatMap(Arrays::stream)
        .collect(Collectors.toList());
  }
}
            """.trimIndent()
        )

        val (_, t_Animal) = acu.declaredTypeSignatures()

        spy.shouldBeOk {
            acu.firstMethodCall() shouldHaveType java.util.List::class[t_Animal]
        }
    }
    parserTest("#5047 inference failed with enum") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            interface Function<T,R> { R apply(T t); }

            public class Main {
                public enum OptOutStatus {
                    UNKNOWN_STATUS(3L);

                    private final long id;

                    OptOutStatus(long id) {
                        this.id = id;
                    }

                    public long id() {
                        return this.id;
                    }
                }

                static class Utils {
                    private Long getValue(OptOutStatus val) {
                        return getValue(val, OptOutStatus::id);
                    }

                    private <T extends Enum<T>> Long getValue(T enumValue, Function<T, Long> fn) {
                        if (enumValue == null) {
                            return null;
                        }
                        return fn.apply(enumValue);
                    }
                }
            }

            """.trimIndent()
        )

        val (_, _, optOutEnum) = acu.declaredTypeSignatures()
        val (_, getValue2) = acu.methodDeclarations().filter { it.name == "getValue" }.toList()

        spy.shouldBeOk {
            val info = acu.firstMethodCall().overloadSelectionInfo
            info::isFailed shouldBe false
            info.methodType shouldBeSomeInstantiationOf getValue2.genericSignature
            info.methodType.formalParameters[0] shouldBe optOutEnum
        }
    }
    parserTest("#5190 NPE in type inf") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
    import java.util.Iterator;
    interface Optional<V> {
        static <T> Optional<T> ofNullable(T t) {}
    }
    interface Map<K,V> {}
    interface List<V> extends Iterable<V> {}
    interface AttributeValue{}
    
class Main {

        private Optional<Map<String, AttributeValue>> loadForKey(final String key) {
            return Optional.ofNullable(
                getOnlyElement(queryForKey(key), null)
            );
        }

        private List<Map<String, AttributeValue>> queryForKey(final String key) {
            return null;
        }

        public static <T> T getOnlyElement(final Iterable<? extends T> iterable, final T defaultValue) {
            return getOnlyElement(iterable.iterator(), defaultValue);
        }

        public static <T> T getOnlyElement(final Iterator<? extends T> iterator, final T defaultValue) {
            return null;
        }

    }
            """.trimIndent()
        )

        val (_, _, _) = acu.declaredTypeSignatures()
        val (ofNullable) = acu.methodDeclarations().toList { it.genericSignature }

        spy.shouldBeOk {
            val info = acu.firstMethodCall().overloadSelectionInfo
            info::isFailed shouldBe false
            info.methodType shouldBeSomeInstantiationOf ofNullable
        }
    }

    parserTest("Lower bounds of an interdependent batch are used before upper bounds") {
        // Reduced from spring-framework's JsonPathAssertions#value usages.
        // The inference context for `value` ends up with the bounds
        //     'a { 'a <: Object, 'a <: 'd }      ('a is T of value)
        //     'd { 'd >: Integer, 'd >: 'a }     ('d is T of equalTo)
        // 'a and 'd are interdependent, so they are instantiated simultaneously.
        // If UPPER may be applied to 'a in the same pass in which LOWER is
        // applied to 'd, we get 'a := Object and 'd := Integer, and incorporating
        // both yields the false bound 'a <: Integer, ie the call does not resolve
        // at all. Like javac, LOWER must be exhausted over the whole batch first:
        // 'd := Integer, and only once that is incorporated 'a := glb(Object, Integer).
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class Scratch {
                interface Matcher<T> {}
                interface Consumer<T> {}
                interface BodyContentSpec {}

                interface JsonPathAssertions {
                    <T> BodyContentSpec value(Matcher<? super T> matcher);
                    <T> BodyContentSpec value(Consumer<T> consumer);
                }

                static <T> Matcher<T> equalTo(T operand) { return null; }

                static void test(JsonPathAssertions a) {
                    a.value(equalTo(42));
                }
            }
            """.trimIndent()
        )

        val (_, t_Matcher, _, t_BodyContentSpec) = acu.declaredTypeSignatures()
        val valueCall = acu.firstMethodCall("value")
        val equalToCall = acu.firstMethodCall("equalTo")

        spy.shouldBeOk {
            valueCall.overloadSelectionInfo::isFailed shouldBe false
            valueCall shouldHaveType t_BodyContentSpec
            // javac infers T := Integer here too, not Object.
            valueCall.methodType.formalParameters[0] shouldBe t_Matcher[`?` `super` int.box()]
            equalToCall shouldHaveType t_Matcher[int.box()]
        }
    }

    parserTest("Interdependent batch in an implicitly typed lambda body") {
        // Reduced from spring-framework's DefaultPublishedEvents#matchingMapped.
        // Same root cause as the test above: `of`'s ivar and the ivar of the
        // flatMap function are interdependent, and applying UPPER too eagerly
        // makes the whole `of` call unresolvable.
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            import java.util.List;
            import java.util.function.Function;
            import java.util.function.Predicate;
            import java.util.stream.Stream;

            class Scratch {
                interface Typed<T> {}

                static class Simple<T> implements Typed<T> {
                    private final List<T> events = null;

                    static <T> Simple<T> of(Stream<T> stream) { return null; }

                    <S> Typed<T> matchingMapped(Function<T, S> mapper, Predicate<? super S> predicate) {
                        return Simple.of(this.events.stream().flatMap(it -> {
                            S mapped = mapper.apply(it);
                            return predicate.test(mapped) ? Stream.of(it) : Stream.empty();
                        }));
                    }
                }
            }
            """.trimIndent()
        )

        val t_Simple = acu.declaredTypeSignatures()[2]
        val (of) = acu.declaredMethodSignatures()
        val ofCall = acu.firstMethodCall("of")

        spy.shouldBeOk {
            ofCall.overloadSelectionInfo::isFailed shouldBe false
            ofCall.methodType shouldBeSomeInstantiationOf of
            // ie Simple<T>, where T is the type param of the enclosing class
            ofCall shouldHaveType t_Simple
        }
    }
})
