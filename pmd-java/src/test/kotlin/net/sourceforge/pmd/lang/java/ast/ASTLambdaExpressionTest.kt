/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe


class ASTLambdaExpressionTest : ParserTestSpec({

    parserTest("Simple lambda expressions") {

        "a -> foo()" should matchExpr<ASTLambdaExpression> {
            it::isExpressionBody shouldBe true
            it::isBlockBody shouldBe false

            it::getParameters shouldBe child {
                child<ASTLambdaParameter> {
                    child<ASTVariableDeclaratorId> {

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
                    child<ASTVariableDeclaratorId> {

                    }
                }
                child<ASTLambdaParameter> {
                    child<ASTVariableDeclaratorId> {

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
                    child<ASTVariableDeclaratorId> {

                    }
                }
                child<ASTLambdaParameter> {
                    child<ASTVariableDeclaratorId> {

                    }
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

                    child<ASTVariableDeclaratorId> {}
                }
                child<ASTLambdaParameter> {
                    child<ASTAnnotation> {}
                    it::getTypeNode shouldBe child<ASTClassOrInterfaceType>(ignoreChildren = true) {}

                    child<ASTVariableDeclaratorId> {}
                }
            }


            child<ASTMethodCall>(ignoreChildren = true) {}
        }

    }

    parserTest("Changing operators should push a new node") {

    }

})
