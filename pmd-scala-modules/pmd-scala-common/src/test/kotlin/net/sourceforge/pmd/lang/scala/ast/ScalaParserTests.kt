/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast

import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper
import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest
import net.sourceforge.pmd.lang.ast.test.SimpleNodePrinter
import org.junit.Test

class ScalaParserTests : BaseTreeDumpTest(SimpleNodePrinter, ".scala") {

    override val parser: BaseParsingHelper<*, *>
        get() = ScalaParsingHelper.DEFAULT.withResourceContext(javaClass, "testdata")

    @Test
    fun testSomeScalaFeatures() = doTest("List")

    @Test
    fun testPackageObject() = doTest("package")

}
