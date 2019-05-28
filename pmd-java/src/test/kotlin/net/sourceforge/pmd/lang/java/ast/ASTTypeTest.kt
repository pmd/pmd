package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.ParserTestCtx.Companion.TypeParsingCtx

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTTypeTest : ParserTestSpec({

    parserTest("Test ambiguous qualified names") {

        "java.util.List" should matchType<ASTClassOrInterfaceType> {
            it::getTypeImage shouldBe "java.util.List"
            it::getImage shouldBe "List"
            it::getTypeArguments shouldBe null
            it::getLhsType shouldBe null

            it::getAmbiguousLhs shouldBe child {
                it::getName shouldBe "java.util"
            }
        }

        "java.util.List<F>" should matchType<ASTClassOrInterfaceType> {

            it::getLhsType shouldBe null
            it::getImage shouldBe "List"

            it::getAmbiguousLhs shouldBe child {
                it::getName shouldBe "java.util"
            }

            it::getTypeArguments shouldBe child {
                child<ASTClassOrInterfaceType> {
                    it::getTypeImage shouldBe "F"
                }
            }
        }
    }

    parserTest("Test simple names") {

        "foo" should matchType<ASTClassOrInterfaceType> {
            it::getTypeImage shouldBe "foo"
            it::getImage shouldBe "foo"

            it::getAmbiguousLhs shouldBe null
            it::getLhsType shouldBe null
        }

    }

    parserTest("Test non-ambiguous segments") {

        // Perhaps surprisingly a type annotation binds to the closest segment
        // So @B binds to "java"
        // If the annotation is not applicable to TYPE_USE then it doesn't compile

        // this happens in type context, eg in a cast, or in an extends list

        // TYPE_USE annotations are prohibited eg before a declaration

        inContext(TypeParsingCtx) {


            "@B java.util.@C Map" should parseAs {

                classType("Map") {
                    it::getLhsType shouldBe classType("util") {
                        it::getLhsType shouldBe classType("java") {
                            annotationList {
                                annotation("B")
                            }
                        }
                    }

                    annotationList {
                        annotation("C")
                    }

                    it::getTypeImage shouldBe "java.util.Map"
                }
            }


            "java.util.Map.@Foo Entry<K, V>" should matchType<ASTClassOrInterfaceType> {
                it::getTypeImage shouldBe "java.util.Map.Entry"
                it::getImage shouldBe "Entry"

                it::getLhsType shouldBe null

                it::getAmbiguousLhs shouldBe child {
                    it::getTypeImage shouldBe "java.util.Map"
                    it::getImage shouldBe "java.util.Map"
                    it::getName shouldBe "java.util.Map"
                }

                annotationList {
                    annotation("Foo")
                }

                it::getTypeArguments shouldBe child {

                    child<ASTClassOrInterfaceType> {
                        it::getTypeImage shouldBe "K"
                        it::getTypeArguments shouldBe null
                        it::getLhsType shouldBe null
                    }

                    child<ASTClassOrInterfaceType> {
                        it::getTypeImage shouldBe "V"
                        it::getTypeArguments shouldBe null
                        it::getLhsType shouldBe null
                    }
                }
            }

            "Foo<K>.@A Bar.Brew<V>" should matchType<ASTClassOrInterfaceType> {

                it::getTypeImage shouldBe "Foo.Bar.Brew"

                it::getLhsType shouldBe classType("Bar") {
                    it::getTypeImage shouldBe "Foo.Bar"
                    it::getTypeArguments shouldBe null

                    it::getLhsType shouldBe classType("Foo") {
                        it::getTypeImage shouldBe "Foo"

                        it::getTypeArguments shouldBe typeArgList {
                            classType("K")
                        }
                    }


                    annotationList {
                        annotation("A")
                    }
                }

                it::getTypeArguments shouldBe child {
                    child<ASTClassOrInterfaceType> {
                        it::getTypeImage shouldBe "V"
                    }
                }
            }
        }

        parserTest("Test array types") {

            "ArrayTypes[][][]" should matchType<ASTArrayType> {

                it::getElementType shouldBe child<ASTClassOrInterfaceType> {
                    it::getTypeImage shouldBe "ArrayTypes"
                    it::getImage shouldBe "ArrayTypes"
                }

                it::getDimensions shouldBe child {

                    child<ASTArrayTypeDim> {}
                    child<ASTArrayTypeDim> {}
                    child<ASTArrayTypeDim> {}
                }
            }

        }
    }
})
