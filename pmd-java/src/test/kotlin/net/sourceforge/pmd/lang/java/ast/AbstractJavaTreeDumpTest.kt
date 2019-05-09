/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import com.github.oowekyala.treeutils.printers.SimpleTreePrinter
import net.sourceforge.pmd.lang.ast.Node
import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest
import net.sourceforge.pmd.lang.ast.test.RelevantAttributePrinter
import net.sourceforge.pmd.lang.java.JavaParsingHelper

abstract class AbstractJavaTreeDumpTest : BaseTreeDumpTest(
        printer = RelevantAttributePrinter(SimpleTreePrinter.AsciiStrings),
        pathToFixtures = "testdata",
        extension = ".java"
) {
    override fun parseFile(fileText: String): Node =
            JavaParsingHelper.WITH_PROCESSING.parse(fileText)

}

