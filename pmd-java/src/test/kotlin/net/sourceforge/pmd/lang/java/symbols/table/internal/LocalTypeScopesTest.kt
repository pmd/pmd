/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.component6
import net.sourceforge.pmd.lang.ast.test.component7
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol
import net.sourceforge.pmd.lang.java.types.JClassType
import net.sourceforge.pmd.lang.java.types.shouldHaveType
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
                acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }

        val (insideFoo, insideInner, insideOther) =
                acu.descendants(ASTFieldDeclaration::class.java).toList()

        doTest("Inside a type: other toplevel types and inner classes are in scope") {

            insideFoo.symbolTable.shouldResolveTypeTo<JClassType>("Foo", foo)
            insideFoo.symbolTable.shouldResolveTypeTo<JClassType>("Inner", inner)
            insideFoo.symbolTable.shouldResolveTypeTo<JClassType>("Other", other)

        }

        doTest("Inside a sibling: inner classes are not in scope") {

            insideOther.symbolTable.shouldResolveTypeTo<JClassType>("Foo", foo)

            insideOther.symbolTable.types().resolveFirst("Inner") shouldBe null

            insideOther.symbolTable.shouldResolveTypeTo<JClassType>("Other", other)

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

        val (_, inner, localInner) = acu.descendants(ASTAnyTypeDeclaration::class.java).crossFindBoundaries().toList { it.typeMirror }

        val (_/*the block*/, iVar, localClass, i2Var) =
                acu.descendants(ASTStatement::class.java).toList()

        doTest("Before the local type declaration, only the nested class is in scope") {

            iVar.symbolTable.shouldResolveTypeTo<JClassType>("Inner", inner)

            listOf(i2Var, localClass).forEach {
                it.symbolTable.shouldResolveTypeTo<JClassType>("Inner", localInner)
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

            insideFoo.symbolTable.shouldResolveTypeTo("Inner", inner.typeMirror)

            insideInner.symbolTable.shouldResolveTypeTo<JClassType>("Inner").let {
                it.symbol::getCanonicalName shouldBe "myTest.Foo.Inner"
            }
        }

        doTest("Inside extends clause: Inner is the import") {

            foo.superClassTypeNode.symbolTable.shouldResolveTypeTo<JClassType>("Inner").let {
                it.symbol::getCanonicalName shouldBe "somewhere.Inner"
            }
        }

        doTest("Inside Other: Inner is imported") {

            insideOther.symbolTable.shouldResolveTypeTo<JClassType>("Inner").let {
                it.symbol::getCanonicalName shouldBe "somewhere.Inner"
                it.symbol::isUnresolved shouldBe true
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

        val (_, cKK, cKkEntry, cN2, cN2i2) =
                acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }

        // setup
        n2.typeMirror.symbol shouldBe cN2.symbol
        mapEntry shouldHaveType with(acu.typeDsl) { java.util.Map.Entry::class.raw }
        kkEntry shouldHaveType cKkEntry
        (kkEntry.typeMirror as JClassType).enclosingType shouldBe cKK // not cN2! this calls getAsSuper
        n2i2 shouldHaveType cN2i2
        i4.typeMirror.symbol?.isUnresolved shouldBe true
    }

})
