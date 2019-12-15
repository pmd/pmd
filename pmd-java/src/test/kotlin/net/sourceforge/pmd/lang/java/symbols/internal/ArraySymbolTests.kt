package net.sourceforge.pmd.lang.java.symbols.internal

import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.specs.WordSpec
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.java.ast.JavaNode
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolFactory
import net.sourceforge.pmd.lang.java.symbols.testSymFactory

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ArraySymbolTests : WordSpec({

    "An array symbol" should {

        "have a length field" {
            val intArr = testSymFactory.makeArraySymbol(SymbolFactory.INT_SYM)

            intArr.getDeclaredField("length").shouldBeA<JFieldSymbol> {
                it::isFinal shouldBe true
                it::getSimpleName shouldBe "length"
                it::getPackageName shouldBe "java.lang"
                it::getEnclosingClass shouldBe intArr
            }


            val javanodeArr = testSymFactory.makeArraySymbol(testSymFactory.getClassSymbol(JavaNode::class.java))


            javanodeArr.getDeclaredField("length").shouldBeA<JFieldSymbol> {
                it::isFinal shouldBe true
                it::getSimpleName shouldBe "length"
                it::getPackageName shouldBe "net.sourceforge.pmd.lang.java.ast"
                it::getEnclosingClass shouldBe javanodeArr
            }
        }

        "have a clone method" {
            val intArr = testSymFactory.makeArraySymbol(SymbolFactory.INT_SYM)

            intArr.getDeclaredMethods("clone").single().also {
                it::getSimpleName shouldBe "clone"
                it::getPackageName shouldBe "java.lang"
                it::getEnclosingClass shouldBe intArr
            }


            val javanodeArr = testSymFactory.makeArraySymbol(testSymFactory.getClassSymbol(JavaNode::class.java))

            javanodeArr.getDeclaredMethods("clone").single().also {
                it::getSimpleName shouldBe "clone"
                it::getPackageName shouldBe "net.sourceforge.pmd.lang.java.ast"
                it::getEnclosingClass shouldBe javanodeArr
            }
        }

        "have a constructor method" {
            val intArr = testSymFactory.makeArraySymbol(SymbolFactory.INT_SYM)

            intArr.constructors.single().also {
                it::getSimpleName shouldBe JConstructorSymbol.CTOR_NAME
                it::getPackageName shouldBe "java.lang"
                it::getEnclosingClass shouldBe intArr
                it.formalParameters.shouldHaveSize(1)
            }


            val javanodeArr = testSymFactory.makeArraySymbol(testSymFactory.getClassSymbol(JavaNode::class.java))

            javanodeArr.constructors.single().also {
                it::getSimpleName shouldBe JConstructorSymbol.CTOR_NAME
                it::getEnclosingClass shouldBe javanodeArr
                it::getPackageName shouldBe "net.sourceforge.pmd.lang.java.ast"
                it.formalParameters.shouldHaveSize(1)
            }
        }

        "reflect its simple name properly" {

            val intArr = testSymFactory.makeArraySymbol(SymbolFactory.INT_SYM)

            intArr::getSimpleName shouldBe "int[]"

            val iiarr = testSymFactory.makeArraySymbol(intArr)

            iiarr::getSimpleName shouldBe "int[][]"

        }

        "reflect its binary name properly" {

            val intArr = testSymFactory.makeArraySymbol(SymbolFactory.INT_SYM)

            intArr::getBinaryName shouldBe "int[]"

            val iiarr = testSymFactory.makeArraySymbol(intArr)

            iiarr::getBinaryName shouldBe "int[][]"

        }

    }

})
