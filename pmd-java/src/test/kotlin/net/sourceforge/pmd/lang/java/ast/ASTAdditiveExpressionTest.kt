/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe


class ASTAdditiveExpressionTest : ParserTestSpec({

    parserTest("Simple additive expression should be flat") {

        "1 + 2 + 3" should matchExpr<ASTAdditiveExpression> {
            it::getOperator shouldBe "+"

            child<ASTNumericLiteral> {
                it::getValueAsInt shouldBe 1
            }
            child<ASTNumericLiteral> {
                it::getValueAsInt shouldBe 2
            }
            child<ASTNumericLiteral> {
                it::getValueAsInt shouldBe 3
            }
        }
    }

    // TODO

})