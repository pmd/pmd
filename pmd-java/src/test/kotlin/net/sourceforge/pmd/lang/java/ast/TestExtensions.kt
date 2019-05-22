package net.sourceforge.pmd.lang.java.ast

import com.github.oowekyala.treeutils.matchers.TreeNodeWrapper
import net.sourceforge.pmd.lang.ast.Node
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldMatch
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType.PrimitiveType.*
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

fun TreeNodeWrapper<Node, *>.annotation(spec: TreeNodeWrapper<Node, ASTAnnotation>.() -> Unit = EmptyAssertions) =
        child(ignoreChildren = spec == EmptyAssertions, nodeSpec = spec)


fun TreeNodeWrapper<Node, *>.annotation(name: String, spec: TreeNodeWrapper<Node, ASTAnnotation>.() -> Unit = EmptyAssertions) =
        child<ASTAnnotation>(ignoreChildren = spec == EmptyAssertions) {
            it::getAnnotationName shouldBe name
            spec()
        }


fun TreeNodeWrapper<Node, *>.enumConstant(name: String, spec: TreeNodeWrapper<Node, ASTEnumConstant>.() -> Unit = EmptyAssertions) =
        child<ASTEnumConstant> {
            it::getName shouldBe name
            spec()
        }

fun TreeNodeWrapper<Node, *>.variableId(name: String, otherAssertions: (ASTVariableDeclaratorId) -> Unit = {}) =
        child<ASTVariableDeclaratorId>(ignoreChildren = true) {
            it::getVariableName shouldBe name
            otherAssertions(it)
        }

fun TreeNodeWrapper<Node, *>.variableDeclarator(name: String, spec: TreeNodeWrapper<Node, ASTVariableDeclarator>.() -> Unit = EmptyAssertions) =
        child<ASTVariableDeclarator> {
            it::getVariableId shouldBe variableId(name)
            spec()
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

fun TreeNodeWrapper<Node, *>.typeParamList(contents: TreeNodeWrapper<Node, ASTTypeParameters>.() -> Unit) =
        child(nodeSpec = contents)

fun TreeNodeWrapper<Node, *>.typeArgList(contents: TreeNodeWrapper<Node, ASTTypeArguments>.() -> Unit = EmptyAssertions) =
        child(ignoreChildren = contents == EmptyAssertions, nodeSpec = contents)

fun TreeNodeWrapper<Node, *>.diamond() =
        child<ASTTypeArguments> {
            it::isDiamond shouldBe true
        }

fun TreeNodeWrapper<Node, *>.block(contents: TreeNodeWrapper<Node, ASTBlock>.() -> Unit = EmptyAssertions) =
        child<ASTBlock>(ignoreChildren = contents == EmptyAssertions) {
            contents()
        }

fun TreeNodeWrapper<Node, *>.fieldDecl(contents: TreeNodeWrapper<Node, ASTFieldDeclaration>.() -> Unit) =
        child<ASTFieldDeclaration> {
            contents()
        }


fun TreeNodeWrapper<Node, *>.typeParam(name: String, contents: TreeNodeWrapper<Node, ASTTypeParameter>.() -> ASTType? = { null }) =
        child<ASTTypeParameter> {
            it::getParameterName shouldBe name
            it::getTypeBoundNode shouldBe contents()
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

fun TreeNodeWrapper<Node, *>.stringLit(image: String, contents: TreeNodeWrapper<Node, ASTStringLiteral>.() -> Unit = EmptyAssertions) =
        child<ASTStringLiteral> {
            it::getImage shouldBe image
            contents()
        }


fun TreeNodeWrapper<Node, *>.ambiguousName(image: String, contents: TreeNodeWrapper<Node, ASTAmbiguousName>.() -> Unit = EmptyAssertions) =
        child<ASTAmbiguousName> {
            it::getImage shouldBe image
            contents()
        }

fun TreeNodeWrapper<Node, *>.memberValuePair(name: String, contents: TreeNodeWrapper<Node, ASTMemberValuePair>.() -> ASTMemberValue) =
        child<ASTMemberValuePair> {
            it::getMemberName shouldBe name
            it::getMemberValue shouldBe contents()
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

fun TreeNodeWrapper<Node, *>.number(primitiveType: ASTPrimitiveType.PrimitiveType? = null, assertions: TreeNodeWrapper<Node, ASTNumericLiteral>.() -> Unit = EmptyAssertions) =
        child<ASTNumericLiteral> {
            if (primitiveType != null) {
                it::getPrimitiveType shouldBe primitiveType

                it::isIntLiteral shouldBe (primitiveType == INT)
                it::isDoubleLiteral shouldBe (primitiveType == DOUBLE)
                it::isFloatLiteral shouldBe (primitiveType == FLOAT)
                it::isLongLiteral shouldBe (primitiveType == LONG)
            }

            assertions()
        }

fun TreeNodeWrapper<Node, *>.boolean(value: Boolean) =
        child<ASTBooleanLiteral> {
            it::isTrue shouldBe value
        }
