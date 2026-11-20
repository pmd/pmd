/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Latest
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J1_8
import net.sourceforge.pmd.lang.test.ast.shouldBe

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTTypeParametersTest : ParserTestSpec({
    parserTestContainer("Test simple parameters", javaVersions = J1_8..Latest) {
        inContext(TypeParametersParsingCtx) {
            "<T>" should parseAs {
                typeParamList {
                    typeParam("T")
                }
            }

            "<T, S>" should parseAs {
                typeParamList {
                    typeParam("T")
                    typeParam("S")
                }
            }

            "<@F T, S>" should parseAs {
                typeParamList {
                    typeParam("T") {
                        annotation("F")

                        null
                    }
                    typeParam("S")
                }
            }

            "<@F T extends @N Runnable>" should parseAs {
                typeParamList {
                    typeParam("T") {
                        annotation("F")

                        classType("Runnable") {
                            annotation("N")
                        }
                    }
                }
            }

            // TODO Deprecated: Tests deprecated @Image attribute of TypeParameter
            "<T>" should parseAs {
                typeParamList {
                    child<ASTTypeParameter> {
                        it::getName shouldBe "T"
                        it::getImage shouldBe it.name
                    }
                }
            }
        }
    }
})
