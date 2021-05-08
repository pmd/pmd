/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldMatchN
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.types.*
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


    parserTest("Test context passing overcomes null lower bound") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
            class Foo {

                <T> java.util.List<T> asList(T[] arr) { return null; }

                {
                    java.util.List<Integer> c = asList(null);
                }
            }
        """)

        val call = acu.firstMethodCall()

        spy.shouldBeOk {
            call shouldHaveType gen.`t_List{Integer}`
        }
    }

    parserTest("Test nested ternaries in invoc ctx") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
            class Foo {

                static <T> T id(T t) { return t; }

                {
                    id(x > 0 ? Double.POSITIVE_INFINITY
                             : x < 0 ? Double.NEGATIVE_INFINITY
                                     : Double.NaN);
                }
            }
        """)

        val call = acu.firstMethodCall()

        spy.shouldBeOk {
            call.shouldMatchN {
                methodCall("id") {
                    argList {
                        ternaryExpr {
                            it shouldHaveType double

                            unspecifiedChildren(2)
                            ternaryExpr {
                                it shouldHaveType double

                                unspecifiedChildren(3)
                            }
                        }
                    }
                }
            }

            call shouldHaveType double.box()
        }
    }

    parserTest("Test standalonable ternary in invoc ctx") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
            class Foo {{
                String packageName = "", className = "";
                String.format("L%s%s%s;",
                              packageName,
                              (packageName.length() > 0 ? "/" : ""),
                              className);
            }}
        """)

        val ternary = acu.descendants(ASTConditionalExpression::class.java).firstOrThrow()

        spy.shouldBeOk {
            // not String (this is not a standalone, just in an invocation ctx)
            ternary shouldHaveType ts.OBJECT
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
                            it shouldHaveType double

                        skipQualifier()
                        argList {
                            variableAccess("magnitude")
                            child<ASTConditionalExpression> {
                                methodCall("isNaN") {
                                    it shouldHaveType boolean

                                    skipQualifier()
                                    argList(1)
                                }

                                unspecifiedChild()
                                unspecifiedChild()
                            }
                        }

                        if (!askOuterFirst)
                            it shouldHaveType double

                    }
                }
            }
        }

        doTest(askOuterFirst = true)
        doTest(askOuterFirst = false)

    }


    parserTest("Ternaries with an additive expr as context") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
            class O {

                public static String toString(Class c) {
                    return (c.isInterface() ? "interface " : (c.isPrimitive() ? "" : "class "))
                        + c.getName();
                }
            }
            """.trimIndent())


        val additive = acu.descendants(ASTReturnStatement::class.java).firstOrThrow().expr!!

        spy.shouldBeOk {
            additive shouldHaveType ts.STRING
        }
    }



    parserTest("Standalone ctor in invocation ctx") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
import java.io.*;

class O {
    public static void doSt(OutputStream out) {
        new DataOutputStream(new BufferedOutputStream(out));
    }
}
        """.trimIndent())


        val (outerCtor, innerCtor) = acu.ctorCalls().toList()

        spy.shouldBeOk {
            outerCtor shouldHaveType DataOutputStream::class.raw
            innerCtor shouldHaveType BufferedOutputStream::class.raw

            innerCtor.overloadSelectionInfo.let {
                it::isFailed shouldBe false
                it::needsUncheckedConversion shouldBe false
            }
        }
    }


    parserTest("Method call in invocation ctx of standalone ctor") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
import java.io.*;

class O {

    public static void doSt(OutputStream out) {
        new DataOutputStream(wrap(out));
    }

    static OutputStream wrap(OutputStream out) { return out; }

}
        """.trimIndent())


        val (outerCtor, wrapCall) = acu.descendants(InvocationNode::class.java).toList()

        spy.shouldBeOk {
            outerCtor shouldHaveType DataOutputStream::class.raw
            wrapCall shouldHaveType OutputStream::class.raw

            wrapCall.overloadSelectionInfo.let {
                it::isFailed shouldBe false
                it::needsUncheckedConversion shouldBe false
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
                    it shouldHaveType it.typeSystem.INT

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


    parserTest("Test C-style array dimensions as target type") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
import java.util.Iterator;
import java.util.Map;

class Scratch {

    static <T> T[] getArr(T[] a) { return null; }

    {
        String arr[] = getArr(new String[0]);
    }
}

        """.trimIndent())

        val t_Scratch = acu.firstTypeSignature()

        spy.shouldBeOk {
            acu.firstMethodCall().methodType.shouldMatchMethod(
                    named = "getArr",
                    declaredIn = t_Scratch,
                    withFormals = listOf(ts.STRING.toArray()),
                    returning = ts.STRING.toArray()
            )
        }
    }

    parserTest("Array initializer is an assignment context") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""

    class Scratch {
        {
            Runnable[] r = {
                () -> { } // is a Runnable
            }, r2[] = {
                { // multilevel array
                    () -> { }
                }
            };

            r = new Runnable[] { () -> { } }; // in array alloc
        }
    }

        """.trimIndent())

        val (lambda1, lambda2, lambda3) = acu.descendants(ASTLambdaExpression::class.java).toList()

        spy.shouldBeOk {
            lambda1 shouldHaveType Runnable::class.decl
            lambda2 shouldHaveType Runnable::class.decl
            lambda3 shouldHaveType Runnable::class.decl
        }
    }
})
