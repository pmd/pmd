/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.ast.test.shouldMatchN
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol
import net.sourceforge.pmd.lang.java.symbols.JFormalParamSymbol
import net.sourceforge.pmd.lang.java.types.*
import java.util.function.Supplier

/**
 *
 */
class SpecialMethodsTest : ProcessorTestSpec({


    parserTest("Test getClass special type") {


        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            import java.util.function.Function;
            import java.util.function.Supplier;

            class Scratch<K> {

                <T> T sup(Supplier<Class<T>> t) { return null; }
                <T> T id(Function<T, Class<T>> t) { return null; }

                {
                    Scratch<K> k = this.sup(this::getClass);
                    Scratch<K> k2 = this.id(Scratch<K>::getClass);
                    Scratch raw = this.id(Scratch::getClass); //error

                    k.getClass();
                }
            }

        """.trimIndent())

        val t_Scratch = acu.descendants(ASTAnyTypeDeclaration::class.java).firstOrThrow().typeMirror

        val (k, k2, raw, call) = acu.descendants(ASTMethodCall::class.java).toList()

        doTest("Test this::getClass") {
            spy.shouldBeOk {
                k.shouldMatchN {
                    methodCall("sup") {
                        thisExpr { it shouldHaveType t_Scratch; null }
                        argList {
                            methodRef("getClass") {
                                thisExpr()
                                it shouldHaveType Supplier::class[Class::class[captureMatcher(`?` extends t_Scratch.erasure)]]
                            }
                        }
                    }
                }
            }
            spy.resetInteractions()
        }

        doTest("Test Scratch<K>::getClass") {
            spy.shouldBeOk {
                k2.arguments[0].shouldMatchN {
                    methodRef("getClass") {
                        typeExpr {
                            classType("Scratch")
                        }

                        it shouldHaveType let {
                            val capture = captureMatcher(`?` extends t_Scratch.erasure)
                            // same capture in both params
                            java.util.function.Function::class[capture, Class::class[capture]]
                        }
                    }
                }
            }
            spy.resetInteractions()
        }

        doTest("Test method call") {
            spy.shouldBeOk {
                call shouldHaveType Class::class[`?` extends t_Scratch.erasure]
            }
        }
    }

    parserTest("Test enum methods") {


        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            import java.util.Arrays;

            enum Foo {
                ;

                {
                    Arrays.stream(values());
                }
            }

        """.trimIndent()
        )

        val t_Foo = acu.descendants(ASTAnyTypeDeclaration::class.java).firstOrThrow().typeMirror

        val streamCall = acu.descendants(ASTMethodCall::class.java).firstOrThrow()

        spy.shouldBeOk {
            streamCall shouldHaveType gen.t_Stream[t_Foo]
            streamCall.arguments[0] shouldHaveType t_Foo.toArray()
        }
    }

    parserTest("getClass in invocation ctx, unchecked conversion") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""

            class Scratch {
                public static <T,U> T[] copyOf(U[] original, Class<? extends T[]> newType) {
                    return null;
                }


                public static <T, E> T[] doCopy(T[] a) {
                    E[] elements = null;
                    return (T[]) copyOf(elements, a.getClass());
                }
            }

        """.trimIndent())

        val t_Scratch = acu.descendants(ASTAnyTypeDeclaration::class.java).firstOrThrow().typeMirror

        val call = acu.descendants(ASTMethodCall::class.java).firstOrThrow()

        spy.shouldBeOk {
            call.shouldMatchN {
                methodCall("copyOf") {
                    it shouldHaveType ts.OBJECT.toArray()

                    argList {
                        variableAccess("elements")

                        methodCall("getClass") {
                            it shouldHaveType Class::class[`?` extends ts.OBJECT.toArray()]

                            variableAccess("a")

                            argList(0)
                        }
                    }
                }
            }
        }
    }


    parserTest("Record ctor formal param reference") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""

           record Foo(int comp) {
                Foo {
                    comp = comp - 1;
                }
           }

        """.trimIndent())

        val (compLhs, compRhs) = acu.descendants(ASTVariableAccess::class.java).toList()
        val id = acu.varId("comp")

        spy.shouldBeOk {
            compLhs.referencedSym.shouldBeA<JFormalParamSymbol> {
                it.tryGetNode() shouldBe id
                it.declaringSymbol.shouldBeA<JConstructorSymbol>()
            }

            // same spec
            compRhs.referencedSym.shouldBeA<JFormalParamSymbol> {
                it.tryGetNode() shouldBe id
                it.declaringSymbol.shouldBeA<JConstructorSymbol>()
            }
        }
    }


})
