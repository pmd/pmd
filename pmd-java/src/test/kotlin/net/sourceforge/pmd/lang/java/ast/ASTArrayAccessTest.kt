/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe

/**
 * Nodes that previously corresponded to ASTAllocationExpression.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTArrayAccessTest : ParserTestSpec({

    parserTest("Array access auto disambiguation") {

        inContext(ExpressionParsingCtx) {

            "a.b[0]" should parseAs {

                arrayAccess {
                    it::getQualifier shouldBe fieldAccess("b") {
                        it::getQualifier shouldBe ambiguousName("a")
                    }

                    it::getIndexExpression shouldBe int(0)
                }

            }


            "b[0]" should parseAs {

                arrayAccess {

                    it::getQualifier shouldBe variableAccess("b")


                    it::getIndexExpression shouldBe int(0)
                }
            }
        }
    }
})
