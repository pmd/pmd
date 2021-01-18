/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.JavaVersion
import net.sourceforge.pmd.lang.java.ast.JavaVersion.*
import java.io.IOException

class ASTPatternTest : ParserTestSpec({

    parserTest("Test patterns only available on JDK 14+15 (preview)", javaVersions = JavaVersion.values().asList().minus(J14__PREVIEW).minus(J15__PREVIEW)) {

        expectParseException("Pattern Matching for instanceof is only supported with Java 14 Preview and Java 15 Preview") {
            parseAstExpression("obj instanceof Class c")
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
