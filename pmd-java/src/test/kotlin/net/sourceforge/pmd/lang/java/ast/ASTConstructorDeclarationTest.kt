/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe

class ASTConstructorDeclarationTest : ParserTestSpec({

    parserTest("Receiver parameters") {

        "Foo(@A Foo this){}" should matchDeclaration<ASTConstructorDeclaration> {

            it::getFormalParameters shouldBe child {
                it::getParameterCount shouldBe 0
                it::toList shouldBe emptyList()

                it::getReceiverParameter shouldBe child {
                    classType("Foo") {
                        annotation("A")
                    }
                }

            }

        }

        "Foo(@A Bar Bar.this, int other){}" should matchDeclaration<ASTConstructorDeclaration> {

            it::getFormalParameters shouldBe child {
                it::getParameterCount shouldBe 1

                it::getReceiverParameter shouldBe child {
                    classType("Bar") {
                        annotation("A")
                    }
                }

                it::toList shouldBe listOf(
                        child {
                            primitiveType(ASTPrimitiveType.PrimitiveType.INT)
                            variableId("other")
                        }
                )


            }

        }
    }

    parserTest("Annotation placement") {

        "@OnDecl <T extends K> Foo() { return; }" should matchDeclaration<ASTConstructorDeclaration> {

            annotation("OnDecl")

            typeParamList {
                typeParam("T") {
                    classType("K")
                }
            }

            child<ASTFormalParameters> {

            }

            block()
        }
    }
})
