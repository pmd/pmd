/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.DummyNode.DummyRootNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.SemanticErrorReporter;
import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.document.TextRegion;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
import net.sourceforge.pmd.lang.rule.impl.DefaultRuleViolationFactory;

/**
 * Dummy language used for testing PMD.
 */
public class DummyLanguageModule extends BaseLanguageModule {

    public static final String NAME = "Dummy";
    public static final String TERSE_NAME = "dummy";

    public DummyLanguageModule() {
        super(NAME, null, TERSE_NAME, "dummy");
        addVersion("1.0", new Handler());
        addVersion("1.1", new Handler());
        addVersion("1.2", new Handler());
        addVersion("1.3", new Handler());
        addVersion("1.4", new Handler());
        addVersion("1.5", new Handler(), "5");
        addVersion("1.6", new Handler(), "6");
        addDefaultVersion("1.7", new Handler(), "7");
        addVersion("1.8", new Handler(), "8");
        addVersion("1.9-throws", new HandlerWithParserThatThrows());
        addVersion("1.9-semantic_error", new HandlerWithParserThatReportsSemanticError());
    }

    public static DummyRootNode parse(String code) {
        return parse(code, TextFile.UNKNOWN_FILENAME);
    }

    public static DummyRootNode parse(String code, String filename) {
        LanguageVersion version = DummyLanguageModule.getInstance().getDefaultVersion();
        ParserTask task = new ParserTask(
            TextDocument.readOnlyString(code, filename, version),
            SemanticErrorReporter.noop()
        );
        return (DummyRootNode) version.getLanguageVersionHandler().getParser().parse(task);
    }


    public static DummyLanguageModule getInstance() {
        return (DummyLanguageModule) LanguageRegistry.getLanguage(NAME);
    }

    public static class Handler extends AbstractPmdLanguageVersionHandler {

        @Override
        public RuleViolationFactory getRuleViolationFactory() {
            return new RuleViolationFactory();
        }

        @Override
        public Parser getParser() {
            return DummyLanguageModule::readLispNode;
        }
    }

    public static class HandlerWithParserThatThrows extends Handler {

        @Override
        public Parser getParser() {
            return task -> {
                throw new AssertionError("test error while parsing");
            };
        }
    }

    public static class HandlerWithParserThatReportsSemanticError extends Handler {

        @Override
        public Parser getParser() {
            return task -> {
                RootNode root = super.getParser().parse(task);
                task.getReporter().error(root, "An error occurred!");
                return root;
            };
        }
    }

    /**
     * Creates a tree of nodes that corresponds to the nesting structures
     * of parentheses in the text. The image of each node is also populated.
     * This is useful to create non-trivial trees with all the relevant
     * data (eg coordinates) set properly.
     *
     * Eg {@code (a(b)x(c))} will create a tree with a node "a", with two
     * children "b" and "c". "x" is ignored. The node "a" is not the root
     * node, it has a {@link DummyRootNode} as parent, whose image is "".
     */
    private static DummyRootNode readLispNode(ParserTask task) {
        TextDocument document = task.getTextDocument();
        final DummyRootNode root = new DummyRootNode().withTaskInfo(task);
        root.setRegion(document.getEntireRegion());

        DummyNode top = root;
        int lastNodeStart = 0;
        Chars text = document.getText();
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

                top.setRegion(TextRegion.fromBothOffsets(top.getTextRegion().getStartOffset(), i));

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

    public static class RuleViolationFactory extends DefaultRuleViolationFactory {

        @Override
        public RuleViolation createViolation(Rule rule, @NonNull Node node, FileLocation location, @NonNull String formattedMessage) {
            return new ParametricRuleViolation(rule, location, formattedMessage) {
                {
                    this.packageName = "foo"; // just for testing variable expansion
                }
            };
        }

    }
}
