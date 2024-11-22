/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

// CHECKSTYLE:OFF
// Generated from net/sourceforge/pmd/lang/julia/ast/Julia.g4 by ANTLR 4.9.3
package net.sourceforge.pmd.lang.julia.ast;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link JuliaParser}.
 *
 * @deprecated Since 7.8.0. This class was never intended to be generated. It will be removed with no replacement.
 */
@Deprecated
@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public interface JuliaListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link JuliaParser#main}.
	 * @param ctx the parse tree
	 */
	void enterMain(JuliaParser.MainContext ctx);
	/**
	 * Exit a parse tree produced by {@link JuliaParser#main}.
	 * @param ctx the parse tree
	 */
	void exitMain(JuliaParser.MainContext ctx);
	/**
	 * Enter a parse tree produced by {@link JuliaParser#functionDefinition}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDefinition(JuliaParser.FunctionDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link JuliaParser#functionDefinition}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDefinition(JuliaParser.FunctionDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link JuliaParser#functionDefinition1}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDefinition1(JuliaParser.FunctionDefinition1Context ctx);
	/**
	 * Exit a parse tree produced by {@link JuliaParser#functionDefinition1}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDefinition1(JuliaParser.FunctionDefinition1Context ctx);
	/**
	 * Enter a parse tree produced by {@link JuliaParser#functionDefinition2}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDefinition2(JuliaParser.FunctionDefinition2Context ctx);
	/**
	 * Exit a parse tree produced by {@link JuliaParser#functionDefinition2}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDefinition2(JuliaParser.FunctionDefinition2Context ctx);
	/**
	 * Enter a parse tree produced by {@link JuliaParser#functionIdentifier}.
	 * @param ctx the parse tree
	 */
	void enterFunctionIdentifier(JuliaParser.FunctionIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link JuliaParser#functionIdentifier}.
	 * @param ctx the parse tree
	 */
	void exitFunctionIdentifier(JuliaParser.FunctionIdentifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link JuliaParser#whereClause}.
	 * @param ctx the parse tree
	 */
	void enterWhereClause(JuliaParser.WhereClauseContext ctx);
	/**
	 * Exit a parse tree produced by {@link JuliaParser#whereClause}.
	 * @param ctx the parse tree
	 */
	void exitWhereClause(JuliaParser.WhereClauseContext ctx);
	/**
	 * Enter a parse tree produced by {@link JuliaParser#functionBody}.
	 * @param ctx the parse tree
	 */
	void enterFunctionBody(JuliaParser.FunctionBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link JuliaParser#functionBody}.
	 * @param ctx the parse tree
	 */
	void exitFunctionBody(JuliaParser.FunctionBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link JuliaParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(JuliaParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JuliaParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(JuliaParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JuliaParser#beginStatement}.
	 * @param ctx the parse tree
	 */
	void enterBeginStatement(JuliaParser.BeginStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JuliaParser#beginStatement}.
	 * @param ctx the parse tree
	 */
	void exitBeginStatement(JuliaParser.BeginStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JuliaParser#doStatement}.
	 * @param ctx the parse tree
	 */
	void enterDoStatement(JuliaParser.DoStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JuliaParser#doStatement}.
	 * @param ctx the parse tree
	 */
	void exitDoStatement(JuliaParser.DoStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JuliaParser#forStatement}.
	 * @param ctx the parse tree
	 */
	void enterForStatement(JuliaParser.ForStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JuliaParser#forStatement}.
	 * @param ctx the parse tree
	 */
	void exitForStatement(JuliaParser.ForStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JuliaParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void enterIfStatement(JuliaParser.IfStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JuliaParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void exitIfStatement(JuliaParser.IfStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JuliaParser#letStatement}.
	 * @param ctx the parse tree
	 */
	void enterLetStatement(JuliaParser.LetStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JuliaParser#letStatement}.
	 * @param ctx the parse tree
	 */
	void exitLetStatement(JuliaParser.LetStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JuliaParser#macroStatement}.
	 * @param ctx the parse tree
	 */
	void enterMacroStatement(JuliaParser.MacroStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JuliaParser#macroStatement}.
	 * @param ctx the parse tree
	 */
	void exitMacroStatement(JuliaParser.MacroStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JuliaParser#structStatement}.
	 * @param ctx the parse tree
	 */
	void enterStructStatement(JuliaParser.StructStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JuliaParser#structStatement}.
	 * @param ctx the parse tree
	 */
	void exitStructStatement(JuliaParser.StructStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JuliaParser#tryCatchStatement}.
	 * @param ctx the parse tree
	 */
	void enterTryCatchStatement(JuliaParser.TryCatchStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JuliaParser#tryCatchStatement}.
	 * @param ctx the parse tree
	 */
	void exitTryCatchStatement(JuliaParser.TryCatchStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JuliaParser#typeStatement}.
	 * @param ctx the parse tree
	 */
	void enterTypeStatement(JuliaParser.TypeStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JuliaParser#typeStatement}.
	 * @param ctx the parse tree
	 */
	void exitTypeStatement(JuliaParser.TypeStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JuliaParser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void enterWhileStatement(JuliaParser.WhileStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link JuliaParser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void exitWhileStatement(JuliaParser.WhileStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link JuliaParser#anyToken}.
	 * @param ctx the parse tree
	 */
	void enterAnyToken(JuliaParser.AnyTokenContext ctx);
	/**
	 * Exit a parse tree produced by {@link JuliaParser#anyToken}.
	 * @param ctx the parse tree
	 */
	void exitAnyToken(JuliaParser.AnyTokenContext ctx);
}
