/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.positive;

import java.util.ArrayDeque;
import java.util.Deque;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTCatchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.properties.BooleanProperty;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;


/**
 * Implements the standard cyclomatic complexity rule
 * <p>
 * Standard rules: +1 for each decision point, including case statements but not
 * including boolean operators unlike CyclomaticComplexityRule.
 *
 * @author Alan Hohn, based on work by Donald A. Leckie
 *
 * @since June 18, 2014
 */
@Deprecated
public class StdCyclomaticComplexityRule extends AbstractJavaRule {

    public static final PropertyDescriptor<Integer> REPORT_LEVEL_DESCRIPTOR
            = PropertyFactory.intProperty("reportLevel")
                             .desc("Cyclomatic Complexity reporting threshold")
                             .require(positive()).defaultValue(10).build();

    public static final BooleanProperty SHOW_CLASSES_COMPLEXITY_DESCRIPTOR = new BooleanProperty(
            "showClassesComplexity", "Add class average violations to the report", true, 2.0f);

    public static final BooleanProperty SHOW_METHODS_COMPLEXITY_DESCRIPTOR = new BooleanProperty(
            "showMethodsComplexity", "Add method average violations to the report", true, 3.0f);

    private int reportLevel;
    private boolean showClassesComplexity = true;
    private boolean showMethodsComplexity = true;

    protected static class Entry {
        private Node node;
        private int decisionPoints = 1;
        public int highestDecisionPoints;
        public int methodCount;

        private Entry(Node node) {
            this.node = node;
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
    public Object visit(ASTCompilationUnit node, Object data) {
        reportLevel = getProperty(REPORT_LEVEL_DESCRIPTOR);
        showClassesComplexity = getProperty(SHOW_CLASSES_COMPLEXITY_DESCRIPTOR);
        showMethodsComplexity = getProperty(SHOW_METHODS_COMPLEXITY_DESCRIPTOR);
        super.visit(node, data);
        return data;
    }

    @Override
    public Object visit(ASTIfStatement node, Object data) {
        entryStack.peek().bumpDecisionPoints();
        super.visit(node, data);
        return data;
    }

    @Override
    public Object visit(ASTCatchStatement node, Object data) {
        entryStack.peek().bumpDecisionPoints();
        super.visit(node, data);
        return data;
    }

    @Override
    public Object visit(ASTForStatement node, Object data) {
        entryStack.peek().bumpDecisionPoints();
        super.visit(node, data);
        return data;
    }

    @Override
    public Object visit(ASTDoStatement node, Object data) {
        entryStack.peek().bumpDecisionPoints();
        super.visit(node, data);
        return data;
    }

    @Override
    public Object visit(ASTSwitchStatement node, Object data) {
        Entry entry = entryStack.peek();

        int childCount = node.getNumChildren();
        int lastIndex = childCount - 1;
        for (int n = 0; n < lastIndex; n++) {
            Node childNode = node.getChild(n);
            if (childNode instanceof ASTSwitchLabel) {
                // default is generally not considered a decision (same as
                // "else")
                ASTSwitchLabel sl = (ASTSwitchLabel) childNode;
                if (!sl.isDefault()) {
                    childNode = node.getChild(n + 1);
                    if (childNode instanceof ASTBlockStatement) {
                        entry.bumpDecisionPoints();
                    }
                }
            }
        }
        super.visit(node, data);
        return data;
    }

    @Override
    public Object visit(ASTWhileStatement node, Object data) {
        entryStack.peek().bumpDecisionPoints();
        super.visit(node, data);
        return data;
    }

    @Override
    public Object visit(ASTConditionalExpression node, Object data) {
        entryStack.peek().bumpDecisionPoints();
        super.visit(node, data);
        return data;
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }

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
    public Object visit(ASTMethodDeclaration node, Object data) {
        entryStack.push(new Entry(node));
        super.visit(node, data);
        Entry methodEntry = entryStack.pop();
        if (!isSuppressed(node)) {
            int methodDecisionPoints = methodEntry.decisionPoints;
            Entry classEntry = entryStack.peek();
            classEntry.methodCount++;
            classEntry.bumpDecisionPoints(methodDecisionPoints);

            if (methodDecisionPoints > classEntry.highestDecisionPoints) {
                classEntry.highestDecisionPoints = methodDecisionPoints;
            }

            ASTMethodDeclarator methodDeclarator = null;
            for (int n = 0; n < node.getNumChildren(); n++) {
                Node childNode = node.getChild(n);
                if (childNode instanceof ASTMethodDeclarator) {
                    methodDeclarator = (ASTMethodDeclarator) childNode;
                    break;
                }
            }

            if (showMethodsComplexity && methodEntry.decisionPoints >= reportLevel) {
                addViolation(data, node,
                        new String[] { "method", methodDeclarator == null ? "" : methodDeclarator.getImage(),
                            String.valueOf(methodEntry.decisionPoints), });
            }
        }
        return data;
    }

    @Override
    public Object visit(ASTEnumDeclaration node, Object data) {
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
    public Object visit(ASTConstructorDeclaration node, Object data) {
        entryStack.push(new Entry(node));
        super.visit(node, data);
        Entry constructorEntry = entryStack.pop();
        if (!isSuppressed(node)) {
            int constructorDecisionPointCount = constructorEntry.decisionPoints;
            Entry classEntry = entryStack.peek();
            classEntry.methodCount++;
            classEntry.decisionPoints += constructorDecisionPointCount;
            if (constructorDecisionPointCount > classEntry.highestDecisionPoints) {
                classEntry.highestDecisionPoints = constructorDecisionPointCount;
            }
            if (showMethodsComplexity && constructorEntry.decisionPoints >= reportLevel) {
                addViolation(data, node, new String[] { "constructor", classEntry.node.getImage(),
                    String.valueOf(constructorDecisionPointCount), });
            }
        }
        return data;
    }
}
