/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import java.util.Map;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimitiveType;
import net.sourceforge.pmd.ast.ASTType;
import net.sourceforge.pmd.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.AccessNode;
import net.sourceforge.pmd.properties.StringProperty;

public class VariableNamingConventions extends AbstractRule {

    private String[] staticPrefixes;
    private String[] staticSuffixes;
    private String[] memberPrefixes;
    private String[] memberSuffixes;

    private static final PropertyDescriptor staticPrefixesDescriptor = new StringProperty(
    	"staticPrefix", "Static prefixes", new String[] {""},	1.0f , ','
    	);
 
    private static final PropertyDescriptor staticSuffixesDescriptor = new StringProperty(
       	"staticSuffix", "Static suffixes", new String[] {""},	2.0f , ','
       	);    
 
    private static final PropertyDescriptor memberPrefixesDescriptor = new StringProperty(
       	"memberPrefix", "Member prefixes", new String[] {""},	3.0f , ','
       	);
    
    private static final PropertyDescriptor memberSuffixesDescriptor = new StringProperty(
       	"memberSuffix", "Member suffixes", new String[] {""},	4.0f , ','
       	);
    
    private static final Map propertyDescriptorsByName = asFixedMap( new PropertyDescriptor[] {
    	staticPrefixesDescriptor, staticSuffixesDescriptor, 
    	memberPrefixesDescriptor, memberSuffixesDescriptor
		});
    
    /**
     * @return Map
     */
    protected Map propertiesByName() {
    	return propertyDescriptorsByName;
    }    
    
    public Object visit(ASTCompilationUnit node, Object data) {
        init();
        return super.visit(node, data);
    }

    protected void init() {
        staticPrefixes = getStringProperties(staticPrefixesDescriptor);
        staticSuffixes = getStringProperties(staticSuffixesDescriptor);
        memberPrefixes = getStringProperties(memberPrefixesDescriptor);
        memberSuffixes = getStringProperties(memberSuffixesDescriptor);
    }

    public Object visit(ASTFieldDeclaration node, Object data) {
        return checkNames(node, data);
    }

    private Object checkNames(ASTFieldDeclaration node, Object data) {
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

            if (varName.equals("serialVersionUID") || (node.isFinal() && !node.isStatic() && !node.isInterfaceMember())) {
                return data;
            }

            // static finals (and interface fields, which are implicitly static and final) are
            // checked for uppercase
            if ((node.isStatic() && node.isFinal()) || (node.jjtGetParent().jjtGetParent().jjtGetParent() instanceof ASTClassOrInterfaceDeclaration && ((ASTClassOrInterfaceDeclaration) node.jjtGetParent().jjtGetParent().jjtGetParent()).isInterface())) {
                if (!varName.equals(varName.toUpperCase())) {
                    addViolationWithMessage(data, childNodeName, "Variables that are final and static should be in all caps.");
                }
                return data;
            }

            String strippedVarName = null;
            if (node.isStatic()) {
                strippedVarName = normalizeStaticVariableName(varName);
            } else {
                strippedVarName = normalizeMemberVariableName(varName);
            }

            if (strippedVarName.indexOf('_') >= 0) {
                addViolationWithMessage(data, childNodeName, "Variables that are not final should not contain underscores (except for underscores in standard prefix/suffix).");
            }
            if (Character.isUpperCase(varName.charAt(0))) {
                addViolationWithMessage(data, childNodeName, "Variables should start with a lowercase character");
            }
        }
        return data;
    }

    private String normalizeMemberVariableName(String varName) {
        return stripSuffix(stripPrefix(varName, memberPrefixes), memberSuffixes);
    }

    private String normalizeStaticVariableName(String varName) {
        return stripSuffix(stripPrefix(varName, staticPrefixes), staticSuffixes);
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
}
