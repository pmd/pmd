/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol
import net.sourceforge.pmd.lang.java.symbols.table.internal.JavaSemanticErrors.*
import net.sourceforge.pmd.lang.java.types.JClassType
import net.sourceforge.pmd.lang.java.types.Substitution
import net.sourceforge.pmd.lang.test.ast.*
import net.sourceforge.pmd.lang.test.ast.shouldBe
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TypeDisambiguationTest : ParserTestSpec({
    parserTest("Inner class names") {
        enableProcessing()

        val acu = parser.parse(
                """
            class Foo {
               Foo.Inner f1;
               class Inner { }
            }
        """
        )

        val (foo, inner) = acu.descendants(ASTTypeDeclaration::class.java).toList { it.symbol }
        val (f1) = acu.descendants(ASTFieldDeclaration::class.java).toList()

        f1.typeNode.shouldMatchNode<ASTClassType> {
            it::isFullyQualified shouldBe false
            it::getSimpleName shouldBe "Inner"
            it::getReferencedSym shouldBe inner
            it::getAmbiguousLhs shouldBe null
            it::getQualifier shouldBe classType("Foo") {
                it::isFullyQualified shouldBe false
                it::getPackageQualifier shouldBe null
                it::getReferencedSym shouldBe foo
            }
        }
    }


    parserTestContainer("Fully qualified names") {
        enableProcessing(true)

        inContext(TypeParsingCtx) {
            "javasymbols.testdata.Statics" should parseAs {
                qualClassType("javasymbols.testdata.Statics") {
                    it::isFullyQualified shouldBe true
                    it::getPackageQualifier shouldBe "javasymbols.testdata"
                    it::getQualifier shouldBe null
                    it::getAmbiguousLhs shouldBe null
                }
            }

            "javasymbols.testdata.Statics.PublicStatic" should parseAs {
                qualClassType("javasymbols.testdata.Statics.PublicStatic") {
                    it::isFullyQualified shouldBe false

                    it::getQualifier shouldBe qualClassType("javasymbols.testdata.Statics") {
                        it::isFullyQualified shouldBe true
                        it::getPackageQualifier shouldBe "javasymbols.testdata"
                        it::getQualifier shouldBe null
                        it::getAmbiguousLhs shouldBe null
                    }
                }
            }
        }
    }

    parserTestContainer("Package names in module") {
        val code = """
            module java.base {
               opens java.util;
               provides java.util.Map with java.util.HashMap;
               //       ^^^^^^^^^          ^^^^^^^^^
            }
        """

        doTest("test without disambig") {
            val acu = parser.parse(code)

            acu.descendants(ASTAmbiguousName::class.java).toList().shouldHaveSize(2)
        }

        doTest("test with disambig") {
            enableProcessing()
            val acu = parser.parse(code)

            acu.descendants(ASTAmbiguousName::class.java).toList().shouldBeEmpty()
        }
    }


    parserTestContainer("Failures") {
        val logger = enableProcessing()

        val acu = parser.parse("""
            package com;
            class Foo {
               Foo.Bar f1;
               class Inner { }
            }
        """)

        val (foo) = acu.descendants(ASTTypeDeclaration::class.java).toList()
        val (fooBar) = acu.descendants(ASTClassType::class.java).toList()


        doTest("Unresolved inner type should produce a warning") {
            val (node, args) = logger.warnings[CANNOT_RESOLVE_MEMBER]!![0]
            args.map { it.toString() } shouldBe listOf("Bar", "com.Foo", "an unresolved type")
            node.shouldBeA<ASTClassType> { }
        }

        doTest("Unresolved inner type should have a symbol anyway") {
            fooBar.shouldMatchN {
                classType("Bar") {
                    classType("Foo") {
                        it::getReferencedSym shouldBe foo.symbol
                    }

                    it.referencedSym.shouldBeA<JClassSymbol> {
                        it::isUnresolved shouldBe true
                        it::getSimpleName shouldBe "Bar"
                    }
                }
            }
        }
    }

    parserTestContainer("Ambiguity errors") {
        val logger = enableProcessing()

        val acu = parser.parse("""
           package p;
           class Scratch {
               interface A { class Mem {} }
               interface B { class Mem {} }
               class Foo implements A, B {
                   Mem m; // Mem is ambiguous between A.Mem, B.Mem
               }

               Foo.Mem m; // Foo.Mem is ambiguous between A.Mem, B.Mem
           }
        """)

        val (refInFoo, refInScratch) = acu.descendants(ASTFieldDeclaration::class.java)
            .crossFindBoundaries().map { it.typeNode as ASTClassType }.toList()
        val (_, _, aMem) = acu.descendants(ASTClassDeclaration::class.java)
            .crossFindBoundaries().toList { it.symbol }


        doTest("Ambiguous inner type should produce an error (ref in Foo)") {
            val (_, args) = logger.warnings[AMBIGUOUS_NAME_REFERENCE]?.first { it.first == refInFoo }!!
            args.map { it.toString() } shouldBe listOf("Mem", "p.Scratch.A.Mem", "p.Scratch.B.Mem")
        }

        doTest("Ambiguous inner type should produce an error (ref in Scratch)") {
            val (_, args) = logger.warnings[AMBIGUOUS_NAME_REFERENCE]?.first { it.first == refInScratch }!!
            args.map { it.toString() } shouldBe listOf("Mem", "p.Scratch.A.Mem", "p.Scratch.B.Mem")
        }

        doTest("Ambiguous inner type should have a symbol anyway") {
            refInFoo.referencedSym shouldBe aMem
            refInScratch.referencedSym shouldBe aMem
        }
    }

    parserTest("Malformed types") {
        val logger = enableProcessing()

        val acu = parser.parse(
                """
           package p;
           class Scratch<X> {
               static class K {}
               static class Foo<Y, X> {}
               class Inner<Y> {} // non-static

               Scratch.Foo<K, K>        m0; // ok
               Scratch.K<K>             m1; // error
               Scratch.K                m2; // ok
               Scratch.Foo<K>           m3; // error
               Scratch.Foo<K, K, K>     m4; // error
               Scratch.Foo              m5; // raw type, ok

               Scratch<K>               s0; // ok
               Scratch<K, K>            s1; // error
               Scratch                  s2; // raw type, ok

               // Scratch<K>.Foo        m6; // todo error: Foo is static
               // Scratch<K>.Foo<K, K>  m7; // todo error: Foo is static

               // Scratch<K>.Inner<K, K>    m; // ok, fully parameterized
               // Scratch.Inner<K, K>       m; // todo error: Scratch must be parameterized 
               // Scratch.Inner             m; // ok, raw type
           }
        """
        )

        val (m0, m1, m2, m3, m4, m5, s0, s1, s2) =
            acu.descendants(ASTFieldDeclaration::class.java).map { it.typeNode as ASTClassType }.toList()

        fun assertErrored(t: ASTClassType, expected: Int, actual: Int) {
            val errs = logger.warnings[MALFORMED_GENERIC_TYPE]?.filter { it.first == t }
                ?: emptyList()
            assertEquals(errs.size, 1, "`${t.text}` should have produced a single error")
            errs.single().second.toList() shouldBe listOf(expected, actual)
        }

        fun assertNoError(t: ASTClassType) {
            val err = logger.warnings[MALFORMED_GENERIC_TYPE]?.firstOrNull { it.first == t }
            assertNull(err, "`${t.text}` should not have produced an error")
        }

        assertNoError(m0)
        assertErrored(m1, expected = 0, actual = 1)
        assertNoError(m2)
        assertErrored(m3, expected = 2, actual = 1)
        assertErrored(m4, expected = 2, actual = 3)
        assertNoError(m5)

        assertNoError(s0)
        assertErrored(s1, expected = 1, actual = 2)
        assertNoError(s2)
    }

    parserTest("Unresolved inner types") {
        enableProcessing()

        val acu = parser.parse(
                """
           package p;

           import k.OuterUnresolved;

           class Scratch<X> {
               OuterUnresolved.InnerUnresolved m0;
           }
        """
        )

        val (m0) =
            acu.descendants(ASTFieldDeclaration::class.java).map { it.typeNode as ASTClassType }.toList()

        val outerUnresolved = m0.qualifier!!
        val outerT = outerUnresolved.typeMirror.shouldBeA<JClassType> {
            it.symbol.shouldBeA<JClassSymbol> { classSymbol ->
                classSymbol::isUnresolved shouldBe true
                classSymbol::getSimpleName shouldBe "OuterUnresolved"
            }
        }

        val innerT = m0.typeMirror.shouldBeA<JClassType> {
            it::getEnclosingType shouldBe outerT
            it.symbol.shouldBeA<JClassSymbol> { classSymbol ->
                classSymbol::isUnresolved shouldBe true
                classSymbol::getSimpleName shouldBe "InnerUnresolved"
                classSymbol.enclosingClass.shouldBeSameInstanceAs(outerT.symbol)
            }
        }

        outerT.symbol.getDeclaredClass("InnerUnresolved").shouldBeSameInstanceAs(innerT.symbol)
    }

    parserTest("Invalid annotations") {
        val logger = enableProcessing()

        val acu = parser.parse(
                """
           package p;
           class C<T> {
                @interface A { }
                interface I { }

                // @T // this is invalid code, T is a type, not an annotation
                @C
                @I
                @Unresolved
                @A
                int field;
           }
        """
        )

        val (aC, aI, aUnresolved, aOk) =
            acu.descendants(ASTAnnotation::class.java).map { it.typeNode }.toList()

        fun assertErrored(t: ASTClassType) {
            val errs = logger.warnings[EXPECTED_ANNOTATION_TYPE]?.filter { it.first == t }
                ?: emptyList()
            assertEquals(errs.size, 1, "`${t.text}` should have produced a single error")
            errs.single().second.toList() shouldBe emptyList()
        }

        fun assertNoError(t: ASTClassType) {
            val err = logger.warnings[MALFORMED_GENERIC_TYPE]?.firstOrNull { it.first == t }
            assertNull(err, "`${t.text}` should not have produced an error")
        }

        assertNoError(aUnresolved)
        assertNoError(aOk)

        assertErrored(aC)
        assertErrored(aI)
    }

    parserTest("#4436 Import on demand of class defined in same compilation unit that has an extends clause") {
        // the extends clause must be disambiguated early to process the on demand import (ODI),
        // which is itself needed to resolve a supertype of Foo (which is itself part of the ODI)

        // this is a cyclic dependency, we should probably avoid collecting the static imports
        // before all classes of the CU have been visited
        assertNoSemanticErrorsOrWarnings()

        val acu = parser.parse("""
package p;
import static p.Assert2.*;

class Assert {
    static class Foo extends Bar { }
}
class Assert2 extends Assert {
    static class Bar {}
}
class Foo2 extends Foo { }

        """)

        val (_, foo, _, bar, foo2) = acu.descendants(ASTTypeDeclaration::class.java).toList { it.symbol }

        foo2.superclass shouldBe foo
        foo.superclass shouldBe bar
    }

    parserTest("#4436 Static import on demand of inherited member type of class defined in same compilation unit") {
        assertNoSemanticErrorsOrWarnings()

        val acu = parser.parse("""
package p;
import static p.Foo.*;

class Foo extends FooParent implements KeyProvider<Bar> { }
class FooParent {
    static class Bar { }
}
interface KeyProvider<K> { }
        """)

        val (foo, fooParent, bar, keyProvider) = acu.descendants(ASTTypeDeclaration::class.java).toList { it.symbol }

        foo.superclass shouldBe fooParent
        foo.getSuperInterfaceTypes(Substitution.EMPTY).single().shouldBe(
            acu.typeSystem.parameterise(keyProvider, listOf(acu.typeSystem.declaration(bar)))
        )
    }

    parserTest("#4436 Static import on demand of inherited member type of a class declared later in the file") {
        // The header of Later must be disambiguated before the one of First
        assertNoSemanticErrorsOrWarnings()

        val acu = parser.parse("""
package p;
import static p.Later.*;

class First extends Inner { }
class Later extends LaterParent { }
class LaterParent {
    static class Inner { }
}
        """)

        val (first, later, laterParent, inner) = acu.descendants(ASTTypeDeclaration::class.java).toList { it.symbol }

        first.superclass shouldBe inner
        later.superclass shouldBe laterParent
    }

    parserTest("#4436 Static import on demand of inherited member type of a nested class") {
        // The header of Nested must be disambiguated before the one of First,
        // but it is in the body of Outer
        assertNoSemanticErrorsOrWarnings()

        val acu = parser.parse("""
package p;
import static p.Outer.Nested.*;

class First extends Inner { }
class Outer {
    static class Nested extends NestedParent { }
}
class NestedParent {
    static class Inner { }
}
        """)

        val (first, _, nested, nestedParent, inner) = acu.descendants(ASTTypeDeclaration::class.java).toList { it.symbol }

        first.superclass shouldBe inner
        nested.superclass shouldBe nestedParent
    }

    parserTest("#4436 Static import on demand of a nested class whose supertype is inherited by its enclosing class") {
        // NestedParent is resolved in the body of Outer, which needs
        // the supertypes of Outer, before the header of Outer is processed.
        assertNoSemanticErrorsOrWarnings()

        val acu = parser.parse("""
package p;
import static p.Outer.Nested.*;

class First extends Inner { }
class Outer extends OuterBase {
    static class Nested extends NestedParent { }
}
class OuterBase {
    static class NestedParent {
        static class Inner { }
    }
}
        """)

        val (first, outer, nested, outerBase, nestedParent, inner) =
            acu.descendants(ASTTypeDeclaration::class.java).crossFindBoundaries().toList { it.symbol }

        first.superclass shouldBe inner
        outer.superclass shouldBe outerBase
        nested.superclass shouldBe nestedParent
    }

    parserTest("#4436 Single static import of inherited member type of class, used in the header of that class") {
        assertNoSemanticErrorsOrWarnings()

        val acu = parser.parse("""
package p;
import static p.Foo.Bar;

class Foo extends FooParent implements KeyProvider<Bar> { }
class FooParent {
    static class Bar { }
}
interface KeyProvider<K> { }
        """)

        val (foo, fooParent, bar, keyProvider) = acu.descendants(ASTTypeDeclaration::class.java).toList { it.symbol }

        foo.superclass shouldBe fooParent
        foo.getSuperInterfaceTypes(Substitution.EMPTY).single().shouldBe(
            acu.typeSystem.parameterise(keyProvider, listOf(acu.typeSystem.declaration(bar)))
        )
    }

    parserTest("#4436 Inherited member type qualified by the class being declared, in its own header") {
        assertNoSemanticErrorsOrWarnings()

        val acu = parser.parse("""
package p;
class Foo extends FooParent implements KeyProvider<Foo.Bar> {
    public Bar getKey() { return Bar.A; }
}
class FooParent {
    public enum Bar { A, B, C }
}
interface KeyProvider<K> { K getKey(); }
        """)

        val (foo, fooParent, bar, keyProvider) = acu.descendants(ASTTypeDeclaration::class.java).toList { it.symbol }

        foo.superclass shouldBe fooParent
        foo.getSuperInterfaceTypes(Substitution.EMPTY).single().shouldBe(
            acu.typeSystem.parameterise(keyProvider, listOf(acu.typeSystem.declaration(bar)))
        )

        val fooBar = acu.descendants(ASTClassType::class.java).first { it.simpleName == "Bar" }!!
        fooBar.shouldMatchN {
            classType("Bar") {
                classType("Foo") {
                    it::getReferencedSym shouldBe foo
                }
                it::getReferencedSym shouldBe bar
            }
        }
    }

    parserTestContainer("#4436 Cyclic inheritance through a member type should not crash") {
        // All of these are rejected by javac with "cyclic inheritance involving A".
        // We only check that disambiguation terminates and gives every type a symbol.
        enableProcessing()

        fun assertAllTypesHaveSymbol(code: String) {
            val acu = parser.parse(code)
            acu.descendants(ASTClassType::class.java).crossFindBoundaries().forEach {
                withClue(it.text) {
                    it.referencedSym shouldNotBe null
                    it.typeMirror shouldNotBe null
                }
            }
        }

        doTest("Member type declared in the class") {
            assertAllTypesHaveSymbol("""
                package p;
                class A extends A.B { static class B { } }
            """)
        }

        doTest("Member type that does not exist") {
            assertAllTypesHaveSymbol("""
                package p;
                class A extends A.B { }
            """)
        }

        doTest("Member type inherited from a superinterface") {
            assertAllTypesHaveSymbol("""
                package p;
                class A extends A.B implements I { }
                interface I { class B { } }
            """)
        }

        doTest("Mutually dependent headers") {
            assertAllTypesHaveSymbol("""
                package p;
                class A extends C.B { static class B { } }
                class C extends A.B { }
            """)
        }
    }

    parserTest("#4436 Inherited member types are not in scope in the header of the class without qualification") {
        // javac rejects this: the header scope does not include members of the class.
        // This should not crash, and Bar should stay unresolved.
        val logger = enableProcessing()

        val acu = parser.parse("""
package p;
class Foo extends FooParent implements KeyProvider<Bar> { }
class FooParent {
    static class Bar { }
}
interface KeyProvider<K> { }
        """)

        val (foo, fooParent) = acu.descendants(ASTTypeDeclaration::class.java).toList { it.symbol }

        foo.superclass shouldBe fooParent

        val barNode = acu.descendants(ASTClassType::class.java).first { it.simpleName == "Bar" }!!
        barNode.referencedSym.shouldBeA<JClassSymbol> {
            it::isUnresolved shouldBe true
        }
        logger.warnings[CANNOT_RESOLVE_SYMBOL]!!.map { it.first } shouldBe listOf(barNode)
    }
})
