

import io.kotlintest.should
import io.kotlintest.shouldBe
import net.sourceforge.pmd.lang.java.ast.*
import org.junit.Test

class Java11Test {


    @Test
    fun testLocalVariableSyntaxForLambdaParametersWithJava10() {

        "(var x) -> String.valueOf(x)" should matchExpr<ASTLambdaExpression>(javaVersion = "10") {
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

        "(var x, var y) -> x + y" should matchExpr<ASTLambdaExpression>(javaVersion = "10") {
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

        "(@Nonnull var x) -> String.valueOf(x)" should matchExpr<ASTLambdaExpression>(javaVersion = "10") {
            child<ASTFormalParameters> {
                child<ASTFormalParameter> {
                    child<ASTAnnotation>(ignoreChildren = true) {}
                    unspecifiedChildren(2)
                }
            }
            unspecifiedChild()
        }
    }

    @Test
    fun testLocalVariableSyntaxForLambdaParametersWithJava11() {

        "(var x) -> String.valueOf(x)" should matchExpr<ASTLambdaExpression>(javaVersion = "11") {
            child<ASTFormalParameters> {
                child<ASTFormalParameter> {
                    it.isTypeInferred shouldBe true
                    child<ASTVariableDeclaratorId> { }
                }
            }

            unspecifiedChild()
        }
    }
}
