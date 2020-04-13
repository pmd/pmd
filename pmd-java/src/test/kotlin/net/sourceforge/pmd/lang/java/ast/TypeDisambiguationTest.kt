package net.sourceforge.pmd.lang.java.ast

import io.kotlintest.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.ast.test.shouldMatchN
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol
import net.sourceforge.pmd.lang.java.symbols.table.internal.SemanticChecksLogger

class TypeDisambiguationTest : ParserTestSpec({


    parserTest("Inner class names") {
        enableProcessing()

        val acu = parser.parse("""
            class Foo {
               Foo.Inner f1;
               class Inner { }
            }
        """)

        val (foo, inner) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.symbol }
        val (f1) = acu.descendants(ASTFieldDeclaration::class.java).toList()

        f1.typeNode.shouldMatchNode<ASTClassOrInterfaceType> {
            it::getSimpleName shouldBe "Inner"
            it::getImage shouldBe "Inner"
            it::getReferencedSym shouldBe inner
            it::getAmbiguousLhs shouldBe null
            it::getQualifier shouldBe classType("Foo") {
                it::getReferencedSym shouldBe foo
            }
        }
    }


    parserTest("Fully qualified names") {
        enableProcessing(true)

        inContext(TypeParsingCtx) {
            "javasymbols.testdata.Statics" should parseAs {
                classType("Statics") {
                    it::getImage shouldBe "javasymbols.testdata.Statics"
                    it::getQualifier shouldBe null
                    it::getAmbiguousLhs shouldBe null
                }
            }

            "javasymbols.testdata.Statics.PublicStatic" should parseAs {
                classType("PublicStatic") {

                    it::getQualifier shouldBe classType("Statics") {
                        it::getImage shouldBe "javasymbols.testdata.Statics"
                        it::getQualifier shouldBe null
                        it::getAmbiguousLhs shouldBe null
                    }
                }
            }
        }
    }


    parserTest("Failures") {
        val logger = enableProcessing()

        val acu = parser.parse("""
            package com;
            class Foo {
               Foo.Bar f1;
               class Inner { }
            }
        """)

        val (foo) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList()
        val (fooBar) = acu.descendants(ASTClassOrInterfaceType::class.java).toList()


        doTest("Unresolved inner type should produce a warning") {
            val (node, args) = logger.warnings[SemanticChecksLogger.CANNOT_RESOLVE_MEMBER]!![0]
            args.map { it.toString() } shouldBe listOf("Bar", "com.Foo", "an unresolved type")
            node.shouldBeA<ASTClassOrInterfaceType> { }
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
})
