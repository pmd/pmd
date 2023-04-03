/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain
import net.sourceforge.pmd.lang.ast.impl.javacc.MalformedSourceException
import net.sourceforge.pmd.lang.ast.test.IntelliMarker
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.document.TextDocument
import net.sourceforge.pmd.lang.java.JavaParsingHelper

fun makeJavaTranslatedDocument(
    code: String,
): TextDocument {
    val base = TextDocument.readOnlyString(
        code,
        JavaParsingHelper.DEFAULT.defaultVersion
    )
    return InternalApiBridge.javaTokenDoc().translate(base)
}

class JavaUnicodeEscapesTest : IntelliMarker, FunSpec({

    test("Test java invalid unicode escapes") {

        val comment = """\u002F\u0k2a\u002a\u002a\u002F"""

        val exception = shouldThrow<MalformedSourceException> {
            makeJavaTranslatedDocument(comment)
        }

        exception.message!!.shouldContain(Regex("line \\d+, column \\d+"))
        exception.message!!.shouldContain("\\u0k2a")

        exception.cause!!.shouldBeA<NumberFormatException> {
            it.message!!.shouldContain("valid hexadecimal digit")
        }
    }

    test("Test incomplete unicode escape ") {

        val comment = """\u00"""

        val mse = shouldThrow<MalformedSourceException> {
            makeJavaTranslatedDocument(comment)
        }
        mse.message!!.shouldContain(Regex("line \\d+, column \\d+"))
        mse.message!!.shouldContain("\\u00")
        mse.cause!!.shouldBeA<IndexOutOfBoundsException>()
    }

})


