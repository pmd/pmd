/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.JavaVersion
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J14__PREVIEW
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J15__PREVIEW
import java.io.IOException

class ASTPatternTest : ParserTestSpec({

    parserTest("Test patterns only available on JDK 14+15 (preview)", javaVersions = JavaVersion.except(J14__PREVIEW, J15__PREVIEW)) {


        inContext(ExpressionParsingCtx) {
            "obj instanceof Class c" should throwParseException {
                it.message.shouldContain("Type test patterns in instanceof is a preview feature of JDK")
            }
        }
    }

    parserTest("Test simple patterns", javaVersions = listOf(J14__PREVIEW, J15__PREVIEW)) {

        importedTypes += IOException::class.java
        inContext(ExpressionParsingCtx) {

            "obj instanceof Class c" should parseAs {
                infixExpr(BinaryOp.INSTANCEOF) {
                    variableAccess("obj")
                    child<ASTPatternExpression> {
                        it::getPattern shouldBe child<ASTTypeTestPattern> {
                            it::getTypeNode shouldBe classType("Class")
                            it::getVarId shouldBe variableId("c") {
                                it::getModifiers shouldBe modifiers {  } // dummy modifier list
                                it.hasExplicitModifiers(JModifier.FINAL) shouldBe false
                                it.hasModifiers(JModifier.FINAL) shouldBe true
                            }
                        }
                    }
                }
            }
        }
    }


})
