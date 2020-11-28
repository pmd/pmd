/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal

import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.component6
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol
import net.sourceforge.pmd.lang.java.symbols.JFormalParamSymbol
import net.sourceforge.pmd.lang.java.symbols.JLocalVariableSymbol
import org.intellij.lang.annotations.Language

/**
 *
 */
class PatternVarTests : ProcessorTestSpec({

    parserTest("Bindings with if/else") {

        val acu = parser.parse("""
class Outer<T> {
    private T var;

    void withIf(Object a) {
        if (a instanceof String var) {
            var.toString(); // the binding
        }
        var.toString(); // the field
    }

    void withIfElse1(Object a) {
        if (!(a instanceof String var)) {
            var.toString(); // the field
        }
        var.toString(); // the field
    }

    void withIfElse2(Object a) {
        if (!(a instanceof String var)) {
            var.toString(); // the field var
            return;
        }
        var.toString(); // the binding
    }

    void withIfElse3(Object a) {
        if (!(a instanceof String var)) {
            var.toString(); // the field var
        } else 
            var.toString(); // the binding
    }
}
        """.trimIndent())

        val (c1, c2, c3, c4) =
                acu.descendants(ASTMethodDeclaration::class.java).toList()

        fun checkVars(m: ASTMethodDeclaration, firstIsPattern: Boolean, secondIsPattern: Boolean) {
            val (var1, var2) = m.descendants(ASTMethodCall::class.java).map { it.qualifier as ASTVariableAccess }.toList()
            withClue("First var in $m") {
                var1.referencedSym!!.tryGetNode()!!.isPatternBinding shouldBe firstIsPattern
            }
            withClue("Second var in $m") {
                var2.referencedSym!!.tryGetNode()!!.isPatternBinding shouldBe secondIsPattern
            }
        }

        doTest("Inside inner class: f is inner field") {

        }
    }

})
