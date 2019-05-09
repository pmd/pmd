/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.test

import com.github.oowekyala.treeutils.printers.TreePrinter
import net.sourceforge.pmd.lang.ast.Node
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.test.assertEquals


/**
 * Compare a dump of a file against a saved baseline.
 *
 * @param printer The node printer used to dump the trees
 * @param pathToFixtures Path to the test files within the directory of the test case
 * @param extension Extension that the unparsed source file is supposed to have
 */
abstract class BaseTreeDumpTest(
        val printer: TreePrinter<Node>,
        val pathToFixtures: String,
        val extension: String
) {

    /**
     * Parses the given string into a node.
     */
    abstract fun parseFile(fileText: String): Node


    /**
     * Executes the test. The test files are looked up in [pathToFixtures],
     * in the resource directory *of the subclass*.
     * The reference test file must be named [fileBaseName] + [ExpectedExt].
     * The source file to parse must be named [fileBaseName] + [extension].
     */
    fun doTest(fileBaseName: String) {
        val expectedFile = findTestFile(javaClass, "$pathToFixtures/$fileBaseName$ExpectedExt").toFile()
        val sourceFile = findTestFile(javaClass, "$pathToFixtures/$fileBaseName$extension").toFile()

        assert(sourceFile.isFile) {
            "Source file $sourceFile is missing"
        }

        val parsed = parseFile(sourceFile.readText()) // UTF-8
        val actual = printer.dumpSubtree(parsed)

        if (!expectedFile.exists()) {
            expectedFile.writeText(actual)
            throw AssertionError("Reference file $expectedFile doesn't exist, created it anyway")
        }

        val expected = expectedFile.readText()

        assertEquals(expected, actual, "Tree dump comparison failed, see the reference: $expectedFile")
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
    }

}
