/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

@file:Suppress("LocalVariableName")

package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId
import net.sourceforge.pmd.lang.java.ast.ProcessorTestSpec
import net.sourceforge.pmd.lang.java.types.shouldMatchMethod
import net.sourceforge.pmd.lang.java.types.typeDsl

/**
 */
class RawTypeInferenceTest : ProcessorTestSpec({

    parserTest("Test raw type in argument erases result") {

        val logGetter = logTypeInference()
        val acu = parser.parse(
                """

class C {

    static <T extends Enum<T>> T valueOf(Class<T> k) { return null; } 

    static {
        var c = valueOf((Class) Object.class);
    }
}

                """.trimIndent()
        )

        val (t_C) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }

        val call = acu.descendants(ASTMethodCall::class.java).firstOrThrow()
        val id = acu.descendants(ASTVariableDeclaratorId::class.java).first { it.name == "c" }!!

        assert(logGetter().isEmpty())
        with(call.typeDsl) {
            call.methodType.shouldMatchMethod(
                    named = "valueOf",
                    declaredIn = t_C,
                    withFormals = listOf(Class::class[gen.t_Enum]),
                    returning = gen.t_Enum
            )
            call.typeMirror shouldBe gen.t_Enum
        }
        assert(logGetter().isEmpty())

        with(call.typeDsl) {
            id.typeMirror shouldBe gen.t_Enum
        }
    }

    /*
If the ... is generic:

 If unchecked conversion was necessary for the method to be applicable during constraint
 set reduction in §18.5.1, the constraint formula ‹|R| → T› is reduced and incorporated
 with B2.



 Otherwise, the constraint formula ‹R θ → T› is reduced and incorporated with B2.


If the chosen method is not generic, then:

     If unchecked conversion was necessary for the method to be applicable, the
      parameter types of the invocation type are the parameter types of the method's
      type, and the return type and thrown types are given by the erasures of the
      return type and thrown types of the method's type.


     */


})
