/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldMatchN
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
        }
    }

    parserTest("Test strict overload") {

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

    parserTest("Test partially unresolved") {

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

    parserTest("Test strict overload 2 args") {

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
    parserTest("Test varargs overload") {

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

    parserTest("Test varargs overload with array") {

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


        val acu = parser.parse("""
           class Scratch {
                int foo(Class<?> k) {} // this is selected
                void foo(Object o) {}
                {
                    Class k = Scratch.class; // raw
                    foo(k);
                }
           }

        """.trimIndent())

        val (fooClass) = acu.descendants(ASTMethodDeclaration::class.java).toList()

        val call = acu.descendants(ASTMethodCall::class.java).first()!!

        call.shouldMatchN {
            methodCall("foo") {
                it shouldHaveType it.typeSystem.INT
                it.methodType.symbol shouldBe fooClass.symbol

                argList {
                    variableAccess("k") {
                        it shouldHaveType with (it.typeDsl) { Class::class.raw }
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
            """)

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
                    // Integer is more specific than Long because int -> Integer
                    // only involves boxing, while int -> Long needs widening and
                    // then boxing.
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
    parserTest("Two overloads with boxed types, widening required, ambiguous") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class Static {
                static {
                    // ambiguous: 1 is int, and neither Double nor Long is more
                    // specific because they both involve boxing + widening
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
        spy.shouldBeAmbiguous(call)
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
        """.trimIndent())

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


})
