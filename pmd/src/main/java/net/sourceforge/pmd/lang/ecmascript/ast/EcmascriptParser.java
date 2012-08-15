/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ecmascript.EcmascriptParserOptions;

import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ErrorCollector;
import org.mozilla.javascript.ast.ParseProblem;

public class EcmascriptParser {
    protected final EcmascriptParserOptions parserOptions;

    public EcmascriptParser(EcmascriptParserOptions parserOptions) {
	this.parserOptions = parserOptions;
    }

    protected AstRoot parseEcmascript(final Reader reader, final List<ParseProblem> parseProblems) throws ParseException {
	final CompilerEnvirons compilerEnvirons = new CompilerEnvirons();
	compilerEnvirons.setRecordingComments(parserOptions.isRecordingComments());
	compilerEnvirons.setRecordingLocalJsDocComments(parserOptions.isRecordingLocalJsDocComments());
	compilerEnvirons.setLanguageVersion(parserOptions.getRhinoLanguageVersion().getVersion());
	compilerEnvirons.setIdeMode(true); // Scope's don't appear to get set right without this
	compilerEnvirons.setWarnTrailingComma(true);

	// TODO We should do something with Rhino errors...
	final ErrorCollector errorCollector = new ErrorCollector();
	final Parser parser = new Parser(compilerEnvirons, errorCollector);
	try {
	    // TODO Fix hardcode
	    final String sourceURI = "unknown";
	    // TODO Fix hardcode
	    final int lineno = 0;
	    AstRoot astRoot = parser.parse(reader, sourceURI, lineno);
	    parseProblems.addAll(errorCollector.getErrors());
	    return astRoot;
	} catch (final IOException e) {
	    throw new ParseException(e);
	}
    }

    public EcmascriptNode parse(final Reader reader) {
	final List<ParseProblem> parseProblems = new ArrayList<ParseProblem>();
	final AstRoot astRoot = parseEcmascript(reader, parseProblems);
	final EcmascriptTreeBuilder treeBuilder = new EcmascriptTreeBuilder(parseProblems);
	return treeBuilder.build(astRoot);
    }
}
