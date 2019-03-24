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

        "java.util.Map.@Foo Entry<K, V>" should matchType<ASTClassOrInterfaceType> {
            it::getTypeImage shouldBe "java.util.Map.Entry"
            it::getImage shouldBe "Entry"

            it::getLhsType shouldBe null

            it::getAmbiguousLhs shouldBe child {
                it::getTypeImage shouldBe "java.util.Map"
                it::getImage shouldBe "java.util.Map"
                it::getName shouldBe "java.util.Map"
            }

            child<ASTMarkerAnnotation> {
                it::getAnnotationName shouldBe "Foo"
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

            it::getLhsType shouldBe child {
                it::getTypeImage shouldBe "Foo.Bar"

                it::getTypeArguments shouldBe null

                it::getLhsType shouldBe child {
                    it::getTypeImage shouldBe "Foo"

                    it::getTypeArguments shouldBe child {
                        child<ASTClassOrInterfaceType> {
                            it::getTypeImage shouldBe "K"
                        }
                    }
                }

                child<ASTMarkerAnnotation> {
                    it::getAnnotationName shouldBe "A"
                }
            }

            it::getTypeArguments shouldBe child {
                child<ASTClassOrInterfaceType> {
                    it::getTypeImage shouldBe "V"
                }
            }
        }
    }

})