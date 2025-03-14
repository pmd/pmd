/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.test.ast.shouldBe

/**
 * @author Clément Fournier
 */
class ASTFieldAccessTest : ParserTestSpec({
    parserTestContainer("Field access expressions") {
        inContext(ExpressionParsingCtx) {
            "Type.this.foo" should parseAs {
                fieldAccess("foo") {

                    it::getQualifier shouldBe child<ASTThisExpression> {
                        it::getQualifier shouldBe classType("Type")
                    }
                }
            }

            "foo().foo" should parseAs {
                fieldAccess("foo") {

                    it::getQualifier shouldBe child<ASTMethodCall> {
                        it::getQualifier shouldBe null
                        it::getMethodName shouldBe "foo"

                        it::getArguments shouldBe child {}
                    }
                }
            }

            "a.b.c" should parseAs {
                fieldAccess("c") {
                    val fieldAccess = it

                    it::getQualifier shouldBe child<ASTAmbiguousName> {
                        it::getName shouldBe "a.b"
                        // test the parent is set correctly
                        it::getParent shouldBe fieldAccess
                    }
                }
            }

            "a" should parseAs {
                variableAccess("a")
            }
        }
    }
})
