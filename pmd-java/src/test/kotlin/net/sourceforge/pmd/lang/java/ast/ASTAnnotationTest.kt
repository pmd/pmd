/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Earliest
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J1_3

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTAnnotationTest : ParserTestSpec({


    parserTest("Test annot fails before JDK 1.4", javaVersions = Earliest..J1_3) {

        inContext(AnnotationParsingCtx) {
            "@F" shouldNot parse()
            "@F(a=1)" shouldNot parse()
        }
    }

    parserTest("Marker annotations") {

        inContext(AnnotationParsingCtx) {

            "@F" should parseAs {
                child<ASTMarkerAnnotation> {
                    it::getAnnotationName shouldBe "F"
                    it::getSimpleName shouldBe "F"
                }
            }

            "@java.lang.Override" should parseAs {
                child<ASTMarkerAnnotation> {
                    it::getAnnotationName shouldBe "java.lang.Override"
                    it::getSimpleName shouldBe "Override"
                }
            }
        }

    }

    parserTest("Single-value shorthand") {

        inContext(AnnotationParsingCtx) {

            "@F(\"ohio\")" should parseAs {
                child<ASTSingleMemberAnnotation> {
                    it::getAnnotationName shouldBe "F"
                    it::getSimpleName shouldBe "F"

                    it::getMemberValue shouldBe stringLit("\"ohio\"")
                }
            }

            "@org.F({java.lang.Math.PI})" should parseAs {
                child<ASTSingleMemberAnnotation> {
                    it::getAnnotationName shouldBe "org.F"
                    it::getSimpleName shouldBe "F"

                    it::getMemberValue shouldBe child<ASTMemberValueArrayInitializer> {
                        child<ASTFieldAccess> {
                            it::getFieldName shouldBe "PI"
                            ambiguousName("java.lang.Math")
                        }
                    }
                }
            }

            "@org.F({@Aha, @Oh})" should parseAs {
                child<ASTSingleMemberAnnotation> {
                    it::getAnnotationName shouldBe "org.F"
                    it::getSimpleName shouldBe "F"

                    it::getMemberValue shouldBe child<ASTMemberValueArrayInitializer> {
                        annotation("Aha")
                        annotation("Oh")
                    }
                }
            }
            "@org.F(@Oh)" should parseAs {
                child<ASTSingleMemberAnnotation> {
                    it::getAnnotationName shouldBe "org.F"
                    it::getSimpleName shouldBe "F"

                    it::getMemberValue shouldBe annotation("Oh")
                }
            }
        }

    }

    parserTest("Normal annotation") {

        inContext(AnnotationParsingCtx) {

            "@F(a=\"ohio\")" should parseAs {
                child<ASTNormalAnnotation> {
                    it::getAnnotationName shouldBe "F"
                    it::getSimpleName shouldBe "F"

                    memberValuePair("a") {
                        stringLit("\"ohio\"")
                    }
                }
            }

            "@org.F(a={java.lang.Math.PI}, b=2)" should parseAs {
                child<ASTNormalAnnotation> {
                    it::getAnnotationName shouldBe "org.F"
                    it::getSimpleName shouldBe "F"

                    memberValuePair("a") {
                        child<ASTMemberValueArrayInitializer> {
                            child<ASTFieldAccess> {
                                it::getFieldName shouldBe "PI"
                                ambiguousName("java.lang.Math")
                            }
                        }
                    }

                    memberValuePair("b") {
                        number()
                    }
                }
            }

            "@org.F({@Aha, @Oh})" should parseAs {
                child<ASTSingleMemberAnnotation> {
                    it::getAnnotationName shouldBe "org.F"
                    it::getSimpleName shouldBe "F"

                    it::getMemberValue shouldBe child<ASTMemberValueArrayInitializer> {
                        annotation("Aha")
                        annotation("Oh")
                    }
                }
            }


            """
    @TestAnnotation({@SuppressWarnings({}),
                     @SuppressWarnings({"Beware the ides of March.",}),
                     @SuppressWarnings({"Look both ways", "Before Crossing",}), })
            """ should parseAs {

                child<ASTSingleMemberAnnotation> {

                    it::getMemberValue shouldBe child<ASTMemberValueArrayInitializer> {
                        child<ASTSingleMemberAnnotation> {

                            it::getMemberValue shouldBe child<ASTMemberValueArrayInitializer> {}
                        }
                        child<ASTSingleMemberAnnotation> {

                            it::getMemberValue shouldBe child<ASTMemberValueArrayInitializer> {
                                stringLit("\"Beware the ides of March.\"")
                            }
                        }
                        child<ASTSingleMemberAnnotation> {

                            it::getMemberValue shouldBe child<ASTMemberValueArrayInitializer> {
                                stringLit("\"Look both ways\"")
                                stringLit("\"Before Crossing\"")
                            }
                        }
                    }
                }
            }
        }
    }
})
