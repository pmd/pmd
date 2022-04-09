/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind.*

class ASTFieldDeclarationTest : ParserTestSpec({

    parserTest("Extra dimensions") {

        inContext(TypeBodyParsingCtx) {

            // int x[][] = null;
            // int[] x[][] = null;

            "int x @A@B[];" should parseAs {
                fieldDecl {

                    it::getModifiers shouldBe modifiers { }

                    it::isPublic shouldBe false
                    it::isSyntacticallyPublic shouldBe false
                    it::isPackagePrivate shouldBe true

                    primitiveType(INT)

                    varDeclarator {
                        variableId("x") {

                            it::isField shouldBe true

                            it::getExtraDimensions shouldBe child {
                                arrayDim {
                                    annotation("A")
                                    annotation("B")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    parserTest("In annotation") {

        genClassHeader = "@interface A"

        inContext(TypeBodyParsingCtx) {

            // int x[][] = null;
            // int[] x[][] = null;

            "@A int x[] = { 2 };" should parseAs {
                fieldDecl {
                    it.preferredReportLocation!!.image shouldBe "x" // the ident

                    it::getModifiers shouldBe modifiers {
                        it::getExplicitModifiers shouldBe emptySet()
                        it::getEffectiveModifiers shouldBe setOf(JModifier.PUBLIC, JModifier.STATIC, JModifier.FINAL)

                        annotation("A")
                    }

                    primitiveType(INT)

                    varDeclarator {
                        variableId("x") {

                            it::isField shouldBe true

                            it::getExtraDimensions shouldBe child {
                                arrayDim { }
                            }
                        }

                        it::getInitializer shouldBe arrayInitializer {
                            int(2)
                        }
                    }
                }
            }
        }
    }

})
