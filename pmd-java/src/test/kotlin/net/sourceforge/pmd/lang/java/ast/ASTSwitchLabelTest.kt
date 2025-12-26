/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.test.ast.shouldBe

/**
 * @author Clément Fournier
 */
class ASTSwitchLabelTest : ProcessorTestSpec({

    parserTestContainer("Different label forms") {
        inContext(ExpressionParsingCtx) {
            """
              switch (x) {
               default: y = 2;
               case null, default: y = 3;
               case null: y = 4;
               case 4: break;
               case 4, 6, a?b:c : break;
              }
            """.trimIndent() should parseAs {
                switchExpr {
                    it::hasDefaultCase shouldBe true
                    it::isExhaustiveEnumSwitch shouldBe false

                    variableAccess("x")
                    switchFallthrough {
                        switchDefaultLabel()
                        exprStatement()
                    }
                    switchFallthrough {
                        switchNullLabel(isDefault = true)
                        exprStatement()
                    }
                    switchFallthrough {
                        switchNullLabel(isDefault = false)
                        exprStatement()
                    }
                    switchFallthrough {
                        switchLabel {
                            int(4)
                        }
                        breakStatement()
                    }
                    switchFallthrough {
                        switchLabel {
                            it::isPatternLabel shouldBe false
                            it::getGuard shouldBe null
                            int(4)
                            int(6)
                            ternaryExpr()
                        }
                        breakStatement()
                    }
                }
            }
        }
    }

    parserTestContainer("Guards and patterns") {
        inContext(ExpressionParsingCtx) {
            """
              switch (x) {
               case Integer i, Label _: 
               case Label _: 
               case Record(int _): 
               case Integer i when i != 0: 
              }
            """.trimIndent() should parseAs {
                switchExpr {
                    it::hasDefaultCase shouldBe false
                    it::isExhaustiveEnumSwitch shouldBe false

                    variableAccess("x")
                    switchFallthrough {
                        switchLabel {
                            it.exprList.toList() shouldBe emptyList()
                            it::isPatternLabel shouldBe true
                            typePattern {
                                modifiers()
                                classType("Integer")
                                variableId("i")
                            }
                            typePattern {
                                modifiers()
                                classType("Label")
                                variableId("_")
                            }
                        }
                    }
                    switchFallthrough {
                        switchLabel {
                            it.exprList.toList() shouldBe emptyList()
                            it::isPatternLabel shouldBe true
                            typePattern {
                                modifiers()
                                classType("Label")
                                variableId("_")
                            }
                        }
                    }
                    switchFallthrough {
                        switchLabel {
                            it.exprList.toList() shouldBe emptyList()
                            it::isPatternLabel shouldBe true

                            recordPattern("Record")
                        }
                    }
                    switchFallthrough {
                        switchLabel {
                            it.exprList.toList() shouldBe emptyList()
                            it::isPatternLabel shouldBe true
                            typePattern {
                                modifiers()
                                classType("Integer")
                                variableId("i")
                            }
                            it::getGuard shouldBe child<ASTGuard> {
                                it::getGuard shouldBe infixExpr(BinaryOp.NE)
                            }
                        }
                    }

                }
            }
        }
    }
    class SwitchLabelParsingContext(arrow: Boolean) : NodeParsingCtx<ASTSwitchLabel>("switch label") {
        private val sep = if (arrow) " ->" else " :"

        override fun getTemplate(construct: String, ctx: ParserTestCtx): String =
            ExpressionParsingCtx.getTemplate(
                """
                    switch (x) {
                        $construct $sep 
                    }
                """.trimIndent(),
                ctx
            )

        override fun retrieveNode(acu: ASTCompilationUnit): ASTSwitchLabel =
            ExpressionParsingCtx.retrieveNode(acu)
                .let { it as ASTSwitchExpression }
                .branches[0]!!.label!!
    }

    parserTestContainer("Invalid syntax") {
        inContext(SwitchLabelParsingContext(arrow = false)) {
            "case Integer i, 4" shouldNot parse()
            "case 4, Integer i" shouldNot parse()
            "case null, null" shouldNot parse()
            "case null, default, null" shouldNot parse()
            "case default, null" shouldNot parse()
            "case 4 when 4 == 2" shouldNot parse()
            "case Integer i when 4 == 2, Boolean b when b" shouldNot parse()
            "case Integer i when 4 == 2, Boolean b" shouldNot parse()

            "case Integer i when 4 == 2" should parse()
            "case Integer i, Boolean b when b" should parse()
        }
    }

})
