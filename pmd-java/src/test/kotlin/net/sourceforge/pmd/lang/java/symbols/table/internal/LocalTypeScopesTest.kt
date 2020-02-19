/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal

import io.kotlintest.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration
import net.sourceforge.pmd.lang.java.ast.ParserTestSpec
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol

class LocalScopesTest : ParserTestSpec({


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

        val (insideFoo, insideInner, insideOther) =
                acu.descendants(ASTFieldDeclaration::class.java).toList()

        doTest("Inside a type: other toplevel types and inner classes are in scope") {

            val fooSym = insideFoo.symbolTable.shouldResolveTypeTo<JClassSymbol>("Foo") {
                result::getCanonicalName shouldBe "myTest.Foo"
            }

            insideFoo.symbolTable.shouldResolveTypeTo<JClassSymbol>("Inner") {
                result::getCanonicalName shouldBe "myTest.Foo.Inner"
                result::getEnclosingClass shouldBe fooSym
            }

            insideFoo.symbolTable.shouldResolveTypeTo<JClassSymbol>("Other") {
                result::getCanonicalName shouldBe "myTest.Other"
                result::getEnclosingClass shouldBe null
            }

        }

        doTest("Inside a sibling: inner classes are not in scope") {

            insideOther.symbolTable.shouldResolveTypeTo<JClassSymbol>("Foo") {
                result::getCanonicalName shouldBe "myTest.Foo"
            }

            insideOther.symbolTable.resolveTypeName("Inner") shouldBe null

            insideOther.symbolTable.shouldResolveTypeTo<JClassSymbol>("Other") {
                result::getCanonicalName shouldBe "myTest.Other"
                result::getEnclosingClass shouldBe null
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

            insideFoo.symbolTable.shouldResolveTypeTo<JClassSymbol>("Inner") {
                result::getCanonicalName shouldBe "myTest.Foo.Inner"

                contributor.shouldBeA<ASTClassOrInterfaceDeclaration> {
                    it::getSymbol shouldBe result
                }
            }

            insideInner.symbolTable.shouldResolveTypeTo<JClassSymbol>("Inner") {
                result::getCanonicalName shouldBe "myTest.Foo.Inner"

                contributor.shouldBeA<ASTClassOrInterfaceDeclaration> {
                    it::getSymbol shouldBe result
                }
            }
        }

        doTest("Inside extends clause: Inner is the import") {

            foo.superClassTypeNode.symbolTable.shouldResolveTypeTo<JClassSymbol>("Inner") {
                result::getCanonicalName shouldBe "somewhere.Inner"
                contributor.shouldBeA<ASTImportDeclaration> {}
            }
        }

        doTest("Inside Other: Inner is imported") {

            insideOther.symbolTable.shouldResolveTypeTo<JClassSymbol>("Inner") {
                result::getCanonicalName shouldBe "somewhere.Inner"
                result::isUnresolved shouldBe true

                contributor.shouldBeA<ASTImportDeclaration>()
            }
        }
    }
})
