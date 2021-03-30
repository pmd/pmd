/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.test

import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.shouldNotBe
import net.sourceforge.pmd.lang.ast.impl.AbstractNode
import net.sourceforge.pmd.lang.ast.GenericToken
import net.sourceforge.pmd.lang.ast.Node
import net.sourceforge.pmd.lang.ast.TextAvailableNode
import net.sourceforge.pmd.lang.ast.impl.javacc.AbstractJjtreeNode
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken
import net.sourceforge.pmd.util.document.Chars
import java.util.*


/**
 * Returns the text as a string. This is to allow
 * comparing the text to another string
 */
val TextAvailableNode.textStr: String
    get() = text.toString()

infix fun TextAvailableNode.shouldHaveText(str: String) {
    this::textStr shouldBe str
}

fun TextAvailableNode.textOfReportLocation(): String? =
        reportLocation.regionInFile?.let(textDocument::sliceText)?.toString()


fun Node.assertTextRangeIsOk() {

    reportLocation shouldNotBe null

    val parent = parent ?: return

    if (this is TextAvailableNode && parent is TextAvailableNode) {
        parent.text.toString().shouldContain(this.text.toString())
    }
}


fun Node.assertBounds(bline: Int, bcol: Int, eline: Int, ecol: Int) {
    reportLocation.apply {
        this::getBeginLine shouldBe bline
        this::getBeginColumn shouldBe bcol
        this::getEndLine shouldBe eline
        this::getEndColumn shouldBe ecol
    }
}
