/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;

import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.internal.RulesetStageDependencyHelper;
import net.sourceforge.pmd.internal.SystemProps;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.RootNode;

public class SourceCodeProcessor {

    private final PMDConfiguration configuration;
    private final RulesetStageDependencyHelper dependencyHelper;


    public SourceCodeProcessor(PMDConfiguration configuration) {
        this.configuration = configuration;
        dependencyHelper = new RulesetStageDependencyHelper(configuration);
    }

    /**
     * Processes the input stream against a rule set using the given input
     * encoding.
     *
     * @param sourceCode
     *            The InputStream to analyze.
     * @param ruleSets
     *            The collection of rules to process against the file.
     * @param ctx
     *            The context in which PMD is operating.
     * @throws PMDException
     *             if the input encoding is unsupported, the input stream could
     *             not be parsed, or other error is encountered.
     * @see #processSourceCode(Reader, RuleSets, RuleContext)
     */
    public void processSourceCode(InputStream sourceCode, RuleSets ruleSets, RuleContext ctx) throws PMDException {
        try (Reader streamReader = new InputStreamReader(sourceCode, configuration.getSourceEncoding())) {
            processSourceCode(streamReader, ruleSets, ctx);
        } catch (IOException e) {
            throw new PMDException("IO exception: " + e.getMessage(), e);
        }
    }

    /**
     * Processes the input stream against a rule set using the given input
     * encoding. If the LanguageVersion is <code>null</code> on the RuleContext,
     * it will be automatically determined. Any code which wishes to process
     * files for different Languages, will need to be sure to either properly
     * set the Language on the RuleContext, or set it to <code>null</code>
     * first.
     *
     * @see RuleContext#setLanguageVersion(net.sourceforge.pmd.lang.LanguageVersion)
     * @see PMDConfiguration#getLanguageVersionOfFile(String)
     *
     * @param sourceCode
     *            The Reader to analyze.
     * @param ruleSets
     *            The collection of rules to process against the file.
     * @param ctx
     *            The context in which PMD is operating.
     * @throws PMDException
     *             if the input encoding is unsupported, the input stream could
     *             not be parsed, or other error is encountered.
     */
    public void processSourceCode(Reader sourceCode, RuleSets ruleSets, RuleContext ctx) throws PMDException {
        determineLanguage(ctx);

        // Coarse check to see if any RuleSet applies to file, will need to do a finer RuleSet specific check later
        if (ruleSets.applies(ctx.getSourceCodeFile())) {
            if (isCacheUpToDate(ctx)) {
                reportCachedRuleViolations(ctx);
            } else {
                processSourceCodeWithoutCache(sourceCode, ruleSets, ctx);
            }
        }
    }

    private boolean isCacheUpToDate(final RuleContext ctx) {
        return configuration.getAnalysisCache().isUpToDate(ctx.getSourceCodeFile());
    }

    private void reportCachedRuleViolations(final RuleContext ctx) {
        for (final RuleViolation rv : configuration.getAnalysisCache().getCachedViolations(ctx.getSourceCodeFile())) {
            ctx.getReport().addRuleViolation(rv);
        }
    }

    private void processSourceCodeWithoutCache(final Reader sourceCode, final RuleSets ruleSets, final RuleContext ctx) throws PMDException {
        try {
            ruleSets.start(ctx);
            processSource(sourceCode, ruleSets, ctx);
        } catch (ParseException pe) {
            configuration.getAnalysisCache().analysisFailed(ctx.getSourceCodeFile());
            throw new PMDException("Error while parsing " + ctx.getSourceCodeFile(), pe);
        } catch (Exception e) {
            configuration.getAnalysisCache().analysisFailed(ctx.getSourceCodeFile());
            throw new PMDException("Error while processing " + ctx.getSourceCodeFile(), e);
        } catch (StackOverflowError | AssertionError e) {
            if (SystemProps.isErrorRecoveryMode()) {
                configuration.getAnalysisCache().analysisFailed(ctx.getSourceCodeFile());
                throw new PMDException("Error while processing " + ctx.getSourceCodeFile(), e);
            }
            throw e;
        } finally {
            ruleSets.end(ctx);
        }
    }

    private Node parse(RuleContext ctx, Reader sourceCode, Parser parser) {
        try (TimedOperation to = TimeTracker.startOperation(TimedOperationCategory.PARSER)) {
            return parser.parse(String.valueOf(ctx.getSourceCodeFile()), sourceCode);
        }
    }



    private void processSource(Reader sourceCode, RuleSets ruleSets, RuleContext ctx) {

        // basically:
        // 1. make the union of all stage dependencies of each rule, by language, for the Rulesets
        // 2. order them by dependency
        // 3. run them and time them if needed

        // The problem is the first two steps need only be done once.
        // They're probably costly and if we do this here without changing anything,
        // they'll be done on each file! Btw currently the "usesDfa" and such are nested loops testing
        // all rules of all rulesets, but they're run on each file too!

        // FIXME - this implementation is a hack to wire-in stages without
        //  needing to change RuleSet immediately

        // With mutable RuleSets, caching of the value can't be guaranteed to be accurate...
        // The approach I'd like to take is either
        // * to create a new RunnableRulesets class which is immutable, and performs all these preliminary
        //   computations upon construction.
        // * or to modify Ruleset and Rulesets to be immutable. This IMO is a better option because it makes
        //   these objects easier to reason about and pass around from thread to thread. It also avoid creating
        //   a new class, and breaking SourceCodeProcessor's API too much.
        //
        // The "preliminary computations" also include:
        // * removing dysfunctional rules
        // * separating rulechain rules from normal rules
        // * grouping rules by language/ file extension
        // * etc.

        LanguageVersion languageVersion = ctx.getLanguageVersion();

        Parser parser = PMD.parserFor(languageVersion, configuration);

        RootNode rootNode = (RootNode) parse(ctx, sourceCode, parser);

        dependencyHelper.runLanguageSpecificStages(ruleSets, languageVersion, rootNode);

        ruleSets.apply(Collections.singletonList(rootNode), ctx);
    }




    private void determineLanguage(RuleContext ctx) {
        // If LanguageVersion of the source file is not known, make a
        // determination
        if (ctx.getLanguageVersion() == null) {
            LanguageVersion languageVersion = configuration.getLanguageVersionOfFile(ctx.getSourceCodeFilename());
            ctx.setLanguageVersion(languageVersion);
        }
    }
}
