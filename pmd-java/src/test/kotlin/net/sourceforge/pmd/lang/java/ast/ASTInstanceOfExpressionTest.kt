/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind

class ASTInstanceOfExpressionTest : ParserTestSpec({

    parserTest("InstanceofExpression can be annotated") {

        inContext(ExpressionParsingCtx) {

            "f instanceof @A K" should parseAs {
                infixExpr(BinaryOp.INSTANCEOF) {
                    it::getLeftOperand shouldBe variableAccess("f")
                    it::getRightOperand shouldBe typeExpr {
                        classType("K") {
                            annotation("A")
                        }
                    }
                }
            }
        }
    }

    parserTest("Instanceof with pattern") {

        inContext(ExpressionParsingCtx) {

            "o instanceof String s && s.length() > 4" should parseAs {
                infixExpr(BinaryOp.CONDITIONAL_AND) {
                    infixExpr(BinaryOp.INSTANCEOF) {
                        it::getLeftOperand shouldBe variableAccess("o")
                        it::getRightOperand shouldBe patternExpr {
                            typePattern {
                                modifiers()
                                classType("String")
                                variableId("s")
                            }
                        }
                    }
                    infixExpr(BinaryOp.GT) {
                        it::getLeftOperand shouldBe methodCall("length")
                        it::getRightOperand shouldBe number {  }
                    }
                }
            }
        }
    }

    parserTest("InstanceofExpression cannot test primitive types") {

        inContext(ExpressionParsingCtx) {
            PrimitiveTypeKind.values().map { it.simpleName }.forEach {
                "f instanceof $it" shouldNot parse()
                "f instanceof @A $it" shouldNot parse()
            }
        }
    }

})
