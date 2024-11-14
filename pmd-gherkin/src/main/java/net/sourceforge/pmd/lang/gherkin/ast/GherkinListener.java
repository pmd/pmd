/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

// CHECKSTYLE:OFF
// Generated from net/sourceforge/pmd/lang/gherkin/ast/Gherkin.g4 by ANTLR 4.9.3
package net.sourceforge.pmd.lang.gherkin.ast;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link GherkinParser}.
 *
 * @deprecated Since 7.8.0. This class was never intended to be generated. It will be removed with no replacement.
 */

@Deprecated
@net.sourceforge.pmd.annotation.Generated("org.antlr.v4.Tool")
 public interface GherkinListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link GherkinParser#main}.
	 * @param ctx the parse tree
	 */
	void enterMain(GherkinParser.MainContext ctx);
	/**
	 * Exit a parse tree produced by {@link GherkinParser#main}.
	 * @param ctx the parse tree
	 */
	void exitMain(GherkinParser.MainContext ctx);
	/**
	 * Enter a parse tree produced by {@link GherkinParser#feature}.
	 * @param ctx the parse tree
	 */
	void enterFeature(GherkinParser.FeatureContext ctx);
	/**
	 * Exit a parse tree produced by {@link GherkinParser#feature}.
	 * @param ctx the parse tree
	 */
	void exitFeature(GherkinParser.FeatureContext ctx);
	/**
	 * Enter a parse tree produced by {@link GherkinParser#instructionLine}.
	 * @param ctx the parse tree
	 */
	void enterInstructionLine(GherkinParser.InstructionLineContext ctx);
	/**
	 * Exit a parse tree produced by {@link GherkinParser#instructionLine}.
	 * @param ctx the parse tree
	 */
	void exitInstructionLine(GherkinParser.InstructionLineContext ctx);
	/**
	 * Enter a parse tree produced by {@link GherkinParser#instruction}.
	 * @param ctx the parse tree
	 */
	void enterInstruction(GherkinParser.InstructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link GherkinParser#instruction}.
	 * @param ctx the parse tree
	 */
	void exitInstruction(GherkinParser.InstructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link GherkinParser#stepInstruction}.
	 * @param ctx the parse tree
	 */
	void enterStepInstruction(GherkinParser.StepInstructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link GherkinParser#stepInstruction}.
	 * @param ctx the parse tree
	 */
	void exitStepInstruction(GherkinParser.StepInstructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link GherkinParser#background}.
	 * @param ctx the parse tree
	 */
	void enterBackground(GherkinParser.BackgroundContext ctx);
	/**
	 * Exit a parse tree produced by {@link GherkinParser#background}.
	 * @param ctx the parse tree
	 */
	void exitBackground(GherkinParser.BackgroundContext ctx);
	/**
	 * Enter a parse tree produced by {@link GherkinParser#rulex}.
	 * @param ctx the parse tree
	 */
	void enterRulex(GherkinParser.RulexContext ctx);
	/**
	 * Exit a parse tree produced by {@link GherkinParser#rulex}.
	 * @param ctx the parse tree
	 */
	void exitRulex(GherkinParser.RulexContext ctx);
	/**
	 * Enter a parse tree produced by {@link GherkinParser#scenario}.
	 * @param ctx the parse tree
	 */
	void enterScenario(GherkinParser.ScenarioContext ctx);
	/**
	 * Exit a parse tree produced by {@link GherkinParser#scenario}.
	 * @param ctx the parse tree
	 */
	void exitScenario(GherkinParser.ScenarioContext ctx);
	/**
	 * Enter a parse tree produced by {@link GherkinParser#scenarioOutline}.
	 * @param ctx the parse tree
	 */
	void enterScenarioOutline(GherkinParser.ScenarioOutlineContext ctx);
	/**
	 * Exit a parse tree produced by {@link GherkinParser#scenarioOutline}.
	 * @param ctx the parse tree
	 */
	void exitScenarioOutline(GherkinParser.ScenarioOutlineContext ctx);
	/**
	 * Enter a parse tree produced by {@link GherkinParser#step}.
	 * @param ctx the parse tree
	 */
	void enterStep(GherkinParser.StepContext ctx);
	/**
	 * Exit a parse tree produced by {@link GherkinParser#step}.
	 * @param ctx the parse tree
	 */
	void exitStep(GherkinParser.StepContext ctx);
	/**
	 * Enter a parse tree produced by {@link GherkinParser#stepItem}.
	 * @param ctx the parse tree
	 */
	void enterStepItem(GherkinParser.StepItemContext ctx);
	/**
	 * Exit a parse tree produced by {@link GherkinParser#stepItem}.
	 * @param ctx the parse tree
	 */
	void exitStepItem(GherkinParser.StepItemContext ctx);
	/**
	 * Enter a parse tree produced by {@link GherkinParser#tagline}.
	 * @param ctx the parse tree
	 */
	void enterTagline(GherkinParser.TaglineContext ctx);
	/**
	 * Exit a parse tree produced by {@link GherkinParser#tagline}.
	 * @param ctx the parse tree
	 */
	void exitTagline(GherkinParser.TaglineContext ctx);
	/**
	 * Enter a parse tree produced by {@link GherkinParser#and}.
	 * @param ctx the parse tree
	 */
	void enterAnd(GherkinParser.AndContext ctx);
	/**
	 * Exit a parse tree produced by {@link GherkinParser#and}.
	 * @param ctx the parse tree
	 */
	void exitAnd(GherkinParser.AndContext ctx);
	/**
	 * Enter a parse tree produced by {@link GherkinParser#anystep}.
	 * @param ctx the parse tree
	 */
	void enterAnystep(GherkinParser.AnystepContext ctx);
	/**
	 * Exit a parse tree produced by {@link GherkinParser#anystep}.
	 * @param ctx the parse tree
	 */
	void exitAnystep(GherkinParser.AnystepContext ctx);
	/**
	 * Enter a parse tree produced by {@link GherkinParser#but}.
	 * @param ctx the parse tree
	 */
	void enterBut(GherkinParser.ButContext ctx);
	/**
	 * Exit a parse tree produced by {@link GherkinParser#but}.
	 * @param ctx the parse tree
	 */
	void exitBut(GherkinParser.ButContext ctx);
	/**
	 * Enter a parse tree produced by {@link GherkinParser#datatable}.
	 * @param ctx the parse tree
	 */
	void enterDatatable(GherkinParser.DatatableContext ctx);
	/**
	 * Exit a parse tree produced by {@link GherkinParser#datatable}.
	 * @param ctx the parse tree
	 */
	void exitDatatable(GherkinParser.DatatableContext ctx);
	/**
	 * Enter a parse tree produced by {@link GherkinParser#given}.
	 * @param ctx the parse tree
	 */
	void enterGiven(GherkinParser.GivenContext ctx);
	/**
	 * Exit a parse tree produced by {@link GherkinParser#given}.
	 * @param ctx the parse tree
	 */
	void exitGiven(GherkinParser.GivenContext ctx);
	/**
	 * Enter a parse tree produced by {@link GherkinParser#then}.
	 * @param ctx the parse tree
	 */
	void enterThen(GherkinParser.ThenContext ctx);
	/**
	 * Exit a parse tree produced by {@link GherkinParser#then}.
	 * @param ctx the parse tree
	 */
	void exitThen(GherkinParser.ThenContext ctx);
	/**
	 * Enter a parse tree produced by {@link GherkinParser#when}.
	 * @param ctx the parse tree
	 */
	void enterWhen(GherkinParser.WhenContext ctx);
	/**
	 * Exit a parse tree produced by {@link GherkinParser#when}.
	 * @param ctx the parse tree
	 */
	void exitWhen(GherkinParser.WhenContext ctx);
	/**
	 * Enter a parse tree produced by {@link GherkinParser#examples}.
	 * @param ctx the parse tree
	 */
	void enterExamples(GherkinParser.ExamplesContext ctx);
	/**
	 * Exit a parse tree produced by {@link GherkinParser#examples}.
	 * @param ctx the parse tree
	 */
	void exitExamples(GherkinParser.ExamplesContext ctx);
	/**
	 * Enter a parse tree produced by {@link GherkinParser#instructionDescription}.
	 * @param ctx the parse tree
	 */
	void enterInstructionDescription(GherkinParser.InstructionDescriptionContext ctx);
	/**
	 * Exit a parse tree produced by {@link GherkinParser#instructionDescription}.
	 * @param ctx the parse tree
	 */
	void exitInstructionDescription(GherkinParser.InstructionDescriptionContext ctx);
	/**
	 * Enter a parse tree produced by {@link GherkinParser#stepDescription}.
	 * @param ctx the parse tree
	 */
	void enterStepDescription(GherkinParser.StepDescriptionContext ctx);
	/**
	 * Exit a parse tree produced by {@link GherkinParser#stepDescription}.
	 * @param ctx the parse tree
	 */
	void exitStepDescription(GherkinParser.StepDescriptionContext ctx);
	/**
	 * Enter a parse tree produced by {@link GherkinParser#description}.
	 * @param ctx the parse tree
	 */
	void enterDescription(GherkinParser.DescriptionContext ctx);
	/**
	 * Exit a parse tree produced by {@link GherkinParser#description}.
	 * @param ctx the parse tree
	 */
	void exitDescription(GherkinParser.DescriptionContext ctx);
	/**
	 * Enter a parse tree produced by {@link GherkinParser#text}.
	 * @param ctx the parse tree
	 */
	void enterText(GherkinParser.TextContext ctx);
	/**
	 * Exit a parse tree produced by {@link GherkinParser#text}.
	 * @param ctx the parse tree
	 */
	void exitText(GherkinParser.TextContext ctx);
}