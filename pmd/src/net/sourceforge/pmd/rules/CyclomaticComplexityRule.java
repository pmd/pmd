package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.ast.ASTBlockStatement;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTForStatement;
import net.sourceforge.pmd.ast.ASTIfStatement;
import net.sourceforge.pmd.ast.ASTInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.ASTSwitchLabel;
import net.sourceforge.pmd.ast.ASTSwitchStatement;
import net.sourceforge.pmd.ast.ASTUnmodifiedClassDeclaration;
import net.sourceforge.pmd.ast.ASTWhileStatement;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;

import java.text.MessageFormat;
import java.util.Stack;

/**
 *
 * @author Donald A. Leckie
 * @since January 14, 2003
 * @version $Revision$, $Date$
 */
public class CyclomaticComplexityRule extends AbstractRule
{
    private Stack m_entryStack = new Stack();

    /**
     **************************************************************************
     *
     * @param node
     * @param data
     *
     * @return
     */
    public Object visit(ASTIfStatement node, Object data)
    {
        Entry entry = (Entry) m_entryStack.peek();
        entry.m_decisionPoints++;
        super.visit(node, data);

        return data;
    }

    /**
     **************************************************************************
     *
     * @param node
     * @param data
     *
     * @return
     */
    public Object visit(ASTForStatement node, Object data)
    {
        Entry entry = (Entry) m_entryStack.peek();
        entry.m_decisionPoints++;
        super.visit(node, data);

        return data;
    }

    /**
     **************************************************************************
     *
     * @param node
     * @param data
     *
     * @return
     */
    public Object visit(ASTSwitchStatement node, Object data)
    {
        Entry entry = (Entry) m_entryStack.peek();

        int childCount = node.jjtGetNumChildren();
        int lastIndex = childCount - 1;

        for (int n = 0; n < lastIndex; n++)
        {
            Node childNode = node.jjtGetChild(n);

            if (childNode instanceof ASTSwitchLabel)
            {
                childNode = node.jjtGetChild(n + 1);

                if (childNode instanceof ASTBlockStatement)
                {
                    entry.m_decisionPoints++;
                }
            }
        }

        super.visit(node, data);

        return data;
    }

    /**
     **************************************************************************
     *
     * @param node
     * @param data
     *
     * @return
     */
    public Object visit(ASTWhileStatement node, Object data)
    {
        Entry entry = (Entry) m_entryStack.peek();
        entry.m_decisionPoints++;
        super.visit(node, data);

        return data;
    }

    /**
     **************************************************************************
     *
     * @param node
     * @param data
     *
     * @return
     */
    public Object visit(ASTUnmodifiedClassDeclaration node, Object data)
    {
        m_entryStack.push(new Entry(node));
        super.visit(node, data);
        Entry classEntry = (Entry) m_entryStack.pop();
        double decisionPoints = (double) classEntry.m_decisionPoints;
        double methodCount = (double) classEntry.m_methodCount;
        int complexityAverage = (methodCount == 0) ? 1 : (int) (Math.rint(decisionPoints / methodCount));

        if ((complexityAverage >= getIntProperty("reportLevel")) ||
            (classEntry.m_highestDecisionPoints >= getIntProperty("reportLevel")))
        {
            // The {0} "{1}" has a cyclomatic complexity of {2}.
            RuleContext ruleContext = (RuleContext) data;
            String template = getMessage();
            String className = node.getImage();
            String complexityHighest = String.valueOf(classEntry.m_highestDecisionPoints);
            String complexity = String.valueOf(complexityAverage)
                              + " (Highest = "
                              + complexityHighest
                              + ")";
            String[] args = {"class", className, complexity};
            String message = MessageFormat.format(template, args);
            int lineNumber = node.getBeginLine();
            RuleViolation ruleViolation = createRuleViolation(ruleContext, lineNumber, message);
            ruleContext.getReport().addRuleViolation(ruleViolation);
        }

        return data;
    }

    /**
     **************************************************************************
     *
     * @param node
     * @param data
     *
     * @return
     */
    public Object visit(ASTMethodDeclaration node, Object data)
    {
        Node parentNode = node.jjtGetParent();

        while (parentNode != null)
        {
            if (parentNode instanceof ASTInterfaceDeclaration)
            {
                return data;
            }

            parentNode = parentNode.jjtGetParent();
        }

        m_entryStack.push(new Entry(node));
        super.visit(node, data);
        Entry methodEntry = (Entry) m_entryStack.pop();
        int methodDecisionPoints = methodEntry.m_decisionPoints;
        Entry classEntry = (Entry) m_entryStack.peek();
        classEntry.m_methodCount++;
        classEntry.m_decisionPoints += methodDecisionPoints;

        if (methodDecisionPoints > classEntry.m_highestDecisionPoints)
        {
            classEntry.m_highestDecisionPoints = methodDecisionPoints;
        }

        ASTMethodDeclarator methodDeclarator = null;

        for (int n = 0; n < node.jjtGetNumChildren(); n++)
        {
            Node childNode = node.jjtGetChild(n);

            if (childNode instanceof ASTMethodDeclarator)
            {
                methodDeclarator = (ASTMethodDeclarator) childNode;
                break;
            }
        }

        if (methodEntry.m_decisionPoints >= getIntProperty("reportLevel"))
        {
            // The {0} "{1}" has a cyclomatic complexity of {2}.
            RuleContext ruleContext = (RuleContext) data;
            String template = getMessage();
            String methodName = (methodDeclarator == null) ? "" : methodDeclarator.getImage();
            String complexity = String.valueOf(methodEntry.m_decisionPoints);
            String[] args = {"method", methodName, complexity};
            String message = MessageFormat.format(template, args);
            int lineNumber = node.getBeginLine();
            RuleViolation ruleViolation = createRuleViolation(ruleContext, lineNumber, message);
            ruleContext.getReport().addRuleViolation(ruleViolation);
        }

        return data;
    }

    /**
     **************************************************************************
     *
     * @param node
     * @param data
     *
     * @return
     */
    public Object visit(ASTConstructorDeclaration node, Object data)
    {
        m_entryStack.push(new Entry(node));
        super.visit(node, data);
        Entry constructorEntry = (Entry) m_entryStack.pop();
        int constructorDecisionPointCount = constructorEntry.m_decisionPoints;
        Entry classEntry = (Entry) m_entryStack.peek();
        classEntry.m_methodCount++;
        classEntry.m_decisionPoints += constructorDecisionPointCount;

        if (constructorDecisionPointCount > classEntry.m_highestDecisionPoints)
        {
            classEntry.m_highestDecisionPoints = constructorDecisionPointCount;
        }

        if (constructorEntry.m_decisionPoints >= getIntProperty("reportLevel"))
        {
            // The {0} "{1}" has a cyclomatic complexity of {2}.
            RuleContext ruleContext = (RuleContext) data;
            String template = getMessage();
            String constructorName = classEntry.m_node.getImage();
            String complexity = String.valueOf(constructorDecisionPointCount);
            String[] args = {"constructor", constructorName, complexity};
            String message = MessageFormat.format(template, args);
            int lineNumber = node.getBeginLine();
            RuleViolation ruleViolation = createRuleViolation(ruleContext, lineNumber, message);
            ruleContext.getReport().addRuleViolation(ruleViolation);
        }

        return data;
    }

    /**
     ***************************************************************************
     ***************************************************************************
     ***************************************************************************
     */
    private class Entry
    {
        // ASTUnmodifedClassDeclaration or ASTMethodDeclarator or ASTConstructorDeclaration
        private SimpleNode m_node;
        public int m_decisionPoints = 1;
        public int m_highestDecisionPoints;
        public int m_methodCount;

        /**
         ***********************************************************************
         *
         * @param node
         */
        private Entry(SimpleNode node)
        {
            m_node = node;
        }
    }
}