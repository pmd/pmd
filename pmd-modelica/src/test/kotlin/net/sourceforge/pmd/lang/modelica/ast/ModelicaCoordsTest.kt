/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.matchNode
import net.sourceforge.pmd.lang.ast.test.assertPosition
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldHaveText
import net.sourceforge.pmd.lang.modelica.ModelicaParsingHelper

class ModelicaCoordsTest : FunSpec({


    test("Test line/column numbers for implicit nodes") {

        """
package TestPackage
  package EmptyPackage
  end EmptyPackage;
end TestPackage;
      """.trim().parseModelica() should matchNode<ASTStoredDefinition> {

            it shouldHaveText """package TestPackage
  package EmptyPackage
  end EmptyPackage;
end TestPackage;"""

            it.assertPosition(1, 1, 4, 17)

            child<ASTClassDefinition> {
                it shouldHaveText """package TestPackage
  package EmptyPackage
  end EmptyPackage;
end TestPackage"""
                it.assertPosition(1, 1, 4, 16)

                child<ASTClassPrefixes> {
                    it shouldHaveText "package"
                    it.assertPosition(1, 1, 1, 8)

                    child<ASTPackageClause> {
                        it shouldHaveText "package"
                        it.assertPosition(1, 1, 1, 8)
                    }
                }
                child<ASTClassSpecifier> {
                    it shouldHaveText """TestPackage
  package EmptyPackage
  end EmptyPackage;
end TestPackage"""
                    it.assertPosition(1, 9, 4, 16)

                    child<ASTSimpleLongClassSpecifier> {
                        it shouldHaveText """TestPackage
  package EmptyPackage
  end EmptyPackage;
end TestPackage"""

                        it.assertPosition(1, 9, 4, 16)

                        child<ASTSimpleName> {
                            it shouldHaveText "TestPackage"
                            it.assertPosition(1, 9, 1, 20)
                        }
                        child<ASTComposition> {
                            it shouldHaveText """package EmptyPackage
  end EmptyPackage;"""
                            it.assertPosition(2, 3, 3, 20)

                            child<ASTElementList> {
                                it shouldHaveText """package EmptyPackage
  end EmptyPackage;"""
                                it.assertPosition(2, 3, 3, 20)

                                child<ASTRegularElement> {
                                    it shouldHaveText """package EmptyPackage
  end EmptyPackage"""
                                    it.assertPosition(2, 3, 3, 19)

                                    child<ASTClassDefinition> {
                                        it shouldHaveText """package EmptyPackage
  end EmptyPackage"""
                                        it.assertPosition(2, 3, 3, 19)
                                        it.isPartial shouldBe false

                                        child<ASTClassPrefixes> {
                                            it shouldHaveText "package"
                                            it.assertPosition(2, 3, 2, 10)

                                            child<ASTPackageClause> {
                                                it shouldHaveText "package"
                                                it.assertPosition(2, 3, 2, 10)
                                            }
                                        }
                                        child<ASTClassSpecifier> {
                                            it shouldHaveText """EmptyPackage
  end EmptyPackage"""
                                            it.assertPosition(2, 11, 3, 19)

                                            child<ASTSimpleLongClassSpecifier> {
                                                it shouldHaveText """EmptyPackage
  end EmptyPackage"""
                                                it.assertPosition(2, 11, 3, 19)
                                                it.simpleClassName shouldBe "EmptyPackage"

                                                child<ASTSimpleName> {
                                                    it shouldHaveText "EmptyPackage"
                                                    it.assertPosition(2, 11, 2, 23)

                                                }
                                                child<ASTComposition> {
                                                    it shouldHaveText ""
                                                    it.firstToken::isImplicit shouldBe true
                                                    it.lastToken shouldBe it.firstToken

                                                    it.assertPosition(3, 3, 3, 3)

                                                    child<ASTElementList> {
                                                        it shouldHaveText ""
                                                        it.firstToken::isImplicit shouldBe true
                                                        it.lastToken shouldBe it.firstToken

                                                        it.assertPosition(3, 3, 3, 3)
                                                    }
                                                }
                                                child<ASTSimpleName> {
                                                    it shouldHaveText "EmptyPackage"
                                                    it::getImage shouldBe "EmptyPackage"
                                                    it.assertPosition(3, 7, 3, 19)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        child<ASTSimpleName> {
                            it shouldHaveText "TestPackage"
                            it.assertPosition(4, 5, 4, 16)
                        }
                    }
                }
            }
        }
    }
})

fun String.parseModelica(): ASTStoredDefinition =
        ModelicaParsingHelper.DEFAULT.parse(this)
