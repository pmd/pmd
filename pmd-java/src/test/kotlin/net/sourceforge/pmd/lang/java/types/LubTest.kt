/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types

import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import io.kotlintest.specs.AbstractFunSpec
import java.io.Serializable

/**
 * @author Clément Fournier
 */
class LubTest : AbstractFunSpec({
    with(TypeDslOf(testTypeSystem)) { // import construction DSL
        with(gen) { // import constants

            fun lub(vararg us: JTypeMirror) = ts.lub(us.toList())

            test("Test generic supertype set") {

                `t_ArrayList{Integer}`.superTypeSet shouldBe setOf(
                        `t_ArrayList{Integer}`,
                        `t_AbstractList{Integer}`,
                        `t_AbstractCollection{Integer}`,
                        `t_List{Integer}`,
                        `t_Collection{Integer}`,
                        `t_Iterable{Integer}`,
                        java.util.RandomAccess::class.decl,
                        ts.OBJECT,
                        ts.CLONEABLE,
                        ts.SERIALIZABLE
                )

            }

            test("Test raw generic supertype set erased") {

                t_ArrayList.superTypeSet shouldBe setOf(
                        t_ArrayList,
                        t_AbstractList,
                        t_AbstractCollection,
                        t_List,
                        t_Collection,
                        t_Iterable,
                        java.util.RandomAccess::class.decl,
                        ts.OBJECT,
                        ts.CLONEABLE,
                        ts.SERIALIZABLE
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

            test("Test lub with related type arguments") {

                lub(`t_List{Integer}`, `t_List{Number}`) shouldBe `t_List{? extends Number}`
                lub(`t_List{Integer}`, `t_List{? extends Number}`) shouldBe `t_List{? extends Number}`
            }

            test("Test lub with identical type arguments") {

                lub(`t_ArrayList{Integer}`, `t_LinkedList{Integer}`) shouldBe (`t_AbstractList{Integer}` * ts.CLONEABLE * ts.SERIALIZABLE)
            }

            test("Test simple lub") {

                lub(java.lang.String::class.decl,
                        java.lang.Integer::class.decl) shouldBe (
                        ts.SERIALIZABLE * comparableOf(`?` extends (ts.SERIALIZABLE * comparableOf(`?`)))
                        )
            }

            test("Test lub of one type") {

                forAll(RefTypeGen) { ref ->
                    lub(ref) shouldBe ref
                    true
                }

            }

            test("Test lub with interface intersection") {

                // this example recurses into lub(Comparable<String>, Comparable<Number>), at which point
                // we ask lcta(String, Number) again, meaning it's a good test for recursion breaking

                lub(`t_List{Integer}`, `t_List{Number}`) shouldBe `t_List{? extends Number}`

                // List<? extends Serializable & Comparable<?>>

                val result = List::class[`?` extends (Serializable::class.decl * java.lang.Comparable::class[`?`])]

                lub(`t_List{String}`, `t_List{Integer}`) shouldBe result

            }
        }
    }

})
