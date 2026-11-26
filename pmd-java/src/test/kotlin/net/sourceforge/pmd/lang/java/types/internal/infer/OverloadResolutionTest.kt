/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import javasymbols.testdata.ProtectedMembers
import javasymbols.testdata.ProtectedOverloads
import net.sourceforge.pmd.lang.test.ast.component6
import net.sourceforge.pmd.lang.test.ast.component7
import net.sourceforge.pmd.lang.test.ast.component8
import net.sourceforge.pmd.lang.test.ast.shouldBe
import net.sourceforge.pmd.lang.test.ast.shouldMatchN
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.types.*
import net.sourceforge.pmd.lang.java.types.testdata.Overloads

@Suppress("LocalVariableName")
class OverloadResolutionTest : ProcessorTestSpec({

    val t_SomeEnum_name = "net.sourceforge.pmd.lang.java.types.testdata.SomeEnum"
    val t_Overloads_name = "net.sourceforge.pmd.lang.java.types.testdata.Overloads"



    test("Test conversion in compat tests") {

        fun assertConvertible(types: Pair<JTypeMirror, JTypeMirror>, pos: Boolean, canBox: Boolean = true) {
            val (t, s) = types
            val res = Infer.isConvertible(t, s, canBox).somehow()
            assert(if (pos) res else !res) {
                "Failure, expected\n\t${if (pos) "" else " not"} $t \n\t\t<: $s"
            }
        }

        fun assertConvertible(types: Pair<JTypeMirror, JTypeMirror>, canBox: Boolean = true) {
            assertConvertible(types, true, canBox)
        }

        fun assertNotConvertible(types: Pair<JTypeMirror, JTypeMirror>, canBox: Boolean = true) {
            if (types.first != types.second)
                assertConvertible(types, false, canBox)
        }

        with(TypeDslOf(testTypeSystem)) {

            ts.allPrimitives.forEach {
                assertConvertible(it to ts.OBJECT)    // boxing then widening
                assertConvertible(it to it)           // identity

                // unboxing
                assertConvertible(it.box() to it)
                assertNotConvertible(it.box() to it, false)

                // boxing
                assertConvertible(it to it.box())
                assertNotConvertible(it to it.box(), false)

                // boxing, then widening
                assertConvertible(it to ts.SERIALIZABLE)
                assertNotConvertible(it to ts.SERIALIZABLE, false)

                it.superTypeSet.forEach { s ->
                    // widening
                    assertConvertible(it to s)
                    assertConvertible(it to s, false)

                    // unboxing, then widening
                    assertConvertible(it.box() to s)
                    assertNotConvertible(it.box() to s, false)

                    // narrowing
                    assertNotConvertible(s to it)
                    assertNotConvertible(s to it, false)
                }
            }

            assertNotConvertible(byte to char)        // widening then narrowing (allowed in casts)
            assertNotConvertible(ts.BOXED_VOID to ts.INT) // unrelated types
            assertNotConvertible(int to long.box())   // widening then boxing (#7149)
            assertNotConvertible(int to long.box(), false)
            assertNotConvertible(int to double.box()) // widening then boxing (#7149)
            assertNotConvertible(ts.NULL_TYPE to int) // null to primitive (#7149)
        }
    }

    parserTestContainer("Test strict overload") {
        asIfIn(Overloads::class.java)

        inContext(ExpressionParsingCtx) {
            "of($t_SomeEnum_name.FOO)" should parseAs {
                methodCall("of") {
                    with (it.typeDsl) {
                        val t_SomeEnum = typeOf(t_SomeEnum_name)
                        it shouldHaveType gen.t_EnumSet[t_SomeEnum] // EnumSet<SomeEnum>
                        it.methodType.shouldMatchMethod(
                                named = "of",
                                declaredIn = typeOf(t_Overloads_name),
                                withFormals = listOf(t_SomeEnum),
                                returning = gen.t_EnumSet[t_SomeEnum]
                        )
                    }

                    it::getArguments shouldBe child {
                        fieldAccess("FOO")
                    }
                }
            }
        }
    }

    parserTestContainer("Test partially unresolved") {
        asIfIn(Overloads::class.java)

        inContext(ExpressionParsingCtx) {
            "of(DoesntExist.FOO)" should parseAs {
                methodCall("of") {
                    with (it.typeDsl) {
                        // EnumSet</*unresolved*/>
                        it shouldHaveType gen.t_EnumSet[ts.UNKNOWN]
                        // Overloads::of(/*unresolved*/) -> java.util.EnumSet</*unresolved*/>
                        it.methodType.shouldMatchMethod(
                                named = "of",
                                declaredIn = typeOf(t_Overloads_name),
                                withFormals = listOf(ts.UNKNOWN),
                                returning = gen.t_EnumSet[ts.UNKNOWN]
                        )
                    }

                    it::getArguments shouldBe child {
                        fieldAccess("FOO")
                    }
                }
            }
        }
    }

    parserTestContainer("Test strict overload 2 args") {
        asIfIn(Overloads::class.java)

        inContext(ExpressionParsingCtx) {
            "of($t_SomeEnum_name.FOO, $t_SomeEnum_name.BAR)" should parseAs {
                methodCall("of") {
                    with (it.typeDsl) {
                        val t_SomeEnum = typeOf(t_SomeEnum_name)
                        it shouldHaveType gen.t_EnumSet[t_SomeEnum] // EnumSet<SomeEnum>
                        it.methodType.shouldMatchMethod(
                                named = "of",
                                declaredIn = typeOf(t_Overloads_name),
                                withFormals = listOf(t_SomeEnum, t_SomeEnum),
                                returning = gen.t_EnumSet[t_SomeEnum]
                        )
                    }

                    it::getArguments shouldBe child {
                        fieldAccess("FOO")
                        fieldAccess("BAR")
                    }
                }
            }
        }
    }

    parserTestContainer("Test varargs overload") {
        asIfIn(Overloads::class.java)

        inContext(ExpressionParsingCtx) {
            "of($t_SomeEnum_name.FOO, $t_SomeEnum_name.BAR,  $t_SomeEnum_name.BAR)" should parseAs {
                methodCall("of") {
                    with (it.typeDsl) {
                        val t_SomeEnum = typeOf(t_SomeEnum_name)
                        it shouldHaveType gen.t_EnumSet[t_SomeEnum] // EnumSet<SomeEnum>

                        it.overloadSelectionInfo::isVarargsCall shouldBe true
                        // Overloads::of(SomeEnum, SomeEnum...) -> EnumSet<SomeEnum>
                        it.methodType.shouldMatchMethod(
                                named = "of",
                                declaredIn = typeOf(t_Overloads_name),
                                withFormals = listOf(t_SomeEnum, t_SomeEnum.toArray()),
                                returning = gen.t_EnumSet[t_SomeEnum]
                        )
                    }

                    it::getArguments shouldBe child {
                        fieldAccess("FOO")
                        fieldAccess("BAR")
                        fieldAccess("BAR")
                    }
                }
            }
        }
    }

    parserTestContainer("Test varargs overload with array") {
        asIfIn(Overloads::class.java)

        inContext(ExpressionParsingCtx) {
            "of($t_SomeEnum_name.FOO, new $t_SomeEnum_name[]{$t_SomeEnum_name.BAR,  $t_SomeEnum_name.BAR})" should parseAs {
                methodCall("of") {
                    // SomeEnum
                    val t_SomeEnum = with(it.typeDsl) { typeOf(t_SomeEnum_name) }
                    // SomeEnum[]
                    val t_SomeEnumArray = with(it.typeDsl) { t_SomeEnum.toArray() }

                    with (it.typeDsl) {
                        it shouldHaveType gen.t_EnumSet[t_SomeEnum] // EnumSet<SomeEnum>

                        it.overloadSelectionInfo::isVarargsCall shouldBe false // selected in strict phase
                        // Overloads::of(SomeEnum, SomeEnum...) -> EnumSet<SomeEnum>
                        it.methodType.shouldMatchMethod(
                                named = "of",
                                declaredIn = typeOf(t_Overloads_name),
                                withFormals = listOf(t_SomeEnum, t_SomeEnumArray),
                                returning = gen.t_EnumSet[t_SomeEnum]
                        )
                    }

                    it::getArguments shouldBe child {
                        fieldAccess("FOO")
                        child<ASTArrayAllocation> {
                            it shouldHaveType t_SomeEnumArray
                            unspecifiedChild()
                            child<ASTArrayInitializer> {
                                it shouldHaveType t_SomeEnumArray
                                fieldAccess("BAR") {
                                    it shouldHaveType t_SomeEnum
                                    typeExpr {
                                        qualClassType(t_SomeEnum_name)
                                    }
                                }
                                fieldAccess("BAR") {
                                    it shouldHaveType t_SomeEnum
                                    typeExpr {
                                        qualClassType(t_SomeEnum_name)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    parserTest("Test overload resolution with unchecked conversion") {
        val acu = parser.parse(
           """
           class Scratch {
                int foo(Class<?> k) {} // this is selected
                void foo(Object o) {}
                {
                    Class k = Scratch.class; // raw
                    foo(k);
                }
           }

           """.trimIndent()
        )

        val (fooClass) = acu.descendants(ASTMethodDeclaration::class.java).toList()

        val call = acu.descendants(ASTMethodCall::class.java).first()!!

        call.shouldMatchN {
            methodCall("foo") {
                it shouldHaveType it.typeSystem.INT
                it.methodType.symbol shouldBe fooClass.symbol

                argList {
                    variableAccess("k") {
                        it shouldHaveType with(it.typeDsl) { Class::class.raw }
                    }
                }
            }
        }
    }

    parserTest("Test primitive conversion in loose phase") {
        inContext(ExpressionParsingCtx) {
            val acu = parser.parse(
                """
                class Foo {
                    void foo(long i) {
                        foo('c');
                    }

                    void foo(String other) {}
                }
                """
            )

            val fooM = acu.descendants(ASTMethodDeclaration::class.java).firstOrThrow()

            val call = acu.descendants(ASTMethodCall::class.java).firstOrThrow()

            call.shouldMatchN {
                methodCall("foo") {
                    it.methodType.apply {
                        formalParameters shouldBe listOf(it.typeSystem.LONG)
                        symbol shouldBe fooM.symbol
                    }
                    argList {
                        charLit("'c'")
                    }
                }
            }
        }
    }

    parserTest("#4557 two overloads with boxed types") {
        val acu = parser.parse(
            """
            package p;

            import static p.Static.assertThat;

            class Klass {
                static {
                    // This is assertThat(Integer)
                    // assertThat(Long) is not applicable, because int -> Long
                    // would need widening and then boxing (#7149).
                    assertThat(1);
                }
            }
            class Static {

                public static Object assertThat(Integer actual) {
                    return null;
                }

                public static Object assertThat(Long actual) {
                    return null;
                }

            }
            """.trimIndent()
        )

        val fooM = acu.methodDeclarations().firstOrThrow()
        val call = acu.firstMethodCall()

        call.overloadSelectionInfo.should {
            it.isFailed shouldBe false
            it.methodType.symbol shouldBe fooM.symbol
        }
    }

    parserTest("Two overloads with boxed types, widening required, not applicable") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class Static {
                static {
                    // not applicable: 1 is int, and JLS 5.3 allows boxing then
                    // widening reference, not widening primitive then boxing (#7149)
                    assertThat(1);
                }

                public static Object assertThat(Double actual) {
                    return null;
                }

                public static Object assertThat(Long actual) {
                    return null;
                }

            }
            """.trimIndent()
        )

        val call = acu.firstMethodCall()
        spy.shouldHaveMissingCtDecl(call)
    }

    parserTest("#7149 widening then boxing is not a loose invocation conversion") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class Loose {
                static void f(int x, Long y) { }
                static void f(long x, long y) { }

                void t(Integer value) {
                    // f(int, Long) is not applicable, 2 -> Long would need widening then boxing
                    f(value, 2);
                }
            }
            """.trimIndent()
        )

        val call = acu.firstMethodCall()

        spy.shouldBeOk {
            call.methodType.shouldMatchMethod(
                named = "f",
                declaredIn = acu.firstTypeSignature(),
                withFormals = listOf(long, long),
                returning = void
            )
        }
    }

    parserTest("#7149 int argument for a Long parameter is not applicable") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class G {
                static void g(Long y) { }

                void t() {
                    g(1);
                }
            }
            """.trimIndent()
        )

        spy.shouldHaveMissingCtDecl(acu.firstMethodCall())
    }

    parserTest("#7149 int argument for a Number parameter is applicable by boxing and widening") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class N {
                static void f(Number n) { }

                void t() {
                    f(1);
                }
            }
            """.trimIndent()
        )

        val call = acu.firstMethodCall()

        spy.shouldBeOk {
            call.methodType.shouldMatchMethod(
                named = "f",
                declaredIn = acu.firstTypeSignature(),
                withFormals = listOf(Number::class.decl),
                returning = void
            )
        }
    }

    parserTest("#7149 null argument for an int parameter is not applicable") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class H {
                static void h(int x) { }

                void t() {
                    h(null);
                }
            }
            """.trimIndent()
        )

        spy.shouldHaveMissingCtDecl(acu.firstMethodCall())
    }

    parserTest("#7149 type variable bounded by Integer is applicable to an int parameter") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class TV {
                static void h(int x) { }

                <T extends Integer> void t(T t) {
                    h(t);
                }
            }
            """.trimIndent()
        )

        val call = acu.firstMethodCall()

        spy.shouldBeOk {
            call.methodType.shouldMatchMethod(
                named = "h",
                declaredIn = acu.firstTypeSignature(),
                withFormals = listOf(int),
                returning = void
            )
        }
    }

    parserTest("#7146 int is not more specific than Integer") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class Example {
                static void d(int x, int y) { }
                static void d(long x, Integer y) { }

                void test(Integer value) {
                    // ambiguous: both are applicable by loose invocation,
                    // and neither int nor Integer is a subtype of the other
                    d(value, 5);
                }
            }
            """.trimIndent()
        )

        spy.shouldBeAmbiguous(acu.firstMethodCall())
    }

    parserTest("#7146 only e(int, int) is applicable by strict invocation") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class E {
                static void e(int x, int y) { }
                static void e(long x, Integer y) { }

                void t() {
                    e(1, 5);
                }
            }
            """.trimIndent()
        )

        val call = acu.firstMethodCall()

        spy.shouldBeOk {
            call.methodType.shouldMatchMethod(
                named = "e",
                declaredIn = acu.firstTypeSignature(),
                withFormals = listOf(int, int),
                returning = void
            )
        }
    }

    parserTest("#7146 int... is not more specific than Object... for an empty varargs call") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class VA {
                static void v(int... a) { }
                static void v(Object... a) { }

                void t() {
                    // ambiguous: int is not a subtype of Object
                    v();
                }
            }
            """.trimIndent()
        )

        spy.shouldBeAmbiguous(acu.firstMethodCall())
    }

    parserTest("#7149 conditional with a null branch is typed as the box of the other branch") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class Cond {
                static void h(int x) { }
                static void g(int x) { }
                static void g(Integer x) { }
                static void k(boolean x) { }

                void t(boolean b) {
                    // javac types b ? 1 : null as Integer, so h(int) applies by unboxing
                    h(b ? 1 : null);
                    // and g(Integer) is selected in the strict phase
                    g(b ? null : 1);
                    // the same for Boolean
                    k(b ? true : null);
                }
            }
            """.trimIndent()
        )

        val t_Cond = acu.firstTypeSignature()
        val (hCall, gCall, kCall) = acu.descendants(ASTMethodCall::class.java).toList()

        spy.shouldBeOk {
            hCall.methodType.shouldMatchMethod(
                named = "h",
                declaredIn = t_Cond,
                withFormals = listOf(int),
                returning = void
            )
            gCall.methodType.shouldMatchMethod(
                named = "g",
                declaredIn = t_Cond,
                withFormals = listOf(int.box()),
                returning = void
            )
            kCall.methodType.shouldMatchMethod(
                named = "k",
                declaredIn = t_Cond,
                withFormals = listOf(boolean),
                returning = void
            )
        }
    }

    parserTest("Overload selection must identify fallbacks if any") {
        val acu = parser.parse(
            """
import java.util.Arrays;
import java.util.stream.Collectors;
import java.lang.reflect.Type;

class Scratch {

    static void foo(int notOk) {}
    static void foo(long notOk) {}
    static void foo(String ok) {}
    static void foo(Object ok) {}

    static {
        Class<?>[] genArray = null;
// notice the context
//      vvv
        foo(Arrays.stream(genArray)
                  .map(Type::getTypeName)
                  .collect(Collectors.joining(", ")));
    }
}
            """.trimIndent()
        )

        val fooCall = acu.descendants(ASTMethodCall::class.java).firstOrThrow()

        fooCall.shouldMatchN {
            methodCall("foo") {
                argList {
                    methodCall("collect") {
                        it shouldHaveType with(it.typeDsl) { gen.t_String }

                        methodCall("map") {

                            it shouldHaveType with(it.typeDsl) { gen.t_Stream[gen.t_String] }

                            it::getQualifier shouldBe methodCall("stream") {
                                it.overloadSelectionInfo.isVarargsCall shouldBe false
                                it shouldHaveType with(it.typeDsl) { gen.t_Stream[Class::class[`?`]] }
                                unspecifiedChild()
                                argList {
                                    variableAccess("genArray") {
                                        it shouldHaveType with(it.typeDsl) { Class::class[`?`].toArray() }
                                    }
                                }
                            }

                            argList {
                                methodRef("getTypeName") {
                                    with(it.typeDsl) {
                                        it shouldHaveType gen.t_Function[Class::class[`?`], gen.t_String]
                                    }

                                    skipQualifier()
                                }
                            }
                        }

                        argList(1)
                    }
                }
            }
        }
    }

    parserTest("#7152 type-name-qualified call also considers instance methods") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class Foo {
                static void m(int x) { }
                void m(Object o) { }

                void t(Integer value) {
                    // javac selects m(Object) and reports that it is not static (JLS 15.12.3)
                    Foo.m(value);
                }
            }
            """.trimIndent()
        )

        val call = acu.firstMethodCall()

        spy.shouldBeOk {
            call.methodType.shouldMatchMethod(
                named = "m",
                declaredIn = acu.firstTypeSignature(),
                withFormals = listOf(ts.OBJECT),
                returning = void
            )
        }
    }

    parserTest("#7152 type-name-qualified call selects the most specific static method") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class Sup {
                static void m(Object o) { }
            }

            class Foo extends Sup {
                void m(int x) { }

                void t(Integer value) {
                    Foo.m(value); // Sup.m(Object) is applicable by strict invocation
                }
            }
            """.trimIndent()
        )

        val (sup) = acu.declaredTypeSignatures()
        val call = acu.firstMethodCall()

        spy.shouldBeOk {
            call.methodType.shouldMatchMethod(
                named = "m",
                declaredIn = sup,
                withFormals = listOf(ts.OBJECT),
                returning = void
            )
        }
    }

    parserTest("#7152 interface-qualified call also considers abstract methods") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            interface I {
                void m(Object o);
                static void m(int x) { }
            }

            class Foo {
                void t(Integer value) {
                    I.m(value); // javac selects m(Object) and reports that it is not static
                    I.m(1);
                }
            }
            """.trimIndent()
        )

        val (i) = acu.declaredTypeSignatures()
        val (abstractCall, staticCall) = acu.methodCalls().toList()

        spy.shouldBeOk {
            abstractCall.methodType.shouldMatchMethod(
                named = "m",
                declaredIn = i,
                withFormals = listOf(ts.OBJECT),
                returning = void
            )
            staticCall.methodType.shouldMatchMethod(
                named = "m",
                declaredIn = i,
                withFormals = listOf(int),
                returning = void
            )
        }
    }

    parserTest("#7152 static method of a superinterface is not a candidate") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            interface J {
                static void s(Object o) { }
            }

            interface K extends J { }

            class Foo implements J {
                void t(Integer value) {
                    K.s(value);
                }
            }
            """.trimIndent()
        )

        spy.shouldHaveNoApplicableMethods(acu.firstMethodCall())
    }

    parserTest("#7152 static interface method is not a member of an implementing class") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            interface J {
                static void s(Object o) { }
            }

            class Sup implements J {
                static void s(int x) { }
            }

            class Foo extends Sup {
                void t(Integer value) {
                    Foo.s(value);
                }
            }
            """.trimIndent()
        )

        val (_, sup) = acu.declaredTypeSignatures()
        val call = acu.firstMethodCall()

        spy.shouldBeOk {
            call.methodType.shouldMatchMethod(
                named = "s",
                declaredIn = sup,
                withFormals = listOf(int),
                returning = void
            )
        }
    }

    parserTest("#7152 static interface method is not a member of the type of an expression qualifier") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            interface J {
                static void s(Object o) { }
            }

            class Impl implements J { }

            class Foo {
                void t(Impl x, Integer value) {
                    x.s(value); // javac: cannot find symbol
                }
            }
            """.trimIndent()
        )

        spy.shouldHaveNoApplicableMethods(acu.firstMethodCall())
    }

    parserTest("#7152 private method of the superclass is not a member of the qualifier type") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class Outer {
                static class Parent {
                    private void m(Object o) { }
                }

                static class Child extends Parent {
                    static void m(int x) { }
                }

                void t(Child child, Integer value) {
                    // Parent.m(Object) is accessible in this nest, but not inherited (JLS 8.2)
                    Child.m(value);
                    child.m(value);
                }
            }
            """.trimIndent()
        )

        val (_, _, child) = acu.declaredTypeSignatures()
        val (typeCall, exprCall) = acu.methodCalls().toList()

        spy.shouldBeOk {
            typeCall.methodType.shouldMatchMethod(
                named = "m",
                declaredIn = child,
                withFormals = listOf(int),
                returning = void
            )
            exprCall.methodType.shouldMatchMethod(
                named = "m",
                declaredIn = child,
                withFormals = listOf(int),
                returning = void
            )
        }
    }

    parserTest("#7152 package-private method of a superclass in another package is not a member of the qualifier type") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            package javasymbols.testdata;

            import javasymbols.testdata.deep.SubclassOfPackagePrivateSuper;

            class Foo extends SubclassOfPackagePrivateSuper {
                static void m(Object o) { }

                void t() {
                    // PackagePrivateSuper.m(long) is accessible here, but not inherited by Foo
                    Foo.m(1);
                }
            }
            """.trimIndent()
        )

        val call = acu.firstMethodCall()

        spy.shouldBeOk {
            call.methodType.shouldMatchMethod(
                named = "m",
                declaredIn = acu.firstTypeSignature(),
                withFormals = listOf(ts.OBJECT),
                returning = void
            )
        }
    }

    parserTest("#7152 inherited instance method is a candidate for a type name qualifier") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class Parent {
                public void m(Object o) { }
            }

            class Child extends Parent {
                static void m(int x) { }

                void t(Integer value) {
                    // javac selects Parent.m(Object) and reports that it is not static
                    Child.m(value);
                }
            }
            """.trimIndent()
        )

        val (parent) = acu.declaredTypeSignatures()
        val call = acu.firstMethodCall()

        spy.shouldBeOk {
            call.methodType.shouldMatchMethod(
                named = "m",
                declaredIn = parent,
                withFormals = listOf(ts.OBJECT),
                returning = void
            )
        }
    }

    parserTest("#7152 protected method of Object is not a member of an interface type") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            interface I { }

            class Foo {
                void t(I i) {
                    i.clone(); // javac: cannot find symbol
                }
            }
            """.trimIndent()
        )

        spy.shouldHaveNoApplicableMethods(acu.firstMethodCall())
    }

    parserTest("#7152 protected method of Object is not in scope in an interface body") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            interface I {
                default void d() {
                    clone(); // javac: cannot find symbol
                }
            }
            """.trimIndent()
        )

        spy.shouldHaveNoApplicableMethods(acu.firstMethodCall())
    }

    parserTest("#7152 public methods of Object and redeclared methods are members of an interface type") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            interface I { }

            interface J {
                Object clone();
            }

            class Foo {
                void t(I i, J j) {
                    i.hashCode();
                    j.clone();
                }
            }
            """.trimIndent()
        )

        val (_, j) = acu.declaredTypeSignatures()
        val (hashCodeCall, cloneCall) = acu.methodCalls().toList()

        spy.shouldBeOk {
            hashCodeCall.methodType.shouldMatchMethod(
                named = "hashCode",
                declaredIn = ts.OBJECT,
                withFormals = emptyList(),
                returning = int
            )
            cloneCall.methodType.shouldMatchMethod(
                named = "clone",
                declaredIn = j,
                withFormals = emptyList(),
                returning = ts.OBJECT
            )
        }
    }

    parserTest("#7152 private method of the superclass is not a member of an intersection bound") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class Outer {
                interface Marker { }

                static class Parent {
                    private void m(Object o) { }
                }

                static class Child extends Parent {
                    static void m(int x) { }
                }

                <T extends Child & Marker> void t(T t, Integer value) {
                    t.m(value);
                }
            }
            """.trimIndent()
        )

        val child = acu.declaredTypeSignatures().first { it.symbol.simpleName == "Child" }
        val call = acu.firstMethodCall()

        spy.shouldBeOk {
            call.methodType.shouldMatchMethod(
                named = "m",
                declaredIn = child,
                withFormals = listOf(int),
                returning = void
            )
        }
    }

    parserTest("#7152 static interface method of a component is not a member of an intersection") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class Outer {
                interface I {
                    static void m(Object o) { }
                }

                static class C {
                    static void m(int x) { }
                }

                <T extends C & I> void t(T t, Integer value) {
                    t.m(value);
                }
            }
            """.trimIndent()
        )

        val declaring = acu.declaredTypeSignatures().first { it.symbol.simpleName == "C" }
        val call = acu.firstMethodCall()

        spy.shouldBeOk {
            call.methodType.shouldMatchMethod(
                named = "m",
                declaredIn = declaring,
                withFormals = listOf(int),
                returning = void
            )
        }
    }

    parserTest("#7152 private method of the class component is not a member of an intersection") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class Outer {
                interface Marker { }

                static class C {
                    private void m(Object o) { }
                    static void m(int x) { }
                }

                <T extends C & Marker> void t(T t, Integer value) {
                    t.m(value);
                }
            }
            """.trimIndent()
        )

        val declaring = acu.declaredTypeSignatures().first { it.symbol.simpleName == "C" }
        val call = acu.firstMethodCall()

        spy.shouldBeOk {
            call.methodType.shouldMatchMethod(
                named = "m",
                declaredIn = declaring,
                withFormals = listOf(int),
                returning = void
            )
        }
    }

    parserTest("#7152 type variable bounded by a type variable has the members of the class bound") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class Outer {
                static class Parent {
                    private void m(Object o) { }
                }

                static class Child extends Parent {
                    static void m(int x) { }
                }

                <T extends Child, U extends T> void t(U u, Integer value) {
                    u.m(value);
                }
            }
            """.trimIndent()
        )

        val declaring = acu.declaredTypeSignatures().first { it.symbol.simpleName == "Child" }
        val call = acu.firstMethodCall()

        spy.shouldBeOk {
            call.methodType.shouldMatchMethod(
                named = "m",
                declaredIn = declaring,
                withFormals = listOf(int),
                returning = void
            )
        }
    }

    parserTest("#7152 private method of the only bound of a type variable is a candidate") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class Outer {
                static class C {
                    private void m(Object o) { }
                    static void m(int x) { }
                }

                <T extends C> void t(T t, Integer value) {
                    // javac selects m(Object) and reports that it has private access
                    t.m(value);
                }
            }
            """.trimIndent()
        )

        val declaring = acu.declaredTypeSignatures().first { it.symbol.simpleName == "C" }
        val call = acu.firstMethodCall()

        spy.shouldBeOk {
            call.methodType.shouldMatchMethod(
                named = "m",
                declaredIn = declaring,
                withFormals = listOf(ts.OBJECT),
                returning = void
            )
        }
    }

    parserTest("#7152 protected method of Object is not a member of an intersection of interfaces") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class Outer {
                interface I {
                    void clone(Object... xs);
                }

                interface J { }

                <T extends I & J> void t(T t) {
                    // javac resolves this to I.clone(Object...)
                    t.clone();
                }

                <T extends Object & I> void o(T t) {
                    t.clone();
                }
            }
            """.trimIndent()
        )

        val i = acu.declaredTypeSignatures().first { it.symbol.simpleName == "I" }
        val (andJ, andObject) = acu.methodCalls().toList()

        spy.shouldBeOk {
            andJ.methodType.shouldMatchMethod(
                named = "clone",
                declaredIn = i,
                withFormals = listOf(ts.OBJECT.toArray()),
                returning = void
            )
            andObject.methodType.shouldMatchMethod(
                named = "clone",
                declaredIn = i,
                withFormals = listOf(ts.OBJECT.toArray()),
                returning = void
            )
        }
    }

    parserTest("#7152 public method of Object is a member of an intersection of interfaces") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class Outer {
                interface I { }

                interface J { }

                <T extends I & J> void t(T t) {
                    t.hashCode();
                }
            }
            """.trimIndent()
        )

        val call = acu.firstMethodCall()

        spy.shouldBeOk {
            call.methodType.shouldMatchMethod(
                named = "hashCode",
                declaredIn = ts.OBJECT,
                withFormals = emptyList(),
                returning = int
            )
        }
    }

    parserTest("#7152 protected instance method is a candidate through a type name only if the type is a subclass") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            package javasymbols.testdata.deep;

            import javasymbols.testdata.ProtectedOverloads;
            import javasymbols.testdata.impls.SubclassOfProtectedOverloads;

            class Sub extends ProtectedOverloads {
                void t(Integer value) {
                    // m(Object) is not accessible through these types (JLS 6.6.2.1), javac selects m(int)
                    ProtectedOverloads.m(value);
                    SubclassOfProtectedOverloads.m(value);
                    // javac selects m(Object) and reports that it is not static
                    Sub.m(value);
                    // the qualifier does not matter for a static method
                    ProtectedOverloads.s(value);
                }
            }
            """.trimIndent()
        )

        val (base, other, sub, static) = acu.methodCalls().toList()

        spy.shouldBeOk {
            val decl = ProtectedOverloads::class.decl
            base.methodType.shouldMatchMethod(named = "m", declaredIn = decl, withFormals = listOf(int), returning = void)
            other.methodType.shouldMatchMethod(named = "m", declaredIn = decl, withFormals = listOf(int), returning = void)
            sub.methodType.shouldMatchMethod(named = "m", declaredIn = decl, withFormals = listOf(ts.OBJECT), returning = void)
            static.methodType.shouldMatchMethod(named = "s", declaredIn = decl, withFormals = listOf(ts.OBJECT), returning = void)
        }
    }

    parserTest("#7152 protected method of Object does not hide a method of the enclosing class in an interface body") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class Outer {
                static void clone(int x) { }

                interface I {
                    default void d() {
                        // Object.clone() is not a member of I, so the search goes on to Outer
                        clone(1);
                    }
                }
            }
            """.trimIndent()
        )

        val call = acu.firstMethodCall()

        spy.shouldBeOk {
            call.methodType.shouldMatchMethod(
                named = "clone",
                declaredIn = acu.firstTypeSignature(),
                withFormals = listOf(int),
                returning = void
            )
        }
    }

    parserTest("#7152 protected instance method is a candidate through a type name in the same package") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            package javasymbols.testdata;

            class SamePackage {
                void t(Integer value) {
                    // javac selects m(Object) and reports that it is not static
                    ProtectedOverloads.m(value);
                }
            }
            """.trimIndent()
        )

        val call = acu.firstMethodCall()

        spy.shouldBeOk {
            call.methodType.shouldMatchMethod(
                named = "m",
                declaredIn = ProtectedOverloads::class.decl,
                withFormals = listOf(ts.OBJECT),
                returning = void
            )
        }
    }

    parserTest("#7152 protected instance method is a candidate through a type name of an enclosing subclass") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            package javasymbols.testdata.deep;

            import javasymbols.testdata.ProtectedOverloads;

            class Nested extends ProtectedOverloads {
                class Inner extends ProtectedOverloads {
                    void u(Integer value) {
                        // Nested is not a subclass of Inner, but Nested encloses this call and is a subclass:
                        // javac selects m(Object) and reports that it is not static
                        Nested.m(value);
                        // javac selects m(int)
                        ProtectedOverloads.m(value);
                    }
                }
            }
            """.trimIndent()
        )

        val (nested, base) = acu.methodCalls().crossFindBoundaries().toList()

        spy.shouldBeOk {
            val decl = ProtectedOverloads::class.decl
            nested.methodType.shouldMatchMethod(named = "m", declaredIn = decl, withFormals = listOf(ts.OBJECT), returning = void)
            base.methodType.shouldMatchMethod(named = "m", declaredIn = decl, withFormals = listOf(int), returning = void)
        }
    }

    parserTest("#7152 protected methods are candidates through a type name in a class nested in a subclass") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            package javasymbols.testdata.deep;

            import javasymbols.testdata.ProtectedOverloads;

            class Nested extends ProtectedOverloads {
                class Plain {
                    void u(Integer value) {
                        // javac selects m(Object) and reports that it is not static
                        Nested.m(value);
                        // javac selects m(int)
                        ProtectedOverloads.m(value);
                        // javac selects s(Object)
                        ProtectedOverloads.s(value);
                    }
                }

                static class StaticPlain {
                    void v(Integer value) {
                        // javac selects s(Object)
                        ProtectedOverloads.s(value);
                    }
                }
            }
            """.trimIndent()
        )

        val (nested, base, static, staticInStatic) = acu.methodCalls().crossFindBoundaries().toList()

        spy.shouldBeOk {
            val decl = ProtectedOverloads::class.decl
            nested.methodType.shouldMatchMethod(named = "m", declaredIn = decl, withFormals = listOf(ts.OBJECT), returning = void)
            base.methodType.shouldMatchMethod(named = "m", declaredIn = decl, withFormals = listOf(int), returning = void)
            static.methodType.shouldMatchMethod(named = "s", declaredIn = decl, withFormals = listOf(ts.OBJECT), returning = void)
            staticInStatic.methodType.shouldMatchMethod(named = "s", declaredIn = decl, withFormals = listOf(ts.OBJECT), returning = void)
        }
    }

    parserTest("#7152 protected methods are not candidates through a type name outside a subclass") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            package javasymbols.testdata.deep;

            import javasymbols.testdata.ProtectedOverloads;

            class Unrelated {
                void t(Integer value) {
                    // javac selects m(int) and s(int)
                    ProtectedOverloads.m(value);
                    ProtectedOverloads.s(value);
                }
            }
            """.trimIndent()
        )

        val (instance, static) = acu.methodCalls().toList()

        spy.shouldBeOk {
            val decl = ProtectedOverloads::class.decl
            instance.methodType.shouldMatchMethod(named = "m", declaredIn = decl, withFormals = listOf(int), returning = void)
            static.methodType.shouldMatchMethod(named = "s", declaredIn = decl, withFormals = listOf(int), returning = void)
        }
    }

    parserTest("#7147 protected method is accessible through a qualifier only if its type is a subclass") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            package javasymbols.testdata.deep;

            import javasymbols.testdata.ProtectedMembers;
            import javasymbols.testdata.impls.SubclassOfProtectedMembers;

            class Sub extends ProtectedMembers {
                Sub() { super(0); }

                void t(ProtectedMembers base, SubclassOfProtectedMembers other, Sub sub, Integer value) {
                    // m(Object) is not accessible through these types (JLS 6.6.2.1), javac selects m(int)
                    base.m(value);
                    other.m(value);
                    // javac selects m(Object)
                    sub.m(value);
                    this.m(value);
                    super.m(value);
                    // the qualifier does not matter for a static method, javac selects s(Object)
                    base.s(value);
                }
            }
            """.trimIndent()
        )

        val (base, other, sub, self, sup, static) = acu.methodCalls().toList()

        spy.shouldBeOk {
            val decl = ProtectedMembers::class.decl
            base.methodType.shouldMatchMethod(named = "m", declaredIn = decl, withFormals = listOf(int), returning = void)
            other.methodType.shouldMatchMethod(named = "m", declaredIn = decl, withFormals = listOf(int), returning = void)
            sub.methodType.shouldMatchMethod(named = "m", declaredIn = decl, withFormals = listOf(ts.OBJECT), returning = void)
            self.methodType.shouldMatchMethod(named = "m", declaredIn = decl, withFormals = listOf(ts.OBJECT), returning = void)
            sup.methodType.shouldMatchMethod(named = "m", declaredIn = decl, withFormals = listOf(ts.OBJECT), returning = void)
            static.methodType.shouldMatchMethod(named = "s", declaredIn = decl, withFormals = listOf(ts.OBJECT), returning = void)
        }
    }

    parserTest("#7147 protected method is accessible in the classes nested in a subclass") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            package javasymbols.testdata.deep;

            import javasymbols.testdata.ProtectedMembers;

            class Sub extends ProtectedMembers {
                Sub() { super(0); }

                class Inner {
                    void t(Sub sub, ProtectedMembers base, Integer value) {
                        // javac selects m(Object)
                        sub.m(value);
                        Sub.this.m(value);
                        Sub.super.m(value);
                        // javac selects m(int)
                        base.m(value);
                    }
                }

                static class Nested {
                    void t(Sub sub, Integer value) {
                        // javac selects m(Object)
                        sub.m(value);
                    }
                }

                interface NestedInterface {
                    default void t(Sub sub, Integer value) {
                        // the interface is not a subclass, but it is in the body of Sub: javac selects m(Object)
                        sub.m(value);
                    }
                }

                class InnerSub extends ProtectedMembers {
                    InnerSub() { super(0); }

                    void t(Sub sub, Integer value) {
                        // Sub is not a subclass of InnerSub, but the search goes on to Sub: javac selects m(Object)
                        sub.m(value);
                    }
                }

                void t(Sub sub, Integer value) {
                    new Runnable() {
                        public void run() {
                            // javac selects m(Object)
                            sub.m(value);
                        }
                    };
                }
            }
            """.trimIndent()
        )

        val (inner, innerThis, innerSuper, innerBase, nested, nestedInterface, innerSub, anonymous) =
            acu.methodCalls().crossFindBoundaries().toList()

        spy.shouldBeOk {
            val decl = ProtectedMembers::class.decl
            inner.methodType.shouldMatchMethod(named = "m", declaredIn = decl, withFormals = listOf(ts.OBJECT), returning = void)
            innerThis.methodType.shouldMatchMethod(named = "m", declaredIn = decl, withFormals = listOf(ts.OBJECT), returning = void)
            innerSuper.methodType.shouldMatchMethod(named = "m", declaredIn = decl, withFormals = listOf(ts.OBJECT), returning = void)
            innerBase.methodType.shouldMatchMethod(named = "m", declaredIn = decl, withFormals = listOf(int), returning = void)
            nested.methodType.shouldMatchMethod(named = "m", declaredIn = decl, withFormals = listOf(ts.OBJECT), returning = void)
            nestedInterface.methodType.shouldMatchMethod(named = "m", declaredIn = decl, withFormals = listOf(ts.OBJECT), returning = void)
            innerSub.methodType.shouldMatchMethod(named = "m", declaredIn = decl, withFormals = listOf(ts.OBJECT), returning = void)
            anonymous.methodType.shouldMatchMethod(named = "m", declaredIn = decl, withFormals = listOf(ts.OBJECT), returning = void)
        }
    }

    parserTest("#7147 the qualifier type must be a subclass of the class whose body contains the call") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            package javasymbols.testdata.deep;

            import javasymbols.testdata.ProtectedMembers;

            class Sub extends ProtectedMembers {
                Sub() { super(0); }
            }

            class Unrelated {
                void t(ProtectedMembers base, Integer value) {
                    // Unrelated is not a subclass: javac selects s(int)
                    base.s(value);
                    new ProtectedMembers(0) {
                        void u(ProtectedMembers other) {
                            // ProtectedMembers is not a subclass of the anonymous class: javac selects m(int)
                            other.m(value);
                            // javac selects m(Object)
                            this.m(value);
                        }
                    };
                }

                static class Nested extends ProtectedMembers {
                    Nested() { super(0); }

                    void t(Sub sub, Nested nested, Integer value) {
                        // Sub is not a subclass of Nested, and Unrelated is not a subclass: javac selects m(int)
                        sub.m(value);
                        // javac selects m(Object)
                        nested.m(value);
                    }

                    class Deeper {
                        void t(Nested nested, Integer value) {
                            // Deeper is not a subclass, but Nested is: javac selects m(Object)
                            nested.m(value);
                        }
                    }
                }
            }
            """.trimIndent()
        )

        val (static, anonOther, anonThis, sub, nested, deeper) = acu.methodCalls().crossFindBoundaries().toList()

        spy.shouldBeOk {
            val decl = ProtectedMembers::class.decl
            static.methodType.shouldMatchMethod(named = "s", declaredIn = decl, withFormals = listOf(int), returning = void)
            anonOther.methodType.shouldMatchMethod(named = "m", declaredIn = decl, withFormals = listOf(int), returning = void)
            anonThis.methodType.shouldMatchMethod(named = "m", declaredIn = decl, withFormals = listOf(ts.OBJECT), returning = void)
            sub.methodType.shouldMatchMethod(named = "m", declaredIn = decl, withFormals = listOf(int), returning = void)
            nested.methodType.shouldMatchMethod(named = "m", declaredIn = decl, withFormals = listOf(ts.OBJECT), returning = void)
            deeper.methodType.shouldMatchMethod(named = "m", declaredIn = decl, withFormals = listOf(ts.OBJECT), returning = void)
        }
    }

    parserTest("#7147 protected method is accessible through a type variable only if its bound is a subclass") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            package javasymbols.testdata.deep;

            import javasymbols.testdata.ProtectedMembers;

            class Sub extends ProtectedMembers {
                Sub() { super(0); }

                <T extends Sub & Runnable, U extends ProtectedMembers & Runnable> void t(T t, U u, Integer value) {
                    // javac selects m(Object)
                    t.m(value);
                    // javac selects m(int)
                    u.m(value);
                }
            }
            """.trimIndent()
        )

        val (t, u) = acu.methodCalls().toList()

        spy.shouldBeOk {
            val decl = ProtectedMembers::class.decl
            t.methodType.shouldMatchMethod(named = "m", declaredIn = decl, withFormals = listOf(ts.OBJECT), returning = void)
            u.methodType.shouldMatchMethod(named = "m", declaredIn = decl, withFormals = listOf(int), returning = void)
        }
    }

    parserTest("#7147 protected method of Object is not accessible through an Object qualifier or in an interface") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class Sub {
                void t(Object object, Sub sub) throws CloneNotSupportedException {
                    // javac reports that clone() has protected access in Object
                    object.clone();
                    // javac selects Object.clone()
                    sub.clone();
                }
            }

            interface I {
                default void t(Impl impl) throws CloneNotSupportedException {
                    // an interface is not a subclass of Object (JLS 6.6.2): javac reports
                    // that clone() has protected access in Object
                    impl.clone();
                }
            }

            class Impl implements I { }
            """.trimIndent()
        )

        val (obj, sub, impl) = acu.methodCalls().toList()

        spy.shouldHaveNoApplicableMethods(obj)
        spy.shouldHaveNoApplicableMethods(impl)
        sub.methodType.shouldMatchMethod(named = "clone", declaredIn = acu.typeSystem.OBJECT, withFormals = emptyList(), returning = acu.typeSystem.OBJECT)
    }

    parserTest("#7147 protected method of Object is not accessible through an intersection bound") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class Test {
                <T extends K & J> void t(T t) {
                    // Object.clone() is not accessible through T (JLS 6.6.2.1): javac selects K.clone(Object...)
                    t.clone();
                }
            }

            class K {
                public void clone(Object... args) { }
            }

            interface J { }
            """.trimIndent()
        )

        val call = acu.firstMethodCall()

        spy.shouldBeOk {
            call.methodType.shouldMatchMethod(
                named = "clone",
                declaredIn = acu.declaredTypeSignatures()[1],
                withFormals = listOf(ts.OBJECT.toArray()),
                returning = void
            )
        }
    }

    parserTest("#7147 protected method is accessible through a qualifier whose type or superclass is unresolved") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class Sub {
                void t(Unknown unknown, Partial partial) throws CloneNotSupportedException {
                    // with an incomplete auxclasspath, these types may be subclasses of Sub,
                    // so Object.clone() stays accessible
                    unknown.clone();
                    partial.clone();
                }
            }

            class Partial extends Missing { }
            """.trimIndent()
        )

        val (unknown, partial) = acu.methodCalls().toList()

        spy.shouldBeOk {
            unknown.methodType.shouldMatchMethod(named = "clone", declaredIn = ts.OBJECT, withFormals = emptyList(), returning = ts.OBJECT)
            partial.methodType.shouldMatchMethod(named = "clone", declaredIn = ts.OBJECT, withFormals = emptyList(), returning = ts.OBJECT)
        }
    }

    parserTest("#7147 private and package-private methods and constructors of other classes stay inaccessible") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            package javasymbols.testdata.deep;

            import javasymbols.testdata.PackagePrivateSuper;

            class A {
                private A(int x) { }
                A(Object o) { }
                private void m(int x) { }
                void m(Object o) { }
            }

            class B {
                void t(A a, PackagePrivateSuper p) {
                    // A(int) and m(int) are private in another nest: javac selects A(Object) and m(Object)
                    new A(1);
                    a.m(1);
                    // javac reports that m(long) is not public in PackagePrivateSuper
                    p.m(1L);
                }
            }
            """.trimIndent()
        )

        val ctor = acu.firstCtorCall()
        val (call, packagePrivate) = acu.methodCalls().toList()

        spy.shouldHaveNoApplicableMethods(packagePrivate)
        call.withTypeDsl {
            val t_A = acu.firstTypeSignature()
            ctor.methodType.shouldMatchMethod(named = "new", declaredIn = t_A, withFormals = listOf(ts.OBJECT), returning = t_A)
            call.methodType.shouldMatchMethod(named = "m", declaredIn = t_A, withFormals = listOf(ts.OBJECT), returning = void)
        }
    }

    parserTest("#7147 conflicting access modifiers do not stop the resolution of new expressions and qualified calls") {
        val acu = parser.parse(
            """
            class A {
                public private A(int x) { }
                public A(String s) { }
                public private void m(int x) { }
                public void m(String s) { }
            }

            class B {
                void t(A a) {
                    // javac rejects the declarations of A(int) and m(int)
                    new A("");
                    a.m("");
                }
            }
            """.trimIndent()
        )

        val ctor = acu.firstCtorCall()
        val call = acu.firstMethodCall()

        call.withTypeDsl {
            val t_A = acu.firstTypeSignature()
            ctor.methodType.shouldMatchMethod(named = "new", declaredIn = t_A, withFormals = listOf(ts.STRING), returning = t_A)
            call.methodType.shouldMatchMethod(named = "m", declaredIn = t_A, withFormals = listOf(ts.STRING), returning = void)
        }
    }
})
