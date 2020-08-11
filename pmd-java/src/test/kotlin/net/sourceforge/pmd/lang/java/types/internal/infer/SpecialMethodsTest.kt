/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldMatchN
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.types.typeDsl
import java.util.*

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

        val normalizer = CaptureNormalizer()

        doTest("Test this::getClass") {
            k.shouldMatchN {
                methodCall("sup") {
                    thisExpr { it.typeMirror shouldBe t_Scratch; null }
                    argList {
                        methodRef("getClass") {
                            thisExpr()

                            normalizer.normalizeCaptures(it.typeMirror.toString())
                                    .shouldBe("$juf.Supplier<$jlang.Class<capture#1 of ? extends ${t_Scratch.erasure}>>")
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

                            normalizer.normalizeCaptures(it.typeMirror.toString())
                                    .shouldBe("$juf.Function<capture#2 of ? extends Scratch, $jlang.Class<capture#2 of ? extends Scratch>>")
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

                it::getQualifier shouldBe unspecifiedChild()

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


})
