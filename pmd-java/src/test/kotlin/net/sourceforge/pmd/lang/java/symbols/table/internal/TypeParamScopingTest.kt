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

class TypeParamScopingTest : ParserTestSpec({

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

    parserTest("Scoping inside a type param section") {

        doTest("Bounded by a param to the right") {

            val acu = parser.withProcessing().parse("""

            package myTest;

            class Foo<T extends X, X> {}

            """)

            val (t, x) = acu.descendants(ASTTypeParameter::class.java).toList()

            t.typeBoundNode.shouldBeA<ASTClassOrInterfaceType> {
                it.symbolTable.shouldResolveTypeTo<JTypeParameterSymbol>("X") {
                    result shouldBe x.symbol
                }
            }
        }

        doTest("Bounded by a param to the left") {

            val acu = parser.withProcessing().parse("""

            package myTest;

            class Foo<X, T extends X> {}

            """)

            val (x, t) = acu.descendants(ASTTypeParameter::class.java).toList()

            t.typeBoundNode.shouldBeA<ASTClassOrInterfaceType> {
                it.symbolTable.shouldResolveTypeTo<JTypeParameterSymbol>("X") {
                    result shouldBe x.symbol
                }
            }
        }

        doTest("Bounded by itself") {

            val acu = parser.withProcessing().parse("""

            package myTest;

            class Foo<T extends Foo<T>> {}

            """)

            val (t) = acu.descendants(ASTTypeParameter::class.java).toList()

            t.typeBoundNode.shouldBeA<ASTClassOrInterfaceType> {
                it.symbolTable.shouldResolveTypeTo<JTypeParameterSymbol>("T") {
                    result shouldBe t.symbol
                }
            }
        }


    }

    parserTest("Type params of methods") {

        val acu = parser.withProcessing().parse("""

            package myTest;

            class Foo<X, T> {
            
                @ /*Foo#*/ T // type params are not in scope in modifier list 
                <T extends /*Foo#*/ X> void foo(T pt, X px) {
                    T vt;
                    X vx;

                    {
                         class X {}

                         X vx2;
                    }
                }
            }

            """)

        // type parameters
        val (x, t, t2) = acu.descendants(ASTTypeParameter::class.java).toList()

        // parameters
        val (pt, px) = acu.descendants(ASTFormalParameter::class.java).map { it.typeNode }.toList()

        // variables
        val (vt, vx, vx2) = acu.descendants(ASTLocalVariableDeclaration::class.java).map { it.typeNode }.toList()

        // classes
        val (_, localX) = acu.descendants(ASTClassOrInterfaceDeclaration::class.java).toList()

        doTest("TParams of class are in scope inside method tparam declaration") {

            t2.typeBoundNode.shouldBeA<ASTClassOrInterfaceType> {
                it.symbolTable.shouldResolveTypeTo<JTypeParameterSymbol>("X") {
                    result shouldBe x.symbol
                }
            }

        }

        doTest("TParams of method are in scope in formal parameter section") {

            pt.symbolTable shouldBe px.symbolTable

            pt.symbolTable.shouldResolveTypeTo<JTypeParameterSymbol>("T") {
                result shouldBe t2.symbol
            }

            px.symbolTable.shouldResolveTypeTo<JTypeParameterSymbol>("X") {
                result shouldBe x.symbol
            }
        }

        doTest("TParams of method are in scope in method body") {

            vt.symbolTable shouldBe vx.symbolTable

            vt.symbolTable.shouldResolveTypeTo<JTypeParameterSymbol>("T") {
                result shouldBe t2.symbol
            }

            vx.symbolTable.shouldResolveTypeTo<JTypeParameterSymbol>("X") {
                result shouldBe x.symbol
            }
        }

        doTest("TParams of method are *not* in scope in modifier list") {

            val annot = acu.descendants(ASTAnnotation::class.java).first()!!

            annot.symbolTable.shouldResolveTypeTo<JTypeParameterSymbol>("T") {
                result shouldBe t.symbol // not t2
            }
        }

        doTest("Local class shadows type param") {

            vx2.symbolTable.shouldResolveTypeTo<JClassSymbol>("X") {
                result shouldBe localX.symbol
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
