/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.Node
import net.sourceforge.pmd.lang.ast.test.*
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.modelica.ModelicaParsingHelper

class ModelicaCoordsTest : FunSpec({


    test("Test line/column numbers for implicit nodes") {

        """
package TestPackage
  package EmptyPackage
  end EmptyPackage;
end TestPackage;
      """.trim().parseModelica() should matchNode<ASTStoredDefinition> {

            it textEquals """package TestPackage
  package EmptyPackage
  end EmptyPackage;
end TestPackage;"""

            it.assertBounds(1, 1, 4, 17)

            child<ASTClassDefinition> {
                it textEquals """package TestPackage
  package EmptyPackage
  end EmptyPackage;
end TestPackage"""
                it.assertBounds(1, 1, 4, 16)

                child<ASTClassPrefixes> {
                    it textEquals "package"
                    it.assertBounds(1, 1, 1, 8)

                    child<ASTPackageClause> {
                        it textEquals "package"
                        it.assertBounds(1, 1, 1, 8)
                    }
                }
                child<ASTClassSpecifier> {
                    it textEquals """TestPackage
  package EmptyPackage
  end EmptyPackage;
end TestPackage"""
                    it.assertBounds(1, 9, 4, 16)

                    child<ASTSimpleLongClassSpecifier> {
                        it textEquals """TestPackage
  package EmptyPackage
  end EmptyPackage;
end TestPackage"""

                        it.assertBounds(1, 9, 4, 16)

                        child<ASTSimpleName> {
                            it textEquals "TestPackage"
                            it.assertBounds(1, 9, 1, 20)
                        }
                        child<ASTComposition> {
                            it textEquals """package EmptyPackage
  end EmptyPackage;"""
                            it.assertBounds(2, 3, 3, 20)

                            child<ASTElementList> {
                                it textEquals """package EmptyPackage
  end EmptyPackage;"""
                                it.assertBounds(2, 3, 3, 20)

                                child<ASTRegularElement> {
                                    it textEquals """package EmptyPackage
  end EmptyPackage"""
                                    it.assertBounds(2, 3, 3, 19)

                                    child<ASTClassDefinition> {
                                        it textEquals """package EmptyPackage
  end EmptyPackage"""
                                        it.assertBounds(2, 3, 3, 19)
                                        it.isPartial shouldBe false

                                        child<ASTClassPrefixes> {
                                            it textEquals "package"
                                            it.assertBounds(2, 3, 2, 10)

                                            child<ASTPackageClause> {
                                                it textEquals "package"
                                                it.assertBounds(2, 3, 2, 10)
                                            }
                                        }
                                        child<ASTClassSpecifier> {
                                            it textEquals """EmptyPackage
  end EmptyPackage"""
                                            it.assertBounds(2, 11, 3, 19)

                                            child<ASTSimpleLongClassSpecifier> {
                                                it textEquals """EmptyPackage
  end EmptyPackage"""
                                                it.assertBounds(2, 11, 3, 19)
                                                it.simpleClassName shouldBe "EmptyPackage"

                                                child<ASTSimpleName> {
                                                    it textEquals "EmptyPackage"
                                                    it.assertBounds(2, 11, 2, 23)

                                                }
                                                child<ASTComposition> {
                                                    it textEquals ""
                                                    it.firstToken::isImplicit shouldBe true
                                                    it.lastToken shouldBe it.firstToken

                                                    it.assertBounds(3, 3, 3, 3)

                                                    child<ASTElementList> {
                                                        it textEquals ""
                                                        it.firstToken::isImplicit shouldBe true
                                                        it.lastToken shouldBe it.firstToken

                                                        it.assertBounds(3, 3, 3, 3)
                                                    }
                                                }
                                                child<ASTSimpleName> {
                                                    it textEquals "EmptyPackage"
                                                    it::getImage shouldBe "EmptyPackage"
                                                    it.assertBounds(3, 7, 3, 19)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        child<ASTSimpleName> {
                            it textEquals "TestPackage"
                            it.assertBounds(4, 5, 4, 16)
                        }
                    }
                }
            }
        }
    }
})

fun String.parseModelica(): ASTStoredDefinition =
        ModelicaParsingHelper.DEFAULT.parse(this)
