/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldMatchN
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.types.*
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind.DOUBLE
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind.INT
import net.sourceforge.pmd.lang.java.types.testdata.TypeInferenceTestCases


class BranchingExprsTestCases : ProcessorTestSpec({

    fun TypeSystem.stringSupplier() : JTypeMirror = with (TypeDslOf(this)) {
        java.util.function.Supplier::class[gen.t_String]
    }

    parserTest("Test ternary lets context flow") {

        asIfIn(TypeInferenceTestCases::class.java)

        inContext(ExpressionParsingCtx) {

            "makeThree(true ? () -> \"foo\" : () -> \"bar\")" should parseAs {
                methodCall("makeThree") {

                    argList {
                        ternaryExpr {
                            boolean(true)
                            child<ASTLambdaExpression> {
                                it.typeMirror shouldBe it.typeSystem.stringSupplier()
                                child<ASTLambdaParameterList> { }
                                stringLit("\"foo\"")
                            }
                            child<ASTLambdaExpression> {
                                it.typeMirror shouldBe it.typeSystem.stringSupplier()
                                child<ASTLambdaParameterList> { }
                                stringLit("\"bar\"")
                            }
                        }
                    }
                }
            }
        }
    }

    parserTest("Test ternary infers outer stuff") {

        asIfIn(TypeInferenceTestCases::class.java)

        inContext(ExpressionParsingCtx) {


            "makeThree(true ? () -> \"foo\" : () -> \"bar\")" should parseAs {

                methodCall("makeThree") {
                    argList {
                        ternaryExpr {
                            it.typeMirror shouldBe it.typeSystem.stringSupplier()

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

    parserTest("Test ternary without context lubs params") {

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
                            it.typeMirror shouldBe lubOfBothLists
                            boolean(true)
                            with(it.typeDsl) {
                                child<ASTConstructorCall>(ignoreChildren = true) {
                                    it.typeMirror shouldBe gen.`t_ArrayList{String}`
                                }
                                child<ASTConstructorCall>(ignoreChildren = true) {
                                    it.typeMirror shouldBe gen.`t_LinkedList{String}`
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    parserTest("Test switch without context lubs params") {

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

    parserTest("Test ternary without context promotes primitives") {

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
                            it shouldHaveType it.typeSystem.INT
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

        val acu = parser.parse("""
class Scratch {

    static void putBoolean(byte[] b, int off, boolean val) {
        b[off] = (byte) (val ? 1 : 0);
    }
}

        """.trimIndent())

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

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
            import java.util.Collection;
            import java.util.List;
            import java.util.Set;

            class Test {

                Collection<String> fun(boolean messageSelector) {
                    Collection<String> textFromMessage =
                            // compile error: a cast doesn't contribute a target type,
                            // the ternary is inferred to Collection<Object>
                            (Collection<String>) (messageSelector ? emptyList() : emptySet());

                    // ok
                    return (messageSelector ? emptyList() : emptySet());
                }

                // target-type dependent methods
                <T> List<T> emptyList() {return null;}
                <T> Set<T> emptySet() {return null;}
            }
        """.trimIndent())

        val (ternary1, ternary2) = acu.descendants(ASTConditionalExpression::class.java).toList()

        spy.shouldBeOk {
            ternary1 shouldHaveType java.util.Collection::class[ts.OBJECT]
            ternary2 shouldHaveType java.util.Collection::class[ts.STRING]
        }
    }

    parserTest("Null branches produce null type") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
            import java.util.Collection;
            class Test {
                Collection<String> fun(boolean messageSelector) {
                    // reference ternary in assignment ctx so it takes the target type (Collection<String>)
                    return (messageSelector ? null : null);
                    // cast context isn't a target type so it has NULL_TYPE
                    return (Collection<String>) (messageSelector ? null : null);
                }
            }
        """.trimIndent())

        val (ternary1, ternary2) = acu.descendants(ASTConditionalExpression::class.java).toList()

        spy.shouldBeOk {
            ternary1 shouldHaveType java.util.Collection::class[ts.STRING]
            ternary2 shouldHaveType ts.NULL_TYPE
        }
    }


    parserTest("Assignment context doesn't influence standalone ternary") {


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
                                it.typeMirror shouldBe it.typeSystem.INT.box()

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
                            it.typeMirror.shouldBePrimitive(INT)

                            boolean(true)
                            int(1)
                            char('c')
                        }
                    }
                }
            }
        }
    }

    parserTest("Reference ternary with context has type of its target") {

        inContext(StatementParsingCtx) {

            "Object ter = true ? String.valueOf(1) : String.valueOf(2);" should parseAs {
                localVarDecl {
                    modifiers { }
                    classType("Object")
                    variableDeclarator("ter") {

                        ternaryExpr {
                            it.typeMirror shouldBe it.typeSystem.OBJECT // not String

                            boolean(true)
                            methodCall("valueOf") {
                                it.typeMirror shouldBe it.typeSystem.STRING

                                unspecifiedChildren(2)
                            }
                            methodCall("valueOf") {
                                it.typeMirror shouldBe it.typeSystem.STRING

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
                                    it.typeMirror shouldBe it.typeSystem.lub(it.typeSystem.STRING, it.typeSystem.INT)

                                    boolean(true)
                                    methodCall("valueOf") {
                                        it.typeMirror shouldBe it.typeSystem.STRING

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

})
