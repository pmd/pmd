/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd.test

import io.kotest.assertions.throwables.shouldThrow
import net.sourceforge.pmd.cpd.*
import net.sourceforge.pmd.lang.Language
import net.sourceforge.pmd.lang.LanguagePropertyBundle
import net.sourceforge.pmd.lang.LanguageRegistry
import net.sourceforge.pmd.lang.ast.TokenMgrError
import net.sourceforge.pmd.lang.document.TextDocument
import net.sourceforge.pmd.lang.document.TextFile
import net.sourceforge.pmd.lang.document.FileId
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
    val language: CpdCapableLanguage,
    override val extensionIncludingDot: String
) : BaseTextComparisonTest() {

    constructor(langId: String, extensionIncludingDot: String) : this(
        LanguageRegistry.CPD.getLanguageById(langId) as CpdCapableLanguage,
        extensionIncludingDot
    )

    fun newTokenizer(config: LanguagePropertyConfig): Tokenizer {
        val properties = language.newPropertyBundle().also { config.setProperties(it) }
        return language.createCpdTokenizer(properties)
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
            val tokens = tokenize(newTokenizer(config), fdata)
            buildString { format(tokens) }
        }
    }

    @JvmOverloads
    fun expectTokenMgrError(
        source: String,
        fileName: FileId = FileId.UNKNOWN,
        properties: LanguagePropertyConfig = defaultProperties()
    ): TokenMgrError =
        expectTokenMgrError(FileData(fileName, source), properties)

    @JvmOverloads
    fun expectTokenMgrError(
        fileData: FileData,
        config: LanguagePropertyConfig = defaultProperties()
    ): TokenMgrError =
        shouldThrow {
            tokenize(newTokenizer(config), fileData)
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
        colStart = append(Indent).append(escapedImage).padCol(colStart, Col0Width)
        @Suppress("UNUSED_VALUE")
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


    private fun sourceCodeOf(fileData: FileData): TextDocument =
        TextDocument.readOnlyString(fileData.fileText, fileData.fileName, language.defaultVersion)

    @JvmOverloads
    fun sourceCodeOf(text: String, fileName: FileId = FileId.UNKNOWN): FileData =
        FileData(fileName = fileName, fileText = text)

    fun tokenize(tokenizer: Tokenizer, fileData: FileData): Tokens =
        Tokens().also { tokens ->
            val source = sourceCodeOf(fileData)
            Tokenizer.tokenize(tokenizer, source, tokens)
        }

    private companion object {
        const val Indent = "    "
        const val Col0Width = 40
        const val Col1Width = 10 + Indent.length
        val ImageSize = Col0Width - Indent.length - 2 // -2 is for the "[]"
    }
}

interface LanguagePropertyConfig {
    fun setProperties(properties: LanguagePropertyBundle)
}
