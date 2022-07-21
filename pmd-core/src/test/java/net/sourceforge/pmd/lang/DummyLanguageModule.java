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
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextRegion;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
import net.sourceforge.pmd.lang.rule.impl.DefaultRuleViolationFactory;

/**
 * Dummy language used for testing PMD.
 */
public class DummyLanguageModule extends SimpleLanguageModuleBase {

    public static final String NAME = "Dummy";
    public static final String TERSE_NAME = "dummy";

    public DummyLanguageModule() {
        super(LanguageMetadata.withId(TERSE_NAME).name(NAME).extensions("dummy")
                              .addVersion("1.0")
                              .addVersion("1.1")
                              .addVersion("1.2")
                              .addVersion("1.3")
                              .addVersion("1.4")
                              .addVersion("1.5", "5")
                              .addVersion("1.6", "6")
                              .addDefaultVersion("1.7", "7")
                              .addVersion("1.8", "8"), new Handler());
    }

    public static DummyLanguageModule getInstance() {
        return (DummyLanguageModule) Objects.requireNonNull(LanguageRegistry.PMD.getLanguageByFullName(NAME));
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
    public static DummyRootNode readLispNode(ParserTask task) {
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
