/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.benchmark.Benchmark;
import net.sourceforge.pmd.benchmark.Benchmarker;
import net.sourceforge.pmd.lang.*;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.xpath.Initializer;

import org.apache.commons.io.IOUtils;

public class SourceCodeProcessor {

    private final PMDConfiguration configuration;

    public SourceCodeProcessor(PMDConfiguration configuration) {
    	this.configuration = configuration;
    }
    
    
    /**
     * Processes the input stream against a rule set using the given input encoding.
     *
     * @param sourceCode The InputStream to analyze.
     * @param ruleSets The collection of rules to process against the file.
     * @param ctx The context in which PMD is operating.
     * @throws PMDException if the input encoding is unsupported, the input stream could
     *                      not be parsed, or other error is encountered.
     * @see #processSourceCode(Reader, RuleSets, RuleContext)
     */
    public void processSourceCode(InputStream sourceCode, RuleSets ruleSets, RuleContext ctx) throws PMDException {
		try {
		    processSourceCode(new InputStreamReader(sourceCode, configuration.getSourceEncoding()), ruleSets, ctx);
		} catch (UnsupportedEncodingException uee) {
		    throw new PMDException("Unsupported encoding exception: " + uee.getMessage());
		}
    }
    
    
    /**
     * Processes the input stream against a rule set using the given input encoding.
     * If the LanguageVersion is <code>null</code>  on the RuleContext, it will
     * be automatically determined.  Any code which wishes to process files for
     * different Languages, will need to be sure to either properly set the
     * Language on the RuleContext, or set it to <code>null</code> first.
     *
     * @see RuleContext#setLanguageVersion(net.sourceforge.pmd.lang.LanguageVersion)
     * @see PMDConfiguration#getLanguageVersionOfFile(String)
     *
     * @param sourceCode The Reader to analyze.
     * @param ruleSets The collection of rules to process against the file.
     * @param ctx The context in which PMD is operating.
     * @throws PMDException if the input encoding is unsupported, the input stream could
     *                      not be parsed, or other error is encountered.
     */
    public void processSourceCode(Reader sourceCode, RuleSets ruleSets, RuleContext ctx) throws PMDException {
    	determineLanguage(ctx);

		// make sure custom XPath functions are initialized
		Initializer.initialize();

	    // Coarse check to see if any RuleSet applies to file, will need to do a finer RuleSet specific check later
		 if (ruleSets.applies(ctx.getSourceCodeFile())) {

		try {
			processSource(sourceCode, ruleSets,ctx);

		} catch (ParseException pe) {
		    throw new PMDException("Error while parsing " + ctx.getSourceCodeFilename(), pe);
		} catch (Exception e) {
		    throw new PMDException("Error while processing " + ctx.getSourceCodeFilename(), e);
		} finally {
		    IOUtils.closeQuietly(sourceCode);
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
    
//    private ParserOptions getParserOptions(final LanguageVersionHandler languageVersionHandler) {
//		// TODO Handle Rules having different parser options.
//		ParserOptions parserOptions = languageVersionHandler.getDefaultParserOptions();
//		parserOptions.setSuppressMarker(configuration.getSuppressMarker());
//		return parserOptions;
//    }

    private void usesDFA(LanguageVersion languageVersion, Node rootNode, RuleSets ruleSets, Language language ) {

		if (ruleSets.usesDFA(language)) {
		    long start = System.nanoTime();
		    VisitorStarter dataFlowFacade = languageVersion.getLanguageVersionHandler().getDataFlowFacade();
		    dataFlowFacade.start(rootNode);
		    long end = System.nanoTime();
		    Benchmarker.mark(Benchmark.DFA, end - start, 0);
		}
    }

    private void usesTypeResolution(LanguageVersion languageVersion, Node rootNode, RuleSets ruleSets, Language language) {
	
		if (ruleSets.usesTypeResolution(language)) {
		    long start = System.nanoTime();
		    languageVersion.getLanguageVersionHandler().getTypeResolutionFacade(configuration.getClassLoader()).start(rootNode);
		    long end = System.nanoTime();
		    Benchmarker.mark(Benchmark.TypeResolution, end - start, 0);
		}
    }
    
    private void processSource(Reader sourceCode, RuleSets ruleSets, RuleContext ctx) {
		LanguageVersion languageVersion = ctx.getLanguageVersion();
		LanguageVersionHandler languageVersionHandler = languageVersion.getLanguageVersionHandler();
		Parser parser = PMD.parserFor(languageVersion, configuration);
		
		Node rootNode = parse(ctx, sourceCode, parser);
		symbolFacade(rootNode, languageVersionHandler);
		Language language = languageVersion.getLanguage();
		usesDFA(languageVersion, rootNode, ruleSets, language);
		usesTypeResolution(languageVersion, rootNode, ruleSets,language);
		
		List<Node> acus = new ArrayList<>();
		acus.add(rootNode);
		ruleSets.apply(acus, ctx, language);
	}



	private void determineLanguage(RuleContext ctx) {
		// If LanguageVersion of the source file is not known, make a determination
		if (ctx.getLanguageVersion() == null) {
		    LanguageVersion languageVersion = configuration.getLanguageVersionOfFile(ctx.getSourceCodeFilename());
		    ctx.setLanguageVersion(languageVersion);
		}
    }
}
