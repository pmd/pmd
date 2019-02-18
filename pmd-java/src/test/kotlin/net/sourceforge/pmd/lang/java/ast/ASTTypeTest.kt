package net.sourceforge.pmd.lang.java.ast

import io.kotlintest.shouldBe

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTTypeTest : ParserTestSpec({

    parserTest("Test non-recursive ClassOrInterfaceTypes") {

        "java.util.List" should matchType<ASTClassOrInterfaceType> {
            it.typeImage shouldBe "java.util.List"
            it.typeArguments.shouldBeEmpty()
            it.leftHandSide.shouldBeEmpty()
        }

        "java.util.List<F>" should matchType<ASTClassOrInterfaceType> {

            it.leftHandSide.shouldBeEmpty()

            it.typeArguments shouldBePresent child {
                child<ASTTypeArgument> {
                    child<ASTClassOrInterfaceType> {
                        it.typeImage shouldBe "F"
                    }
                }
            }
        }
    }

    parserTest("Test recursive ClassOrInterfaceTypes") {

        "java.util.Map.@Foo Entry<K, V>" should matchType<ASTClassOrInterfaceType> {
            it.typeImage shouldBe "java.util.Map.Entry"
            it.image shouldBe "Entry"

            it.leftHandSide shouldBePresent child {
                it.typeImage shouldBe "java.util.Map"
            }

            child<ASTAnnotation> {
                it.annotationName shouldBe "Foo"

                child<ASTMarkerAnnotation> {
                    child<ASTName> { }
                }
            }

            it.typeArguments shouldBePresent child {

                child<ASTTypeArgument> {
                    child<ASTClassOrInterfaceType> {
                        it.typeImage shouldBe "K"
                        it.typeArguments.shouldBeEmpty()
                        it.leftHandSide.shouldBeEmpty()
                    }
                }

                child<ASTTypeArgument> {
                    child<ASTClassOrInterfaceType> {
                        it.typeImage shouldBe "V"
                        it.typeArguments.shouldBeEmpty()
                        it.leftHandSide.shouldBeEmpty()
                    }
                }
            }
        }

        "Foo<K>.@A Bar.Brew<V>" should matchType<ASTClassOrInterfaceType> {

            it.typeImage shouldBe "Foo.Bar.Brew"

            it.leftHandSide shouldBePresent child {
                it.typeImage shouldBe "Foo.Bar"

                it.typeArguments.shouldBeEmpty()

                it.leftHandSide shouldBePresent child {
                    it.typeImage shouldBe "Foo"

                    it.typeArguments shouldBePresent child {
                        child<ASTTypeArgument> {
                            child<ASTClassOrInterfaceType> {
                                it.typeImage shouldBe "K"
                            }
                        }
                    }
                }

                child<ASTAnnotation> {
                    it.annotationName shouldBe "A"

                    child<ASTMarkerAnnotation> {
                        child<ASTName> { }
                    }
                }
            }

            it.typeArguments shouldBePresent child {
                child<ASTTypeArgument> {
                    child<ASTClassOrInterfaceType> {
                        it.typeImage shouldBe "V"
                    }
                }
            }
        }
    }

})