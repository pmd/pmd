/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Latest
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J1_8

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTTypeParametersTest : ParserTestSpec({

    parserTest("Test simple parameters", javaVersions = J1_8..Latest) {

        "<T>" should matchTypeParameters {

            child<ASTTypeParameter> {
                it::getParameterName shouldBe "T"
                it::getTypeBoundNode shouldBe null
            }

        }

        "<T, S>" should matchTypeParameters {

            child<ASTTypeParameter> {
                it::getParameterName shouldBe "T"
                it::getTypeBoundNode shouldBe null
            }

            child<ASTTypeParameter> {
                it::getParameterName shouldBe "S"
                it::getTypeBoundNode shouldBe null
            }

        }

        "<@F T, S>" should matchTypeParameters {

            child<ASTTypeParameter> {
                it::getParameterName shouldBe "T"
                it::getTypeBoundNode shouldBe null

                child<ASTMarkerAnnotation> {}

            }

            child<ASTTypeParameter> {
                it::getParameterName shouldBe "S"
                it::getTypeBoundNode shouldBe null
            }
        }

        "<@F T extends @N Runnable>" should matchTypeParameters {

            child<ASTTypeParameter> {
                it::getParameterName shouldBe "T"

                child<ASTMarkerAnnotation> {
                    it::getAnnotationName shouldBe "F"
                }

                val bound = child<ASTTypeBound> {

                    child<ASTMarkerAnnotation> {
                        it::getAnnotationName shouldBe "N"
                    }

                    it::getTypeNode shouldBe child<ASTClassOrInterfaceType> {
                        it::getTypeImage shouldBe "Runnable"
                    }
                }

                it::getTypeBoundNode shouldBe bound.typeNode
            }
        }
    }

})
