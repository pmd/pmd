/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldHaveSize
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.java.ast.JavaNode
import net.sourceforge.pmd.lang.java.symbols.JAccessibleElementSymbol.PRIMITIVE_PACKAGE
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol
import net.sourceforge.pmd.lang.java.symbols.internal.getDeclaredMethods

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ArraySymbolTests : WordSpec({

    val INT_SYM = testTypeSystem.getClassSymbol(java.lang.Integer.TYPE)
    val STRING_SYM = testTypeSystem.getClassSymbol(java.lang.String::class.java)

    fun makeArraySym(comp: JTypeDeclSymbol?) = ArraySymbolImpl(testTypeSystem, comp)

    "An array symbol" should {

        "have a length field" {
            val intArr = makeArraySym(INT_SYM)

            intArr.getDeclaredField("length").shouldBeA<JFieldSymbol> {
                it::isFinal shouldBe true
                it::getSimpleName shouldBe "length"
                it::getPackageName shouldBe PRIMITIVE_PACKAGE
                it::getEnclosingClass shouldBe intArr
            }


            val javanodeArr = makeArraySym(testTypeSystem.getClassSymbol(JavaNode::class.java))


            javanodeArr.getDeclaredField("length").shouldBeA<JFieldSymbol> {
                it::isFinal shouldBe true
                it::getSimpleName shouldBe "length"
                it::getPackageName shouldBe "net.sourceforge.pmd.lang.java.ast"
                it::getEnclosingClass shouldBe javanodeArr
            }
        }

        "have a clone method" {
            val intArr = makeArraySym(INT_SYM)

            intArr.getDeclaredMethods("clone").single().also {
                it::getSimpleName shouldBe "clone"
                it::getPackageName shouldBe PRIMITIVE_PACKAGE
                it::getEnclosingClass shouldBe intArr
            }


            val javanodeArr = makeArraySym(testTypeSystem.getClassSymbol(JavaNode::class.java))

            javanodeArr.getDeclaredMethods("clone").single().also {
                it::getSimpleName shouldBe "clone"
                it::getPackageName shouldBe "net.sourceforge.pmd.lang.java.ast"
                it::getEnclosingClass shouldBe javanodeArr
            }
        }

        "have a constructor method" {
            val intArr = makeArraySym(INT_SYM)

            intArr.constructors.single().also {
                it::getSimpleName shouldBe JConstructorSymbol.CTOR_NAME
                it::getPackageName shouldBe PRIMITIVE_PACKAGE
                it::getEnclosingClass shouldBe intArr
                it.formalParameters.shouldHaveSize(1)
            }


            val javanodeArr = makeArraySym(testTypeSystem.getClassSymbol(JavaNode::class.java))

            javanodeArr.constructors.single().also {
                it::getSimpleName shouldBe JConstructorSymbol.CTOR_NAME
                it::getEnclosingClass shouldBe javanodeArr
                it::getPackageName shouldBe "net.sourceforge.pmd.lang.java.ast"
                it.formalParameters.shouldHaveSize(1)
            }
        }

        "reflect its simple name properly" {

            val stringArr = makeArraySym(STRING_SYM)

            stringArr::getSimpleName shouldBe "String[]"

            val intArr = makeArraySym(INT_SYM)

            intArr::getSimpleName shouldBe "int[]"

            val iiarr = makeArraySym(intArr)

            iiarr::getSimpleName shouldBe "int[][]"

        }

        "reflect its binary name properly" {

            val stringArr = makeArraySym(STRING_SYM)

            stringArr::getBinaryName shouldBe "java.lang.String[]"

            val intArr = makeArraySym(INT_SYM)

            intArr::getBinaryName shouldBe "int[]"

            val iiarr = makeArraySym(intArr)

            iiarr::getBinaryName shouldBe "int[][]"

        }

        "reflect its package name properly" {

            val stringArr = makeArraySym(STRING_SYM)

            stringArr::getPackageName shouldBe "java.lang"

            val iarr = makeArraySym(INT_SYM)

            iarr::getPackageName shouldBe PRIMITIVE_PACKAGE

        }

    }

})
