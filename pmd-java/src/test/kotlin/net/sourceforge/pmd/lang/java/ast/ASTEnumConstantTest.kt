/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe

class ASTEnumConstantTest : ParserTestSpec({

    parserTest("Enum constants should have a variable declarator id") {

        "enum Foo { A, B }" should matchToplevelType<ASTEnumDeclaration> {

            child<ASTEnumBody> {

                child<ASTEnumConstant> {
                    variableId("A") {
                        it::isEnumConstant shouldBe true
                        it::isField shouldBe false // TODO ???
                    }
                }

                child<ASTEnumConstant> {
                    variableId("B") {
                        it::isEnumConstant shouldBe true
                        it::isField shouldBe false // TODO ???
                    }
                }
            }
        }
    }


    parserTest("Enum constants should have an anonymous class node") {

        "enum Foo { B { } }" should matchToplevelType<ASTEnumDeclaration> {

            child<ASTEnumBody> {

                child<ASTEnumConstant> {
                    variableId("B") {
                        it::isEnumConstant shouldBe true
                        it::isField shouldBe false // TODO ???
                    }

                    child<ASTAnonymousClassDeclaration> {
                        child<ASTClassOrInterfaceBody> {

                        }
                    }
                }
            }
        }
    }

    parserTest("Enum constants should contain their annotations") {

        "enum Foo { @C B, @A@a C }" should matchToplevelType<ASTEnumDeclaration> {

            child<ASTEnumBody> {

                child<ASTEnumConstant> {
                    annotation("C")

                    variableId("B")
                }

                child<ASTEnumConstant> {
                    annotation("A")
                    annotation("a")

                    variableId("C")
                }
            }
        }
    }

    parserTest("Enum constants with arguments") {

        "enum Foo { B(\"str\") }" should matchToplevelType<ASTEnumDeclaration> {

            child<ASTEnumBody> {

                child<ASTEnumConstant> {
                    variableId("B") {
                        it::isEnumConstant shouldBe true
                        it::isField shouldBe false // TODO ???
                    }

                    child<ASTArgumentList> {
                        child<ASTStringLiteral> { }
                    }
                }
            }
        }

        "enum Foo { B(\"str\") { } }" should matchToplevelType<ASTEnumDeclaration> {

            child<ASTEnumBody> {

                child<ASTEnumConstant> {
                    variableId("B") {
                        it::isEnumConstant shouldBe true
                        it::isField shouldBe false // TODO ???
                    }

                    child<ASTArgumentList> {
                        child<ASTStringLiteral> { }
                    }

                    child<ASTAnonymousClassDeclaration> {
                        child<ASTClassOrInterfaceBody> { }
                    }
                }
            }
        }
    }

})
