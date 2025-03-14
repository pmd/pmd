/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.Comment;
import org.mozilla.javascript.ast.ErrorCollector;
import org.mozilla.javascript.ast.ParseProblem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.FileAnalysisException;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextPos2d;

public final class EcmascriptParser implements net.sourceforge.pmd.lang.ast.Parser {
    private static final Logger LOGGER = LoggerFactory.getLogger(EcmascriptParser.class);
    private final LanguagePropertyBundle properties;

    public EcmascriptParser(LanguagePropertyBundle properties) {
        this.properties = properties;
    }

    private AstRoot parseEcmascript(final FileId fileId, final String sourceCode, final LanguageVersion version, final List<ParseProblem> parseProblems) throws ParseException {
        final CompilerEnvirons compilerEnvirons = new CompilerEnvirons();
        compilerEnvirons.setRecordingComments(true);
        compilerEnvirons.setRecordingLocalJsDocComments(true);
        compilerEnvirons.setLanguageVersion(determineRhinoLanguageVersion(version));
        // Scope's don't appear to get set right without this
        compilerEnvirons.setIdeMode(true);
        compilerEnvirons.setWarnTrailingComma(true);
        // see bug #1150 "EmptyExpression" for valid statements!
        compilerEnvirons.setReservedKeywordAsIdentifier(true);

        final ErrorCollector errorCollector = new ErrorCollector();
        final Parser parser = new Parser(compilerEnvirons, errorCollector);
        final String sourceURI = fileId.getOriginalPath();
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
        final AstRoot astRoot = parseEcmascript(task.getFileId(), task.getSourceText(), version, parseProblems);

        List<ParseProblem> errors = parseProblems.stream().filter(p -> p.getType() == ParseProblem.Type.Error).collect(Collectors.toList());
        if (!errors.isEmpty()) {
            String errorMessage = errors.stream().map(p -> {
                TextPos2d textPos2d = task.getTextDocument().lineColumnAtOffset(p.getFileOffset());
                FileLocation caret = FileLocation.caret(task.getFileId(), textPos2d.getLine(), textPos2d.getColumn());
                return caret.startPosToStringWithFile() + ": " + p.getMessage();
            }).collect(Collectors.joining(System.lineSeparator()));

            // TODO throw new ParseException(errors.size() + " problems found:" + System.lineSeparator() + errorMessage);
            // can't throw ParseException as that would fail many analysis. The parser replaced the errors with
            // EmptyStatement.
            LOGGER.warn("{} javascript problems found:{}{}", errors.size(), System.lineSeparator(), errorMessage);
        }

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
