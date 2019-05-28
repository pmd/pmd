/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType.PrimitiveType.INT
import net.sourceforge.pmd.lang.java.ast.ParserTestCtx.Companion.ExpressionParsingCtx
import net.sourceforge.pmd.lang.java.ast.ParserTestCtx.Companion.StatementParsingCtx


class ASTLambdaExpressionTest : ParserTestSpec({

    parserTest("Simple lambda expressions") {

        "a -> foo()" should matchExpr<ASTLambdaExpression> {
            it::isExpressionBody shouldBe true
            it::isBlockBody shouldBe false

            it::getParameters shouldBe child {
                child<ASTLambdaParameter> {
                    variableId("a") {
                        it::isTypeInferred shouldBe true
                        it::isLambdaParameter shouldBe true
                    }
                }
            }


            child<ASTMethodCall>(ignoreChildren = true) {}
        }

        "(a,b) -> foo()" should matchExpr<ASTLambdaExpression> {
            it::isExpressionBody shouldBe true
            it::isBlockBody shouldBe false

            it::getParameters shouldBe child {
                child<ASTLambdaParameter> {
                    variableId("a") {
                        it::isTypeInferred shouldBe true
                        it::isLambdaParameter shouldBe true
                    }

                }
                child<ASTLambdaParameter> {
                    variableId("b") {
                        it::isTypeInferred shouldBe true
                        it::isLambdaParameter shouldBe true
                    }
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

                    it::getTypeNode shouldBe primitiveType(INT)

                    variableId("a") {
                        it::isFinal shouldBe true
                        it::isLambdaParameter shouldBe true
                        it::isTypeInferred shouldBe false
                    }
                }
                child<ASTLambdaParameter> {
                    annotationList {
                        annotation()
                    }
                    it::getTypeNode shouldBe classType("List")
                    variableId("b") {
                        it::isFinal shouldBe false
                        it::isLambdaParameter shouldBe true
                        it::isTypeInferred shouldBe false
                    }
                }
            }


            child<ASTMethodCall>(ignoreChildren = true) {}
        }

    }

    parserTest("Negative lambda contexts") {
        inContext(StatementParsingCtx) {
            "a -> {}" shouldNot parse()
        }
    }

    parserTest("Positive lambda contexts") {

        inContext(ExpressionParsingCtx) {
            "(a -> {})" should parse()
        }
    }

})
