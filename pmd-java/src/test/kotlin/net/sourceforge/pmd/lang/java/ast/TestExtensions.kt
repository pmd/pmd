package net.sourceforge.pmd.lang.java.ast

import com.github.oowekyala.treeutils.matchers.TreeNodeWrapper
import net.sourceforge.pmd.lang.ast.GenericToken
import net.sourceforge.pmd.lang.ast.Node
import net.sourceforge.pmd.lang.ast.test.*
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

fun JavaNode.tokenList(): List<GenericToken> {
    val lst = mutableListOf<GenericToken>()
    var t = firstToken
    lst += t
    while (t != lastToken) {
        t = t.next
        lst += t
    }
    return lst
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

fun TreeNodeWrapper<Node, *>.thisExpr(qualifier: (ASTThisExpression) -> ASTClassOrInterfaceType? = { null }) =
        child<ASTThisExpression> {
            qualifier(it).let { qual ->
                it::getQualifier shouldBe qual
            }
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


fun TreeNodeWrapper<Node, *>.variableAccess(name: String, accessType: ASTAssignableExpr.AccessType? = null, otherAssertions: (ASTVariableAccess) -> Unit = {}) =
        child<ASTVariableAccess> {
            it::getVariableName shouldBe name
            if (accessType != null) {
                it::getAccessType shouldBe accessType
            }
            otherAssertions(it)
        }

fun TreeNodeWrapper<Node, *>.fieldAccess(name: String, accessType: ASTAssignableExpr.AccessType? = null, otherAssertions: NodeSpec<ASTFieldAccess> = EmptyAssertions) =
        child<ASTFieldAccess>(ignoreChildren = otherAssertions == EmptyAssertions) {
            it::getFieldName shouldBe name
            if (accessType != null) {
                it::getAccessType shouldBe accessType
            }

            otherAssertions()
        }

// this isn't a node anymore
fun <T : Node, R : ASTExpression> TreeNodeWrapper<Node, T>.parenthesized(depth: Int = 1, inside: ValuedNodeSpec<T, R>): R =
        inside().also {
            it::isParenthesized shouldBe true
            it::getParenthesisDepth shouldBe depth
        }


fun TreeNodeWrapper<Node, *>.unaryExpr(op: UnaryOp, baseExpr: TreeNodeWrapper<Node, out ASTExpression>.() -> ASTExpression): ASTExpression =
        child<ASTUnaryExpression> {
            it::getOperator shouldBe op
            it::getOperand shouldBe baseExpr()
        }


fun TreeNodeWrapper<Node, *>.postfixMutation(op: IncrementOp, baseExpr: ValuedNodeSpec<ASTIncrementExpression, ASTPrimaryExpression>) =
        incrementExpr(op, isPrefix = false, baseExpr = baseExpr)

fun TreeNodeWrapper<Node, *>.prefixMutation(op: IncrementOp, baseExpr: ValuedNodeSpec<ASTIncrementExpression, ASTPrimaryExpression>) =
        incrementExpr(op, isPrefix = true, baseExpr = baseExpr)

fun TreeNodeWrapper<Node, *>.incrementExpr(op: IncrementOp, isPrefix: Boolean, baseExpr: ValuedNodeSpec<ASTIncrementExpression, ASTPrimaryExpression>) =
        child<ASTIncrementExpression> {
            it::getOp shouldBe op
            it::isPostfix shouldBe !isPrefix
            it::isPrefix shouldBe isPrefix
            it::isDecrement shouldBe (op == IncrementOp.DECREMENT)
            it::isIncrement shouldBe (op == IncrementOp.INCREMENT)
            it::getOperand shouldBe baseExpr()
        }

fun TreeNodeWrapper<Node, *>.typeParamList(contents: NodeSpec<ASTTypeParameters>) =
        child(nodeSpec = contents)

fun TreeNodeWrapper<Node, *>.typeArgList(contents: NodeSpec<ASTTypeArguments> = EmptyAssertions) =
        child(ignoreChildren = contents == EmptyAssertions, nodeSpec = contents)

fun TreeNodeWrapper<Node, *>.throwsList(contents: NodeSpec<ASTThrowsList> = EmptyAssertions) =
        child(ignoreChildren = contents == EmptyAssertions, nodeSpec = contents)

fun TreeNodeWrapper<Node, *>.voidType() =
        child<ASTResultType> {
            it::getTypeNode shouldBe null
            it::isVoid shouldBe true
        }

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


fun TreeNodeWrapper<Node, *>.primitiveType(type: ASTPrimitiveType.PrimitiveType, assertions: NodeSpec<ASTPrimitiveType> = EmptyAssertions) =
        child<ASTPrimitiveType> {
            it::getModelConstant shouldBe type
            it::getTypeImage shouldBe type.token
            assertions()
        }


fun TreeNodeWrapper<Node, *>.castExpr(contents: NodeSpec<ASTCastExpression>) =
        child<ASTCastExpression> {
            contents()
        }

fun TreeNodeWrapper<Node, *>.stringLit(image: String, contents: NodeSpec<ASTStringLiteral> = EmptyAssertions) =
        child<ASTStringLiteral> {
            it::getImage shouldBe image
            it::isTextBlock shouldBe false
            contents()
        }

fun TreeNodeWrapper<Node, *>.textBlock(contents: NodeSpec<ASTStringLiteral> = EmptyAssertions) =
        child<ASTStringLiteral> {
            it::isTextBlock shouldBe true
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

// TODO inline those more specific methods and remove them (infixExpr is enough)
//  not in this PR to reduce diff

fun TreeNodeWrapper<Node, *>.additiveExpr(op: BinaryOp, assertions: NodeSpec<ASTInfixExpression>) =
        infixExpr(op, assertions)


fun TreeNodeWrapper<Node, *>.assignmentExpr(op: AssignmentOp, assertions: NodeSpec<ASTAssignmentExpression> = EmptyAssertions) =
        child<ASTAssignmentExpression>(ignoreChildren = assertions == EmptyAssertions) {
            it::getOp shouldBe op
            assertions()
        }

fun TreeNodeWrapper<Node, *>.equalityExpr(op: BinaryOp, assertions: NodeSpec<ASTInfixExpression>) =
        infixExpr(op, assertions)


fun TreeNodeWrapper<Node, *>.shiftExpr(op: BinaryOp, assertions: NodeSpec<ASTInfixExpression>) =
        infixExpr(op, assertions)


fun TreeNodeWrapper<Node, *>.compExpr(op: BinaryOp, assertions: NodeSpec<ASTInfixExpression>) =
        infixExpr(op, assertions)


fun TreeNodeWrapper<Node, *>.infixExpr(op: BinaryOp, assertions: NodeSpec<ASTInfixExpression>) =
        child<ASTInfixExpression> {
            it::getOperator shouldBe op
            assertions()
        }


fun TreeNodeWrapper<Node, *>.instanceOfExpr(assertions: NodeSpec<ASTInstanceOfExpression>) =
        child<ASTInstanceOfExpression> {
            assertions()
        }

fun TreeNodeWrapper<Node, *>.andExpr(assertions: NodeSpec<ASTInfixExpression>) =
        infixExpr(BinaryOp.AND, assertions)


fun TreeNodeWrapper<Node, *>.multiplicativeExpr(op: BinaryOp, assertions: NodeSpec<ASTInfixExpression>) =
        infixExpr(op, assertions)


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

val EmptyAssertions: NodeSpec<out Node> = {}

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

fun TreeNodeWrapper<Node, *>.arrayType(elementType: ValuedNodeSpec<ASTArrayType, ASTType>, dims: NodeSpec<ASTArrayDimensions> = EmptyAssertions) =
        child<ASTArrayType> {
            it::getElementType shouldBe elementType()
            it::getDimensions shouldBe child {
                dims()
            }
        }

fun TreeNodeWrapper<Node, *>.arrayDim(assertions: NodeSpec<ASTArrayTypeDim> = EmptyAssertions) =
        child<ASTArrayTypeDim> {
            assertions()
        }

fun TreeNodeWrapper<Node, *>.boolean(value: Boolean) =
        child<ASTBooleanLiteral> {
            it::isTrue shouldBe value
        }


fun TreeNodeWrapper<Node, *>.classDecl(simpleName: String, assertions: NodeSpec<ASTClassOrInterfaceDeclaration> = EmptyAssertions) =
        child<ASTClassOrInterfaceDeclaration>(ignoreChildren = assertions == EmptyAssertions) {
            it::getImage shouldBe simpleName

            assertions()
        }

fun TreeNodeWrapper<Node, *>.classBody(assertions: NodeSpec<ASTClassOrInterfaceBody> = EmptyAssertions) =
        child<ASTClassOrInterfaceBody>(ignoreChildren = assertions == EmptyAssertions) {
            assertions()
        }
