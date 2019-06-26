/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Latest
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J1_7
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J9
import net.sourceforge.pmd.lang.java.ast.ParserTestCtx.Companion.StatementParsingCtx

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTTryStatementTest : ParserTestSpec({
    parserTest("Test try with resources", javaVersions = J1_7..Latest) {

        "try (Foo a = 2){}" should matchStmt<ASTTryStatement> {

            child<ASTResourceSpecification> {
                child<ASTResources> {
                    child<ASTResource> {
                        child<ASTClassOrInterfaceType> {}
                        child<ASTVariableDeclaratorId> {}
                        child<ASTNumericLiteral> {}
                    }
                }
            }
            child<ASTBlock> {}
        }

        "try (final Foo a = 2){}" should matchStmt<ASTTryStatement> {

            child<ASTResourceSpecification> {
                child<ASTResources> {
                    child<ASTResource> {
                        it::isFinal shouldBe true
                        child<ASTClassOrInterfaceType> {}
                        child<ASTVariableDeclaratorId> {}
                        child<ASTNumericLiteral> {}
                    }
                }
            }

            child<ASTBlock> {}
        }

    }
    parserTest("Test concise try with resources", javaVersions = J9..Latest) {


        "try (a){}" should matchStmt<ASTTryStatement> {

            child<ASTResourceSpecification> {
                child<ASTResources> {
                    child<ASTResource> {
                        variableRef("a")
                    }
                }
            }


            child<ASTBlock> {}

        }


        "try (a.b){}" should matchStmt<ASTTryStatement> {

            child<ASTResourceSpecification> {
                child<ASTResources> {
                    child<ASTResource> {
                        child<ASTFieldAccess> {
                            child<ASTAmbiguousName> {

                            }
                        }
                    }
                }
            }

            child<ASTBlock> {}
        }


        "try ( a.foo() ){}" should notParseIn(StatementParsingCtx)
        "try (new Foo()){}" should notParseIn(StatementParsingCtx)

    }

})
