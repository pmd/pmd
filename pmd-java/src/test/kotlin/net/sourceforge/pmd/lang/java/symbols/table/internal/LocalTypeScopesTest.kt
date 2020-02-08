/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal

import io.kotlintest.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable
import net.sourceforge.pmd.lang.java.symbols.table.ResolveResult

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

        doTest("Inside Other: Inner is imported") {

            insideOther.symbolTable.shouldResolveTypeTo<JClassSymbol>("Inner") {
                result::getCanonicalName shouldBe "somewhere.Inner"
                result::isUnresolved shouldBe true

                contributor.shouldBeA<ASTImportDeclaration>()
            }
        }

    }

    parserTest("Scoping of type parameters") {

        val acu = parser.withProcessing().parse("""

            package myTest;

            import somewhere.T;

            class Foo<T> {
                Foo foo;

                class Inner {
                    T f; // Foo#T
                }

                class Inner2<T> {
                    T f2; // Inner2#T

                }
            }

            class Other {
                T i; // the import
            }
        """)


        val (fooClass, innerClass, inner2Class, otherClass) =
                acu.descendants(ASTClassOrInterfaceDeclaration::class.java).toList()

        val (insideFoo, insideInner, insideInner2, insideOther) =
                acu.descendants(ASTFieldDeclaration::class.java).toList()

        doTest("Inside Foo: T is Foo#T") {

            insideFoo.symbolTable.shouldResolveTypeTo<JTypeParameterSymbol>("T") {
                result::getSimpleName shouldBe "T"
                result::getDeclaringSymbol shouldBe fooClass.symbol

                contributor.shouldBeA<ASTTypeParameter> {
                    it::getSymbol shouldBe result
                }
            }
        }

        doTest("Inside Inner: T is Foo#T") {

            insideInner.symbolTable.shouldResolveTypeTo<JTypeParameterSymbol>("T") {
                result::getSimpleName shouldBe "T"
                result::getDeclaringSymbol shouldBe fooClass.symbol

                contributor.shouldBeA<ASTTypeParameter> {
                    it::getSymbol shouldBe result
                }
            }
        }

        doTest("Inside Inner2: T is Inner2#T, shadowed") {

            insideInner2.symbolTable.shouldResolveTypeTo<JTypeParameterSymbol>("T") {
                result::getSimpleName shouldBe "T"
                result::getDeclaringSymbol shouldBe inner2Class.symbol

                contributor.shouldBeA<ASTTypeParameter> {
                    it::getSymbol shouldBe result
                }
            }
        }

        doTest("Inside Other: T is imported, type params are not in scope") {

            insideOther.symbolTable.shouldResolveTypeTo<JClassSymbol>("T") {
                result::getCanonicalName shouldBe "somewhere.T"
                result::isUnresolved shouldBe true

                contributor.shouldBeA<ASTImportDeclaration>()
            }
        }

    }


})

private inline fun <reified T : JTypeDeclSymbol> JSymbolTable.shouldResolveTypeTo(simpleName: String,
                                                                                  assertions: ResolveResult<T>.() -> Unit): T {
    val result = this.resolveTypeName(simpleName)
    assert(result != null) { "Could not resolve $simpleName inside $this" }
    return result!!.shouldBeA(assertions).result
}
