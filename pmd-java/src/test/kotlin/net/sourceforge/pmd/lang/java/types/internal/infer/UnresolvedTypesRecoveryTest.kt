/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

@file:Suppress("LocalVariableName")

package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.ast.test.shouldMatchN
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol
import net.sourceforge.pmd.lang.java.types.*

/**
 */
class UnresolvedTypesRecoveryTest : ProcessorTestSpec({

    parserTest("Test failed invoc context lets args be inferred as standalones") {

        val acu = parser.parse(
                """
import java.io.IOException;
import ooo.Unresolved;

class C {

    static {
        try { } catch (IOException ioe) {
            throw new Unresolved(ioe.getMessage(), ioe);
        }
    }
}

                """.trimIndent()
        )


        val call = acu.descendants(ASTConstructorCall::class.java).firstOrThrow()

        call.shouldMatchN {
            constructorCall {
                classType("Unresolved") {
                    TypeOps.isUnresolved(it.typeMirror) shouldBe true
                    it.typeMirror.symbol.shouldBeA<JClassSymbol> {
                        it.binaryName shouldBe "ooo.Unresolved"
                    }
                }

                it.methodType shouldBe it.typeSystem.UNRESOLVED_METHOD
                it.typeMirror shouldBe it.typeNode.typeMirror

                argList {
                    methodCall("getMessage") {
                        it.typeMirror shouldBe it.typeSystem.STRING
                        variableAccess("ioe")
                        argList {}
                    }
                    variableAccess("ioe")
                }
            }
        }
    }
})
