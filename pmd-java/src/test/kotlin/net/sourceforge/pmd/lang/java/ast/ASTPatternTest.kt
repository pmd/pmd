/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J16
import java.io.IOException
import net.sourceforge.pmd.lang.ast.test.shouldBe as typeShouldBe

class ASTPatternTest : ParserTestSpec({
    val typePatternsVersions = JavaVersion.since(J16)

    parserTest("Test patterns only available on JDK16 or higher (including preview)",
        javaVersions = JavaVersion.except(typePatternsVersions)) {

        "obj instanceof Class c" should {
            expectParseException("Pattern Matching for instanceof is only supported with JDK >= 16") {
                parseAstExpression(it)
            }
        }
    }

    parserTest("Test simple patterns", javaVersions = typePatternsVersions) {

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
