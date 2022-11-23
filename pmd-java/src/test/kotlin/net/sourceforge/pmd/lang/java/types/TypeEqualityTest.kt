/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.checkAll
import io.kotest.property.forAll
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol
import net.sourceforge.pmd.lang.java.symbols.internal.asm.createUnresolvedAsmSymbol
import net.sourceforge.pmd.lang.java.symbols.internal.forAllEqual

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

                forAll(ts.refTypeGen, ts.primitiveGen) { ref, prim ->
                    ref != prim
                }
            }

            test("Test array equality") {

                checkAll(ts.allTypesGen, ts.allTypesGen) { t, s ->
                    (t == s) shouldBe (t.toArray(1) == s.toArray(1))
                }
            }

            test("Test equality symmetry") {

                checkAll(ts.allTypesGen, ts.allTypesGen) { t, s ->
                    (t == s) shouldBe (s == t)
                }
            }

            test("Test wildcard equality") {

                fun canBeWildCardBound(t: JTypeMirror) = !(t.isPrimitive || t is JWildcardType)

                forAll(ts.allTypesGen, ts.allTypesGen) { t, s ->
                    (canBeWildCardBound(t) && canBeWildCardBound(s)) implies {
                        (t == s) == (`?` extends t == `?` extends s)
                    }
                }

                forAll(ts.allTypesGen, ts.allTypesGen) { t, s ->
                    (canBeWildCardBound(t) && canBeWildCardBound(s)) implies {
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


            test("Test non well-formed types") {
                val sym = ts.createUnresolvedAsmSymbol("does.not.Exist") as JClassSymbol
                // not equal
                sym[t_String, t_String] shouldNotBe sym[t_String]
                sym[t_String] shouldNotBe sym[t_String, t_String]
                sym[t_Integer] shouldNotBe sym[t_String]

                // equal
                sym[t_String, t_String] shouldBe sym[t_String, t_String]
                sym[t_String] shouldBe sym[t_String]
                sym[t_String, t_Integer] shouldBe sym[t_String, t_Integer]
            }
        }
    }


})
