/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.ParserTestCtx.Companion.ExpressionParsingCtx

class ASTCastExpressionTest : ParserTestSpec({

    parserTest("Class instance creation") {

        "(Foo) obj" should matchExpr<ASTCastExpression> {

            it::getCastType shouldBe child<ASTClassOrInterfaceType> {

            }

            unspecifiedChild()
        }

        "(@F Foo) obj" should matchExpr<ASTCastExpression> {

            child<ASTMarkerAnnotation> {

            }

            it::getCastType shouldBe child<ASTClassOrInterfaceType> {

            }

            unspecifiedChild()
        }
    }


    parserTest("Test intersection in cast", javaVersions = JavaVersion.J1_8..JavaVersion.Latest) {

        "(@F Foo & Bar) obj" should matchExpr<ASTCastExpression> {

            child<ASTMarkerAnnotation> {

            }

            it::getCastType shouldBe child<ASTIntersectionType> {

                child<ASTClassOrInterfaceType> {
                    it::getTypeImage shouldBe "Foo"
                }

                child<ASTClassOrInterfaceType> {
                    it::getTypeImage shouldBe "Bar"
                }

            }

            unspecifiedChild()
        }

        "(@F Foo & @B Bar) obj" should notParseIn(ExpressionParsingCtx)
    }


})
