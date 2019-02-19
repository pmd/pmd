package net.sourceforge.pmd.lang.java.ast

import io.kotlintest.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe

/**
 * @author Clément Fournier
 */
class ASTFieldAccessTest : ParserTestSpec({

    parserTest("Field access exprs") {
        "Type.this.foo" should matchExpr<ASTFieldAccess> {
            it::getFieldName shouldBe "foo"
            it::getImage shouldBe "foo"

            it::getLhsExpression shouldBePresent child<ASTThisExpression> {
                it::getQualifier shouldBePresent child {
                    it.typeArguments.shouldBeEmpty()
                    it.typeImage shouldBe "Type"
                }
            }
        }

        "foo().foo" should matchExpr<ASTFieldAccess> {

            it::getFieldName shouldBe "foo"
            it::getImage shouldBe "foo"

            it::getLhsExpression shouldBePresent child<ASTMethodCall> {
                it::getLhsExpression.shouldBeEmpty()
                it::getMethodName shouldBe "foo"
                it::getImage shouldBe "foo"

                it::getArguments shouldBe child {}
            }
        }


        "a.b.c" should matchExpr<ASTFieldAccess> {
            it::getImage shouldBe "c"
            it::getFieldName shouldBe "c"

            it::getLhsExpression shouldBePresent child<ASTAmbiguousName> {
                it.name shouldBe "a.b"
            }
        }


        "a" should matchExpr<ASTVariableReference> {
            it::getVariableName shouldBe "a"
        }
    }
})