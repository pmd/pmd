/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jaxen.Navigator;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.DummyNode.DummyRootNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.SourceCodePositioner;
import net.sourceforge.pmd.lang.ast.xpath.AbstractASTXPathHandler;
import net.sourceforge.pmd.lang.ast.xpath.DocumentNavigator;
import net.sourceforge.pmd.lang.rule.AbstractRuleChainVisitor;
import net.sourceforge.pmd.lang.rule.AbstractRuleViolationFactory;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
import net.sourceforge.pmd.lang.rule.RuleChainVisitor;
import net.sourceforge.pmd.util.IOUtil;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.sxpath.IndependentContext;

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
    }

    public static Language getInstance() {
        return LanguageRegistry.getLanguage(NAME);
    }

    public static DummyRootNode parse(String code) {
        return parse(code, "nofilename");
    }

    public static DummyRootNode parse(String code, String filename) {
        DummyRootNode rootNode = readLispNode(code);
        AbstractParser.setFileName(filename, rootNode);
        return rootNode;
    }

    /**
     * @deprecated for removal with PMD 7. A language dependent rule chain visitor is not needed anymore.
     *     See {@link RuleChainVisitor}.
     */
    @Deprecated
    public static class DummyRuleChainVisitor extends AbstractRuleChainVisitor {

        @Override
        protected void visit(Rule rule, Node node, RuleContext ctx) {
            rule.apply(Arrays.asList(node), ctx);
        }

        @Override
        protected void indexNodes(List<Node> nodes, RuleContext ctx) {
            for (Node n : nodes) {
                indexNode(n);
                List<Node> childs = new ArrayList<>();
                for (int i = 0; i < n.getNumChildren(); i++) {
                    childs.add(n.getChild(i));
                }
                indexNodes(childs, ctx);
            }
        }
    }

    public static class Handler extends AbstractLanguageVersionHandler {
        public static class TestFunctions {
            public static boolean typeIs(final XPathContext context, final String fullTypeName) {
                return false;
            }
        }

        @Override
        public XPathHandler getXPathHandler() {
            return new AbstractASTXPathHandler() {
                @Override
                public void initialize(IndependentContext context) {
                    super.initialize(context, LanguageRegistry.getLanguage(DummyLanguageModule.NAME), TestFunctions.class);
                }

                @Override
                public void initialize() {
                }

                @Override
                public Navigator getNavigator() {
                    return new DocumentNavigator();
                }
            };
        }

        @Override
        public RuleViolationFactory getRuleViolationFactory() {
            return new RuleViolationFactory();
        }

        @Override
        public Parser getParser(ParserOptions parserOptions) {
            return new AbstractParser(parserOptions) {
                @Override
                public Node parse(String fileName, Reader source) throws ParseException {
                    try {
                        String text = IOUtil.readToString(source);
                        DummyRootNode rootNode = readLispNode(text);
                        AbstractParser.setFileName(fileName, rootNode);
                        return rootNode;
                    } catch (IOException e) {
                        throw new ParseException(e);
                    }
                }

                @Override
                public Map<Integer, String> getSuppressMap() {
                    return Collections.emptyMap();
                }

                @Override
                public boolean canParse() {
                    return true;
                }

                @Override
                protected TokenManager createTokenManager(Reader source) {
                    return null;
                }
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
        final DummyRootNode root = new DummyRootNode();
        DummyNode top = root;
        SourceCodePositioner positioner = new SourceCodePositioner(text);
        top.setCoords(1, 1, positioner.getLastLine(), positioner.getLastLineColumn());
        int lastNodeStart = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '(') {
                DummyNode node = new DummyNode();
                node.setParent(top);
                top.jjtAddChild(node, top.getNumChildren());
                // setup coordinates
                int bline = positioner.lineNumberFromOffset(i);
                int bcol = positioner.columnFromOffset(bline, i);
                node.setBeginLine(bline);
                node.setBeginColumn(bcol);
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
                top.setEndLine(eline);
                top.setEndColumn(ecol);

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

    public static class RuleViolationFactory extends AbstractRuleViolationFactory {

        @Override
        protected RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message) {
            return createRuleViolation(rule, ruleContext, node, message, 0, 0);
        }

        @Override
        protected RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message,
                                                    int beginLine, int endLine) {
            ParametricRuleViolation<Node> rv = new ParametricRuleViolation<Node>(rule, ruleContext, node, message) {
                public String getPackageName() {
                    this.packageName = "foo"; // just for testing variable expansion
                    return super.getPackageName();
                }
            };
            rv.setLines(beginLine, endLine);
            return rv;
        }
    }
}
