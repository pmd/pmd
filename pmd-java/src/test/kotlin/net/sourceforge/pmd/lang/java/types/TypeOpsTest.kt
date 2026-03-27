/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.shuffle
import io.kotest.property.checkAll
import net.sourceforge.pmd.lang.java.symbols.internal.asm.createUnresolvedAsmSymbol

/**
 * @author Clément Fournier
 */
class TypeOpsTest : FunSpec({

    with(TypeDslOf(testTypeSystem)) { // import construction DSL
        with(gen) { // import constants


            // for any permutation of input, the output should be the same
            suspend fun checkMostSpecific(input: List<JTypeMirror>, output: List<JTypeMirror>) {

                checkAll(Arb.shuffle(input)) { ts ->
                    TypeOps.mostSpecific(ts).shouldContainExactlyInAnyOrder(output)
                }
            }

            test("Test most specific") {

                checkAll(ts.subtypesArb()) { (t, s) ->
                    TypeOps.mostSpecific(setOf(t, s)).shouldContainExactly(t)
                }
            }

            test("Test most when types are equal") {

                checkMostSpecific(
                        input = listOf(t_AbstractList, t_AbstractList),
                        output = listOf(t_AbstractList))
            }

            test("Test most specific of unresolved types") {
                val tA = ts.declaration(ts.createUnresolvedAsmSymbol("a.A"))
                val tB = ts.declaration(ts.createUnresolvedAsmSymbol("a.B"))

                checkMostSpecific(
                    input = listOf(tA, tB),
                    output = listOf(tA, tB)
                )
            }

            test("Test most specific unchecked") {


                checkMostSpecific(
                        input = listOf(t_List, `t_List{?}`),
                        output = listOf(`t_List{?}`))

                checkMostSpecific(
                        input = listOf(t_List, `t_List{?}`, `t_List{Integer}`),
                        output = listOf(`t_List{Integer}`))

                checkMostSpecific(
                        input = listOf(t_List, `t_List{?}`, `t_List{Integer}`, `t_List{String}`),
                        output = listOf(`t_List{String}`, `t_List{Integer}`))

            }

            test("Bug #5029: Recursive type projection") {

                val (acu, spy) = javaParser.parseWithTypeInferenceSpy(
                    """
                       class Job<J extends Job<J, R>, R extends Run<J, R>> {
                            J getLast() { return null; }
                       }
                       class Run<J extends Job<J, R>, R extends Run<J, R>> {}
                       
                       class Foo {
                         static Job<?, ?> foo(Job<?,?> job) {
                            var x = job.getLast();
                            return x;
                         }
                       }
                    """.trimIndent()
                )
                val (jobt, runt) = acu.declaredTypeSignatures()
                val xVar = acu.varId("x")
                spy.shouldBeOk {
                    xVar shouldHaveType jobt[`?` extends jobt[`?`, `?` extends runt[`?`, `?`]], `?`]
                }


            }
            test("#5167 problem in projection") {
                val (acu, spy) = javaParser.parseWithTypeInferenceSpy(
                    """
import java.lang.annotation.Annotation;
interface Bar<T> {
    Baz<T> getBaz();
}

interface Predicate<T> {
    boolean check(T t);
}
interface Stream<T>{
    T findSome();
}
interface Baz<T>{
    Stream<Bar<? super T>> filterMethods(Predicate<? super T> p);
}

class Foo {

    private static Bar<?> foo(
        Bar<?> type, Class<? extends Annotation> annotation, boolean required) {
        var method = type.getBaz().filterMethods(m -> true).findSome();
        return method;
    }
}
            """.trimIndent()
                )

                val (barT) = acu.declaredTypeSignatures()
                val methodId = acu.varId("method")

                spy.shouldBeOk {
                    methodId shouldHaveType barT[`?`]
                }
            }

            test("Bug: projectUpwards does not StackOverflow with cyclic captured type var bounds") {
                // Container<T extends Container<? extends T>> has an F-bound that contains
                // a wildcard self-reference. Calling getValue() on Container<?> returns a
                // captured type variable α whose upper bound is Container<? extends α>.
                // Projecting α upwards visits Container<? extends α>, then tries to project
                // the wildcard bound α again — a cycle. Without the fix, this SOEs because
                // visitTypeVar for captured vars followed the bound without tracking the var
                // in the RecursionStop, so the guard in recurseIfNotDone (which only fires
                // for JTypeVar args in visitClass) was never reached for this path.
                val (acu, spy) = javaParser.parseWithTypeInferenceSpy(
                    """
                    class Container<T extends Container<? extends T>> {
                        T getValue() { return null; }
                    }
                    class Foo {
                        void test(Container<?> c) {
                            var x = c.getValue();
                        }
                    }
                    """.trimIndent()
                )

                val (containerT) = acu.declaredTypeSignatures()
                val xVar = acu.varId("x")

                // α is the capture variable created from the `?` in Container<?>
                val alpha = captureMatcher(`?`)

                // projectUpwards(α) = Container<? extends α>
                // (α's upper bound is Container<? extends α>; projecting it visits the
                // wildcard bound α again but the cycle guard stops the recursion there,
                // returning α unchanged, so the wildcard ? extends α is also unchanged)
                spy.shouldBeOk {
                    xVar shouldHaveType containerT[`?` extends alpha]
                }
            }

            test("Bug: projectUpwards does not StackOverflow when captured var is a direct type arg whose bound cycles back via wildcard") {
                // Variant of the previous test: here the captured var α appears as a DIRECT
                // type argument of the class being projected (not just via a wildcard bound).
                // visitClass calls recurseIfNotDone(α, body); body calls visitTypeVar(α) via
                // the `contains` path. Without a guard there, following α's bound leads to
                // Container<? extends α>, the wildcard path visits α again via `contains`,
                // and the recursion never terminates.
                val (acu, spy) = javaParser.parseWithTypeInferenceSpy(
                    """
                    class Container<T extends Container<? extends T>> {
                        Container<T> wrap() { return null; }
                    }
                    class Foo {
                        void test(Container<?> c) {
                            var x = c.wrap();
                        }
                    }
                    """.trimIndent()
                )

                val (containerT) = acu.declaredTypeSignatures()
                val xVar = acu.varId("x")
                val alpha = captureMatcher(`?`)

                spy.shouldBeOk {
                    xVar shouldHaveType containerT[`?` extends containerT[`?` extends alpha]]
                }
            }

            test("isSpecialUnresolved") {
                val unresolved = ts.declaration(ts.createUnresolvedAsmSymbol("Unknown"))
                TypeOps.isSpecialUnresolved(unresolved) shouldBe false
                TypeOps.isSpecialUnresolved(unresolved.toArray()) shouldBe false
                TypeOps.isSpecialUnresolved(unresolved.toArray(3)) shouldBe false

                for (ty in listOf(ts.UNKNOWN, ts.ERROR)) {
                    TypeOps.isSpecialUnresolved(ty) shouldBe true
                    TypeOps.isSpecialUnresolved(ty.toArray()) shouldBe false
                    TypeOps.isSpecialUnresolved(ty.toArray(3)) shouldBe false
                }
            }

            test("isSpecialUnresolvedOrArray") {
                val unresolved = ts.declaration(ts.createUnresolvedAsmSymbol("Unknown"))
                TypeOps.isSpecialUnresolvedOrArray(unresolved) shouldBe false
                TypeOps.isSpecialUnresolvedOrArray(unresolved.toArray()) shouldBe false
                TypeOps.isSpecialUnresolvedOrArray(unresolved.toArray(3)) shouldBe false

                for (ty in listOf(ts.UNKNOWN, ts.ERROR)) {
                    TypeOps.isSpecialUnresolvedOrArray(ty) shouldBe true
                    TypeOps.isSpecialUnresolvedOrArray(ty.toArray()) shouldBe true
                    TypeOps.isSpecialUnresolvedOrArray(ty.toArray(3)) shouldBe true
                }
            }

            test("hasUnresolvedSymbol") {
                val unresolved = ts.declaration(ts.createUnresolvedAsmSymbol("Unknown"))
                TypeOps.hasUnresolvedSymbol(unresolved) shouldBe true
                TypeOps.hasUnresolvedSymbol(unresolved.toArray()) shouldBe false
                TypeOps.hasUnresolvedSymbol(unresolved.toArray(3)) shouldBe false

                for (ty in listOf(ts.UNKNOWN, ts.ERROR)) {
                    TypeOps.hasUnresolvedSymbol(ty) shouldBe false
                    TypeOps.hasUnresolvedSymbol(ty.toArray()) shouldBe false
                    TypeOps.hasUnresolvedSymbol(ty.toArray(3)) shouldBe false
                }
            }


            test("hasUnresolvedSymbolOrArray") {
                val unresolved = ts.declaration(ts.createUnresolvedAsmSymbol("Unknown"))
                TypeOps.hasUnresolvedSymbolOrArray(unresolved) shouldBe true
                TypeOps.hasUnresolvedSymbolOrArray(unresolved.toArray()) shouldBe true
                TypeOps.hasUnresolvedSymbolOrArray(unresolved.toArray(3)) shouldBe true

                for (ty in listOf(ts.UNKNOWN, ts.ERROR)) {
                    TypeOps.hasUnresolvedSymbolOrArray(ty) shouldBe false
                    TypeOps.hasUnresolvedSymbolOrArray(ty.toArray()) shouldBe false
                    TypeOps.hasUnresolvedSymbolOrArray(ty.toArray(3)) shouldBe false
                }
            }


            test("isUnresolved") {
                val unresolved = ts.declaration(ts.createUnresolvedAsmSymbol("Unknown"))
                TypeOps.isUnresolved(unresolved) shouldBe true
                TypeOps.isUnresolved(unresolved.toArray()) shouldBe false
                TypeOps.isUnresolved(unresolved.toArray(3)) shouldBe false

                for (ty in listOf(ts.UNKNOWN, ts.ERROR)) {
                    TypeOps.isUnresolved(ty) shouldBe true
                    TypeOps.isUnresolved(ty.toArray()) shouldBe false
                    TypeOps.isUnresolved(ty.toArray(3)) shouldBe false
                }
            }

            test("isUnresolvedOrArray") {
                val unresolved = ts.declaration(ts.createUnresolvedAsmSymbol("Unknown"))
                TypeOps.isUnresolvedOrArray(unresolved) shouldBe true
                TypeOps.isUnresolvedOrArray(unresolved.toArray()) shouldBe true
                TypeOps.isUnresolvedOrArray(unresolved.toArray(3)) shouldBe true

                for (ty in listOf(ts.UNKNOWN, ts.ERROR)) {
                    TypeOps.isUnresolvedOrArray(ty) shouldBe true
                    TypeOps.isUnresolvedOrArray(ty.toArray()) shouldBe true
                    TypeOps.isUnresolvedOrArray(ty.toArray(3)) shouldBe true
                }
            }
        }
    }


})
