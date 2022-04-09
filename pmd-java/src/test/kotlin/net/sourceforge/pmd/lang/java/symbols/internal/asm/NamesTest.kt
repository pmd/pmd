/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.IntelliMarker

class NamesTest : IntelliMarker, FunSpec({

    test("Test inner class names") {

        val names = ClassStub.Names("java/text/NumberFormat\$Style")

        names.binaryName shouldBe "java.text.NumberFormat\$Style"
        names.canonicalName shouldBe null
        names.simpleName shouldBe null
        names.packageName shouldBe "java.text"
    }

    test("Test default package") {

        val names = ClassStub.Names("NumberFormat")

        names.binaryName shouldBe "NumberFormat"
        names.canonicalName shouldBe "NumberFormat"
        names.simpleName shouldBe "NumberFormat"
        names.packageName shouldBe ""
    }

    test("Test names with trailing dollar") {

        val names = ClassStub.Names("javasymbols/testdata/deep/ClassWithDollar\$")

        names.binaryName shouldBe "javasymbols.testdata.deep.ClassWithDollar\$"
        names.canonicalName shouldBe null
        names.simpleName shouldBe null
        names.packageName shouldBe "javasymbols.testdata.deep"
    }

    test("Test names dollar in package name") {

        val names = ClassStub.Names("\$javasymbols\$/test\$data/de\$ep/ClassWithDollar\$")

        names.binaryName shouldBe "\$javasymbols\$.test\$data.de\$ep.ClassWithDollar\$"
        names.packageName shouldBe "\$javasymbols\$.test\$data.de\$ep"
        names.canonicalName shouldBe null
        names.simpleName shouldBe null
    }

})
