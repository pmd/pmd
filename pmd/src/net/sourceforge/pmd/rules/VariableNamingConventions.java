/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimitiveType;
import net.sourceforge.pmd.ast.ASTType;
import net.sourceforge.pmd.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.AccessNode;

import java.util.ArrayList;
import java.util.List;

public class VariableNamingConventions extends AbstractRule {

    public static final String SEPARATOR = ",";

    private String[] staticPrefix;
    private String[] staticSuffix;
    private String[] memberPrefix;
    private String[] memberSuffix;

    public Object visit(ASTCompilationUnit node, Object data) {
        init();
        return super.visit(node, data);
    }

    protected void init() {
        staticPrefix = split(getStringProperty("staticPrefix"));
        staticSuffix = split(getStringProperty("staticSuffix"));
        memberPrefix = split(getStringProperty("memberPrefix"));
        memberSuffix = split(getStringProperty("memberSuffix"));
    }

    public Object visit(ASTFieldDeclaration node, Object data) {
        return checkNames(node, data);
    }

    public Object checkNames(AccessNode node, Object data) {
        ASTType childNodeType = (ASTType) node.jjtGetChild(0);
        String varType = "";
        if (childNodeType.jjtGetChild(0) instanceof ASTName) {
            varType = ((ASTName) childNodeType.jjtGetChild(0)).getImage();
        } else if (childNodeType.jjtGetChild(0) instanceof ASTPrimitiveType) {
            varType = ((ASTPrimitiveType) childNodeType.jjtGetChild(0)).getImage();
        }
        if (varType != null && varType.length() > 0) {
            //Get the variable name
            ASTVariableDeclarator childNodeName = (ASTVariableDeclarator) node.jjtGetChild(1);
            ASTVariableDeclaratorId childNodeId = (ASTVariableDeclaratorId) childNodeName.jjtGetChild(0);
            String varName = childNodeId.getImage();

            if (varName.equals("serialVersionUID")) {
                return data;
            }

            // non static final class fields are OK
            if (node.isFinal() && !node.isStatic()) {
                if (node.jjtGetParent().jjtGetParent().jjtGetParent() instanceof ASTClassOrInterfaceDeclaration) {
                    ASTClassOrInterfaceDeclaration c = (ASTClassOrInterfaceDeclaration)node.jjtGetParent().jjtGetParent().jjtGetParent();
                    if (!c.isInterface()) {
                        return data;
                    }
                }
            }

            // static finals (and interface fields, which are implicitly static and final) are
            // checked for uppercase
            if ((node.isStatic() && node.isFinal()) || (node.jjtGetParent().jjtGetParent().jjtGetParent() instanceof ASTClassOrInterfaceDeclaration && ((ASTClassOrInterfaceDeclaration)node.jjtGetParent().jjtGetParent().jjtGetParent()).isInterface())) {
                if (!varName.equals(varName.toUpperCase())) {
                    addViolation(data, childNodeName, "Variables that are final and static should be in all caps.");
                }
                return data;
            }

            String strippedVarName = null;
            if (node.isStatic()) {
                strippedVarName = normalizeStaticVariableName(varName);
            } else {
                strippedVarName = normalizeMemberVariableName(varName);
            }

            if (strippedVarName.indexOf("_") >= 0) {
                addViolation(data, childNodeName, "Variables that are not final should not contain underscores (except for underscores in standard prefix/suffix).");
            }
            if (Character.isUpperCase(varName.charAt(0))) {
                addViolation(data, childNodeName, "Variables should start with a lowercase character");
            }
        }
        return data;
    }

    private String normalizeMemberVariableName(String varName) {
        return stripSuffix(stripPrefix(varName, memberPrefix), memberSuffix);
    }

    private String normalizeStaticVariableName(String varName) {
        return stripSuffix(stripPrefix(varName, staticPrefix), staticSuffix);
    }

    private String stripSuffix(String varName, String[] suffix) {
        if (suffix != null) {
            for (int i = 0; i < suffix.length; i++) {
                if (varName.endsWith(suffix[i])) {
                    varName = varName.substring(0, varName.length() - suffix[i].length());
                    break;
                }
            }
        }
        return varName;
    }

    private String stripPrefix(String varName, String[] prefix) {
        if (prefix == null) {
            return varName;
        }
        for (int i = 0; i < prefix.length; i++) {
            if (varName.startsWith(prefix[i])) {
                return varName.substring(prefix[i].length());
            }
        }
        return varName;
    }

    protected String[] split(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }

        int index = str.indexOf(SEPARATOR);
        if (index == -1) {
            return new String[]{str};
        }

        List list = new ArrayList();
        int currPos = 0;
        int len = SEPARATOR.length();
        while (index != -1) {
            list.add(str.substring(currPos, index));
            currPos = index + len;
            index = str.indexOf(SEPARATOR, currPos);
        }
        list.add(str.substring(currPos));
        return (String[]) list.toArray(new String[list.size()]);
    }
}
