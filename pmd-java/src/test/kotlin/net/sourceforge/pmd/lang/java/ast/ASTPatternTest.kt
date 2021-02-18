/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe as typeShouldBe
import net.sourceforge.pmd.lang.java.ast.JavaVersion.*
import java.io.IOException

class ASTPatternTest : ParserTestSpec({

    val typePatternsVersions = JavaVersion.since(J16).plus(J15__PREVIEW)

    parserTest("Test patterns only available on JDK 15 (preview) and JDK16 and JDK16 (preview)", javaVersions = typePatternsVersions) {

        inContext(ExpressionParsingCtx) {
            "obj instanceof Class c" should throwParseException {
                it.message.shouldContain("Type patterns in instanceof was only standardized in Java 16")
            }
        }
    }

    parserTest("Test simple patterns", javaVersions = typePatternsVersions) {

        importedTypes += IOException::class.java
        inContext(ExpressionParsingCtx) {

            "obj instanceof Class c" should parseAs {
                infixExpr(BinaryOp.INSTANCEOF) {
                    variableAccess("obj")
                    child<ASTPatternExpression> {
                        //it.isAnnotationPresent("java.lang.Deprecated") shouldBe false
                        it::getPattern shouldBe child<ASTTypePattern> {
                            it::getTypeNode shouldBe classType("Class")
                            it::getVarId shouldBe variableId("c") {
                                it::getModifiers shouldBe modifiers {  } // dummy modifier list
                                it.hasExplicitModifiers(JModifier.FINAL) shouldBe false
                                it.hasModifiers(JModifier.FINAL) shouldBe false
                            }
                        }
                    }
                }
            }

            "obj instanceof final Class c" should parseAs {
                infixExpr(BinaryOp.INSTANCEOF) {
                    variableAccess("obj")
                    child<ASTPatternExpression> {
                        //it.isAnnotationPresent("java.lang.Deprecated") shouldBe false
                        it::getPattern shouldBe child<ASTTypePattern> {
                            it::getTypeNode shouldBe classType("Class")
                            it::getVarId shouldBe variableId("c") {
                                it::getModifiers shouldBe modifiers {  } // dummy modifier list
                                it.hasExplicitModifiers(JModifier.FINAL) shouldBe true
                                it.hasModifiers(JModifier.FINAL) shouldBe true
                            }
                        }
                    }
                }
            }

            "obj instanceof @Deprecated Class c" should parseAs {
                infixExpr(BinaryOp.INSTANCEOF) {
                    variableAccess("obj")
                    child<ASTPatternExpression> {
                        child<ASTAnnotation>(ignoreChildren = true) {
                            it.annotationName shouldBe "Deprecated"
                        }

                        //it.isAnnotationPresent("java.lang.Deprecated") shouldBe true
                        it::getPattern shouldBe child<ASTTypePattern> {
                            it::getTypeNode shouldBe classType("Class")
                            it::getVarId shouldBe variableId("c") {
                                it::getModifiers shouldBe modifiers {  } // dummy modifier list
                                it.hasExplicitModifiers(JModifier.FINAL) shouldBe true
                                it.hasModifiers(JModifier.FINAL) shouldBe true
                            }
                        }
                    }
                }
            }
        }
    }
})
