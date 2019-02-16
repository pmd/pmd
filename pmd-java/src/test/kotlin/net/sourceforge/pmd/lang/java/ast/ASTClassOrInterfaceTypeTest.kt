package net.sourceforge.pmd.lang.java.ast

import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTClassOrInterfaceTypeTest : FunSpec({


    testGroup("Test non-recursive COITs") {

        "java.util.List" should matchType<ASTClassOrInterfaceType> {
            it.typeImage shouldBe "java.util.List"
        }

        "java.util.List<F>" should matchType<ASTClassOrInterfaceType> {


            child<ASTTypeArguments> {
                child<ASTTypeArgument> {
                    child<ASTClassOrInterfaceType> {
                        it.typeImage shouldBe "F"
                    }
                }
            }
        }
    }

    testGroup("Test recursive COITs") {

        "java.util.Map.@Foo Entry<K, V>" should matchType<ASTClassOrInterfaceType> {
            it.typeImage shouldBe "java.util.Map.Entry"
            it.image shouldBe "Entry"

            it.leftHandSide shouldBePresent child {
                it.typeImage shouldBe "java.util.Map"
            }

            child<ASTAnnotation> {
                it.annotationName shouldBe "Foo"

                child<ASTMarkerAnnotation> {
                    child<ASTName> {  }
                }
            }

            child<ASTTypeArguments> {
                child<ASTTypeArgument> {
                    child<ASTClassOrInterfaceType> {
                        it.typeImage shouldBe "K"
                    }
                }

                child<ASTTypeArgument> {
                    child<ASTClassOrInterfaceType> {
                        it.typeImage shouldBe "V"
                    }
                }
            }
        }

        "Foo<K>.@A Bar.Brew<V>" should matchType<ASTClassOrInterfaceType> {

            it.typeImage shouldBe "Foo.Bar.Brew"

            it.leftHandSide shouldBePresent child {
                it.typeImage shouldBe "Foo.Bar"

                it.leftHandSide shouldBePresent child {
                    it.typeImage shouldBe "Foo"

                    child<ASTTypeArguments> {
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

            child<ASTTypeArguments> {
                child<ASTTypeArgument> {
                    child<ASTClassOrInterfaceType> {
                        it.typeImage shouldBe "V"
                    }
                }
            }
        }
    }


})