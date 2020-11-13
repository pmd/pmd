/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.AccessNode.Visibility.*
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind.INT

class ModifiersTest : ParserTestSpec({

    parserTest("Local classes") {

        inContext(StatementParsingCtx) {

            """
               @F class Local {
                    private int i;
               }
            """ should parseAs {
                localClassDecl(simpleName = "Local") {

                    it::getVisibility shouldBe V_LOCAL
                    it::getEffectiveVisibility shouldBe V_LOCAL

                    it::isAbstract shouldBe false
                    it::isFinal shouldBe false
                    it::isLocal shouldBe true
                    it::isNested shouldBe false

                    it::getModifiers shouldBe modifiers {
                        annotation("F")
                    }

                    typeBody {
                        fieldDecl {
                            modifiers()
                            primitiveType(INT)
                            varDeclarator {
                                variableId("i") {
                                    it::getVisibility shouldBe V_PRIVATE
                                    it::getEffectiveVisibility shouldBe V_LOCAL
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    parserTest("Anon classes") {

        inContext(StatementParsingCtx) {

            """
               new Runnable() {
                    private int i;
                    public void run() { int l; }
               };
            """ should parseAs {
                exprStatement {
                    val (i, l) = it.descendants(ASTVariableDeclaratorId::class.java)
                            .crossFindBoundaries()
                            .toList()

                    i.let {
                        it::getVisibility shouldBe V_PRIVATE
                        it::getEffectiveVisibility shouldBe V_ANONYMOUS
                    }

                    l.let {
                        it::getVisibility shouldBe V_LOCAL
                        it::getEffectiveVisibility shouldBe V_LOCAL
                    }

                    val runMethod = it.descendants(ASTMethodDeclaration::class.java).crossFindBoundaries().firstOrThrow()

                    runMethod.let {
                        it::getVisibility shouldBe V_PUBLIC
                        it::getEffectiveVisibility shouldBe V_ANONYMOUS
                    }

                    constructorCall()
                }
            }
        }
    }


})
