/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldMatchN
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.types.JTypeMirror
import net.sourceforge.pmd.lang.java.types.TypeDslOf
import net.sourceforge.pmd.lang.java.types.testTypeSystem
import net.sourceforge.pmd.lang.java.types.testdata.Overloads
import net.sourceforge.pmd.lang.java.types.typeDsl

class OverloadResolutionTest : ProcessorTestSpec({

    val t_SomeEnum = "net.sourceforge.pmd.lang.java.types.testdata.SomeEnum"
    val t_Overloads = "net.sourceforge.pmd.lang.java.types.testdata.Overloads"



    test("Test conversion in compat tests") {

        fun assertConvertible(types: Pair<JTypeMirror, JTypeMirror>, pos: Boolean, canBox: Boolean = true) {
            val (t, s) = types
            val res = Infer.isConvertible(t, s, canBox)
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

            "of($t_SomeEnum.FOO)" should parseAs {
                methodCall("of") {
                    it.methodType.toString() shouldBe "$t_Overloads.<E extends java.lang.Enum<E>> of($t_SomeEnum) -> java.util.EnumSet<$t_SomeEnum>"
                    it.typeMirror.toString() shouldBe "java.util.EnumSet<$t_SomeEnum>"

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
                    it.methodType.toString() shouldBe "$t_Overloads.<E extends java.lang.Enum<E>> of(/*unresolved*/) -> java.util.EnumSet</*unresolved*/>"
                    it.typeMirror.toString() shouldBe "java.util.EnumSet</*unresolved*/>"

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
            "of($t_SomeEnum.FOO, $t_SomeEnum.BAR)" should parseAs {
                methodCall("of") {
                    it.typeMirror.toString() shouldBe "java.util.EnumSet<$t_SomeEnum>"
                    it.methodType.toString() shouldBe "$t_Overloads.<E extends java.lang.Enum<E>> of($t_SomeEnum, $t_SomeEnum) -> java.util.EnumSet<$t_SomeEnum>"

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
            "of($t_SomeEnum.FOO, $t_SomeEnum.BAR,  $t_SomeEnum.BAR)" should parseAs {
                methodCall("of") {
                    it.typeMirror.toString() shouldBe "java.util.EnumSet<$t_SomeEnum>"
                    it.methodType.toString() shouldBe "$t_Overloads.<E extends java.lang.Enum<E>> of($t_SomeEnum, $t_SomeEnum...) -> java.util.EnumSet<$t_SomeEnum>"

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

            "of($t_SomeEnum.FOO, new $t_SomeEnum[]{$t_SomeEnum.BAR,  $t_SomeEnum.BAR})" should parseAs {
                methodCall("of") {
                    it.typeMirror.toString() shouldBe "java.util.EnumSet<$t_SomeEnum>"
                    it.methodType.toString() shouldBe "$t_Overloads.<E extends java.lang.Enum<E>> of($t_SomeEnum, $t_SomeEnum...) -> java.util.EnumSet<$t_SomeEnum>"

                    it::getArguments shouldBe child {
                        fieldAccess("FOO")
                        child<ASTArrayAllocation> {
                            it.typeMirror.toString() shouldBe "$t_SomeEnum[]"
                            unspecifiedChild()
                            child<ASTArrayInitializer> {
                                it.typeMirror.toString() shouldBe "$t_SomeEnum[]"
                                fieldAccess("BAR") {
                                    it.typeMirror.toString() shouldBe t_SomeEnum
                                    typeExpr {
                                        qualClassType(t_SomeEnum)
                                    }
                                }
                                fieldAccess("BAR") {
                                    it.typeMirror.toString() shouldBe t_SomeEnum
                                    typeExpr {
                                        qualClassType(t_SomeEnum)
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
                it::getTypeMirror shouldBe with (it.typeDsl) { ts.INT }
                it.methodType.symbol shouldBe fooClass.symbol

                argList {
                    variableAccess("k") {
                        it::getTypeMirror shouldBe with (it.typeDsl) { Class::class.raw }
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
})
