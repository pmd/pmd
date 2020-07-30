/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType.PrimitiveType

class ASTConstructorDeclarationTest : ParserTestSpec({

    parserTest("Receiver parameters") {

        inContext(TypeBodyParsingCtx) {

            "Foo(@A Foo this){}" should parseAs {
                constructorDecl {
                    it::getName shouldBe "Foo"
                    it::getTypeParameters shouldBe null
                    it::isVarargs shouldBe false
                    // notice that arity is zero
                    it::getArity shouldBe 0

                    it::getModifiers shouldBe modifiers { }

                    it::getFormalParameters shouldBe formalsList(0) {
                        it::getReceiverParameter shouldBe child {
                            classType("Foo") {
                                annotation("A")
                            }
                        }
                    }

                    it::getBody shouldBe block()
                }
            }

            "Foo(@A Bar Bar.this, int other){}" should parseAs {
                constructorDecl {
                    it::getName shouldBe "Foo"
                    it::getTypeParameters shouldBe null
                    it::isVarargs shouldBe false
                    it::getArity shouldBe 1

                    it::getModifiers shouldBe modifiers { }

                    it::getFormalParameters shouldBe formalsList(1) {

                        it::getReceiverParameter shouldBe child {
                            classType("Bar") {
                                annotation("A")
                            }
                        }

                        it.toList() shouldBe listOf(
                                child {
                                    localVarModifiers { }
                                    primitiveType(PrimitiveType.INT)
                                    variableId("other")
                                }
                        )


                    }

                    it::getThrowsList shouldBe null
                    it::getBody shouldBe block()
                }
            }
        }
    }

    parserTest("Annotation placement") {

        inContext(TypeBodyParsingCtx) {

            "@OnDecl <T extends K> Foo() { return; }" should parseAs {
                constructorDecl {

                    it::getName shouldBe "Foo"

                    it::getModifiers shouldBe modifiers {
                        annotation("OnDecl")
                    }

                    typeParamList {
                        typeParam("T") {
                            classType("K")
                        }
                    }

                    formalsList(0)

                    block()
                }
            }
        }
    }
})
