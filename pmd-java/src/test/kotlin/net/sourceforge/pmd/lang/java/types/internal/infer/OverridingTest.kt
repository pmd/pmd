/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

@file:Suppress("LocalVariableName")

package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldMatchN
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.types.*
import net.sourceforge.pmd.util.OptionalBool
import kotlin.test.assertFalse

/**
 */
class OverridingTest : ProcessorTestSpec({


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

            val (scratchM, otherM, otherOverload) = acu.methodDeclarations().toList { it.sig }


            assert(TypeOps.areOverrideEquivalent(scratchM, otherM)) {
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
                OverloadSet.shouldAlwaysTakePrecedence(otherM, scratchM, ctx) shouldBe OptionalBool.YES
                OverloadSet.shouldAlwaysTakePrecedence(scratchM, otherM, ctx) shouldBe OptionalBool.NO

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

        val (scratchM, otherM) = acu.methodDeclarations().toList { it.sig }


        assert(TypeOps.areOverrideEquivalent(scratchM, otherM)) {
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


    parserTest("Primitive signatures do not merge") {
        val acu = parser.parse(
                """
class Scratch {
   void m(long t) { }

   static class Inner extends Scratch {
      void m(int t) { }
   }
}
                """.trimIndent()
        )

        val (scratchM, otherM) = acu.methodDeclarations().toList { it.sig }


        assertFalse("Methods are not override-equivalent") {
            TypeOps.areOverrideEquivalent(scratchM, otherM)
        }
    }

    parserTest("Primitive signatures do not merge 2") {
        val acu = parser.parse(
                """
class Scratch {
   void m(int t) { }

   static class Inner extends Scratch {
      void m(long t) { }
   }
}
                """.trimIndent()
        )

        val (scratchM, otherM) = acu.methodDeclarations().toList { it.sig }


        assertFalse("Methods are not override-equivalent") {
            TypeOps.areOverrideEquivalent(scratchM, otherM)
        }
    }



    parserTest("Private method shadowed in inner class") {
        val acu = parser.parse(
                """
class Scratch {
   private void m(Throwable t) { } // private methods are not overridden

   static class Inner extends Scratch {
      private void m(Throwable t) { }
   }
}
                """.trimIndent()
        )

        val (scratchM, otherM) = acu.methodDeclarations().toList { it.sig }


        assert(TypeOps.areOverrideEquivalent(scratchM, otherM)) {
            "Precondition: override-equivalence"
        }

        fun doesOverride(m1: JMethodSig, m2: JMethodSig) =
                TypeOps.overrides(m1, m2, otherM.declaringType)

        doTest("Neither should override the other, they're private") {
            doesOverride(otherM, scratchM) shouldBe false
            doesOverride(scratchM, otherM) shouldBe false
        }

        doTest("But precedence should still be decided") {
            OverloadSet.shouldAlwaysTakePrecedence(scratchM, otherM, otherM.declaringType) shouldBe OptionalBool.NO
            OverloadSet.shouldAlwaysTakePrecedence(otherM, scratchM, otherM.declaringType) shouldBe OptionalBool.YES
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

        val (scratchM, otherM) = acu.methodDeclarations().toList { it.sig }


        assert(TypeOps.areOverrideEquivalent(scratchM, otherM)) {
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
