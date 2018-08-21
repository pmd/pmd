import io.kotlintest.should
import io.kotlintest.shouldBe
import net.sourceforge.pmd.lang.ast.test.matchNode
import net.sourceforge.pmd.lang.java.ast.*

import org.junit.Test

class Java11Test {


    @Test
    fun testLocalVariableSyntaxForLambdaParametersWithJava10() {

        val lambdas = listOf(
                "(var x) -> String.valueOf(x)",
                "(var x, var y) -> x + y",
                "(@Nonnull var x) -> String.valueOf(x)"
        ).map { parseExpression<ASTLambdaExpression>(it, javaVersion = "10") }

        // (var x) -> String.valueOf(x)
        lambdas[0] should matchNode<ASTLambdaExpression> {
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

        // (var x, var y) -> x + y
        lambdas[1] should matchNode<ASTLambdaExpression> {
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

        // (@Nonnull var x) -> String.valueOf(x)
        lambdas[2] should matchNode<ASTLambdaExpression> {
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

        val lambda: ASTLambdaExpression = parseExpression("(var x) -> String.valueOf(x)", javaVersion = "11")

        lambda should matchNode<ASTLambdaExpression> {
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
