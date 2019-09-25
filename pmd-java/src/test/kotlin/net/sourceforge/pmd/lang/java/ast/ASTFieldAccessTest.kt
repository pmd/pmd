package net.sourceforge.pmd.lang.java.ast

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import net.sourceforge.pmd.lang.ast.test.parent
import net.sourceforge.pmd.lang.ast.test.shouldBe

/**
 * @author Clément Fournier
 */
class ASTFieldAccessTest : ParserTestSpec({

    parserTest("Field access exprs") {
        "Type.this.foo" should matchExpr<ASTFieldAccess> {
            it::getFieldName shouldBe "foo"
            it::getImage shouldBe "foo"

            it::getLhs shouldBe child<ASTThisExpression> {
                it::getQualifier shouldBe child {
                    it.typeArguments shouldBe null
                    it.typeImage shouldBe "Type"
                }
            }
        }

        "foo().foo" should matchExpr<ASTFieldAccess> {

            it::getFieldName shouldBe "foo"
            it::getImage shouldBe "foo"

            it::getLhs shouldBe child<ASTMethodCall> {
                it::getLhs shouldBe null
                it::getMethodName shouldBe "foo"
                it::getImage shouldBe "foo"

                it::getArguments shouldBe child {}
            }
        }


        "a.b.c" should matchExpr<ASTFieldAccess> {
            it::getImage shouldBe "c"
            it::getFieldName shouldBe "c"

            val fieldAccess = it

            it::getLhs shouldBe child<ASTAmbiguousName> {
                it::getName shouldBe "a.b"
                // test the parent is set correctly
                it::parent shouldBe fieldAccess
            }
        }


        "a" should matchExpr<ASTVariableAccess> {
            it::getVariableName shouldBe "a"
            it::parent shouldNotBe null
        }
    }
})
