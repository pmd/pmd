
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.ast.JavaVersion.*
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Latest

class Java11Test : FunSpec({


    parserTest("Test lambda parameter with var keyword", javaVersions = J1_8..J10) {

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
                    child<ASTType>(ignoreChildren = true) {}
                    child<ASTVariableDeclaratorId> {}
                }
            }
            unspecifiedChild()
        }
    }

    parserTest("Test lambda parameter with var keyword", javaVersions = J11..Latest) {

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