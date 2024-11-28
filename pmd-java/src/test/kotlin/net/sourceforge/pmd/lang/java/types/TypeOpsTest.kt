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
