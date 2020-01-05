/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast

import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import net.sourceforge.pmd.lang.LanguageRegistry
import net.sourceforge.pmd.lang.ast.Node
import net.sourceforge.pmd.lang.ast.test.matchNode
import net.sourceforge.pmd.lang.ast.test.shouldBe
import java.io.StringReader

class ModelicaCoordsTest : FunSpec({


    test("Test line/column numbers for implicit nodes") {

        """
package TestPackage
  package EmptyPackage
  end EmptyPackage;
end TestPackage;
      """.trim().parseModelica() should matchNode<ASTStoredDefinition> {

            it.assertBounds(1, 1, 4, 16)

            child<ASTClassDefinition> {
                it.assertBounds(1, 1, 4, 15)

                child<ASTClassPrefixes> {
                    it.assertBounds(1, 1, 1, 7)

                    child<ASTPackageClause> {
                        it.assertBounds(1, 1, 1, 7)
                    }
                }
                child<ASTClassSpecifier> {
                    it.assertBounds(1, 9, 4, 15)

                    child<ASTSimpleLongClassSpecifier> {
                        it.assertBounds(1, 9, 4, 15)

                        child<ASTSimpleName> {
                            it.assertBounds(1, 9, 1, 19)
                        }
                        child<ASTComposition> {
                            it.assertBounds(2, 3, 3, 19)

                            child<ASTElementList> {
                                it.assertBounds(2, 3, 3, 19)

                                child<ASTRegularElement> {
                                    it.assertBounds(2, 3, 3, 18)

                                    child<ASTClassDefinition> {
                                        it.assertBounds(2, 3, 3, 18)
                                        it.isPartial shouldBe false

                                        child<ASTClassPrefixes> {
                                            it.assertBounds(2, 3, 2, 9)

                                            child<ASTPackageClause> {
                                                it.assertBounds(2, 3, 2, 9)
                                            }
                                        }
                                        child<ASTClassSpecifier> {
                                            it.assertBounds(2, 11, 3, 18)

                                            child<ASTSimpleLongClassSpecifier> {
                                                it.assertBounds(2, 11, 3, 18)
                                                it.simpleClassName shouldBe "EmptyPackage"

                                                child<ASTSimpleName> {
                                                    it.assertBounds(2, 11, 2, 22)

                                                }
                                                child<ASTComposition> {

                                                    it.assertBounds(3, 3, 3, 2)

                                                    child<ASTElementList> {
                                                        /*
                                                            This ElementList is empty and has no explicit token.
                                                         */

                                                        it.assertBounds(3, 3, 3, 2)
                                                    }
                                                }
                                                child<ASTSimpleName> {
                                                    it.assertBounds(3, 7, 3, 18)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        child<ASTSimpleName> {
                            it.assertBounds(4, 5, 4, 15)
                        }
                    }
                }
            }
        }
    }
})

fun String.parseModelica(): ASTStoredDefinition {
    val ver = LanguageRegistry.getLanguage("Modelica").defaultVersion.languageVersionHandler
    val parser = ver.getParser(ver.defaultParserOptions)

    return parser.parse(":dummy:", StringReader(this)) as ASTStoredDefinition
}

fun Node.assertBounds(bline: Int, bcol: Int, eline: Int, ecol: Int) {
    this::getBeginLine shouldBe bline
    this::getBeginColumn shouldBe bcol
    this::getEndLine shouldBe eline
    this::getEndColumn shouldBe ecol
}
