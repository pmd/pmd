package net.sourceforge.pmd.lang.java.ast

import com.github.oowekyala.treeutils.matchers.TreeNodeWrapper
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.shouldNotBe
import net.sourceforge.pmd.internal.util.IteratorUtil
import net.sourceforge.pmd.lang.ast.GenericToken
import net.sourceforge.pmd.lang.ast.Node
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken
import net.sourceforge.pmd.lang.ast.test.NodeSpec
import net.sourceforge.pmd.lang.ast.test.ValuedNodeSpec
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldMatch
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType.PrimitiveType
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType.PrimitiveType.*
import java.util.*
import kotlin.reflect.KCallable

fun <T, C : Collection<T>> C?.shouldContainAtMostOneOf(vararg expected: T) {
    this shouldNotBe null
    assert(this!!.intersect(setOf(expected)).size <= 1) {
        "$this should contain exactly one of ${expected.toSet()}"
    }
}


fun haveModifier(mod: JModifier): Matcher<AccessNode> = object : Matcher<AccessNode> {
    override fun test(value: AccessNode): MatcherResult =
            MatcherResult(value.hasModifiers(mod), "Expected $value to have modifier $mod", "Expected $value to not have modifier $mod")
}

fun haveExplicitModifier(mod: JModifier): Matcher<AccessNode> = object : Matcher<AccessNode> {
    override fun test(value: AccessNode): MatcherResult {
        return MatcherResult(value.hasExplicitModifiers(mod), "Expected $value to have modifier $mod", "Expected $value to not have modifier $mod")
    }
}

fun haveVisibility(vis: AccessNode.Visibility): Matcher<AccessNode> = object : Matcher<AccessNode> {
    override fun test(value: AccessNode): MatcherResult =
            MatcherResult(value.visibility == vis, "Expected $value to have visibility $vis", "Expected $value to not have visibility $vis")
}

fun JavaNode.tokenList(): List<JavaccToken> =
        IteratorUtil.toList(TokenUtils.tokenRange(this))

fun String.addArticle() = when (this[0].toLowerCase()) {
    'a', 'e', 'i', 'o', 'u' -> "an $this"
    else -> "a $this"
}


fun TreeNodeWrapper<Node, *>.modifiers(spec: ValuedNodeSpec<ASTModifierList, Unit> = EmptyAssertions) =
        child(ignoreChildren = spec == EmptyAssertions, nodeSpec = spec)

fun TreeNodeWrapper<Node, *>.localVarModifiers(spec: ValuedNodeSpec<ASTModifierList, Unit> = EmptyAssertions) =
        child<ASTModifierList>(ignoreChildren = spec == EmptyAssertions) {

            // at most "final"
            (it.explicitModifiers - JModifier.FINAL).shouldBeEmpty()
            (it.effectiveModifiers - JModifier.FINAL).shouldBeEmpty()

            spec()
        }

fun TreeNodeWrapper<Node, *>.annotation(spec: ValuedNodeSpec<ASTAnnotation, Unit> = EmptyAssertions) =
        child(ignoreChildren = spec == EmptyAssertions, nodeSpec = spec)


fun TreeNodeWrapper<Node, *>.annotation(name: String, spec: NodeSpec<ASTAnnotation> = EmptyAssertions) =
        child<ASTAnnotation>(ignoreChildren = spec == EmptyAssertions) {
            it::getAnnotationName shouldBe name
            spec()
        }

fun TreeNodeWrapper<Node, *>.catchClause(name: String, spec: NodeSpec<ASTCatchClause> = EmptyAssertions) =
        child<ASTCatchClause> {
            it.parameter::getName shouldBe name
            spec()
        }

fun TreeNodeWrapper<Node, *>.catchFormal(name: String, spec: NodeSpec<ASTCatchParameter> = EmptyAssertions) =
        child<ASTCatchParameter> {
            it::getName shouldBe name
            spec()
        }

fun TreeNodeWrapper<Node, *>.enumConstant(name: String, spec: NodeSpec<ASTEnumConstant> = EmptyAssertions) =
        child<ASTEnumConstant>(ignoreChildren = spec === EmptyAssertions) {
            it::getName shouldBe name
            spec()
        }

fun TreeNodeWrapper<Node, *>.enumDecl(name: String, spec: NodeSpec<ASTEnumDeclaration> = EmptyAssertions) =
        child<ASTEnumDeclaration> {
            it::getSimpleName shouldBe name
            spec()
        }

fun TreeNodeWrapper<Node, *>.enumBody(contents: NodeSpec<ASTEnumBody> = EmptyAssertions) =
        child<ASTEnumBody> {
            contents()
        }

fun TreeNodeWrapper<Node, *>.thisExpr(qualifier: ValuedNodeSpec<ASTThisExpression, ASTClassOrInterfaceType?> = { null }) =
        child<ASTThisExpression> {
            it::getQualifier shouldBe qualifier()
        }

fun TreeNodeWrapper<Node, *>.variableId(name: String, otherAssertions: NodeSpec<ASTVariableDeclaratorId> = EmptyAssertions) =
        child<ASTVariableDeclaratorId>(ignoreChildren = otherAssertions == EmptyAssertions) {
            it::getVariableName shouldBe name
            otherAssertions()
        }

fun TreeNodeWrapper<Node, *>.simpleLambdaParam(name: String, otherAssertions: NodeSpec<ASTVariableDeclaratorId> = EmptyAssertions) =
        child<ASTLambdaParameter> {
            it::getModifiers shouldBe modifiers {  }

            child<ASTVariableDeclaratorId>(ignoreChildren = otherAssertions == EmptyAssertions) {
                it::getVariableName shouldBe name
                otherAssertions()
            }
        }

fun TreeNodeWrapper<Node, *>.variableDeclarator(name: String, spec: NodeSpec<ASTVariableDeclarator> = EmptyAssertions) =
        child<ASTVariableDeclarator> {
            it::getVarId shouldBe variableId(name)
            spec()
        }

fun TreeNodeWrapper<Node, *>.varDeclarator(spec: NodeSpec<ASTVariableDeclarator> = EmptyAssertions) =
        child<ASTVariableDeclarator> {
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

private fun <T : JavaNode, L : ASTList<T>> listSpec(size: Int?, contents: NodeSpec<L>) =
        Pair<Boolean, NodeSpec<L>>(contents === EmptyAssertions) {
            it.toList()::size shouldBe it.size()
            if (size != null) {
                it::size shouldBe size
            }
            contents()
        }

private inline fun <reified T : JavaNode> TreeNodeWrapper<Node, *>.childW(ignoreChildren: Boolean, crossinline contents: NodeSpec<T>): T =
        child(ignoreChildren = ignoreChildren) {
            contents()
        }

private inline fun <reified T : JavaNode> TreeNodeWrapper<Node, *>.childW(pair: Pair<Boolean, NodeSpec<T>>): T =
        childW(ignoreChildren = pair.first, contents = pair.second)

fun TreeNodeWrapper<Node, *>.typeParamList(size: Int? = null, contents: NodeSpec<ASTTypeParameters> = EmptyAssertions) =
        childW(listSpec(size, contents))

fun TreeNodeWrapper<Node, *>.argList(size: Int? = null, contents: NodeSpec<ASTArgumentList> = EmptyAssertions) =
        childW(listSpec(size, contents))

fun TreeNodeWrapper<Node, *>.dimList(size: Int? = null, contents: NodeSpec<ASTArrayDimensions> = EmptyAssertions) =
        childW(listSpec(size, contents))

fun TreeNodeWrapper<Node, *>.throwsList(size: Int? = null, contents: NodeSpec<ASTThrowsList> = EmptyAssertions) =
        childW(listSpec(size, contents))

fun TreeNodeWrapper<Node, *>.typeArgList(size: Int? = null, contents: NodeSpec<ASTTypeArguments> = EmptyAssertions) =
        childW(listSpec(size, contents))

fun TreeNodeWrapper<Node, *>.lambdaFormals(size: Int? = null, contents: NodeSpec<ASTLambdaParameterList> = EmptyAssertions) =
        childW(listSpec(size, contents))

fun TreeNodeWrapper<Node, *>.formalsList(arity: Int, contents: NodeSpec<ASTFormalParameters> = EmptyAssertions) =
        childW(listSpec(arity, contents))

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
        typeArgList(0) {
            // no children
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

fun TreeNodeWrapper<Node, *>.tryStmt(contents: NodeSpec<ASTTryStatement>) =
        child<ASTTryStatement> {
            contents()
        }

fun TreeNodeWrapper<Node, *>.fieldDecl(contents: NodeSpec<ASTFieldDeclaration>) =
        child<ASTFieldDeclaration> {
            contents()
        }

fun TreeNodeWrapper<Node, *>.constructorDecl(contents: NodeSpec<ASTConstructorDeclaration>) =
        child<ASTConstructorDeclaration> {
            contents()
        }

fun TreeNodeWrapper<Node, *>.methodDecl(contents: NodeSpec<ASTMethodDeclaration>) =
        child<ASTMethodDeclaration> {
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

fun TreeNodeWrapper<Node, *>.qualClassType(canoName: String, contents: NodeSpec<ASTClassOrInterfaceType> = EmptyAssertions) =
        child<ASTClassOrInterfaceType>(ignoreChildren = contents == EmptyAssertions) {
            it::getImage shouldBe canoName
            it::getSimpleName shouldBe canoName.substringAfterLast('.')
            contents()
        }

fun TreeNodeWrapper<Node, *>.unionType(contents: NodeSpec<ASTUnionType> = EmptyAssertions) =
        child<ASTUnionType>(ignoreChildren = contents == EmptyAssertions) {
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


fun TreeNodeWrapper<Node, *>.primitiveType(type: PrimitiveType, assertions: NodeSpec<ASTPrimitiveType> = EmptyAssertions) =
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


fun TreeNodeWrapper<Node, *>.charLit(image: String, contents: NodeSpec<ASTCharLiteral> = EmptyAssertions) =
        child<ASTCharLiteral> {
            it::getImage shouldBe image
            contents()
        }


fun TreeNodeWrapper<Node, *>.boolean(value: Boolean) =
        child<ASTBooleanLiteral> {
            it::getConstValue shouldBe value
        }


fun TreeNodeWrapper<Node, *>.nullLit() =
        child<ASTNullLiteral> {
            it::getConstValue shouldBe null
        }

fun TreeNodeWrapper<Node, *>.boolean(value: Boolean, contents: NodeSpec<ASTBooleanLiteral> = EmptyAssertions) =
        child<ASTBooleanLiteral> {
            it::getConstValue shouldBe value
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
            it::getName shouldBe image
            contents()
        }

fun TreeNodeWrapper<Node, *>.memberValuePair(name: String, contents: ValuedNodeSpec<ASTMemberValuePair, ASTMemberValue>) =
        child<ASTMemberValuePair> {
            it::getName shouldBe name
            it::getValue shouldBe contents()
        }

fun TreeNodeWrapper<Node, *>.shorthandMemberValue(contents: ValuedNodeSpec<ASTMemberValuePair, ASTMemberValue>) =
        memberValuePair("value") {
            it::isShorthand shouldBe true
            contents()
        }


fun TreeNodeWrapper<Node, *>.assignmentExpr(op: AssignmentOp, assertions: NodeSpec<ASTAssignmentExpression> = EmptyAssertions) =
        child<ASTAssignmentExpression>(ignoreChildren = assertions == EmptyAssertions) {
            it::getOperator shouldBe op
            assertions()
        }


fun TreeNodeWrapper<Node, *>.infixExpr(op: BinaryOp, assertions: NodeSpec<ASTInfixExpression> = EmptyAssertions) =
        child<ASTInfixExpression>(ignoreChildren = assertions === EmptyAssertions) {
            it::getOperator shouldBe op
            assertions()
        }



fun TreeNodeWrapper<Node, *>.blockLambda(assertions: ValuedNodeSpec<ASTLambdaExpression, ASTBlock?> = {null}) =
        child<ASTLambdaExpression> {
            it::isBlockBody shouldBe true
            it::isExpressionBody shouldBe false
            val block = assertions()
            if (block == null) unspecifiedChildren(2)
            else it::getBlockBody shouldBe block
        }


fun TreeNodeWrapper<Node, *>.lambdaParam(assertions: NodeSpec<ASTLambdaParameter> = EmptyAssertions) =
        child<ASTLambdaParameter> {
            assertions()
        }

fun TreeNodeWrapper<Node, *>.exprLambda(assertions: ValuedNodeSpec<ASTLambdaExpression, ASTExpression?> = {null}) =
        child<ASTLambdaExpression> {
            it::isBlockBody shouldBe false
            it::isExpressionBody shouldBe true
            val block = assertions()
            if (block == null) unspecifiedChildren(2)
            else it::getExpressionBody shouldBe block
        }


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

fun TreeNodeWrapper<Node, *>.number(primitiveType: PrimitiveType? = null, assertions: NodeSpec<ASTNumericLiteral> = EmptyAssertions) =
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
            it::isVarargs shouldBe false
            assertions()
        }

fun TreeNodeWrapper<Node, *>.varargsArrayDim(assertions: NodeSpec<ASTArrayTypeDim> = EmptyAssertions) =
        child<ASTArrayTypeDim> {
            it::isVarargs shouldBe true
            assertions()
        }

fun TreeNodeWrapper<Node, *>.arrayInitializer(assertions: NodeSpec<ASTArrayInitializer> = EmptyAssertions) =
        child<ASTArrayInitializer> {
            assertions()
        }

fun TreeNodeWrapper<Node, *>.arrayAlloc(assertions: NodeSpec<ASTArrayAllocation> = EmptyAssertions) =
        child<ASTArrayAllocation> {
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



fun TreeNodeWrapper<Node, *>.classDecl(simpleName: String, assertions: NodeSpec<ASTClassOrInterfaceDeclaration> = EmptyAssertions) =
        child<ASTClassOrInterfaceDeclaration>(ignoreChildren = assertions == EmptyAssertions) {
            it::getImage shouldBe simpleName

            assertions()
        }

fun TreeNodeWrapper<Node, *>.typeBody(contents: NodeSpec<ASTTypeBody> = EmptyAssertions) =
        child<ASTTypeBody>(ignoreChildren = contents == EmptyAssertions) {
            contents()
        }
