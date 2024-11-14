/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

// CHECKSTYLE:OFF
// Generated from net/sourceforge/pmd/lang/gherkin/ast/Gherkin.g4 by ANTLR 4.9.3
package net.sourceforge.pmd.lang.gherkin.ast;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link GherkinParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 *
 * @deprecated Since 7.8.0. This class was never intended to be generated. It will be removed with no replacement.
 */

@Deprecated
@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public interface GherkinVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link GherkinParser#main}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMain(GherkinParser.MainContext ctx);
	/**
	 * Visit a parse tree produced by {@link GherkinParser#feature}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFeature(GherkinParser.FeatureContext ctx);
	/**
	 * Visit a parse tree produced by {@link GherkinParser#instructionLine}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInstructionLine(GherkinParser.InstructionLineContext ctx);
	/**
	 * Visit a parse tree produced by {@link GherkinParser#instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInstruction(GherkinParser.InstructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link GherkinParser#stepInstruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStepInstruction(GherkinParser.StepInstructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link GherkinParser#background}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBackground(GherkinParser.BackgroundContext ctx);
	/**
	 * Visit a parse tree produced by {@link GherkinParser#rulex}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRulex(GherkinParser.RulexContext ctx);
	/**
	 * Visit a parse tree produced by {@link GherkinParser#scenario}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScenario(GherkinParser.ScenarioContext ctx);
	/**
	 * Visit a parse tree produced by {@link GherkinParser#scenarioOutline}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScenarioOutline(GherkinParser.ScenarioOutlineContext ctx);
	/**
	 * Visit a parse tree produced by {@link GherkinParser#step}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStep(GherkinParser.StepContext ctx);
	/**
	 * Visit a parse tree produced by {@link GherkinParser#stepItem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStepItem(GherkinParser.StepItemContext ctx);
	/**
	 * Visit a parse tree produced by {@link GherkinParser#tagline}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTagline(GherkinParser.TaglineContext ctx);
	/**
	 * Visit a parse tree produced by {@link GherkinParser#and}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnd(GherkinParser.AndContext ctx);
	/**
	 * Visit a parse tree produced by {@link GherkinParser#anystep}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnystep(GherkinParser.AnystepContext ctx);
	/**
	 * Visit a parse tree produced by {@link GherkinParser#but}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBut(GherkinParser.ButContext ctx);
	/**
	 * Visit a parse tree produced by {@link GherkinParser#datatable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDatatable(GherkinParser.DatatableContext ctx);
	/**
	 * Visit a parse tree produced by {@link GherkinParser#given}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGiven(GherkinParser.GivenContext ctx);
	/**
	 * Visit a parse tree produced by {@link GherkinParser#then}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitThen(GherkinParser.ThenContext ctx);
	/**
	 * Visit a parse tree produced by {@link GherkinParser#when}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhen(GherkinParser.WhenContext ctx);
	/**
	 * Visit a parse tree produced by {@link GherkinParser#examples}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExamples(GherkinParser.ExamplesContext ctx);
	/**
	 * Visit a parse tree produced by {@link GherkinParser#instructionDescription}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInstructionDescription(GherkinParser.InstructionDescriptionContext ctx);
	/**
	 * Visit a parse tree produced by {@link GherkinParser#stepDescription}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStepDescription(GherkinParser.StepDescriptionContext ctx);
	/**
	 * Visit a parse tree produced by {@link GherkinParser#description}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDescription(GherkinParser.DescriptionContext ctx);
	/**
	 * Visit a parse tree produced by {@link GherkinParser#text}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitText(GherkinParser.TextContext ctx);
}