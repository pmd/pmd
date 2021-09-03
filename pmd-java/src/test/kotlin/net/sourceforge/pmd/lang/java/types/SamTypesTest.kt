/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

@file:Suppress("LocalVariableName")

package net.sourceforge.pmd.lang.java.types

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration
import net.sourceforge.pmd.lang.java.ast.ProcessorTestSpec

class SamTypesTest : ProcessorTestSpec({

    parserTest("Test SAM when some default overrides an abstract method") {

        val acu = parser.parse("""
            
interface Top<T> {
    void accept(T t);
}

interface Sub extends Top<Integer> {
    default void accept(Integer i) { }
    
    void accept(int i); // this is the single abstract method of Sub
}

        """.trimIndent())

        val (t_Top, t_Sub) = acu.descendants(ASTClassOrInterfaceDeclaration::class.java).toList { it.typeMirror }
        val (topAccept, subAcceptOverride, subAccept) = acu.descendants(ASTMethodDeclaration::class.java).toList { it.genericSignature }

        TypeOps.findFunctionalInterfaceMethod(t_Top) shouldBe topAccept

        TypeOps.findFunctionalInterfaceMethod(t_Sub) shouldBe subAccept

        TypeOps.overrides(subAcceptOverride, topAccept, t_Sub) shouldBe true
    }

})


