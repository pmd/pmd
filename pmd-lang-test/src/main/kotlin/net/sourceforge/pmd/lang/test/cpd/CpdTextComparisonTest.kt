/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.test.cpd

import io.kotest.assertions.throwables.shouldThrow
import net.sourceforge.pmd.cpd.CpdCapableLanguage
import net.sourceforge.pmd.cpd.CpdLexer
import net.sourceforge.pmd.cpd.TokenEntry
import net.sourceforge.pmd.cpd.Tokens
import net.sourceforge.pmd.lang.LanguagePropertyBundle
import net.sourceforge.pmd.lang.LanguageRegistry
import net.sourceforge.pmd.lang.ast.LexException
import net.sourceforge.pmd.lang.document.FileId
import net.sourceforge.pmd.lang.document.TextDocument
import net.sourceforge.pmd.lang.test.BaseTextComparisonTest
import org.apache.commons.lang3.StringUtils

/**
 * CPD test comparing a dump of a file against a saved baseline.
 * Each token is printed on a separate line.
 *
 * @param extensionIncludingDot File extension for the language.
 *                              Baseline files are saved in txt files.
 */
abstract class CpdTextComparisonTest(
    val language: CpdCapableLanguage,
    override val extensionIncludingDot: String
) : BaseTextComparisonTest() {

    constructor(langId: String, extensionIncludingDot: String) : this(
        LanguageRegistry.CPD.getLanguageById(langId) as CpdCapableLanguage,
        extensionIncludingDot
    )

    fun newCpdLexer(config: LanguagePropertyConfig): CpdLexer {
        val properties = language.newPropertyBundle().also { config.setProperties(it) }
        return language.createCpdLexer(properties)
    }

    override val resourceLoader: Class<*>
        get() = javaClass

    override val resourcePrefix: String
        get() = "testdata"


    open fun defaultProperties(): LanguagePropertyConfig = object : LanguagePropertyConfig {
        override fun setProperties(properties: LanguagePropertyBundle) {
            // use defaults
        }
    }

    /**
     * A test comparing the output of the tokenizer.
     *
     * @param fileBaseName   Name of the source file (without extension or resource prefix)
     * @param expectedSuffix Suffix to append to the expected file. This allows reusing the same source file
     *                       with different configurations, provided the suffix is different
     * @param config     Properties to configure the tokenizer
     */
    @JvmOverloads
    fun doTest(
        fileBaseName: String,
        expectedSuffix: String = "",
        config: LanguagePropertyConfig = defaultProperties()
    ) {
        super.doTest(fileBaseName, expectedSuffix) { fdata ->
            val tokens = tokenize(newCpdLexer(config), fdata)
            buildString { format(tokens) }
        }
    }

    @JvmOverloads
    fun expectLexException(
        source: String,
        fileName: FileId = FileId.UNKNOWN,
        properties: LanguagePropertyConfig = defaultProperties()
    ): LexException =
        expectLexException(FileData(fileName, source), properties)

    @JvmOverloads
    fun expectLexException(
        fileData: FileData,
        config: LanguagePropertyConfig = defaultProperties()
    ): LexException =
        shouldThrow {
            tokenize(newCpdLexer(config), fileData)
        }


    private fun StringBuilder.format(tokens: Tokens) {
        appendHeader().appendLine()

        var curLine = -1

        for (token in tokens.tokens) {

            if (token.isEof) {
                append("EOF").appendLine()
                continue
            }

            if (curLine != token.beginLine) {
                curLine = token.beginLine
                append('L').append(curLine).appendLine()
            }

            formatLine(token, tokens).appendLine()
        }
    }


    private fun StringBuilder.appendHeader() =
            formatLine(
                    escapedImage = "[Image] or [Truncated image[",
                    bcol = "Bcol",
                    ecol = "Ecol"
            )


    private fun StringBuilder.formatLine(token: TokenEntry, tokens: Tokens) =
            formatLine(
                    escapedImage = escapeImage(token.getImage(tokens)),
                    bcol = token.beginColumn,
                    ecol = token.endColumn
            )


    private fun StringBuilder.formatLine(escapedImage: String, bcol: Any, ecol: Any): StringBuilder {
        var colStart = length
        colStart = append(INDENT).append(escapedImage).padCol(colStart, COL_0_WIDTH)
        @Suppress("UNUSED_VALUE")
        colStart = append(INDENT).append(bcol).padCol(colStart, COL_1_WIDTH)
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

        var truncated = StringUtils.truncate(escaped, IMAGE_SIZE)

        if (truncated.endsWith('\\') && !truncated.endsWith("\\\\"))
            truncated = truncated.substring(0, truncated.length - 1) // cut inside an escape

        return if (truncated.length < escaped.length)
            "[$truncated["
        else
            "[$truncated]"

    }


    private fun sourceCodeOf(fileData: FileData): TextDocument =
        TextDocument.readOnlyString(fileData.fileText, fileData.fileName, language.defaultVersion)

    @JvmOverloads
    fun sourceCodeOf(text: String, fileName: FileId = FileId.UNKNOWN): FileData =
        FileData(fileName = fileName, fileText = text)

    fun tokenize(cpdLexer: CpdLexer, fileData: FileData): Tokens =
        CpdLexer.tokenize(cpdLexer, sourceCodeOf(fileData))

    private companion object {
        const val INDENT = "    "
        const val COL_0_WIDTH = 40
        const val COL_1_WIDTH = 10 + INDENT.length
        const val IMAGE_SIZE = COL_0_WIDTH - INDENT.length - 2 // -2 is for the "[]"
    }
}

interface LanguagePropertyConfig {
    fun setProperties(properties: LanguagePropertyBundle)
}
