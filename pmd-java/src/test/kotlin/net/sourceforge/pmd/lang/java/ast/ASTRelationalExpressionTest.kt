/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.ParserTestCtx.Companion.ExpressionParsingCtx


class ASTRelationalExpressionTest : ParserTestSpec({


    parserTest("Relational expressions operator") {

        "1 < 3" should matchExpr<ASTRelationalExpression> {
            it::getOp shouldBe BinaryOp.LT

            child<ASTNumericLiteral> {}

            child<ASTNumericLiteral> {}
        }


        "1 <= 3" should matchExpr<ASTRelationalExpression> {
            it::getOp shouldBe BinaryOp.LE

            child<ASTNumericLiteral> {}

            child<ASTNumericLiteral> {}
        }

        "1 > 3" should matchExpr<ASTRelationalExpression> {
            it::getOp shouldBe BinaryOp.GT

            child<ASTNumericLiteral> {}

            child<ASTNumericLiteral> {}
        }

        "1 >= 3" should matchExpr<ASTRelationalExpression> {
            it::getOp shouldBe BinaryOp.GE

            child<ASTNumericLiteral> {}

            child<ASTNumericLiteral> {}
        }
    }


    parserTest("Relational expressions cannot be nested") {

        "1 < 3" should matchExpr<ASTRelationalExpression> {
            it::getOp shouldBe BinaryOp.LT

            child<ASTNumericLiteral> {
                it::getValueAsInt shouldBe 1
            }

            child<ASTNumericLiteral> {
                it::getValueAsInt shouldBe 3
            }
        }

        "1 < 3 < 4" should notParseIn(ExpressionParsingCtx)

    }

    parserTest("Relational expressions precedence") {

        "1 < 3 instanceof Boolean" should matchExpr<ASTInstanceOfExpression> {

            child<ASTRelationalExpression> {

                child<ASTNumericLiteral> {
                    it::getValueAsInt shouldBe 1
                }

                child<ASTNumericLiteral> {
                    it::getValueAsInt shouldBe 3
                }
            }

            it::getTypeNode shouldBe child<ASTClassOrInterfaceType>(ignoreChildren = true) {}
        }

        "1 == 3 < 4" should matchExpr<ASTEqualityExpression> {
            child<ASTNumericLiteral> {}

            child<ASTRelationalExpression> {

                child<ASTNumericLiteral> {}
                child<ASTNumericLiteral> {}
            }
        }

        "1 < 3 + 4 instanceof Boolean" should matchExpr<ASTInstanceOfExpression> {

            child<ASTRelationalExpression> {

                child<ASTNumericLiteral> {
                    it::getValueAsInt shouldBe 1
                }

                child<ASTAdditiveExpression> {
                    child<ASTNumericLiteral> {}
                    child<ASTNumericLiteral> {}
                }
            }

            it::getTypeNode shouldBe child<ASTClassOrInterfaceType>(ignoreChildren = true) {}
        }

    }

})
