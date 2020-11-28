/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal

import io.kotest.assertions.withClue
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.*

/**
 *
 */
class PatternVarTests : ProcessorTestSpec({

    parserTest("Bindings with if/else", javaVersion = JavaVersion.J15__PREVIEW) {

        fun checkVars(firstIsPattern: Boolean, secondIsPattern: Boolean, code: () -> String) {
            val exprCode = code().trimIndent()
            val sourceCode = """
                class Foo {
                    int var; // a field
                    {
                        someFun( $exprCode );
                    }
                }
            """.trimIndent()
            val acu = parser.parse(sourceCode)


            val expr = acu.descendants(ASTArgumentList::class.java)[0]!!
            val (var1, var2) = expr.descendants(ASTMethodCall::class.java).crossFindBoundaries().map { it.qualifier as ASTVariableAccess }.toList()
            withClue("First var in\n$exprCode") {
                var1.referencedSym!!.tryGetNode()!!::isPatternBinding shouldBe firstIsPattern
            }
            withClue("Second var in\n$exprCode") {
                var2.referencedSym!!.tryGetNode()!!::isPatternBinding shouldBe secondIsPattern
            }
        }

        doTest("If with then that falls through") {

            checkVars(firstIsPattern = true, secondIsPattern = false) {
                """
                a -> {
                    if (a instanceof String var) {
                        var.toString(); // the binding
                    }
                    var.toString(); // the field
                }
                """
            }
        }

        doTest("If with then that falls through, negated condition") {

            checkVars(firstIsPattern = false, secondIsPattern = false) {
                """
                a -> {
                    if (!(a instanceof String var)) {
                        var.toString(); // the field
                    }
                    var.toString(); // the field
                }
                """
            }
        }

        doTest("If with else and negated condition") {

            checkVars(firstIsPattern = false, secondIsPattern = true) {
                """
                a -> {
                    if (!(a instanceof String var)) {
                        var.toString(); // the field var
                    } else
                        var.toString(); // the binding
                }
                """
            }
        }

        doTest("If with then that completes abruptly") {

            checkVars(firstIsPattern = false, secondIsPattern = true) {
                """
                a -> {
                    if (!(a instanceof String var)) {
                        var.toString(); // the field var
                        return;
                    }
                    var.toString(); // the binding
                }
                """
            }
        }
    }

})
