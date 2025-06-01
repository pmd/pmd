/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

// CHECKSTYLE:OFF
// Generated from net/sourceforge/pmd/lang/julia/ast/Julia.g4 by ANTLR 4.9.3
package net.sourceforge.pmd.lang.julia.ast;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link JuliaParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 *
 * @deprecated Since 7.8.0. This class was never intended to be generated. It will be removed with no replacement.
 */
@Deprecated
@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
public interface JuliaVisitor<T> extends ParseTreeVisitor<T> {
    /**
     * Visit a parse tree produced by {@link JuliaParser#main}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMain(JuliaParser.MainContext ctx);
    /**
     * Visit a parse tree produced by {@link JuliaParser#functionDefinition}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFunctionDefinition(JuliaParser.FunctionDefinitionContext ctx);
    /**
     * Visit a parse tree produced by {@link JuliaParser#functionDefinition1}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFunctionDefinition1(JuliaParser.FunctionDefinition1Context ctx);
    /**
     * Visit a parse tree produced by {@link JuliaParser#functionDefinition2}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFunctionDefinition2(JuliaParser.FunctionDefinition2Context ctx);
    /**
     * Visit a parse tree produced by {@link JuliaParser#functionIdentifier}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFunctionIdentifier(JuliaParser.FunctionIdentifierContext ctx);
    /**
     * Visit a parse tree produced by {@link JuliaParser#whereClause}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitWhereClause(JuliaParser.WhereClauseContext ctx);
    /**
     * Visit a parse tree produced by {@link JuliaParser#functionBody}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitFunctionBody(JuliaParser.FunctionBodyContext ctx);
    /**
     * Visit a parse tree produced by {@link JuliaParser#statement}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitStatement(JuliaParser.StatementContext ctx);
    /**
     * Visit a parse tree produced by {@link JuliaParser#beginStatement}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitBeginStatement(JuliaParser.BeginStatementContext ctx);
    /**
     * Visit a parse tree produced by {@link JuliaParser#doStatement}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitDoStatement(JuliaParser.DoStatementContext ctx);
    /**
     * Visit a parse tree produced by {@link JuliaParser#forStatement}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitForStatement(JuliaParser.ForStatementContext ctx);
    /**
     * Visit a parse tree produced by {@link JuliaParser#ifStatement}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitIfStatement(JuliaParser.IfStatementContext ctx);
    /**
     * Visit a parse tree produced by {@link JuliaParser#letStatement}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitLetStatement(JuliaParser.LetStatementContext ctx);
    /**
     * Visit a parse tree produced by {@link JuliaParser#macroStatement}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitMacroStatement(JuliaParser.MacroStatementContext ctx);
    /**
     * Visit a parse tree produced by {@link JuliaParser#structStatement}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitStructStatement(JuliaParser.StructStatementContext ctx);
    /**
     * Visit a parse tree produced by {@link JuliaParser#tryCatchStatement}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTryCatchStatement(JuliaParser.TryCatchStatementContext ctx);
    /**
     * Visit a parse tree produced by {@link JuliaParser#typeStatement}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitTypeStatement(JuliaParser.TypeStatementContext ctx);
    /**
     * Visit a parse tree produced by {@link JuliaParser#whileStatement}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitWhileStatement(JuliaParser.WhileStatementContext ctx);
    /**
     * Visit a parse tree produced by {@link JuliaParser#anyToken}.
     * @param ctx the parse tree
     * @return the visitor result
     */
    T visitAnyToken(JuliaParser.AnyTokenContext ctx);
}
