/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd.test

import io.kotest.assertions.throwables.shouldThrow
import net.sourceforge.pmd.cpd.SourceCode
import net.sourceforge.pmd.cpd.TokenEntry
import net.sourceforge.pmd.cpd.Tokenizer
import net.sourceforge.pmd.cpd.Tokens
import net.sourceforge.pmd.lang.ast.TokenMgrError
import net.sourceforge.pmd.test.BaseTextComparisonTest
import org.apache.commons.lang3.StringUtils
import java.util.*

/**
 * CPD test comparing a dump of a file against a saved baseline.
 * Each token is printed on a separate line.
 *
 * @param extensionIncludingDot File extension for the language.
 *                              Baseline files are saved in txt files.
 */
abstract class CpdTextComparisonTest(
        override val extensionIncludingDot: String
) : BaseTextComparisonTest() {

    abstract fun newTokenizer(properties: Properties): Tokenizer

    override val resourceLoader: Class<*>
        get() = javaClass

    override val resourcePrefix: String
        get() = "testdata"


    open fun defaultProperties() = Properties()

    /**
     * A test comparing the output of the tokenizer.
     *
     * @param fileBaseName   Name of the source file (without extension or resource prefix)
     * @param expectedSuffix Suffix to append to the expected file. This allows reusing the same source file
     *                       with different configurations, provided the suffix is different
     * @param properties     Properties to configure [newTokenizer]
     */
    @JvmOverloads
    fun doTest(fileBaseName: String, expectedSuffix: String = "", properties: Properties = defaultProperties()) {
        super.doTest(fileBaseName, expectedSuffix) { fileData ->
            val sourceCode = SourceCode(SourceCode.StringCodeLoader(fileData.fileText, fileData.fileName))
            val tokens = Tokens().also {
                val tokenizer = newTokenizer(properties)
                tokenizer.tokenize(sourceCode, it)
            }

            buildString { format(tokens) }
        }
    }

    @JvmOverloads
    fun expectTokenMgrError(source: String, properties: Properties = defaultProperties()): TokenMgrError =
            shouldThrow {
                newTokenizer(properties).tokenize(sourceCodeOf(source), Tokens())
            }


    private fun StringBuilder.format(tokens: Tokens) {
        appendHeader().appendln()

        var curLine = -1

        for (token in tokens.iterator()) {

            if (token === TokenEntry.EOF) {
                append("EOF").appendln()
                continue
            }

            if (curLine != token.beginLine) {
                curLine = token.beginLine
                append('L').append(curLine).appendln()
            }

            formatLine(token).appendln()
        }
    }


    private fun StringBuilder.appendHeader() =
            formatLine(
                    escapedImage = "[Image] or [Truncated image[",
                    bcol = "Bcol",
                    ecol = "Ecol"
            )


    private fun StringBuilder.formatLine(token: TokenEntry) =
            formatLine(
                    escapedImage = escapeImage(token.toString()),
                    bcol = token.beginColumn,
                    ecol = token.endColumn
            )


    private fun StringBuilder.formatLine(escapedImage: String, bcol: Any, ecol: Any): StringBuilder {
        var colStart = length
        colStart = append(Indent).append(escapedImage).padCol(colStart, Col0Width)
        colStart = append(Indent).append(bcol).padCol(colStart, Col1Width)
        return append(ecol)
    }

    private fun StringBuilder.padCol(colStart: Int, colWidth: Int): Int {
        for (i in 1..(colStart + colWidth - this.length))
            append(' ')

        return length
    }


    private fun escapeImage(str: String): String {
        val escaped = str
                .replace("\\", "\\\\")                 // escape backslashes
                .replace("\r\n", "\\r\\n")             // CRLF (treated specially because it has a different length)
                .replace("\t", "\\t")                  // TAB
                .replace(Regex("\\u000D\\u000A|[\\u000A\\u000B\\u000C\\u000D\\u0085\\u2028\\u2029]"), "\\\\n")       // escape other newlines (normalizing), use \\R with java8+
                .replace(Regex("[]\\[]"), "\\\\$0")   // escape []

        var truncated = StringUtils.truncate(escaped, ImageSize)

        if (truncated.endsWith('\\') && !truncated.endsWith("\\\\"))
            truncated = truncated.substring(0, truncated.length - 1) // cut inside an escape

        return if (truncated.length < escaped.length)
            "[$truncated["
        else
            "[$truncated]"

    }


    fun sourceCodeOf(str: String): SourceCode = SourceCode(SourceCode.StringCodeLoader(str))

    fun tokenize(tokenizer: Tokenizer, str: String): Tokens =
            Tokens().also {
                tokenizer.tokenize(sourceCodeOf(str), it)
            }

    private companion object {
        const val Indent = "    "
        const val Col0Width = 40
        const val Col1Width = 10 + Indent.length
        val ImageSize = Col0Width - Indent.length - 2 // -2 is for the "[]"
    }
}
