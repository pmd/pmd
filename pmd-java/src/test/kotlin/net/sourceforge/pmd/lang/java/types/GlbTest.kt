/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import io.kotest.property.forAll
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

                forAll(ts.allTypesGen, ts.allTypesGen) { t, s ->
                    canIntersect(t, s) and t.isSubtypeOf(s) implies {
                        glb(t, s) == t
                    }
                }

                // in particular
                forAll(ts.allTypesGen) { t ->
                    glb(t, t) == t // regardless of what kind of type t is
                }
            }

            test("Test intersection symmetry") {

                forAll(ts.allTypesGen, ts.allTypesGen) { t, s ->
                    canIntersect(t, s) implies {
                        glb(t, s) == glb(s, t)
                    }
                }
            }

            test("Test intersection left associativity") {

                forAll(ts.allTypesGen, ts.allTypesGen, ts.allTypesGen) { t, s, r ->
                    canIntersect(t, s, r) implies {
                        glb(glb(t, s), r) == glb(t, s, r)
                    }
                }
            }

            test("Test intersection right associativity") {

                forAll(ts.allTypesGen, ts.allTypesGen, ts.allTypesGen) { t, s, r ->
                    canIntersect(t, s, r) implies {
                        glb(s, glb(s, r)) == glb(t, s, r)
                    }
                }
            }
        }
    }

})
