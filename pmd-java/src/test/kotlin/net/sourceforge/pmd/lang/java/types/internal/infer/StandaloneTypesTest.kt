/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.arbitrary.filterNot
import io.kotest.property.checkAll
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldMatchN
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.ast.BinaryOp.*
import net.sourceforge.pmd.lang.java.types.*
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind.*

class StandaloneTypesTest : ProcessorTestSpec({

    parserTest("Test array clone") {

        inContext(ExpressionParsingCtx) {

            fun matchArrayClone(compType: TypeDslMixin.() -> JTypeMirror) =
                    parseAs {
                        methodCall("clone") {
                            with(it.typeDsl) {
                                val t = compType()
                                val tArray = t.toArray()
                                it.methodType.shouldMatchMethod(named = "clone", declaredIn = tArray, withFormals = emptyList(), returning = tArray)
                                it.typeMirror shouldBe tArray

                                it::getQualifier shouldBe child<ASTArrayAllocation>(ignoreChildren = true) {
                                    it.typeMirror shouldBe tArray
                                }

                                it::getArguments shouldBe argList(0)
                            }
                        }
                    }

            "new int[0].clone()" should matchArrayClone { int }
            "new String[0].clone()" should matchArrayClone { gen.t_String }
            "new String[0][].clone()" should matchArrayClone { gen.t_String.toArray() }
        }
    }

    parserTest("Test array length") {

        inContext(ExpressionParsingCtx) {


            fun matchArrayLength(compType: TypeDslMixin.() -> JTypeMirror) =
                    parseAs {
                        fieldAccess("length") {
                            it.typeMirror shouldBe it.typeSystem.INT

                            val component = with (it.typeDsl) { compType() }

                            it::getQualifier shouldBe child<ASTArrayAllocation>(ignoreChildren = true) {
                                it.typeMirror shouldBe it.typeSystem.arrayType(component) // t[]
                            }
                        }
                    }

            "new int[0].length" should matchArrayLength { int }
            "new String[0].length" should matchArrayLength { gen.t_String }
            "new String[0][].length" should matchArrayLength { gen.t_String.toArray() }
        }
    }

    parserTest("Test binary numeric promotion on infix ops") {

        inContext(ExpressionParsingCtx) {

            listOf(ADD, MUL, SUB, MOD, DIV, OR, AND, XOR)
                    .forEach {

                        val op = it.token

                        "(byte) 1 $op (byte) 2" should haveType { int }
                        "1 $op (byte) 2" should haveType { int }
                        "1  $op 2" should haveType { int }
                        "1  $op 2d" should haveType { double }
                        "1d $op 2" should haveType { double }
                        "1d $op 2l" should haveType { double }
                        "1  $op 2l" should haveType { long }
                        "1f $op 2l" should haveType { float }
                        "1f $op 2" should haveType { float }
                        "1f $op 2d" should haveType { double }

                    }
        }
    }


    parserTest("Test array initializer") {

        val block = StatementParsingCtx.parseNode("{ int[] is = { a }; int[][] iis = { { } }; }", ctx = this)
        val (oneDim, twoDim, nested) = block.descendants(ASTArrayInitializer::class.java).toList()
        with(block.typeDsl) {
            oneDim.typeMirror shouldBe int.toArray()

            withClue("Multi dim array") {
                twoDim.typeMirror shouldBe int.toArray(2)
                nested.typeMirror shouldBe int.toArray()
            }
        }
    }

    parserTest("Test array allocation") {

        val block = StatementParsingCtx.parseNode("{ Object is = new int[0]; is = new String[0][]; }", ctx = this)
        val (intArray, stringArray) = block.descendants(ASTArrayAllocation::class.java).toList()
        with(block.typeDsl) {
            intArray.typeMirror shouldBe int.toArray()
            stringArray.typeMirror shouldBe ts.STRING.toArray(2)
        }
    }


    parserTest("Test unary mutation expressions have the same type as the variable") {

        inContext(StatementParsingCtx) {

            listOf(CHAR, BYTE, SHORT, INT, LONG).forEach { kind ->

                val block = doParse("{ $kind v; v++; v--; --v; ++v; }")

                block.shouldMatchN {
                    block {
                        localVarDecl()

                        fun assertExprType(unaryOp: UnaryOp) {
                            exprStatement {
                                unaryExpr(unaryOp) {
                                    val t = it.typeSystem.getPrimitive(kind)

                                    it::getTypeMirror shouldBe t
                                    variableAccess("v") {
                                        it::getTypeMirror shouldBe t
                                    }
                                }
                            }
                        }

                        assertExprType(UnaryOp.POST_INCREMENT)
                        assertExprType(UnaryOp.POST_DECREMENT)
                        assertExprType(UnaryOp.PRE_DECREMENT)
                        assertExprType(UnaryOp.PRE_INCREMENT)
                    }
                }
            }
        }
    }

    parserTest("Test unary numeric promotion on shift ops") {

        inContext(ExpressionParsingCtx) {

            listOf(LEFT_SHIFT, RIGHT_SHIFT, UNSIGNED_RIGHT_SHIFT)
                    .forEach {

                        val op = it.token

                        "(byte) 2  $op 2" should haveType { int }
                        "(char) 2  $op 2" should haveType { int }
                        "(short) 2 $op 2" should haveType { int }
                        "(int) 2   $op 2" should haveType { int }

                        "2L  $op 2" should haveType { long }
                        "2D  $op 2" should haveType { double }
                        "2F  $op 2" should haveType { float }

                    }
        }
    }

    parserTest("Test boolean ops") {

        inContext(ExpressionParsingCtx) {

            listOf(
                    CONDITIONAL_OR,
                    CONDITIONAL_AND,
                    EQ,
                    NE,
                    LE,
                    GE,
                    GT,
                    INSTANCEOF,
                    LT
            )
                    .forEach {

                        val op = it.token

                        "1 $op String" should haveType { boolean }

                    }
        }
    }

    parserTest("Test assignment expr") {

        inContext(StatementParsingCtx) {

            """
            {
                java.util.List<? extends Number> l = java.util.Collections.emptyList();
                l = l = l;
            }
        """ should parseAs {
                block {
                    localVarDecl()
                    exprStatement {
                        with(it.typeDsl) {
                            val captEnd = captureMatcher(`?` extends gen.t_Number)
                            val captMid = captureMatcher(`?` extends gen.t_Number)
                            val captFirst = captureMatcher(`?` extends gen.t_Number)

                            assignmentExpr(AssignmentOp.ASSIGN) {
                                it.typeMirror shouldBe gen.t_List[captEnd]

                                variableAccess("l") {
                                    // write access: not captured
                                    it.typeMirror shouldBe gen.`t_List{? extends Number}`
                                }

                                assignmentExpr(AssignmentOp.ASSIGN) {
                                    it.typeMirror shouldBe gen.t_List[captMid]

                                    variableAccess("l") {
                                        // write access: not captured
                                        it.typeMirror shouldBe gen.`t_List{? extends Number}`
                                    }
                                    variableAccess("l") {
                                        it.typeMirror shouldBe gen.t_List[captFirst]
                                    }
                                }
                            }.also {
                                // captures are distinct
                                captFirst shouldNotBe captMid
                                captMid shouldNotBe captEnd
                                captEnd shouldNotBe captFirst
                            }
                        }
                    }
                }
            }
        }
    }

    parserTest("Test field access on type variable") {

        val method = TypeBodyParsingCtx.parseNode("<T extends int[]> void foo(T t) { t.length++; }", ctx = this)

        val tvar = method.descendants(ASTTypeParameter::class.java).firstOrThrow().typeMirror
        val fieldAccess = method.descendants(ASTFieldAccess::class.java).firstOrThrow()

        fieldAccess.shouldMatchN {
            fieldAccess("length") {
                variableAccess("t") {
                    it.typeMirror shouldBe tvar
                }
                it.typeMirror shouldBe it.typeSystem.INT
                it.referencedSym shouldNotBe null
                it.referencedSym!!.enclosingClass shouldBe it.typeSystem.getClassSymbol(IntArray::class.java)
            }
        }
    }

    parserTest("Test literals") {

        inContext(ExpressionParsingCtx) {

            doTest("Booleans") {
                "true" should haveType { boolean }
                "false" should haveType { boolean }
            }

            doTest("Class literals") {
                "String.class" should haveType { Class::class[gen.t_String] }
                "void.class" should haveType { Class::class[ts.BOXED_VOID] }
            }
        }
    }

    parserTest("Test exception parameter") {
        inContext(StatementParsingCtx) {
            """
            try {}
            catch (IllegalArgumentException | IllegalStateException e) {
                e.printStackTrace();
            }
        """ should parseAs {
                tryStmt {
                    block()
                    catchClause("e") {
                        catchFormal("e") {
                            modifiers { }
                            unionType {
                                classType("IllegalArgumentException") {
                                    it::getTypeMirror shouldBe with(it.typeDsl) { IllegalArgumentException::class.decl }
                                }
                                classType("IllegalStateException") {
                                    it::getTypeMirror shouldBe with(it.typeDsl) { IllegalStateException::class.decl }
                                }


                                val extype = it.typeMirror // RuntimeException

                                with(it.typeDsl) {
                                    IllegalStateException::class.decl shouldBeSubtypeOf extype
                                    IllegalArgumentException::class.decl shouldBeSubtypeOf extype
                                }
                            }
                            variableId("e")
                        }
                        block()
                    }
                }

            }

        }
    }

    parserTest("Test unboxed foreach parameter") {
        inContext(StatementParsingCtx) {
            """
            {
                List<Integer> l = java.util.Collections.emptyList();
                for (int controller : list);
            }
        """ should parseAs {
                block {
                    localVarDecl()
                    foreachLoop {
                        it.varId.typeMirror.isPrimitive(INT) shouldBe true

                        localVarDecl()
                        variableAccess("list")
                        emptyStatement()
                    }
                }
            }
        }
    }

})
