/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast

import io.kotlintest.should
import io.kotlintest.specs.FunSpec
import net.sourceforge.pmd.lang.LanguageRegistry
import net.sourceforge.pmd.lang.ast.Node
import net.sourceforge.pmd.lang.ast.test.matchNode
import net.sourceforge.pmd.lang.ast.test.shouldBe
import java.io.StringReader

class ScalaTreeTests : FunSpec({


    test("Test line/column numbers") {

        """
class Foo {
 val I = "" 
}  
      """.trim().parseScala() should matchNode<ASTSource> {

            child<ASTDefnClass> {
                it.assertBounds(bline = 1, bcol = 1, eline = 3, ecol = 1)
                it::isImplicit shouldBe false

                child<ASTTypeName> {
                    it.assertBounds(bline = 1, bcol = 7, eline = 1, ecol = 9)
                    it::isImplicit shouldBe false
                }

                child<ASTCtorPrimary> {
                    it.assertBounds(bline = 1, bcol = 11, eline = 1, ecol = 10) // node has zero length
                    it::isImplicit shouldBe true

                    child<ASTNameAnonymous> {
                        it.assertBounds(bline = 1, bcol = 11, eline = 1, ecol = 10) // node has zero length
                        it::isImplicit shouldBe true
                    }
                }

                child<ASTTemplate> {
                    it.assertBounds(bline = 1, bcol = 11, eline = 3, ecol = 1)
                    it::isImplicit shouldBe false

                    child<ASTSelf> {
                        it.assertBounds(bline = 2, bcol = 2, eline = 2, ecol = 1) // node has zero length
                        it::isImplicit shouldBe true

                        child<ASTNameAnonymous> {
                            it.assertBounds(bline = 2, bcol = 2, eline = 2, ecol = 1) // node has zero length
                            it::isImplicit shouldBe true
                        }
                    }

                    child<ASTDefnVal> {
                        it.assertBounds(bline = 2, bcol = 2, eline = 2, ecol = 11)
                        it::isImplicit shouldBe false

                        child<ASTPatVar> {
                            it.assertBounds(bline = 2, bcol = 6, eline = 2, ecol = 6)
                            it::isImplicit shouldBe false

                            child<ASTTermName> {
                                it.assertBounds(bline = 2, bcol = 6, eline = 2, ecol = 6)
                                it::isImplicit shouldBe false
                            }
                        }

                        child<ASTLitString> {
                            it.assertBounds(bline = 2, bcol = 10, eline = 2, ecol = 11)
                        }
                    }
                }
            }
        }
    }
})

fun String.parseScala(): ASTSource {
    val ver = LanguageRegistry.getLanguage("Scala").defaultVersion.languageVersionHandler
    val parser = ver.getParser(ver.defaultParserOptions)

    return parser.parse(":dummy:", StringReader(this)) as ASTSource
}

fun Node.assertBounds(bline: Int, bcol: Int, eline: Int, ecol: Int) {
    this::getBeginLine shouldBe bline
    this::getBeginColumn shouldBe bcol
    this::getEndLine shouldBe eline
    this::getEndColumn shouldBe ecol
}
