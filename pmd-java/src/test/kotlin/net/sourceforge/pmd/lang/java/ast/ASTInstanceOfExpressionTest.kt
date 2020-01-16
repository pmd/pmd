/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType.PrimitiveType

class ASTInstanceOfExpressionTest : ParserTestSpec({

    parserTest("InstanceofExpression can be annotated") {

        inContext(ExpressionParsingCtx) {

            "f instanceof @A K" should parseAs {
                instanceOfExpr {
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

    parserTest("InstanceofExpression cannot test primitive types") {

        inContext(ExpressionParsingCtx) {
            PrimitiveType.values().map { it.token }.forEach {
                "f instanceof $it" shouldNot parse()
                "f instanceof @A $it" shouldNot parse()
            }
        }
    }

})
