/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.test.ast.assertPosition
import net.sourceforge.pmd.lang.test.ast.shouldBe

class ASTClassDeclarationTest : ParserTestSpec({
    parserTestContainer("Report location") {
        inContext(RootParsingCtx) {
            """
                public
                abstract
                class
                MyAbstractClass
                {
                }
            """.trimIndent() should parseAs {
                classDecl("MyAbstractClass", ) {
                    it.assertPosition(4, 1, 4, 16)

                    val identifier = it.textDocument.sliceOriginalText(it.reportLocation.regionInFile)
                    identifier.toString() shouldBe "MyAbstractClass"

                    child<ASTModifierList> {}
                    child<ASTClassBody> {}
                }
            }
        }
    }

    parserTestContainer("Local classes") {
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


                    it.descendants(ASTClassDeclaration::class.java)
                            .first()
                            .shouldMatchNode<ASTClassDeclaration> {

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
