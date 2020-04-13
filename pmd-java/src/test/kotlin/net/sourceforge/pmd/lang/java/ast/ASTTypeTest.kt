package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTTypeTest : ParserTestSpec({

    parserTest("Test ambiguous qualified names") {

        inContext(TypeParsingCtx) {


            "java.util.List" should parseAs {
                classType("List") {

                    it::getTypeImage shouldBe "java.util.List"
                    it::getImage shouldBe "List"
                    it::getTypeArguments shouldBe null
                    it::getQualifier shouldBe null

                    it::getAmbiguousLhs shouldBe child {
                        it::getName shouldBe "java.util"
                    }
                }
            }

            "java.util.List<F>" should parseAs {
                classType("List") {

                    it::getQualifier shouldBe null
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

            "foo" should parseAs {
                classType("foo") {
                    it::getTypeImage shouldBe "foo"
                    it::getImage shouldBe "foo"

                    it::getAmbiguousLhs shouldBe null
                    it::getQualifier shouldBe null
                }
            }
        }
    }

    parserTest("Test non-ambiguous segments") {

        // Perhaps surprisingly a type annotation binds to the closest segment
        // So @B binds to "java"
        // If the annotation is not applicable to TYPE_USE then it doesn't compile

        // this happens in type context, eg in a cast, or in an extends list

        // TYPE_USE annotations are prohibited eg before a declaration

        inContext(TypeParsingCtx) {


            "@B @H java.util.@C @K Map" should parseAs {

                classType("Map") {
                    it::getQualifier shouldBe classType("util") {
                        it::getQualifier shouldBe classType("java") {
                            annotation("B")
                            annotation("H")
                        }
                    }

                    annotation("C")
                    annotation("K")

                    it::getTypeImage shouldBe "java.util.Map"
                }
            }


            "java.util.Map.@Foo Entry<K, V>" should parseAs {
                classType("Entry") {
                    it::getTypeImage shouldBe "java.util.Map.Entry"

                    it::getQualifier shouldBe null

                    it::getAmbiguousLhs shouldBe child {
                        it::getTypeImage shouldBe "java.util.Map"
                        it::getImage shouldBe "java.util.Map"
                        it::getName shouldBe "java.util.Map"
                    }

                    annotation("Foo")

                    it::getTypeArguments shouldBe child {

                        classType("K") {
                            it::getTypeArguments shouldBe null
                            it::getQualifier shouldBe null
                        }

                        classType("V") {
                            it::getTypeArguments shouldBe null
                            it::getQualifier shouldBe null
                        }
                    }
                }
            }

            "Foo<K>.@A Bar.Brew<V>" should parseAs {
                classType("Brew") {

                    it::getTypeImage shouldBe "Foo.Bar.Brew"

                    it::getQualifier shouldBe classType("Bar") {
                        it::getTypeImage shouldBe "Foo.Bar"
                        it::getTypeArguments shouldBe null

                        it::getQualifier shouldBe classType("Foo") {
                            it::getTypeImage shouldBe "Foo"

                            it::getTypeArguments shouldBe typeArgList {
                                classType("K")
                            }
                        }


                        annotation("A")
                    }

                    it::getTypeArguments shouldBe child {
                        classType("V") {
                            it::getTypeArguments shouldBe null
                            it::getQualifier shouldBe null
                        }
                    }
                }
            }
        }
    }
})
