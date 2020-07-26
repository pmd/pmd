/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotlintest.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.types.testdata.TypeInferenceTestCases
import net.sourceforge.pmd.lang.java.types.typeDsl

/**
 * Expensive test cases for the overload resolution phase.
 *
 * Edit: So those used to be very expensive (think minutes of execution),
 * but optimisations made them very fast.
 */
class PolyResolutionTest : ProcessorTestSpec({


    parserTest("Test context passing") {

        inContext(StatementParsingCtx) {

            """
            java.util.List<Integer> c = java.util.Arrays.asList(null);

        """ should parseAs {
                localVarDecl {
                    with(it.typeDsl) {
                        modifiers { }
                        classType("List") {
                            // it.typeMirror shouldBe RefTypeGen.`t_List{Integer}`
                            typeArgList()
                        }

                        child<ASTVariableDeclarator> {
                            variableId("c") {
                                it.typeMirror shouldBe gen.`t_List{Integer}`
                            }
                            child<ASTMethodCall> {
                                it.typeMirror shouldBe gen.`t_List{Integer}`
                                it.qualifier shouldBe typeExpr {
                                    classType("Arrays") {
                                        it.typeMirror shouldBe java.util.Arrays::class.decl
                                    }
                                }
                                argList {
                                    child<ASTNullLiteral> {
                                        it.typeMirror shouldBe ts.NULL_TYPE
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    parserTest("Test nested ternaries in invoc ctx") {
        asIfIn(TypeInferenceTestCases::class.java)

        inContext(StatementParsingCtx) {

            """
                id(x > 0 ? Double.POSITIVE_INFINITY : x < 0 ? Double.NEGATIVE_INFINITY : Double.NaN);
            """ should parseAs {
                exprStatement {
                    methodCall("id") {
                        argList {
                            child<ASTConditionalExpression> {
                                infixExpr(BinaryOp.GT) {
                                    variableAccess("x")
                                    int(0)
                                }
                                fieldAccess("POSITIVE_INFINITY")
                                child<ASTConditionalExpression> {
                                    it::getTypeMirror shouldBe it.typeSystem.DOUBLE

                                    infixExpr(BinaryOp.LT) {
                                        variableAccess("x")
                                        int(0)
                                    }
                                    fieldAccess("NEGATIVE_INFINITY")
                                    fieldAccess("NaN")
                                }

                                it::getTypeMirror shouldBe it.typeSystem.DOUBLE
                            }
                        }

                        it::getTypeMirror shouldBe it.typeSystem.DOUBLE.box()
                    }
                }
            }


        }

    }

    parserTest("Test with varargs method") {

        inContext(StatementParsingCtx) {

            """
            String.format("L%s%s%s;",
                          binaryToInternal(packageName),
                          (packageName.length() > 0 ? "/" : ""),
                          className);
        """ should parseAs {

                exprStatement {
                    methodCall("format") {

                        typeExpr {
                            classType("String")
                        }
                        argList {
                            stringLit("\"L%s%s%s;\"")
                            methodCall("binaryToInternal") {
                                argList {
                                    variableAccess("packageName")
                                }
                            }
                            child<ASTConditionalExpression> {

                                it.typeMirror.toString() shouldBe "java.lang.Object"

                                infixExpr(BinaryOp.GT) {
                                    methodCall("length") {
                                        ambiguousName("packageName")
                                        argList {}
                                    }

                                    int(0)
                                }

                                stringLit("\"/\"")
                                stringLit("\"\"")
                            }
                            variableAccess("className")
                        }
                    }
                }
            }
        }
    }


})
