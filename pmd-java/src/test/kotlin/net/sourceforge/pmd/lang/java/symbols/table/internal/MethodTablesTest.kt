/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal

import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.shouldBe
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall
import net.sourceforge.pmd.lang.java.ast.ParserTestSpec

class MethodTablesTest : ParserTestSpec({


    parserTest("Methods of Object are in scope in interfaces") {

        val acu = parser.withProcessing().parse("""
            interface Foo {
                default Class<? extends Foo> foo() {
                    return getClass();
                }
            }
        """)

        val (insideFoo) =
                acu.descendants(ASTMethodCall::class.java).toList()

        insideFoo.symbolTable.methods().resolve("getClass").also {
            it.shouldHaveSize(1)
            it[0].apply {
                formalParameters shouldBe emptyList()
                declaringType shouldBe insideFoo.typeSystem.OBJECT
            }
        }

    }

})
