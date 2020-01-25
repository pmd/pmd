/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import io.kotlintest.matchers.beEmpty
import io.kotlintest.matchers.collections.shouldContainExactly
import io.kotlintest.should
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.JModifier.*

class ASTEnumConstantTest : ParserTestSpec({

    parserTest("Enum constants should have a variable declarator id") {

        inContext(TopLevelTypeDeclarationParsingCtx) {
            "enum Foo { A, B }" should parseAs {
                enumDecl("Foo") {
                    it::getModifiers shouldBe modifiers {}

                    enumBody {
                        enumConstant("A") {
                            it::isAnonymousClass shouldBe false

                            it::getModifiers shouldBe modifiers { }

                            it::getVarId shouldBe variableId("A") {
                                it::isEnumConstant shouldBe true
                                it::isField shouldBe false
                            }

                            it::getArguments shouldBe null
                            it::getAnonymousClass shouldBe null
                        }

                        enumConstant("B") {
                            it::isAnonymousClass shouldBe false

                            it::getModifiers shouldBe modifiers { }

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
        }
    }


    parserTest("Enum constants should have an anonymous class node") {

        inContext(TopLevelTypeDeclarationParsingCtx) {
            "enum Foo { B { } }" should parseAs {
                enumDecl("Foo") {
                    it::getModifiers shouldBe modifiers {}

                    enumBody {
                        enumConstant("B") {
                            it::isAnonymousClass shouldBe true

                            it::getModifiers shouldBe modifiers { }

                            it::getVarId shouldBe variableId("B") {
                                it::isEnumConstant shouldBe true
                                it::isField shouldBe false
                            }

                            it::getArguments shouldBe null

                            it::getAnonymousClass shouldBe child {
                                it::getModifiers shouldBe modifiers { }

                                typeBody()
                            }
                        }
                    }
                }
            }
        }
    }


    parserTest("Enum constants should contain their annotations") {

        inContext(TopLevelTypeDeclarationParsingCtx) {
            "enum Foo { @C B, @A@a C }" should parseAs {
                enumDecl("Foo") {
                    it::getModifiers shouldBe modifiers {}

                    enumBody {

                        enumConstant("B") {

                            val c = it

                            it::getModifiers shouldBe modifiers {
                                c::declaredAnnotationsList shouldBe listOf(annotation("C"))
                            }


                            it::getVarId shouldBe variableId("B")

                            it::getArguments shouldBe null
                            it::getAnonymousClass shouldBe null
                        }

                        enumConstant("C") {


                            val c = it

                            it::getModifiers shouldBe modifiers {
                                c::declaredAnnotationsList shouldBe listOf(annotation("A"), annotation("a"))
                            }


                            it::getVarId shouldBe variableId("C")

                            it::getArguments shouldBe null
                            it::getAnonymousClass shouldBe null
                        }
                    }
                }
            }
        }
    }


    parserTest("Enum constants with arguments") {

        inContext(TopLevelTypeDeclarationParsingCtx) {
            "enum Foo { B(\"str\") }" should parseAs {
                enumDecl("Foo") {
                    it::getModifiers shouldBe modifiers {}

                    enumBody {

                        enumConstant("B") {

                            it::getModifiers shouldBe modifiers {
                                it.explicitModifiers should beEmpty()
                                it.effectiveModifiers.shouldContainExactly(PUBLIC, STATIC, FINAL)
                            }

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
            }

            "enum Foo { B(\"str\") { } }" should parseAs {
                enumDecl("Foo") {

                    it::getModifiers shouldBe modifiers {}


                    enumBody {
                        enumConstant("B") {

                            it::getModifiers shouldBe modifiers {}

                            it::getVarId shouldBe variableId("B") {
                                it::isEnumConstant shouldBe true
                                it::isField shouldBe false
                            }

                            it::getArguments shouldBe child {
                                stringLit("\"str\"")
                            }

                            it::getAnonymousClass shouldBe child {
                                it::getModifiers shouldBe modifiers { }
                                typeBody()
                            }
                        }
                    }
                }
            }
        }
    }

})
