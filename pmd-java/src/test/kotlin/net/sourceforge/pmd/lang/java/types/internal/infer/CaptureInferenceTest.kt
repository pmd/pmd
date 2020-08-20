/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldMatchN
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.types.*
import java.util.*
import java.util.function.Supplier
import java.util.function.ToIntFunction
import java.util.stream.Collector
import java.util.stream.Collectors

/**
 * @author Clément Fournier
 */
class CaptureInferenceTest : ProcessorTestSpec({

    parserTest("Test capture incompatibility recovery") {

        otherImports += "java.util.List"

        inContext(TypeBodyParsingCtx) {

            val getCall = doParse("""
                void something(List<?> l) {
                    l.set(1, l.get(0)); // captured, fails
                }
            """).descendants(ASTMethodCall::class.java).get(1)!!


            // todo now we don't recover, we get UNRESOLVED
            //  this is related to the commented out code in TypeOps#typeArgsContains
            //  I think current behavior is best for now
            // normalizer.normalizeCaptures(getCall.typeMirror.toString()) shouldBe "capture#1 of ?"
            with (getCall.typeDsl) {
                val capture1 = captureMatcher(`?`)
                getCall.methodType.shouldMatchMethod(
                        named = "get",
                        declaredIn = gen.t_List[capture1],
                        withFormals = listOf(int),
                        returning = capture1
                )
            }

            val setCall = getCall.ancestors(ASTMethodCall::class.java).first()!!

            with (setCall.typeDsl) {
                setCall.methodType shouldBe ts.UNRESOLVED_METHOD
                // there is also no fallback for this anymore
                // val capture2 = captureMatcher(`?`)
                // setCall.methodType.shouldMatchMethod(
                //         named = "set",
                //         declaredIn = gen.t_List[capture2],
                //         withFormals = listOf(int, capture2),
                //         returning = capture2
                // )
            }
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
                    methodCall("comparingInt") {
                        // eg. java.util.Comparator<capture#45 of ? extends java.lang.String>
                        val captureOfString: JTypeVar

                        with(it.typeDsl) {
                            captureOfString = captureMatcher(`?` extends gen.t_String)

                            it.typeMirror shouldBe gen.t_Comparator[captureOfString]

                            it.methodType.shouldMatchMethod(
                                    named = "comparingInt",
                                    declaredIn = gen.t_Comparator,
                                    withFormals = listOf(ToIntFunction::class[`?` `super` captureOfString]),
                                    returning = gen.t_Comparator[captureOfString]
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
                                    it.typeMirror shouldBe ToIntFunction::class[captureOfString]
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

                        skipQualifier()

                        argList {
                            variableAccess("c") {
                                with(it.typeDsl) {
                                    gen.t_Collection[captureMatcher(`?` extends tvar)]
                                }
                            }

                        }
                    }

                    variableAccess("characteristics")
                }
            }
        }
    }


    parserTest("Problem with GLB of several capture variables") {

        val acu = parser.parse("""
            import java.util.HashMap;
            import java.util.Map;
            import java.util.function.Function;
            import java.util.stream.Collector;
            import java.util.stream.Collectors;

            class Scratch {

                public static <T, K, A, D>
                Collector<T, ?, Map<K, D>> groupingBy(Function<? super T, ? extends K> classifier,
                                                      Collector<? super T, A, D> downstream) {
                    return Collectors.groupingBy(classifier, HashMap::new, downstream);
                }
            }


        """.trimIndent())

        /* Signature of the other groupingBy:

        public static <T, K, D, A, M extends Map<K, D>>

            Collector<T, ?, M> groupingBy(Function<? super T, ? extends K> classifier,
                                          Supplier<M> mapFactory,
                                          Collector<? super T, A, D> downstream)
         */

        val (tvar, kvar, avar, dvar) = acu.descendants(ASTTypeParameter::class.java).toList { it.typeMirror }
        val call = acu.descendants(ASTMethodCall::class.java).first()!!

        call.shouldMatchN {
            methodCall("groupingBy") {
                with(it.typeDsl) {
                    it.methodType.shouldMatchMethod(
                            named = "groupingBy",
                            declaredIn = Collectors::class.raw,
                            withFormals = listOf(
                                    gen.t_Function[`?` `super` tvar, `?` extends kvar],
                                    Supplier::class[gen.t_Map[kvar, dvar]],
                                    Collector::class[`?` `super` tvar, avar, dvar]
                            ),
                            returning =  Collector::class[tvar, `?`, gen.t_Map[kvar, dvar]]
                    )
                }

                skipQualifier()

                argList {
                    variableAccess("classifier")

                    constructorRef {
                        // HMM this should be HashMap<K, D>
                        typeExpr {
                            classType("HashMap")
                        }
                    }

                    variableAccess("downstream")
                }
            }
        }
    }

    parserTest("Weird capture bug") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
                """

import java.util.Arrays;

interface NodeStream<T> {

    static <T> NodeStream<T> union(NodeStream<? extends T>... streams) {
        return union(Arrays.asList(streams));
    }

    static <T> NodeStream<T> union(Iterable<? extends NodeStream<? extends T>> streams) {
        return null;
    }

}

                """.trimIndent()
        )

        val (_, unionOfIter) = acu.methodDeclarations().toList { it.sig }

        spy.shouldBeOk {
            acu.firstMethodCall().methodType.shouldBeSomeInstantiationOf(unionOfIter)
        }
    }
})


