/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal

import io.kotlintest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.shouldBe
import net.sourceforge.pmd.lang.ast.test.component6
import net.sourceforge.pmd.lang.ast.test.component7
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol
import org.checkerframework.checker.nullness.qual.NonNull

class MemberInheritanceTest : ParserTestSpec({


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
                acu.descendants(ASTMethodDeclaration::class.java).toList()

        doTest("Inside Sup: Sup#f(int) is in scope") {

            supF.symbolTable.methods().resolve("f").let {
                it.shouldHaveSize(1)
                it[0] shouldBe supF.symbol
            }
        }

        doTest("Inside Outer: both Sup#f(int) and Outer#f() are in scope") {
            outerF.symbolTable.methods().resolve("f").let {
                it.shouldHaveSize(2)
                it.shouldContainExactlyInAnyOrder(supF.symbol, outerF.symbol)
            }
        }

        doTest("Inside Inner: neither Sup#f(int) nor Outer#f() are in scope") {
            // only Inner#f() and Sup2#f(String)
            innerF.symbolTable.methods().resolve("f").let {
                it.shouldHaveSize(2)
                it.shouldContainExactlyInAnyOrder(sup2F.symbol, innerF.symbol)
            }
        }

        doTest("If there is no shadowing then declarations of outer classes are in scope (g methods)") {
            // only Inner#f() and Sup2#f(String)
            innerF.symbolTable.methods().resolve("g").let {
                it.shouldHaveSize(2)
                it.shouldContainExactlyInAnyOrder(supG.symbol, outerG.symbol)
            }
        }
    }


    parserTest("Shadowing of inherited types") {

        doTest("Inaccessible member types of supertypes shadow types inherited from further supertypes") {
            val acu = parser.withProcessing().parse("""

            interface M { class E {} }

            class Sup implements M {
                private class E {} // shadows M.E, inaccessible from Foo
            }

            class Foo extends Sup {
                E e; // not in scope
            }
        """)

            val (insideFoo) = acu.descendants(ASTFieldDeclaration::class.java).toList()

            insideFoo.symbolTable.types().resolveFirst("E") shouldBe null
        }

        doTest("Accessible member types of supertypes shadow types inherited from further supertypes") {
            val acu = parser.withProcessing().parse("""

            interface M { class E {} }

            class Sup implements M {
                class E {} // shadows M.E
            }

            class Foo extends Sup {
                E e; // Sup.E
            }
        """)

            val (m, me, sup, supe, foo) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.symbol }

            val (insideFoo) = acu.descendants(ASTFieldDeclaration::class.java).toList()

            insideFoo.symbolTable.shouldResolveTypeTo<JClassSymbol>("E", supe)
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

            val (m, me, sup, supf, supk, foo, fook) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.symbol }

            val (insideSup, insideFoo) = acu.descendants(ASTFieldDeclaration::class.java).toList()

            insideFoo.symbolTable.shouldResolveTypeTo("E", me)
            insideFoo.symbolTable.shouldResolveTypeTo("F", supf)
            insideFoo.symbolTable.shouldResolveTypeTo("K", fook)

            insideSup.symbolTable.shouldResolveTypeTo("K", supk)
        }
    }

})
