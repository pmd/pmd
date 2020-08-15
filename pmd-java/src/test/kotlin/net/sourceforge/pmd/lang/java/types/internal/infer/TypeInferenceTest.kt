/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

@file:Suppress("LocalVariableName")

package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.NodeSpec
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.ast.test.shouldMatchN
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.ast.ParserTestSpec.GroupTestCtx.VersionedTestCtx
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol
import net.sourceforge.pmd.lang.java.types.*
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind.INT
import net.sourceforge.pmd.lang.java.types.testdata.TypeInferenceTestCases
import java.util.*
import java.util.function.Supplier
import kotlin.collections.ArrayList

/**
 */
class TypeInferenceTest : ProcessorTestSpec({

    val jutil = "java.util"
    val juf = "$jutil.function"
    val justream = "$jutil.stream"
    val jlang = "java.lang"

    parserTest("Test method invoc resolution") {

        importedTypes += Arrays::class.java

        inContext(ExpressionParsingCtx) {

            "Arrays.asList(\"a\")" should parseAs {

                methodCall("asList") {
                    val arraysClass = with(it.typeDsl) { java.util.Arrays::class.decl }
                    val asList = arraysClass.getMethodsByName("asList")[0]

                    it.methodType.also {
                        it::getName shouldBe "asList"
                        it::isVarargs shouldBe true
                        it.formalParameters[0].shouldBeA<JArrayType> {
                            it.componentType shouldBe it.typeSystem.STRING
                        }
                        it::getReturnType shouldBe TypeGen(it.typeSystem).`t_List{String}`
                        it::getTypeParameters shouldBe asList.typeParameters // not substituted
                    }

                    it::getQualifier shouldBe unspecifiedChild()
                    argList(1)
                }
            }
        }
    }

    parserTest("Test method invoc lub of params") {

        importedTypes += Arrays::class.java

        val call = parseExpr<ASTMethodCall>("Arrays.asList(1, 2.0)")

        val arraysClass = with(call.typeDsl) { Arrays::class.decl }
        val asList = arraysClass.getMethodsByName("asList")[0]


        call.methodType.also {
            it.isVarargs shouldBe true
            val (formal, ret) = with(TypeDslOf(it.typeSystem)) {
                // we can't hardcode the lub result because it is JDK specific
                val `t_lub(Double, Integer)` = ts.lub(double.box(), int.box())

                Pair(
                        `t_lub(Double, Integer)`,
                        gen.t_List[`t_lub(Double, Integer)`]
                )
            }

            it.formalParameters[0].shouldBeA<JArrayType> {
                it.componentType shouldBe formal
            }

            it.returnType shouldBe ret
            it.typeParameters shouldBe asList.typeParameters // not substituted
        }
    }

    parserTest("Test method invoc resolution, nested invocation exprs") {

        asIfIn(TypeInferenceTestCases::class.java)

        //  defined as
        //  public static <K, L extends List<K>> L appendL(List<? extends K> in, L top)

        val call = parseExpr<ASTMethodCall>("appendL(Arrays.asList(\"a\"), new ArrayList<>())")
        call.methodType.also {
            it.name shouldBe "appendL"
            with(call.typeDsl) {

                assertSoftly {

                    it.formalParameters[0] shouldBe gen.`t_List{? extends String}`
                    it.formalParameters[1] shouldBe gen.`t_ArrayList{String}`
                    it.isVarargs shouldBe false
                    it.returnType shouldBe gen.`t_ArrayList{String}`

                }
            }
        }
    }

    parserTest("Test method resolution with lambda param") {

        asIfIn(TypeInferenceTestCases::class.java)

        //  defined as
        //  public static <T, L extends List<T>> L appendL(List<? extends T> in, L top)

        val call = parseExpr<ASTMethodCall>("makeThree(() -> \"foo\")")

        call.methodType.also {
            it.name shouldBe "makeThree"
            with(call.typeDsl) {
                it.formalParameters[0] shouldBe Supplier::class[gen.t_String]
                it.returnType shouldBe gen.`t_List{String}`
            }
        }
    }


    val stream =
            """Stream.of("a", "b")
                     .map(it -> it.isEmpty())
                     .collect(Collectors.toList())
                """.trimIndent()

    val streamSpec: NodeSpec<ASTMethodCall> = {

        it::getMethodName shouldBe "collect"
        it.typeMirror shouldBe with(it.typeDsl) { gen.t_List[boolean.box()] } // List<Boolean>
        it::getQualifier shouldBe child<ASTMethodCall> {
            it::getMethodName shouldBe "map"
            it.typeMirror shouldBe with(it.typeDsl) { gen.t_Stream[boolean.box()] } // Stream<Boolean>
            it::getQualifier shouldBe child<ASTMethodCall> {
                it::getMethodName shouldBe "of"
                it.typeMirror shouldBe with(it.typeDsl) { gen.t_Stream[gen.t_String] } // Stream<String>
                it::getQualifier shouldBe typeExpr {
                    classType("Stream")
                }

                it::getArguments shouldBe child {
                    stringLit("\"a\"") {
                        it::getTypeMirror shouldBe it.typeSystem.STRING
                    }
                    stringLit("\"b\"") {
                        it::getTypeMirror shouldBe it.typeSystem.STRING
                    }
                }
            }

            it::getArguments shouldBe child {
                child<ASTLambdaExpression> {

                    val `t_Function{String, Boolean}` = with(it.typeDsl) { gen.t_Function[gen.t_String, boolean.box()] }

                    it.typeMirror shouldBe `t_Function{String, Boolean}`
                    with(it.typeDsl) {
                        it.functionalMethod.shouldMatchMethod(
                                named = "apply",
                                declaredIn = `t_Function{String, Boolean}`,
                                withFormals = listOf(gen.t_String),
                                returning = boolean.box()
                        )
                    }

                    child<ASTLambdaParameterList> {
                        child<ASTLambdaParameter> {
                            localVarModifiers { }
                            variableId("it")
                        }
                    }
                    it::getExpression shouldBe child<ASTMethodCall> {
                        it::getTypeMirror shouldBe it.typeSystem.BOOLEAN
                        it::getQualifier shouldBe variableAccess("it") {
                            it.typeMirror shouldBe it.typeSystem.STRING
                        }
                        it::getArguments shouldBe child {}
                    }
                }
            }
        }
        it::getArguments shouldBe child {
            unspecifiedChild()
        }
    }


    parserTest("Test method call chain") {

        asIfIn(TypeInferenceTestCases::class.java)

        inContext(ExpressionParsingCtx) {
            stream should parseAs {
                child(nodeSpec = streamSpec)
            }
        }
    }

    parserTest("Test method call chain as var initializer") {
        asIfIn(TypeInferenceTestCases::class.java)

        inContext(StatementParsingCtx) {
            "var foo = $stream;" should parseAs {
                localVarDecl {
                    modifiers { }

                    it::isTypeInferred shouldBe true
                    varDeclarator {
                        variableId("foo") {
                            it.typeMirror shouldBe with(it.typeDsl) { gen.t_List[boolean.box()] }
                        }

                        child(nodeSpec = streamSpec)
                    }
                }
            }
        }
    }

    parserTest("Test local var inference") {

        asIfIn(TypeInferenceTestCases::class.java)

        val chain = """
            {
                var map = new java.util.HashMap<Object, int[]>(((4 * convCount) / 3) + 1);
                for (var entry : map.entrySet()) {
                    int[] positions = entry.getValue();
                }
            }
        """.trimIndent()


        inContext(StatementParsingCtx) {
            chain should parseAs {
                block {

                    localVarDecl {
                        localVarModifiers {  }
                        variableDeclarator("map") {
                            constructorCall()
                        }
                    }

                    foreachLoop {
                        localVarDecl {
                            localVarModifiers {  }
                            variableDeclarator("entry") {
                            }
                        }
                        methodCall("entrySet") {
                            variableAccess("map") {
                                it::getTypeMirror shouldBe with (it.typeDsl) {
                                    java.util.HashMap::class[ts.OBJECT, ts.INT.toArray(1)]
                                }
                            }
                            argList(0)
                        }
                        block {
                            localVarDecl {
                                localVarModifiers {  }
                                arrayType { primitiveType(INT); arrayDimList() }
                                variableDeclarator("positions") {
                                    methodCall("getValue") {
                                        it::getTypeMirror shouldBe with (it.typeDsl) {
                                            ts.INT.toArray(1)
                                        }

                                        variableAccess("entry") {
                                            it::getTypeMirror shouldBe with (it.typeDsl) {
                                                java.util.Map.Entry::class[ts.OBJECT, ts.INT.toArray(1)]
                                            }
                                        }
                                        argList(0)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    parserTest("Test for var inference projection") {

        asIfIn(TypeInferenceTestCases::class.java)

        inContext(TypeBodyParsingCtx) {
            """
            static <T> void take5(Iterable<? extends T> iter) {
                for (var entry : iter) { } // entry is projected to `T`, not `? extends T`
            }
        """.trimIndent() should parseAs {
                methodDecl {
                    modifiers()
                    val tparams = typeParamList(1)
                    val tvar = tparams.getChild(0)!!.typeMirror

                    voidResult()
                    formalsList(1)
                    block {
                        foreachLoop {
                            localVarDecl {
                                localVarModifiers {  }
                                child<ASTVariableDeclarator> {
                                    variableId("entry") {
                                        it.typeMirror shouldBe tvar // not ? extends T
                                    }
                                }
                            }
                            variableAccess("iter") {
                                it.typeMirror shouldBe with(it.typeDsl) {
                                    gen.t_Iterable[captureMatcher(`?` extends tvar)]
                                }
                            }
                            block {}
                        }
                    }
                }
            }
        }
    }

    parserTest("Test void compatible lambda") {

        asIfIn(TypeInferenceTestCases::class.java)
        inContext(StatementParsingCtx) {

            """
             final $jlang.Runnable pr = 0 == null ? null : () -> id(true);
        """ should parseAs {
                localVarDecl {
                    localVarModifiers {  }
                    qualClassType("$jlang.Runnable")
                    variableDeclarator("pr") {
                        child<ASTConditionalExpression> {
                            unspecifiedChildren(2)
                            exprLambda {
                                it.typeMirror shouldBe with(it.typeDsl) { java.lang.Runnable::class.decl }

                                lambdaFormals(0)
                                methodCall("id")
                            }
                        }
                    }
                }
            }
        }
    }

    parserTest("Test many dependencies") {

        asIfIn(TypeInferenceTestCases::class.java)
        inContext(StatementParsingCtx) {

            """
             final $jutil.Map<String, String> map = $justream.Stream.of("de", "").collect($justream.Collectors.toMap($juf.Function.identity(), $juf.Function.identity()));
        """ should parseAs {
                localVarDecl {
                    localVarModifiers { }
                    unspecifiedChild()
                    variableDeclarator("map") {
                        methodCall("collect") {
                            it.typeMirror shouldBe with(it.typeDsl) {
                                java.util.Map::class[ts.STRING, ts.STRING]
                            }
                            unspecifiedChild()
                            argList {
                                methodCall("toMap") {
                                    unspecifiedChild()
                                    argList {
                                        methodCall("identity") {
                                            unspecifiedChildren(2)
                                            it.typeMirror shouldBe with(it.typeDsl) {
                                                java.util.function.Function::class[ts.STRING, ts.STRING]
                                            }
                                        }
                                        methodCall("identity") {
                                            unspecifiedChildren(2)
                                            it.typeMirror shouldBe with(it.typeDsl) {
                                                java.util.function.Function::class[ts.STRING, ts.STRING]
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    parserTest("Test type var bound substitution in inherited members") {


        val acu = parser.parse("""
import java.util.ArrayList;
import java.util.List;

class Scratch<O> {
    
    
    <K extends List<O>> K inherited(K k) {
        return k;
    }
    
    static class Inner<T> extends Scratch<T> {
        {
            ArrayList<T> t = new ArrayList<>();
            List<T> res = inherited(t);
        }
    }
}
        """.trimIndent())

        val tParam = acu.descendants(ASTTypeParameter::class.java).first { it.parameterName == "T" }!!.typeMirror

        acu.descendants(ASTMethodCall::class.java)
                .firstOrThrow()
                .shouldMatchN {
                    methodCall("inherited") {
                        it.typeMirror shouldBe with(it.typeDsl) { java.util.ArrayList::class[tParam] }
                        argList {
                            variableAccess("t")
                        }
                    }
                }

    }


    parserTest("Test inference var inst substitution in enclosing ctx") {

        val acu = parser.parse("""
import java.util.ArrayList;
import java.util.List;

class Scratch {

    static <K> K m(List<? extends K> k) {
        return null;
    }

    static <T> List<T> of(T k) {
        return null;
    }

    {
        List<String> t = new ArrayList<>();
        Object res = of(m(t));
    }
}
        """.trimIndent())

        acu.descendants(ASTMethodCall::class.java)
                .firstOrThrow()
                .shouldMatchN {
                    methodCall("of") {
                        it.typeMirror shouldBe with(it.typeDsl) { gen.`t_List{String}` }
                        argList {
                            methodCall("m") {
                                it.typeMirror shouldBe with(it.typeDsl) { gen.t_String }
                                argList {
                                    variableAccess("t")
                                }
                            }
                        }
                    }
                }

    }


    parserTest("Constructor with inner class") {

        logTypeInference(true)

        val acu = parser.parse("""
import java.util.Iterator;
import java.util.Map;

class MyMap<K, V> {


    Iterator<K> descendingKeyIterator() {
        return new KeyIter(lo(), hi());
    }

    Entry lo() {return null;}

    Entry hi() {return null;}

    class Entry implements Map.Entry<K,V> { }

    class KeyIter implements Iterator<K> {

        <E extends Map.Entry<? extends K, ? extends V>>

        KeyIter(E lo, E hi) {}

        @Override
        public boolean hasNext() {return false;}

        @Override
        public K next() {return null;}
    }
}

        """.trimIndent())

        val (t_MyMap, t_MyMapEntry, t_KeyIter) = acu.descendants(ASTAnyTypeDeclaration::class.java).toList { it.typeMirror }
        val (kvar, vvar) = acu.descendants(ASTTypeParameter::class.java).toList { it.typeMirror }

        val ctorCall = acu.descendants(ASTConstructorCall::class.java).firstOrThrow()

        ctorCall.shouldMatchN {
            constructorCall {
                val `t_MyMap{K,V}KeyIter`: JClassType
                val `t_MyMap{K,V}Entry`: JClassType

                with(it.typeDsl) {
                    `t_MyMap{K,V}KeyIter` = t_MyMap[kvar, vvar].selectInner(t_KeyIter.symbol, emptyList())
                    `t_MyMap{K,V}Entry` = t_MyMap[kvar, vvar].selectInner(t_MyMapEntry.symbol, emptyList())

                    it.methodType.shouldMatchMethod(
                            named = JConstructorSymbol.CTOR_NAME,
                            declaredIn = `t_MyMap{K,V}KeyIter`,
                            withFormals = listOf(`t_MyMap{K,V}Entry`, `t_MyMap{K,V}Entry`),
                            returning = `t_MyMap{K,V}KeyIter`
                    )
                }

                it::getTypeNode shouldBe classType("KeyIter") {
                    it.typeMirror shouldBe `t_MyMap{K,V}KeyIter`
                }

                argList(2)
            }
        }

    }

})

private inline fun <reified T : ASTExpression> VersionedTestCtx.parseExpr(exprStr: String) =
        ExpressionParsingCtx.parseNode(exprStr, this, parser.withProcessing()) as T
