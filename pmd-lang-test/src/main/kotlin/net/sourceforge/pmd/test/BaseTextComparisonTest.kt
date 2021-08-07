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

    /**
     * Resource prefix to look for test files. This is
     * resolved from the [resourceLoader] class. Separate
     * directories with '/', not '.'.
     */
    protected abstract val resourcePrefix: String

    /** Extension that the unparsed source file is supposed to have. */
    protected abstract val extensionIncludingDot: String

    data class FileData(val fileName:String, val fileText:String)

    /**
     * Executes the test. The test files are looked up using the [parser].
     * The reference test file must be named [fileBaseName] + [ExpectedExt].
     * The source file to parse must be named [fileBaseName] + [extensionIncludingDot].
     *
     * @param transformTextContent Function that maps the contents of the source file to the
     *                             "expected" format.
     */
    internal fun doTest(fileBaseName: String,
                        expectedSuffix: String = "",
                        transformTextContent: (FileData) -> String) {
        val expectedFile = findTestFile(resourceLoader, "${resourcePrefix}/$fileBaseName$expectedSuffix$ExpectedExt").toFile()

        val actual = transformTextContent(sourceText(fileBaseName))

        if (!expectedFile.exists()) {
            expectedFile.writeText(actual)
            throw AssertionError("Reference file doesn't exist, created it at $expectedFile")
        }

        val expected = expectedFile.readText()

        assertEquals(expected.normalize(), actual.normalize(), "File comparison failed, see the reference: $expectedFile")
    }

    protected fun sourceText(fileBaseName: String): FileData {
        val sourceFile = findTestFile(resourceLoader, "${resourcePrefix}/$fileBaseName$extensionIncludingDot").toFile()

        assert(sourceFile.isFile) {
            "Source file $sourceFile is missing"
        }

        val sourceText = sourceFile.readText(Charsets.UTF_8).normalize()
        return FileData(fileName = sourceFile.toString(), fileText = sourceText)
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
        // normalize the path, because if eg we have src/test/resources/some/pack/../other,
        // where the directory 'some/pack' does not exist, the file will not be found, even if
        // some/other exists. Normalization turns the above path into src/test/resources/some/other
        val norm = Paths.get("$path/$resourcePath").normalize()
        return srcTestResources.resolve(norm)
    }

    companion object {
        const val ExpectedExt = ".txt"

        fun String.normalize() = replace(
                // \R on java 8+
                regex = Regex("\\u000D\\u000A|[\\u000A\\u000B\\u000C\\u000D\\u0085\\u2028\\u2029]"),
                replacement = "\n"
        )
    }

}
