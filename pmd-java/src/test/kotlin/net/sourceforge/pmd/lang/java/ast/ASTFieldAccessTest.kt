package net.sourceforge.pmd.lang.java.ast

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import net.sourceforge.pmd.lang.ast.test.shouldBe

/**
 * @author Clément Fournier
 */
class ASTFieldAccessTest : ParserTestSpec({

    parserTest("Field access exprs") {
        "Type.this.foo" should matchExpr<ASTFieldAccess> {
            it::getFieldName shouldBe "foo"
            it::getImage shouldBe "foo"

            it::getQualifier shouldBe child<ASTThisExpression> {
                it::getQualifier shouldBe child {
                    it.typeArguments shouldBe null
                    it.typeImage shouldBe "Type"
                }
            }
        }

        "foo().foo" should matchExpr<ASTFieldAccess> {

            it::getFieldName shouldBe "foo"
            it::getImage shouldBe "foo"

            it::getQualifier shouldBe child<ASTMethodCall> {
                it::getQualifier shouldBe null
                it::getMethodName shouldBe "foo"
                it::getImage shouldBe "foo"

                it::getArguments shouldBe child {}
            }
        }


        "a.b.c" should matchExpr<ASTFieldAccess> {
            it::getImage shouldBe "c"
            it::getFieldName shouldBe "c"

            val fieldAccess = it

            it::getQualifier shouldBe child<ASTAmbiguousName> {
                it::getName shouldBe "a.b"
                // test the parent is set correctly
                it::getParent shouldBe fieldAccess
            }
        }


        "a" should matchExpr<ASTVariableAccess> {
            it::getVariableName shouldBe "a"
            it::getParent shouldNotBe null
        }
    }
})
