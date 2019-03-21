/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Latest
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J1_5
import net.sourceforge.pmd.lang.java.ast.ParserTestCtx.Companion.TypeParsingCtx

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTWildcardTypeTest : ParserTestSpec({

    parserTest("Test simple names", javaVersions = J1_5..Latest) {

        "List<? extends B>" should matchType<ASTWildcardType> {

            it::isUpperBound shouldBe true
            it::isLowerBound shouldBe false

            it::getTypeBoundNode shouldBe child<ASTClassOrInterfaceType> {
                it::getTypeImage shouldBe "B"
            }
        }

        "List<? extends B & C>" should notParseIn(TypeParsingCtx)
    }

})