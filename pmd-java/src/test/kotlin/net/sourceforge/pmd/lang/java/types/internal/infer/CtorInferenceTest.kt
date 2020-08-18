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

        with(acu.typeDsl) {

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

    parserTest("Enum constant ctors") {

        val acu = parser.parse(
                """

            import java.util.function.Function;
            enum E {
                A,
                B(),
                C(1),
                D(1.0),
                ;

                E(int i) {}
                E(double c) {}
                E() {}
            }
            """)

        val (t_E) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }
        val (intCtor, doubleCtor, defaultCtor) = acu.descendants(ASTConstructorDeclaration::class.java).toList { it.symbol }

        val (a, b, c, d) = acu.descendants(ASTEnumConstant::class.java).toList()

        with(acu.typeDsl) {

            listOf(a, b).forAll {
                it.methodType.symbol shouldBe defaultCtor
            }

            c.methodType.shouldMatchMethod(
                    named = JConstructorSymbol.CTOR_NAME,
                    declaredIn = t_E,
                    withFormals = listOf(int),
                    returning = t_E
            ).also { it.symbol shouldBe intCtor }

            d.methodType.shouldMatchMethod(
                    named = JConstructorSymbol.CTOR_NAME,
                    declaredIn = t_E,
                    withFormals = listOf(double),
                    returning = t_E
            ).also { it.symbol shouldBe doubleCtor }

        }
    }


    parserTest("Generic enum constant ctors") {
        logTypeInference(true)

        val acu = parser.parse(
                """

            import java.util.function.Function;
            enum E {
                A(1.0, i -> i + 1),
                ;

                E() {}
                E(double c, double k) {}
                <T> E(T c, Function<? super T, ? extends T> fun) {}
            }
            """)

        val (t_E) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }
        val (_, _, genericCtor) = acu.descendants(ASTConstructorDeclaration::class.java).toList { it.symbol }

        val (a) = acu.descendants(ASTEnumConstant::class.java).toList()

        with(acu.typeDsl) {

            a.methodType.shouldMatchMethod(
                    named = JConstructorSymbol.CTOR_NAME,
                    declaredIn = t_E,
                    withFormals = listOf(double.box(), gen.t_Function[`?` `super` double.box(), `?` extends double.box()]),
                    returning = t_E
            ).also { it.symbol shouldBe genericCtor }
        }

    }

    parserTest("Anonymous enum ctor") {

        val acu = parser.parse(
                """

            import java.util.function.Function;
            enum E {
                A { }
            }
            """)

        val (t_E) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }

        val (a) = acu.descendants(ASTEnumConstant::class.java).toList()


        a.methodType.shouldMatchMethod(
                named = JConstructorSymbol.CTOR_NAME,
                declaredIn = t_E,
                withFormals = emptyList(),
                returning = t_E // not the anonymous type
        )

    }

})
