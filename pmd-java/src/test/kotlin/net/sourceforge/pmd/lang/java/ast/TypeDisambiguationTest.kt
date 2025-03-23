/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol
import net.sourceforge.pmd.lang.java.symbols.table.internal.JavaSemanticErrors.*
import net.sourceforge.pmd.lang.java.types.JClassType
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

                @T
                @C
                @I
                @Unresolved
                @A
                int field;
           }
        """
        )

        val (aT, aC, aI, aUnresolved, aOk) =
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

        assertErrored(aT)
        assertErrored(aC)
        assertErrored(aI)
    }

    parserTest("!TODO Import on demand of class defined in same compilation unit that has an extends clause") {
        // the extends clause must be disambiguated early to process the on demand import (ODI),
        // which is itself needed to resolve a supertype of Foo (which is itself part of the ODI)

        // this is a cyclic dependency, we should probably avoid collecting the static imports
        // before all classes of the CU have been visited
        enableProcessing()

        @Suppress("UNUSED_VARIABLE") val acu = parser.parse("""
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
    }
})
