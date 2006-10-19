package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.ASTImplementsList;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.SimpleNode;

import java.util.List;

public class OverrideBothEqualsAndHashcode extends AbstractRule {

    private boolean implementsComparable = false;

    private boolean containsEquals = false;

    private boolean containsHashCode = false;
    
    private SimpleNode nodeFound = null;

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }
        super.visit(node, data);
        if (!implementsComparable && (containsEquals ^ containsHashCode)) {
            if(nodeFound == null){
                nodeFound = node;
            }
            addViolation(data, nodeFound);
        }
        implementsComparable = containsEquals = containsHashCode = false;
        nodeFound = null;
        return data;
    }

    public Object visit(ASTImplementsList node, Object data) {
        for (int ix = 0; ix < node.jjtGetNumChildren(); ix++) {
            if (node.jjtGetChild(ix).getClass().equals(ASTClassOrInterfaceType.class)
                    && ((SimpleNode) node.jjtGetChild(ix)).hasImageEqualTo("Comparable")) {
                implementsComparable = true;
                return data;
            }
        }
        return super.visit(node, data);
    }

    public Object visit(ASTMethodDeclarator node, Object data) {
        if (implementsComparable) {
            return data;
        }

        int iFormalParams = 0;
        String paramName = null;
        for (int ix = 0; ix < node.jjtGetNumChildren(); ix++) {
            SimpleNode sn = (SimpleNode) node.jjtGetChild(ix);
            if (sn.getClass().equals(ASTFormalParameters.class)) {
                List allParams = ((ASTFormalParameters) sn).findChildrenOfType(ASTFormalParameter.class);
                for (int i = 0; i < allParams.size(); i++) {
                    iFormalParams++;
                    ASTFormalParameter formalParam = (ASTFormalParameter) allParams.get(i);
                    ASTClassOrInterfaceType param = (ASTClassOrInterfaceType) formalParam.getFirstChildOfType(ASTClassOrInterfaceType.class);
                    if (param != null) {
                        paramName = param.getImage();
                    }
                }
            }
        }

        if (iFormalParams == 0 && node.hasImageEqualTo("hashCode")) {
            containsHashCode = true;
            nodeFound = node;
        } else if (iFormalParams == 1 && node.hasImageEqualTo("equals") && ("Object".equals(paramName) || "java.lang.Object".equals(paramName))) {
            containsEquals = true;
            nodeFound = node;
        }
        return super.visit(node, data);
    }

}
