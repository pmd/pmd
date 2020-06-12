/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd.test

import net.sourceforge.pmd.cpd.SourceCode
import net.sourceforge.pmd.cpd.TokenEntry
import net.sourceforge.pmd.cpd.Tokenizer
import net.sourceforge.pmd.cpd.Tokens
import net.sourceforge.pmd.test.BaseTextComparisonTest
import org.apache.commons.lang3.StringUtils

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

    abstract fun newTokenizer(): Tokenizer

    override val resourceLoader: Class<*>
        get() = javaClass

    override val resourcePrefix: String
        get() = "cpdData"

    override fun transformTextContent(sourceText: String): String {
        val sourceCode = SourceCode(SourceCode.StringCodeLoader(sourceText))
        val tokens = Tokens().also { newTokenizer().tokenize(sourceCode, it) }

        return buildString { format(tokens) }
    }


    private fun StringBuilder.format(tokens: Tokens) {
        appendHeader().appendln()

        var curLine = -1

        for (token in tokens.iterator()) {

            if (curLine != token.beginLine && token !== TokenEntry.EOF) {
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
                .replace("\\", "\\\\")               // escape backslashes
                .replace(Regex("\\R"), "\\\\n")     // escape newlines (normalizing)
                .replace(Regex("[]\\[]"), "\\\\$0") // escape []

        var truncated = StringUtils.truncate(escaped, ImageSize)

        if (truncated.endsWith('\\') && !truncated.endsWith("\\\\"))
            truncated = truncated.substring(0, truncated.length - 1) // cut inside an escape

        return if (truncated.length < escaped.length)
            "[$truncated["
        else
            "[$truncated]"

    }

    private companion object {
        const val Indent = "    "
        const val Col0Width = 40
        const val Col1Width = 10 + Indent.length
        val ImageSize = Col0Width - Indent.length - 2 // -2 is for the "[]"
    }
}
