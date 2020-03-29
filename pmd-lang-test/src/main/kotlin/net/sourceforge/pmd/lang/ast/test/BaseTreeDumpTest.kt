/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.test

import net.sourceforge.pmd.util.treeexport.TreeRenderer
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.test.assertEquals


/**
 * Compare a dump of a file against a saved baseline.
 *
 * @param printer The node printer used to dump the trees
 * @param extension Extension that the unparsed source file is supposed to have
 */
abstract class BaseTreeDumpTest(
        val printer: TreeRenderer,
        val extension: String
) {

    abstract val parser: BaseParsingHelper<*, *>

    /**
     * Executes the test. The test files are looked up using the [parser].
     * The reference test file must be named [fileBaseName] + [ExpectedExt].
     * The source file to parse must be named [fileBaseName] + [extension].
     */
    fun doTest(fileBaseName: String) {
        val expectedFile = findTestFile(parser.resourceLoader, "${parser.resourcePrefix}/$fileBaseName$ExpectedExt").toFile()
        val sourceFile = findTestFile(parser.resourceLoader, "${parser.resourcePrefix}/$fileBaseName$extension").toFile()

        assert(sourceFile.isFile) {
            "Source file $sourceFile is missing"
        }

        val parsed = parser.parse(sourceFile.readText()) // UTF-8
        val actual = StringBuilder().also { printer.renderSubtree(parsed, it) }.toString()

        if (!expectedFile.exists()) {
            expectedFile.writeText(actual)
            throw AssertionError("Reference file doesn't exist, created it at $expectedFile")
        }

        val expected = expectedFile.readText()

        assertEquals(expected.normalize(), actual.normalize(), "Tree dump comparison failed, see the reference: $expectedFile")
    }

    // Outputting a path makes for better error messages
    private val srcTestResources = let {
        // this is set from maven surefire
        System.getProperty("mvn.project.src.test.resources")
                ?.let { Paths.get(it).toAbsolutePath() }
                // that's for when the tests are run inside the IDE
                ?: Paths.get(javaClass.protectionDomain.codeSource.location.file)
                        // go up from target/test-classes into the project root
                        .resolve("../../src/test/resources").normalize()
    }

    private fun findTestFile(contextClass: Class<*>, resourcePath: String): Path {
        val path = contextClass.`package`.name.replace('.', '/')
        return srcTestResources.resolve("$path/$resourcePath")
    }

    companion object {
        const val ExpectedExt = ".txt"

        fun String.normalize() = replace(Regex("\\R"), "\n")
    }

}
