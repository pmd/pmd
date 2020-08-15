/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldMatchN
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol
import net.sourceforge.pmd.lang.java.types.*
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind.INT
import net.sourceforge.pmd.lang.java.types.testdata.TypeInferenceTestCases
import java.io.BufferedOutputStream
import java.io.DataOutputStream
import java.io.OutputStream

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

    parserTest("Test standalonable ternary in invoc ctx") {

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
                            ternaryExpr {

                                // not String (this is a not standalone, just in an invocation ctx)
                                it.typeMirror shouldBe it.typeSystem.OBJECT

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

    parserTest("Ternary condition does not let context flow") {

        suspend fun doTest(askOuterFirst: Boolean) {

            val acu = parser.parse("""

class O {
    public static double copySign(double magnitude, double sign) {
        return Math.copySign(magnitude, (Double.isNaN(sign) ? 1.0d : sign));
    }
}
        """.trimIndent())


            val (copySignCall) =
                    acu.descendants(ASTMethodCall::class.java).toList()

            val double = acu.typeSystem.DOUBLE
            val boolean = acu.typeSystem.BOOLEAN

            val testName = if (askOuterFirst) "Ask outer first" else "Ask inner first"
            doTest(testName) {

                copySignCall.shouldMatchN {
                    methodCall("copySign") {

                        if (askOuterFirst)
                            it::getTypeMirror shouldBe double

                        it::getQualifier shouldBe unspecifiedChild()
                        argList {
                            variableAccess("magnitude")
                            child<ASTConditionalExpression> {
                                methodCall("isNaN") {
                                    it::getTypeMirror shouldBe boolean

                                    it::getQualifier shouldBe unspecifiedChild()
                                    argList(1)
                                }

                                unspecifiedChild()
                                unspecifiedChild()
                            }
                        }

                        if (!askOuterFirst)
                            it::getTypeMirror shouldBe double

                    }
                }
            }
        }

        doTest(askOuterFirst = true)
        doTest(askOuterFirst = false)

    }


    parserTest("Ternaries with an additive expr as context") {

        val acu = parser.parse("""

class O {

    public static String toString(Class c) {
        return (c.isInterface() ? "interface " : (c.isPrimitive() ? "" : "class "))
            + c.getName();
    }
}
        """.trimIndent())


        val additive =
                acu.descendants(ASTReturnStatement::class.java).firstOrThrow().expr!!

        additive.shouldMatchN {
            infixExpr(BinaryOp.ADD) {
                it::getTypeMirror shouldBe it.typeSystem.STRING

                ternaryExpr {
                    it::getCondition shouldBe methodCall("isInterface") {
                        variableAccess("c")
                        argList(0)
                    }

                    it::getThenBranch shouldBe stringLit("\"interface \"")

                    it::getElseBranch shouldBe ternaryExpr {
                        it::getCondition shouldBe methodCall("isPrimitive") {
                            variableAccess("c")
                            argList(0)
                        }
                        it::getThenBranch shouldBe stringLit("\"\"")

                        it::getElseBranch shouldBe stringLit("\"class \"")
                    }
                }

                methodCall("getName") {
                    variableAccess("c")
                    argList(0)
                }
            }
        }

    }



    parserTest("Standalone ctor in invocation ctx") {

        val acu = parser.parse("""
import java.io.*;

class O {
    public static void doSt(OutputStream out) {
        new DataOutputStream(new BufferedOutputStream(out));
    }
}
        """.trimIndent())


        val outerCtor =
                acu.descendants(ASTConstructorCall::class.java).firstOrThrow()

        outerCtor.shouldMatchN {
            constructorCall {
                it::getTypeMirror shouldBe with(it.typeDsl) {
                    DataOutputStream::class.raw
                }

                it::getTypeNode shouldBe unspecifiedChild()

                argList {
                    constructorCall {
                        with(it.typeDsl) {
                            it::getTypeMirror shouldBe BufferedOutputStream::class.raw

                            it.methodType.shouldMatchMethod(
                                    named = JConstructorSymbol.CTOR_NAME,
                                    declaredIn = BufferedOutputStream::class.raw,
                                    withFormals = listOf(OutputStream::class.raw),
                                    returning = BufferedOutputStream::class.raw
                            )
                        }

                        it::getTypeNode shouldBe unspecifiedChild()

                        argList {
                            variableAccess("out")
                        }
                    }
                }
            }
        }

    }


    parserTest("Method call in invocation ctx of standalone ctor") {

        val acu = parser.parse("""
import java.io.*;

class O {

    public static void doSt(OutputStream out) {
        new DataOutputStream(wrap(out));
    }
    
    static OutputStream wrap(OuputStream out) { return out; }

}
        """.trimIndent())


        val outerCtor =
                acu.descendants(ASTConstructorCall::class.java).firstOrThrow()

        outerCtor.shouldMatchN {
            constructorCall {
                it::getTypeMirror shouldBe with(it.typeDsl) {
                    DataOutputStream::class.raw
                }

                it::getTypeNode shouldBe unspecifiedChild()

                argList {
                    methodCall("wrap") {
                        it::getTypeMirror shouldBe with(it.typeDsl) {
                            OutputStream::class.raw
                        }

                        argList {
                            variableAccess("out")
                        }
                    }
                }
            }
        }

    }


    parserTest("Method call in some ternary bug") {

        val acu = parser.parse("""

class O {

     public static String getConnectPermission(Object url) {
        String urlString = url.toString();
        int bangPos = urlString.indexOf("!/");
        urlString = urlString.substring(4, bangPos > -1 ? bangPos : urlString.length()); // <- HERE the call to length is bugged
        return urlString;
    }

}
        """.trimIndent())


        val ternary =
                acu.descendants(ASTConditionalExpression::class.java).firstOrThrow()

        ternary.shouldMatchN {
            ternaryExpr {
                unspecifiedChild()
                unspecifiedChild()
                methodCall("length") {
                    it.typeMirror shouldBe it.typeSystem.INT

                    unspecifiedChild()
                    unspecifiedChild()
                }
            }
        }

    }



    parserTest("Cast context doesn't constrain invocation type") {

        val acu = parser.parse("""
class Scratch {

    static <T> T id(T t) {
        return t;
    }
    
    static {
        Comparable o = null;
        // T := Object, and there's no error
        o = (String) id(o);
    }
}

        """.trimIndent())

        val (t_Scratch) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }

        val call = acu.descendants(ASTMethodCall::class.java).firstOrThrow()

        call.shouldMatchN {
            methodCall("id") {
                with(it.typeDsl) {
                    it.methodType.shouldMatchMethod(
                            named = "id",
                            declaredIn = t_Scratch,
                            withFormals = listOf(gen.t_Comparable),
                            returning = gen.t_Comparable
                    )
                }

                argList(1)
            }
        }
    }




})
