/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldBeSingleton
import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.ast.test.shouldMatchN
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.types.*
import net.sourceforge.pmd.lang.java.types.testdata.TypeInferenceTestCases
import java.util.*
import java.util.function.ToIntFunction

/**
 * @author Clément Fournier
 */
class CaptureInferenceTest : ProcessorTestSpec({

    parserTest("Test capture incompatibility recovery") {

        asIfIn(TypeInferenceTestCases::class.java)

        inContext(TypeBodyParsingCtx) {

            val normalizer = CaptureNormalizer()

            val getCall = doParse("""
                void something(List<?> l) {
                    l.set(1, l.get(0)); // captured, fails
                }
            """)
                    .descendants(ASTMethodCall::class.java).get(1)!!


            normalizer.normalizeCaptures(getCall.typeMirror.toString()) shouldBe "capture#1 of ?"
            normalizer.normalizeCaptures(getCall.methodType.toString()) shouldBe "java.util.List<capture#1 of ?>.get(int) -> capture#1 of ?"

            val setCall = getCall.ancestors(ASTMethodCall::class.java).first()!!

            // we still get a type
            normalizer.normalizeCaptures(setCall.methodType.toString()) shouldBe "java.util.List<capture#2 of ?>.set(int, capture#2 of ?) -> capture#2 of ?"
        }
    }


    parserTest("Test lower wildcard compatibility") {


        val acu = parser.parse("""
           package java.lang;

           import java.util.Iterator;
           import java.util.function.Consumer;

           public interface Iterable<T> {
               Iterator<T> iterator();

               default void forEach(Consumer<? super T> action) {
                   for (T t : this) {
                       action.accept(t);
                   }
               }
           }

        """.trimIndent())

        val tVar = acu.descendants(ASTTypeParameter::class.java).first()!!.symbol.typeMirror

        val call = acu.descendants(ASTMethodCall::class.java).first()!!

        call.shouldMatchN {
            methodCall("accept") {
                it::getTypeMirror shouldBe with(it.typeDsl) { ts.NO_TYPE }

                variableAccess("action") {}
                argList {
                    variableAccess("t") {
                        it.typeMirror shouldBe tVar
                    }
                }
            }
        }
    }

    parserTest("Test method ref on captured thing") {

        val acu = parser.parse("""
           import java.util.List;
           import java.util.ArrayList;
           import java.util.Comparator;

           class Scratch {
               private List<? extends String> sortIt(final List<? extends String> stats) {
                    final List<? extends String> statList = new ArrayList<>(stats);
                    statList.sort(Comparator.comparingInt(Object::hashCode));
                    return statList;
               }
           }

        """.trimIndent())

        val call = acu.descendants(ASTMethodCall::class.java).first()!!

        call.shouldMatchN {
            methodCall("sort") {
                it::getTypeMirror shouldBe with(it.typeDsl) { ts.NO_TYPE }

                variableAccess("statList") {}
                argList {
                    var capture: JTypeVar? = null
                    methodCall("comparingInt") {
                        with(it.typeDsl) {
                            // eg. java.util.Comparator<capture#45 of ? extends java.lang.String>
                            val ret = it.typeMirror.shouldBeA<JClassType> {
                                it.symbol shouldBe gen.t_Comparator.symbol
                                it.typeArgs.shouldBeSingleton {
                                    capture = it.shouldBeCaptureOf(`?` extends gen.t_String)
                                }
                            }

                            it.methodType.shouldMatchMethod(
                                    named = "comparingInt",
                                    declaredIn = gen.t_Comparator,
                                    withFormals = listOf(ToIntFunction::class[`?` `super` capture!!]),
                                    returning = ret
                            )
                        }


                        typeExpr {
                            classType("Comparator")
                        }

                        argList {
                            methodRef("hashCode") {
                                typeExpr {
                                    classType("Object")
                                }

                                with(it.typeDsl) {
                                    it.referencedMethod shouldBe ts.OBJECT.getMethodsByName("hashCode").single()
                                    it.typeMirror shouldBe ToIntFunction::class[capture!!]
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    parserTest("Test independent captures merging") {

        val acu = parser.parse("""
           import java.util.*;

           class Scratch {

               private <T> Spliterator<T> spliterator0(Collection<? extends T> collection, int characteristics) {
                   return null;
               }

               public static <T> Spliterator<T> spliterator(Collection<? extends T> c,
                                                            int characteristics) {
                   return spliterator0(Objects.requireNonNull(c), characteristics);
               }
           }

        """.trimIndent())

        val t_Scratch = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }.single()
        val tvar = acu.descendants(ASTTypeParameter::class.java).toList { it.typeMirror }[1]
        val call = acu.descendants(ASTMethodCall::class.java).first()!!

        call.shouldMatchN {
            methodCall("spliterator0") {
                with (it.typeDsl) {
                    it.methodType.shouldMatchMethod(
                            named = "spliterator0",
                            declaredIn = t_Scratch,
                            withFormals = listOf(gen.t_Collection[`?` extends tvar], int),
                            returning = Spliterator::class[tvar]
                    )
                }

                argList {
                    methodCall("requireNonNull") {
                        with (it.typeDsl) {
                            it.methodType.shouldMatchMethod(named = "requireNonNull", declaredIn = Objects::class.raw)
                        }

                        it::getQualifier shouldBe unspecifiedChild()

                        argList {
                            variableAccess("c") {
                                with (it.typeDsl) {
                                    it.typeMirror.shouldBeA<JClassType>{
                                        it.typeArgs[0].shouldBeCaptureOf(`?` extends tvar)
                                    }
                                }
                            }

                        }
                    }

                    variableAccess("characteristics")
                }
            }
        }
    }

})

/**
 * Captured variables have unique identifiers, that should
 * be normalized when writing tests.
 */
class CaptureNormalizer {

    var curNum = 0

    val map = mutableMapOf<Int, String>()

    val r = Regex("(?<=capture#)-?\\d+")

    fun normalizeCaptures(s: String): String {

        return r.replace(s) {
            val num = it.value.toInt()

            if (num in map) map[num]!!
            else {
                curNum++
                map[num] = curNum.toString()
                curNum.toString()
            }
        }

    }

}


