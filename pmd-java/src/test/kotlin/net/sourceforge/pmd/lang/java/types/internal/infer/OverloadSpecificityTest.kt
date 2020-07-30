/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.types.JClassType
import net.sourceforge.pmd.lang.java.types.JMethodSig
import net.sourceforge.pmd.lang.java.types.TypeOps
import net.sourceforge.pmd.lang.java.types.TypeOps.areOverrideEquivalent
import net.sourceforge.pmd.lang.java.types.internal.infer.OverloadComparator.shadows
import net.sourceforge.pmd.lang.java.types.internal.infer.OverloadComparator.shouldTakePrecedence
import net.sourceforge.pmd.lang.java.types.testdata.Overloads
import net.sourceforge.pmd.util.OptionalBool

class OverloadSpecificityTest : ProcessorTestSpec({

    val t_Overloads = "net.sourceforge.pmd.lang.java.types.testdata.Overloads"

    parserTest("Test strict overload") {

        asIfIn(Overloads::class.java)

        inContext(ExpressionParsingCtx) {

            "ambig(\"a\", \"b\")" should parseAs {
                methodCall("ambig") {
                    it.methodType.toString() shouldBe "$t_Overloads.ambig(java.lang.String, java.lang.String, java.lang.CharSequence...) -> void"
                    it.typeMirror.toString() shouldBe "void"

                    it::getArguments shouldBe child {
                        stringLit("\"a\"")
                        stringLit("\"b\"")
                    }
                }
            }
        }
    }

    parserTest("Test generic varargs overload") {

        asIfIn(Overloads::class.java)

        inContext(ExpressionParsingCtx) {

            "genericOf(new String[] {\"\"})" should parseAs {
                methodCall("genericOf") {
                    it.methodType.toString() shouldBe "$t_Overloads.<T> genericOf(java.lang.String...) -> java.util.List<java.lang.String>"
                    it.typeMirror.toString() shouldBe "java.util.List<java.lang.String>"
                    it.isVarargsCall shouldBe false // selected in strict phase

                    it::getArguments shouldBe child {
                        arrayAlloc()
                    }
                }
            }
        }
    }

    parserTest("Test method hidden by enclosing class") {

        val acu = parser.parse(
                """
class Scratch {
    void m(Throwable t) { }
    
    class Foo {
        void m(Throwable t) { }
    }
}

class Other {
    void m(Throwable t) { }
}

                """.trimIndent()
        )

        val (m1, m2, m3) = acu.descendants(ASTMethodDeclaration::class.java).toList { it.sig }


        // precondition
        assert(areOverrideEquivalent(m1, m2)
                && areOverrideEquivalent(m2, m3)) {
            "override equivalence"
        }

        val types = listOf(m1, m2, m3).map { it.declaringType as JClassType }
        val ctx = m2.declaringType as JClassType

        doTest("Scratch.Foo::m should hide Scratch::m") {
            shadows(m2, m1, ctx) shouldBe OptionalBool.YES
        }

        doTest("Scratch::m should not hide Scratch.Foo::m") {
            shadows(m1, m2, ctx) shouldBe OptionalBool.NO
        }

        doTest("A method should not hide itself") {
            types.forEach { ctx ->
                listOf(m1, m2, m3).forEach {
                    shadows(it, it, ctx) shouldBe OptionalBool.UNKNOWN
                }
            }
        }

        doTest("Other::m should be unrelated to the other methods") {
            types.forEach { ctx ->
                shadows(m1, m3, ctx) shouldBe OptionalBool.UNKNOWN
                shadows(m2, m3, ctx) shouldBe OptionalBool.UNKNOWN
                shadows(m3, m1, ctx) shouldBe OptionalBool.UNKNOWN
                shadows(m3, m2, ctx) shouldBe OptionalBool.UNKNOWN
            }
        }
    }

    fun overridingSetup(name: String, mod: String) {

        parserTest(name) {
            val acu = parser.parse(
                    """
class Scratch {
   $mod void m(Throwable t) { }
}

class Other extends Scratch {
    $mod void m(Throwable t) { }
    $mod void m(Exception t) { }
}

                """.trimIndent()
            )

            val (scratchM, otherM, otherOverload) = acu.descendants(ASTMethodDeclaration::class.java).toList { it.sig }


            assert(areOverrideEquivalent(scratchM, otherM)) {
                "Precondition: override-equivalence"
            }

            fun doesOverride(m1: JMethodSig, m2: JMethodSig) =
                    TypeOps.overrides(m1, m2, otherM.declaringType)

            doTest("Other::m should override Scratch::m") {
                doesOverride(otherM, scratchM) shouldBe true
            }

            doTest("Scratch::m should not override Other::m") {
                doesOverride(scratchM, otherM) shouldBe false
            }

            doTest("Overloads don't override each other") {
                doesOverride(otherM, otherOverload) shouldBe false
                doesOverride(otherOverload, otherM) shouldBe false
                doesOverride(otherOverload, scratchM) shouldBe false
                doesOverride(scratchM, otherOverload) shouldBe false
            }

            doTest("The overriding method should be preferred") {
                val ctx = otherM.declaringType as JClassType
                shouldTakePrecedence(otherM, scratchM, ctx) shouldBe OptionalBool.YES
                shouldTakePrecedence(scratchM, otherM, ctx) shouldBe OptionalBool.NO

            }

            doTest("A method should override itself by convention") {
                listOf(scratchM, otherM, otherOverload).forEach {
                    doesOverride(it, it) shouldBe true
                }
            }
        }
    }

    overridingSetup("Test public static method hiding", "public static")
    overridingSetup("Test protected static method hiding", "protected static")
    overridingSetup("Test package-private static method hiding", "static")

    overridingSetup("Test public method overriding", "public")
    overridingSetup("Test protected method overriding", "protected")
    overridingSetup("Test package-private method overriding", "")


    parserTest("Both inherited and visible from outer class") {
        val acu = parser.parse(
                """
class Scratch {
   static void m(Throwable t) { }
   
   static class Inner extends Scratch {
      static void m(Throwable t) { } 
   }
}
                """.trimIndent()
        )

        val (scratchM, otherM) = acu.descendants(ASTMethodDeclaration::class.java).toList { it.sig }


        assert(areOverrideEquivalent(scratchM, otherM)) {
            "Precondition: override-equivalence"
        }

        fun doesOverride(m1: JMethodSig, m2: JMethodSig) =
                TypeOps.overrides(m1, m2, otherM.declaringType)

        doTest("Other::m should override Scratch::m") {
            doesOverride(otherM, scratchM) shouldBe true
        }

        doTest("Scratch::m should not override Other::m") {
            doesOverride(scratchM, otherM) shouldBe false
        }
    }

    parserTest("Merged abstract signature in class") {
        val acu = parser.parse(
                """
class Scratch {
   static void m(Throwable t) { }
   
   static class Inner extends Scratch {
      static void m(Throwable t) { } 
   }
}
                """.trimIndent()
        )

        val (scratchM, otherM) = acu.descendants(ASTMethodDeclaration::class.java).toList { it.sig }


        assert(areOverrideEquivalent(scratchM, otherM)) {
            "Precondition: override-equivalence"
        }


        fun doesOverride(m1: JMethodSig, m2: JMethodSig) =
                TypeOps.overrides(m1, m2, otherM.declaringType)

        doTest("Other::m should override Scratch::m") {
            doesOverride(otherM, scratchM) shouldBe true
        }

        doTest("Scratch::m should not override Other::m") {
            doesOverride(scratchM, otherM) shouldBe false
        }
    }

})
