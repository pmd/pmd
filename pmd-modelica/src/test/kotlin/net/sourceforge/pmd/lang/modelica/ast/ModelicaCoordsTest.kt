/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.Node
import net.sourceforge.pmd.lang.ast.test.matchNode
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

            it::getText shouldBe """package TestPackage
  package EmptyPackage
  end EmptyPackage;
end TestPackage;"""

            it.assertBounds(1, 1, 4, 17)

            child<ASTClassDefinition> {
                it::getText shouldBe """package TestPackage
  package EmptyPackage
  end EmptyPackage;
end TestPackage"""
                it.assertBounds(1, 1, 4, 16)

                child<ASTClassPrefixes> {
                    it::getText shouldBe "package"
                    it.assertBounds(1, 1, 1, 8)

                    child<ASTPackageClause> {
                        it::getText shouldBe "package"
                        it.assertBounds(1, 1, 1, 8)
                    }
                }
                child<ASTClassSpecifier> {
                    it::getText shouldBe """TestPackage
  package EmptyPackage
  end EmptyPackage;
end TestPackage"""
                    it.assertBounds(1, 9, 4, 16)

                    child<ASTSimpleLongClassSpecifier> {
                        it::getText shouldBe """TestPackage
  package EmptyPackage
  end EmptyPackage;
end TestPackage"""

                        it.assertBounds(1, 9, 4, 16)

                        child<ASTSimpleName> {
                            it::getText shouldBe "TestPackage"
                            it.assertBounds(1, 9, 1, 20)
                        }
                        child<ASTComposition> {
                            it::getText shouldBe """package EmptyPackage
  end EmptyPackage;"""
                            it.assertBounds(2, 3, 3, 20)

                            child<ASTElementList> {
                                it::getText shouldBe """package EmptyPackage
  end EmptyPackage;"""
                                it.assertBounds(2, 3, 3, 20)

                                child<ASTRegularElement> {
                                    it::getText shouldBe """package EmptyPackage
  end EmptyPackage"""
                                    it.assertBounds(2, 3, 3, 19)

                                    child<ASTClassDefinition> {
                                        it::getText shouldBe """package EmptyPackage
  end EmptyPackage"""
                                        it.assertBounds(2, 3, 3, 19)
                                        it.isPartial shouldBe false

                                        child<ASTClassPrefixes> {
                                            it::getText shouldBe "package"
                                            it.assertBounds(2, 3, 2, 10)

                                            child<ASTPackageClause> {
                                                it::getText shouldBe "package"
                                                it.assertBounds(2, 3, 2, 10)
                                            }
                                        }
                                        child<ASTClassSpecifier> {
                                            it::getText shouldBe """EmptyPackage
  end EmptyPackage"""
                                            it.assertBounds(2, 11, 3, 19)

                                            child<ASTSimpleLongClassSpecifier> {
                                                it::getText shouldBe """EmptyPackage
  end EmptyPackage"""
                                                it.assertBounds(2, 11, 3, 19)
                                                it.simpleClassName shouldBe "EmptyPackage"

                                                child<ASTSimpleName> {
                                                    it::getText shouldBe "EmptyPackage"
                                                    it.assertBounds(2, 11, 2, 23)

                                                }
                                                child<ASTComposition> {
                                                    it::getText shouldBe ""
                                                    it.firstToken::isImplicit shouldBe true
                                                    it.lastToken shouldBe it.firstToken

                                                    it.assertBounds(3, 3, 3, 3)

                                                    child<ASTElementList> {
                                                        it::getText shouldBe ""
                                                        it.firstToken::isImplicit shouldBe true
                                                        it.lastToken shouldBe it.firstToken

                                                        it.assertBounds(3, 3, 3, 3)
                                                    }
                                                }
                                                child<ASTSimpleName> {
                                                    it::getText shouldBe "EmptyPackage"
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
                            it::getText shouldBe "TestPackage"
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

fun Node.assertBounds(bline: Int, bcol: Int, eline: Int, ecol: Int) {
    this::getBeginLine shouldBe bline
    this::getBeginColumn shouldBe bcol
    this::getEndLine shouldBe eline
    this::getEndColumn shouldBe ecol
}
