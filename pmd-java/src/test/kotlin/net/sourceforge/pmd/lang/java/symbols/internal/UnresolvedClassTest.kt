/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal

import io.kotlintest.specs.FunSpec
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.symbols.internal.impl.UnresolvedSymFactory

/**
 * @author Clément Fournier
 */
class UnresolvedClassTest : FunSpec({

    test("Test simple unresolved class") {

        val sym = UnresolvedSymFactory().makeUnresolvedReference("some.pack.Class")

        sym::isUnresolved shouldBe true
        sym::getSimpleName shouldBe "Class"
        sym::getPackageName shouldBe "some.pack"
        sym::getCanonicalName shouldBe "some.pack.Class"
        sym::getBinaryName shouldBe "some.pack.Class"

        sym::isClass shouldBe true
        sym::isArray shouldBe false
        sym::isAnonymousClass shouldBe false
        sym::isEnum shouldBe false
        sym::isInterface shouldBe false
    }

})
