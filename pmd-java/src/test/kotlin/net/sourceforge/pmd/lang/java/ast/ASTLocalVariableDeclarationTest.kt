/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind.INT

class ASTLocalVariableDeclarationTest : ParserTestSpec({

    parserTest("Extra dimensions") {

        inContext(StatementParsingCtx) {

            // int x[][] = null;
            // int[] x[][] = null;

            "int x@A[];" should parseAs {
                localVarDecl {

                    it::getModifiers shouldBe modifiers { }

                    primitiveType(INT)

                    varDeclarator {
                        variableId("x") {

                            it::isField shouldBe false

                            it::getExtraDimensions shouldBe child {
                                arrayDim {
                                    annotation("A")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

})
