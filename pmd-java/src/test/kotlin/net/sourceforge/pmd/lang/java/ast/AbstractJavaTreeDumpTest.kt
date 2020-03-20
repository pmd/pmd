/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.Node
import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest
import net.sourceforge.pmd.lang.ast.test.SimpleNodePrinter
import net.sourceforge.pmd.lang.java.JavaParsingHelper
import net.sourceforge.pmd.util.treeexport.TreeRenderer

abstract class AbstractJavaTreeDumpTest(printer: TreeRenderer = SimpleNodePrinter)
    : BaseTreeDumpTest(
        printer = printer,
        pathToFixtures = "testdata",
        extension = ".java"
) {
    override fun parseFile(fileText: String): Node =
            JavaParsingHelper.WITH_PROCESSING.parse(fileText)

}

