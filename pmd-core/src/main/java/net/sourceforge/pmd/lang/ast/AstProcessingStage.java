/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.List;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.LanguageVersionHandler;

// @formatter:off
/**
 * Represents one of the stages applying on the AST
 * after parsing is done. Each of these stages implicitly
 * depends on the parser stage.
 *
 * <p>An analysis on a file goes through the following stages:
 * <ul>
 *     <li> Parsing stage: taking the source and configuration, and returning an AST
 *     <li> Language-specific AST visits: sequence of stages specific to each language.
 *          Each stage performs side effects on the AST, e.g. to resolve such things as comments,
 *          types, DFA graph, etc.
 *     <li> Rulechain application: all rulechain rules are run on the final AST
 *     <li> Rule application: other rules are run
 * </ul>
 *
 * <p>These steps are run on each file during the analysis, unless the cache entry of the file is up-to-date.
 * They're all run sequentially by the same thread. Rule application performs side-effects on the Report,
 * which is rendered after all files have been processed.
 *
 * <p>Parsing and rule[chain] application stages are considered special and are handled differently for now.
 * A {@link LanguageVersionHandler} is responsible for listing all available {@link AstProcessingStage}s
 * (see {@link LanguageVersionHandler#getProcessingStages()}). The actual set of stages that will get executed
 * for a run is the union of the dependencies of the rules in the run {@link RuleSets}.
 *
 * <p>Additional doc, to be moved elsewhere probably:
 * <p>PMD's execution goes through other more global stages (not sure about the exact order):
 * <ul>
 *   <li> Rule loading: creates a {@link RuleSets} from the ruleset files
 *   <li> Cache loading: loads the cache file for incremental analysis if any, creates the new cache
 *   <li> Report creation: creates a report object for the rules to act on
 *   <li> File collection: collects the files to analyse and dispatches them to worker threads.
 *        Each file undergoes the steps described above.
 *   <li> Report rendering
 *   <li> Cache persisting
 * </ul>
 *
 *
 * @author Clément Fournier
 * @since 6.10.0
 */
// @formatter:on
@Experimental
public interface AstProcessingStage<T extends AstProcessingStage<T>> extends Comparable<T> {


    /**
     * Gets the stages on which this stage depends.
     * E.g. the type resolution stage may depend on the
     * qualified name resolution stage.
     *
     * <p>Returns an empty list if this stage only depends
     * on the parser stage.
     */
    List<T> getDependencies();


    /**
     * Returns the name of this stage, used e.g. to display in a
     * benchmark report.
     *
     * @return The name of the stage.
     */
    String getDisplayName();


    /**
     * Performs some side effects on the AST, e.g. to resolve something.
     *
     * @param rootNode      Root of the tree
     * @param configuration Configuration
     */
    void processAST(RootNode rootNode, AstAnalysisConfiguration configuration);


    /**
     * Returns true if the given rule depends on this stage,
     * in which case, any ruleset containing that rule depends
     * on this stage too.
     *
     * @param rule Rule to test
     *
     * @return true if the given rule depends on this stage.
     */
    // This can be customized for each step.
    // For now we use the Rule.isDfa() / Rule.isTypeRes()
    // In the future we can check an annotation.
    // If the rule is an XPath rule, then additional work
    // performed by the XPath analyser will be taken into account.

    // This separation based on the type of rule means that the
    // method for dependency determination should probably be on the
    // Rule interface, but I don't know for now.
    @Experimental
    boolean ruleDependsOnThisStage(Rule rule);

}
