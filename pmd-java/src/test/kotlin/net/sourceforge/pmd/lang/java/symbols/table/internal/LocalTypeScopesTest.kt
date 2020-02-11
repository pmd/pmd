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
import net.sourceforge.pmd.lang.java.types.typeDsl

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

            insideOther.symbolTable.types().resolveFirst("Inner") shouldBe null

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


    parserTest("Inner class creation expressions should have inner classes in scope") {

        val acu = parser.withProcessing().parse("""
            package scratch;

            import java.util.Map.Entry;

            class Scratch {
                void foo(N2 f2) {   // scratch.N2
                    Entry m;        // Map.Entry
                    f2.new Entry(); // KK.Entry (shadows Map.Entry)
                    f2.new I2();    // N2.I2
                    new I4();       // unresolved
                }
            }

            class KK {
                protected class Entry {}
            }

            class N2 extends KK {
                class I2 {}
            }

            class KKK {
                static class I4 {}
            }
        """)


        val (n2, mapEntry, kkEntry, n2i2, i4) =
                acu.descendants(ASTClassOrInterfaceType::class.java).toList()

        val (_, _, cKkEntry, cN2, cN2i2) =
                acu.descendants(ASTAnyTypeDeclaration::class.java).toList()

        // setup
        n2.referencedSym shouldBe cN2.symbol
        mapEntry.referencedSym shouldBe with(acu.typeDsl) { java.util.Map.Entry::class.decl }
        kkEntry.referencedSym shouldBe cKkEntry.symbol
        n2i2.referencedSym shouldBe cN2i2.symbol
        i4.referencedSym::isUnresolved shouldBe true
    }

})
