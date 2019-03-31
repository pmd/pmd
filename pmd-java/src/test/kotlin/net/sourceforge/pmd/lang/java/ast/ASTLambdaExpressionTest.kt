/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.ParserTestCtx.Companion.ExpressionParsingCtx
import net.sourceforge.pmd.lang.java.ast.ParserTestCtx.Companion.StatementParsingCtx


class ASTLambdaExpressionTest : ParserTestSpec({

    parserTest("Simple lambda expressions") {

        "a -> foo()" should matchExpr<ASTLambdaExpression> {
            it::isExpressionBody shouldBe true
            it::isBlockBody shouldBe false

            it::getParameters shouldBe child {
                child<ASTLambdaParameter> {
                    variableId("a")
                }
            }


            child<ASTMethodCall>(ignoreChildren = true) {}
        }

        "(a,b) -> foo()" should matchExpr<ASTLambdaExpression> {
            it::isExpressionBody shouldBe true
            it::isBlockBody shouldBe false

            it::getParameters shouldBe child {
                child<ASTLambdaParameter> {
                    variableId("a")

                }
                child<ASTLambdaParameter> {
                    variableId("b")
                }
            }


            child<ASTMethodCall>(ignoreChildren = true) {}
        }

        "(a,b) -> { foo(); } " should matchExpr<ASTLambdaExpression> {
            it::isExpressionBody shouldBe false
            it::isBlockBody shouldBe true

            it::getParameters shouldBe child {
                child<ASTLambdaParameter> {
                    variableId("a")

                }
                child<ASTLambdaParameter> {
                    variableId("b")
                }
            }


            child<ASTBlock>(ignoreChildren = true) {}
        }

        "(final int a, @F List<String> b) -> foo()" should matchExpr<ASTLambdaExpression> {
            it::isExpressionBody shouldBe true
            it::isBlockBody shouldBe false

            it::getParameters shouldBe child {
                child<ASTLambdaParameter> {
                    it::isFinal shouldBe true

                    it::getTypeNode shouldBe child<ASTPrimitiveType> {}

                    variableId("a")
                }
                child<ASTLambdaParameter> {
                    annotation()
                    it::getTypeNode shouldBe child<ASTClassOrInterfaceType>(ignoreChildren = true) {}
                    variableId("b")
                }
            }


            child<ASTMethodCall>(ignoreChildren = true) {}
        }

    }

    parserTest("Negative lambda contexts") {

        "a -> {}" shouldNot parseIn(StatementParsingCtx)
//        "a -> {} + 4" shouldNot parseIn(ExpressionParsingCtx)

    }

    parserTest("Positive lambda contexts") {

        "(a -> {})" should parseIn(ExpressionParsingCtx)

    }

})
