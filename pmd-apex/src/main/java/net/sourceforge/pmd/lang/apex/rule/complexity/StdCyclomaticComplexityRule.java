/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.complexity;

import java.util.Stack;

import net.sourceforge.pmd.lang.apex.ast.ASTBooleanExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTDoLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTForEachStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTForLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTIfBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTTernaryExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTTryCatchFinallyBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserEnum;
import net.sourceforge.pmd.lang.apex.ast.ASTUserInterface;
import net.sourceforge.pmd.lang.apex.ast.ASTUserTrigger;
import net.sourceforge.pmd.lang.apex.ast.ASTWhileLoopStatement;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.properties.BooleanProperty;
import net.sourceforge.pmd.properties.IntegerProperty;

/**
 * Implements the standard cyclomatic complexity rule
 * <p>
 * Standard rules: +1 for each decision point, but not including boolean
 * operators unlike CyclomaticComplexityRule.
 * 
 * @author ported on Java version of Alan Hohn, based on work by Donald A.
 *         Leckie
 * 
 * @since June 18, 2014
 */
public class StdCyclomaticComplexityRule extends AbstractApexRule {

    public static final IntegerProperty REPORT_LEVEL_DESCRIPTOR 
            = IntegerProperty.builder("reportLevel")
                             .desc("Cyclomatic Complexity reporting threshold")
                             .min(1).max(30).defalt(10).uiOrder(1.0f).build();

    public static final BooleanProperty SHOW_CLASSES_COMPLEXITY_DESCRIPTOR = new BooleanProperty(
            "showClassesComplexity", "Add class average violations to the report", true, 2.0f);

    public static final BooleanProperty SHOW_METHODS_COMPLEXITY_DESCRIPTOR = new BooleanProperty(
            "showMethodsComplexity", "Add method average violations to the report", true, 3.0f);

    private int reportLevel;
    private boolean showClassesComplexity = true;
    private boolean showMethodsComplexity = true;

    protected static class Entry {
        private int decisionPoints = 1;
        public int highestDecisionPoints;
        public int methodCount;

        private Entry(Node node) {
        }

        public void bumpDecisionPoints() {
            decisionPoints++;
        }

        public void bumpDecisionPoints(int size) {
            decisionPoints += size;
        }

        public int getComplexityAverage() {
            return (double) methodCount == 0 ? 1 : (int) Math.rint((double) decisionPoints / (double) methodCount);
        }
    }

    protected Stack<Entry> entryStack = new Stack<>();

    public StdCyclomaticComplexityRule() {
        definePropertyDescriptor(REPORT_LEVEL_DESCRIPTOR);
        definePropertyDescriptor(SHOW_CLASSES_COMPLEXITY_DESCRIPTOR);
        definePropertyDescriptor(SHOW_METHODS_COMPLEXITY_DESCRIPTOR);

        setProperty(CODECLIMATE_CATEGORIES, "Complexity");
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 250);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {
        reportLevel = getProperty(REPORT_LEVEL_DESCRIPTOR);
        showClassesComplexity = getProperty(SHOW_CLASSES_COMPLEXITY_DESCRIPTOR);
        showMethodsComplexity = getProperty(SHOW_METHODS_COMPLEXITY_DESCRIPTOR);
        entryStack.push(new Entry(node));
        super.visit(node, data);
        Entry classEntry = entryStack.pop();
        if (showClassesComplexity) {
            if (classEntry.getComplexityAverage() >= reportLevel || classEntry.highestDecisionPoints >= reportLevel) {
                addViolation(data, node, new String[] { "class", node.getImage(),
                    classEntry.getComplexityAverage() + " (Highest = " + classEntry.highestDecisionPoints + ')', });
            }
        }
        return data;
    }

    @Override
    public Object visit(ASTUserTrigger node, Object data) {
        reportLevel = getProperty(REPORT_LEVEL_DESCRIPTOR);
        showClassesComplexity = getProperty(SHOW_CLASSES_COMPLEXITY_DESCRIPTOR);
        showMethodsComplexity = getProperty(SHOW_METHODS_COMPLEXITY_DESCRIPTOR);
        entryStack.push(new Entry(node));
        super.visit(node, data);
        Entry classEntry = entryStack.pop();
        if (showClassesComplexity) {
            if (classEntry.getComplexityAverage() >= reportLevel || classEntry.highestDecisionPoints >= reportLevel) {
                addViolation(data, node, new String[] { "trigger", node.getImage(),
                    classEntry.getComplexityAverage() + " (Highest = " + classEntry.highestDecisionPoints + ')', });
            }
        }
        return data;
    }

    @Override
    public Object visit(ASTUserInterface node, Object data) {
        return data;
    }

    @Override
    public Object visit(ASTUserEnum node, Object data) {
        entryStack.push(new Entry(node));
        super.visit(node, data);
        Entry classEntry = entryStack.pop();
        if (classEntry.getComplexityAverage() >= reportLevel || classEntry.highestDecisionPoints >= reportLevel) {
            addViolation(data, node, new String[] { "class", node.getImage(),
                classEntry.getComplexityAverage() + "(Highest = " + classEntry.highestDecisionPoints + ')', });
        }
        return data;
    }

    @Override
    public Object visit(ASTMethod node, Object data) {
        if (!node.getImage().matches("<clinit>|<init>|clone")) {
            entryStack.push(new Entry(node));
            super.visit(node, data);
            Entry methodEntry = entryStack.pop();
            int methodDecisionPoints = methodEntry.decisionPoints;
            Entry classEntry = entryStack.peek();
            classEntry.methodCount++;
            classEntry.bumpDecisionPoints(methodDecisionPoints);

            if (methodDecisionPoints > classEntry.highestDecisionPoints) {
                classEntry.highestDecisionPoints = methodDecisionPoints;
            }

            if (showMethodsComplexity && methodEntry.decisionPoints >= reportLevel) {
                String methodType = (node.getNode().getMethodInfo().isConstructor()) ? "constructor" : "method";
                addViolation(data, node,
                        new String[] { methodType, node.getImage(), String.valueOf(methodEntry.decisionPoints) });
            }
        }
        return data;
    }

    @Override
    public Object visit(ASTIfBlockStatement node, Object data) {
        entryStack.peek().bumpDecisionPoints();
        super.visit(node, data);
        return data;
    }

    @Override
    public Object visit(ASTTryCatchFinallyBlockStatement node, Object data) {
        entryStack.peek().bumpDecisionPoints();
        super.visit(node, data);
        return data;
    }

    @Override
    public Object visit(ASTForLoopStatement node, Object data) {
        entryStack.peek().bumpDecisionPoints();
        super.visit(node, data);
        return data;
    }

    @Override
    public Object visit(ASTForEachStatement node, Object data) {
        entryStack.peek().bumpDecisionPoints();
        super.visit(node, data);
        return data;
    }

    @Override
    public Object visit(ASTWhileLoopStatement node, Object data) {
        entryStack.peek().bumpDecisionPoints();
        super.visit(node, data);
        return data;
    }

    @Override
    public Object visit(ASTDoLoopStatement node, Object data) {
        entryStack.peek().bumpDecisionPoints();
        super.visit(node, data);
        return data;
    }

    @Override
    public Object visit(ASTTernaryExpression node, Object data) {
        entryStack.peek().bumpDecisionPoints();
        super.visit(node, data);
        return data;
    }

    @Override
    public Object visit(ASTBooleanExpression node, Object data) {
        return data;
    }
}
