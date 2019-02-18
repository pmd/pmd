package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTExpressionTest : ParserTestSpec({

    parserTest("this keyword") {

        "this" should matchExpr<ASTThisExpression> { }

        "Type.this" should matchExpr<ASTThisExpression> {

            it::getQualifier shouldBePresent child {
                it::getImage shouldBe "Type"
            }
        }

    }

    parserTest("Field access exprs") {

        "Type.this.foo" should matchExpr<ASTFieldAccess> {
            it::getFieldName shouldBe "foo"
            it::getImage shouldBe "foo"

            it::getLeftHandSide shouldBePresent child<ASTThisExpression> {
                it::getQualifier shouldBePresent child<ASTAmbiguousNameExpr> { }
            }
        }

        "foo().foo" should matchExpr<ASTFieldAccess> {

            it::getFieldName shouldBe "foo"
            it::getImage shouldBe "foo"

            it::getLeftHandSide shouldBePresent child<ASTMethodCall> {
                it::getLhsExpression.shouldBeEmpty()
                it::getMethodName shouldBe "foo"
                it::getImage shouldBe "foo"

                it::getArguments shouldBe child {}
            }
        }

    }


    parserTest("Ambiguous names") {

        "a.b.c" should matchExpr<ASTAmbiguousNameExpr> {
            it::getImage shouldBe "a.b.c"
        }

        "a" should matchExpr<ASTAmbiguousNameExpr> {
            it::getImage shouldBe "a"
        }
    }
})