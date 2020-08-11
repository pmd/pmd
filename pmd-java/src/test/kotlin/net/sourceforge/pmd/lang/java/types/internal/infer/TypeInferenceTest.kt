/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

@file:Suppress("LocalVariableName")

package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import net.sourceforge.pmd.lang.ast.test.NodeSpec
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBeA
import net.sourceforge.pmd.lang.ast.test.shouldMatchN
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.ast.ParserTestSpec.GroupTestCtx.VersionedTestCtx
import net.sourceforge.pmd.lang.java.types.*
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind.INT
import net.sourceforge.pmd.lang.java.types.internal.infer.ast.JavaExprMirrors
import net.sourceforge.pmd.lang.java.types.testdata.TypeInferenceTestCases
import java.util.*
import java.util.function.BiFunction
import java.util.function.BinaryOperator
import java.util.function.Supplier
import java.util.stream.Stream

/**
 */
class TypeInferenceTest : ProcessorTestSpec({

    val jutil = "java.util"
    val juf = "$jutil.function"
    val justream = "$jutil.stream"
    val jlang = "java.lang"

    fun ASTMethodCall.computeInferenceResult(): JMethodSig {
        val testInfer = Infer(typeSystem, 8, TypeInferenceLogger.noop())
        val mirrorFactory = JavaExprMirrors(testInfer)

        val mirror = mirrorFactory.getMirror(this) as ExprMirror.InvocationMirror

        mirror.enclosingType shouldNotBe null

        val site = testInfer.newCallSite(mirror, null)

        return testInfer.determineInvocationTypeResult(site)
    }

    parserTest("Test method invoc resolution") {

        val call = parseExpr<ASTMethodCall>("$jutil.Arrays.asList(\"a\")")

        val arraysClass = with(call.typeDsl) { Arrays::class.decl }
        val asList = arraysClass.getMethodsByName("asList")[0]

        call.computeInferenceResult().also {
            it::getName shouldBe "asList"
            it::isVarargs shouldBe true
            it.formalParameters[0].shouldBeA<JArrayType> {
                it.componentType shouldBe with(call.typeDsl) { String::class.decl }
            }
            it::getReturnType shouldBe TypeGen(call.typeSystem).`t_List{String}`
            it::getTypeParameters shouldBe asList.typeParameters // not substituted
        }
    }

    parserTest("Test method invoc lub of params") {

        val call = parseExpr<ASTMethodCall>("$jutil.Arrays.asList(1, 2.0)")

        val arraysClass = with(call.typeDsl) { Arrays::class.decl }
        val asList = arraysClass.getMethodsByName("asList")[0]


        call.computeInferenceResult().also {
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
        //  public static <T, L extends List<T>> L appendL(List<? extends T> in, L top)

        val call = parseExpr<ASTMethodCall>("appendL($jutil.Arrays.asList(\"a\"), new $jutil.ArrayList<>())")
        call.computeInferenceResult().also {
            it.name shouldBe "appendL"
            with(call.typeDsl) {

                it.formalParameters[0] shouldBe gen.`t_List{? extends String}`
                it.formalParameters[1] shouldBe gen.`t_ArrayList{String}`
                it.isVarargs shouldBe false
                it.returnType shouldBe gen.`t_ArrayList{String}`
            }
        }
    }

    parserTest("Test method resolution with lambda param") {

        asIfIn(TypeInferenceTestCases::class.java)

        //  defined as
        //  public static <T, L extends List<T>> L appendL(List<? extends T> in, L top)

        val call = parseExpr<ASTMethodCall>("makeThree(() -> \"foo\")")

        call.computeInferenceResult().also {
            it.name shouldBe "makeThree"
            with(call.typeDsl) {
                it.formalParameters[0] shouldBe Supplier::class[gen.t_String]
                it.returnType shouldBe gen.`t_List{String}`
            }
        }
    }


    val stream =
            """$jutil.stream.Stream.of("a", "b")
                                   .map(it -> it.isEmpty())
                                   .collect($jutil.stream.Collectors.toList())
                """.trimIndent()

    val streamSpec: NodeSpec<ASTMethodCall> = {

        val streamSym = it.typeSystem.getClassSymbol(Stream::class.java)!!

        it::getMethodName shouldBe "collect"
        it.typeMirror shouldBe with(it.typeDsl) { gen.t_List[boolean.box()] } // List<Boolean>
        it::getQualifier shouldBe child<ASTMethodCall> {
            it::getMethodName shouldBe "map"
            it.typeMirror shouldBe with(it.typeDsl) { gen.t_Stream[boolean.box()] } // Stream<Boolean>
            it::getQualifier shouldBe child<ASTMethodCall> {
                it::getMethodName shouldBe "of"
                it.typeMirror shouldBe with(it.typeDsl) { gen.t_Stream[gen.t_String] } // Stream<String>
                it::getQualifier shouldBe typeExpr {
                    qualClassType("$jutil.stream.Stream")
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

    parserTest("Test call chain with method reference") {

        asIfIn(TypeInferenceTestCases::class.java)

        val chain = """$jutil.stream.Stream.of("")
                                                    .map($jlang.String::isEmpty)
                                                    .collect($jutil.stream.Collectors.toList())
                """.trimIndent()

        inContext(ExpressionParsingCtx) {


            chain should parseAs {
                methodCall("collect") {
                    it.typeMirror shouldBe with(it.typeDsl) { gen.t_List[boolean.box()] }
                    it::getQualifier shouldBe child<ASTMethodCall> {
                        it::getMethodName shouldBe "map"
                        it.typeMirror shouldBe with(it.typeDsl) { gen.t_Stream[boolean.box()] }
                        it::getQualifier shouldBe child<ASTMethodCall> {
                            it::getMethodName shouldBe "of"
                            it.typeMirror shouldBe with(it.typeDsl) { gen.t_Stream[gen.t_String] }
                            it::getQualifier shouldBe typeExpr {
                                qualClassType("$jutil.stream.Stream")
                            }

                            it::getArguments shouldBe child {
                                stringLit("\"\"")
                            }
                        }

                        it::getArguments shouldBe child {

                            methodRef("isEmpty") {

                                with(it.typeDsl) {
                                    val `t_Function{String, Boolean}` = gen.t_Function[gen.t_String, boolean.box()]

                                    it.typeMirror shouldBe `t_Function{String, Boolean}`
                                    it.referencedMethod.shouldMatchMethod(named = "isEmpty", declaredIn = gen.t_String, withFormals = emptyList(), returning = boolean)
                                    // Function<String, Boolean>.apply(String) -> Boolean
                                    it.functionalMethod.shouldMatchMethod(named = "apply", declaredIn = `t_Function{String, Boolean}`, withFormals = listOf(gen.t_String), returning = boolean.box())
                                }

                                typeExpr {
                                    qualClassType("$jlang.String")
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
    }

    parserTest("Test call chain with constructor reference") {

        asIfIn(TypeInferenceTestCases::class.java)

        val chain = """$jutil.stream.Stream.of(1, 2)
                                                    .map(int[]::new)
                                                    .collect($jutil.stream.Collectors.toList())
                """.trimIndent()

        inContext(ExpressionParsingCtx) {

            chain should parseAs {
                methodCall("collect") {
                    it::getMethodName shouldBe "collect"
                    it.typeMirror shouldBe with (it.typeDsl) { gen.t_List[int.toArray() ]} // List<int[]>
                    it::getQualifier shouldBe child<ASTMethodCall> {
                        it::getMethodName shouldBe "map"
                        it.typeMirror shouldBe with (it.typeDsl) { gen.t_Stream[int.toArray() ]} // Stream<int[]>
                        it::getQualifier shouldBe child<ASTMethodCall> {
                            it::getMethodName shouldBe "of"
                            it.typeMirror shouldBe with (it.typeDsl) { gen.t_Stream[int.box()]} // Stream<Integer>
                            it::getQualifier shouldBe typeExpr {
                                qualClassType("$jutil.stream.Stream")
                            }

                            it::getArguments shouldBe child {
                                int(1)
                                int(2)
                            }
                        }

                        it::getArguments shouldBe child {

                            // Function<Integer, int[]>
                            val `t_Function{Integer, Array{int}}` = with(it.typeDsl) { gen.t_Function[int.box(), int.toArray()] }

                            constructorRef {
                                it.typeMirror shouldBe `t_Function{Integer, Array{int}}`
                                with(it.typeDsl) {
                                    it.referencedMethod.shouldMatchMethod(named = "new", declaredIn = int.toArray(), /* int[]*/ withFormals = listOf(int), returning = int.toArray())
                                    it.functionalMethod.shouldMatchMethod(named = "apply", declaredIn = `t_Function{Integer, Array{int}}`, withFormals = listOf(int.box()), returning = int.toArray())
                                }

                                typeExpr {
                                    arrayType {
                                        primitiveType(INT)
                                        arrayDimList()
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
    }


    parserTest("Test call chain with array method reference") {

        asIfIn(TypeInferenceTestCases::class.java)

        val chain = """$jutil.stream.Stream.<int[]>of(new int[0])
                                                    .map(int[]::clone)
                """.trimIndent()


        inContext(ExpressionParsingCtx) {
            chain should parseAs {
                methodCall("map") {

                    it.typeMirror shouldBe with(it.typeDsl) { gen.t_Stream[int.toArray()] }

                    it::getQualifier shouldBe child<ASTMethodCall> {
                        it::getMethodName shouldBe "of"
                        it.typeMirror shouldBe with(it.typeDsl) { gen.t_Stream[int.toArray()] }

                        it::getQualifier shouldBe typeExpr {
                            qualClassType("$jutil.stream.Stream")
                        }

                        it::getExplicitTypeArguments shouldBe child {
                            arrayType()
                        }

                        it::getArguments shouldBe child {
                            unspecifiedChild()
                        }
                    }

                    it::getArguments shouldBe argList {

                        methodRef("clone") {

                            with(it.typeDsl) {
                                // Function<int[], int[]>
                                val `t_Function{Array{int}, Array{int}}` = gen.t_Function[int.toArray(), int.toArray()]

                                it.typeMirror shouldBe `t_Function{Array{int}, Array{int}}`
                                it.referencedMethod.shouldMatchMethod(named = "clone", declaredIn = int.toArray(), withFormals = emptyList(), returning = int.toArray())
                                it.functionalMethod.shouldMatchMethod(named = "apply", declaredIn = `t_Function{Array{int}, Array{int}}`, withFormals = listOf(int.toArray()), returning = int.toArray())
                            }

                            typeExpr {
                                arrayType {
                                    primitiveType(INT)
                                    arrayDimList()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    parserTest("Test method reference overload resolution") {

        asIfIn(TypeInferenceTestCases::class.java)
        val stringBuilder = "$jlang.StringBuilder"

        val chain = """$jutil.stream.Stream.of("", 4)
                                           .reduce(new $stringBuilder(), $stringBuilder::append, $stringBuilder::append)
                    """.trimIndent()

        inContext(ExpressionParsingCtx) {


            chain should parseAs {
                methodCall("reduce") {


                    // we can't hardcode the lub because it is jdk specific
                    val serialLub = with (it.typeDsl) {
                        ts.lub(gen.t_String, gen.t_Integer)
                    }

                    val t_BiFunction = with (it.typeDsl) { ts.getClassSymbol(BiFunction::class.java)!! }
                    val t_BinaryOperator = with (it.typeDsl) { ts.getClassSymbol(BinaryOperator::class.java)!! }
                    val t_Sb = with (it.typeDsl) { gen.t_StringBuilder }

                    with (it.typeDsl) {

                        it.typeMirror shouldBe t_Sb
                        it.methodType.shouldMatchMethod(
                                named = "reduce",
                                declaredIn = gen.t_Stream[serialLub],
                                withFormals = listOf(
                                        t_Sb,
                                        t_BiFunction[t_Sb, `?` `super` serialLub, t_Sb],
                                        t_BinaryOperator[t_Sb]
                                ),
                                returning = t_Sb
                        )
                    }

                    it::getQualifier shouldBe child<ASTMethodCall> {
                        it::getMethodName shouldBe "of"
                        it.typeMirror shouldBe with(it.typeDsl) { gen.t_Stream[serialLub] }
                        it::getQualifier shouldBe typeExpr {
                            qualClassType("$jutil.stream.Stream")
                        }

                        it::getArguments shouldBe child {
                            unspecifiedChildren(2)
                        }
                    }

                    it::getArguments shouldBe child {
                        child<ASTConstructorCall>(ignoreChildren = true) {
                        }

                        methodRef("append") {
                            with (it.typeDsl) {
                                val myBifunction = t_BiFunction[t_Sb, serialLub, t_Sb]

                                it.typeMirror shouldBe myBifunction
                                it.referencedMethod.shouldMatchMethod(named = "append", declaredIn = t_Sb, withFormals = listOf(ts.OBJECT), returning = t_Sb)
                                it.functionalMethod.shouldMatchMethod(named = "apply", declaredIn = myBifunction, withFormals = listOf(t_Sb, serialLub), returning = t_Sb)
                            }

                            typeExpr {
                                qualClassType(stringBuilder)
                            }
                        }

                        methodRef("append") {

                            with (it.typeDsl) {
                                val myBifunction = t_BiFunction[t_Sb, t_Sb, t_Sb]

                                it.typeMirror shouldBe t_BinaryOperator[t_Sb]
                                // notice it's more specific than the first append (CharSequence formal)
                                it.referencedMethod.shouldMatchMethod(named = "append", declaredIn = t_Sb, withFormals = listOf(gen.t_CharSequence), returning = t_Sb)
                                // notice the owner of the function is BiFunction and not BinaryOperator. It's inherited by BinaryOperator
                                it.functionalMethod.shouldMatchMethod(named = "apply", declaredIn = myBifunction, withFormals = listOf(t_Sb, t_Sb), returning = t_Sb)
                            }

                            typeExpr {
                                qualClassType(stringBuilder)
                            }
                        }
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
                                    gen.t_Iterable[`?` extends tvar]
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

    parserTest("Test method ref with this as LHS") {


        val acu = parser.parse("""
            
            package scratch;

            import static java.util.stream.Collectors.joining;

            import java.util.Comparator;
            import java.util.Deque;

            class Archive {
                
                private String getName() {
                    return "foo";
                }

                private String toInversePath(Deque<Archive> path) {
                    return path.stream()
                               .map(Archive::getName)
                               .collect(joining(" <- "));
                }

                private Comparator<Deque<Archive>> comparator() {
                    return Comparator.<Deque<Archive>, String>
                        comparing(deque -> deque.peekFirst().getName())
                        .thenComparingInt(Deque::size)
                        .thenComparing(this::toInversePath);
                }

            }
        """.trimIndent())

        val thisToInversePath = acu.descendants(ASTMethodReference::class.java)[2]!!

        thisToInversePath.shouldMatchN {
            methodRef("toInversePath") {
                it.functionalMethod.toString() shouldBe "java.util.function.Function<java.util.Deque<scratch.Archive>, java.lang.String>.apply(java.util.Deque<scratch.Archive>) -> java.lang.String"
                thisExpr()
            }
        }
    }

    parserTest("Test method ref with void return type") {


        val acu = parser.parse("""
            import java.util.Optional;
            class Archive {

                private String getName() {
                    Optional.of(this).ifPresent(Archive::getName);
                    return "foo";
                }
            }
        """.trimIndent())


        val getName = acu.descendants(ASTMethodDeclaration::class.java).first()!!
        val thisToInversePath = acu.descendants(ASTMethodCall::class.java).first()!!

        thisToInversePath.shouldMatchN {
            methodCall("ifPresent") {
                unspecifiedChild()
                argList {
                    methodRef("getName") {
                        it.functionalMethod.toString() shouldBe "$juf.Consumer<Archive>.accept(Archive) -> void"
                        it.referencedMethod.toString() shouldBe "Archive.getName() -> $jlang.String"
                        it.referencedMethod.symbol shouldBe getName.symbol

                        typeExpr {
                            classType("Archive")
                        }
                    }
                }
            }
        }
    }


    parserTest("Test method ref in stream 2") {

        logTypeInference(verbose = true)

        val acu = parser.parse("""
            import java.util.Optional;
            import java.util.List;
            import java.util.stream.Stream;
            import java.util.Objects;

            class Archive {

                private static void loopChecks2(List<Archive> step, List<Archive> pred, List<Archive> fini) {
                    Stream.of(step, pred, fini).flatMap(List::stream).filter(Objects::nonNull).anyMatch(it -> true);
                }

            }
        """.trimIndent())


        val t_Archive = acu.descendants(ASTClassOrInterfaceDeclaration::class.java).firstOrThrow().typeMirror
        val anyMatch = acu.descendants(ASTMethodCall::class.java).first()!!

        anyMatch.shouldMatchN {
            methodCall("anyMatch") {
                it::getTypeMirror shouldBe it.typeSystem.BOOLEAN

                methodCall("filter") {

                    it::getTypeMirror shouldBe with (it.typeDsl) { gen.t_Stream[t_Archive] }

                    methodCall("flatMap") {

                        it::getTypeMirror shouldBe with (it.typeDsl) { gen.t_Stream[t_Archive] }

                        methodCall("of") {
                            it::getQualifier shouldBe unspecifiedChild()

                            it::getTypeMirror shouldBe with (it.typeDsl) { gen.t_Stream[gen.t_List[t_Archive]] }

                            argList(3)
                        }

                        argList(1)
                    }

                    argList(1)
                }

                argList(1)
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

    parserTest("Test getClass special type") {


        val acu = parser.parse("""
            import java.util.function.Function;
            import java.util.function.Supplier;

            class Scratch<K> {

                <T> T sup(Supplier<Class<T>> t) { return null; }
                <T> T id(Function<T, Class<T>> t) { return null; }

                {
                    Scratch<K> k = this.sup(this::getClass);
                    Scratch<K> k2 = this.id(Scratch<K>::getClass);
                    Scratch raw = this.id(Scratch::getClass); //error

                    k.getClass();
                }
            }

        """.trimIndent())

        val t_Scratch = acu.descendants(ASTAnyTypeDeclaration::class.java).firstOrThrow().typeMirror!!

        val (k, k2, raw, call) = acu.descendants(ASTMethodCall::class.java).toList()

        val normalizer = CaptureNormalizer()

        doTest("Test this::getClass") {
            k.shouldMatchN {
                methodCall("sup") {
                    thisExpr { it.typeMirror shouldBe t_Scratch; null }
                    argList {
                        methodRef("getClass") {
                            thisExpr()

                            normalizer.normalizeCaptures(it.typeMirror.toString())
                                    .shouldBe("$juf.Supplier<$jlang.Class<capture#1 of ? extends ${t_Scratch.erasure}>>")
                        }
                    }
                }
            }
        }

        doTest("Test Scratch<K>::getClass") {
            k2.shouldMatchN {
                methodCall("id") {
                    thisExpr()
                    argList {
                        methodRef("getClass") {
                            typeExpr {
                                classType("Scratch")
                            }

                            normalizer.normalizeCaptures(it.typeMirror.toString())
                                    .shouldBe("$juf.Function<capture#2 of ? extends Scratch, $jlang.Class<capture#2 of ? extends Scratch>>")
                        }
                    }
                }
            }
        }

        doTest("Test method call") {
            call.shouldMatchN {
                methodCall("getClass") {

                    it::getTypeMirror shouldBe with (it.typeDsl) {
                        Class::class[`?` extends t_Scratch.erasure]
                    }

                    variableAccess("k")
                    argList {}
                }
            }
        }
    }


})

private inline fun <reified T : ASTExpression> VersionedTestCtx.parseExpr(exprStr: String) =
        ExpressionParsingCtx.parseNode(exprStr, this, parser.withProcessing()) as T
