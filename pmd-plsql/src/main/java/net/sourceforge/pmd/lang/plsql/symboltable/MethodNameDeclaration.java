/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.symboltable;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.plsql.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.plsql.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.plsql.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.plsql.ast.ASTTriggerTimingPointSection;
import net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode;
import net.sourceforge.pmd.lang.symboltable.AbstractNameDeclaration;

public class MethodNameDeclaration extends AbstractNameDeclaration {
    private static final Logger LOGGER = Logger.getLogger(MethodNameDeclaration.class.getName());

    public MethodNameDeclaration(ASTMethodDeclarator node) {
        super(node);
    }

    /**
     * Treat a TimingPointSection within a Compound Trigger like a packaged
     * FUNCTION or PROCEDURE. SRT
     *
     * @param node
     */
    public MethodNameDeclaration(ASTTriggerTimingPointSection node) {
        super(node);
    }

    public int getParameterCount() {
        return ((ASTMethodDeclarator) node).getParameterCount();
    }

    /**
     * PL/SQL does not currently allow varargs outside the STANDARD package.
     *
     * @return false
     */
    public boolean isVarargs() {
        return false;
    }

    public ASTMethodDeclarator getMethodNameDeclaratorNode() {
        return (ASTMethodDeclarator) node;
    }

    public String getParameterDisplaySignature() {
        StringBuilder sb = new StringBuilder("(");
        ASTFormalParameters params = (ASTFormalParameters) node.getChild(0);
        // TODO - this can be optimized - add [0] then ,[n] in a loop.
        // no need to trim at the end
        for (int i = 0; i < ((ASTMethodDeclarator) node).getParameterCount(); i++) {
            ASTFormalParameter p = (ASTFormalParameter) params.getChild(i);
            sb.append(p.getTypeNode().getTypeImage());
            // if (p.isVarargs()) {
            // sb.append("...");
            // }
            sb.append(',');
        }
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(')');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MethodNameDeclaration)) {
            return false;
        }

        MethodNameDeclaration other = (MethodNameDeclaration) o;

        // compare name
        if (!other.node.getImage().equals(node.getImage())) {
            return false;
        }

        // compare parameter count - this catches the case where there are no
        // params, too
        if (((ASTMethodDeclarator) other.node).getParameterCount() != ((ASTMethodDeclarator) node)
                .getParameterCount()) {
            return false;
        }

        // compare parameter types
        // SRT ASTFormalParameters myParams = (ASTFormalParameters)
        // node.getChild(0);
        // SRT ASTFormalParameters otherParams = (ASTFormalParameters)
        // other.node.getChild(0);
        ASTFormalParameters myParams = node.getFirstDescendantOfType(ASTFormalParameters.class);
        ASTFormalParameters otherParams = other.node.getFirstDescendantOfType(ASTFormalParameters.class);
        for (int i = 0; i < ((ASTMethodDeclarator) node).getParameterCount(); i++) {
            ASTFormalParameter myParam = (ASTFormalParameter) myParams.getChild(i);
            ASTFormalParameter otherParam = (ASTFormalParameter) otherParams.getChild(i);

            // Compare vararg
            // if (myParam.isVarargs() != otherParam.isVarargs()) {
            // return false;
            // }

            Node myTypeNode = myParam.getTypeNode().getChild(0);
            Node otherTypeNode = otherParam.getTypeNode().getChild(0);

            // compare primitive vs reference type
            if (myTypeNode.getClass() != otherTypeNode.getClass()) {
                return false;
            }

            // simple comparison of type images
            // this can be fooled by one method using "String"
            // and the other method using "java.lang.String"
            // once we get real types in here that should get fixed
            String myTypeImg;
            String otherTypeImg;
            // if (myTypeNode instanceof ASTPrimitiveType) {
            // myTypeImg = myTypeNode.getImage();
            // otherTypeImg = otherTypeNode.getImage();
            // } else {
            myTypeImg = ((AbstractPLSQLNode) myTypeNode.getChild(0)).getImage();
            otherTypeImg = ((AbstractPLSQLNode) otherTypeNode.getChild(0)).getImage();
            // }

            if (!myTypeImg.equals(otherTypeImg)) {
                return false;
            }

            // if type is ASTPrimitiveType and is an array, make sure the other
            // one is also
        }
        return true;
    }

    @Override
    public int hashCode() {
        try {
            // SRT node.getImage().hashCode() + ((ASTMethodDeclarator)node).getParameterCount();
            return node.hashCode();
        } catch (Exception e) {
            if (LOGGER.isLoggable(Level.FINEST)) {
                LOGGER.finest(
                        "MethodNameDeclaration problem for " + node + " of class " + node.getClass().getCanonicalName()
                                + " => " + node.getBeginLine() + "/" + node.getBeginColumn());
            }
            // @TODO SRT restore the thrown exception - throw e;
            return 0;
        }
    }

    @Override
    public String toString() {
        // SRT return "Method " + node.getImage() + ", line " +
        // node.getBeginLine() + ", params = " + ((ASTMethodDeclarator)
        // node).getParameterCount();
        return node.toString();
    }
}
