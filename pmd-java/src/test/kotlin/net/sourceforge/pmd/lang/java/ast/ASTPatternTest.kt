/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J16
import java.io.IOException

class ASTPatternTest : ProcessorTestSpec({

    val typePatternsVersions = JavaVersion.since(J16)

    parserTest("Test patterns only available on JDK16 or higher (including preview)",
        javaVersions = JavaVersion.except(typePatternsVersions)) {

        inContext(ExpressionParsingCtx) {
            "obj instanceof Class c" shouldNot parse()
        }
    }

    parserTest("Test simple patterns", javaVersions = typePatternsVersions) {

        importedTypes += IOException::class.java
        inContext(ExpressionParsingCtx) {

            "obj instanceof Class c" should parseAs {
                infixExpr(BinaryOp.INSTANCEOF) {
                    variableAccess("obj")
                    child<ASTPatternExpression> {
                        it.pattern shouldBe child<ASTTypePattern> {
                            it.isAnnotationPresent("java.lang.Deprecated") shouldBe false
                            it.modifiers shouldBe modifiers { } // dummy/empty modifier list
                            it.typeNode shouldBe classType("Class")
                            it.varId shouldBe variableId("c") {
                                it.hasExplicitModifiers(JModifier.FINAL) shouldBe false
                                it.hasModifiers(JModifier.FINAL) shouldBe false
                                it.isPatternBinding shouldBe true
                            }
                        }
                    }
                }
            }

            "obj instanceof final Class c" should parseAs {
                infixExpr(BinaryOp.INSTANCEOF) {
                    variableAccess("obj")
                    child<ASTPatternExpression> {
                        it.pattern shouldBe child<ASTTypePattern> {
                            it.isAnnotationPresent("java.lang.Deprecated") shouldBe false
                            it.modifiers shouldBe modifiers { } // explicit modifier list
                            it.typeNode shouldBe classType("Class")
                            it.varId shouldBe variableId("c") {
                                it.hasExplicitModifiers(JModifier.FINAL) shouldBe true
                                it.hasModifiers(JModifier.FINAL) shouldBe true
                                it.isPatternBinding shouldBe true
                            }
                        }
                    }
                }
            }

            "obj instanceof @Deprecated Class c" should parseAs {
                infixExpr(BinaryOp.INSTANCEOF) {
                    variableAccess("obj")
                    child<ASTPatternExpression> {
                        it.pattern shouldBe child<ASTTypePattern> {
                            it.isAnnotationPresent("java.lang.Deprecated") shouldBe true
                            it.modifiers shouldBe modifiers {
                                annotation("Deprecated")
                            }
                            it.typeNode shouldBe classType("Class")
                            it.varId shouldBe variableId("c") {
                                it.hasExplicitModifiers(JModifier.FINAL) shouldBe false
                                it.hasModifiers(JModifier.FINAL) shouldBe false
                                it.isPatternBinding shouldBe true
                            }
                        }
                    }
                }
            }
        }
    }
})
