/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types.internal.infer

import io.kotlintest.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldMatchN
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.ast.BinaryOp.*
import net.sourceforge.pmd.lang.java.types.*
import net.sourceforge.pmd.lang.java.types.testdata.Overloads
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

class StandaloneTypesTest : ProcessorTestSpec({


    val arrayComponentSubjects = RefTypeGen.constants() + PrimitiveGen.constants()


    parserTest("Test array clone") {

        inContext(ExpressionParsingCtx) {
            arrayComponentSubjects.forEach { t ->

                "new $t[0].clone()" should parseAs {
                    methodCall("clone") {
                        it.methodType.toString() shouldBe "$t[].clone() -> $t[]"
                        it.typeMirror.toString() shouldBe "$t[]"

                        it::getQualifier shouldBe child<ASTArrayAllocation>(ignoreChildren = true) {
                            it.typeMirror.toString() shouldBe "$t[]"
                        }

                        it::getArguments shouldBe child {

                        }
                    }
                }
            }
        }
    }

    parserTest("Test array length") {

        asIfIn(Overloads::class.java)

        inContext(ExpressionParsingCtx) {

            arrayComponentSubjects.forEach { t ->

                "new $t[0].length" should parseAs {
                    fieldAccess("length") {
                        it.typeMirror.toString() shouldBe "int"

                        it::getQualifier shouldBe child<ASTArrayAllocation>(ignoreChildren = true) {
                            it.typeMirror.toString() shouldBe "$t[]"
                        }
                    }
                }
            }
        }
    }

    parserTest("Test binary numeric promotion on infix ops") {

        asIfIn(Overloads::class.java)

        inContext(ExpressionParsingCtx) {

            listOf(ADD, MUL, SUB, MOD, DIV)
                    .forEach {

                        val op = it.token

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


    parserTest("Test unary mutation expressions have the same type as the variable") {

        asIfIn(Overloads::class.java)

        inContext(StatementParsingCtx) {

            listOf(JPrimitiveType.PrimitiveTypeKind.CHAR, JPrimitiveType.PrimitiveTypeKind.BYTE, JPrimitiveType.PrimitiveTypeKind.SHORT, JPrimitiveType.PrimitiveTypeKind.INT, JPrimitiveType.PrimitiveTypeKind.LONG).forEach { kind ->

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

        asIfIn(Overloads::class.java)

        inContext(ExpressionParsingCtx) {
            val ts = testTypeSystem

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

        asIfIn(Overloads::class.java)

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

                        "1  $op String" should haveType { boolean }

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
                tryStmt{
                    block()
                    catchClause("e") {
                        catchFormal("e") {
                            modifiers {  }
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

})
