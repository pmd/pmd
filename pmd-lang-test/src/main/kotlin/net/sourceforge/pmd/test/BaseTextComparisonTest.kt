/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.test

import java.nio.file.Path
import java.nio.file.Paths
import kotlin.test.assertEquals


/**
 * Compare a dump of a file against a saved baseline.
 * See subclasses CpdTextComparisonTest, BaseTreeDumpTest.
 */
abstract class BaseTextComparisonTest {

    protected abstract val resourceLoader: Class<*>
    protected abstract val resourcePrefix: String

    /** Extension that the unparsed source file is supposed to have. */
    protected abstract val extensionIncludingDot: String
    /** Turn the contents of the source file into the "actual" string. */
    protected abstract fun transformTextContent(sourceText: String): String

    /**
     * Executes the test. The test files are looked up using the [parser].
     * The reference test file must be named [fileBaseName] + [ExpectedExt].
     * The source file to parse must be named [fileBaseName] + [extensionIncludingDot].
     */
    fun doTest(fileBaseName: String) {
        val expectedFile = findTestFile(resourceLoader, "${resourcePrefix}/$fileBaseName$ExpectedExt").toFile()
        val sourceFile = findTestFile(resourceLoader, "${resourcePrefix}/$fileBaseName$extensionIncludingDot").toFile()

        assert(sourceFile.isFile) {
            "Source file $sourceFile is missing"
        }

        val actual = transformTextContent(sourceFile.readText()) // UTF-8

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
