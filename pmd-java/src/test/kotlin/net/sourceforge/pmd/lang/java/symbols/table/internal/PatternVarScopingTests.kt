/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal

import io.kotest.assertions.withClue
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.java.ast.*

/**
 *
 */
class PatternVarScopingTests : ProcessorTestSpec({


    fun ParserTestCtx.checkVars(firstIsPattern: Boolean, secondIsPattern: Boolean, code: () -> String) {
        val exprCode = code().trimIndent()
        val sourceCode = """
                class Foo {
                    int var; // a field
                    {
                        someFun( $exprCode
                         ); // move that out so it doesn't get commented out
                    }
                }
            """.trimIndent()
        val acu = this.parser.parse(sourceCode)


        val expr = acu.descendants(ASTArgumentList::class.java)[0]!!
        val (var1, var2) = expr.descendants(ASTMethodCall::class.java).crossFindBoundaries()
            .map { it.qualifier as ASTVariableAccess }.toList()
        withClue("First var in\n$exprCode") {
            var1.referencedSym!!.tryGetNode()!!::isPatternBinding shouldBe firstIsPattern
        }
        withClue("Second var in\n$exprCode") {
            var2.referencedSym!!.tryGetNode()!!::isPatternBinding shouldBe secondIsPattern
        }
    }

    parserTest("Bindings with if/else", javaVersion = JavaVersion.J17) {

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
        doTest("Test while(true) is handled correctly") {

            checkVars(firstIsPattern = false, secondIsPattern = true) {
                """
                args -> {
                        if (!(args[0] instanceof Boolean var)) {
                            var.toString(); // the field
                            while (true) { }
                        }
                        var.toString(); //the binding
                }
                """
            }
        }
    }

    parserTest("Bindings within condition", javaVersion = JavaVersion.J17) {


        doTest("Condition with and") {

            checkVars(firstIsPattern = true, secondIsPattern = false) {
                """
                a -> {
                    if (a instanceof String var && var.isEmpty()) { // the binding
                    }
                    var.toString(); // the field
                }
                """
            }
        }

        doTest("Condition with or (negated)") {

            checkVars(firstIsPattern = true, secondIsPattern = false) {
                """
                a -> {
                    if (!(a instanceof String var) || var.isEmpty()) { // the binding
                    }
                    var.toString(); // the field
                }
                """
            }
        }
        doTest("Condition with or") {

            checkVars(firstIsPattern = false, secondIsPattern = false) {
                """
                a -> {
                    if (a instanceof String var || var.isEmpty()) { // the field
                    }
                    var.toString(); // the field
                }
                """
            }
        }
    }


    parserTest("Bindings within ternary", javaVersion = JavaVersion.J17) {


        doTest("Positive cond") {

            checkVars(firstIsPattern = true, secondIsPattern = false) {
                """
                a -> a instanceof String var ? var.isEmpty() // the binding
                                             : var.isEmpty() // the field
                """
            }
        }
        doTest("Negative cond") {

            checkVars(firstIsPattern = false, secondIsPattern = true) {
                """
                a -> !(a instanceof String var) ? var.isEmpty() // the field
                                                : var.isEmpty() // the binding
                """
            }
        }

    }



    parserTest("Bindings within labeled stmt", javaVersion = JavaVersion.J17) {

        checkVars(firstIsPattern = false, secondIsPattern = true) {
            """
                a -> {
                    label:
                        if (!(a instanceof String var)) {
                            var.toString(); // the field
                            return;
                        }
                    var.toString(); // the binding
                }
                """
        }
    }

    parserTest("Bindings within switch expr with yield", javaVersion = JavaVersion.J17) {

        checkVars(firstIsPattern = false, secondIsPattern = true) {
            """
                a -> switch (1) {
                    case 1 -> {
                        if (!(a instanceof String var)) {
                            var.toString(); // the field
                            yield 12;
                        }
                        var.toString(); // the binding
                        yield 2;
                   }
                }
                """
        }
    }


    parserTest("Bindings within for loop", javaVersion = JavaVersion.J17) {


        doTest("Positive cond") {

            checkVars(firstIsPattern = true, secondIsPattern = false) {
                """
                a -> {
                    for (; a instanceof String var; var = var.substring(1)) { // the binding

                    }
                    var.toString(); // the field
                }
                """
            }
        }
        doTest("Negated cond, body does nothing") {

            checkVars(firstIsPattern = false, secondIsPattern = true) {
                """
                a -> {
                    for (; !(a instanceof String var); var = var.substring(1)) { // the field

                    }
                    var.toString(); // the binding though it is unreachable
                }
                """
            }
        }
        doTest("Negated cond, body doesn't break") {

            checkVars(firstIsPattern = false, secondIsPattern = true) {
                """
                a -> {
                    for (; !(a instanceof String var); var = var.substring(1)) { // the field
                        while (true) {
                            break;
                        }
                    }
                    var.toString(); // the binding
                }
                """
            }
        }
        doTest("Negated cond, body does break") {

            checkVars(firstIsPattern = false, secondIsPattern = false) {
                """
                a -> {
                    for (; !(a instanceof String var); var = var.substring(1)) { // the field
                        break;
                    }
                    var.toString(); // the field
                }
                """
            }
        }
        doTest("Both bindings and init vars are in scope") {
            inContext(StatementParsingCtx) {
                val loop = doParse(
                    """
                 for (String x=""; a instanceof String v; v = x.substring(1)) {
                        break;
                 }
                """
                ).shouldBeA<ASTForStatement>()

                val (x, v) = loop.descendants(ASTVariableDeclaratorId::class.java).toList()

                val (_, vref, xref) = loop.descendants(ASTVariableAccess::class.java).toList()
                vref.shouldResolveToLocal(v)
                xref.shouldResolveToLocal(x)
            }
        }
    }
    parserTest("Bindings within while loop", javaVersion = JavaVersion.J17) {


        doTest("Positive cond") {

            checkVars(firstIsPattern = true, secondIsPattern = false) {
                """
                a -> {
                    while(a instanceof String var) {
                        var.toString(); // the binding
                    }
                    var.toString(); // the field
                }
                """
            }
        }
        doTest("Negated cond") {

            checkVars(firstIsPattern = false, secondIsPattern = true) {
                """
                a -> {
                    while(!(a instanceof String var)) {
                        var.toString(); // the field
                    }
                    var.toString(); // the binding though it is unreachable
                }
                """
            }
        }
        doTest("Negated cond, body doesn't break") {

            checkVars(firstIsPattern = false, secondIsPattern = true) {
                """
                a -> {
                    while(!(a instanceof String var)) {
                        var.toString(); // the field
                        while (true) {
                            break;
                        }
                    }
                    var.toString(); // the binding
                }
                """
            }
        }
        doTest("Negated cond, body does break") {

            checkVars(firstIsPattern = false, secondIsPattern = false) {
                """
                a -> {
                    while(!(a instanceof String var)) {
                        var.toString(); // the field
                        break;
                    }
                    var.toString(); // the field
                }
                """
            }
        }
    }

})
