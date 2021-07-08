/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.java.symbols.internal.asm.createUnresolvedAsmSymbol

/**
 * Tests "the greatest lower bound" (glb).
 * See net.sourceforge.pmd.lang.java.types.TypeSystem.glb(Collection<? extends JTypeMirror>).
 *
 * @author Clément Fournier
 */
class GlbTest : FunSpec({

    with(TypeDslOf(testTypeSystem)) { // import construction DSL
        with(gen) { // import constants

            test("Test intersection minimization") {

                checkAll(ts.subtypesArb()) { (t, s) ->
                    glb(t, s) shouldBe t
                }

                // in particular
                checkAll(ts.refTypeGen) { t ->
                    glb(t, t) shouldBe t // regardless of what kind of type t is
                }
            }

            test("Test intersection symmetry") {

                checkAll(ts.allTypesGen, ts.allTypesGen) { t, s ->
                    if (canIntersect(t, s)) {
                        glb(t, s) shouldBe glb(s, t)
                    }
                }
            }

            test("Test intersection left associativity") {

                checkAll(ts.allTypesGen, ts.allTypesGen, ts.allTypesGen) { t, s, r ->
                    if (canIntersect(t, s, r)) {
                        glb(glb(t, s), r) shouldBe glb(t, s, r)
                    }
                }
            }

            test("Test intersection right associativity") {

                checkAll(ts.allTypesGen, ts.allTypesGen, ts.allTypesGen) { t, s, r ->
                    if (canIntersect(t, s, r)) {
                        glb(t, glb(s, r)) shouldBe glb(t, s, r)
                    }
                }
            }

            test("Test GLB min") {

                glb(ts.SERIALIZABLE, t_ArrayList) shouldBe t_ArrayList
                glb(t_ArrayList, ts.SERIALIZABLE) shouldBe t_ArrayList
                glb(t_List, `t_List{?}`) shouldBe `t_List{?}`

            }


            test("Test lub of zero types") {
                shouldThrow<IllegalArgumentException> {
                    ts.glb(emptyList())
                }
            }


            test("Test GLB errors") {

                shouldThrow<IllegalArgumentException> {
                    glb(int, t_Number)
                }
                shouldThrow<IllegalArgumentException> {
                    glb(int, ts.OBJECT)
                }
            }

            test("Test GLB corner cases") {

                glb(t_Iterable[`?` extends t_Number], t_Iterable[t_String]).shouldBeA<JIntersectionType> {
                    it.components.shouldContainExactly(t_Iterable[`?` extends t_Number], t_Iterable[t_String])
                }
                glb(`t_ArrayList{Integer}`, ts.NULL_TYPE) shouldBe ts.NULL_TYPE
                glb(`t_ArrayList{Integer}`, t_Iterable[`?` extends t_Number], t_Iterable[t_String]).shouldBeA<JIntersectionType> {
                    it.components.shouldContainExactly(`t_ArrayList{Integer}`, t_Iterable[t_String])
                }

                glb(`t_List{? extends Number}`, `t_Collection{Integer}`).shouldBeA<JIntersectionType> {
                    it.components.shouldContainExactly(`t_List{? extends Number}`, `t_Collection{Integer}`)
                }

                glb(t_List.toArray(), t_Iterable).shouldBeA<JIntersectionType> {
                    it.components.shouldContainExactly(t_List.toArray(), t_Iterable)
                    it.inducedClassType.shouldBeNull()
                }
                glb(`t_List{? extends Number}`, `t_Collection{Integer}`, `t_ArrayList{Integer}`) shouldBe `t_ArrayList{Integer}`
                glb(`t_List{? extends Number}`, `t_List{String}`, `t_Enum{JPrimitiveType}`).shouldBeA<JIntersectionType> {
                    it.components.shouldContainExactly(`t_Enum{JPrimitiveType}`, `t_List{String}`, `t_List{? extends Number}`)
                }
            }

            test("Test GLB with unresolved things") {
                val tA = ts.declaration(ts.createUnresolvedAsmSymbol("a.A"))
                val tB = ts.declaration(ts.createUnresolvedAsmSymbol("a.B"))

                tA shouldBeSubtypeOf tB
                tB shouldBeSubtypeOf tA

                TypeOps.mostSpecific(setOf(tA, tB)) shouldBe setOf(tA, tB)

                glb(tA, tB).shouldBeA<JIntersectionType> {
                    it.components.shouldContainExactly(tA, tB)
                }
            }
        }
    }

})
