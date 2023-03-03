/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer

import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall
import net.sourceforge.pmd.lang.java.ast.ProcessorTestSpec
import net.sourceforge.pmd.lang.java.types.firstMethodCall
import net.sourceforge.pmd.lang.java.types.parseWithTypeInferenceSpy
import net.sourceforge.pmd.lang.java.types.shouldHaveType
import net.sourceforge.pmd.lang.java.types.shouldMatchMethod
import java.util.*


class ExplicitTypesTest : ProcessorTestSpec({

    // todo test explicitly typed lambda
    // todo test explicit type args on ctor call


    parserTest("Test explicit type arguments") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """

           import java.util.Collection;

           public class SomeIter {
               {
                  foreach(java.util.Collections.<String>emptyList());
               }

               <T> void foreach(Collection<? super T> action) {}
           }

        """.trimIndent()
        )


        val call = acu.firstMethodCall()

        spy.shouldBeOk {
            call shouldHaveType ts.NO_TYPE
            call.arguments[0].shouldBeA<ASTMethodCall> {
                it.methodType.shouldMatchMethod(
                    named = "emptyList",
                    declaredIn = Collections::class.decl,
                    withFormals = emptyList(),
                    returning = gen.`t_List{String}`
                )
            }
        }
    }

})
