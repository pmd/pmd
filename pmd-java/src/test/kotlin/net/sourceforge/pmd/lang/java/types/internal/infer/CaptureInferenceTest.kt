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

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
            class Archive {
                void something(java.util.List<?> l) {
                    l.set(1, l.get(0)); // captured, fails
                }
            }
        """.trimIndent())

        val setCall = acu.firstMethodCall()
        val getCall = setCall.arguments[1] as ASTMethodCall

        spy.shouldHaveMissingCtDecl(setCall)

        acu.withTypeDsl {
            val capture1 = captureMatcher(`?`)
            getCall.methodType.shouldMatchMethod(
                    named = "get",
                    declaredIn = gen.t_List[capture1],
                    withFormals = listOf(int),
                    returning = capture1
            )

            setCall.methodType shouldBe ts.UNRESOLVED_METHOD
        }
    }


    parserTest("Test lower wildcard compatibility") {


        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
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

        val tVar = acu.typeVar("T")
        val call = acu.firstMethodCall()

        spy.shouldBeOk {
            call shouldHaveType void
            call.arguments[0] shouldHaveType tVar
        }
    }

    parserTest("Test method ref on captured thing") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
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

        val call = acu.firstMethodCall()

        spy.shouldBeOk {
            call.shouldMatchN {
                methodCall("sort") {
                    it shouldHaveType void

                    variableAccess("statList") {}
                    argList {
                        methodCall("comparingInt") {
                            // eg. java.util.Comparator<capture#45 of ? extends java.lang.String>

                            val captureOfString = captureMatcher(`?` extends gen.t_String)

                            it shouldHaveType gen.t_Comparator[captureOfString]

                            it.methodType.shouldMatchMethod(
                                    named = "comparingInt",
                                    declaredIn = gen.t_Comparator,
                                    withFormals = listOf(ToIntFunction::class[`?` `super` captureOfString]),
                                    returning = gen.t_Comparator[captureOfString]
                            )

                            unspecifiedChild()

                            argList {
                                methodRef("hashCode") {
                                    unspecifiedChild()

                                    it.referencedMethod shouldBe ts.OBJECT.getMethodsByName("hashCode").single()
                                    it shouldHaveType ToIntFunction::class[captureOfString]
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    parserTest("Test independent captures merging") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
           import java.util.*;

           class Scratch {

               private <K> Spliterator<K> spliterator0(Collection<? extends K> collection, int characteristics) {
                   return null;
               }

               public static <T> Spliterator<T> spliterator(Collection<? extends T> c,
                                                            int characteristics) {
                   return spliterator0(Objects.requireNonNull(c), characteristics);
               }
           }

        """.trimIndent())

        val tvar = acu.typeVar("T")
        val call = acu.firstMethodCall()
        val reqnonnull = call.arguments[0] as ASTMethodCall

        spy.shouldBeOk {
            call.methodType.shouldMatchMethod(
                    named = "spliterator0",
                    withFormals = listOf(gen.t_Collection[`?` extends tvar], int),
                    returning = Spliterator::class[tvar]
            )

            val capture = captureMatcher(`?` extends tvar)
            reqnonnull shouldHaveType gen.t_Collection[capture]
            reqnonnull.methodType.shouldMatchMethod(named = "requireNonNull", declaredIn = Objects::class.raw)

            reqnonnull.arguments[0] shouldHaveType gen.t_Collection[capture]
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
                            returning = Collector::class[tvar, `?`, gen.t_Map[kvar, dvar]]
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

    parserTest("Capture vars should be exploded in typeArgsContains") {

        /*
        Phase STRICT, NodeStream<T>.<T> union(java.lang.Iterable<? extends NodeStream<? extends T>>) -> NodeStream<T>
            Context 4,          union(java.lang.Iterable<? extends NodeStream<? extends δ>>) -> NodeStream<δ>
            ARGUMENTS
                Checking arg 0 against java.lang.Iterable<? extends NodeStream<? extends δ>>
                    At:   /*unknown*/:7 :22..7:44
                    Expr: Arrays.asList(streams)
                    Phase INVOC_STRICT, java.util.Arrays.<T> asList(T...) -> java.util.List<T>
                        Context 5,          asList(ε...) -> java.util.List<ε>
                        RETURN
                            New bound           (ctx 5):   ε <: NodeStream<? extends δ>

                        ARGUMENTS
                            Checking arg 0 against ε[]
                                At:   /*unknown*/:7 :36..7:43
                                Expr: streams
                                New bound           (ctx 5):   ε >: NodeStream<? extends T>


                        New bound           (ctx 4):   δ >: capture#103 of ? extends T
                        Ctx 4 adopts [ε] from ctx 5
                    Success: asList(ε...) -> java.util.List<ε>


            Ivar instantiated   (ctx 4):   δ := capture#103 of ? extends T
            Ivar instantiated   (ctx 4):   δ := capture#103 of ? extends T
            New bound           (ctx 4):   ε <: NodeStream<? extends capture#103 of ? extends T>
            Failed: Incompatible bounds: ε <: NodeStream<? extends capture#103 of ? extends T> and ε >: NodeStream<? extends T>
        FAILED! SAD!

        The problem here is that we have

        ε <: NodeStream<? extends δ>
        ε >: NodeStream<? extends T>

        For these bounds to be compatible, we must have

        NodeStream<? extends T> <: NodeStream<? extends δ>

        The LHS is captured, so we'd actually test

        NodeStream<capture#103 of ? extends T> <: NodeStream<? extends δ>

        This reduces to

        capture#103 of ? extends T <= ? extends δ
        T <: δ

        This means we must crack the cvar in TypeOps::typeArgContains.

        Possibly, this could be recursive and lead to stackoverflow? Idk
         */

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

        val (_, unionOfIter) = acu.methodDeclarations().toList { it.genericSignature }

        spy.shouldBeOk {
            acu.firstMethodCall().methodType.shouldBeSomeInstantiationOf(unionOfIter)
        }
    }


    parserTest("Ivar should be instantiated with lower not upper bound") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
                """

interface MostlySingularMultimap<K, V> {

    public static class Builder<K, V> {  }

    public static class Function<I, O> {
        static <S> Function<S, S> identity();
    }


    public Builder<K, V> groupBy(Iterable<? extends V> values,
                                 Function<? super V, ? extends K> keyExtractor) {
        return groupBy(values, keyExtractor, Function.identity());
    }


    public <I> Builder<K, V> groupBy(Iterable<? extends I> values,
                                     Function<? super I, ? extends K> keyExtractor,
                                     Function<? super I, ? extends V> valueExtractor) {
        return null;
    }

}

                """.trimIndent()
        )

        val (_, _, lastGroupBy) = acu.methodDeclarations().toList { it.genericSignature }

        spy.shouldBeOk {
            acu.firstMethodCall().methodType.shouldBeSomeInstantiationOf(lastGroupBy)
        }
    }


    parserTest("Unbounded wild has bound of its underlying tvar") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
                """
import java.util.List;

class Scratch<S extends Scratch<S>> {

    public List<S> getS() {return null;}

    static {
        List<Scratch<?>> l = null;
        Scratch<?> unbounded = null;
        l.addAll(unbounded.getS());
    }
}
                """.trimIndent()
        )

        spy.shouldBeOk {
            acu.firstMethodCall().methodType
        }
    }


    parserTest("Array access should be captured") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
                """
class CompletableFuture<T> {

    private static <U, T extends U> CompletableFuture<U> uniCopyStage(CompletableFuture<T> src) {return null;}

    public static CompletableFuture<Object> anyOf(CompletableFuture<?>... cfs) {
        return (cfs.length == 0)
               ? new CompletableFuture<Object>()
               : uniCopyStage(cfs[0]);
    }
}

                """.trimIndent()
        )

        val t_CompletableFuture = acu.firstTypeSignature()

        spy.shouldBeOk {
            val captureMatcher = captureMatcher(`?`)

            val arrType = acu.descendants(ASTArrayAccess::class.java).firstOrThrow().typeMirror

            arrType shouldBe t_CompletableFuture[captureMatcher]

            acu.firstMethodCall().methodType.shouldMatchMethod(
                named = "uniCopyStage",
                withFormals = listOf(t_CompletableFuture[captureMatcher]),
                returning = t_CompletableFuture[ts.OBJECT]
            )
        }
    }


    parserTest("Capture with type annotation") {

        val (acu, spy) = parser.logTypeInferenceVerbose().parseWithTypeInferenceSpy(
            """
import java.lang.annotation.*;
interface Iterator<Q> { Q next(); }
interface Function<U,V> { 
    V apply(U u);
}
@Target(ElementType.TYPE_USE)
@interface Nullable {}
class Foo {
    public static <T, R> void flatMap(Iterator<? extends T> iter, Function<? super T, ? extends @Nullable Iterator<? extends R>> f) {
        Iterator<? extends R> next = f.apply(iter.next());
    }
}

                """.trimIndent()
        )

        val (t_Iterator, _, t_Nullable) = acu.declaredTypeSignatures()
        val rvar = acu.typeVar("R")
        val tvar = acu.typeVar("T")

        spy.shouldBeOk {
            acu.firstMethodCall().methodType.shouldMatchMethod(
                named = "apply",
                withFormals = listOf(captureMatcher(`?` `super` tvar)),
                returning = captureMatcher(`?` extends (`@`(t_Nullable.symbol) on t_Iterator[`?` extends rvar]))
            )
            acu.firstMethodCall().overloadSelectionInfo::isFailed shouldBe false
        }
    }

})


