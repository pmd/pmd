/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import io.kotest.property.forAll

/**
 * @author Clément Fournier
 */
class TypeEqualityTest : FunSpec({

    // TODO test that a non-generic type is equal to itself whether created with declaration or rawType

    val ts = testTypeSystem
    with(TypeDslOf(ts)) {
        with(gen) {

            test("Test primitive equality") {

                forAll(ts.primitiveGen) {
                    it == it
                }

                boolean shouldNotBe int
                double shouldNotBe int
                char shouldNotBe byte
                char shouldNotBe ts.OBJECT

                forAll(gen, ts.primitiveGen) { ref, prim ->
                    ref != prim
                }
            }

            test("Test array equality") {

                forAll(ts.allTypesGen, ts.allTypesGen) { t, s ->
                    (t == s) == (t.toArray(1) == t.toArray(1))
                }
            }

            test("Test equality symmetry") {

                forAll(ts.allTypesGen, ts.allTypesGen) { t, s ->
                    (t == s) == (s == t)
                }
            }

            test("Test wildcard equality") {

                fun canBeWildCardBound(t: JTypeMirror) = !(t.isPrimitive || t is JWildcardType)

                forAll(ts.allTypesGen, ts.allTypesGen) { t, s ->
                    canBeWildCardBound(t) implies {
                        (t == s) == (`?` extends t == `?` extends s)
                    }
                }

                forAll(ts.allTypesGen, ts.allTypesGen) { t, s ->
                    canBeWildCardBound(t) implies {
                        (t == s) == (`?` `super` t == `?` `super` s)
                    }
                }
            }

            test("Test intersection equality") {

                forAll(ts.allTypesGen, ts.allTypesGen) { t, s ->
                    canIntersect(t, s) implies {
                        glb(t, s) == glb(t, s)
                    }
                }
            }
        }
    }


})
