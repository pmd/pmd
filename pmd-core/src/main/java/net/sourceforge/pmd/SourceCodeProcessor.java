/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;

import org.apache.commons.io.IOUtils;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.internal.RulesetStageDependencyHelper;
import net.sourceforge.pmd.internal.SystemProps;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.SemanticErrorReporter;

/**
 * Source code processor is internal.
 */
@Deprecated
@InternalApi
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

    private RootNode parse(Parser parser, ParserTask task) {
        try (TimedOperation ignored = TimeTracker.startOperation(TimedOperationCategory.PARSER)) {
            return parser.parse(task);
        }
    }



    private void processSource(Reader reader, RuleSets ruleSets, RuleContext ctx) throws IOException {
        LanguageVersion languageVersion = ctx.getLanguageVersion();
        String filename = ctx.getSourceCodeFilename();
        String sourceCode = IOUtils.toString(reader);

        ParserTask task = new ParserTask(
            languageVersion,
            filename,
            sourceCode,
            SemanticErrorReporter.noop() // TODO
        );

        // todo following 2 lines should be deleted
        languageVersion.getLanguageVersionHandler().declareParserTaskProperties(task.getProperties());
        task.getProperties().setProperty(ParserTask.COMMENT_MARKER, configuration.getSuppressMarker());

        Parser parser = languageVersion.getLanguageVersionHandler().getParser();

        RootNode rootNode = parse(parser, task);

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
