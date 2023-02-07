/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import com.github.oowekyala.treeutils.matchers.TreeNodeWrapper
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import net.sourceforge.pmd.lang.ast.Node
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken
import net.sourceforge.pmd.lang.ast.test.NodeSpec
import net.sourceforge.pmd.lang.ast.test.ValuedNodeSpec
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind.*
import net.sourceforge.pmd.util.IteratorUtil

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
        tokens().toList()

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


fun TreeNodeWrapper<Node, *>.annotation(simpleName: String, spec: NodeSpec<ASTAnnotation> = EmptyAssertions) =
        child<ASTAnnotation>(ignoreChildren = spec == EmptyAssertions) {
            it::getSimpleName shouldBe simpleName
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
            it::getName shouldBe name
            if (accessType != null) {
                it::getAccessType shouldBe accessType
            }
            otherAssertions(it)
        }

fun TreeNodeWrapper<Node, *>.fieldAccess(name: String, accessType: ASTAssignableExpr.AccessType? = null, otherAssertions: NodeSpec<ASTFieldAccess> = EmptyAssertions) =
        child<ASTFieldAccess>(ignoreChildren = otherAssertions == EmptyAssertions) {
            it::getName shouldBe name
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

fun TreeNodeWrapper<Node, *>.ifStatement(contents: NodeSpec<ASTIfStatement> = EmptyAssertions) =
        child<ASTIfStatement>(ignoreChildren = contents == EmptyAssertions) {
            contents()
        }

fun TreeNodeWrapper<Node, *>.returnStatement(contents: ValuedNodeSpec<ASTReturnStatement, ASTExpression>) =
        child<ASTReturnStatement> {
            it::getExpr shouldBe contents()
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

fun TreeNodeWrapper<Node, *>.continueStatement(label: String? = null, contents: NodeSpec<ASTContinueStatement> = EmptyAssertions) =
        child<ASTContinueStatement>(ignoreChildren = contents == EmptyAssertions) {
            it::getLabel shouldBe label
            contents()
        }

fun TreeNodeWrapper<Node, *>.labeledStatement(label: String, contents: ValuedNodeSpec<ASTLabeledStatement, ASTStatement>) =
        child<ASTLabeledStatement>(ignoreChildren = contents == EmptyAssertions) {
            it::getLabel shouldBe label
            it::getStatement shouldBe contents()
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
            it::getName shouldBe name
            it::getTypeBoundNode shouldBe contents()
        }

fun TreeNodeWrapper<Node, *>.classType(simpleName: String, contents: NodeSpec<ASTClassOrInterfaceType> = EmptyAssertions) =
        child<ASTClassOrInterfaceType>(ignoreChildren = contents == EmptyAssertions) {
            it::getSimpleName shouldBe simpleName
            contents()
        }

fun TreeNodeWrapper<Node, *>.qualClassType(canoName: String, contents: NodeSpec<ASTClassOrInterfaceType> = EmptyAssertions) =
        child<ASTClassOrInterfaceType>(ignoreChildren = contents == EmptyAssertions) {
            val simpleName = canoName.substringAfterLast('.')
            it::getSimpleName shouldBe simpleName
            it.text.toString() shouldBe canoName
            contents()
        }

fun TreeNodeWrapper<Node, *>.unionType(contents: NodeSpec<ASTUnionType> = EmptyAssertions) =
        child<ASTUnionType>(ignoreChildren = contents == EmptyAssertions) {
            contents()
        }

fun TreeNodeWrapper<Node, *>.voidType() = child<ASTVoidType>() {}


fun TreeNodeWrapper<Node, *>.typeExpr(contents: ValuedNodeSpec<ASTTypeExpression, ASTType>) =
        child<ASTTypeExpression>(ignoreChildren = contents == EmptyAssertions) {
            it::getTypeNode shouldBe contents()
        }

fun TreeNodeWrapper<Node, *>.patternExpr(contents: ValuedNodeSpec<ASTPatternExpression, ASTPattern>) =
        child<ASTPatternExpression>(ignoreChildren = contents == EmptyAssertions) {
            it::getPattern shouldBe contents()
        }
fun TreeNodeWrapper<Node, *>.typePattern(contents: NodeSpec<ASTTypePattern>) =
        child<ASTTypePattern>(ignoreChildren = contents == EmptyAssertions) {
            contents()
        }
fun TreeNodeWrapper<Node, *>.guardedPattern(contents: NodeSpec<ASTGuardedPattern>) =
        child<ASTGuardedPattern>(ignoreChildren = contents == EmptyAssertions) {
            contents()
        }


fun TreeNodeWrapper<Node, *>.arrayType(contents: NodeSpec<ASTArrayType> = EmptyAssertions) =
        child<ASTArrayType>(ignoreChildren = contents == EmptyAssertions) {
            contents()
        }


fun TreeNodeWrapper<Node, *>.primitiveType(type: PrimitiveTypeKind, assertions: NodeSpec<ASTPrimitiveType> = EmptyAssertions) =
        child<ASTPrimitiveType> {
            it::getKind shouldBe type
            it::getTypeImage shouldBe type.toString()
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
            it::isEmpty shouldBe it.constValue.isEmpty()
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
            it::isEmpty shouldBe it.constValue.isEmpty()
            contents()
        }

fun TreeNodeWrapper<Node, *>.classLiteral(contents: ValuedNodeSpec<ASTClassLiteral, ASTType>) =
        child<ASTClassLiteral> {
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


fun TreeNodeWrapper<Node, *>.methodCall(methodName: String, assertions: NodeSpec<ASTMethodCall> = EmptyAssertions) =
        child<ASTMethodCall>(ignoreChildren = assertions == EmptyAssertions) {
            it::getMethodName shouldBe methodName
            assertions()
        }

fun TreeNodeWrapper<Node, out QualifiableExpression>.skipQualifier() =
        it::getQualifier shouldBe unspecifiedChild()

fun TreeNodeWrapper<Node, *>.argList(assertions: NodeSpec<ASTArgumentList> = EmptyAssertions) =
        child<ASTArgumentList> {
            assertions()
        }

fun TreeNodeWrapper<Node, *>.methodRef(methodName: String, assertions: NodeSpec<ASTMethodReference> = EmptyAssertions) =
        child<ASTMethodReference>(ignoreChildren = assertions === EmptyAssertions) {
            it::getMethodName shouldBe methodName
            it::isConstructorReference shouldBe false
            assertions()
        }

fun TreeNodeWrapper<Node, *>.constructorRef(assertions: ValuedNodeSpec<ASTMethodReference, ASTTypeExpression>) =
        child<ASTMethodReference> {
            it::getMethodName shouldBe "new"
            it::isConstructorReference shouldBe true
            it::getQualifier shouldBe assertions()
        }

val EmptyAssertions: NodeSpec<out Node> = {}

fun TreeNodeWrapper<Node, *>.ternaryExpr(assertions: NodeSpec<ASTConditionalExpression> = EmptyAssertions) =
        child<ASTConditionalExpression>(ignoreChildren = assertions == EmptyAssertions) {
            assertions()
        }

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
            it.exprList.toList() shouldBe emptyList()
            assertions()
        }

fun TreeNodeWrapper<Node, *>.number(typeKind: PrimitiveTypeKind? = null, assertions: NodeSpec<ASTNumericLiteral> = EmptyAssertions) =
        child<ASTNumericLiteral> {
            if (typeKind != null) {

                it::isIntLiteral shouldBe (typeKind == INT)
                it::isDoubleLiteral shouldBe (typeKind == DOUBLE)
                it::isFloatLiteral shouldBe (typeKind == FLOAT)
                it::isLongLiteral shouldBe (typeKind == LONG)
            }

            assertions()
        }

fun TreeNodeWrapper<Node, *>.int(value: Int? = null, assertions: NodeSpec<ASTNumericLiteral> = EmptyAssertions) =
        number(typeKind = INT) {
            if (value != null) {
                it::getValueAsInt shouldBe value
            }

            assertions()
        }

fun TreeNodeWrapper<Node, *>.char(value: Char? = null, assertions: NodeSpec<ASTCharLiteral> = EmptyAssertions) =
        child<ASTCharLiteral> {
            if (value != null) {
                it::getConstValue shouldBe value
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

fun TreeNodeWrapper<Node, *>.arrayDimList(assertions: NodeSpec<ASTArrayDimensions> = EmptyAssertions) =
        child<ASTArrayDimensions>(ignoreChildren = assertions == EmptyAssertions) {
            assertions()
        }


fun TreeNodeWrapper<Node, *>.arrayInitializer(assertions: NodeSpec<ASTArrayInitializer> = EmptyAssertions) =
        child<ASTArrayInitializer> {
            assertions()
        }

fun TreeNodeWrapper<Node, *>.arrayAlloc(assertions: NodeSpec<ASTArrayAllocation> = EmptyAssertions) =
        child<ASTArrayAllocation>(ignoreChildren = assertions == EmptyAssertions) {
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
