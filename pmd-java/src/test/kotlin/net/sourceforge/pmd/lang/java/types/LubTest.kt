/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import net.sourceforge.pmd.lang.java.types.testdata.LubTestData.*

/**
 * Tests "least upper bound" (lub).
 * See net.sourceforge.pmd.lang.java.types.TypeSystem.lub(Collection<? extends JTypeMirror>).
 *
 * @author Clément Fournier
 */
class LubTest : FunSpec({

    with(TypeDslOf(testTypeSystem)) { // import construction DSL
        with(gen) { // import constants

            test("Test generic supertype set") {

                val set = setOf(
                        GenericSub::class[int.box()],
                        GenericSuper::class[int.box()],
                        ts.OBJECT,
                        I3::class.decl,
                        I2::class[I1::class.decl],
                        I1::class.decl
                )

                val sups = GenericSub::class[int.box()].superTypeSet

                sups shouldBe set

            }

            test("Test raw generic supertype set erased") {

                GenericSub::class.raw.superTypeSet shouldBe setOf(
                        GenericSub::class.raw,
                        GenericSuper::class.raw,
                        ts.forceErase(I3::class.decl),
                        I2::class.raw,
                        ts.forceErase(I1::class.decl),
                        ts.OBJECT
                )

            }


            test("Test most specific set") {
                TypeOps.mostSpecific(setOf(
                        `t_List{Integer}`,
                        `t_Collection{Integer}`,
                        `t_Iterable{Integer}`,
                        ts.OBJECT
                )) shouldBe setOf(`t_List{Integer}`)

                TypeOps.mostSpecific(setOf(
                        t_List,
                        t_Collection,
                        t_Iterable,
                        ts.OBJECT
                )) shouldBe setOf(t_List)

            }
            test("Test relevant parametrisations") {
                Lub.relevant(t_List,
                        setOf(
                                `t_List{Integer}`,
                                `t_List{String}`,
                                `t_Collection{Integer}`,
                                `t_Iterable{Integer}`,
                                ts.OBJECT
                        )
                ) shouldBe listOf(`t_List{Integer}`, `t_List{String}`)
            }

            test("Test lub with related type arguments (LCTA)") {

                fun checkLcta(vararg components: JTypeMirror, expected: JTypeMirror) {
                    val l = ts.lub(components.map { GenericSub::class[it] }) as JClassType
                    l.typeArgs[0] shouldBe expected
                }

                lub(GenericSub::class[t_Integer], GenericSub::class[`?` extends t_Number]) shouldBe GenericSub::class[`?` extends t_Number]

                checkLcta(t_Integer,             t_Number,              expected = `?` extends t_Number)
                checkLcta(t_Integer,             `?` extends t_Number,  expected = `?` extends t_Number)
                checkLcta(t_Integer,             `?` `super` t_Number,  expected = `?` `super` t_Integer)
                checkLcta(t_Integer,             `?` `super` t_Integer, expected = `?` `super` t_Integer)
                checkLcta(t_Integer,             `?` extends t_Integer, expected = `?` extends t_Integer)

                checkLcta(`?` `super` t_Integer, `?` `super` t_Number,  expected = `?` `super` t_Integer)
                checkLcta(`?` extends t_Integer, `?` extends t_Number,  expected = `?` extends t_Number)
                checkLcta(`?` extends t_Integer, `?` `super` t_Number,  expected = `?`)
                checkLcta(`?` `super` t_Integer, `?` extends t_Number,  expected = `?`)

            }

            test("Test lub with identical type arguments") {

                lub(GenericSub::class[t_Integer], GenericSub2::class[t_Integer]) shouldBe (GenericSuper::class[t_Integer] * I2::class[`?`])
            }

            test("Test simple lub") {

                lub(Sub1::class.decl, Sub2::class.decl) shouldBe (
                        I1::class.decl * t_Comparable[`?` extends (I1::class.decl * t_Comparable[`?`])]
                )
            }

            test("Test lub of one type") {

                ts.allTypesGen.checkAll { ref ->
                    lub(ref) shouldBe ref
                }

            }

            test("Test lub of zero types") {
                shouldThrow<IllegalArgumentException> {
                    ts.lub(emptyList())
                }
            }

            test("Test lub with interface intersection") {

                // this example recurses into lub(Comparable<Sub1>, Comparable<Sub2>), at which point
                // we ask lcta(Sub1, Sub2) again, meaning it's a good test for recursion breaking

                // List<? extends I1 & Comparable<?>>

                val result = List::class[`?` extends (I1::class.decl * t_Comparable[`?`])]

                lub(t_List[Sub1::class.decl], t_List[Sub2::class.decl]) shouldBe result

            }
        }
    }

})
