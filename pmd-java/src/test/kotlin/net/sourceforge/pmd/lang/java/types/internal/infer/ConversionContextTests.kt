/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldMatchN
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.types.*
import java.io.BufferedOutputStream
import java.io.DataOutputStream
import java.io.OutputStream

class ConversionContextTests : ProcessorTestSpec({

    parserTest("Test simple contexts") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
            class Foo {
                double foo() {
                    String.valueOf((Double) 1d);
                    return 2;
                }
            }
        """)

        val (valueOf, _, doubleCast, doubleLit, intLit) = acu.descendants(ASTExpression::class.java).toList()

        spy.shouldBeOk {
            valueOf.conversionContextType shouldBe null
            doubleCast.conversionContextType?.targetType shouldBe ts.OBJECT
            doubleLit.conversionContextType?.targetType shouldBe double.box()
            intLit.conversionContextType?.targetType shouldBe double
        }
    }
})
