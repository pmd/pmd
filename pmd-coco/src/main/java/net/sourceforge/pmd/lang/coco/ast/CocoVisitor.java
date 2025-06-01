/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

// CHECKSTYLE:OFF
// Generated from net/sourceforge/pmd/lang/coco/ast/Coco.g4 by ANTLR 4.9.3
package net.sourceforge.pmd.lang.coco.ast;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link CocoParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 *
 * @deprecated Since 7.8.0. This class was never intended to be generated. It will be removed with no replacement.
 */
@Deprecated
@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
public interface CocoVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link CocoParser#module}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitModule(CocoParser.ModuleContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#declaration}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDeclaration(CocoParser.DeclarationContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#attribute}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAttribute(CocoParser.AttributeContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#attributeDeclaration}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAttributeDeclaration(CocoParser.AttributeDeclarationContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#importDeclaration}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitImportDeclaration(CocoParser.ImportDeclarationContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#variableDeclaration}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitVariableDeclaration(CocoParser.VariableDeclarationContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#enumDeclaration}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEnumDeclaration(CocoParser.EnumDeclarationContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#structDeclaration}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitStructDeclaration(CocoParser.StructDeclarationContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#typeAliasDeclaration}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTypeAliasDeclaration(CocoParser.TypeAliasDeclarationContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#functionDeclaration}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFunctionDeclaration(CocoParser.FunctionDeclarationContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#instanceDeclaration}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitInstanceDeclaration(CocoParser.InstanceDeclarationContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#portDeclaration}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPortDeclaration(CocoParser.PortDeclarationContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#componentDeclaration}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitComponentDeclaration(CocoParser.ComponentDeclarationContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#externalConstantDeclaration}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExternalConstantDeclaration(CocoParser.ExternalConstantDeclarationContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#externalTypeDeclaration}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExternalTypeDeclaration(CocoParser.ExternalTypeDeclarationContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#externalTypeElement}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExternalTypeElement(CocoParser.ExternalTypeElementContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#externalFunctionDeclaration}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExternalFunctionDeclaration(CocoParser.ExternalFunctionDeclarationContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#genericTypeDeclaration}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitGenericTypeDeclaration(CocoParser.GenericTypeDeclarationContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#genericTypes}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitGenericTypes(CocoParser.GenericTypesContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#genericType}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitGenericType(CocoParser.GenericTypeContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#enumElement}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEnumElement(CocoParser.EnumElementContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#enumCase}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEnumCase(CocoParser.EnumCaseContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#caseParameters}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCaseParameters(CocoParser.CaseParametersContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#caseParameter}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCaseParameter(CocoParser.CaseParameterContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#structElement}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitStructElement(CocoParser.StructElementContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#fieldDeclaration}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFieldDeclaration(CocoParser.FieldDeclarationContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#componentElement}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitComponentElement(CocoParser.ComponentElementContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#staticMemberDeclaration}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitStaticMemberDeclaration(CocoParser.StaticMemberDeclarationContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#constructorDeclaration}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitConstructorDeclaration(CocoParser.ConstructorDeclarationContext ctx);
    /**
     * Visit a parse tree produced by the {@code IfExpression}
     * labeled alternative in {@link CocoParser#expression}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitIfExpression(CocoParser.IfExpressionContext ctx);
    /**
     * Visit a parse tree produced by the {@code TryOperatorExpression}
     * labeled alternative in {@link CocoParser#expression}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTryOperatorExpression(CocoParser.TryOperatorExpressionContext ctx);
    /**
     * Visit a parse tree produced by the {@code UnaryOperatorExpression}
     * labeled alternative in {@link CocoParser#expression}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitUnaryOperatorExpression(CocoParser.UnaryOperatorExpressionContext ctx);
    /**
     * Visit a parse tree produced by the {@code OptionalExpression}
     * labeled alternative in {@link CocoParser#expression}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOptionalExpression(CocoParser.OptionalExpressionContext ctx);
    /**
     * Visit a parse tree produced by the {@code ArithmicOrLogicalExpression}
     * labeled alternative in {@link CocoParser#expression}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitArithmicOrLogicalExpression(CocoParser.ArithmicOrLogicalExpressionContext ctx);
    /**
     * Visit a parse tree produced by the {@code LiteralExpression}
     * labeled alternative in {@link CocoParser#expression}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLiteralExpression(CocoParser.LiteralExpressionContext ctx);
    /**
     * Visit a parse tree produced by the {@code ArrayLiteralExpression}
     * labeled alternative in {@link CocoParser#expression}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitArrayLiteralExpression(CocoParser.ArrayLiteralExpressionContext ctx);
    /**
     * Visit a parse tree produced by the {@code NondetExpression}
     * labeled alternative in {@link CocoParser#expression}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitNondetExpression(CocoParser.NondetExpressionContext ctx);
    /**
     * Visit a parse tree produced by the {@code GroupedExpression}
     * labeled alternative in {@link CocoParser#expression}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitGroupedExpression(CocoParser.GroupedExpressionContext ctx);
    /**
     * Visit a parse tree produced by the {@code BlockExpression}
     * labeled alternative in {@link CocoParser#expression}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBlockExpression(CocoParser.BlockExpressionContext ctx);
    /**
     * Visit a parse tree produced by the {@code MatchExpression}
     * labeled alternative in {@link CocoParser#expression}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMatchExpression(CocoParser.MatchExpressionContext ctx);
    /**
     * Visit a parse tree produced by the {@code StructLiteralExpression}
     * labeled alternative in {@link CocoParser#expression}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitStructLiteralExpression(CocoParser.StructLiteralExpressionContext ctx);
    /**
     * Visit a parse tree produced by the {@code MemberReferenceExpression}
     * labeled alternative in {@link CocoParser#expression}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMemberReferenceExpression(CocoParser.MemberReferenceExpressionContext ctx);
    /**
     * Visit a parse tree produced by the {@code AssignmentExpression}
     * labeled alternative in {@link CocoParser#expression}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAssignmentExpression(CocoParser.AssignmentExpressionContext ctx);
    /**
     * Visit a parse tree produced by the {@code VariableReferenceExpression}
     * labeled alternative in {@link CocoParser#expression}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitVariableReferenceExpression(CocoParser.VariableReferenceExpressionContext ctx);
    /**
     * Visit a parse tree produced by the {@code ImplicitMemberExpression}
     * labeled alternative in {@link CocoParser#expression}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitImplicitMemberExpression(CocoParser.ImplicitMemberExpressionContext ctx);
    /**
     * Visit a parse tree produced by the {@code ExternalFunction}
     * labeled alternative in {@link CocoParser#expression}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExternalFunction(CocoParser.ExternalFunctionContext ctx);
    /**
     * Visit a parse tree produced by the {@code CastExpression}
     * labeled alternative in {@link CocoParser#expression}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCastExpression(CocoParser.CastExpressionContext ctx);
    /**
     * Visit a parse tree produced by the {@code StateInvariantExpression}
     * labeled alternative in {@link CocoParser#expression}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitStateInvariantExpression(CocoParser.StateInvariantExpressionContext ctx);
    /**
     * Visit a parse tree produced by the {@code CallExpression}
     * labeled alternative in {@link CocoParser#expression}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitCallExpression(CocoParser.CallExpressionContext ctx);
    /**
     * Visit a parse tree produced by the {@code ExternalLiteral}
     * labeled alternative in {@link CocoParser#expression}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExternalLiteral(CocoParser.ExternalLiteralContext ctx);
    /**
     * Visit a parse tree produced by the {@code ArraySubscriptExpression}
     * labeled alternative in {@link CocoParser#expression}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitArraySubscriptExpression(CocoParser.ArraySubscriptExpressionContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#blockExpression_}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBlockExpression_(CocoParser.BlockExpression_Context ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#ifExpression_}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitIfExpression_(CocoParser.IfExpression_Context ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#matchExpression_}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMatchExpression_(CocoParser.MatchExpression_Context ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#nondetExpression_}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitNondetExpression_(CocoParser.NondetExpression_Context ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#fieldAssignments}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFieldAssignments(CocoParser.FieldAssignmentsContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#fieldAssignment}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFieldAssignment(CocoParser.FieldAssignmentContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#nondetClauses}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitNondetClauses(CocoParser.NondetClausesContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#nondetClause}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitNondetClause(CocoParser.NondetClauseContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#matchClauses}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMatchClauses(CocoParser.MatchClausesContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#matchClause}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMatchClause(CocoParser.MatchClauseContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#pattern}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPattern(CocoParser.PatternContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#enumCasePattern}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEnumCasePattern(CocoParser.EnumCasePatternContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#idParameterPatterns}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitIdParameterPatterns(CocoParser.IdParameterPatternsContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#idParameterPattern}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitIdParameterPattern(CocoParser.IdParameterPatternContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#variableDeclarationPattern}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitVariableDeclarationPattern(CocoParser.VariableDeclarationPatternContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#parameterPatterns}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitParameterPatterns(CocoParser.ParameterPatternsContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#parameterPattern}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitParameterPattern(CocoParser.ParameterPatternContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#expressions}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExpressions(CocoParser.ExpressionsContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#statement}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitStatement(CocoParser.StatementContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#declarationStatement}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDeclarationStatement(CocoParser.DeclarationStatementContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#returnStatement}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitReturnStatement(CocoParser.ReturnStatementContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#becomeStatement}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBecomeStatement(CocoParser.BecomeStatementContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#whileStatement}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitWhileStatement(CocoParser.WhileStatementContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#forStatement}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitForStatement(CocoParser.ForStatementContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#breakStatement}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBreakStatement(CocoParser.BreakStatementContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#continueStatement}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitContinueStatement(CocoParser.ContinueStatementContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#portElement}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitPortElement(CocoParser.PortElementContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#functionInterfaceDeclaration}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFunctionInterfaceDeclaration(CocoParser.FunctionInterfaceDeclarationContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#signalDeclaration}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSignalDeclaration(CocoParser.SignalDeclarationContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#stateMachineDeclaration}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitStateMachineDeclaration(CocoParser.StateMachineDeclarationContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#stateMachineElement}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitStateMachineElement(CocoParser.StateMachineElementContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#stateDeclaration}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitStateDeclaration(CocoParser.StateDeclarationContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#eventStateDeclaration}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEventStateDeclaration(CocoParser.EventStateDeclarationContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#executionStateDeclaration}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExecutionStateDeclaration(CocoParser.ExecutionStateDeclarationContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#eventStateElement}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEventStateElement(CocoParser.EventStateElementContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#entryFunctionDeclaration}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEntryFunctionDeclaration(CocoParser.EntryFunctionDeclarationContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#exitFunctionDeclaration}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitExitFunctionDeclaration(CocoParser.ExitFunctionDeclarationContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#stateInvariant}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitStateInvariant(CocoParser.StateInvariantContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#transitionDeclaration}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTransitionDeclaration(CocoParser.TransitionDeclarationContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#eventTransition}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEventTransition(CocoParser.EventTransitionContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#eventSource}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEventSource(CocoParser.EventSourceContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#spontaneousTransition}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitSpontaneousTransition(CocoParser.SpontaneousTransitionContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#timerTransition}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTimerTransition(CocoParser.TimerTransitionContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#eventHandler}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitEventHandler(CocoParser.EventHandlerContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#offer}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOffer(CocoParser.OfferContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#offerClauses}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOfferClauses(CocoParser.OfferClausesContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#offerClause}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitOfferClause(CocoParser.OfferClauseContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#parameters}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitParameters(CocoParser.ParametersContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#parameter}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitParameter(CocoParser.ParameterContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#literalExpression_}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLiteralExpression_(CocoParser.LiteralExpression_Context ctx);
    /**
     * Visit a parse tree produced by the {@code BinaryType}
     * labeled alternative in {@link CocoParser#type}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBinaryType(CocoParser.BinaryTypeContext ctx);
    /**
     * Visit a parse tree produced by the {@code GroupType}
     * labeled alternative in {@link CocoParser#type}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitGroupType(CocoParser.GroupTypeContext ctx);
    /**
     * Visit a parse tree produced by the {@code FunctionType}
     * labeled alternative in {@link CocoParser#type}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFunctionType(CocoParser.FunctionTypeContext ctx);
    /**
     * Visit a parse tree produced by the {@code UnaryType}
     * labeled alternative in {@link CocoParser#type}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitUnaryType(CocoParser.UnaryTypeContext ctx);
    /**
     * Visit a parse tree produced by the {@code LiteralType}
     * labeled alternative in {@link CocoParser#type}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLiteralType(CocoParser.LiteralTypeContext ctx);
    /**
     * Visit a parse tree produced by the {@code TypeReference}
     * labeled alternative in {@link CocoParser#type}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTypeReference(CocoParser.TypeReferenceContext ctx);
    /**
     * Visit a parse tree produced by the {@code ReferenceType}
     * labeled alternative in {@link CocoParser#type}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitReferenceType(CocoParser.ReferenceTypeContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#types}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTypes(CocoParser.TypesContext ctx);
    /**
     * Visit a parse tree produced by {@link CocoParser#dotIdentifierList}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDotIdentifierList(CocoParser.DotIdentifierListContext ctx);
}
