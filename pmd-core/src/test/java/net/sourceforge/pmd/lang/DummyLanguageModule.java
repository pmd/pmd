/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.DummyNode.DummyRootNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.ast.SourceCodePositioner;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
import net.sourceforge.pmd.lang.rule.impl.DefaultRuleViolationFactory;
import net.sourceforge.pmd.processor.PmdRunnableTest;

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
        PmdRunnableTest.registerCustomVersions(this::addVersion);
    }

    public static DummyLanguageModule getInstance() {
        return (DummyLanguageModule) Objects.requireNonNull(LanguageRegistry.getLanguage(NAME));
    }

    public static DummyRootNode parse(String code) {
        return parse(code, "nofilename");
    }

    public static DummyRootNode parse(String code, String filename) {
        DummyRootNode rootNode = readLispNode(code);
        rootNode.withFileName(filename);
        return rootNode;
    }


    public static class Handler extends AbstractPmdLanguageVersionHandler {
        @Override
        public RuleViolationFactory getRuleViolationFactory() {
            return new RuleViolationFactory();
        }

        @Override
        public Parser getParser() {
            return task -> {
                DummyRootNode rootNode = readLispNode(task.getSourceText());
                rootNode.withFileName(task.getFileDisplayName());
                rootNode.withLanguage(task.getLanguageVersion());
                rootNode.withSourceText(task.getSourceText());
                return rootNode;
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
    private static DummyRootNode readLispNode(String text) {
        final DummyRootNode root = new DummyRootNode().withSourceText(text);
        DummyNode top = root;
        SourceCodePositioner positioner = new SourceCodePositioner(text);
        top.setCoords(1, 1, positioner.getLastLine(), positioner.getLastLineColumn());
        int lastNodeStart = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '(') {
                DummyNode node = new DummyNode();
                node.setParent(top);
                top.addChild(node, top.getNumChildren());
                // setup coordinates
                int bline = positioner.lineNumberFromOffset(i);
                int bcol = positioner.columnFromOffset(bline, i);
                node.setCoords(bline, bcol, bline, bcol);
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
                // setup coordinates
                int eline = positioner.lineNumberFromOffset(i);
                int ecol = positioner.columnFromOffset(eline, i);
                top.setCoords(top.getBeginLine(), top.getBeginColumn(), eline, ecol);

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
        public RuleViolation createViolation(Rule rule, @NonNull Node location, @NonNull String formattedMessage) {
            return new ParametricRuleViolation<Node>(rule, location, formattedMessage) {
                {
                    this.packageName = "foo"; // just for testing variable expansion
                }
            };
        }
    }
}
