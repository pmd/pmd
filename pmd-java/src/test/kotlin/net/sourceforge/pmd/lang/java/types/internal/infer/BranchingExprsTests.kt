/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer

import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.types.*
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind.CHAR
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind.DOUBLE
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind.INT
import net.sourceforge.pmd.lang.java.types.testdata.TypeInferenceTestCases
import net.sourceforge.pmd.lang.test.ast.shouldBe
import net.sourceforge.pmd.lang.test.ast.shouldMatchN


class BranchingExprsTests : ProcessorTestSpec({

    fun TypeSystem.stringSupplier() : JTypeMirror = with (TypeDslOf(this)) {
        java.util.function.Supplier::class[gen.t_String]
    }

    parserTestContainer("Test ternary lets context flow") {
        asIfIn(TypeInferenceTestCases::class.java)

        inContext(ExpressionParsingCtx) {
            "makeThree(true ? () -> \"foo\" : () -> \"bar\")" should parseAs {
                methodCall("makeThree") {
                    argList {
                        ternaryExpr {
                            boolean(true)
                            child<ASTLambdaExpression> {
                                it shouldHaveType it.typeSystem.stringSupplier()
                                child<ASTLambdaParameterList> { }
                                stringLit("\"foo\"")
                            }
                            child<ASTLambdaExpression> {
                                it shouldHaveType it.typeSystem.stringSupplier()
                                child<ASTLambdaParameterList> { }
                                stringLit("\"bar\"")
                            }
                        }
                    }
                }
            }
        }
    }

    parserTestContainer("Test ternary infers outer stuff") {
        asIfIn(TypeInferenceTestCases::class.java)

        inContext(ExpressionParsingCtx) {
            "makeThree(true ? () -> \"foo\" : () -> \"bar\")" should parseAs {
                methodCall("makeThree") {
                    argList {
                        ternaryExpr {
                            it shouldHaveType it.typeSystem.stringSupplier()

                            boolean(true)
                            child<ASTLambdaExpression> {
                                child<ASTLambdaParameterList> { }
                                stringLit("\"foo\"")
                            }
                            child<ASTLambdaExpression> {
                                child<ASTLambdaParameterList> { }
                                stringLit("\"bar\"")
                            }
                        }
                    }
                }
            }
        }
    }

    parserTestContainer("Test ternary without context lubs params") {
        otherImports += "java.util.ArrayList"
        otherImports += "java.util.LinkedList"

        inContext(StatementParsingCtx) {
            "var ter = true ? new ArrayList<String>() : new LinkedList<String>();" should parseAs {
                localVarDecl {

                    modifiers { }

                    variableDeclarator("ter") {

                        val lubOfBothLists = with (it.typeDsl) {
                            ts.lub(gen.`t_ArrayList{String}`, gen.`t_LinkedList{String}`)
                        }

                        ternaryExpr {
                            it shouldHaveType lubOfBothLists
                            boolean(true)
                            with(it.typeDsl) {
                                child<ASTConstructorCall>(ignoreChildren = true) {
                                    it shouldHaveType gen.`t_ArrayList{String}`
                                }
                                child<ASTConstructorCall>(ignoreChildren = true) {
                                    it shouldHaveType gen.`t_LinkedList{String}`
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    parserTestContainer("Test switch without context lubs params") {
        otherImports += "java.util.ArrayList"
        otherImports += "java.util.LinkedList"
        otherImports += "java.util.Collections"

        inContext(StatementParsingCtx) {
            """
                var ter = switch(foo) {
                 case 1  -> new ArrayList<String>();
                 case 2  -> new LinkedList<String>();
                 default -> Collections.<String>emptyList();
                };
            """ should parseAs {
                localVarDecl {
                    modifiers { }

                    variableDeclarator("ter") {
                        child<ASTSwitchExpression> {
                            it shouldHaveType it.typeDsl.gen.`t_List{String}`
                            unspecifiedChildren(4)
                        }
                    }
                }
            }

            """
                var ter = switch(foo) {
                 case 1  -> 1;
                 case 2  -> 3;
                 default -> -1d;
                };
            """ should parseAs {
                localVarDecl {
                    modifiers { }

                    variableDeclarator("ter") {
                        child<ASTSwitchExpression> {
                            it shouldHaveType it.typeSystem.DOUBLE
                            unspecifiedChildren(4)
                        }
                    }
                }
            }

            """
                // round 2
                var ter = switch(foo) {
                 case 1  -> 1;
                 case 2  -> 3;
                 default -> -1d;
                };
            """ should parseAs {
                localVarDecl {
                    modifiers { }

                    child<ASTVariableDeclarator> {
                        variableId("ter") {
                            it::isTypeInferred shouldBe true
                            it shouldHaveType it.typeSystem.DOUBLE
                        }
                        unspecifiedChild()
                    }
                }
            }
        }
    }

    parserTestContainer("Test ternary without context promotes primitives") {
        inContext(StatementParsingCtx) {
            "var ter = true ? 1 : 3;" should parseAs {
                localVarDecl {
                    modifiers { }

                    variableDeclarator("ter") {

                        ternaryExpr {
                            it shouldHaveType it.typeSystem.INT
                            boolean(true)
                            int(1)
                            int(3)
                        }
                    }
                }
            }

            "var ter = true ? 1 : 3.0;" should parseAs {
                localVarDecl {
                    modifiers { }

                    variableDeclarator("ter") {

                        ternaryExpr {
                            it shouldHaveType it.typeSystem.DOUBLE
                            boolean(true)
                            int(1)
                            number(DOUBLE)
                        }
                    }
                }
            }

            "var ter = true ? 1 : 'c';" should parseAs {
                localVarDecl {
                    modifiers { }

                    variableDeclarator("ter") {

                        ternaryExpr {
                            it shouldHaveType it.typeSystem.CHAR
                            boolean(true)
                            int(1)
                            char('c')
                        }
                    }
                }
            }
        }
    }

    parserTest("Cast context doesn't influence standalone ternary") {
        val acu = parser.parse(
                """
class Scratch {

    static void putBoolean(byte[] b, int off, boolean val) {
        b[off] = (byte) (val ? 1 : 0);
    }
}

            """.trimIndent()
        )

        val ternary = acu.descendants(ASTConditionalExpression::class.java).firstOrThrow()

        ternary.shouldMatchN {
            ternaryExpr {
                it.typeMirror.shouldBePrimitive(INT)
                variableAccess("val")
                int(1)
                int(0)
            }
        }
    }

    parserTest("Cast context doesn't provide target type (only for lambdas)") {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
                """
            import java.util.Collection;
            import java.util.List;
            import java.util.Set;

            class Test {

                Collection<String> fun(boolean messageSelector) {
                    Collection<String> textFromMessage =
                            // compile error: Inconvertible types; cannot cast 'java.util.Collection<java.lang.Object>' to 'java.util.Collection<java.lang.String>'
                            // a cast doesn't contribute a target type,
                            // the ternary is inferred to Collection<Object>
                            (Collection<String>) (messageSelector ? emptyList() : emptySet());

                    // ok
                    return (messageSelector ? emptyList() : emptySet());
                }

                // target-type dependent methods
                <T> List<T> emptyList() {return null;}
                <T> Set<T> emptySet() {return null;}
            }
            """.trimIndent()
        )

        val (ternary1, ternary2) = acu.descendants(ASTConditionalExpression::class.java).toList()

        spy.shouldBeOk {
            ternary1 shouldHaveType gen.t_Collection[ts.OBJECT] // java.util.Collection<Object>
            ternary2 shouldHaveType gen.`t_Collection{String}` // java.util.Collection<java.lang.String>
        }
    }

    parserTest("Null branches produce null type") {
                val (acu, spy) = parser.parseWithTypeInferenceSpy(
                """
            import java.util.Collection;
            class Test {
                Collection<String> fun(boolean messageSelector) {
                    // reference ternary in assignment ctx so it takes the target type (Collection<String>)
                    return (messageSelector ? null : null);
                    // cast context isn't a target type so it has NULL_TYPE
                    return (Collection<String>) (messageSelector ? null : null);
                }
            }
           """.trimIndent()
            )

        val (ternary1, ternary2) = acu.descendants(ASTConditionalExpression::class.java).toList()

        spy.shouldBeOk {
            ternary1 shouldHaveType java.util.Collection::class[ts.STRING]
            ternary2 shouldHaveType ts.NULL_TYPE
        }
    }


    parserTestContainer("Assignment context doesn't influence standalone ternary") {
        inContext(StatementParsingCtx) {
            "double ter = true ? 1 : 3;" should parseAs {
                localVarDecl {
                    modifiers { }
                    primitiveType(DOUBLE)
                    variableDeclarator("ter") {

                        ternaryExpr {
                            it.typeMirror.shouldBePrimitive(INT)

                            boolean(true)
                            int(1)
                            int(3)
                        }
                    }
                }
            }

            "double ter = true ? new Integer(2) : 3;" should parseAs {
                localVarDecl {
                    modifiers { }
                    primitiveType(DOUBLE)
                    variableDeclarator("ter") {

                        ternaryExpr {
                            it.typeMirror.shouldBePrimitive(INT) // unboxed

                            boolean(true)
                            constructorCall {
                                it shouldHaveType it.typeSystem.INT.box()

                                unspecifiedChildren(2)
                            }
                            int(3)
                        }
                    }
                }
            }

            "double ter = true ? 1 : 3.0;" should parseAs {
                localVarDecl {
                    modifiers { }
                    primitiveType(DOUBLE)
                    variableDeclarator("ter") {

                        ternaryExpr {
                            it.typeMirror.shouldBePrimitive(DOUBLE)

                            boolean(true)
                            int(1)
                            number(DOUBLE)
                        }
                    }
                }
            }

            "double ter = true ? 1 : 'c';" should parseAs {
                localVarDecl {
                    modifiers { }
                    primitiveType(DOUBLE)
                    variableDeclarator("ter") {

                        ternaryExpr {
                            it.typeMirror.shouldBePrimitive(CHAR)

                            boolean(true)
                            int(1)
                            char('c')
                        }
                    }
                }
            }
        }
    }

    parserTestContainer("Reference ternary with context has type of its target") {
        inContext(StatementParsingCtx) {
            "Object ter = true ? String.valueOf(1) : String.valueOf(2);" should parseAs {
                localVarDecl {
                    modifiers { }
                    classType("Object")
                    variableDeclarator("ter") {

                        ternaryExpr {
                            it shouldHaveType it.typeSystem.OBJECT // not String

                            boolean(true)
                            methodCall("valueOf") {
                                it shouldHaveType it.typeSystem.STRING

                                unspecifiedChildren(2)
                            }
                            methodCall("valueOf") {
                                it shouldHaveType it.typeSystem.STRING

                                unspecifiedChildren(2)
                            }
                        }
                    }
                }
            }

            // note: a cast context is not a target type, which makes the conditional
            // use the LUB rule to determine its type.
            "String ter = (String) (Object) (true ? String.valueOf(1) : 2);" should parseAs {
                localVarDecl {
                    modifiers { }
                    classType("String")
                    variableDeclarator("ter") {

                        castExpr {
                            unspecifiedChild()
                            castExpr {
                                unspecifiedChild()

                                ternaryExpr {
                                    it shouldHaveType it.typeSystem.lub(it.typeSystem.STRING, it.typeSystem.INT)

                                    boolean(true)
                                    methodCall("valueOf") {
                                        it shouldHaveType it.typeSystem.STRING

                                        unspecifiedChildren(2)
                                    }
                                    int(2)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    parserTest("#7150 byte and short operands give short", javaVersions = JavaVersion.since(JavaVersion.J1_7)) {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class Scratch {
                void t(boolean flag, byte b, short s, Byte bb, Short ss) {
                    short c1 = flag ? b : s;
                    short c2 = flag ? bb : ss;
                    short c3 = flag ? b : ss;
                }
            }
            """.trimIndent()
        )

        val conditionals = acu.descendants(ASTConditionalExpression::class.java).toList()

        spy.shouldBeOk {
            conditionals.forEach { it shouldHaveType short }
        }
    }

    parserTest("#7150 int constant representable in byte, short or char takes that type", javaVersions = JavaVersion.since(JavaVersion.J1_7)) {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class Scratch {
                void t(boolean flag, byte b, short s, Byte bb, Character ch) {
                    final int one = 1;
                    char c1 = flag ? 'a' : 1;
                    byte c2 = flag ? b : 1;
                    short c3 = flag ? s : 1;
                    byte c4 = flag ? bb : 1;
                    char c5 = flag ? ch : 1;
                    char c6 = flag ? 'a' : one;
                }
            }
            """.trimIndent()
        )

        val conditionals = acu.descendants(ASTConditionalExpression::class.java).toList()

        spy.shouldBeOk {
            val expected = listOf(char, byte, short, byte, char, char)
            conditionals.zip(expected).forEach { (cond, type) -> cond shouldHaveType type }
        }
    }

    parserTest("#7150 int constant outside the range of the other operand gives int", javaVersions = JavaVersion.since(JavaVersion.J1_7)) {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class Scratch {
                void t(boolean flag, byte b, short s, int n) {
                    byte c1 = flag ? b : 127;
                    int c2 = flag ? b : 128;
                    short c3 = flag ? s : 32767;
                    int c4 = flag ? s : 32768;
                    char c5 = flag ? 'a' : 65535;
                    int c6 = flag ? 'a' : 65536;
                    int c7 = flag ? 'a' : -1;
                    int c8 = flag ? 'a' : n; // not a constant
                }
            }
            """.trimIndent()
        )

        val conditionals = acu.descendants(ASTConditionalExpression::class.java).toList()

        spy.shouldBeOk {
            val expected = listOf(byte, int, short, int, char, int, int, int)
            conditionals.zip(expected).forEach { (cond, type) -> cond shouldHaveType type }
        }
    }

    parserTest("#7150 diamond in a branch of a standalone conditional is inferred from its arguments", javaVersions = JavaVersion.since(JavaVersion.J1_7)) {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class Gen<T> {
                Gen(T t) { }

                void t(boolean flag) {
                    Object x = (Object) (flag ? new Gen<>("a") : new Gen<>("b"));
                }
            }
            """.trimIndent()
        )

        val (t_Gen) = acu.declaredTypeSignatures()
        val conditional = acu.descendants(ASTConditionalExpression::class.java).firstOrThrow()

        spy.shouldBeOk {
            conditional shouldHaveType t_Gen[ts.STRING]
            conditional.thenBranch shouldHaveType t_Gen[ts.STRING]
            conditional.elseBranch shouldHaveType t_Gen[ts.STRING]
        }
    }

    parserTest("#7150 char conditional selects the overload with a char parameter", javaVersions = JavaVersion.since(JavaVersion.J1_7)) {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class Cond {
                static void c(int x, int y) { }
                static void c(Integer x, char y) { }

                void t(Integer value, boolean flag) {
                    c(value, flag ? 'a' : 1);
                }
            }
            """.trimIndent()
        )

        val t_Cond = acu.firstTypeSignature()
        val call = acu.firstMethodCall()

        spy.shouldBeOk {
            call.methodType.shouldMatchMethod(
                named = "c",
                declaredIn = t_Cond,
                withFormals = listOf(int.box(), char),
                returning = void
            )
        }
    }

    parserTest("#7150 char conditional infers Character for a type parameter", javaVersions = JavaVersion.since(JavaVersion.J1_7)) {
        val (acu, spy) = parser.parseWithTypeInferenceSpy(
            """
            class Gen {
                static <T> T g(T t) { return t; }

                void t(boolean flag) {
                    g(flag ? 'a' : 1);
                }
            }
            """.trimIndent()
        )

        val call = acu.firstMethodCall()

        spy.shouldBeOk {
            call shouldHaveType char.box()
        }
    }
})
