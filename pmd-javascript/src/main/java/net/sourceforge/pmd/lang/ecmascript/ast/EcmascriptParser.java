/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.Comment;
import org.mozilla.javascript.ast.ErrorCollector;
import org.mozilla.javascript.ast.ParseProblem;

import net.sourceforge.pmd.lang.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ecmascript.EcmascriptParserOptions;
import net.sourceforge.pmd.util.document.TextDocument;

public class EcmascriptParser {
    protected final EcmascriptParserOptions parserOptions;

    public EcmascriptParser(EcmascriptParserOptions parserOptions) {
        this.parserOptions = parserOptions;
    }

    protected AstRoot parseEcmascript(final String sourceCode, final List<ParseProblem> parseProblems)
            throws ParseException {
        final CompilerEnvirons compilerEnvirons = new CompilerEnvirons();
        compilerEnvirons.setRecordingComments(parserOptions.isRecordingComments());
        compilerEnvirons.setRecordingLocalJsDocComments(parserOptions.isRecordingLocalJsDocComments());
        compilerEnvirons.setLanguageVersion(parserOptions.getRhinoLanguageVersion().getVersion());
        // Scope's don't appear to get set right without this
        compilerEnvirons.setIdeMode(true);
        compilerEnvirons.setWarnTrailingComma(true);
        // see bug #1150 "EmptyExpression" for valid statements!
        compilerEnvirons.setReservedKeywordAsIdentifier(true);

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

    public ASTAstRoot parse(final ParserTask task) {
        final List<ParseProblem> parseProblems = new ArrayList<>();
        final TextDocument document = task.getTextDocument();
        final AstRoot astRoot = parseEcmascript(document.getText().toString(), parseProblems);
        final EcmascriptTreeBuilder treeBuilder = new EcmascriptTreeBuilder(document, parseProblems);
        ASTAstRoot tree = (ASTAstRoot) treeBuilder.build(astRoot);
        tree.addTaskInfo(task);

        String suppressMarker = task.getCommentMarker();
        Map<Integer, String> suppressMap = new HashMap<>();
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
        tree.setNoPmdComments(suppressMap);
        return tree;
    }

}
