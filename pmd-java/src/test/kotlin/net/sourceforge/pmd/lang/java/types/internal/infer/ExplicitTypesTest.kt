/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldMatchN
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.types.shouldMatchMethod
import net.sourceforge.pmd.lang.java.types.typeDsl
import java.util.*


class ExplicitTypesTest : ProcessorTestSpec({


    parserTest("Test explicit type arguments") {

        val acu = parser.parse("""

           import java.util.Collection;

           public class SomeIter {
               {
                  foreach(java.util.Collections.<String>emptyList());
               }

               <T> void foreach(Collection<? super T> action) {}
           }

        """.trimIndent())


        val call = acu.descendants(ASTMethodCall::class.java).first()!!

        call.shouldMatchN {
            methodCall("foreach") {
                it.typeMirror shouldBe it.typeSystem.NO_TYPE
                argList {
                    methodCall("emptyList") {
                        unspecifiedChild()
                        typeArgList(1)
                        argList(0)

                        with(it.typeDsl) {
                            it.methodType.shouldMatchMethod(
                                    named = "emptyList",
                                    declaredIn = Collections::class.decl,
                                    withFormals = emptyList(),
                                    returning = gen.`t_List{String}`
                            )
                        }
                    }
                }
            }
        }
    }

})
