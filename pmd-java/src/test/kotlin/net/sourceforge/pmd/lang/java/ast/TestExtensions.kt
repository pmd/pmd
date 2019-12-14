package net.sourceforge.pmd.lang.java.ast

import com.github.oowekyala.treeutils.matchers.TreeNodeWrapper
import io.kotlintest.matchers.haveSize
import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.should
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
    var t = jjtGetFirstToken()
    lst += t
    while (t != jjtGetLastToken()) {
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

fun TreeNodeWrapper<Node, *>.thisExpr(qualifier: ValuedNodeSpec<ASTThisExpression, ASTClassOrInterfaceType?> = { null }) =
        child<ASTThisExpression> {
            it::getQualifier shouldBe qualifier()
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

fun TreeNodeWrapper<Node, *>.arrayAccess(accessType: ASTAssignableExpr.AccessType? = null, otherAssertions: NodeSpec<ASTArrayAccess> = EmptyAssertions) =
        child<ASTArrayAccess>(ignoreChildren = otherAssertions == EmptyAssertions) {
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

// this isn't a node anymore
fun TreeNodeWrapper<Node, *>.methodCall(name: String, inside: NodeSpec<ASTMethodCall> = EmptyAssertions) =
        child<ASTMethodCall>(ignoreChildren = inside === EmptyAssertions) {
            it::getMethodName shouldBe name
            inside()
        }


fun TreeNodeWrapper<Node, *>.unaryExpr(op: UnaryOp, baseExpr: TreeNodeWrapper<Node, ASTUnaryExpression>.() -> ASTExpression) =
        child<ASTUnaryExpression> {
            it::getOperator shouldBe op
            it::getOperand shouldBe baseExpr()
        }


fun TreeNodeWrapper<Node, *>.typeParamList(contents: NodeSpec<ASTTypeParameters>) =
        child(nodeSpec = contents)

fun TreeNodeWrapper<Node, *>.typeArgList(contents: NodeSpec<ASTTypeArguments> = EmptyAssertions) =
        child(ignoreChildren = contents == EmptyAssertions, nodeSpec = contents)

fun TreeNodeWrapper<Node, *>.throwsList(contents: NodeSpec<ASTThrowsList> = EmptyAssertions) =
        child(ignoreChildren = contents == EmptyAssertions, nodeSpec = contents)

fun TreeNodeWrapper<Node, *>.formalsList(arity: Int, contents: NodeSpec<ASTFormalParameters> = EmptyAssertions) =
        child<ASTFormalParameters>(ignoreChildren = contents == EmptyAssertions) {
            it::getParameterCount shouldBe arity
            it.toList() should haveSize(arity)
            contents()
        }

fun TreeNodeWrapper<Node, *>.voidResult() =
        child<ASTResultType> {
            it::getTypeNode shouldBe null
            it::isVoid shouldBe true
        }

fun TreeNodeWrapper<Node, *>.resultType(contents: ValuedNodeSpec<ASTResultType, ASTType>) =
        child<ASTResultType>(ignoreChildren = contents == EmptyAssertions) {
            it::getTypeNode shouldBe contents()
            it::isVoid shouldBe false
        }

fun TreeNodeWrapper<Node, *>.defaultValue(contents: ValuedNodeSpec<ASTDefaultValue, ASTMemberValue>) =
        child<ASTDefaultValue>(ignoreChildren = contents == EmptyAssertions) {
            it::getConstant shouldBe contents()
        }

fun TreeNodeWrapper<Node, *>.diamond() =
        child<ASTTypeArguments> {
            it::isDiamond shouldBe true
        }

fun TreeNodeWrapper<Node, *>.block(contents: NodeSpec<ASTBlock> = EmptyAssertions) =
        child<ASTBlock>(ignoreChildren = contents == EmptyAssertions) {
            contents()
        }

fun TreeNodeWrapper<Node, *>.emptyStatement(contents: NodeSpec<ASTEmptyStatement> = EmptyAssertions) =
        child<ASTEmptyStatement>(ignoreChildren = contents == EmptyAssertions) {
            contents()
        }

fun TreeNodeWrapper<Node, *>.forLoop(body: ValuedNodeSpec<ASTForStatement, ASTStatement?> = { null }) =
        child<ASTForStatement> {
            val body = body()
            if (body != null) it::getBody shouldBe body
            else unspecifiedChildren(it.numChildren)
        }

fun TreeNodeWrapper<Node, *>.forUpdate(body: ValuedNodeSpec<ASTForUpdate, ASTStatementExpressionList>) =
        fromChild<ASTForUpdate, ASTStatementExpressionList> {
            it::getExprList shouldBe body()
            it.exprList
        }

fun TreeNodeWrapper<Node, *>.forInit(body: ValuedNodeSpec<ASTForInit, ASTStatement>) =
        fromChild<ASTForInit, ASTStatement> {
            it::getStatement shouldBe body()
            it.statement
        }

fun TreeNodeWrapper<Node, *>.statementExprList(body: NodeSpec<ASTStatementExpressionList> = EmptyAssertions) =
        child<ASTStatementExpressionList>(ignoreChildren = body === EmptyAssertions) {
            body()
        }

fun TreeNodeWrapper<Node, *>.foreachLoop(body: ValuedNodeSpec<ASTForeachStatement, ASTStatement?> = { null }) =
        child<ASTForeachStatement> {
            val body = body()
            if (body != null) it::getBody shouldBe body
            else unspecifiedChildren(it.numChildren)
        }

fun TreeNodeWrapper<Node, *>.doLoop(body: ValuedNodeSpec<ASTDoStatement, ASTStatement?> = { null }) =
        child<ASTDoStatement> {
            val body = body()
            if (body != null) it::getBody shouldBe body
            else unspecifiedChildren(it.numChildren)
        }

fun TreeNodeWrapper<Node, *>.whileLoop(body: ValuedNodeSpec<ASTWhileStatement, ASTStatement?> = { null }) =
        child<ASTWhileStatement> {
            val body = body()
            if (body != null) it::getBody shouldBe body
            else unspecifiedChildren(it.numChildren)
        }

fun TreeNodeWrapper<Node, *>.constructorCall(contents: NodeSpec<ASTConstructorCall> = EmptyAssertions) =
        child<ASTConstructorCall>(ignoreChildren = contents == EmptyAssertions) {
            contents()
        }

fun TreeNodeWrapper<Node, *>.breakStatement(label: String? = null, contents: NodeSpec<ASTBreakStatement> = EmptyAssertions) =
        child<ASTBreakStatement>(ignoreChildren = contents == EmptyAssertions) {
            it::getLabel shouldBe label
            contents()
        }

fun TreeNodeWrapper<Node, *>.yieldStatement(contents: ValuedNodeSpec<ASTYieldStatement, ASTExpression?> = {null}) =
        child<ASTYieldStatement> {
            val e = contents()
            if (e != null) it::getExpr shouldBe e
            else unspecifiedChild()
        }


fun TreeNodeWrapper<Node, *>.throwStatement(contents: NodeSpec<ASTThrowStatement> = EmptyAssertions) =
        child<ASTThrowStatement>(ignoreChildren = contents == EmptyAssertions) {
            contents()
        }

fun TreeNodeWrapper<Node, *>.localVarDecl(contents: NodeSpec<ASTLocalVariableDeclaration> = EmptyAssertions) =
        child<ASTLocalVariableDeclaration>(ignoreChildren = contents == EmptyAssertions) {
            contents()
        }

fun TreeNodeWrapper<Node, *>.localClassDecl(simpleName: String, contents: NodeSpec<ASTClassOrInterfaceDeclaration> = EmptyAssertions) =
        child<ASTLocalClassStatement> {
            it::getDeclaration shouldBe classDecl(simpleName, contents)
        }

fun TreeNodeWrapper<Node, *>.exprStatement(contents: ValuedNodeSpec<ASTExpressionStatement, ASTExpression?> = { null }) =
        child<ASTExpressionStatement> {
            val expr = contents()
            if (expr != null) it::getExpr shouldBe expr
            else unspecifiedChild()
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


fun TreeNodeWrapper<Node, *>.typeExpr(contents: ValuedNodeSpec<ASTTypeExpression, ASTType>) =
        child<ASTTypeExpression>(ignoreChildren = contents == EmptyAssertions) {
            it::getTypeNode shouldBe contents()
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
            it::getOperator shouldBe op
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


fun TreeNodeWrapper<Node, *>.instanceOfExpr(assertions: NodeSpec<ASTInfixExpression>) =
        infixExpr(BinaryOp.INSTANCEOF, assertions)

fun TreeNodeWrapper<Node, *>.andExpr(assertions: NodeSpec<ASTInfixExpression>) =
        infixExpr(BinaryOp.AND, assertions)


fun TreeNodeWrapper<Node, *>.multiplicativeExpr(op: BinaryOp, assertions: NodeSpec<ASTInfixExpression>) =
        infixExpr(op, assertions)


fun TreeNodeWrapper<Node, *>.methodRef(methodName: String, assertions: NodeSpec<ASTMethodReference> = EmptyAssertions) =
        child<ASTMethodReference>(ignoreChildren = assertions === EmptyAssertions) {
            it::getMethodName shouldBe methodName
            it::isConstructorReference shouldBe false
            assertions()
        }

fun TreeNodeWrapper<Node, *>.constructorRef(assertions: ValuedNodeSpec<ASTMethodReference, ASTTypeExpression>) =
        child<ASTMethodReference> {
            it::getMethodName shouldBe null
            it::getImage shouldBe "new"
            it::isConstructorReference shouldBe true
            it::getQualifier shouldBe assertions()
        }

val EmptyAssertions: NodeSpec<out Node> = {}

fun TreeNodeWrapper<Node, *>.switchExpr(assertions: NodeSpec<ASTSwitchExpression> = EmptyAssertions): ASTSwitchExpression =
        child(ignoreChildren = assertions == EmptyAssertions) {
            assertions()
        }
fun TreeNodeWrapper<Node, *>.switchStmt(assertions: NodeSpec<ASTSwitchStatement> = EmptyAssertions) =
        child<ASTSwitchStatement>(ignoreChildren = assertions == EmptyAssertions) {
            assertions()
        }

fun TreeNodeWrapper<Node, *>.switchArrow(rhs: ValuedNodeSpec<ASTSwitchArrowBranch, ASTSwitchArrowRHS?> = { null }) =
        child<ASTSwitchArrowBranch> {
            val rhs = rhs()
            if (rhs != null) it::getRightHandSide shouldBe rhs
            else unspecifiedChildren(2) // label + rhs
        }

fun TreeNodeWrapper<Node, *>.switchFallthrough(assertions: NodeSpec<ASTSwitchFallthroughBranch> = EmptyAssertions) =
        child<ASTSwitchFallthroughBranch>(ignoreChildren = assertions == EmptyAssertions) {
            assertions()
        }
fun TreeNodeWrapper<Node, *>.switchLabel(assertions: NodeSpec<ASTSwitchLabel> = EmptyAssertions) =
        child<ASTSwitchLabel>(ignoreChildren = assertions == EmptyAssertions) {
            it::isDefault shouldBe false
            assertions()
        }

fun TreeNodeWrapper<Node, *>.switchDefaultLabel(assertions: NodeSpec<ASTSwitchLabel> = EmptyAssertions) =
        child<ASTSwitchLabel>(ignoreChildren = assertions == EmptyAssertions) {
            it::isDefault shouldBe true
            it::getExprList shouldBe emptyList()
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

fun TreeNodeWrapper<Node, *>.arrayType(elementType: ValuedNodeSpec<ASTArrayType, ASTType>, dims: NodeSpec<ASTArrayDimensions>) =
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

fun TreeNodeWrapper<Node, *>.arrayInitializer(assertions: NodeSpec<ASTArrayInitializer> = EmptyAssertions) =
        child<ASTArrayInitializer> {
            assertions()
        }

fun TreeNodeWrapper<Node, *>.memberValueArray(assertions: NodeSpec<ASTMemberValueArrayInitializer> = EmptyAssertions) =
        child<ASTMemberValueArrayInitializer> {
            assertions()
        }

fun TreeNodeWrapper<Node, *>.annotationMethod(contents: NodeSpec<ASTMethodDeclaration> = EmptyAssertions) =
        child<ASTMethodDeclaration>(ignoreChildren = contents == EmptyAssertions) {
            it.enclosingType.shouldBeInstanceOf<ASTAnnotationTypeDeclaration>()
            it::getThrowsList shouldBe null
            it::getTypeParameters shouldBe null
            it::getBody shouldBe null
            it::isAbstract shouldBe true
            it::getArity shouldBe 0

            contents()
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

fun TreeNodeWrapper<Node, *>.typeBody(contents: NodeSpec<ASTTypeBody> = EmptyAssertions) =
        child<ASTTypeBody> {
            contents()
        }
