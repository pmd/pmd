/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.coupling;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReferenceType;
import net.sourceforge.pmd.lang.java.ast.ASTResultType;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.ClassScope;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;


/**
 * CouplingBetweenObjects attempts to capture all unique Class attributes,
 * local variables, and return types to determine how many objects a class is
 * coupled to. This is only a guage and isn't a hard and fast rule. The threshold
 * value is configurable and should be determined accordingly
 *
 * @author aglover
 * @since Feb 20, 2003
 */
public class CouplingBetweenObjectsRule extends AbstractJavaRule {

    private int couplingCount;
    private Set<String> typesFoundSoFar;

    private static final PropertyDescriptor THRESHOLD_DESCRIPTOR = new IntegerProperty(
    	"threshold", "Coupling threshold value", 2, 10, 2, 1.0f
    	);
    
    private static final Map<String, PropertyDescriptor> PROPERTY_DESCRIPTORS_BY_NAME = asFixedMap(THRESHOLD_DESCRIPTOR);
        
    
    public Object visit(ASTCompilationUnit cu, Object data) {
        typesFoundSoFar = new HashSet<String>();
        couplingCount = 0;

        Object returnObj = cu.childrenAccept(this, data);

        if (couplingCount > getIntProperty(THRESHOLD_DESCRIPTOR)) {
            addViolation(data, cu, "A value of " + couplingCount + " may denote a high amount of coupling within the class");
        }

        return returnObj;
    }

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }
        return super.visit(node, data);
    }

    public Object visit(ASTResultType node, Object data) {
        for (int x = 0; x < node.jjtGetNumChildren(); x++) {
            Node tNode = node.jjtGetChild(x);
            if (tNode instanceof ASTType) {
        	Node reftypeNode = tNode.jjtGetChild(0);
                if (reftypeNode instanceof ASTReferenceType) {
                    Node classOrIntType = reftypeNode.jjtGetChild(0);
                    if (classOrIntType instanceof ASTClassOrInterfaceType) {
                	Node nameNode = classOrIntType;
                        this.checkVariableType(nameNode, nameNode.getImage());
                    }
                }
            }
        }
        return super.visit(node, data);
    }

    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        handleASTTypeChildren(node);
        return super.visit(node, data);
    }

    public Object visit(ASTFormalParameter node, Object data) {
        handleASTTypeChildren(node);
        return super.visit(node, data);
    }

    public Object visit(ASTFieldDeclaration node, Object data) {
        for (int x = 0; x < node.jjtGetNumChildren(); ++x) {
            Node firstStmt = node.jjtGetChild(x);
            if (firstStmt instanceof ASTType) {
                ASTType tp = (ASTType) firstStmt;
                Node nd = tp.jjtGetChild(0);
                checkVariableType(nd, nd.getImage());
            }
        }

        return super.visit(node, data);
    }

    /**
     * convience method to handle hierarchy. This is probably too much
     * work and will go away once I figure out the framework
     */
    private void handleASTTypeChildren(Node node) {
        for (int x = 0; x < node.jjtGetNumChildren(); x++) {
            Node sNode = node.jjtGetChild(x);
            if (sNode instanceof ASTType) {
        	Node nameNode = sNode.jjtGetChild(0);
                checkVariableType(nameNode, nameNode.getImage());
            }
        }
    }

    /**
     * performs a check on the variable and updates the counter. Counter is
     * instance for a class and is reset upon new class scan.
     *
     * @param variableType The variable type.
     */
    private void checkVariableType(Node nameNode, String variableType) {
        // TODO - move this into the symbol table somehow?
        if (nameNode.getParentsOfType(ASTClassOrInterfaceDeclaration.class).isEmpty()) {
            return;
        }
        //if the field is of any type other than the class type
        //increment the count
        ClassScope clzScope = ((JavaNode)nameNode).getScope().getEnclosingClassScope();
        if (!clzScope.getClassName().equals(variableType) && (!this.filterTypes(variableType)) && !this.typesFoundSoFar.contains(variableType)) {
            couplingCount++;
            typesFoundSoFar.add(variableType);
        }
    }

    /**
     * Filters variable type - we don't want primatives, wrappers, strings, etc.
     * This needs more work. I'd like to filter out super types and perhaps interfaces
     *
     * @param variableType The variable type.
     * @return boolean true if variableType is not what we care about
     */
    private boolean filterTypes(String variableType) {
        return variableType != null && (variableType.startsWith("java.lang.") || (variableType.equals("String")) || filterPrimitivesAndWrappers(variableType));
    }

    /**
     * @param variableType The variable type.
     * @return boolean true if variableType is a primitive or wrapper
     */
    private boolean filterPrimitivesAndWrappers(String variableType) {
        return variableType.equals("int") || variableType.equals("Integer") || variableType.equals("char") || variableType.equals("Character") || variableType.equalsIgnoreCase("double") || variableType.equalsIgnoreCase("long") || variableType.equalsIgnoreCase("short") || variableType.equalsIgnoreCase("float") || variableType.equalsIgnoreCase("byte") || variableType.equalsIgnoreCase("boolean");
    }
    
    /**
     * @return Map
     */
    protected Map<String, PropertyDescriptor> propertiesByName() {
    	return PROPERTY_DESCRIPTORS_BY_NAME;
    }
}
