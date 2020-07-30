/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol

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

        val (fooT, inner2T) =
                acu.descendants(ASTTypeParameter::class.java).toList()

        val (insideFoo, insideInner, insideInner2, insideOther) =
                acu.descendants(ASTFieldDeclaration::class.java).toList()

        doTest("Inside Foo: T is Foo#T") {

            insideFoo.symbolTable.shouldResolveTypeTo("T", fooT.symbol)
        }

        doTest("Inside Inner: T is Foo#T") {

            insideInner.symbolTable.shouldResolveTypeTo("T", fooT.symbol)
        }

        doTest("Inside Inner2: T is Inner2#T, shadowed") {

            insideInner2.symbolTable.shouldResolveTypeTo("T", inner2T.symbol)
        }

        doTest("Inside Other: T is imported, type params are not in scope") {

            insideOther.symbolTable.shouldResolveTypeTo<JClassSymbol>("T").let {
                it::getCanonicalName shouldBe "somewhere.T"
                it::isUnresolved shouldBe true
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
                it.symbolTable.shouldResolveTypeTo("X", x.symbol)
            }
        }

        doTest("Bounded by a param to the left") {

            val acu = parser.withProcessing().parse("""

            package myTest;

            class Foo<X, T extends X> {}

            """)

            val (x, t) = acu.descendants(ASTTypeParameter::class.java).toList()

            t.typeBoundNode.shouldBeA<ASTClassOrInterfaceType> {
                it.symbolTable.shouldResolveTypeTo("X", x.symbol)
            }
        }

        doTest("Bounded by itself") {

            val acu = parser.withProcessing().parse("""

            package myTest;

            class Foo<T extends Foo<T>> {}

            """)

            val (t) = acu.descendants(ASTTypeParameter::class.java).toList()

            t.typeBoundNode.shouldBeA<ASTClassOrInterfaceType> {
                it.symbolTable.shouldResolveTypeTo("T", t.symbol)
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
                it.symbolTable.shouldResolveTypeTo("X", x.symbol)
            }

        }

        doTest("TParams of method are in scope in formal parameter section") {

            pt.symbolTable shouldBe px.symbolTable

            pt.symbolTable.shouldResolveTypeTo("T", t2.symbol)
            px.symbolTable.shouldResolveTypeTo("X", x.symbol)
        }

        doTest("TParams of method are in scope in method body") {

            for (node in listOf(vt, vx)) {
                node.symbolTable.shouldResolveTypeTo("T", t2.symbol)
                node.symbolTable.shouldResolveTypeTo("X", x.symbol)
            }
        }

        doTest("TParams of method are *not* in scope in modifier list") {

            val annot = acu.descendants(ASTAnnotation::class.java).first()!!

            annot.symbolTable.shouldResolveTypeTo("T", t.symbol) //not t2
        }

        doTest("Local class shadows type param") {
            vx2.symbolTable.shouldResolveTypeTo("X", localX.symbol)
        }

    }


    parserTest("Type parameters shadow member types") {

        val acu = parser.withProcessing().parse("""

            package myTest;

            class Foo<T> {
                void m(T t) {} // the type param
                class T {
                    void m(T t) {} // the class
                }
            }
        """)

        val (fooClass, innerTClass) =
                acu.descendants(ASTClassOrInterfaceDeclaration::class.java).toList()

        val (tparam) = fooClass.symbol.typeParameters

        val (insideFoo, insideT) =
                acu.descendants(ASTFormalParameter::class.java).toList()

        doTest("Inside Foo: T is Foo#T") {
            insideFoo.symbolTable.shouldResolveTypeTo("T", tparam)
        }

        doTest("Inside Foo.T: T is Foo.T") {
            insideT.symbolTable.shouldResolveTypeTo("T", innerTClass.symbol)
        }
    }


})
