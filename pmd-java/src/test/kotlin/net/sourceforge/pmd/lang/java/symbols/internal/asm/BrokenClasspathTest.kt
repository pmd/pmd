/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm

import io.kotest.assertions.fail
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.types.JClassType
import net.sourceforge.pmd.lang.java.types.TypeOps
import net.sourceforge.pmd.lang.java.types.TypeSystem
import net.sourceforge.pmd.lang.java.types.testTypeSystem

class BrokenClasspathTest : FunSpec({

    val rootCp = Classpath.contextClasspath()
    val brokenCp = rootCp.exclude(setOf("javasymbols/brokenclasses/SuperItf.class"))

    test("Test classpath setup ") {
        rootCp.findResource("javasymbols/brokenclasses/BrokenGeneric.class") shouldNotBe null
        rootCp.findResource("javasymbols/brokenclasses/SuperItf.class") shouldNotBe null
        rootCp.findResource("javasymbols/brokenclasses/SuperKlass.class") shouldNotBe null

        // this one is null
        brokenCp.findResource("javasymbols/brokenclasses/SuperItf.class") shouldBe null
        brokenCp.findResource("javasymbols/brokenclasses/BrokenGeneric.class") shouldNotBe null
        brokenCp.findResource("javasymbols/brokenclasses/SuperKlass.class") shouldNotBe null
    }

    test("Test load subclass (symbol only)") {

        val resolver = AsmSymbolResolver(testTypeSystem, brokenCp)

        val found = resolver.resolveClassFromBinaryName("javasymbols.brokenclasses.BrokenGeneric")
            ?: fail("not found")

        found.superclass!!::getBinaryName shouldBe "javasymbols.brokenclasses.SuperKlass"
        found.superInterfaces!!.map { it.binaryName } shouldBe listOf("javasymbols.brokenclasses.SuperItf")

        withClue("Isn't resolved") {
            found.superInterfaces[0]::isUnresolved shouldBe true
        }
    }


    test("Test load from typesystem") {

        val ts = TypeSystem.usingClasspath(brokenCp)
        val subclassSym = ts.getClassSymbol("javasymbols.brokenclasses.BrokenGeneric")!!

        val unresolvedItfSym = subclassSym.superInterfaces[0]
        unresolvedItfSym::isUnresolved shouldBe true

        // since we're loading things lazily this type hasn't tried to populate its superinterfaces
        val superItfType = ts.declaration(unresolvedItfSym) as JClassType
        val subclassType = ts.declaration(subclassSym) as JClassType
        val (tvarC, tvarD) = subclassType.formalTypeParams

        // and now since the super interface *type* is parameterized, we'll try to create SuperItf<D,D>
        // Except SuperItf is unresolved.

        val expected = ts.parameterise(unresolvedItfSym, listOf(tvarD, tvarD))

        expected shouldBe superItfType.withTypeArguments(listOf(tvarD, tvarD))
        subclassType.superInterfaces[0] shouldBe expected

        subclassType.isConvertibleTo(expected) shouldBe TypeOps.Convertibility.SUBTYPING
    }

})

fun TypeSystem.createUnresolvedAsmSymbol(binaryName: String) =
    AsmSymbolResolver(this, Classpath.contextClasspath())
        .resolveFromInternalNameCannotFail(binaryName.replace('.', '/'))
