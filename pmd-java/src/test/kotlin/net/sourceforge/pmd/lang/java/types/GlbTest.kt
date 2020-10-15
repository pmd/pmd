/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.property.PropTestConfig
import io.kotest.property.checkAll
import io.kotest.property.forAll
import io.mockk.InternalPlatformDsl.toArray
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.java.types.testdata.LubTestData
import net.sourceforge.pmd.lang.java.types.testdata.LubTestData.*
import java.io.Serializable

/**
 * @author Clément Fournier
 */
class GlbTest : FunSpec({

    with(TypeDslOf(testTypeSystem)) { // import construction DSL
        with(gen) { // import constants

            test("Test intersection minimization") {

                forAll(ts.subtypesArb()) { (t, s) ->
                    glb(t, s) == t
                }

                // in particular
                checkAll(ts.allTypesGen) { t ->
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
        }
    }

})
