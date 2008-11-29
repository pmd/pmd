/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.ParseException;

import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ErrorCollector;

public class EcmascriptParser {

    protected Map<AstNode, EcmascriptNode> nodeCache = new HashMap<AstNode, EcmascriptNode>();

    protected AstRoot parseEcmascript(final Reader reader) throws ParseException {
	// TODO Fix hardcode
	final CompilerEnvirons compilerEnvirons = new CompilerEnvirons();
	//compilerEnvirons.setLanguageVersion(Context.VERSION_1_0);
	// TODO Fix hardcode
	final ErrorReporter errorReporter = new ErrorCollector();
	final Parser parser = new Parser(compilerEnvirons, errorReporter);
	nodeCache.clear();
	try {
	    // TODO Fix hardcode
	    final String sourceURI = "unknown";
	    // TODO Fix hardcode
	    final int lineno = 0;
	    return parser.parse(reader, sourceURI, lineno);
	} catch (final IOException e) {
	    throw new ParseException(e);
	}
    }

    public EcmascriptNode parse(final Reader reader) {
	final AstRoot astRoot = parseEcmascript(reader);
	final EcmascriptTreeBuilder treeBuilder = new EcmascriptTreeBuilder();
	return treeBuilder.build(astRoot);
    }
}
