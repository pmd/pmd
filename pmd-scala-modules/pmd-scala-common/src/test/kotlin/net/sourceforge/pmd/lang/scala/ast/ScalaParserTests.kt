/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast

import net.sourceforge.pmd.lang.test.ast.BaseParsingHelper
import net.sourceforge.pmd.lang.test.ast.BaseTreeDumpTest
import net.sourceforge.pmd.lang.test.ast.SimpleNodePrinter
import org.junit.jupiter.api.Test

class ScalaParserTests : BaseTreeDumpTest(SimpleNodePrinter, ".scala") {

    override val parser: BaseParsingHelper<*, *>
        get() = ScalaParsingHelper.DEFAULT.withResourceContext(javaClass, "testdata")

    @Test
    fun testSomeScalaFeatures() = doTest("List")

    @Test
    fun testPackageObject() = doTest("package")

}
