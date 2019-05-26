package net.sourceforge.pmd.lang.java.ast

import com.github.oowekyala.treeutils.matchers.TreeNodeWrapper
import net.sourceforge.pmd.lang.ast.Node
import net.sourceforge.pmd.lang.ast.test.NodeSpec
import net.sourceforge.pmd.lang.ast.test.ValuedNodeSpec
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


fun TreeNodeWrapper<Node, *>.annotation(spec: ValuedNodeSpec<ASTAnnotation, Unit> = EmptyAssertions) =
        child(ignoreChildren = spec == EmptyAssertions, nodeSpec = spec)


fun TreeNodeWrapper<Node, *>.annotation(name: String, spec: NodeSpec<ASTAnnotation> = EmptyAssertions) =
        child<ASTAnnotation>(ignoreChildren = spec == EmptyAssertions) {
            it::getAnnotationName shouldBe name
            spec()
        }


fun TreeNodeWrapper<Node, *>.enumConstant(name: String, spec: NodeSpec<ASTEnumConstant> = EmptyAssertions) =
        child<ASTEnumConstant> {
            it::getName shouldBe name
            spec()
        }

fun TreeNodeWrapper<Node, *>.variableId(name: String, otherAssertions: (ASTVariableDeclaratorId) -> Unit = {}) =
        child<ASTVariableDeclaratorId>(ignoreChildren = true) {
            it::getVariableName shouldBe name
            otherAssertions(it)
        }

fun TreeNodeWrapper<Node, *>.variableDeclarator(name: String, spec: NodeSpec<ASTVariableDeclarator> = EmptyAssertions) =
        child<ASTVariableDeclarator> {
            it::getVariableId shouldBe variableId(name)
            spec()
        }


fun TreeNodeWrapper<Node, *>.variableRef(name: String, otherAssertions: (ASTVariableReference) -> Unit = {}) =
        child<ASTVariableReference> {
            it::getVariableName shouldBe name
            otherAssertions(it)
        }
fun TreeNodeWrapper<Node, *>.fieldAccess(name: String, otherAssertions: NodeSpec<ASTFieldAccess> = EmptyAssertions) =
        child<ASTFieldAccess>(ignoreChildren = otherAssertions == EmptyAssertions) {
            it::getFieldName shouldBe name
            otherAssertions()
        }

fun TreeNodeWrapper<Node, *>.parenthesized(inside: ValuedNodeSpec<ASTParenthesizedExpression, ASTExpression>) =
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


fun TreeNodeWrapper<Node, *>.postfixExpr(op: UnaryOp, baseExpr: ValuedNodeSpec<ASTPostfixExpression, ASTPrimaryExpression>) =
        child<ASTPostfixExpression> {
            it::getOp shouldBe op
            it::getBaseExpression shouldBe baseExpr()
        }

fun TreeNodeWrapper<Node, *>.typeParamList(contents: NodeSpec<ASTTypeParameters>) =
        child(nodeSpec = contents)

fun TreeNodeWrapper<Node, *>.typeArgList(contents: NodeSpec<ASTTypeArguments> = EmptyAssertions) =
        child(ignoreChildren = contents == EmptyAssertions, nodeSpec = contents)

fun TreeNodeWrapper<Node, *>.diamond() =
        child<ASTTypeArguments> {
            it::isDiamond shouldBe true
        }

fun TreeNodeWrapper<Node, *>.block(contents: NodeSpec<ASTBlock> = EmptyAssertions) =
        child<ASTBlock>(ignoreChildren = contents == EmptyAssertions) {
            contents()
        }

fun TreeNodeWrapper<Node, *>.fieldDecl(contents: NodeSpec<ASTFieldDeclaration>) =
        child<ASTFieldDeclaration> {
            contents()
        }


fun TreeNodeWrapper<Node, *>.typeParam(name: String, contents: ValuedNodeSpec<ASTTypeParameter, ASTType?> = { null }) =
        child<ASTTypeParameter> {
            it::getParameterName shouldBe name
            it::getTypeBoundNode shouldBe contents()
        }

fun TreeNodeWrapper<Node, *>.classType(simpleName: String, contents: NodeSpec<ASTClassOrInterfaceType> = EmptyAssertions) =
        child<ASTClassOrInterfaceType>(ignoreChildren = contents == EmptyAssertions) {
            it::getSimpleName shouldBe simpleName
            contents()
        }


fun TreeNodeWrapper<Node, *>.arrayType(contents: NodeSpec<ASTArrayType> = EmptyAssertions) =
        child<ASTArrayType>(ignoreChildren = contents == EmptyAssertions) {
            contents()
        }


fun TreeNodeWrapper<Node, *>.primitiveType(type: ASTPrimitiveType.PrimitiveType) =
        child<ASTPrimitiveType> {
            it::getModelConstant shouldBe type
            it::getTypeImage shouldBe type.token
        }


fun TreeNodeWrapper<Node, *>.castExpr(contents: NodeSpec<ASTCastExpression>) =
        child<ASTCastExpression> {
            contents()
        }

fun TreeNodeWrapper<Node, *>.stringLit(image: String, contents: NodeSpec<ASTStringLiteral> = EmptyAssertions) =
        child<ASTStringLiteral> {
            it::getImage shouldBe image
            contents()
        }

fun TreeNodeWrapper<Node, *>.classLiteral(contents: ValuedNodeSpec<ASTClassLiteral, ASTType?>) =
        child<ASTClassLiteral> {
            val tn = it.typeNode
            it::isVoid shouldBe (tn == null)
            it::getTypeNode shouldBe contents()
        }


fun TreeNodeWrapper<Node, *>.ambiguousName(image: String, contents: NodeSpec<ASTAmbiguousName> = EmptyAssertions) =
        child<ASTAmbiguousName> {
            it::getImage shouldBe image
            contents()
        }

fun TreeNodeWrapper<Node, *>.memberValuePair(name: String, contents: ValuedNodeSpec<ASTMemberValuePair, ASTMemberValue>) =
        child<ASTMemberValuePair> {
            it::getMemberName shouldBe name
            it::getMemberValue shouldBe contents()
        }


fun TreeNodeWrapper<Node, *>.additiveExpr(op: BinaryOp, assertions: NodeSpec<ASTAdditiveExpression>) =
        child<ASTAdditiveExpression> {
            it::getOp shouldBe op
            assertions()
        }

fun TreeNodeWrapper<Node, *>.equalityExpr(op: BinaryOp, assertions: NodeSpec<ASTEqualityExpression>) =
        child<ASTEqualityExpression> {
            it::getOp shouldBe op
            assertions()
        }

fun TreeNodeWrapper<Node, *>.andExpr(assertions: NodeSpec<ASTAndExpression>) =
        child<ASTAndExpression> {
            assertions()
        }


fun TreeNodeWrapper<Node, *>.multiplicativeExpr(op: BinaryOp, assertions: NodeSpec<ASTMultiplicativeExpression>) =
        child<ASTMultiplicativeExpression> {
            it::getOp shouldBe op
            assertions()
        }

fun TreeNodeWrapper<Node, *>.methodRef(methodName: String, assertions: NodeSpec<ASTMethodReference>) =
        child<ASTMethodReference> {
            it::getMethodName shouldBe methodName
            it::isConstructorReference shouldBe false
            assertions()
        }

fun TreeNodeWrapper<Node, *>.constructorRef(assertions: ValuedNodeSpec<ASTMethodReference, ASTReferenceType>) =
        child<ASTMethodReference> {
            it::getMethodName shouldBe null
            it::getImage shouldBe "new"
            it::isConstructorReference shouldBe true
            it::getLhsExpression shouldBe null
            it::getAmbiguousLhs shouldBe null
            it::getLhsType shouldBe assertions()
        }

private val EmptyAssertions: NodeSpec<out Node> = {}

fun TreeNodeWrapper<Node, *>.switchExpr(assertions: NodeSpec<ASTSwitchExpression> = EmptyAssertions): ASTSwitchExpression =
        child(ignoreChildren = assertions == EmptyAssertions) {
            assertions()
        }

fun TreeNodeWrapper<Node, *>.number(primitiveType: ASTPrimitiveType.PrimitiveType? = null, assertions: NodeSpec<ASTNumericLiteral> = EmptyAssertions) =
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

fun TreeNodeWrapper<Node, *>.int(value: Int? = null, assertions: NodeSpec<ASTNumericLiteral> = EmptyAssertions) =
        number(primitiveType = INT) {
            if (value != null) {
                it::getValueAsInt shouldBe value
            }

            assertions()
        }

fun TreeNodeWrapper<Node, *>.dimExpr(assertions: NodeSpec<ASTArrayDimExpr> = EmptyAssertions, lengthExpr: ValuedNodeSpec<ASTArrayDimExpr, ASTExpression>) =
        child<ASTArrayDimExpr> {
            assertions()
            it::getLengthExpression shouldBe lengthExpr()
        }

fun TreeNodeWrapper<Node, *>.arrayDim(assertions: NodeSpec<ASTArrayTypeDim> = EmptyAssertions) =
        child<ASTArrayTypeDim> {
            assertions()
        }

fun TreeNodeWrapper<Node, *>.boolean(value: Boolean) =
        child<ASTBooleanLiteral> {
            it::isTrue shouldBe value
        }
