package net.sourceforge.pmd.lang.java.ast

import io.kotlintest.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.java.symbols.table.internal.SemanticChecksLogger

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class TypeDisambiguationTest : ParserTestSpec({


    parserTest("Inner class names") {
        enableProcessing()

        val acu = parser.parse("""
            class Foo {
               Foo.Inner f1;
               class Inner { }
            }
        """)

        val (f1) = acu.descendants(ASTFieldDeclaration::class.java).toList()

        f1.typeNode.shouldMatchNode<ASTClassOrInterfaceType> {
            it::getSimpleName shouldBe "Inner"
            it::getImage shouldBe "Inner"
            it::getAmbiguousLhs shouldBe null
            it::getLhsType shouldBe classType("Foo")
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

        val (node, args) = logger.warnings[SemanticChecksLogger.CANNOT_RESOLVE_MEMBER]!![0]
        args.toList() shouldBe listOf("Bar", "com.Foo", "an unresolved type")
        node.shouldBeA<ASTClassOrInterfaceType> {  }
    }


    parserTest("Fully qualified names") {
        enableProcessing(true)

        inContext(TypeParsingCtx) {
            "javasymbols.testdata.Statics" should parseAs {
                classType("Statics") {
                    it::getImage shouldBe "javasymbols.testdata.Statics"
                    it::getLhsType shouldBe null
                    it::getAmbiguousLhs shouldBe null
                }
            }

            "javasymbols.testdata.Statics.PublicStatic" should parseAs {
                classType("PublicStatic") {

                    it::getLhsType shouldBe classType("Statics") {
                        it::getImage shouldBe "javasymbols.testdata.Statics"
                        it::getLhsType shouldBe null
                        it::getAmbiguousLhs shouldBe null
                    }
                }
            }
        }
    }
})
