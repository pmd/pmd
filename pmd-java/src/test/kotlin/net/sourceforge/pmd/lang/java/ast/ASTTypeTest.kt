package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTTypeTest : ParserTestSpec({

    parserTest("Test ambiguous qualified names") {

        "java.util.List" should matchType<ASTClassOrInterfaceType> {
            it::getTypeImage shouldBe "java.util.List"
            it::getImage shouldBe "List"
            it::getTypeArguments.shouldBeEmpty()
            it::getLhsType.shouldBeEmpty()

            it::getAmbiguousLhs shouldBePresent child {
                it::getName shouldBe "java.util"
            }
        }

        "java.util.List<F>" should matchType<ASTClassOrInterfaceType> {

            it::getLhsType.shouldBeEmpty()
            it::getImage shouldBe "List"

            it::getAmbiguousLhs shouldBePresent child {
                it::getName shouldBe "java.util"
            }

            it::getTypeArguments shouldBePresent child {
                child<ASTTypeArgument> {
                    child<ASTClassOrInterfaceType> {
                        it::getTypeImage shouldBe "F"
                    }
                }
            }
        }
    }

    parserTest("Test simple names") {

        "foo" should matchType<ASTClassOrInterfaceType> {
            it::getTypeImage shouldBe "foo"
            it::getImage shouldBe "foo"

            it::getAmbiguousLhs.shouldBeEmpty()
            it::getLhsType.shouldBeEmpty()
        }

    }

    parserTest("Test non-ambiguous segments") {

        "java.util.Map.@Foo Entry<K, V>" should matchType<ASTClassOrInterfaceType> {
            it::getTypeImage shouldBe "java.util.Map.Entry"
            it::getImage shouldBe "Entry"

            it::getLhsType.shouldBeEmpty()

            it::getAmbiguousLhs shouldBePresent child {
                it::getTypeImage shouldBe "java.util.Map"
                it::getImage shouldBe "java.util.Map"
                it::getName shouldBe "java.util.Map"
            }

            child<ASTAnnotation> {
                it::getAnnotationName shouldBe "Foo"

                child<ASTMarkerAnnotation> {
                    child<ASTName> { }
                }
            }

            it::getTypeArguments shouldBePresent child {

                child<ASTTypeArgument> {
                    child<ASTClassOrInterfaceType> {
                        it::getTypeImage shouldBe "K"
                        it::getTypeArguments.shouldBeEmpty()
                        it::getLhsType.shouldBeEmpty()
                    }
                }

                child<ASTTypeArgument> {
                    child<ASTClassOrInterfaceType> {
                        it::getTypeImage shouldBe "V"
                        it::getTypeArguments.shouldBeEmpty()
                        it::getLhsType.shouldBeEmpty()
                    }
                }
            }
        }

        "Foo<K>.@A Bar.Brew<V>" should matchType<ASTClassOrInterfaceType> {

            it::getTypeImage shouldBe "Foo.Bar.Brew"

            it::getLhsType shouldBePresent child {
                it::getTypeImage shouldBe "Foo.Bar"

                it::getTypeArguments.shouldBeEmpty()

                it::getLhsType shouldBePresent child {
                    it::getTypeImage shouldBe "Foo"

                    it::getTypeArguments shouldBePresent child {
                        child<ASTTypeArgument> {
                            child<ASTClassOrInterfaceType> {
                                it::getTypeImage shouldBe "K"
                            }
                        }
                    }
                }

                child<ASTAnnotation> {
                    it::getAnnotationName shouldBe "A"

                    child<ASTMarkerAnnotation> {
                        child<ASTName> { }
                    }
                }
            }

            it::getTypeArguments shouldBePresent child {
                child<ASTTypeArgument> {
                    child<ASTClassOrInterfaceType> {
                        it::getTypeImage shouldBe "V"
                    }
                }
            }
        }
    }

})