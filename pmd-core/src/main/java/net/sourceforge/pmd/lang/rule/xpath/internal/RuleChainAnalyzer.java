/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.internal;

import java.util.Collections;
import java.util.Comparator;

import net.sourceforge.pmd.lang.ast.Node;

import net.sf.saxon.Configuration;
import net.sf.saxon.expr.AxisExpression;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.FilterExpression;
import net.sf.saxon.expr.LetExpression;
import net.sf.saxon.expr.RootExpression;
import net.sf.saxon.expr.SlashExpression;
import net.sf.saxon.expr.VennExpression;
import net.sf.saxon.expr.sort.DocumentSorter;
import net.sf.saxon.om.AxisInfo;
import net.sf.saxon.pattern.NameTest;
import net.sf.saxon.type.Type;

/**
 * Analyzes the xpath expression to find the root path selector for a element. If found,
 * the element name is available via {@link RuleChainAnalyzer#getRootElement()} and the
 * expression is rewritten to start at "node::self()" instead.
 *
 * <p>It uses a visitor to visit all the different expressions.
 *
 * <p>Example: The XPath expression <code>//A[condition()]/B</code> results the rootElement "A"
 * and the expression is rewritten to be <code>self::node[condition()]/B</code>.
 *
 * <p>DocumentSorter expression is removed. The sorting of the resulting nodes needs to be done
 * after all (sub)expressions have been executed.
 */
public class RuleChainAnalyzer extends SaxonExprVisitor {
    private final Configuration configuration;
    private String rootElement;
    private boolean rootElementReplaced;
    private boolean insideExpensiveExpr;
    private boolean foundPathInsideExpensive;

    public RuleChainAnalyzer(Configuration currentConfiguration) {
        this.configuration = currentConfiguration;
    }

    public String getRootElement() {
        if (!foundPathInsideExpensive && rootElementReplaced) {
            return rootElement;
        }
        return null;
    }

    @Override
    public Expression visit(DocumentSorter e) {
        DocumentSorter result = (DocumentSorter) super.visit(e);
        // sorting of the nodes must be done after all nodes have been found
        return result.getBaseExpression();
    }

    @Override
    public Expression visit(SlashExpression e) {
        if (!insideExpensiveExpr && rootElement == null) {
            if (e.getLhsExpression() instanceof VennExpression) {
                // nested venn expression
                return e;
            }
            Expression result = super.visit(e);
            if (rootElement != null && !rootElementReplaced) {
                if (result instanceof SlashExpression) {
                    SlashExpression newPath = (SlashExpression) result;
                    if (newPath.getStep() instanceof FilterExpression) {
                        FilterExpression filterExpression = (FilterExpression) newPath.getStep();
                        result = new FilterExpression(new AxisExpression(AxisInfo.SELF, null), filterExpression.getFilter());
                        rootElementReplaced = true;
                    } else if (newPath.getStep() instanceof AxisExpression) {
                        Expression start = newPath.getStart();
                        if (start instanceof RootExpression) {
                            result = new AxisExpression(AxisInfo.SELF, null);
                            rootElementReplaced = true;
                        } else if (!(start instanceof VennExpression)) {
                            result = new SlashExpression(start, new AxisExpression(AxisInfo.SELF, null));
                            rootElementReplaced = true;
                        }
                    }
                } else {
                    result = new AxisExpression(AxisInfo.DESCENDANT_OR_SELF, null);
                    rootElementReplaced = true;
                }
            }
            return result;
        } else {
            if (insideExpensiveExpr) {
                foundPathInsideExpensive = true;
            }
            return super.visit(e);
        }
    }

    @Override
    public Expression visit(AxisExpression e) {
        if (rootElement == null && e.getNodeTest() instanceof NameTest) {
            NameTest test = (NameTest) e.getNodeTest();
            if (test.getPrimitiveType() == Type.ELEMENT && e.getAxis() == AxisInfo.DESCENDANT) {
                rootElement = configuration.getNamePool().getClarkName(test.getFingerprint());
            } else if (test.getPrimitiveType() == Type.ELEMENT && e.getAxis() == AxisInfo.CHILD) {
                rootElement = configuration.getNamePool().getClarkName(test.getFingerprint());
            }
        }
        return super.visit(e);
    }

    @Override
    public Expression visit(LetExpression e) {
        // lazy expressions are not a thing in saxon HE
        // instead saxon hoists expensive subexpressions into LetExpressions
        // Eg //A[//B]
        // is transformed to let bs := //B in //A
        // so that the //B is done only once.

        // The cost of an expr is an abstract measure of its expensiveness,
        // Eg the cost of //A or //* is 40, the cost of //A//B is 820
        // (a path expr multiplies the cost of its lhs and rhs)

        if (e.getSequence().getCost() >= 20) {
            boolean prevCtx = insideExpensiveExpr;
            insideExpensiveExpr = true;
            Expression result = super.visit(e);
            insideExpensiveExpr = prevCtx;
            return result;
        } else {
            return super.visit(e);
        }
    }

    @Override
    public Expression visit(VennExpression e) {
        // stop visiting subtree. We assume all unions were at the root
        // and flattened, here we find one that couldn't be flattened
        return e;
    }

    public static Comparator<Node> documentOrderComparator() {
        return DocumentSorterX.INSTANCE;
    }

    /**
     * Split union expressions into their components.
     */
    public static Iterable<Expression> splitUnions(Expression expr) {
        SplitUnions unions = new SplitUnions();
        unions.visit(expr);
        if (unions.getExpressions().isEmpty()) {
            return Collections.singletonList(expr);
        } else {
            return unions.getExpressions();
        }
    }

}
