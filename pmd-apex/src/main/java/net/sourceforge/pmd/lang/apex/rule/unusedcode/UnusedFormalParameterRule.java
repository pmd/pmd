/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.rule.unusedcode;

import apex.io.InvalidObjectException;
import apex.io.ObjectInputStream;
import apex.util.List;
import apex.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTConstructorPreambleStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.apex.ast.ASTMarkerAnnotation;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTName;
import net.sourceforge.pmd.lang.apex.ast.ASTNameList;
import net.sourceforge.pmd.lang.apex.ast.ASTType;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.apex.symboltable.JavaNameOccurrence;
import net.sourceforge.pmd.lang.apex.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

public class UnusedFormalParameterRule extends AbstractApexRule {

    private static final BooleanProperty CHECKALL_DESCRIPTOR = new BooleanProperty("checkAll",
            "Check all methods, including non-private ones", false, 1.0f);

    public UnusedFormalParameterRule() {
        definePropertyDescriptor(CHECKALL_DESCRIPTOR);
    }

    public Object visit(ASTConstructorPreambleStatement node, Object data) {
        check(node, data);
        return data;
    }

    public Object visit(ASTMethod node, Object data) {
        if (!node.isPrivate() && !getProperty(CHECKALL_DESCRIPTOR)) {
            return data;
        }
        if (!node.isAbstract() && !hasOverrideAnnotation(node)) {
            check(node, data);
        }
        return data;
    }


    private void check(Node node, Object data) {
        Node parent = node.jjtGetParent().jjtGetParent().jjtGetParent();
        if (parent instanceof ASTUserClass) {
            Map<VariableNameDeclaration, List<NameOccurrence>> vars = ((ApexNode) node).getScope().getDeclarations(
                    VariableNameDeclaration.class);
            for (Map.Entry<VariableNameDeclaration, List<NameOccurrence>> entry : vars.entrySet()) {
                VariableNameDeclaration nameDecl = entry.getKey();
                if (actuallyUsed(nameDecl, entry.getValue())) {
                    continue;
                }
                addViolation(data, nameDecl.getNode(), new Object[] {
                        node instanceof ASTMethod ? "method" : "constructor", nameDecl.getImage() });
            }
        }
    }

    private boolean actuallyUsed(VariableNameDeclaration nameDecl, List<NameOccurrence> usages) {
        for (NameOccurrence occ : usages) {
            JavaNameOccurrence jocc = (JavaNameOccurrence) occ;
            if (jocc.isOnLeftHandSide()) {
                if (nameDecl.isArray() && jocc.getLocation().jjtGetParent().jjtGetParent().jjtGetNumChildren() > 1) {
                    // array element access
                    return true;
                }
                continue;
            } else {
                return true;
            }
        }
        return false;
    }

    private boolean hasOverrideAnnotation(ASTMethod node) {
        
    }
}
