/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotlintest.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldMatchN
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.types.testdata.TypeInferenceTestCases
import net.sourceforge.pmd.lang.java.types.typeDsl

class LambdaReturnConstraintTest : ProcessorTestSpec({


    parserTest("Test dangling method parameter - ok") {

        importedTypes += java.util.List::class.java
        importedTypes += TypeInferenceTestCases::class.java
        genClassHeader = "class TypeInferenceTestCases"
        packageName = "net.sourceforge.pmd.types.testdata.typeinference"

        val chain = """
            // public static <T, K> T wild(K t)

            // OK - no obvious <T> for wild but since functional method is void it's ok
            java.util.stream.Stream.of(1)
                                   .peek(i -> wild(i))
                                   .collect(java.util.stream.Collectors.toList())

                    """


        val node = ExpressionParsingCtx.parseNode(chain, this)

        node.shouldMatchN {
            methodCall("collect") {
                it.typeMirror.toString() shouldBe "java.util.List<java.lang.Integer>"

                it::getQualifier shouldBe methodCall("peek") {
                    it.typeMirror.toString() shouldBe "java.util.stream.Stream<java.lang.Integer>"

                    it::getQualifier shouldBe methodCall("of") {
                        it.typeMirror.toString() shouldBe "java.util.stream.Stream<java.lang.Integer>"
                        it::getQualifier shouldBe typeExpr {
                            qualClassType("java.util.stream.Stream")
                        }

                        it::getArguments shouldBe child {
                            int(1)
                        }
                    }

                    it::getArguments shouldBe child {

                        child<ASTLambdaExpression> {
                            unspecifiedChild() // params

                            methodCall("wild") {
                                argList {
                                    variableAccess("i")
                                }
                            }
                        }
                    }
                }
                it::getArguments shouldBe child {
                    unspecifiedChild()
                }
            }
        }
    }

    parserTest("Test dangling method parameter recovery") {

        importedTypes += java.util.List::class.java
        importedTypes += TypeInferenceTestCases::class.java
        genClassHeader = "class TypeInferenceTestCases"
        packageName = "net.sourceforge.pmd.typeresolution.testdata.typeinference"

        val chain = """
            // public static <T, K> T wild(K t)

             // Javac error - <R> of map cannot be bound
             // we infer it as Object to recover
            java.util.stream.Stream.of(1)
                                   .map(i -> wild(i))
                                   .collect(java.util.stream.Collectors.toList())

                    """


        val node = ExpressionParsingCtx.parseNode(chain, this)

        node.shouldMatchN {
            methodCall("collect") {
                it.typeMirror.toString() shouldBe "java.util.List</*unresolved*/>"

                it::getQualifier shouldBe methodCall("map") {
                    it.typeMirror.toString() shouldBe "java.util.stream.Stream</*unresolved*/>"

                    it::getQualifier shouldBe methodCall("of") {
                        it.typeMirror.toString() shouldBe "java.util.stream.Stream<java.lang.Integer>"
                        it::getQualifier shouldBe typeExpr {
                            qualClassType("java.util.stream.Stream")
                        }

                        it::getArguments shouldBe child {
                        int(1)
                    }
                }

                it::getArguments shouldBe child {

                    child<ASTLambdaExpression> {
                        unspecifiedChild() // params

                        methodCall("wild") {
                            argList {
                                variableAccess("i")
                            }
                        }
                    }
                }
                }
                it::getArguments shouldBe child {
                    unspecifiedChild()
                }
            }
        }
    }

    val serialFun = "java.util.function.Function<java.lang.String, java.lang.Integer> & java.io.Serializable"

    parserTest("Test functional interface induced by intersection") {

        val acu = parser.parse("""
            import java.io.Serializable;
            import java.util.function.Function;

            class Scratch {

                public static <T extends Function<String, Integer> & Serializable>
                T f(T k) {
                    return k;
                }

                public static void main(String... args) {
                    f(s -> s.length());
                }
            }
        """)

        val (f) = acu.descendants(ASTMethodDeclaration::class.java).toList()
        val (fCall) = acu.descendants(ASTMethodCall::class.java).toList()

        fCall.shouldMatchN {
            methodCall("f") {
                it.methodType.symbol shouldBe f.symbol
                it.methodType.toString() shouldBe "Scratch.<T extends $serialFun> f($serialFun) -> $serialFun"

                argList {
                    exprLambda {
                        lambdaFormals(1)
                        methodCall("length") {
                            variableAccess("s") {
                                it.typeMirror shouldBe with(it.typeDsl) { String::class.decl }
                            }
                            argList(0)
                        }
                    }
                }
            }
        }

    }

    parserTest("Test functional interface induced by intersection 2") {
        // more dependencies between variables here

        val acu = parser.parse("""
            import java.io.Serializable;
            import java.util.function.Function;

            class Scratch {

                public static <R, T extends Function<String, R> & Serializable>
                T f(T k) {
                    return k;
                }

                public static void main(String... args) {
                    f(s -> s.length());
                }
            }
        """)

        val (f) = acu.descendants(ASTMethodDeclaration::class.java).toList()
        val (fCall) = acu.descendants(ASTMethodCall::class.java).toList()

        val boundOfF = "java.util.function.Function<java.lang.String, R> & java.io.Serializable"

        fCall.shouldMatchN {
            methodCall("f") {
                it.methodType.symbol shouldBe f.symbol
                it.methodType.toString() shouldBe "Scratch.<R, T extends $boundOfF> f($serialFun) -> $serialFun"

                argList {
                    exprLambda {
                        lambdaFormals(1)
                        methodCall("length") {
                            variableAccess("s") {
                                it.typeMirror shouldBe with(it.typeDsl) { String::class.decl }
                            }
                            argList(0)
                        }
                    }
                }
            }
        }
    }
})
