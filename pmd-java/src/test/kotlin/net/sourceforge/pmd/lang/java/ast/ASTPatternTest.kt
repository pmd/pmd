/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe as typeShouldBe
import net.sourceforge.pmd.lang.java.ast.JavaVersion
import net.sourceforge.pmd.lang.java.ast.JavaVersion.*
import java.io.IOException

class ASTPatternTest : ParserTestSpec({

    parserTest("Test patterns only available on JDK 15 (preview) and JDK16 and JDK16 (preview)",
        javaVersions = JavaVersion.values().asList().minus(J15__PREVIEW).minus(J16).minus(J16__PREVIEW)) {

        expectParseException("Pattern Matching for instanceof is only supported with Java 15 Preview and Java >= 16") {
            parseAstExpression("obj instanceof Class c")
        }

    }

    parserTest("Test simple patterns", javaVersions = listOf(J15__PREVIEW, J16)) {

        importedTypes += IOException::class.java

        "obj instanceof Class c" should matchExpr<ASTInstanceOfExpression> {
            unspecifiedChild()
            child<ASTTypePattern> {
                it.isAnnotationPresent("java.lang.Deprecated") shouldBe false
                it::getTypeNode typeShouldBe child(ignoreChildren = true) {}

                it::getVarId typeShouldBe child {
                    it.name shouldBe "c"
                    it.isFinal shouldBe false
                }
            }
        }

        "obj instanceof final Class c" should matchExpr<ASTInstanceOfExpression> {
            unspecifiedChild()
            child<ASTTypePattern> {
                it.isAnnotationPresent("java.lang.Deprecated") shouldBe false
                it::getTypeNode typeShouldBe child(ignoreChildren = true) {}

                it::getVarId typeShouldBe child {
                    it.name shouldBe "c"
                    it.isFinal shouldBe true
                }
            }
        }

        "obj instanceof @Deprecated Class c" should matchExpr<ASTInstanceOfExpression> {
            unspecifiedChild()
            child<ASTTypePattern> {
                child<ASTAnnotation>(ignoreChildren = true) {
                    it.annotationName shouldBe "Deprecated"
                }

                it.isAnnotationPresent("java.lang.Deprecated") shouldBe true

                it::getTypeNode typeShouldBe child(ignoreChildren = true) {}

                it::getVarId typeShouldBe child {
                    it.name shouldBe "c"
                    it.isFinal shouldBe false
                }
            }
        }
    }


})
