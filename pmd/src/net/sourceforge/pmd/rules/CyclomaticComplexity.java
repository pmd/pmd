/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.ast.ASTBlockStatement;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTForStatement;
import net.sourceforge.pmd.ast.ASTIfStatement;
import net.sourceforge.pmd.ast.ASTSwitchLabel;
import net.sourceforge.pmd.ast.ASTSwitchStatement;
import net.sourceforge.pmd.ast.ASTWhileStatement;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;

import java.text.MessageFormat;
import java.util.Stack;

/**
 * @author Donald A. Leckie
 * @version $Revision$, $Date$
 * @since January 14, 2003
 */
public class CyclomaticComplexity extends AbstractRule {

    private static class Entry {
        private SimpleNode node;
        private int decisionPoints = 1;
        public int highestDecisionPoints;
        public int methodCount;

        private Entry(SimpleNode node) {
            this.node = node;
        }

        public void bumpDecisionPoints() {
            decisionPoints++;
        }

        public void bumpDecisionPoints(int size) {
            decisionPoints += size;
        }

        public int getComplexityAverage() {
            return ((double) methodCount == 0) ? 1 : (int) (Math.rint((double) decisionPoints / (double) methodCount));
        }
    }

    private Stack entryStack = new Stack();

    public Object visit(ASTIfStatement node, Object data) {
        ((Entry) entryStack.peek()).bumpDecisionPoints();
        super.visit(node, data);
        return data;
    }

    public Object visit(ASTForStatement node, Object data) {
        ((Entry) entryStack.peek()).bumpDecisionPoints();
        super.visit(node, data);
        return data;
    }

    public Object visit(ASTSwitchStatement node, Object data) {
        Entry entry = (Entry) entryStack.peek();
        int childCount = node.jjtGetNumChildren();
        int lastIndex = childCount - 1;
        for (int n = 0; n < lastIndex; n++) {
            Node childNode = node.jjtGetChild(n);
            if (childNode instanceof ASTSwitchLabel) {
                childNode = node.jjtGetChild(n + 1);
                if (childNode instanceof ASTBlockStatement) {
                    entry.bumpDecisionPoints();
                }
            }
        }
        super.visit(node, data);
        return data;
    }

    public Object visit(ASTWhileStatement node, Object data) {
        ((Entry) entryStack.peek()).bumpDecisionPoints();
        super.visit(node, data);
        return data;
    }

/*
    public Object visit(ASTUnmodifiedClassDeclaration node, Object data) {
        entryStack.push(new Entry(node));
        super.visit(node, data);
        Entry classEntry = (Entry) entryStack.pop();
        if ((classEntry.getComplexityAverage() >= getIntProperty("reportLevel")) || (classEntry.highestDecisionPoints >= getIntProperty("reportLevel"))) {
            RuleContext ruleContext = (RuleContext) data;
            String[] args = {"class", node.getImage(), String.valueOf(classEntry.getComplexityAverage()) + " (Highest = " + String.valueOf(classEntry.highestDecisionPoints) + ")"};
            RuleViolation ruleViolation = createRuleViolation(ruleContext, node, MessageFormat.format(getMessage(), args));
            ruleContext.getReport().addRuleViolation(ruleViolation);
        }
        return data;
    }
*/

/*
    public Object visit(ASTMethodDeclaration node, Object data) {
        Node parentNode = node.jjtGetParent();
        while (parentNode != null) {
            if (parentNode instanceof ASTInterfaceDeclaration) {
                return data;
            }
            parentNode = parentNode.jjtGetParent();
        }

        entryStack.push(new Entry(node));
        super.visit(node, data);
        Entry methodEntry = (Entry) entryStack.pop();
        int methodDecisionPoints = methodEntry.decisionPoints;
        Entry classEntry = (Entry) entryStack.peek();
        classEntry.methodCount++;
        classEntry.bumpDecisionPoints(methodDecisionPoints);

        if (methodDecisionPoints > classEntry.highestDecisionPoints) {
            classEntry.highestDecisionPoints = methodDecisionPoints;
        }

        ASTMethodDeclarator methodDeclarator = null;
        for (int n = 0; n < node.jjtGetNumChildren(); n++) {
            Node childNode = node.jjtGetChild(n);
            if (childNode instanceof ASTMethodDeclarator) {
                methodDeclarator = (ASTMethodDeclarator) childNode;
                break;
            }
        }

        if (methodEntry.decisionPoints >= getIntProperty("reportLevel")) {
            RuleContext ruleContext = (RuleContext) data;
            String[] args = {"method", (methodDeclarator == null) ? "" : methodDeclarator.getImage(), String.valueOf(methodEntry.decisionPoints)};
            ruleContext.getReport().addRuleViolation(createRuleViolation(ruleContext, node, MessageFormat.format(getMessage(), args)));
        }

        return data;
    }
*/

    public Object visit(ASTConstructorDeclaration node, Object data) {
        entryStack.push(new Entry(node));
        super.visit(node, data);
        Entry constructorEntry = (Entry) entryStack.pop();
        int constructorDecisionPointCount = constructorEntry.decisionPoints;
        Entry classEntry = (Entry) entryStack.peek();
        classEntry.methodCount++;
        classEntry.decisionPoints += constructorDecisionPointCount;
        if (constructorDecisionPointCount > classEntry.highestDecisionPoints) {
            classEntry.highestDecisionPoints = constructorDecisionPointCount;
        }
        if (constructorEntry.decisionPoints >= getIntProperty("reportLevel")) {
            RuleContext ruleContext = (RuleContext) data;
            String[] args = {"constructor", classEntry.node.getImage(), String.valueOf(constructorDecisionPointCount)};
            RuleViolation ruleViolation = createRuleViolation(ruleContext, node, MessageFormat.format(getMessage(), args));
            ruleContext.getReport().addRuleViolation(ruleViolation);
        }
        return data;
    }

}
