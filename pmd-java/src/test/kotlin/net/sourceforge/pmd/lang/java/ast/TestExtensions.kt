package net.sourceforge.pmd.lang.java.ast

import com.github.oowekyala.treeutils.matchers.TreeNodeWrapper
import net.sourceforge.pmd.lang.ast.Node
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldMatch
import java.util.*
import kotlin.reflect.KCallable

infix fun <T, U : T> Optional<T>.shouldBePresent(any: U) {
    ::isPresent shouldBe true
    ::get shouldBe any
}

fun Optional<*>.shouldBeEmpty() {
    ::isPresent shouldBe false
}

fun KCallable<Optional<*>>.shouldBeEmpty() = this shouldMatch {
    ::isPresent shouldBe false
}

infix fun <T, U : T> KCallable<Optional<T>>.shouldBePresent(any: U) = this shouldMatch {
    ::isPresent shouldBe true
    ::get shouldBe any
}

fun String.addArticle() = when (this[0].toLowerCase()) {
    'a', 'e', 'i', 'o', 'u' -> "an $this"
    else -> "a $this"
}

fun TreeNodeWrapper<Node, *>.annotation(spec: TreeNodeWrapper<Node, ASTAnnotation>.() -> Unit={}) =
        child(ignoreChildren = false, nodeSpec = spec)


fun TreeNodeWrapper<Node, *>.variableId(name: String, otherAssertions: (ASTVariableDeclaratorId) -> Unit = {}) =
        child<ASTVariableDeclaratorId>(ignoreChildren = true) {
            it::getVariableName shouldBe name
            otherAssertions(it)
        }


fun TreeNodeWrapper<Node, *>.variableRef(name: String, otherAssertions: (ASTVariableReference) -> Unit = {}) =
        child<ASTVariableReference> {
            it::getVariableName shouldBe name
            otherAssertions(it)
        }
fun TreeNodeWrapper<Node, *>.fieldAccess(name: String, otherAssertions: TreeNodeWrapper<Node, ASTFieldAccess>.() -> Unit) =
        child<ASTFieldAccess> {
            it::getFieldName shouldBe name
            otherAssertions()
        }

fun TreeNodeWrapper<Node, *>.parenthesized(inside: TreeNodeWrapper<Node, ASTParenthesizedExpression>.() -> ASTExpression) =
        child<ASTParenthesizedExpression> {
            it::getWrappedExpression shouldBe inside()
        }


fun TreeNodeWrapper<Node, *>.unaryExpr(op: UnaryOp, baseExpr: TreeNodeWrapper<Node, out ASTExpression>.() -> ASTExpression): ASTExpression =
        when (op) {
            UnaryOp.INCREMENT -> child<ASTPreIncrementExpression> {
                baseExpr()
            }
            UnaryOp.DECREMENT -> child<ASTPreDecrementExpression> {
                baseExpr()
            }
            else -> child<ASTUnaryExpression> {
                it::getOp shouldBe op
                it::getBaseExpression shouldBe baseExpr()
            }
        }


fun TreeNodeWrapper<Node, *>.postfixExpr(op: UnaryOp, baseExpr: TreeNodeWrapper<Node, ASTPostfixExpression>.() -> ASTPrimaryExpression) =
        child<ASTPostfixExpression> {
            it::getOp shouldBe op
            it::getBaseExpression shouldBe baseExpr()
        }

fun TreeNodeWrapper<Node, *>.classType(simpleName: String, contents: TreeNodeWrapper<Node, ASTClassOrInterfaceType>.() -> Unit = EmptyAssertions) =
        child<ASTClassOrInterfaceType>(ignoreChildren = contents == EmptyAssertions) {
            it::getSimpleName shouldBe simpleName
            contents()
        }


fun TreeNodeWrapper<Node, *>.primitiveType(type: ASTPrimitiveType.PrimitiveType) =
        child<ASTPrimitiveType> {
            it::getModelConstant shouldBe type
            it::getTypeImage shouldBe type.token
        }


fun TreeNodeWrapper<Node, *>.castExpr(contents: TreeNodeWrapper<Node, ASTCastExpression>.() -> Unit) =
        child<ASTCastExpression> {
            contents()
        }


fun TreeNodeWrapper<Node, *>.additiveExpr(op: BinaryOp, assertions: TreeNodeWrapper<Node, ASTAdditiveExpression>.() -> Unit) =
        child<ASTAdditiveExpression> {
            it::getOp shouldBe op
            assertions()
        }

fun TreeNodeWrapper<Node, *>.equalityExpr(op: BinaryOp, assertions: TreeNodeWrapper<Node, ASTEqualityExpression>.() -> Unit) =
        child<ASTEqualityExpression> {
            it::getOp shouldBe op
            assertions()
        }

fun TreeNodeWrapper<Node, *>.andExpr(assertions: TreeNodeWrapper<Node, ASTAndExpression>.() -> Unit) =
        child<ASTAndExpression> {
            assertions()
        }


fun TreeNodeWrapper<Node, *>.multiplicativeExpr(op: BinaryOp, assertions: TreeNodeWrapper<Node, ASTMultiplicativeExpression>.() -> Unit) =
        child<ASTMultiplicativeExpression> {
            it::getOp shouldBe op
            assertions()
        }

private val EmptyAssertions: TreeNodeWrapper<Node, out Node>.() -> Unit = {}

fun TreeNodeWrapper<Node, *>.switchExpr(assertions: TreeNodeWrapper<Node, ASTSwitchExpression>.() -> Unit = EmptyAssertions): ASTSwitchExpression =
        child(ignoreChildren = assertions == EmptyAssertions) {
            assertions()
        }

fun TreeNodeWrapper<Node, *>.number() =
        child<ASTNumericLiteral> {}

fun TreeNodeWrapper<Node, *>.boolean(value: Boolean) =
        child<ASTBooleanLiteral> {
            it::isTrue shouldBe value
        }
