/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol
import net.sourceforge.pmd.lang.java.types.*
import net.sourceforge.pmd.lang.java.types.TypeOps.areOverrideEquivalent
import net.sourceforge.pmd.lang.java.types.internal.infer.OverloadComparator.shouldTakePrecedence
import net.sourceforge.pmd.lang.java.types.testdata.Overloads
import net.sourceforge.pmd.util.OptionalBool
import kotlin.test.assertFalse

private val RefTypeGen.t_Overloads : JClassType
    get() = ts.declaration(ts.getClassSymbol("net.sourceforge.pmd.lang.java.types.testdata.Overloads")!!) as JClassType

class OverloadSpecificityTest : ProcessorTestSpec({

    parserTest("Test strict overload") {

        asIfIn(Overloads::class.java)

        inContext(ExpressionParsingCtx) {

            "ambig(\"a\", \"b\")" should parseAs {
                methodCall("ambig") {
                    with(it.typeDsl) {
                        it.methodType.shouldMatchMethod(
                                named = "ambig",
                                declaredIn = gen.t_Overloads,
                                withFormals = listOf(gen.t_String, gen.t_String, gen.t_CharSequence.toArray()),
                                returning = ts.NO_TYPE
                        )

                        it.typeMirror shouldBe ts.NO_TYPE
                    }

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
                    with (it.typeDsl) {
                        it.methodType.shouldMatchMethod(
                                named = "genericOf",
                                declaredIn = gen.t_Overloads,
                                withFormals = listOf(gen.t_String.toArray()),
                                returning = gen.t_List[gen.t_String]
                        )

                        // List<String>
                        it.typeMirror shouldBe gen.t_List[gen.t_String]
                        it.isVarargsCall shouldBe false // selected in strict phase
                    }

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
    
    class Foo extends Scratch {
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
            shouldTakePrecedence(m2, m1, ctx) shouldBe OptionalBool.YES
        }

        doTest("Scratch::m should not hide Scratch.Foo::m") {
            shouldTakePrecedence(m1, m2, ctx) shouldBe OptionalBool.NO
        }

        doTest("Other::m should be unrelated to the other methods") {
            types.forEach { ctx ->
                shouldTakePrecedence(m1, m3, ctx) shouldBe OptionalBool.UNKNOWN
                shouldTakePrecedence(m2, m3, ctx) shouldBe OptionalBool.UNKNOWN
                shouldTakePrecedence(m3, m1, ctx) shouldBe OptionalBool.UNKNOWN
                shouldTakePrecedence(m3, m2, ctx) shouldBe OptionalBool.UNKNOWN
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

        val (scratchM, otherM) = acu.descendants(ASTMethodDeclaration::class.java).toList { it.sig }


        assertFalse("Methods are not override-equivalent") {
            areOverrideEquivalent(scratchM, otherM)
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

        val (scratchM, otherM) = acu.descendants(ASTMethodDeclaration::class.java).toList { it.sig }


        assertFalse("Methods are not override-equivalent") {
            areOverrideEquivalent(scratchM, otherM)
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

        val (scratchM, otherM) = acu.descendants(ASTMethodDeclaration::class.java).toList { it.sig }


        assert(areOverrideEquivalent(scratchM, otherM)) {
            "Precondition: override-equivalence"
        }

        fun doesOverride(m1: JMethodSig, m2: JMethodSig) =
                TypeOps.overrides(m1, m2, otherM.declaringType)

        doTest("Neither should override the other, they're private") {
            doesOverride(otherM, scratchM) shouldBe false
            doesOverride(scratchM, otherM) shouldBe false
        }

        doTest("But precedence should still be decided") {
            shouldTakePrecedence(scratchM, otherM, otherM.declaringType) shouldBe OptionalBool.NO
            shouldTakePrecedence(otherM, scratchM, otherM.declaringType) shouldBe OptionalBool.YES
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

    parserTest("Test override from outside class") {

        val logGetter = logTypeInference()
        val acu = parser.parse(
                """
class Sup { 
    void m() {}
}

class Sub extends Sup { 
    @Override
    void m() {}
}

class F {
    {
        Sub s = new Sub();
        s.m(); // should be Sub::m, no ambiguity
    }
}

                """.trimIndent()
        )

        val (_, subM) = acu.descendants(ASTMethodDeclaration::class.java).toList { it.sig }
        val call = acu.descendants(ASTMethodCall::class.java).firstOrThrow()

        assert(logGetter().isEmpty())
        call.methodType.symbol shouldBe subM.symbol
        assert(logGetter().isEmpty())
    }

    parserTest("Test hidden method from outside class") {

        val logGetter = logTypeInference()
        val acu = parser.parse(
                """
class Sup { 
    static void m() {}
}

class Sub extends Sup { 
    static void m() {}
}

class F {
    {
        Sub.m(); // should be Sub::m, no ambiguity
    }
}

                """.trimIndent()
        )

        val (_, subM) = acu.descendants(ASTMethodDeclaration::class.java).toList { it.sig }
        val call = acu.descendants(ASTMethodCall::class.java).firstOrThrow()

        assert(logGetter().isEmpty())
        call.methodType.symbol shouldBe subM.symbol
        assert(logGetter().isEmpty())
    }

    parserTest("Test hidden method inside class") {

        val logGetter = logTypeInference()
        val acu = parser.parse(
                """

class Sup {
    static void m() {}
}

class Sub extends Sup {
    static void m() {}
    {
        Sub.m(); // should be Sub::m, no ambiguity
    }
}

                """.trimIndent()
        )

        val (_, subM) = acu.descendants(ASTMethodDeclaration::class.java).toList { it.sig }
        val call = acu.descendants(ASTMethodCall::class.java).firstOrThrow()

        assert(logGetter().isEmpty())
        call.methodType.symbol shouldBe subM.symbol
        assert(logGetter().isEmpty())
    }

    parserTest("Test hidden method inside hiding method") {

        val logGetter = logTypeInference()
        val acu = parser.parse(
                """

class Sup {
    static void m() {}
}

class Sub extends Sup {
    static void m() {
        Sup.m();
    }
}

                """.trimIndent()
        )

        val (supM, _) = acu.descendants(ASTMethodDeclaration::class.java).toList { it.sig }
        val call = acu.descendants(ASTMethodCall::class.java).firstOrThrow()

        assert(logGetter().isEmpty())
        call.methodType.symbol shouldBe supM.symbol
        assert(logGetter().isEmpty())
    }

    parserTest("Test distinct primitive overloads from import") {

        val logGetter = logTypeInference()
        val acu = parser.parse(
                """
import static java.lang.Integer.reverseBytes; // (int) -> int
import static java.lang.Long.reverseBytes; // (long) -> long

class Scratch {
    {
        reverseBytes(0L);
    }
}

                """.trimIndent()
        )

        val call = acu.descendants(ASTMethodCall::class.java).firstOrThrow()

        assert(logGetter().isEmpty())
        with(call.typeDsl) {
            call.methodType.shouldMatchMethod(
                    named = "reverseBytes",
                    declaredIn = long.box(),
                    withFormals = listOf(long),
                    returning = long
            )
        }
        assert(logGetter().isEmpty())
    }

    parserTest("Test specificity between generic ctors") {

        val logGetter = logTypeInference()
        val acu = parser.parse(
                """

class C<U> {
 U fu;
 C() {}
 C(C<U> other) { this.fu = other.fu; }
 C(U fu) { this.fu = fu; }

 static {
     C<String> c = new C<>(new C<>(new C<>()));
 }
}

                """.trimIndent()
        )

        val (t_C) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }

        val call = acu.descendants(ASTConstructorCall::class.java).firstOrThrow()

        assert(logGetter().isEmpty())
        with(call.typeDsl) {
            call.methodType.shouldMatchMethod(
                    named = JConstructorSymbol.CTOR_NAME,
                    declaredIn = t_C[gen.t_String],
                    withFormals = listOf(t_C[gen.t_String]),
                    returning = t_C[gen.t_String]
            )
        }
        assert(logGetter().isEmpty())
    }

})
