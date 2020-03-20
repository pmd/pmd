/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import org.junit.Test

/**
 * Compare a dump of a file against a saved baseline.
 *
 * @author Clément Fournier
 */
class AstTreeDumpTests : AbstractJavaTreeDumpTest() {

    @Test
    fun testComplicatedLambda() = doTest("Bug1429")


}
