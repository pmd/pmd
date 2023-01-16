/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types.internal.infer

import com.github.oowekyala.treeutils.matchers.TreeNodeWrapper
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import net.sourceforge.pmd.lang.ast.Node
import net.sourceforge.pmd.lang.ast.test.NodeSpec
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.types.JClassType
import net.sourceforge.pmd.lang.java.types.shouldHaveType
import net.sourceforge.pmd.lang.java.types.testdata.BoolLogic
import net.sourceforge.pmd.lang.java.types.testdata.TypeInferenceTestCases
import net.sourceforge.pmd.lang.java.types.typeDsl
import kotlin.system.measureTimeMillis
import kotlin.test.assertFalse

/**
 * Expensive test cases for the overload resolution phase.
 *
 * Edit: So those used to be very expensive (think minutes of execution),
 * but optimisations made them very fast.
 */
class StressTest : ProcessorTestSpec({

    fun myLog(string: String) {
        println("[type inf stress test] $string")
    }

    parserTest("Test hard overload resolution - no generics involved") {
        asIfIn(BoolLogic::class.java)

        fun TreeNodeWrapper<Node, out TypeNode>.typeIs(value: Boolean) {
            it.typeMirror.shouldBeA<JClassType> {
                it.symbol.binaryName  shouldBe "net.sourceforge.pmd.lang.java.types.testdata.BoolLogic\$${value.toString().capitalize()}"
            }
        }

        fun TreeNodeWrapper<Node, *>.FALSE(): ASTMethodCall =
                methodCall("FALSE") {
                    typeIs(false)
                    argList { }
                }

        fun TreeNodeWrapper<Node, *>.TRUE(): ASTMethodCall =
                methodCall("TRUE") {
                    typeIs(true)
                    argList { }
                }

        fun TreeNodeWrapper<Node, *>.and(value: Boolean, spec: NodeSpec<ASTArgumentList>): ASTMethodCall =
                methodCall("and") {
                    typeIs(value)
                    argList { spec() }
                }

        fun TreeNodeWrapper<Node, *>.or(value: Boolean, spec: NodeSpec<ASTArgumentList>): ASTMethodCall =
                methodCall("or") {
                    typeIs(value)
                    argList { spec() }
                }

        inContext(ExpressionParsingCtx) {

            // If we weren't caching the compile-time declaration of method calls,
            // nesting 1 level here would multiply by 4 the total number of checks
            //  -> and luckily, these are not varargs or generic methods
            // If this test executes under 2s (startup included) everything's fine

            """

                and(or(TRUE(), and(and(or(FALSE(), and(and(or(FALSE(), and(TRUE(), TRUE())), TRUE()), FALSE())), TRUE()), TRUE())), TRUE())

            """ should parseAs {

                and(true) {
                    or(true) {
                        TRUE()
                        and(false) {
                            and(false) {
                                or(false) {
                                    FALSE()
                                    and(false) {
                                        and(true) {
                                            or(true) {
                                                FALSE()
                                                and(true) {
                                                    TRUE()
                                                    TRUE()
                                                }
                                            }
                                            TRUE()
                                        }
                                        FALSE()
                                    }
                                }
                                TRUE()
                            }
                            TRUE()
                        }
                    }
                    TRUE()
                }
            }
        }
    }

    parserTest("OpenJDK bug 8055984: type inference exponential compilation performance") {
        // https://bugs.openjdk.java.net/browse/JDK-8055984

        val acu = parser.parse("""
            class C<U> {
                U fu;
                C() {}
                C(C<U> other) { this.fu = other.fu; }
                C(U fu) { this.fu = fu; }

                static <U> C<U> m(C<U> src) { return new C<U>(src); }

                public static void main(String argv[]) {
                    /* type inference is expected here: */
                    C<String> c2 = m(new C<>(m(new C<>())));
                    C<String> c3 = m(new C<>(m(new C<>(m(new C<>())))));
                    C<String> c4 = m(new C<>(m(new C<>(m(new C<>(m(new C<>()))))))); // Javac(1.04), ECJ(.71s)
                    C<String> c5 = m(new C<>(m(new C<>(m(new C<>(m(new C<>(m(new C<>()))))))))); // Javac(2.02s), ECJ(1.17s)
                    C<String> c6 = m(new C<>(m(new C<>(m(new C<>(m(new C<>(m(new C<>(m(new C<>()))))))))))); // Javac(4.84s) ECJ(1.67s)
                    C<String> c7 = m(new C<>(m(new C<>(m(new C<>(m(new C<>(m(new C<>(m(new C<>(m(new C<>()))))))))))))); // Javac(14.99s) ECJ(10.82s)
                    C<String> c8 = m(new C<>(m(new C<>(m(new C<>(m(new C<>(m(new C<>(m(new C<>(m(new C<>(m(new C<>()))))))))))))))); // Javac(79.62s) ECJ(134.64s)
                    C<String> c9 = m(new C<>(m(new C<>(m(new C<>(m(new C<>(m(new C<>(m(new C<>(m(new C<>(m(new C<>(m(new C<>()))))))))))))))))); // Javac(437s) ECJ(1305s)
                    C<String> c10 = m(new C<>(m(new C<>(m(new C<>(m(new C<>(m(new C<>(m(new C<>(m(new C<>(m(new C<>(m(new C<>(m(new C<>()))))))))))))))))))); // 3600
                }
            }
        """)

        acu.descendants(ASTLocalVariableDeclaration::class.java)
                .map { it.varIds[0]!!.initializer!! }
                .forEachIndexed { i, expr ->
                    val t = measureTimeMillis {
                        expr.typeMirror shouldNotBe expr.typeSystem.UNKNOWN
                    }
                    myLog("c${i + 2}: $t ms")
                }
    }

    parserTest("OpenJDK bug 8225508: Compiler OOM Error with Type Inference Hierarchy") {
        // https://bugs.openjdk.java.net/browse/JDK-8225508

        val acu = parser.parse("""
        import java.util.Arrays;
        import java.util.List;

        public class InterfaceOverload {
            private interface I<X, IWitness extends I<?, IWitness>> { }
            private interface J<X, JWitness extends J<?, JWitness>> { }
            private interface K<X, KWitness extends K<?, KWitness>> { }
            private interface L<X, LWitness extends L<?, LWitness>> { }
            private interface M<X, MWitness extends M<?, MWitness>> { }
            private interface N<X, MWitness extends N<?, MWitness>> { }

            private static class ConsStruct {
            }
            private static class Empty extends ConsStruct { }
            private static class Cons<X, Y extends ConsStruct> extends ConsStruct { }

            // Cons-ing types: (C5, (B5, (A5, Empty)))
            // Implementing I through M (5 interfaces)
            private static class A5<X> extends Cons<X, Empty> implements
                    I<X, A5<?>>, J<X, A5<?>>, K<X, A5<?>>, L<X, A5<?>>, M<X, A5<?>> { }
            private static class B5<X, Y> extends Cons<X, A5<Y>> implements
                    I<Y, B5<X, ?>>, J<Y, B5<X, ?>>, K<Y, B5<X, ?>>, L<Y, B5<X, ?>>, M<Y, B5<X, ?>> { }
            private static class C5<X, Y, Z> extends Cons<X, B5<Y, Z>> implements
                    I<Z, C5<X, Y, ?>>, J<Z, C5<X, Y, ?>>, K<Z, C5<X, Y, ?>>, L<Z, C5<X, Y, ?>>, M<Z, C5<X, Y, ?>> { }

            // Cons-ing types: (C4, (B4, (A4, Empty)))
            // Implementing I through L (4 interfaces)
            private static class A4<X> extends Cons<X, Empty> implements
                    I<X, A4<?>>, J<X, A4<?>>, K<X, A4<?>>, L<X, A4<?>> { }
            private static class B4<X, Y> extends Cons<X, A4<Y>> implements
                    I<Y, B4<X, ?>>, J<Y, B4<X, ?>>, K<Y, B4<X, ?>>, L<Y, B4<X, ?>> { }
            private static class C4<X, Y, Z> extends Cons<X, B4<Y, Z>> implements
                    I<Z, C4<X, Y, ?>>, J<Z, C4<X, Y, ?>>, K<Z, C4<X, Y, ?>>, L<Z, C4<X, Y, ?>> { }

            // Cons-ing types: (C6, (B6, (A6, Empty)))
            // Implementing I through N (6 interfaces)
            private static class A6<X> extends Cons<X, Empty> implements
                    I<X, A6<?>>, J<X, A6<?>>, K<X, A6<?>>, L<X, A6<?>>, M<X, A6<?>>, N<X, A6<?>> { }
            private static class B6<X, Y> extends Cons<X, A6<Y>> implements
                    I<Y, B6<X, ?>>, J<Y, B6<X, ?>>, K<Y, B6<X, ?>>, L<Y, B6<X, ?>>, M<Y, B6<X, ?>>, N<Y, B6<X, Y>> { }
            private static class C6<X, Y, Z> extends Cons<X, B6<Y, Z>> implements
                    I<Z, C6<X, Y, ?>>, J<Z, C6<X, Y, ?>>, K<Z, C6<X, Y, ?>>, L<Z, C6<X, Y, ?>>, M<Z, C6<X, Y, ?>>, N<Z, C6<X, Y, ?>> { }

            public static void main(String[] args) {
                A5<Boolean>                  foo1 = new A5<>();
                B5<Boolean, Integer>         foo2 = new B5<>();
                C5<Boolean, Integer, String> foo3 = new C5<>();

                A4<Boolean>                  bar1 = new A4<>();
                B4<Boolean, Integer>         bar2 = new B4<>();
                C4<Boolean, Integer, String> bar3 = new C4<>();

                A6<Boolean>                  baz1 = new A6<>();
                B6<Boolean, Integer>         baz2 = new B6<>();
                C6<Boolean, Integer, String> baz3 = new C6<>();

                // Compiles - type hints
                List<Cons<Boolean, ? extends ConsStruct>> asList5WithHints =
                        Arrays.<Cons<Boolean, ? extends ConsStruct>>asList(foo1, foo2, foo3);

                List<Cons<Boolean, ? extends ConsStruct>> asList6WithHints =
                        Arrays.<Cons<Boolean, ? extends ConsStruct>>asList(baz1, baz2, baz3);

                // Compiles at 128m - only 4 interfaces
                List<Cons<Boolean, ? extends ConsStruct>> asList4WithoutHints =
                        Arrays.asList(bar1, bar2, bar3);

                // Runs out of memory at -Xmx2g, but not -Xmx3g
                List<Cons<Boolean, ? extends ConsStruct>> asList5WithoutHints =
                        Arrays.asList(foo1, foo2, foo3);

                // Runs out of memory at -Xmx3g and above
                List<Cons<Boolean, ? extends ConsStruct>> asList6WithoutHints =
                        Arrays.asList(baz1, baz2, baz3);
            }
        }
        """.trimIndent())

        acu.descendants(ASTLocalVariableDeclaration::class.java)
                .map { it.varIds[0]!! }
                .filter { it.name.startsWith("asList") }
                .map { it.initializer!! }
                .forEachIndexed { i, expr ->
                    val t = measureTimeMillis { // todo these measurements are not accurate because typeres is done strictly now
                        assertFalse {
                            expr.typeMirror == expr.typeSystem.UNKNOWN
                        }
                    }
                    myLog("asList$i: $t ms")
                }
    }


    /*
        This takes around a second on my machine. Three optimisations make it tractable:
        - the graph walk. I terminated powerset walk after 5 mins
        - caching of the ctdecl
        ->> mostly, merging of equal ivars

        Javac does this in 30 seconds. Without merging ivars, this takes 60 seconds

        Update: this used to very fast. I reverted some optimisations for clarity,
        which makes this take about 20 secs. So I'm removing some nesting to not
        slow down the build too much.

     */
    parserTest("Test context passing in huge call chain") {


        asIfIn(TypeInferenceTestCases::class.java)

        //     public static <U> List<U> m(List<U> src)

        inContext(StatementParsingCtx) {
            val code = """
            List<Integer> c =
              // 8 lparens per line
              m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(
              m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(
              m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(
              m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(
              m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(
              m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(
              m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(
//              m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(
//              m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(
//              m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(
//              m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(
//              m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(m(new java.util.ArrayList<>(
//              ))))))))
//              ))))))))
//              ))))))))
//              ))))))))
//              ))))))))
              ))))))))
              ))))))))
              ))))))))
              ))))))))
              ))))))))
              ))))))))
              ))))))));

        """

            val t = measureTimeMillis {
                code should parseAs {
                    localVarDecl {
                        modifiers { }
                        classType("List") {
                            //                it shouldHaveType RefTypeGen.`t_List{Integer}`
                            typeArgList()
                        }

                        child<ASTVariableDeclarator> {
                            variableId("c") {
                                it shouldHaveType it.typeDsl.gen.`t_List{Integer}`
                            }
                            child<ASTMethodCall>(ignoreChildren = true) {
                                it shouldHaveType it.typeDsl.gen.`t_List{Integer}`
                            }
                        }
                    }
                }
            }
            myLog("huge call chain: $t ms")
        }
    }


})
