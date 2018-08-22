
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J10
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J11

class Java11Test : FunSpec({


    parserTest("Test lambda parameter with var keyword on java 10", javaVersion = J10) {

        "(var x) -> String.valueOf(x)" should matchExpr<ASTLambdaExpression> {
            child<ASTFormalParameters> {
                child<ASTFormalParameter> {
                    child<ASTType> {
                        it.typeImage shouldBe "var"

                        child<ASTReferenceType> {
                            child<ASTClassOrInterfaceType> {
                                it.image shouldBe "var"
                            }
                        }
                    }

                    child<ASTVariableDeclaratorId> { }
                }
            }

            unspecifiedChild()
        }

        "(var x, var y) -> x + y" should matchExpr<ASTLambdaExpression> {
            child<ASTFormalParameters> {
                child<ASTFormalParameter> {
                    child<ASTType> {
                        it.typeImage shouldBe "var"

                        child<ASTReferenceType> {
                            child<ASTClassOrInterfaceType> {
                                it.image shouldBe "var"
                            }
                        }
                    }
                    child<ASTVariableDeclaratorId> { }
                }

                child<ASTFormalParameter> {
                    child<ASTType> {
                        it.typeImage shouldBe "var"

                        child<ASTReferenceType> {
                            child<ASTClassOrInterfaceType> {
                                it.image shouldBe "var"
                            }
                        }
                    }
                    child<ASTVariableDeclaratorId> { }

                }
            }

            unspecifiedChild()
        }

        "(@Nonnull var x) -> String.valueOf(x)" should matchExpr<ASTLambdaExpression> {
            child<ASTFormalParameters> {
                child<ASTFormalParameter> {
                    child<ASTAnnotation>(ignoreChildren = true) {}
                    unspecifiedChildren(2)
                }
            }
            unspecifiedChild()
        }
    }

    parserTest("Test lambda parameter with var keyword on java 11", javaVersion = J11) {

        "(var x) -> String.valueOf(x)" should matchExpr<ASTLambdaExpression> {
            child<ASTFormalParameters> {
                child<ASTFormalParameter> {
                    it.isTypeInferred shouldBe true
                    child<ASTVariableDeclaratorId> { }
                }
            }

            unspecifiedChild()
        }
    }

})