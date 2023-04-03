/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.Comment;
import org.mozilla.javascript.ast.ErrorCollector;
import org.mozilla.javascript.ast.ParseProblem;

import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.FileAnalysisException;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.RootNode;

public final class EcmascriptParser implements net.sourceforge.pmd.lang.ast.Parser {
    private final LanguagePropertyBundle properties;

    public EcmascriptParser(LanguagePropertyBundle properties) {
        this.properties = properties;
    }

    private AstRoot parseEcmascript(final String sourceCode, final LanguageVersion version, final List<ParseProblem> parseProblems) throws ParseException {
        final CompilerEnvirons compilerEnvirons = new CompilerEnvirons();
        compilerEnvirons.setRecordingComments(true);
        compilerEnvirons.setRecordingLocalJsDocComments(true);
        compilerEnvirons.setLanguageVersion(determineRhinoLanguageVersion(version));
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

    private static int determineRhinoLanguageVersion(LanguageVersion version) {
        switch (version.getVersion()) {
        case "3": return Context.VERSION_1_5;
        case "5": return Context.VERSION_1_8;
        default: return Context.VERSION_ES6;
        }
    }

    @Override
    public RootNode parse(ParserTask task) throws FileAnalysisException {
        final LanguageVersion version = task.getLanguageVersion();
        final List<ParseProblem> parseProblems = new ArrayList<>();
        final AstRoot astRoot = parseEcmascript(task.getSourceText(), version, parseProblems);
        final EcmascriptTreeBuilder treeBuilder = new EcmascriptTreeBuilder(parseProblems);
        ASTAstRoot tree = (ASTAstRoot) treeBuilder.build(astRoot);

        String suppressMarker = properties.getSuppressMarker();
        Map<Integer, String> suppressMap = new HashMap<>();
        if (astRoot.getComments() != null) {
            for (Comment comment : astRoot.getComments()) {
                int nopmd = comment.getValue().indexOf(suppressMarker);
                if (nopmd > -1) {
                    String suppression = comment.getValue().substring(nopmd + suppressMarker.length());
                    suppressMap.put(comment.getLineno(), suppression);
                }
            }
        }
        tree.setAstInfo(new AstInfo<>(task, tree).withSuppressMap(suppressMap));
        return tree;
    }

}
