/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import io.kotlintest.matchers.collections.shouldContainExactly
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Latest
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J1_8

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTIntersectionTypeTest : ParserTestSpec({

    parserTest("Test intersection in cast", javaVersions = J1_8..Latest) {

        "(Foo & Bar) obj" should matchExpr<ASTCastExpression> {
            it::getCastType shouldBe child<ASTIntersectionType> {

                val foo = child<ASTClassOrInterfaceType> {
                    it::getTypeImage shouldBe "Foo"
                }

                val bar = child<ASTClassOrInterfaceType> {
                    it::getTypeImage shouldBe "Bar"
                }

                it.toList().shouldContainExactly(foo, bar)
            }

            it::getCastExpression shouldBe child<ASTVariableReference> {
                it::getVariableName shouldBe "obj"
            }
        }


        "(@A Foo & Bar) obj" should matchExpr<ASTCastExpression> {
            it::getCastType shouldBe child<ASTIntersectionType> {

                val foo = child<ASTAnnotatedType> {

                    it::getDeclaredAnnotations shouldBe listOf(child(ignoreChildren = true) {
                        it::getAnnotationName shouldBe "A"
                    })

                    child<ASTClassOrInterfaceType> {
                        it::getTypeImage shouldBe "Foo"
                    }
                }

                val bar = child<ASTClassOrInterfaceType> {
                    it::getTypeImage shouldBe "Bar"
                }

                it.toList().shouldContainExactly(foo, bar)
            }

            it::getCastExpression shouldBe child<ASTVariableReference> {
                it::getVariableName shouldBe "obj"
            }
        }

    }

})