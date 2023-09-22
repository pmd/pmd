/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol

/**
 *
 */
class ConstValuesKotlinTest : ProcessorTestSpec({


    parserTest("Test reference cycle doesn't crash resolution") {

        val acu = parser.parse("""
            class Foo {
                static final int I1 = I2;
                static final int I2 = I1;
                static final int I3 = 0;
            }
        """.trimIndent())

        val (i1, i2, i3) = acu.descendants(ASTVariableDeclaratorId::class.java).toList()


        i1.initializer!!.constValue shouldBe null
        i2.initializer!!.constValue shouldBe null
        i3.initializer!!.constValue shouldBe 0

        i3.symbol.shouldBeA<JFieldSymbol> {
            it.constValue shouldBe 0
        }
    }

})
