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
                    annotation()
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
            // We allow lambdas in any parenthesized expression
            // this is weaker than the JLS, but they use semantics to forbid this
            "(a -> {})" should parse()
            // conditional expression
            "(cond ? a -> b : () -> c)" should parse()
            "cond ? () -> {} : a -> c" should parse()
            "cond ? () -> b : (() -> c)" should parse()
            "cond ? (a -> {}) : () -> c" should parse()
            "a = cond ? (a -> {}) : () -> c" should parse()
            // method or constructor arg
            "foo(a = cond ? (a -> {}) : () -> c)" should parse()
            "foo(() -> c)" should parse()
            "new Foo(() -> c)" should parse()
            "new Foo(a -> c)" should parse()
            "new Foo(a -> {})" should parse()
            // cast subject
            "(Cast) () -> c" should parse()
            // RHS of assignments
            "k = () -> c" should parse()
        }

        inContext(StatementParsingCtx) {
            //  RHS of assignments
            "St f = cond ? a -> b : () -> c;" should parse()
            "St f = () -> c;" should parse()
            "St f = k = () -> c;" should parse()
            "St f = (k = () -> c);" should parse()
            "St f = (() -> c);" should parse()
        }
    }

})
