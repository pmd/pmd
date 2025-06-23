/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.coco.ast;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link CocoParser}.
 *
 * @deprecated Since 7.8.0. This class was never intended to be generated. It will be removed with no replacement.
 */
@Deprecated
@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public interface CocoListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link CocoParser#module}.
	 * @param ctx the parse tree
	 */
	void enterModule(CocoParser.ModuleContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#module}.
	 * @param ctx the parse tree
	 */
	void exitModule(CocoParser.ModuleContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration(CocoParser.DeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration(CocoParser.DeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#attribute}.
	 * @param ctx the parse tree
	 */
	void enterAttribute(CocoParser.AttributeContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#attribute}.
	 * @param ctx the parse tree
	 */
	void exitAttribute(CocoParser.AttributeContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#attributeDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterAttributeDeclaration(CocoParser.AttributeDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#attributeDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitAttributeDeclaration(CocoParser.AttributeDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#importDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterImportDeclaration(CocoParser.ImportDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#importDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitImportDeclaration(CocoParser.ImportDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaration(CocoParser.VariableDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaration(CocoParser.VariableDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#enumDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterEnumDeclaration(CocoParser.EnumDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#enumDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitEnumDeclaration(CocoParser.EnumDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#structDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterStructDeclaration(CocoParser.StructDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#structDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitStructDeclaration(CocoParser.StructDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#typeAliasDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterTypeAliasDeclaration(CocoParser.TypeAliasDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#typeAliasDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitTypeAliasDeclaration(CocoParser.TypeAliasDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#functionDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDeclaration(CocoParser.FunctionDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#functionDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDeclaration(CocoParser.FunctionDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#instanceDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterInstanceDeclaration(CocoParser.InstanceDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#instanceDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitInstanceDeclaration(CocoParser.InstanceDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#portDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterPortDeclaration(CocoParser.PortDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#portDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitPortDeclaration(CocoParser.PortDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#componentDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterComponentDeclaration(CocoParser.ComponentDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#componentDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitComponentDeclaration(CocoParser.ComponentDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#externalConstantDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterExternalConstantDeclaration(CocoParser.ExternalConstantDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#externalConstantDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitExternalConstantDeclaration(CocoParser.ExternalConstantDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#externalTypeDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterExternalTypeDeclaration(CocoParser.ExternalTypeDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#externalTypeDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitExternalTypeDeclaration(CocoParser.ExternalTypeDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#externalTypeElement}.
	 * @param ctx the parse tree
	 */
	void enterExternalTypeElement(CocoParser.ExternalTypeElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#externalTypeElement}.
	 * @param ctx the parse tree
	 */
	void exitExternalTypeElement(CocoParser.ExternalTypeElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#externalFunctionDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterExternalFunctionDeclaration(CocoParser.ExternalFunctionDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#externalFunctionDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitExternalFunctionDeclaration(CocoParser.ExternalFunctionDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#genericTypeDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterGenericTypeDeclaration(CocoParser.GenericTypeDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#genericTypeDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitGenericTypeDeclaration(CocoParser.GenericTypeDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#genericTypes}.
	 * @param ctx the parse tree
	 */
	void enterGenericTypes(CocoParser.GenericTypesContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#genericTypes}.
	 * @param ctx the parse tree
	 */
	void exitGenericTypes(CocoParser.GenericTypesContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#genericType}.
	 * @param ctx the parse tree
	 */
	void enterGenericType(CocoParser.GenericTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#genericType}.
	 * @param ctx the parse tree
	 */
	void exitGenericType(CocoParser.GenericTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#enumElement}.
	 * @param ctx the parse tree
	 */
	void enterEnumElement(CocoParser.EnumElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#enumElement}.
	 * @param ctx the parse tree
	 */
	void exitEnumElement(CocoParser.EnumElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#enumCase}.
	 * @param ctx the parse tree
	 */
	void enterEnumCase(CocoParser.EnumCaseContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#enumCase}.
	 * @param ctx the parse tree
	 */
	void exitEnumCase(CocoParser.EnumCaseContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#caseParameters}.
	 * @param ctx the parse tree
	 */
	void enterCaseParameters(CocoParser.CaseParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#caseParameters}.
	 * @param ctx the parse tree
	 */
	void exitCaseParameters(CocoParser.CaseParametersContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#caseParameter}.
	 * @param ctx the parse tree
	 */
	void enterCaseParameter(CocoParser.CaseParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#caseParameter}.
	 * @param ctx the parse tree
	 */
	void exitCaseParameter(CocoParser.CaseParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#structElement}.
	 * @param ctx the parse tree
	 */
	void enterStructElement(CocoParser.StructElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#structElement}.
	 * @param ctx the parse tree
	 */
	void exitStructElement(CocoParser.StructElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#fieldDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterFieldDeclaration(CocoParser.FieldDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#fieldDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitFieldDeclaration(CocoParser.FieldDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#componentElement}.
	 * @param ctx the parse tree
	 */
	void enterComponentElement(CocoParser.ComponentElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#componentElement}.
	 * @param ctx the parse tree
	 */
	void exitComponentElement(CocoParser.ComponentElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#staticMemberDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterStaticMemberDeclaration(CocoParser.StaticMemberDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#staticMemberDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitStaticMemberDeclaration(CocoParser.StaticMemberDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#constructorDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterConstructorDeclaration(CocoParser.ConstructorDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#constructorDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitConstructorDeclaration(CocoParser.ConstructorDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IfExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterIfExpression(CocoParser.IfExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IfExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitIfExpression(CocoParser.IfExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code TryOperatorExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterTryOperatorExpression(CocoParser.TryOperatorExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code TryOperatorExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitTryOperatorExpression(CocoParser.TryOperatorExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code UnaryOperatorExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterUnaryOperatorExpression(CocoParser.UnaryOperatorExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code UnaryOperatorExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitUnaryOperatorExpression(CocoParser.UnaryOperatorExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code OptionalExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterOptionalExpression(CocoParser.OptionalExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code OptionalExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitOptionalExpression(CocoParser.OptionalExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ArithmicOrLogicalExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterArithmicOrLogicalExpression(CocoParser.ArithmicOrLogicalExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ArithmicOrLogicalExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitArithmicOrLogicalExpression(CocoParser.ArithmicOrLogicalExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LiteralExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterLiteralExpression(CocoParser.LiteralExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LiteralExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitLiteralExpression(CocoParser.LiteralExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ArrayLiteralExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterArrayLiteralExpression(CocoParser.ArrayLiteralExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ArrayLiteralExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitArrayLiteralExpression(CocoParser.ArrayLiteralExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NondetExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterNondetExpression(CocoParser.NondetExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NondetExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitNondetExpression(CocoParser.NondetExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code GroupedExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterGroupedExpression(CocoParser.GroupedExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code GroupedExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitGroupedExpression(CocoParser.GroupedExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BlockExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBlockExpression(CocoParser.BlockExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BlockExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBlockExpression(CocoParser.BlockExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MatchExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterMatchExpression(CocoParser.MatchExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MatchExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitMatchExpression(CocoParser.MatchExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code StructLiteralExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterStructLiteralExpression(CocoParser.StructLiteralExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code StructLiteralExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitStructLiteralExpression(CocoParser.StructLiteralExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MemberReferenceExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterMemberReferenceExpression(CocoParser.MemberReferenceExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MemberReferenceExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitMemberReferenceExpression(CocoParser.MemberReferenceExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AssignmentExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentExpression(CocoParser.AssignmentExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AssignmentExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentExpression(CocoParser.AssignmentExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code VariableReferenceExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterVariableReferenceExpression(CocoParser.VariableReferenceExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code VariableReferenceExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitVariableReferenceExpression(CocoParser.VariableReferenceExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ImplicitMemberExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterImplicitMemberExpression(CocoParser.ImplicitMemberExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ImplicitMemberExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitImplicitMemberExpression(CocoParser.ImplicitMemberExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExternalFunction}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExternalFunction(CocoParser.ExternalFunctionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExternalFunction}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExternalFunction(CocoParser.ExternalFunctionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CastExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterCastExpression(CocoParser.CastExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CastExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitCastExpression(CocoParser.CastExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code StateInvariantExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterStateInvariantExpression(CocoParser.StateInvariantExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code StateInvariantExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitStateInvariantExpression(CocoParser.StateInvariantExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CallExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterCallExpression(CocoParser.CallExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CallExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitCallExpression(CocoParser.CallExpressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExternalLiteral}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExternalLiteral(CocoParser.ExternalLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExternalLiteral}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExternalLiteral(CocoParser.ExternalLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ArraySubscriptExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterArraySubscriptExpression(CocoParser.ArraySubscriptExpressionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ArraySubscriptExpression}
	 * labeled alternative in {@link CocoParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitArraySubscriptExpression(CocoParser.ArraySubscriptExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#blockExpression_}.
	 * @param ctx the parse tree
	 */
	void enterBlockExpression_(CocoParser.BlockExpression_Context ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#blockExpression_}.
	 * @param ctx the parse tree
	 */
	void exitBlockExpression_(CocoParser.BlockExpression_Context ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#ifExpression_}.
	 * @param ctx the parse tree
	 */
	void enterIfExpression_(CocoParser.IfExpression_Context ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#ifExpression_}.
	 * @param ctx the parse tree
	 */
	void exitIfExpression_(CocoParser.IfExpression_Context ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#matchExpression_}.
	 * @param ctx the parse tree
	 */
	void enterMatchExpression_(CocoParser.MatchExpression_Context ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#matchExpression_}.
	 * @param ctx the parse tree
	 */
	void exitMatchExpression_(CocoParser.MatchExpression_Context ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#nondetExpression_}.
	 * @param ctx the parse tree
	 */
	void enterNondetExpression_(CocoParser.NondetExpression_Context ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#nondetExpression_}.
	 * @param ctx the parse tree
	 */
	void exitNondetExpression_(CocoParser.NondetExpression_Context ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#fieldAssignments}.
	 * @param ctx the parse tree
	 */
	void enterFieldAssignments(CocoParser.FieldAssignmentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#fieldAssignments}.
	 * @param ctx the parse tree
	 */
	void exitFieldAssignments(CocoParser.FieldAssignmentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#fieldAssignment}.
	 * @param ctx the parse tree
	 */
	void enterFieldAssignment(CocoParser.FieldAssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#fieldAssignment}.
	 * @param ctx the parse tree
	 */
	void exitFieldAssignment(CocoParser.FieldAssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#nondetClauses}.
	 * @param ctx the parse tree
	 */
	void enterNondetClauses(CocoParser.NondetClausesContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#nondetClauses}.
	 * @param ctx the parse tree
	 */
	void exitNondetClauses(CocoParser.NondetClausesContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#nondetClause}.
	 * @param ctx the parse tree
	 */
	void enterNondetClause(CocoParser.NondetClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#nondetClause}.
	 * @param ctx the parse tree
	 */
	void exitNondetClause(CocoParser.NondetClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#matchClauses}.
	 * @param ctx the parse tree
	 */
	void enterMatchClauses(CocoParser.MatchClausesContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#matchClauses}.
	 * @param ctx the parse tree
	 */
	void exitMatchClauses(CocoParser.MatchClausesContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#matchClause}.
	 * @param ctx the parse tree
	 */
	void enterMatchClause(CocoParser.MatchClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#matchClause}.
	 * @param ctx the parse tree
	 */
	void exitMatchClause(CocoParser.MatchClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#pattern}.
	 * @param ctx the parse tree
	 */
	void enterPattern(CocoParser.PatternContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#pattern}.
	 * @param ctx the parse tree
	 */
	void exitPattern(CocoParser.PatternContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#enumCasePattern}.
	 * @param ctx the parse tree
	 */
	void enterEnumCasePattern(CocoParser.EnumCasePatternContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#enumCasePattern}.
	 * @param ctx the parse tree
	 */
	void exitEnumCasePattern(CocoParser.EnumCasePatternContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#idParameterPatterns}.
	 * @param ctx the parse tree
	 */
	void enterIdParameterPatterns(CocoParser.IdParameterPatternsContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#idParameterPatterns}.
	 * @param ctx the parse tree
	 */
	void exitIdParameterPatterns(CocoParser.IdParameterPatternsContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#idParameterPattern}.
	 * @param ctx the parse tree
	 */
	void enterIdParameterPattern(CocoParser.IdParameterPatternContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#idParameterPattern}.
	 * @param ctx the parse tree
	 */
	void exitIdParameterPattern(CocoParser.IdParameterPatternContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#variableDeclarationPattern}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclarationPattern(CocoParser.VariableDeclarationPatternContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#variableDeclarationPattern}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclarationPattern(CocoParser.VariableDeclarationPatternContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#parameterPatterns}.
	 * @param ctx the parse tree
	 */
	void enterParameterPatterns(CocoParser.ParameterPatternsContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#parameterPatterns}.
	 * @param ctx the parse tree
	 */
	void exitParameterPatterns(CocoParser.ParameterPatternsContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#parameterPattern}.
	 * @param ctx the parse tree
	 */
	void enterParameterPattern(CocoParser.ParameterPatternContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#parameterPattern}.
	 * @param ctx the parse tree
	 */
	void exitParameterPattern(CocoParser.ParameterPatternContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#expressions}.
	 * @param ctx the parse tree
	 */
	void enterExpressions(CocoParser.ExpressionsContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#expressions}.
	 * @param ctx the parse tree
	 */
	void exitExpressions(CocoParser.ExpressionsContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(CocoParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(CocoParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#declarationStatement}.
	 * @param ctx the parse tree
	 */
	void enterDeclarationStatement(CocoParser.DeclarationStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#declarationStatement}.
	 * @param ctx the parse tree
	 */
	void exitDeclarationStatement(CocoParser.DeclarationStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void enterReturnStatement(CocoParser.ReturnStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void exitReturnStatement(CocoParser.ReturnStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#becomeStatement}.
	 * @param ctx the parse tree
	 */
	void enterBecomeStatement(CocoParser.BecomeStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#becomeStatement}.
	 * @param ctx the parse tree
	 */
	void exitBecomeStatement(CocoParser.BecomeStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void enterWhileStatement(CocoParser.WhileStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void exitWhileStatement(CocoParser.WhileStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#forStatement}.
	 * @param ctx the parse tree
	 */
	void enterForStatement(CocoParser.ForStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#forStatement}.
	 * @param ctx the parse tree
	 */
	void exitForStatement(CocoParser.ForStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#breakStatement}.
	 * @param ctx the parse tree
	 */
	void enterBreakStatement(CocoParser.BreakStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#breakStatement}.
	 * @param ctx the parse tree
	 */
	void exitBreakStatement(CocoParser.BreakStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#continueStatement}.
	 * @param ctx the parse tree
	 */
	void enterContinueStatement(CocoParser.ContinueStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#continueStatement}.
	 * @param ctx the parse tree
	 */
	void exitContinueStatement(CocoParser.ContinueStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#portElement}.
	 * @param ctx the parse tree
	 */
	void enterPortElement(CocoParser.PortElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#portElement}.
	 * @param ctx the parse tree
	 */
	void exitPortElement(CocoParser.PortElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#functionInterfaceDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterFunctionInterfaceDeclaration(CocoParser.FunctionInterfaceDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#functionInterfaceDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitFunctionInterfaceDeclaration(CocoParser.FunctionInterfaceDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#signalDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterSignalDeclaration(CocoParser.SignalDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#signalDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitSignalDeclaration(CocoParser.SignalDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#stateMachineDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterStateMachineDeclaration(CocoParser.StateMachineDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#stateMachineDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitStateMachineDeclaration(CocoParser.StateMachineDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#stateMachineElement}.
	 * @param ctx the parse tree
	 */
	void enterStateMachineElement(CocoParser.StateMachineElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#stateMachineElement}.
	 * @param ctx the parse tree
	 */
	void exitStateMachineElement(CocoParser.StateMachineElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#stateDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterStateDeclaration(CocoParser.StateDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#stateDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitStateDeclaration(CocoParser.StateDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#eventStateDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterEventStateDeclaration(CocoParser.EventStateDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#eventStateDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitEventStateDeclaration(CocoParser.EventStateDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#executionStateDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterExecutionStateDeclaration(CocoParser.ExecutionStateDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#executionStateDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitExecutionStateDeclaration(CocoParser.ExecutionStateDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#eventStateElement}.
	 * @param ctx the parse tree
	 */
	void enterEventStateElement(CocoParser.EventStateElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#eventStateElement}.
	 * @param ctx the parse tree
	 */
	void exitEventStateElement(CocoParser.EventStateElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#entryFunctionDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterEntryFunctionDeclaration(CocoParser.EntryFunctionDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#entryFunctionDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitEntryFunctionDeclaration(CocoParser.EntryFunctionDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#exitFunctionDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterExitFunctionDeclaration(CocoParser.ExitFunctionDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#exitFunctionDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitExitFunctionDeclaration(CocoParser.ExitFunctionDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#stateInvariant}.
	 * @param ctx the parse tree
	 */
	void enterStateInvariant(CocoParser.StateInvariantContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#stateInvariant}.
	 * @param ctx the parse tree
	 */
	void exitStateInvariant(CocoParser.StateInvariantContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#transitionDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterTransitionDeclaration(CocoParser.TransitionDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#transitionDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitTransitionDeclaration(CocoParser.TransitionDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#eventTransition}.
	 * @param ctx the parse tree
	 */
	void enterEventTransition(CocoParser.EventTransitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#eventTransition}.
	 * @param ctx the parse tree
	 */
	void exitEventTransition(CocoParser.EventTransitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#eventSource}.
	 * @param ctx the parse tree
	 */
	void enterEventSource(CocoParser.EventSourceContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#eventSource}.
	 * @param ctx the parse tree
	 */
	void exitEventSource(CocoParser.EventSourceContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#spontaneousTransition}.
	 * @param ctx the parse tree
	 */
	void enterSpontaneousTransition(CocoParser.SpontaneousTransitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#spontaneousTransition}.
	 * @param ctx the parse tree
	 */
	void exitSpontaneousTransition(CocoParser.SpontaneousTransitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#timerTransition}.
	 * @param ctx the parse tree
	 */
	void enterTimerTransition(CocoParser.TimerTransitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#timerTransition}.
	 * @param ctx the parse tree
	 */
	void exitTimerTransition(CocoParser.TimerTransitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#eventHandler}.
	 * @param ctx the parse tree
	 */
	void enterEventHandler(CocoParser.EventHandlerContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#eventHandler}.
	 * @param ctx the parse tree
	 */
	void exitEventHandler(CocoParser.EventHandlerContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#offer}.
	 * @param ctx the parse tree
	 */
	void enterOffer(CocoParser.OfferContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#offer}.
	 * @param ctx the parse tree
	 */
	void exitOffer(CocoParser.OfferContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#offerClauses}.
	 * @param ctx the parse tree
	 */
	void enterOfferClauses(CocoParser.OfferClausesContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#offerClauses}.
	 * @param ctx the parse tree
	 */
	void exitOfferClauses(CocoParser.OfferClausesContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#offerClause}.
	 * @param ctx the parse tree
	 */
	void enterOfferClause(CocoParser.OfferClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#offerClause}.
	 * @param ctx the parse tree
	 */
	void exitOfferClause(CocoParser.OfferClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#parameters}.
	 * @param ctx the parse tree
	 */
	void enterParameters(CocoParser.ParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#parameters}.
	 * @param ctx the parse tree
	 */
	void exitParameters(CocoParser.ParametersContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#parameter}.
	 * @param ctx the parse tree
	 */
	void enterParameter(CocoParser.ParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#parameter}.
	 * @param ctx the parse tree
	 */
	void exitParameter(CocoParser.ParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#literalExpression_}.
	 * @param ctx the parse tree
	 */
	void enterLiteralExpression_(CocoParser.LiteralExpression_Context ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#literalExpression_}.
	 * @param ctx the parse tree
	 */
	void exitLiteralExpression_(CocoParser.LiteralExpression_Context ctx);
	/**
	 * Enter a parse tree produced by the {@code BinaryType}
	 * labeled alternative in {@link CocoParser#type}.
	 * @param ctx the parse tree
	 */
	void enterBinaryType(CocoParser.BinaryTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BinaryType}
	 * labeled alternative in {@link CocoParser#type}.
	 * @param ctx the parse tree
	 */
	void exitBinaryType(CocoParser.BinaryTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code GroupType}
	 * labeled alternative in {@link CocoParser#type}.
	 * @param ctx the parse tree
	 */
	void enterGroupType(CocoParser.GroupTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code GroupType}
	 * labeled alternative in {@link CocoParser#type}.
	 * @param ctx the parse tree
	 */
	void exitGroupType(CocoParser.GroupTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunctionType}
	 * labeled alternative in {@link CocoParser#type}.
	 * @param ctx the parse tree
	 */
	void enterFunctionType(CocoParser.FunctionTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunctionType}
	 * labeled alternative in {@link CocoParser#type}.
	 * @param ctx the parse tree
	 */
	void exitFunctionType(CocoParser.FunctionTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code UnaryType}
	 * labeled alternative in {@link CocoParser#type}.
	 * @param ctx the parse tree
	 */
	void enterUnaryType(CocoParser.UnaryTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code UnaryType}
	 * labeled alternative in {@link CocoParser#type}.
	 * @param ctx the parse tree
	 */
	void exitUnaryType(CocoParser.UnaryTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LiteralType}
	 * labeled alternative in {@link CocoParser#type}.
	 * @param ctx the parse tree
	 */
	void enterLiteralType(CocoParser.LiteralTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LiteralType}
	 * labeled alternative in {@link CocoParser#type}.
	 * @param ctx the parse tree
	 */
	void exitLiteralType(CocoParser.LiteralTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code TypeReference}
	 * labeled alternative in {@link CocoParser#type}.
	 * @param ctx the parse tree
	 */
	void enterTypeReference(CocoParser.TypeReferenceContext ctx);
	/**
	 * Exit a parse tree produced by the {@code TypeReference}
	 * labeled alternative in {@link CocoParser#type}.
	 * @param ctx the parse tree
	 */
	void exitTypeReference(CocoParser.TypeReferenceContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ReferenceType}
	 * labeled alternative in {@link CocoParser#type}.
	 * @param ctx the parse tree
	 */
	void enterReferenceType(CocoParser.ReferenceTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ReferenceType}
	 * labeled alternative in {@link CocoParser#type}.
	 * @param ctx the parse tree
	 */
	void exitReferenceType(CocoParser.ReferenceTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#types}.
	 * @param ctx the parse tree
	 */
	void enterTypes(CocoParser.TypesContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#types}.
	 * @param ctx the parse tree
	 */
	void exitTypes(CocoParser.TypesContext ctx);
	/**
	 * Enter a parse tree produced by {@link CocoParser#dotIdentifierList}.
	 * @param ctx the parse tree
	 */
	void enterDotIdentifierList(CocoParser.DotIdentifierListContext ctx);
	/**
	 * Exit a parse tree produced by {@link CocoParser#dotIdentifierList}.
	 * @param ctx the parse tree
	 */
	void exitDotIdentifierList(CocoParser.DotIdentifierListContext ctx);
}
