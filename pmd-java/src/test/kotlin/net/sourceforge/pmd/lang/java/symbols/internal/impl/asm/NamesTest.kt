/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.asm

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class NamesTest : FunSpec({

    test("Test inner class names") {

        val names = ClassStub.Names("java/text/NumberFormat\$Style")

        names.binaryName shouldBe "java.text.NumberFormat\$Style"
        names.canonicalName shouldBe "java.text.NumberFormat.Style"
        names.simpleName shouldBe "Style"
        names.packageName shouldBe "java.text"
    }

    test("Test default package") {

        val names = ClassStub.Names("NumberFormat")

        names.binaryName shouldBe "NumberFormat"
        names.canonicalName shouldBe "NumberFormat"
        names.simpleName shouldBe "NumberFormat"
        names.packageName shouldBe ""
    }

})
