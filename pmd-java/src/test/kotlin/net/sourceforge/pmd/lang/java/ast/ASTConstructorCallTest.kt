package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType.READ

class ASTConstructorCallTest : ParserTestSpec({

    parserTest("Class instance creation") {

        "new Foo(a)" should matchExpr<ASTConstructorCall> {

            it::getTypeNode shouldBe child {
                it::getTypeImage shouldBe "Foo"
            }

            it::getArguments shouldBe child {

                variableRef("a")
            }
        }

        "new <Bar> Foo<F>()" should matchExpr<ASTConstructorCall> {

            it::getExplicitTypeArguments shouldBe child {
                unspecifiedChild()
            }

            it::getTypeNode shouldBe child {
                it::getTypeImage shouldBe "Foo"

                it::getTypeArguments shouldBe child {
                    unspecifiedChild()
                }
            }

            it::getArguments shouldBe child {}
        }

        "new @Lol Foo<F>()" should matchExpr<ASTConstructorCall> {

            it::getExplicitTypeArguments shouldBe null

            it::getTypeNode shouldBe child {
                it::getTypeImage shouldBe "Foo"

                annotation("Lol")

                it::getTypeArguments shouldBe child {
                    unspecifiedChild()
                }
            }

            it::getArguments shouldBe child {}
        }
    }

    parserTest("Qualified class instance auto disambiguation") {
        /* JLS:
         *  A name is syntactically classified as an ExpressionName in these contexts:
         *       ...
         *     - As the qualifying expression in a qualified class instance creation expression (§15.9)*
         */
        // hence here it must be a field access

        "a.g.c.new Foo(a)" should matchExpr<ASTConstructorCall> {

            it::getLhsExpression shouldBe fieldAccess("c", READ) {
                it::getFieldName shouldBe "c"

                it::getLhsExpression shouldBe ambiguousName("a.g")
            }

            it::getTypeNode shouldBe child {
                it::getTypeImage shouldBe "Foo"
            }

            it::getArguments shouldBe child {

                variableRef("a")
            }
        }

        // and here a variable reference
        "a.new Foo(a)" should matchExpr<ASTConstructorCall> {

            it::getLhsExpression shouldBe variableRef("a")

            it::getTypeNode shouldBe child {
                it::getTypeImage shouldBe "Foo"
            }

            it::getArguments shouldBe child {

                variableRef("a")
            }
        }
    }


    parserTest("Qualified class instance creation") {

        "new O().new <Bar> Foo<F>()" should matchExpr<ASTConstructorCall> {

            it::getLhsExpression shouldBe child<ASTConstructorCall> {

                it::getTypeNode shouldBe classType("O")

                it::getArguments shouldBe child {}
            }

            it::getExplicitTypeArguments shouldBe child {
                unspecifiedChild()
            }

            it::getTypeNode shouldBe classType("Foo") {

                it::getTypeArguments shouldBe typeArgList()
            }

            it::getArguments shouldBe child {}
        }

        "method().new @Lol Foo<F>()" should matchExpr<ASTConstructorCall> {

            it::getLhsExpression shouldBe child<ASTMethodCall> {
                it::getMethodName shouldBe "method"
                it::getArguments shouldBe child {}
            }

            it::getExplicitTypeArguments shouldBe null

            it::getTypeNode shouldBe child {
                it::getTypeImage shouldBe "Foo"

                annotation("Lol")

                it::getTypeArguments shouldBe typeArgList()
            }

            it::getArguments shouldBe child {}
        }
    }

})
