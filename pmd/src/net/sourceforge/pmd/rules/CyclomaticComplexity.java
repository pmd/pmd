/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTBlockStatement;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTForStatement;
import net.sourceforge.pmd.ast.ASTIfStatement;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.ASTSwitchLabel;
import net.sourceforge.pmd.ast.ASTSwitchStatement;
import net.sourceforge.pmd.ast.ASTWhileStatement;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.ASTEnumDeclaration;

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

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }

        entryStack.push(new Entry(node));
        super.visit(node, data);
        Entry classEntry = (Entry) entryStack.pop();
        if ((classEntry.getComplexityAverage() >= getIntProperty("reportLevel")) || (classEntry.highestDecisionPoints >= getIntProperty("reportLevel"))) {
            addViolation(data, node, new String[]{"class", node.getImage(), String.valueOf(classEntry.getComplexityAverage()) + " (Highest = " + String.valueOf(classEntry.highestDecisionPoints) + ")"});
        }
        return data;
    }

    public Object visit(ASTMethodDeclaration node, Object data) {
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
            addViolation(data, node, new String[]{"method", (methodDeclarator == null) ? "" : methodDeclarator.getImage(), String.valueOf(methodEntry.decisionPoints)});
        }

        return data;
    }

    public Object visit(ASTEnumDeclaration node, Object data) {
        entryStack.push(new Entry(node));
        super.visit(node, data);
        Entry classEntry = (Entry) entryStack.pop();
        if ((classEntry.getComplexityAverage() >= getIntProperty("reportLevel")) || (classEntry.highestDecisionPoints >= getIntProperty("reportLevel"))) {
            addViolation(data, node, new String[]{"class", node.getImage(), String.valueOf(classEntry.getComplexityAverage()) + "(Highest = " + String.valueOf(classEntry.highestDecisionPoints) + ")"});
        }
        return data;
    }

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
            addViolation(data, node, new String[]{"constructor", classEntry.node.getImage(), String.valueOf(constructorDecisionPointCount)});
        }
        return data;
    }

}
