/*
 * User: tom
 * Date: Jun 21, 2002
 * Time: 11:26:34 AM
 */
package net.sourceforge.pmd.rules;

import java.util.Iterator;
import java.util.Stack;
import java.util.HashSet;
import java.util.Set;
import java.text.MessageFormat;

import net.sourceforge.pmd.ast.*;
import net.sourceforge.pmd.*;
import net.sourceforge.pmd.symboltable.*;

public class UnusedPrivateInstanceVariableRule extends AbstractRule {

    private Stack nameSpaces = new Stack();
    private boolean foundDeclarationsAlready;

    /**
     * Skip interfaces because they don't have instance variables.
     */
    public Object visit(ASTInterfaceDeclaration node, Object data) {
        return data;
    }

    public Object visit(ASTCompilationUnit node, Object data) {
        nameSpaces.clear();
        foundDeclarationsAlready = false;
        super.visit(node, data);
        if (!nameSpaces.isEmpty()) {
            RuleContext ctx = (RuleContext)data;
            OldSymbolTable scope = ((Namespace)nameSpaces.peek()).peek();
            for (Iterator i = scope.getUnusedSymbols(); i.hasNext();) {
                OldSymbol symbol = (OldSymbol)i.next();
                ctx.getReport().addRuleViolation(createRuleViolation(ctx, symbol.getLine(), MessageFormat.format(getMessage(), new Object[] {symbol.getImage()})));
            }
        }
        return data;
    }

    public Object visit(ASTClassBody node, Object data) {
        if (!foundDeclarationsAlready) {
            foundDeclarationsAlready = true;
            Namespace nameSpace = new Namespace();
            nameSpace.addTable();
            nameSpaces.push(nameSpace);
            for (int i=0;i<node.jjtGetNumChildren(); i++) {
                SimpleNode child = (SimpleNode)node.jjtGetChild(i);
                if (child instanceof ASTClassBodyDeclaration && child.jjtGetNumChildren() > 0 &&  child.jjtGetChild(0) instanceof ASTFieldDeclaration) {
                    ASTFieldDeclaration field = (ASTFieldDeclaration)child.jjtGetChild(0);
                    AccessNode access = (AccessNode)field;
                    if (!access.isPrivate()) {
                        continue;
                    }
                    SimpleNode target = (SimpleNode)field.jjtGetChild(1).jjtGetChild(0);
                    if (target.getImage() != null && target.getImage().equals("serialVersionUID")) {
                        continue;
                    }
                    Namespace group = (Namespace)nameSpaces.peek();
                    group.peek().add(new OldSymbol(target.getImage(), target.getBeginLine()));
                }
            }
        }
        super.visit(node, data);
        return data;
    }

    private Set params = new HashSet();

    public Object visit(ASTMethodDeclaration node, Object data) {
        super.visit(node, data);
        params.clear();
        return data;
    }

    public Object visit(ASTConstructorDeclaration node, Object data) {
        super.visit(node, data);
        params.clear();
        return data;
    }

    public Object visit(ASTFormalParameter node, Object data) {
        ASTVariableDeclaratorId paramName = (ASTVariableDeclaratorId)node.jjtGetChild(1);
        params.add(paramName.getImage());
        return data;
    }

    public Object visit(ASTPrimarySuffix node, Object data) {
        if ((node.jjtGetParent() instanceof ASTPrimaryExpression) && (node.getImage() != null)) {
            ASTPrimaryExpression parent = (ASTPrimaryExpression)node.jjtGetParent();

            boolean force = false;
            if (parent.jjtGetChild(0) instanceof ASTPrimaryPrefix) {
                ASTPrimaryPrefix prefix = (ASTPrimaryPrefix)parent.jjtGetChild(0);
                force = prefix.usesThisModifier();
            }
            recordPossibleUsage(node, force);
        }
        return super.visit(node, data);
    }

    public Object visit(ASTName node, Object data) {
        if ((node.jjtGetParent() instanceof ASTPrimaryPrefix)) {
            recordPossibleUsage(node, false);
        }
        return super.visit(node, data);
    }

    private void recordPossibleUsage(SimpleNode node, boolean force) {
        String otherImg = (node.getImage().indexOf('.') == -1) ? node.getImage() : node.getImage().substring(node.getImage().indexOf('.')+1);
        Namespace group = (Namespace)nameSpaces.peek();
        String name1 = node.getImage();
        if ((!params.contains((name1.indexOf('.') == -1) ? name1 : name1.substring(0, name1.indexOf('.'))) && !params.contains(otherImg)) || force) {
            String name2 = node.getImage();
            group.peek().recordPossibleUsageOf(new OldSymbol((name2.indexOf('.') == -1) ? name2 : name2.substring(0, name2.indexOf('.')), node.getBeginLine()), node);
            group.peek().recordPossibleUsageOf(new OldSymbol(otherImg, node.getBeginLine()), node);
        }
    }
}
