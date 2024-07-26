/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast

import com.github.oowekyala.treeutils.matchers.TreeNodeWrapper
import net.sourceforge.pmd.lang.test.ast.shouldBe
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind

class ASTInstanceOfExpressionTest : ParserTestSpec({
    parserTestContainer("InstanceofExpression can be annotated") {
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

    parserTestContainer("Instanceof with pattern") {
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

    parserTestContainer("InstanceofExpression cannot test primitive types", JavaVersion.except(JavaVersion.J23__PREVIEW)) {
        inContext(ExpressionParsingCtx) {
            PrimitiveTypeKind.values().map { it.simpleName }.forEach {
                "f instanceof $it" shouldNot parse()
                "f instanceof @A $it" shouldNot parse()
            }
        }
    }

    // since Java 23 Preview, primitive types in instanceof are possible (JEP 455)
    parserTestContainer("InstanceofExpression can test primitive types", JavaVersion.J23__PREVIEW) {
        inContext(ExpressionParsingCtx) {
            PrimitiveTypeKind.values().forEach { typeKind ->
                "f instanceof ${typeKind.simpleName}" should parseAs {
                    infixExpr(BinaryOp.INSTANCEOF) {
                        it::getLeftOperand shouldBe variableAccess("f")
                        it::getRightOperand shouldBe typeExpr {
                            primitiveType(typeKind)
                        }
                    }
                }

                "f instanceof @A ${typeKind.simpleName}" should parseAs {
                    infixExpr(BinaryOp.INSTANCEOF) {
                        it::getLeftOperand shouldBe variableAccess("f")
                        it::getRightOperand shouldBe typeExpr {
                            primitiveType(typeKind) {
                                annotation("A")
                            }
                        }
                    }
                }
            }
        }
    }
})
