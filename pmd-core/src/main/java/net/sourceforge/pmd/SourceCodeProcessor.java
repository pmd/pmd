/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.benchmark.Benchmark;
import net.sourceforge.pmd.benchmark.Benchmarker;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.VisitorStarter;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.xpath.Initializer;

public class SourceCodeProcessor {

    private final PMDConfiguration configuration;

    public SourceCodeProcessor(PMDConfiguration configuration) {
        this.configuration = configuration;
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

        // make sure custom XPath functions are initialized
        Initializer.initialize();

        // Coarse check to see if any RuleSet applies to file, will need to do a finer RuleSet specific check later
        if (ruleSets.applies(ctx.getSourceCodeFile())) {
            // Is the cache up to date?
            if (configuration.getAnalysisCache().isUpToDate(ctx.getSourceCodeFile())) {
                for (final RuleViolation rv : configuration.getAnalysisCache().getCachedViolations(ctx.getSourceCodeFile())) {
                    ctx.getReport().addRuleViolation(rv);
                }
                return;
            }

            try {
                ruleSets.start(ctx);
                processSource(sourceCode, ruleSets, ctx);
            } catch (ParseException pe) {
                configuration.getAnalysisCache().analysisFailed(ctx.getSourceCodeFile());
                throw new PMDException("Error while parsing " + ctx.getSourceCodeFilename(), pe);
            } catch (Exception e) {
                configuration.getAnalysisCache().analysisFailed(ctx.getSourceCodeFile());
                throw new PMDException("Error while processing " + ctx.getSourceCodeFilename(), e);
            } finally {
                ruleSets.end(ctx);
            }
        }
    }

    private Node parse(RuleContext ctx, Reader sourceCode, Parser parser) {
        long start = System.nanoTime();
        Node rootNode = parser.parse(ctx.getSourceCodeFilename(), sourceCode);
        ctx.getReport().suppress(parser.getSuppressMap());
        long end = System.nanoTime();
        Benchmarker.mark(Benchmark.Parser, end - start, 0);
        return rootNode;
    }

    private void symbolFacade(Node rootNode, LanguageVersionHandler languageVersionHandler) {
        long start = System.nanoTime();
        languageVersionHandler.getSymbolFacade(configuration.getClassLoader()).start(rootNode);
        long end = System.nanoTime();
        Benchmarker.mark(Benchmark.SymbolTable, end - start, 0);
    }

    private void resolveQualifiedNames(Node rootNode, LanguageVersionHandler handler) {
        long start = System.nanoTime();
        handler.getQualifiedNameResolutionFacade(configuration.getClassLoader()).start(rootNode);
        long end = System.nanoTime();
        Benchmarker.mark(Benchmark.QualifiedNameResolution, end - start, 0);
    }

    // private ParserOptions getParserOptions(final LanguageVersionHandler
    // languageVersionHandler) {
    // // TODO Handle Rules having different parser options.
    // ParserOptions parserOptions =
    // languageVersionHandler.getDefaultParserOptions();
    // parserOptions.setSuppressMarker(configuration.getSuppressMarker());
    // return parserOptions;
    // }

    private void usesDFA(LanguageVersion languageVersion, Node rootNode, RuleSets ruleSets, Language language) {
        if (ruleSets.usesDFA(language)) {
            long start = System.nanoTime();
            VisitorStarter dataFlowFacade = languageVersion.getLanguageVersionHandler().getDataFlowFacade();
            dataFlowFacade.start(rootNode);
            long end = System.nanoTime();
            Benchmarker.mark(Benchmark.DFA, end - start, 0);
        }
    }

    private void usesTypeResolution(LanguageVersion languageVersion, Node rootNode, RuleSets ruleSets,
            Language language) {

        if (ruleSets.usesTypeResolution(language)) {
            long start = System.nanoTime();
            languageVersion.getLanguageVersionHandler().getTypeResolutionFacade(configuration.getClassLoader())
                    .start(rootNode);
            long end = System.nanoTime();
            Benchmarker.mark(Benchmark.TypeResolution, end - start, 0);
        }
    }


    private void usesMultifile(Node rootNode, LanguageVersionHandler languageVersionHandler, RuleSets ruleSets,
                               Language language) {

        if (ruleSets.usesMultifile(language)) {
            long start = System.nanoTime();
            languageVersionHandler.getMultifileFacade().start(rootNode);
            long end = System.nanoTime();
            Benchmarker.mark(Benchmark.Multifile, end - start, 0);
        }
    }


    private void processSource(Reader sourceCode, RuleSets ruleSets, RuleContext ctx) {
        LanguageVersion languageVersion = ctx.getLanguageVersion();
        LanguageVersionHandler languageVersionHandler = languageVersion.getLanguageVersionHandler();
        Parser parser = PMD.parserFor(languageVersion, configuration);

        Node rootNode = parse(ctx, sourceCode, parser);
        resolveQualifiedNames(rootNode, languageVersionHandler);
        symbolFacade(rootNode, languageVersionHandler);
        Language language = languageVersion.getLanguage();
        usesDFA(languageVersion, rootNode, ruleSets, language);
        usesTypeResolution(languageVersion, rootNode, ruleSets, language);
        usesMultifile(rootNode, languageVersionHandler, ruleSets, language);

        List<Node> acus = Collections.singletonList(rootNode);
        ruleSets.apply(acus, ctx, language);
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
