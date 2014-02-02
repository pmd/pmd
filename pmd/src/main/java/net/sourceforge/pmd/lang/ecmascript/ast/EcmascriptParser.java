/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ecmascript.EcmascriptParserOptions;

import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.Comment;
import org.mozilla.javascript.ast.ErrorCollector;
import org.mozilla.javascript.ast.ParseProblem;

public class EcmascriptParser {
    protected final EcmascriptParserOptions parserOptions;

    private Map<Integer, String> suppressMap;
    private String suppressMarker = "NOPMD"; // that's the default value

    public EcmascriptParser(EcmascriptParserOptions parserOptions) {
        this.parserOptions = parserOptions;
        if (parserOptions.getSuppressMarker() != null) {
            suppressMarker = parserOptions.getSuppressMarker();
        }
    }

    protected AstRoot parseEcmascript(final String sourceCode, final List<ParseProblem> parseProblems) throws ParseException {
	final CompilerEnvirons compilerEnvirons = new CompilerEnvirons();
	compilerEnvirons.setRecordingComments(parserOptions.isRecordingComments());
	compilerEnvirons.setRecordingLocalJsDocComments(parserOptions.isRecordingLocalJsDocComments());
	compilerEnvirons.setLanguageVersion(parserOptions.getRhinoLanguageVersion().getVersion());
	compilerEnvirons.setIdeMode(true); // Scope's don't appear to get set right without this
	compilerEnvirons.setWarnTrailingComma(true);
        compilerEnvirons.setReservedKeywordAsIdentifier(true); // see bug #1150 "EmptyExpression" for valid statements!

	// TODO We should do something with Rhino errors...
	final ErrorCollector errorCollector = new ErrorCollector();
	final Parser parser = new Parser(compilerEnvirons, errorCollector);
	// TODO Fix hardcode
	final String sourceURI = "unknown";
	final int beginLineno = 1;
	AstRoot astRoot = parser.parse(sourceCode, sourceURI, beginLineno);
	parseProblems.addAll(errorCollector.getErrors());
	return astRoot;
    }

    public EcmascriptNode<AstRoot> parse(final Reader reader) {
	try {
	    final List<ParseProblem> parseProblems = new ArrayList<ParseProblem>();
	    final String sourceCode = IOUtils.toString(reader);
	    final AstRoot astRoot = parseEcmascript(sourceCode, parseProblems);
	    final EcmascriptTreeBuilder treeBuilder = new EcmascriptTreeBuilder(sourceCode, parseProblems);
	    EcmascriptNode<AstRoot> tree = treeBuilder.build(astRoot);

            suppressMap = new HashMap<Integer, String>();
            if (astRoot.getComments() != null) {
                for (Comment comment : astRoot.getComments()) {
                    int nopmd = comment.getValue().indexOf(suppressMarker);
                    if (nopmd > -1) {
                        String suppression = comment.getValue().substring(nopmd + suppressMarker.length());
                        EcmascriptNode<Comment> node = treeBuilder.build(comment);
                        suppressMap.put(node.getBeginLine(), suppression);
                    }
                }
            }
        return tree;
	} catch (IOException e) {
	    throw new ParseException(e);
	}
    }

    public Map<Integer, String> getSuppressMap() {
        return suppressMap;
    }
}
