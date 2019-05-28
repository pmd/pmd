
import io.kotlintest.shouldBe
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.ast.JavaVersion.*
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Latest

class Java11Test : ParserTestSpec({

    parserTestGroup("Test lambda parameter with var keyword") {

        // var keyword should be a normal type pre-java 11
        onVersions(J1_8..J10) {

            "(var x) -> String.valueOf(x)" should matchExpr<ASTLambdaExpression> {
                child<ASTLambdaParameterList> {
                    child<ASTLambdaParameter> {
                        child<ASTClassOrInterfaceType> {
                            it.image shouldBe "var"
                        }
                        variableId("x")
                    }
                }

                unspecifiedChild()
            }

            "(var x, var y) -> x + y" should matchExpr<ASTLambdaExpression> {
                child<ASTLambdaParameterList> {
                    child<ASTLambdaParameter> {
                        child<ASTClassOrInterfaceType> {
                            it.image shouldBe "var"
                        }
                        variableId("x")
                    }

                    child<ASTLambdaParameter> {
                        child<ASTClassOrInterfaceType> {
                            it.image shouldBe "var"
                        }
                        variableId("y")
                    }
                }

                unspecifiedChild()
            }

            "(@Nonnull var x) -> String.valueOf(x)" should matchExpr<ASTLambdaExpression> {
                child<ASTLambdaParameterList> {
                    child<ASTLambdaParameter> {
                        annotationList {
                            annotation()
                        }
                        child<ASTType>(ignoreChildren = true) {}
                        variableId("x")
                    }
                }
                unspecifiedChild()
            }
        }

        // var keyword should generate no type after java 11
        onVersions(J11..Latest) {
            "(var x) -> String.valueOf(x)" should matchExpr<ASTLambdaExpression> {
                child<ASTLambdaParameterList> {
                    child<ASTLambdaParameter> {
                        it.isTypeInferred shouldBe true
                        variableId("x")
                    }
                }

                unspecifiedChild()
            }
        }
    }

})
