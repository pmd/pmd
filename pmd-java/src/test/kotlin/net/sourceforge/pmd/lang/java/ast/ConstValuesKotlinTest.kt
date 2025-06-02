/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol
import net.sourceforge.pmd.lang.test.ast.shouldBeA

/**
 *
 */
class ConstValuesKotlinTest : ProcessorTestSpec({
    parserTest("Test reference cycle doesn't crash resolution") {
        val acu = parser.parse(
            """
            class Foo {
                static final int I1 = I2;
                static final int I2 = I1;
                static final int I3 = 0;
            }
            """.trimIndent()
        )

        val (i1, i2, i3) = acu.descendants(ASTVariableId::class.java).toList()

        i1.initializer!!.constValue shouldBe null
        i2.initializer!!.constValue shouldBe null
        i3.initializer!!.constValue shouldBe 0

        i3.symbol.shouldBeA<JFieldSymbol> {
            it.constValue shouldBe 0
        }
    }


    parserTest("Test non CT constants are still resolved") {
        val acu = parser.parse(
            """
            class Foo {
                final int i1 = 2;
                static final int I1 = 2;
                {
                    final int i2 = 4;
                    final int nonct = i2 * 4 + i1;
                    final int ct = 4 + I1;
                }
            }
            """.trimIndent()
        )

        val (i1, I1, i2, nonct, ct) = acu.descendants(ASTVariableId::class.java).toList()

        i1.initializer!!.constValue shouldBe 2
        I1.initializer!!.constValue shouldBe 2
        i2.initializer!!.constValue shouldBe 4
        nonct.initializer!!.constValue shouldBe null
        ct.initializer!!.constValue shouldBe 6

        nonct.initializer!!.constFoldingResult.isCompileTimeConstant shouldBe false
        nonct.initializer!!.constFoldingResult.value shouldBe 18
    }
})
