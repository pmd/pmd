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
import net.sourceforge.pmd.lang.java.types.captureMatcher
import net.sourceforge.pmd.lang.java.types.parseWithTypeInferenceSpy
import net.sourceforge.pmd.lang.java.types.typeDsl
import java.util.function.Supplier

/**
 *
 */
class SpecialMethodsTest : ProcessorTestSpec({

    val jutil = "java.util"
    val juf = "$jutil.function"
    val justream = "$jutil.stream"
    val jlang = "java.lang"


    parserTest("Test getClass special type") {


        val acu = parser.parse("""
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
            k.shouldMatchN {
                methodCall("sup") {
                    thisExpr { it.typeMirror shouldBe t_Scratch; null }
                    argList {
                        methodRef("getClass") {
                            thisExpr()

                            it.typeMirror shouldBe with(it.typeDsl) {
                                Supplier::class[Class::class[captureMatcher(`?` extends t_Scratch.erasure)]]
                            }
                        }
                    }
                }
            }
        }

        doTest("Test Scratch<K>::getClass") {
            k2.shouldMatchN {
                methodCall("id") {
                    thisExpr()
                    argList {
                        methodRef("getClass") {
                            typeExpr {
                                classType("Scratch")
                            }


                            it.typeMirror shouldBe with(it.typeDsl) {
                                val capture = captureMatcher(`?` extends t_Scratch.erasure)
                                // same capture in both params
                                java.util.function.Function::class[capture, Class::class[capture]]
                            }
                        }
                    }
                }
            }
        }

        doTest("Test method call") {
            call.shouldMatchN {
                methodCall("getClass") {

                    it::getTypeMirror shouldBe with(it.typeDsl) {
                        Class::class[`?` extends t_Scratch.erasure]
                    }

                    variableAccess("k")
                    argList {}
                }
            }
        }
    }

    parserTest("Test enum methods") {


        val acu = parser.parse("""
            import java.util.Arrays;

            enum Foo {
                ;

                {
                    Arrays.stream(values());
                }
            }

        """.trimIndent())

        val t_Foo = acu.descendants(ASTAnyTypeDeclaration::class.java).firstOrThrow().typeMirror

        val streamCall = acu.descendants(ASTMethodCall::class.java).firstOrThrow()

        streamCall.shouldMatchN {
            methodCall("stream") {
                it.typeMirror shouldBe with(it.typeDsl) {
                    gen.t_Stream[t_Foo]
                }

                skipQualifier()

                argList {
                    methodCall("values") {
                        it.typeMirror shouldBe with(it.typeDsl) {
                            t_Foo.toArray()
                        }

                        argList(0)
                    }
                }
            }
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
                    it.typeMirror shouldBe ts.OBJECT.toArray()

                    argList {
                        variableAccess("elements")

                        methodCall("getClass") {
                            it.typeMirror shouldBe Class::class[`?` extends ts.OBJECT.toArray()]

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

        spy.shouldBeOk {
            compLhs.referencedSym.shouldBeA<JFormalParamSymbol> {
                it.tryGetNode() shouldBe null // this could be controversial
                it.declaringSymbol.shouldBeA<JConstructorSymbol>()
            }

            // same spec
            compRhs.referencedSym.shouldBeA<JFormalParamSymbol> {
                it.tryGetNode() shouldBe null
                it.declaringSymbol.shouldBeA<JConstructorSymbol>()
            }
        }
    }


})
