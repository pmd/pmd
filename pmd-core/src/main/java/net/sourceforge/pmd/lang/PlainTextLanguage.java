/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.Map;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.SourceCodePositioner;
import net.sourceforge.pmd.lang.rule.AbstractRuleViolationFactory;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;
import net.sourceforge.pmd.util.IOUtil;

/**
 * A dummy language implementation whose parser produces a single node.
 * This is provided for cases where a non-null language is required, but
 * the parser is not useful. This is useful eg to mock rules when no other
 * language is on the classpath. This language is not exposed by {@link LanguageRegistry}
 * and can only be used explicitly with {@link #getInstance()}.
 *
 * @author Clément Fournier
 * @since 6.48.0
 */
@Experimental
public final class PlainTextLanguage extends BaseLanguageModule {

    private static final Language INSTANCE = new PlainTextLanguage();

    static final String TERSE_NAME = "text";

    private PlainTextLanguage() {
        super("Plain text", "Plain text", TERSE_NAME, "plain-text-file-goo-extension");
        addVersion("default", new TextLvh(), true);
    }

    /**
     * Returns the singleton instance of this language.
     */
    public static Language getInstance() {
        return INSTANCE;
    }

    private static class TextLvh extends AbstractLanguageVersionHandler {

        private static final RuleViolationFactory RV_FACTORY = new AbstractRuleViolationFactory() {
            @Override
            protected RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String s) {
                return new ParametricRuleViolation<>(rule, ruleContext, node, s);
            }

            @Override
            protected RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String s, int i, int i1) {
                return new ParametricRuleViolation<>(rule, ruleContext, node, s);
            }
        };

        @Override
        public RuleViolationFactory getRuleViolationFactory() {
            return RV_FACTORY;
        }

        @Override
        public Parser getParser(final ParserOptions parserOptions) {
            return new Parser() {
                @Override
                public ParserOptions getParserOptions() {
                    return parserOptions;
                }

                @Override
                public TokenManager getTokenManager(String s, Reader reader) {
                    return null;
                }

                @Override
                public boolean canParse() {
                    return true;
                }

                @Override
                public Node parse(String s, Reader reader) throws ParseException {
                    try {
                        return new PlainTextFile(IOUtil.readToString(reader));
                    } catch (IOException e) {
                        throw new ParseException(e);
                    }
                }

                @Override
                public Map<Integer, String> getSuppressMap() {
                    return Collections.emptyMap();
                }
            };
        }
    }

    /**
     * The only node produced by the parser of {@link PlainTextLanguage}.
     */
    public static final class PlainTextFile extends AbstractNode implements RootNode {

        PlainTextFile(String fileText) {
            super(0);
            SourceCodePositioner positioner = new SourceCodePositioner(fileText);
            this.beginLine = 1;
            this.beginColumn = 1;
            this.endLine = positioner.getLastLine();
            this.endColumn = positioner.getLastLineColumn();
        }

        @Override
        public String getXPathNodeName() {
            return "TextFile";
        }

        @Override
        public String getImage() {
            return null;
        }

        @Override
        public void setImage(String image) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeChildAtIndex(int childIndex) {
            throw new IndexOutOfBoundsException();
        }

        @Override
        public String toString() {
            return "Plain text file (" + endLine + "lines)";
        }
    }

}
