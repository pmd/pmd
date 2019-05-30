/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.ParserTestCtx.Companion.StatementParsingCtx

// TODO merge the java ASTClassOrInterfaceDeclarationTest into this
class ASTClassOrInterfaceDeclTest : ParserTestSpec({

    parserTest("Local classes") {

        inContext(StatementParsingCtx) {

            """
               @F class Local {

               }
            """ should parseAs {

                child<ASTBlockStatement> {
                    classDecl(simpleName = "Local") {

                        it::isAbstract shouldBe false
                        it::isFinal shouldBe false
                        it::isLocal shouldBe true
                        it::isNested shouldBe false

                        annotation("F")
                        classBody {}
                    }
                }
            }

            """
               @F abstract class Local {

               }
            """ should parseAs {

                child<ASTBlockStatement> {
                    classDecl(simpleName = "Local") {
                        annotation("F")

                        it::isAbstract shouldBe true
                        it::isFinal shouldBe false
                        it::isLocal shouldBe true
                        it::isNested shouldBe false

                        classBody {}
                    }
                }
            }
        }
    }


})
