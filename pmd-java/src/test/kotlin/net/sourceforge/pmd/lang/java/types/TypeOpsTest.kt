/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.property.Arb
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.shuffle
import io.kotest.property.checkAll

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

                checkAll(ts.subtypesArb(false)) { (t, s) ->
                    TypeOps.mostSpecific(setOf(t, s)).shouldContainExactly(s)
                }
            }

            test("Test most when types are equal") {

                checkAll(ts.subtypesArb(false)) { (t, s) ->
                    TypeOps.mostSpecific(setOf(t, s)).shouldContainExactly(s)
                }
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
        }
    }

})
