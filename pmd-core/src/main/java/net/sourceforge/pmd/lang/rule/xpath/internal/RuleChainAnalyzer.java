/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.internal;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;

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
import net.sf.saxon.pattern.CombinedNodeTest;
import net.sf.saxon.pattern.NameTest;
import net.sf.saxon.type.Type;

/**
 * Analyzes the xpath expression to find the root path selector for a element. If found,
 * the element name is available via {@link RuleChainAnalyzer#getRootElements()} and the
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
    private List<String> rootElement;
    private boolean rootElementReplaced;
    private boolean insideExpensiveExpr;
    private boolean foundPathInsideExpensive;
    private boolean foundCombinedNodeTest;

    public RuleChainAnalyzer(Configuration currentConfiguration) {
        this.configuration = currentConfiguration;
    }

    public List<String> getRootElements() {
        if (!foundPathInsideExpensive && rootElementReplaced) {
            return rootElement == null ? Collections.emptyList() : rootElement;
        }
        return Collections.emptyList();
    }

    @Override
    public Expression visit(DocumentSorter e) {
        DocumentSorter result = (DocumentSorter) super.visit(e);
        // sorting of the nodes must be done after all nodes have been found
        return result.getBaseExpression();
    }

    public Expression visitSlashPreserveRootElement(SlashExpression e) {
        Expression start = visit(e.getStart());

        // save state
        List<String> elt = rootElement;
        boolean replaced = rootElementReplaced;

        Expression step = visit(e.getStep());

        if (!(e.getStart() instanceof RootExpression)) {
            // restore
            rootElement = elt;
            rootElementReplaced = replaced;
        }

        return new SlashExpression(start, step);
    }

    @Override
    public Expression visit(SlashExpression e) {
        if (!insideExpensiveExpr && rootElement == null) {
            Expression result = visitSlashPreserveRootElement(e);
            if (rootElement != null && !rootElementReplaced) {
                if (result instanceof SlashExpression) {
                    SlashExpression newPath = (SlashExpression) result;
                    Expression step = newPath.getStep();
                    if (step instanceof FilterExpression) {
                        FilterExpression filterExpression = (FilterExpression) step;

                        Deque<Expression> filters = new ArrayDeque<>();
                        Expression walker = filterExpression;
                        while (walker instanceof FilterExpression) {
                            filters.push(((FilterExpression) walker).getFilter());
                            walker = ((FilterExpression) walker).getBase();
                        }
                        result = new FilterExpression(new AxisExpression(AxisInfo.SELF, null), filters.pop());
                        while (!filters.isEmpty()) {
                            result = new FilterExpression(result, filters.pop());
                        }
                        rootElementReplaced = true;
                    } else if (step instanceof AxisExpression) {
                        Expression start = newPath.getStart();
                        if (start instanceof RootExpression) {
                            result = new AxisExpression(AxisInfo.SELF, null);
                        } else if (start instanceof VennExpression) {
                            // abort, set rootElementReplaced so that the
                            // nodes above won't try to replace themselves
                            rootElement = null;
                            result = e;
                        } else {
                            result = new SlashExpression(start, new AxisExpression(AxisInfo.SELF, null));
                        }
                        rootElementReplaced = true;
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
        if (rootElement == null && e.getNodeTest() instanceof NameTest && !foundCombinedNodeTest) {
            NameTest test = (NameTest) e.getNodeTest();
            if (test.getPrimitiveType() == Type.ELEMENT && e.getAxis() == AxisInfo.DESCENDANT) {
                rootElement = listOf(configuration.getNamePool().getClarkName(test.getFingerprint()));
            } else if (test.getPrimitiveType() == Type.ELEMENT && e.getAxis() == AxisInfo.CHILD) {
                rootElement = listOf(configuration.getNamePool().getClarkName(test.getFingerprint()));
            }
        } else if (e.getNodeTest() instanceof CombinedNodeTest) {
            foundCombinedNodeTest = true;
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
        return PmdDocumentSorter.INSTANCE;
    }

}
