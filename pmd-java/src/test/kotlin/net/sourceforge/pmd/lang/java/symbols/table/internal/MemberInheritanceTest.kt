/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal

import io.kotest.assertions.withClue
import io.kotest.matchers.collections.*
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.component6
import net.sourceforge.pmd.lang.ast.test.component7
import net.sourceforge.pmd.lang.ast.test.shouldMatchN
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.ShadowChain
import net.sourceforge.pmd.lang.java.types.JClassType
import net.sourceforge.pmd.lang.java.types.JVariableSig

class MemberInheritanceTest : ParserTestSpec({

    parserTest("Test problem with values scope in enum") {
        inContext(ExpressionParsingCtx) {

            val acu = parser.withProcessing().parse(
                    """
                        package coco;

                        import static coco.Opcode.Set.*;

                        import coco.Opcode.Set;

                        public enum Opcode {;

                            static {
                                // Both Set.values() and Opcode.values() are in scope,
                                Set s =  STANDARD;
                                for (coco.Opcode o: values()) {
                                    
                                }
                            }

                            public enum Set { STANDARD, PICOJAVA }
                        }

                    """.trimIndent())

            val (outer, inner) = acu.descendants(ASTEnumDeclaration::class.java).toList { it.symbol }

            val call = acu.descendants(ASTMethodCall::class.java).firstOrThrow()

            call.shouldMatchN {
                methodCall("values") {

                    it.symbolTable.methods().resolve("values").shouldBeSingleton {
                        it.symbol.enclosingClass shouldBe outer
                    }

                    argList(0) {}
                }
            }

        }
    }


    parserTest("Comb rule: methods of an inner type shadow methods of the enclosing ones") {

        val acu = parser.withProcessing().parse("""
            package test;

            class Sup {
                void f(int i) {}
                void g() {} // different name, so also in scope in Inner
            }

            class Sup2 {
                void f(String i) {}
            }

            class Outer extends Sup {
            
                // f(int) is in scope

                void f() {}
                // different name, so also in scope in Inner
                // notice this one *overrides* Sup#g(), yet is still here
                void g() {} 

                class Inner extends Sup2 {
                    void f() {} // shadows both
                }
            }
        """)

        val (supF, supG, sup2F, outerF, outerG, innerF) =
                acu.descendants(ASTMethodDeclaration::class.java).toList { it.sig }

        val (sup, _, outer, inner) =
                acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.body!! }

        doTest("Inside Sup: Sup#f(int) is in scope") {

            sup.symbolTable.methods().resolve("f").let {
                it.shouldHaveSize(1)
                it[0] shouldBe supF
            }
        }

        doTest("Inside Outer: both Sup#f(int) and Outer#f() are in scope") {
            outer.symbolTable.methods().resolve("f").shouldContainExactly(outerF, supF)
        }

        doTest("Inside Inner: Outer#f() is shadowed") {
            // All of Inner#f(), Sup2#f(String), and Sup#f(int) (through Outer are in scope)
            // But Outer#f() is shadowed by Inner#f()
            inner.symbolTable.methods().resolve("f").shouldContainExactly(innerF, sup2F, supF)
        }

        doTest("If there is no shadowing then declarations of outer classes are in scope (g methods)") {
            // only Outer#g() overrides its parent
            inner.symbolTable.methods().resolve("g").shouldContainExactly(outerG)
        }
    }



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
                declaringType shouldBe acu.typeSystem.OBJECT
            }
        }

    }



    parserTest("Shadowing of inherited types") {

        doTest("Inaccessible member types of supertypes hide types inherited from further supertypes") {
            val acu = parser.withProcessing().parse("""

            interface M { class E {} }

            class Sup implements M {
                private class E {} // hides M.E, inaccessible from Foo
            }

            class Foo extends Sup {
                E e; // not in scope
            }
        """)

            val (insideFoo) = acu.descendants(ASTFieldDeclaration::class.java).toList()

            insideFoo.symbolTable.types().resolveFirst("E") shouldBe null
        }

        doTest("Accessible member types of supertypes hide types inherited from further supertypes") {
            val acu = parser.withProcessing().parse("""

            interface M { class E {} }

            class Sup implements M {
                class E {} // hides M.E
            }

            class Foo extends Sup {
                E e; // Sup.E
            }
        """)

            val (m, me, sup, supe, foo) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }

            val (insideFoo) = acu.descendants(ASTFieldDeclaration::class.java).toList()

            insideFoo.symbolTable.shouldResolveTypeTo<JClassType>("E", supe)
        }

        doTest("All member types should be inherited transitively") {
            val acu = parser.withProcessing().parse("""

            interface M { class E {} }

            class Sup implements M {
                E e; // M.E
                class F {}
                class K {}
            }

            class Foo extends Sup {
                F f; // Sup.F

                class K { }
            }
        """)

            val (m, me, sup, supf, supk, foo, fook) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }

            val (insideSup, insideFoo) = acu.descendants(ASTFieldDeclaration::class.java).toList()

            insideFoo.symbolTable.shouldResolveTypeTo("E", me)
            insideFoo.symbolTable.shouldResolveTypeTo("F", supf)
            insideFoo.symbolTable.shouldResolveTypeTo("K", fook)

            insideSup.symbolTable.shouldResolveTypeTo("K", supk)
        }
    }

    fun ShadowChain<JVariableSig, *>.resolveSyms(name: String) = resolve(name).map { it.symbol }

    parserTest("Ambiguity handling when inheriting members from several unrelated interfaces") {

        // only happens for types & fields, for methods this is handled through override/overload resolution


        doTest("Case 1: two unrelated interfaces") {
            val acu = parser.withProcessing().parse("""

interface I1 {
    class C { }
    C A = new C();
}

interface I2 {
    class C { }
    C A = new C();
}

class Impl implements I1, I2 {
    private final C i = A; // both "C" and "A" are ambiguous
}
        """)

            val (i1, i1c, i2, i2c) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }
            val (i1a, i2a, implA) = acu.descendants(ASTVariableDeclaratorId::class.java).toList()



            withClue("For types") {

                implA.symbolTable.types().resolve("C") shouldBe
                        listOf(i1c, i2c) // ambiguous

            }
            withClue("For fields") {

                implA.symbolTable.variables().resolveSyms("A") shouldBe
                        listOf(i1a.symbol, i2a.symbol) // ambiguous

            }
        }

        doTest("Case 2: two interfaces extending each other, impl has both as direct supertypes") {
            val acu = parser.withProcessing().parse("""

interface I1 {
    class C { }
    C A = new C();
}

interface I2 extends I1 { // <- difference here
    class C { }
    C A = new C();
}

class Impl implements I1, I2 {
    private final C i = A; // both "C" and "A" are still ambiguous
}
        """)

            val (i1, i1c, i2, i2c) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }
            val (i1a, i2a, implA) = acu.descendants(ASTVariableDeclaratorId::class.java).toList()



            withClue("For types") {

                implA.symbolTable.types().resolve("C") shouldBe
                        listOf(i1c, i2c) // ambiguous

            }

            withClue("For fields") {

                implA.symbolTable.variables().resolveSyms("A") shouldBe
                        listOf(i1a.symbol, i2a.symbol) // ambiguous

            }
        }

        doTest("Case 3: two interfaces extending each other, impl has only the deepest as direct supertype") {
            val acu = parser.withProcessing().parse("""

interface I1 {
    class C { }
    C A = new C();
}

interface I2 extends I1 {
    class C { }
    C A = new C();
}

class Impl implements I2 { // <- difference here
    private final C i = A; // I2.A, I2.C
}
        """)

            val (i1, i1c, i2, i2c) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }
            val (i1a, i2a, implA) = acu.descendants(ASTVariableDeclaratorId::class.java).toList()



            withClue("For types") {

                implA.symbolTable.types().resolve("C") shouldBe
                        listOf(i2c) // unambiguous

            }

            withClue("For fields") {

                implA.symbolTable.variables().resolveSyms("A") shouldBe
                        listOf(i2a.symbol) // unambiguous

            }
        }

        doTest("Case 4: I1 as n+1 and n+2 superinterface through superclass ") {
            val acu = parser.withProcessing().parse("""


interface I1 {
    class C { }
    C A = new C();
}

class I2 implements I1 { // <- difference here, this is a class
    class C { }
    C A = new C();
}

class Impl extends I2 implements I1 { // <- still implements I1
    private final C i = A; // both are ambiguous
}

        """)

            val (i1, i1c, i2, i2c) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }
            val (i1a, i2a, implA) = acu.descendants(ASTVariableDeclaratorId::class.java).toList()

            withClue("For types") {

                implA.symbolTable.types().resolve("C") shouldBe
                        listOf(i2c, i1c) // ambiguous

            }

            withClue("For fields") {

                implA.symbolTable.variables().resolveSyms("A") shouldBe
                        listOf(i2a.symbol, i1a.symbol) // ambiguous

            }
        }

        doTest("Case 4.1: superclass doesn't hide unrelated field") {
            val acu = parser.withProcessing().parse("""

interface I1 {
    class C { }
    C A = new C();
}

class I2 {              // <- difference here I2 does not extend I1
    class C { }
    C A = new C();
}

class Impl extends I2 implements I1 { // <- still implements I1
    private final C i = A; // both are ambiguous
}


        """)

            val (i1, i1c, i2, i2c) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }
            val (i1a, i2a, implA) = acu.descendants(ASTVariableDeclaratorId::class.java).toList()

            withClue("For types") {

                implA.symbolTable.types().resolve("C") shouldBe
                        listOf(i2c, i1c) // ambiguous

            }

            withClue("For fields") {

                implA.symbolTable.variables().resolveSyms("A") shouldBe
                        listOf(i2a.symbol, i1a.symbol) // unambiguous

            }
        }


        doTest("Case 4.1: private superclass field member doesn't hide anything") {
            val acu = parser.withProcessing().parse("""

interface I1 {
    class C { }
    C A = new C();
}

class I2 implements I1 { // <- difference here, this is a class
    private class C { }
    private C A = new C();
}

class Impl extends I2 implements I1 { // <- still implements I1
    private final C i = A;            // both come from I1
}

        """)

            val (i1, i1c, i2, i2c) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }
            val (i1a, i2a, implA) = acu.descendants(ASTVariableDeclaratorId::class.java).toList()

            withClue("For types") {

                implA.symbolTable.types().resolve("C") shouldBe
                        listOf(i1c) // unambiguous

            }

            withClue("For fields") {

                implA.symbolTable.variables().resolveSyms("A") shouldBe
                        listOf(i1a.symbol) // unambiguous

            }
        }


        doTest("Case 5: class only extends superclass") {
            val acu = parser.withProcessing().parse("""


interface I1 {
    class C { }
    C A = new C();
}

class I2 implements I1 { // <- difference here, this is a class
    class C { }
    C A = new C();
}

class Impl extends I2 { // <- difference here, doesn't implement I1
    private final C i = A; // both are UNambiguous
}

        """)

            val (i1, i1c, i2, i2c) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }
            val (i1a, i2a, implA) = acu.descendants(ASTVariableDeclaratorId::class.java).toList()

            withClue("For types") {

                implA.symbolTable.types().resolve("C") shouldBe
                        listOf(i2c) // unambiguous

            }

            withClue("For fields") {

                implA.symbolTable.variables().resolveSyms("A") shouldBe
                        listOf(i2a.symbol) // unambiguous

            }
        }

        doTest("Case 6: ambiguity in n+1 supertypes is not transferred to subclass") {
            val acu = parser.withProcessing().parse("""


interface I1 {
    class C { }
    C A = new C();
}

interface I2 {
    class C { }
    C A = new C();
}

class Sup implements I1, I2 {
    class C { }
    C A = new C();
}

class Impl extends Sup  {
    private final C i = A; // no ambiguity, these are the Sup members
}


        """)

            val (i1, i1c, i2, i2c, sup, supC) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }
            val (i1a, i2a, supA, implA) = acu.descendants(ASTVariableDeclaratorId::class.java).toList()

            withClue("For types") {

                implA.symbolTable.types().resolve("C") shouldBe
                        listOf(supC) // ambiguous

            }

            withClue("For fields") {

                implA.symbolTable.variables().resolveSyms("A") shouldBe
                        listOf(supA.symbol) // unambiguous

            }
        }


        doTest("Case 7: ambiguity in n+1 supertypes may be transferred to subclass") {

            val acu = parser.withProcessing().parse("""


interface I1 {
    class C { }
    C A = new C();
}

interface I2 {
    class C { }
    C A = new C();
}

class Sup implements I1, I2 {
    // no declarations to shadow the next ones
}

class Impl extends Sup {
    private final C i = A; // ambiguity for both A and C
}


        """)

            val (i1, i1c, i2, i2c) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }
            val (i1a, i2a, implA) = acu.descendants(ASTVariableDeclaratorId::class.java).toList()

            withClue("For types") {

                implA.symbolTable.types().resolve("C") shouldBe
                        listOf(i1c, i2c) // ambiguous

            }

            withClue("For fields") {

                implA.symbolTable.variables().resolveSyms("A") shouldBe
                        listOf(i1a.symbol, i2a.symbol) // unambiguous

            }
        }

    }

})
