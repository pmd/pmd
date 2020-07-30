/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Earliest
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Latest
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J1_3
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J1_5

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

    parserTest("Marker annotations", javaVersions = J1_5..Latest) {

        inContext(AnnotationParsingCtx) {

            "@F" should parseAs {
                child<ASTAnnotation> {
                    it::getSimpleName shouldBe "F"

                    it::getTypeNode shouldBe classType("F")

                    it::getMemberList shouldBe null

                    it::getAnnotationName shouldBe "F"
                }
            }

            "@java.lang.Override" should parseAs {
                child<ASTAnnotation> {
                    it::getSimpleName shouldBe "Override"

                    it::getTypeNode shouldBe qualClassType("java.lang.Override")

                    it::getMemberList shouldBe null

                    it::getAnnotationName shouldBe "java.lang.Override"
                }
            }

            "@Override" should parseAs {
                child<ASTAnnotation> {
                    it::getSimpleName shouldBe "Override"

                    it::getTypeNode shouldBe qualClassType("Override")

                    it::getMemberList shouldBe null

                    it::getAnnotationName shouldBe "Override"
                }
            }
        }

    }

    parserTest("Single-value shorthand", javaVersions = J1_5..Latest) {

        inContext(AnnotationParsingCtx) {

            "@F(\"ohio\")" should parseAs {
                child<ASTAnnotation> {
                    it::getSimpleName shouldBe "F"

                    it::getTypeNode shouldBe classType("F")

                    it::getMemberList shouldBe child {
                        shorthandMemberValue {
                            stringLit("\"ohio\"")
                        }
                    }
                }
            }

            "@org.F({java.lang.Math.PI})" should parseAs {
                child<ASTAnnotation> {
                    it::getSimpleName shouldBe "F"

                    it::getTypeNode shouldBe qualClassType("org.F")

                    it::getMemberList shouldBe child {
                        shorthandMemberValue {
                            child<ASTMemberValueArrayInitializer> {
                                child<ASTFieldAccess> {
                                    it::getFieldName shouldBe "PI"
                                    ambiguousName("java.lang.Math")
                                }
                            }
                        }
                    }
                }
            }

            "@org.F({@Aha, @Oh})" should parseAs {
                child<ASTAnnotation> {
                    it::getSimpleName shouldBe "F"

                    it::getTypeNode shouldBe qualClassType("org.F")

                    it::getMemberList shouldBe child {
                        shorthandMemberValue {
                            child<ASTMemberValueArrayInitializer> {
                                annotation("Aha")
                                annotation("Oh")
                            }
                        }
                    }
                }
            }
            "@org.F(@Oh)" should parseAs {
                child<ASTAnnotation> {
                    it::getSimpleName shouldBe "F"

                    it::getTypeNode shouldBe qualClassType("org.F")

                    it::getMemberList shouldBe child {
                        shorthandMemberValue {
                            annotation("Oh")
                        }
                    }
                }
            }
        }

    }

    parserTest("Normal annotation", javaVersions = J1_5..Latest) {

        inContext(AnnotationParsingCtx) {

            "@F(a=\"ohio\")" should parseAs {
                child<ASTAnnotation> {
                    it::getSimpleName shouldBe "F"

                    it::getTypeNode shouldBe classType("F")

                    it::getMemberList shouldBe child {
                        memberValuePair("a") {
                            stringLit("\"ohio\"")
                        }
                    }
                }
            }

            "@org.F(a={java.lang.Math.PI}, b=2)" should parseAs {
                child<ASTAnnotation> {
                    it::getSimpleName shouldBe "F"

                    it::getTypeNode shouldBe qualClassType("org.F")

                    it::getMemberList shouldBe child {
                        memberValuePair("a") {
                            child<ASTMemberValueArrayInitializer> {
                                fieldAccess("PI") {
                                    ambiguousName("java.lang.Math")
                                }
                            }
                        }

                        memberValuePair("b") {
                            number()
                        }
                    }
                }
            }


            """
    @TestAnnotation({@SuppressWarnings({}),
                     @SuppressWarnings(value = {"Beware the ides of March.",}),
                     @SuppressWarnings({"Look both ways", "Before Crossing",}), })
            """ should parseAs {

                child<ASTAnnotation> {

                    it::getTypeNode shouldBe classType("TestAnnotation")

                    it::getMemberList shouldBe child {

                        shorthandMemberValue {

                            child<ASTMemberValueArrayInitializer> {
                                annotation {

                                    it::getTypeNode shouldBe classType("SuppressWarnings")

                                    it::getMemberList shouldBe child {
                                        shorthandMemberValue {
                                            child<ASTMemberValueArrayInitializer> {}
                                        }
                                    }
                                }
                                annotation {

                                    it::getTypeNode shouldBe classType("SuppressWarnings")

                                    it::getMemberList shouldBe child {
                                        memberValuePair("value") {
                                            it::isShorthand shouldBe false
                                            child<ASTMemberValueArrayInitializer> {
                                                stringLit("\"Beware the ides of March.\"")
                                            }
                                        }
                                    }
                                }
                                annotation {

                                    it::getTypeNode shouldBe classType("SuppressWarnings")

                                    it::getMemberList shouldBe child {
                                        shorthandMemberValue {
                                            child<ASTMemberValueArrayInitializer> {
                                                stringLit("\"Look both ways\"")
                                                stringLit("\"Before Crossing\"")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
})
