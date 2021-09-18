/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.test

import net.sourceforge.pmd.test.BaseTextComparisonTest
import net.sourceforge.pmd.util.treeexport.TreeRenderer


/**
 * Compare a dump of an AST against a saved baseline.
 *
 * @param printer The node printer used to dump the trees
 * @param extensionIncludingDot Extension that the unparsed source file is supposed to have
 */
abstract class BaseTreeDumpTest(
        private val printer: TreeRenderer,
        override val extensionIncludingDot: String
) : BaseTextComparisonTest() {

    abstract val parser: BaseParsingHelper<*, *>

    override val resourceLoader: Class<*>
        get() = parser.resourceLoader

    override val resourcePrefix: String
        get() = parser.resourcePrefix

    /**
     * @see BaseTextComparisonTest.doTest
     */
    @JvmOverloads
    fun doTest(fileBaseName: String, parser: BaseParsingHelper<*, *> = this.parser) {
        super.doTest(fileBaseName, "") { fileData ->
            buildString {
                val ast = parser.parse(
                    sourceCode = fileData.fileText,
                    fileName = fileData.fileName
                )
                printer.renderSubtree(ast, this)
            }
        }
    }
}
