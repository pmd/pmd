/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldMatchN
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.types.STRING
import net.sourceforge.pmd.lang.java.types.shouldMatchMethod
import net.sourceforge.pmd.lang.java.types.testdata.TypeInferenceTestCases
import net.sourceforge.pmd.lang.java.types.typeDsl
import net.sourceforge.pmd.lang.java.types.withNewBounds

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
                it.typeMirror shouldBe with (it.typeDsl) { gen.t_List[int.box()] } // List<Integer>

                it::getQualifier shouldBe methodCall("peek") {
                    it.typeMirror shouldBe with (it.typeDsl) { gen.t_Stream[int.box()] } // Stream<Integer>

                    it::getQualifier shouldBe methodCall("of") {
                        it.typeMirror shouldBe with (it.typeDsl) { gen.t_Stream[int.box()] } // Stream<Integer>
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
                it.typeMirror shouldBe with (it.typeDsl) { gen.t_List[ts.UNRESOLVED_TYPE] } // List</*unresolved*/>

                it::getQualifier shouldBe methodCall("map") {
                    it.typeMirror shouldBe with (it.typeDsl) { gen.t_Stream[ts.UNRESOLVED_TYPE] } // Stream</*unresolved*/>

                    it::getQualifier shouldBe methodCall("of") {
                        it.typeMirror shouldBe with (it.typeDsl) { gen.t_Stream[int.box()] } // Stream<Integer>
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

        val (t_Scratch) = acu.descendants(ASTClassOrInterfaceDeclaration::class.java).toList { it.typeMirror }
        val (f) = acu.descendants(ASTMethodDeclaration::class.java).toList()
        val (fCall) = acu.descendants(ASTMethodCall::class.java).toList()

        fCall.shouldMatchN {
            methodCall("f") {
                it.methodType.symbol shouldBe f.symbol

                with (it.typeDsl) {
                    // Function<String, Integer> & java.io.Serializable
                    val serialFun = gen.t_Function[gen.t_String, int.box()] * ts.SERIALIZABLE

                    it.methodType.shouldMatchMethod(named = "f", declaredIn = t_Scratch, withFormals = listOf(serialFun), returning = serialFun)
                }

                argList {
                    exprLambda {
                        lambdaFormals(1)
                        methodCall("length") {
                            variableAccess("s") {
                                it.typeMirror shouldBe it.typeSystem.STRING
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

        val (t_Scratch) = acu.descendants(ASTClassOrInterfaceDeclaration::class.java).toList { it.typeMirror }
        val (f) = acu.descendants(ASTMethodDeclaration::class.java).toList()
        val (fCall) = acu.descendants(ASTMethodCall::class.java).toList()

        fCall.shouldMatchN {
            methodCall("f") {
                it.methodType.symbol shouldBe f.symbol

                with (it.typeDsl) {
                    // Function<String, Integer> & java.io.Serializable
                    val serialFun = gen.t_Function[gen.t_String, int.box()] * ts.SERIALIZABLE

                    it.methodType.shouldMatchMethod(named = "f", declaredIn = t_Scratch, withFormals = listOf(serialFun), returning = serialFun)
                }

                argList {
                    exprLambda {
                        lambdaFormals(1)
                        methodCall("length") {
                            variableAccess("s") {
                                it.typeMirror shouldBe it.typeSystem.STRING
                            }
                            argList(0)
                        }
                    }
                }
            }
        }
    }

    parserTest("Test lambda with field access in return expression (inner ctor call)") {
        val acu = parser.parse("""
            import java.util.function.Function;

            class Scratch {

                class WithField {
                    int i;
                }

                static void foo(Function<Scratch, Integer> f) { }

                void main(String[] args) {
                    // Symbol resolution for .i must not fail, even though its
                    // LHS depends on the lambda parameter, and it is a return 
                    // expression of the lambda, so is used by compatibility check

                    foo(s -> s.new WithField().i);
                }
            }
        """)

        val (t_Scratch, t_WithField) = acu.descendants(ASTClassOrInterfaceDeclaration::class.java).toList { it.typeMirror }
        val (foo) = acu.descendants(ASTMethodDeclaration::class.java).toList()
        val (fooCall) = acu.descendants(ASTMethodCall::class.java).toList()

        fooCall.shouldMatchN {
            methodCall("foo") {
                argList {
                    exprLambda {
                        lambdaFormals(1)

                        fieldAccess("i") {
                            it.typeMirror shouldBe it.typeSystem.INT
                            constructorCall {
                                variableAccess("s") {
                                    it.typeMirror shouldBe t_Scratch
                                }
                                classType("WithField") {
                                    it.typeMirror shouldBe t_WithField
                                }
                                argList(0)
                            }
                        }
                    }
                }
                it.methodType.symbol shouldBe foo.symbol // ask after asking for type of inner
            }
        }
    }

    parserTest("Test lambda with field access in return expression (method call)") {
        val acu = parser.parse("""
            import java.util.function.Function;

            class Scratch {

                static class WithField {
                    int i;
                }

                WithField fetch() { return new WithField(); }

                static void foo(Function<Scratch, Integer> f) { }

                void main(String[] args) {
                    // Symbol resolution for .i must not fail, even though its
                    // LHS depends on the lambda parameter, and it is a return 
                    // expression of the lambda, so is used by compatibility check

                    foo(s -> s.fetch().i);
                }
            }
        """)

        val (t_Scratch, t_WithField) = acu.descendants(ASTClassOrInterfaceDeclaration::class.java).toList { it.typeMirror }
        val (fetch, foo) = acu.descendants(ASTMethodDeclaration::class.java).toList()
        val (fooCall) = acu.descendants(ASTMethodCall::class.java).toList()

        fooCall.shouldMatchN {
            methodCall("foo") {
                it.methodType.symbol shouldBe foo.symbol
                argList {
                    exprLambda {
                        lambdaFormals(1)

                        fieldAccess("i") {
                            it.typeMirror shouldBe it.typeSystem.INT
                            methodCall("fetch") {
                                variableAccess("s") {
                                    it.typeMirror shouldBe t_Scratch
                                }
                                argList(0)
                            }
                        }
                    }
                }
            }
        }
    }
})
