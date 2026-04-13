/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import static net.sourceforge.pmd.properties.NumericConstraints.inRange;
import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;

import java.util.ArrayDeque;
import java.util.Deque;

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
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.reporting.RuleContext;


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

    public static final PropertyDescriptor<Integer> REPORT_LEVEL_DESCRIPTOR
            = PropertyFactory.intProperty("reportLevel")
                             .desc("Cyclomatic Complexity reporting threshold")
                             .require(inRange(1, 30))
                             .defaultValue(10)
                             .build();

    public static final PropertyDescriptor<Boolean> SHOW_CLASSES_COMPLEXITY_DESCRIPTOR = booleanProperty("showClassesComplexity").desc("Add class average violations to the report").defaultValue(true).build();

    public static final PropertyDescriptor<Boolean> SHOW_METHODS_COMPLEXITY_DESCRIPTOR = booleanProperty("showMethodsComplexity").desc("Add method average violations to the report").defaultValue(true).build();

    private int reportLevel;
    private boolean showClassesComplexity = true;
    private boolean showMethodsComplexity = true;

    protected static final class Entry {
        private int decisionPoints = 1;
        public int highestDecisionPoints;
        public int methodCount;

        private Entry() {
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

    protected Deque<Entry> entryStack = new ArrayDeque<>();

    public StdCyclomaticComplexityRule() {
        definePropertyDescriptor(REPORT_LEVEL_DESCRIPTOR);
        definePropertyDescriptor(SHOW_CLASSES_COMPLEXITY_DESCRIPTOR);
        definePropertyDescriptor(SHOW_METHODS_COMPLEXITY_DESCRIPTOR);
    }

    @Override
    public void start(RuleContext ctx) {
        reportLevel = getProperty(REPORT_LEVEL_DESCRIPTOR);
        showClassesComplexity = getProperty(SHOW_CLASSES_COMPLEXITY_DESCRIPTOR);
        showMethodsComplexity = getProperty(SHOW_METHODS_COMPLEXITY_DESCRIPTOR);
    }

    @Override
    public RuleContext visit(ASTUserClass node, RuleContext data) {
        entryStack.push(new Entry());
        super.visit(node, data);
        Entry classEntry = entryStack.pop();
        if (showClassesComplexity) {
            if (classEntry.getComplexityAverage() >= reportLevel || classEntry.highestDecisionPoints >= reportLevel) {
                data.addViolation(node, "class", node.getSimpleName(),
                    classEntry.getComplexityAverage() + " (Highest = " + classEntry.highestDecisionPoints + ')');
            }
        }
        return data;
    }

    @Override
    public RuleContext visit(ASTUserTrigger node, RuleContext data) {
        entryStack.push(new Entry());
        super.visit(node, data);
        Entry classEntry = entryStack.pop();
        if (showClassesComplexity) {
            if (classEntry.getComplexityAverage() >= reportLevel || classEntry.highestDecisionPoints >= reportLevel) {
                data.addViolation(node, "trigger", node.getSimpleName(),
                    classEntry.getComplexityAverage() + " (Highest = " + classEntry.highestDecisionPoints + ')');
            }
        }
        return data;
    }

    @Override
    public RuleContext visit(ASTUserInterface node, RuleContext data) {
        return data;
    }

    @Override
    public RuleContext visit(ASTUserEnum node, RuleContext data) {
        return data;
    }

    @Override
    public RuleContext visit(ASTMethod node, RuleContext data) {
        entryStack.push(new Entry());
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
            String methodType = node.isConstructor() ? "constructor" : "method";
            data.addViolation(node,
                    methodType, node.getImage(), String.valueOf(methodEntry.decisionPoints));
        }
        return data;
    }

    @Override
    public RuleContext visit(ASTIfBlockStatement node, RuleContext data) {
        entryStack.peek().bumpDecisionPoints();
        super.visit(node, data);
        return data;
    }

    @Override
    public RuleContext visit(ASTTryCatchFinallyBlockStatement node, RuleContext data) {
        entryStack.peek().bumpDecisionPoints();
        super.visit(node, data);
        return data;
    }

    @Override
    public RuleContext visit(ASTForLoopStatement node, RuleContext data) {
        entryStack.peek().bumpDecisionPoints();
        super.visit(node, data);
        return data;
    }

    @Override
    public RuleContext visit(ASTForEachStatement node, RuleContext data) {
        entryStack.peek().bumpDecisionPoints();
        super.visit(node, data);
        return data;
    }

    @Override
    public RuleContext visit(ASTWhileLoopStatement node, RuleContext data) {
        entryStack.peek().bumpDecisionPoints();
        super.visit(node, data);
        return data;
    }

    @Override
    public RuleContext visit(ASTDoLoopStatement node, RuleContext data) {
        entryStack.peek().bumpDecisionPoints();
        super.visit(node, data);
        return data;
    }

    @Override
    public RuleContext visit(ASTTernaryExpression node, RuleContext data) {
        entryStack.peek().bumpDecisionPoints();
        super.visit(node, data);
        return data;
    }

    @Override
    public RuleContext visit(ASTBooleanExpression node, RuleContext data) {
        return data;
    }
}
