/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.ast.JavaVersion.*
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol
import net.sourceforge.pmd.lang.java.types.*
import java.util.ArrayList

/**
 * @author Clément Fournier
 */
class Java7InferenceTest : ProcessorTestSpec({


    parserTest("Java 7 uses return constraints only if args are not enough", javaVersion = J1_7) {

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
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

    parserTest("Same test in java 8", javaVersion = J1_8) {

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
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


    parserTest("Java 7 uses return constraints if needed", javaVersion = J1_7) {

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
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


    parserTest("Java 7 doesn't let context flow through ternary", javaVersion = J1_7) {

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            import java.util.List;
            import java.util.ArrayList;
            class Gen<T> {
                 public void test() {
                    final List<String> l2;
                    // the conditional expr is inferred to ArrayList<Object>
                    l2 = true ? new ArrayList<>()
                              : new ArrayList<>();
                }
            }
            """
        )

        val (conditional) = acu.descendants(ASTConditionalExpression::class.java).toList()

        spy.shouldBeOk {
            // no context

            conditional.thenBranch.conversionContextType.shouldBeNull()
            conditional.thenBranch shouldHaveType java.util.ArrayList::class[ts.OBJECT]
            conditional.elseBranch shouldHaveType java.util.ArrayList::class[ts.OBJECT]

            // and List<String> on java 8
            conditional shouldHaveType java.util.ArrayList::class[ts.OBJECT]
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
