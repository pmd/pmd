/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.types.internal.infer

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall
import net.sourceforge.pmd.lang.java.ast.ProcessorTestSpec
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol
import net.sourceforge.pmd.lang.java.types.*

/**
 * @author Clément Fournier
 */
class Java7InferenceTest : ProcessorTestSpec({


    parserTest("Java 7 uses return constraints only if args are not enough") {

        val java7Parser = parser.withDefaultVersion("7")

        val (acu, spy) = java7Parser.parseWithTypeInferenceSpy(
            """
            class Gen<T> {
                Gen(T t) {}
                static {
                    // inferred to Gen<Class<String>>, then fails type checking because cannot be stored in Gen<Class<?>>
                    Gen<Class<?>> g = new Gen<>(String.class);
                }
            }
            """
        )

        val (t_Gen) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }
        val (genCall) = acu.descendants(ASTConstructorCall::class.java).toList()

        spy.shouldBeOk {
            // inferred to Gen<Class<String>> on java 7
            ctorInfersTo(genCall, inferredType = t_Gen[Class::class[gen.t_String]])
        }
    }

    parserTest("Same test in java 8") {

        val (acu, spy) = parser.withDefaultVersion("8").parseWithTypeInferenceSpy(
            """
            class Gen<T> {
                Gen(T t) {}
                static {
                    // inferred to Gen<Class<?>>
                    Gen<Class<?>> g = new Gen<>(String.class);
                }
            }
            """
        )

        val (t_Gen) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }

        val (genCall) = acu.descendants(ASTConstructorCall::class.java).toList()

        spy.shouldBeOk {
            ctorInfersTo(genCall, inferredType = t_Gen[Class::class[`?`]])
        }
    }


    parserTest("Java 7 uses return constraints if needed") {

        val java7Parser = parser.withDefaultVersion("7")
        val (acu, spy) = java7Parser.parseWithTypeInferenceSpy(
            """
            class Gen<T> {
                static {
                    // inferred to Gen<Class<?>>
                    Gen<Class<?>> g = new Gen<>();
                }
            }
            """
        )

        val (t_Gen) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }

        val (genCall) = acu.descendants(ASTConstructorCall::class.java).toList()

        spy.shouldBeOk {
            ctorInfersTo(genCall, inferredType = t_Gen[Class::class[`?`]])
        }
    }


})

private fun TypeDslMixin.ctorInfersTo(
    genCall: ASTConstructorCall,
    inferredType: JClassType
) {
    genCall.methodType.shouldMatchMethod(
        named = JConstructorSymbol.CTOR_NAME,
        declaredIn = inferredType,
        returning = inferredType
    )
}
