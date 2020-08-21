/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.types.*
import net.sourceforge.pmd.lang.java.types.TypeOps.areOverrideEquivalent
import net.sourceforge.pmd.lang.java.types.testdata.Overloads
import net.sourceforge.pmd.util.OptionalBool
import net.sourceforge.pmd.lang.java.types.internal.infer.OverloadSet.shouldAlwaysTakePrecedence as shouldTakePrecedence

private val RefTypeConstants.t_Overloads: JClassType
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
                    with(it.typeDsl) {
                        it.methodType.shouldMatchMethod(
                                named = "genericOf",
                                declaredIn = gen.t_Overloads,
                                withFormals = listOf(gen.t_String.toArray()),
                                returning = gen.t_List[gen.t_String]
                        )

                        // List<String>
                        it.typeMirror shouldBe gen.t_List[gen.t_String]
                        it.overloadSelectionInfo.isVarargsCall shouldBe false // selected in strict phase
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

        val (m1, m2, m3) = acu.methodDeclarations().toList { it.sig }


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


    parserTest("Test override from outside class") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
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

        val (_, subM) = acu.methodDeclarations().toList { it.sig }

        spy.shouldBeOk {
            acu.firstMethodCall().methodType.shouldBeSomeInstantiationOf(subM)
        }
    }

    parserTest("Test hidden method from outside class") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy(

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

        val (_, subM) = acu.methodDeclarations().toList { it.sig }

        spy.shouldBeOk {
            acu.firstMethodCall().methodType.shouldBeSomeInstantiationOf(subM)
        }
    }

    parserTest("Test hidden method inside class") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
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

        val (_, subM) = acu.methodDeclarations().toList { it.sig }

        spy.shouldBeOk {
            acu.firstMethodCall().methodType.shouldBeSomeInstantiationOf(subM)
        }
    }

    parserTest("Test hidden method inside hiding method") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
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

        val (supM, _) = acu.methodDeclarations().toList { it.sig }

        spy.shouldBeOk {
            acu.firstMethodCall().methodType.shouldBeSomeInstantiationOf(supM)
        }
    }

    parserTest("Test distinct primitive overloads from import") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
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

        val call = acu.firstMethodCall()

        spy.shouldBeOk {
            call.methodType.shouldMatchMethod(
                    named = "reverseBytes",
                    declaredIn = long.box(),
                    withFormals = listOf(long),
                    returning = long
            )
        }
    }

    parserTest("Test specificity between generic ctors") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
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
        val genericCtor = acu.ctorDeclarations().get(1)!!.sig // new(C<U>)

        spy.shouldBeOk {
            acu.firstCtorCall().methodType.shouldBeSomeInstantiationOf(genericCtor)
        }
    }


    parserTest("Test specificity between generic and non-generic method") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
                """

class Scratch<N extends Number> {
    class ScratchOfInt extends Scratch<Integer> {}

    <N2 extends Number> N2 getN(Scratch<? extends N2> s) {return null;}

    int getN(ScratchOfInt s) { return 0; }

    static {
        getN(new ScratchOfInt());
    }
}

                """.trimIndent()
        )

        val (_, specific) = acu.methodDeclarations().toList { it.sig }

        spy.shouldBeOk {
            acu.firstMethodCall().methodType.shouldBeSomeInstantiationOf(specific)
        }
    }

    parserTest("!Test specificity between lamdbas") {

        logTypeInference(true)

        val (acu, spy) = parser.parseWithTypeInferenceSpy(
                """
class Scratch {

    interface Runnable { void run(); }
    interface Supplier<E> { E get(); }

    static void bench(String label, Runnable runnable) {  }
    static <T> T bench(String label, Supplier<T> runnable) { return null; }

    static void voidMethod() {}

    static {
        bench("foo", () -> new Scratch());  // selects the supplier
        bench("foo", () -> voidMethod());   // selects the runnable
    }
}
                """.trimIndent()
        )

        val (_, _, withRunnable, withSupplier) = acu.declaredMethodSignatures()

        val (supplierCall, runnableCall) = acu.methodCalls().toList()

        spy.shouldBeOk {
            supplierCall.methodType.shouldBeSomeInstantiationOf(withSupplier)
        }

        spy.shouldBeOk {
            runnableCall.methodType.shouldBeSomeInstantiationOf(withRunnable)
        }
    }

})
