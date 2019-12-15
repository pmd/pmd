/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe

class ASTEnumConstantTest : ParserTestSpec({

    parserTest("Enum constants should have a variable declarator id") {

        "enum Foo { A, B }" should matchToplevelType<ASTEnumDeclaration> {

            typeBody {
                enumConstant("A") {
                    it::isAnonymousClass shouldBe false

                    it::getVarId shouldBe variableId("A") {
                        it::isEnumConstant shouldBe true
                        it::isField shouldBe false
                    }

                    it::getArguments shouldBe null
                    it::getAnonymousClass shouldBe null
                }

                enumConstant("B") {
                    it::isAnonymousClass shouldBe false

                    it::getVarId shouldBe variableId("B") {
                        it::isEnumConstant shouldBe true
                        it::isField shouldBe false
                    }

                    it::getArguments shouldBe null
                    it::getAnonymousClass shouldBe null

                }
            }
        }
    }


    parserTest("Enum constants should have an anonymous class node") {

        "enum Foo { B { } }" should matchToplevelType<ASTEnumDeclaration> {
            typeBody {
                enumConstant("B") {
                    it::isAnonymousClass shouldBe true

                    it::getVarId shouldBe variableId("B") {
                        it::isEnumConstant shouldBe true
                        it::isField shouldBe false
                    }

                    it::getArguments shouldBe null

                    it::getAnonymousClass shouldBe child {
                        typeBody()
                    }
                }
            }
        }
    }


    parserTest("Enum constants should contain their annotations") {

        "enum Foo { @C B, @A@a C }" should matchToplevelType<ASTEnumDeclaration> {

            typeBody {

                enumConstant("B") {
                    it::getDeclaredAnnotations shouldBe listOf(annotation("C"))

                    it::getVarId shouldBe variableId("B")

                    it::getArguments shouldBe null
                    it::getAnonymousClass shouldBe null
                }

                enumConstant("C") {
                    it::getDeclaredAnnotations shouldBe listOf(annotation("A"), annotation("a"))

                    it::getVarId shouldBe variableId("C")

                    it::getArguments shouldBe null
                    it::getAnonymousClass shouldBe null
                }
            }
        }
    }


    parserTest("Enum constants with arguments") {

        "enum Foo { B(\"str\") }" should matchToplevelType<ASTEnumDeclaration> {

            typeBody {

                enumConstant("B") {
                    it::getVarId shouldBe variableId("B") {
                        it::isEnumConstant shouldBe true
                        it::isField shouldBe false
                    }

                    it::getArguments shouldBe child {
                        stringLit("\"str\"")
                    }

                    it::getAnonymousClass shouldBe null
                }
            }
        }

        "enum Foo { B(\"str\") { } }" should matchToplevelType<ASTEnumDeclaration> {


            typeBody {
                enumConstant("B") {
                    it::getVarId shouldBe variableId("B") {
                        it::isEnumConstant shouldBe true
                        it::isField shouldBe false
                    }

                    it::getArguments shouldBe child {
                        stringLit("\"str\"")
                    }

                    it::getAnonymousClass shouldBe child {
                        typeBody()
                    }
                }
            }
        }
    }

})
