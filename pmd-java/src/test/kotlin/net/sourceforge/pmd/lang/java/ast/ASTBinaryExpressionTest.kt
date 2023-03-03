/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.java.ast.BinaryOp.*

/*
    Tests for the rest of binary expressions
 */
class ASTBinaryExpressionTest : ParserTestSpec({


    parserTest("Conditional and logical operators") {

        inContext(ExpressionParsingCtx) {
            "a && b && a || b" should parseAs {
                infixExpr(CONDITIONAL_OR) {
                    infixExpr(CONDITIONAL_AND) {
                        infixExpr(CONDITIONAL_AND) {
                            variableAccess("a")
                            variableAccess("b")
                        }
                        variableAccess("a")
                    }

                    variableAccess("b")
                }
            }

            "a && b && a | b" should parseAs {
                infixExpr(CONDITIONAL_AND) {
                    infixExpr(CONDITIONAL_AND) {
                        variableAccess("a")
                        variableAccess("b")
                    }
                    infixExpr(OR) {
                        variableAccess("a")
                        variableAccess("b")
                    }
                }
            }

            "a | b ^ a & b" should parseAs {
                infixExpr(OR) {
                    variableAccess("a")
                    infixExpr(XOR) {
                        variableAccess("b")
                        infixExpr(AND) {
                            variableAccess("a")
                            variableAccess("b")
                        }
                    }
                }
            }
        }
    }

})
