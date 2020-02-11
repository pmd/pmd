/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import io.kotlintest.shouldBe
import net.sourceforge.pmd.lang.ast.TextAvailableNode
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind.*

// Use a string for comparison because CharSequence are not necessarily
// equatable to string
private val TextAvailableNode.textStr: String get() = text.toString()

class JavaTextAccessTest : ParserTestSpec({


    parserTest("Test parens") {

        inContext(StatementParsingCtx) {

            "int a = ((3));" should parseAs {
                localVarDecl {

                    it.textStr shouldBe "int a = ((3));"

                    modifiers {
                        it.textStr shouldBe ""
                    }

                    primitiveType(INT) {
                        it.textStr shouldBe "int"
                    }
                    variableDeclarator("a") {
                        it.textStr shouldBe "a = ((3))"

                        it::getInitializer shouldBe int(3) {
                            it.textStr shouldBe "((3))"
                        }
                    }
                }
            }

            "int a = ((a)).f;" should parseAs {
                localVarDecl {

                    it.textStr shouldBe "int a = ((a)).f;"

                    modifiers {
                        it.textStr shouldBe ""
                    }

                    primitiveType(INT) {
                        it.textStr shouldBe "int"
                    }
                    variableDeclarator("a") {
                        it.textStr shouldBe "a = ((a)).f"

                        it::getInitializer shouldBe fieldAccess("f") {
                            it.textStr shouldBe "((a)).f"

                            it::getQualifier shouldBe variableAccess("a") {
                                it.textStr shouldBe "((a))"
                            }
                        }
                    }
                }
            }

            // the left parens shouldn't be flattened by AbstractLrBinaryExpr
            "int a = ((1 + 2) + f);" should parseAs {
                localVarDecl {

                    it.textStr shouldBe "int a = ((1 + 2) + f);"

                    modifiers {
                        it.textStr shouldBe ""
                    }

                    primitiveType(INT) {
                        it.textStr shouldBe "int"
                    }

                    variableDeclarator("a") {
                        it.textStr shouldBe "a = ((1 + 2) + f)"

                        it::getInitializer shouldBe infixExpr(BinaryOp.ADD) {
                            it.textStr shouldBe "((1 + 2) + f)"

                            infixExpr(BinaryOp.ADD) {
                                it.textStr shouldBe "(1 + 2)"

                                int(1) {
                                    it.textStr shouldBe "1"
                                }
                                int(2) {
                                    it.textStr shouldBe "2"
                                }
                            }

                            variableAccess("f") {
                                it.textStr shouldBe "f"
                            }
                        }
                    }
                }
            }
        }
    }
})
