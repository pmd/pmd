package net.sourceforge.pmd.lang.java.ast

import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Earliest
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Latest
import java.io.IOException

/**
 * @author Clément Fournier
 * @since 6.10.0
 */
class ASTForStatementTest : FunSpec({


    parserTest("Test only init", javaVersions = Earliest..Latest) {

        importedTypes += IOException::class.java

        "for (int i = 0;;) while (true);" should matchStmt<ASTForStatement> {
            it.isForeach shouldBe false
            it.guardExpressionNode shouldBe null
            it.updateClause shouldBe null

            it.initClause shouldBe child {
                child<ASTLocalVariableDeclaration>(ignoreChildren = true) {}
            }

            it.body shouldBe child {

                child<ASTWhileStatement>(ignoreChildren = true) {}
            }
        }
    }


    parserTest("Test only guard", javaVersions = Earliest..Latest) {

        importedTypes += IOException::class.java

        "for (;true;) while (true);" should matchStmt<ASTForStatement> {
            it.isForeach shouldBe false
            it.updateClause shouldBe null

            it.initClause shouldBe null

            it.guardExpressionNode shouldBe child(ignoreChildren = true) { }

            it.body shouldBe child {

                child<ASTWhileStatement>(ignoreChildren = true) {}
            }
        }
    }


    parserTest("Test only update", javaVersions = Earliest..Latest) {

        importedTypes += IOException::class.java

        "for (;;i++) while (true);" should matchStmt<ASTForStatement> {
            it.isForeach shouldBe false
            it.initClause shouldBe null
            it.guardExpressionNode shouldBe null


            it.updateClause shouldBe child {
                child<ASTStatementExpressionList> {
                    child<ASTStatementExpression> {
                        child<ASTPostfixExpression>(ignoreChildren = true) {

                        }
                    }
                }
            }


            it.body shouldBe child {

                child<ASTWhileStatement>(ignoreChildren = true) {}
            }
        }
    }


    parserTest("Test update and guard", javaVersions = Earliest..Latest) {

        importedTypes += IOException::class.java

        "for (;true;i++) while (true);" should matchStmt<ASTForStatement> {
            it.isForeach shouldBe false
            it.initClause shouldBe null

            it.guardExpressionNode shouldBe child(ignoreChildren = true) { }

            it.updateClause shouldBe child {
                child<ASTStatementExpressionList> {
                    child<ASTStatementExpression> {
                        child<ASTPostfixExpression>(ignoreChildren = true) {

                        }
                    }
                }
            }


            it.body shouldBe child {

                child<ASTWhileStatement>(ignoreChildren = true) {}
            }
        }
    }




    parserTest("Test empty body", javaVersions = Earliest..Latest) {

        importedTypes += IOException::class.java

        "for (;;);" should matchStmt<ASTForStatement> {
            it.isForeach shouldBe false
            it.initClause shouldBe null
            it.guardExpressionNode shouldBe null
            it.updateClause shouldBe null


            it.body shouldBe child {
                child<ASTEmptyStatement> {  }
            }
        }
    }


})