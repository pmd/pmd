/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind.*

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTClassLiteralTest : ParserTestSpec({

    parserTest("Class literals") {

        inContext(ExpressionParsingCtx) {


            "void.class" should parseAs {
                classLiteral { voidType() }
            }


            "int.class" should parseAs {
                classLiteral {
                    primitiveType(INT)
                }
            }

            "Integer.class" should parseAs {
                classLiteral {
                    classType("Integer")
                }
            }


            "int[].class" should parseAs {
                classLiteral {
                    arrayType {
                        it::getElementType shouldBe primitiveType(INT)
                        it::getDimensions shouldBe dimList(1) {
                            arrayDim()
                        }
                    }
                }
            }


            "List<?>.class" shouldNot parse()
            "Map<String, String>.class" shouldNot parse()
            "java.util.List.class" should parse()
            "java.util.@F List.class" shouldNot parse()

        }


    }

})
