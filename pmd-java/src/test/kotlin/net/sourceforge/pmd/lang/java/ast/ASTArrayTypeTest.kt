/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe


/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTArrayTypeTest : ParserTestSpec({

    parserTest("Multi-Dim Array") {
        inContext(TypeParsingCtx) {

            "ArrayTypes[][][]" should parseAs {

                arrayType {

                    it::getElementType shouldBe classType("ArrayTypes")

                    it::getDimensions shouldBe dimList {
                        arrayDim { }
                        arrayDim { }
                        arrayDim { }
                    }
                }
            }
        }
    }

    parserTest("Annotated array type") {
        inContext(TypeParsingCtx) {

            "ArrayTypes[][] @A []" should parseAs {

                arrayType {
                    it::getElementType shouldBe classType("ArrayTypes")

                    it::declaredAnnotationsList shouldBe emptyList()
                    arrayDimList {
                        arrayDim { }
                        arrayDim { }
                        arrayDim {
                            val lst = listOf(annotation("A"))
                            it::declaredAnnotationsList shouldBe lst
                        }
                    }
                }
            }
        }
    }

    parserTest("Multi-Dim Array allocation") {
        inContext(ExpressionParsingCtx) {
            "new ArrayTypes[][][] { }" should parseAs {

                arrayAlloc {

                    arrayType({
                        classType("ArrayTypes")
                    }) {
                        arrayDim {  }
                        arrayDim {  }
                        arrayDim {  }
                    }

                    arrayInitializer { }
                }
            }
        }
    }
})
