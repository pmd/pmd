/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTArguments;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.ast.AccessNode;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.symboltable.ClassScope;
import net.sourceforge.pmd.symboltable.MethodNameDeclaration;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class UnusedPrivateMethodRule extends AbstractRule {

    private final Set privateMethodNodes = new HashSet();
    private int depth;

    // Reset state when we leave an ASTCompilationUnit
    public Object visit(ASTCompilationUnit node, Object data) {
        depth = 0;
        super.visit(node, data);
        privateMethodNodes.clear();
        depth = 0;
        return data;
    }

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }

        Map methods = ((ClassScope)node.getScope()).getMethodDeclarations();
        for (Iterator i = methods.keySet().iterator(); i.hasNext();) {
            MethodNameDeclaration mnd = (MethodNameDeclaration)i.next();
            if (check(mnd)) {
                privateMethodNodes.add(mnd);
            }
        }

        depth++;

        // troll for usages, regardless of depth
        super.visit(node, null);

        // if we're back at the top level class, harvest
        if (depth == 1) {
            harvestUnused(data);
        }

        depth--;
        return data;
    }

    private boolean check(MethodNameDeclaration mnd) {
        ASTMethodDeclarator node = (ASTMethodDeclarator)mnd.getNode();
        return ((AccessNode) node.jjtGetParent()).isPrivate() && !node.getImage().equals("readObject") && !node.getImage().equals("writeObject") && !node.getImage().equals("readResolve") && !node.getImage().equals("writeReplace");
    }

    public Object visit(ASTPrimarySuffix node, Object data) {
        if ((node.jjtGetParent() instanceof ASTPrimaryExpression) && (node.getImage() != null)) {
            if (node.jjtGetNumChildren() > 0) {
                ASTArguments args = (ASTArguments) node.jjtGetChild(0);
                removeIfUsed(node, args.getArgumentCount());
                return super.visit(node, data);
            }
            // to handle this.foo()
            //PrimaryExpression
            // PrimaryPrefix
            // PrimarySuffix <-- this node has "foo"
            // PrimarySuffix <-- this node has null
            //  Arguments
            ASTPrimaryExpression parent = (ASTPrimaryExpression) node.jjtGetParent();
            int pointer = 0;
            while (true) {
                if (parent.jjtGetChild(pointer).equals(node)) {
                    break;
                }
                pointer++;
            }
            // now move to the next PrimarySuffix and get the number of arguments
            pointer++;
            // this.foo = foo;
            // yields this:
            // PrimaryExpression
            //  PrimaryPrefix
            //  PrimarySuffix
            // so we check for that
            if (parent.jjtGetNumChildren() <= pointer) {
                return super.visit(node, data);
            }
            if (!(parent.jjtGetChild(pointer) instanceof ASTPrimarySuffix)) {
                return super.visit(node, data);
            }
            ASTPrimarySuffix actualMethodNode = (ASTPrimarySuffix) parent.jjtGetChild(pointer);
            // when does this happen?
            if (actualMethodNode.jjtGetNumChildren() == 0 || !(actualMethodNode.jjtGetChild(0) instanceof ASTArguments)) {
                return super.visit(node, data);
            }
            ASTArguments args = (ASTArguments) actualMethodNode.jjtGetChild(0);
            removeIfUsed(node, args.getArgumentCount());
            // what about Outer.this.foo()?
        }
        return super.visit(node, data);
    }

    public Object visit(ASTName node, Object data) {
        if (node.jjtGetParent() instanceof ASTPrimaryPrefix) {
            ASTPrimaryExpression primaryExpression = (ASTPrimaryExpression) node.jjtGetParent().jjtGetParent();
            if (primaryExpression.jjtGetNumChildren() > 1) {
                ASTPrimarySuffix primarySuffix = (ASTPrimarySuffix) primaryExpression.jjtGetChild(1);
                if (primarySuffix.jjtGetNumChildren() > 0 && (primarySuffix.jjtGetChild(0) instanceof ASTArguments)) {
                    ASTArguments arguments = (ASTArguments) primarySuffix.jjtGetChild(0);
                    removeIfUsed(node, arguments.getArgumentCount());
                }
            }
        }
        return super.visit(node, data);
    }

    private final void removeIfUsed(SimpleNode node, int args) {
        String img = (node.getImage().indexOf('.') == -1) ? node.getImage() : node.getImage().substring(node.getImage().indexOf('.') + 1, node.getImage().length());
        for (Iterator i = privateMethodNodes.iterator(); i.hasNext();) {
            MethodNameDeclaration mnd = (MethodNameDeclaration)i.next();
            ASTMethodDeclarator methodNode = (ASTMethodDeclarator)mnd.getNode();
            // are name and number of parameters the same?
            if (methodNode.getImage().equals(img)
                    && methodNode.getParameterCount() == args
                    && !methodCalledFromItself(node, node.getImage())) {
                // TODO should check parameter types here
                i.remove();
            }
        }
    }

    private final boolean methodCalledFromItself(SimpleNode node, String nodeImage) {
        final ASTMethodDeclaration md = (ASTMethodDeclaration) node.getFirstParentOfType(ASTMethodDeclaration.class);
        if (md!=null) {
            final ASTMethodDeclarator dcl = (ASTMethodDeclarator) md.getFirstChildOfType(ASTMethodDeclarator.class);
            if (dcl!=null && dcl.getImage()!=null&&dcl.getImage().equals(nodeImage)) {
                return true;
            }
        }
        return false;
    }

    private final void harvestUnused(Object ctx) {
        for (Iterator i = privateMethodNodes.iterator(); i.hasNext();) {
            MethodNameDeclaration mnd = (MethodNameDeclaration)i.next();
            ASTMethodDeclarator node = (ASTMethodDeclarator)mnd.getNode();
            addViolation(ctx, node, node.getImage() + mnd.getParameterDisplaySignature());
        }
    }

/*
////    TODO this uses the symbol table
        public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
            for (Iterator i = node.getScope().getUnusedMethodDeclarations();i.hasNext();) {
                VariableNameDeclaration decl = (VariableNameDeclaration)i.next();

                // exclude non-private methods and serializable methods
                if (!decl.getAccessNodeParent().isPrivate() || decl.getImage().equals("readObject") || decl.getImage().equals("writeObject")|| decl.getImage().equals("readResolve")) {
                    continue;
                }

                RuleContext ctx = (RuleContext)data;
                ctx.getReport().addRuleViolation(createRuleViolation(ctx, decl.getNode().getBeginLine(), MessageFormat.format(getMessage(), new Object[] {decl.getNode().getImage()})));
            }
            return super.visit(node, data);
        }
*/


}
