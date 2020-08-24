/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

@file:Suppress("LocalVariableName")

package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.test.NodeSpec
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.ast.test.shouldMatchN
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol
import net.sourceforge.pmd.lang.java.types.*
import java.util.*

/**
 */
class TypeInferenceTest : ProcessorTestSpec({

    val jutil = "java.util"
    val juf = "$jutil.function"
    val justream = "$jutil.stream"

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
                        it::getReturnType shouldBe RefTypeConstants(it.typeSystem).`t_List{String}`
                        it::getTypeParameters shouldBe asList.typeParameters // not substituted
                    }

                    skipQualifier()
                    argList(1)
                }
            }
        }
    }

    parserTest("Test method invoc lub of params") {

        importedTypes += Arrays::class.java

        val call = ExpressionParsingCtx.parseNode("Arrays.asList(1, 2.0)", ctx = this) as ASTMethodCall

        val arraysClass = with(call.typeDsl) { Arrays::class.decl }
        val asList = arraysClass.getMethodsByName("asList")[0]


        call.overloadSelectionInfo.isVarargsCall shouldBe true
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
                        it shouldHaveType it.typeSystem.STRING
                    }
                    stringLit("\"b\"") {
                        it shouldHaveType it.typeSystem.STRING
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
                        it shouldHaveType it.typeSystem.BOOLEAN
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

        otherImports += "java.util.stream.*"

        inContext(ExpressionParsingCtx) {
            stream should parseAs {
                child(nodeSpec = streamSpec)
            }
        }
    }

    parserTest("Test method call chain as var initializer") {
        otherImports += "java.util.stream.*"

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

    parserTest("Test many dependencies") {

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

        logTypeInference(true)

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
interface I<S> {}
class C<Q> implements I<Q> {}

class Scratch<O> {

    <K extends I<O>> K inherited(K k) { return k; }
    
    static class Inner<T> extends Scratch<T> {
        {
            C<T> t = new C<>();
            I<T> res = inherited(t);
        }
    }
}
        """.trimIndent())

        val (t_I, t_C) = acu.declaredTypeSignatures()
        val tParam = acu.typeVariables().first { it.name == "T" }

        spy.shouldBeOk {
            // fixme this test could be better
            acu.firstMethodCall() shouldHaveType t_C[tParam] // of T, not of O
        }
    }


    parserTest("Test inference var inst substitution in enclosing ctx") {

        val (acu, spy) = parser.parseWithTypeInferenceSpy("""
import java.util.ArrayList;
import java.util.List;

class Scratch {

    static <K> K m(List<? extends K> k) { return null; }

    static <T> List<T> of(T k) { return null; }

    {
        List<String> t = new ArrayList<>();
        Object res = of(m(t));
    }
}
        """.trimIndent())

        val (ofCall, mCall) = acu.methodCalls().toList()
        val (m, of) = acu.methodDeclarations().toList { it.sig }

        spy.shouldBeOk {
            ofCall shouldHaveType gen.`t_List{String}`
            ofCall.methodType shouldBeSomeInstantiationOf of

            mCall shouldHaveType gen.t_String
            mCall.methodType shouldBeSomeInstantiationOf m
        }
    }


    parserTest("Constructor with inner class") {

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
