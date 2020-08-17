/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol
import net.sourceforge.pmd.lang.java.types.shouldMatchMethod
import net.sourceforge.pmd.lang.java.types.typeDsl

/**
 * @author Clément Fournier
 */
class CtorInferenceTest : ProcessorTestSpec({


    parserTest("Results of diamond invoc and parameterized invoc are identical (normal classes)") {
        inContext(ExpressionParsingCtx) {

            val acu = parser.parse(
                    """
            class Gen<T> {

                static {

                    Gen<String> g = new Gen<String>(); 
                    g = new Gen<>(); 
                }
            }
            """)

            val (t_Gen) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }

            val (paramCall, genCall) = acu.descendants(ASTConstructorCall::class.java).toList()

            with (acu.typeDsl) {

                listOf(paramCall, genCall).forAll { call ->

                    call.methodType.shouldMatchMethod(
                            named = JConstructorSymbol.CTOR_NAME,
                            declaredIn = t_Gen[gen.t_String],
                            withFormals = emptyList(),
                            returning = t_Gen[gen.t_String]
                    ).also {
                        it.typeParameters shouldBe emptyList()
                        it.isGeneric shouldBe false
                    }

                }
            }
        }
    }

})
