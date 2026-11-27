/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.dummy;

import java.util.Objects;

import net.sourceforge.pmd.cpd.AnyCpdLexer;
import net.sourceforge.pmd.cpd.CpdCapableLanguage;
import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.document.TextPos2d;
import net.sourceforge.pmd.lang.document.TextRegion;
import net.sourceforge.pmd.lang.dummy.DummyNode.DummyRootNode;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;
import net.sourceforge.pmd.reporting.RuleViolation;
import net.sourceforge.pmd.reporting.ViolationDecorator;

/**
 * Dummy language used for testing PMD's tooling (e.g. pmd-ant, pmd-cli, pmd-test).
 *
 * <p>The parser creates a tree of nodes that corresponds to the nesting structure
 * of parentheses in the text, see {@link #readLispNode(ParserTask)}.
 *
 * <p>Special behavior for testing error handling:
 * <ul>
 *     <li>The parser of the language version {@code parserThrows} always throws a {@link ParseException}.</li>
 *     <li>The CPD lexer throws a lex exception, if the text contains {@link #CPD_THROW_LEX_EXCEPTION}.</li>
 * </ul>
 */
public class DummyLanguageModule extends SimpleLanguageModuleBase implements CpdCapableLanguage {

    public static final String NAME = "Dummy";
    public static final String TERSE_NAME = "dummy";
    private static final String PARSER_THROWS = "parserThrows";

    public static final String CPD_THROW_LEX_EXCEPTION = ":throw_lex_source_exception:";

    public DummyLanguageModule() {
        super(LanguageMetadata.withId(TERSE_NAME).name(NAME).extensions("dummy")
                              .addVersion("1.0")
                              .addVersion("1.2")
                              .addDefaultVersion("1.7")
                              .addVersion(PARSER_THROWS), new Handler());
    }

    public static DummyLanguageModule getInstance() {
        return (DummyLanguageModule) Objects.requireNonNull(LanguageRegistry.PMD.getLanguageByFullName(NAME));
    }

    @Override
    public CpdLexer createCpdLexer(LanguagePropertyBundle bundle) {
        CpdLexer base = new AnyCpdLexer();
        return (doc, tokens) -> {
            Chars text = doc.getText();
            int offset = text.indexOf(CPD_THROW_LEX_EXCEPTION, 0);
            if (offset != -1) {
                TextPos2d lc = doc.lineColumnAtOffset(offset);
                throw tokens.makeLexException(lc.getLine(), lc.getColumn(), "test exception", null);
            }
            base.tokenize(doc, tokens);
        };
    }

    private static final class Handler extends AbstractPmdLanguageVersionHandler {

        @Override
        public Parser getParser() {
            return task -> {
                if (PARSER_THROWS.equals(task.getLanguageVersion().getVersion())) {
                    throw new ParseException("ohio");
                }
                return readLispNode(task);
            };
        }

        @Override
        public ViolationDecorator getViolationDecorator() {
            return (node, data) -> data.put(RuleViolation.PACKAGE_NAME, "foo");
        }
    }

    /**
     * Creates a tree of nodes that corresponds to the nesting structures
     * of parentheses in the text. The image of each node is also populated.
     * This is useful to create non-trivial trees with all the relevant
     * data (eg coordinates) set properly.
     *
     * <p>Eg {@code (a(b)x(c))} will create a tree with a node "a", with two
     * children "b" and "c". "x" is ignored. The node "a" is not the root
     * node, it has a {@link DummyRootNode} as parent, whose image is "".
     */
    private static DummyRootNode readLispNode(ParserTask task) {
        final DummyRootNode root = new DummyRootNode(task);
        root.setRegion(task.getTextDocument().getEntireRegion());

        DummyNode top = root;
        int lastNodeStart = 0;
        Chars text = task.getTextDocument().getText();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '(') {
                DummyNode node = new DummyNode();
                node.setParent(top);
                top.addChild(node, top.getNumChildren());
                // setup coordinates, temporary (will be completed when node closes)
                node.setRegion(TextRegion.caretAt(i));

                // cut out image
                if (top.getImage() == null) {
                    // image may be non null if this is not the first child of 'top'
                    // eg in (a(b)x(c)), the image of the parent is set to "a".
                    // When we're processing "(c", we ignore "x".
                    String image = text.substring(lastNodeStart, i);
                    top.setImage(image);
                }
                lastNodeStart = i + 1;
                // node is the top of the stack now
                top = node;
            } else if (c == ')') {
                if (top == null) {
                    throw new ParseException("Unbalanced parentheses: " + text);
                }

                top.setRegion(TextRegion.fromBothOffsets(top.getTextRegion().getStartOffset(), i + 1));

                if (top.getImage() == null) {
                    // cut out image (if node doesn't have children it hasn't been populated yet)
                    String image = text.substring(lastNodeStart, i);
                    top.setImage(image);
                    lastNodeStart = i + 1;
                }
                top = top.getParent();
            }
        }
        if (top != root) {
            throw new ParseException("Unbalanced parentheses: " + text);
        }
        return root;
    }
}
