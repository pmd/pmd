/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import io.kotest.matchers.string.shouldContain
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.JavaVersion
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J14__PREVIEW
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J15__PREVIEW
import java.io.IOException

class ASTPatternTest : ParserTestSpec({

    parserTest("Test patterns only available on JDK 14+15 (preview)", javaVersions = JavaVersion.values().asList().minus(J14__PREVIEW).minus(J15__PREVIEW)) {


        inContext(ExpressionParsingCtx) {
            "obj instanceof Class c" should throwParseException {
                it.message.shouldContain("Pattern Matching for instanceof is only supported with Java 14 Preview and Java 15 Preview")
            }
        }
    }

    parserTest("Test simple patterns", javaVersions = listOf(J14__PREVIEW, J15__PREVIEW)) {

        importedTypes += IOException::class.java

        "obj instanceof Class c" should matchExpr<ASTInstanceOfExpression> {
            unspecifiedChild()
            child<ASTTypeTestPattern> {
                it::getTypeNode shouldBe child(ignoreChildren = true) {}

                it::getVarId shouldBe child {
                    it::getVariableName shouldBe "c"
                }
            }
        }
    }


})
