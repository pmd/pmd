package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J14__PREVIEW
import java.io.IOException

class ASTPatternTest : ParserTestSpec({

    parserTest("Test patterns only available on JDK 14 (preview)", javaVersions = !J14__PREVIEW) {

        expectParseException("Cannot use type test patterns in instanceof when running in JDK other than 14-preview") {
            parseAstExpression("obj instanceof Class c")
        }

    }

    parserTest("Test simple patterns", javaVersion = J14__PREVIEW) {

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
