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
class ASTAnnotatedTypeTest : ParserTestSpec({

    parserTest("Test intersection in cast", javaVersions = J1_8..Latest) {

        "@I int" should matchType<ASTAnnotatedType> {

            child<ASTMarkerAnnotation> { }

            it::getBaseType shouldBe child<ASTPrimitiveType> { }

        }

    }

})