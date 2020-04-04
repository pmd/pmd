/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal

import io.kotlintest.shouldBe
import net.sourceforge.pmd.lang.ast.test.component6
import net.sourceforge.pmd.lang.ast.test.component7
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol

class LocalTypeScopesTest : ParserTestSpec({


    parserTest("Scoping of types in a compilation unit") {

        val acu = parser.withProcessing().parse("""

            package myTest;

            import java.util.List;

            class Foo {
                Foo foo;

                class Inner {
                    List f;
                }
            }

            class Other {
                Inner i;
            }
        """)

        val (foo, inner, other) =
                acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.symbol }

        val (insideFoo, insideInner, insideOther) =
                acu.descendants(ASTFieldDeclaration::class.java).toList()

        doTest("Inside a type: other toplevel types and inner classes are in scope") {

            insideFoo.symbolTable.shouldResolveTypeTo<JClassSymbol>("Foo", foo)
            insideFoo.symbolTable.shouldResolveTypeTo<JClassSymbol>("Inner", inner)
            insideFoo.symbolTable.shouldResolveTypeTo<JClassSymbol>("Other", other)

        }

        doTest("Inside a sibling: inner classes are not in scope") {

            insideOther.symbolTable.shouldResolveTypeTo<JClassSymbol>("Foo", foo)

            insideOther.symbolTable.resolveTypeName("Inner") shouldBe null

            insideOther.symbolTable.shouldResolveTypeTo<JClassSymbol>("Other", other)

        }

    }

    parserTest("Scoping of local classes") {

        val acu = parser.withProcessing().parse("""
            package myTest;

            class Foo {
                class Inner {}
                void method() {
                    Inner i = new Inner(); // this is a new Foo.Inner
                    class Inner {}         // local class, that shadows Foo.Inner
                    Inner i2 = new Inner(); // this is a new instance of the local class
                }
            }
        """)

        val (_, inner, localInner) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.symbol }

        val (_/*the block*/, iVar, localClass, i2Var) =
                acu.descendants(ASTStatement::class.java).toList()

        doTest("Before the local type declaration, only the nested class is in scope") {

            iVar.symbolTable.shouldResolveTypeTo<JClassSymbol>("Inner", inner)

            listOf(i2Var, localClass).forEach {
                it.symbolTable.shouldResolveTypeTo<JClassSymbol>("Inner", localInner)
            }
        }
    }

    parserTest("Scoping of types w.r.t. imports") {

        val acu = parser.withProcessing().parse("""

            package myTest;

            import somewhere.Inner;

            class Foo extends Inner { // somewhere.Inner
                Foo foo;

                class Inner {
                    List f;
                }
            }

            class Other {
                Inner i;
            }
        """)

        val (foo, inner, other) =
                acu.descendants(ASTClassOrInterfaceDeclaration::class.java).toList()

        val (insideFoo, insideInner, insideOther) =
                acu.descendants(ASTFieldDeclaration::class.java).toList()

        doTest("Inside Foo/Inner: Inner is the inner class") {

            insideFoo.symbolTable.shouldResolveTypeTo("Inner", inner.symbol)

            insideInner.symbolTable.shouldResolveTypeTo<JClassSymbol>("Inner").let {
                it::getCanonicalName shouldBe "myTest.Foo.Inner"
            }
        }

        doTest("Inside extends clause: Inner is the import") {

            foo.superClassTypeNode.symbolTable.shouldResolveTypeTo<JClassSymbol>("Inner").let {
                it::getCanonicalName shouldBe "somewhere.Inner"
            }
        }

        doTest("Inside Other: Inner is imported") {

            insideOther.symbolTable.shouldResolveTypeTo<JClassSymbol>("Inner").let {
                it::getCanonicalName shouldBe "somewhere.Inner"
                it::isUnresolved shouldBe true
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

            insideFoo.symbolTable.resolveTypeName("E") shouldBe null
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

            insideFoo.symbolTable.shouldResolveTypeTo<JClassSymbol>("E", me)
            insideFoo.symbolTable.shouldResolveTypeTo<JClassSymbol>("F", supf)
            insideFoo.symbolTable.shouldResolveTypeTo<JClassSymbol>("K", fook)

            insideSup.symbolTable.shouldResolveTypeTo<JClassSymbol>("K", supk)
        }
    }

})
