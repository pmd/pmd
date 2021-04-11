/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.types.internal.infer

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall
import net.sourceforge.pmd.lang.java.ast.ProcessorTestSpec
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol
import net.sourceforge.pmd.lang.java.types.parseWithTypeInferenceSpy
import net.sourceforge.pmd.lang.java.types.shouldMatchMethod

/**
 * @author Clément Fournier
 */
class Java7InferenceTest : ProcessorTestSpec({


    parserTest("Java 7 uses return constraints only if args are not enough") {

        val (acu, spy) = parser.withDefaultVersion("7").parseWithTypeInferenceSpy(
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
            val `t_Class{String}` = Class::class[gen.t_String]

            // inferred to Gen<Class<String>> on java 7
            genCall.methodType.shouldMatchMethod(
                named = JConstructorSymbol.CTOR_NAME,
                declaredIn = t_Gen[`t_Class{String}`],
                withFormals = listOf(`t_Class{String}`),
                returning = t_Gen[`t_Class{String}`]
            )
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
            // inferred to Gen<Class<String>> on java 7
            val `t_Class{?}` = Class::class[`?`]

            genCall.methodType.shouldMatchMethod(
                named = JConstructorSymbol.CTOR_NAME,
                declaredIn = t_Gen[`t_Class{?}`],
                withFormals = listOf(`t_Class{?}`),
                returning = t_Gen[`t_Class{?}`]
            )

        }
    }


})
