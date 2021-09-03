/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe

class ASTClassOrInterfaceDeclarationTest : ParserTestSpec({

    parserTest("Local classes") {

        inContext(StatementParsingCtx) {

            """
               @F class Local {

               }
            """ should parseAs {
                localClassDecl(simpleName = "Local") {

                    it::isAbstract shouldBe false
                    it::isFinal shouldBe false
                    it::isLocal shouldBe true
                    it::isNested shouldBe false

                    it::getModifiers shouldBe modifiers {
                        annotation("F")
                    }

                    typeBody()
                }
            }

            """
               @F abstract @C class Local {

               }
            """ should parseAs {

                localClassDecl(simpleName = "Local") {

                    it::getModifiers shouldBe modifiers {
                        annotation("F")
                        annotation("C")
                    }

                    it::isAbstract shouldBe true
                    it::isFinal shouldBe false
                    it::isLocal shouldBe true
                    it::isNested shouldBe false

                    typeBody()
                }
            }

            """
                class Local { class Nested {} void bar() {class Local2 {}}}
            """ should parseAs {

                localClassDecl(simpleName = "Local") {

                    it::getModifiers shouldBe modifiers {}

                    it::isLocal shouldBe true
                    it::isNested shouldBe false


                    it.descendants(ASTClassOrInterfaceDeclaration::class.java)
                            .first()
                            .shouldMatchNode<ASTClassOrInterfaceDeclaration> {

                                it::getModifiers shouldBe modifiers {}


                                it::isLocal shouldBe false
                                it::isNested shouldBe true

                                typeBody()

                            }

                    typeBody()
                }
            }
        }
    }


})
