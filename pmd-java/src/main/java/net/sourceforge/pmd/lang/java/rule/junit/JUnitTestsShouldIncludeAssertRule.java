/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.junit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMarkerAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTMemberValuePair;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTNormalAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTReferenceType;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.symboltable.Scope;

public class JUnitTestsShouldIncludeAssertRule extends AbstractJUnitRule {
    
    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }
        return super.visit(node, data);
    }
    
    @Override
    public Object visit(ASTMethodDeclaration method, Object data) {
        if (isJUnitMethod(method, data)) {
            if (!containsAssert(method.getBlock(), false) && !containsExpect(method.jjtGetParent())) {
                addViolation(data, method);
            }
        }
        return data;
    }
    
    private List<NameDeclaration> getRuleAnnotatedExpectedExceptions(Scope classScope) {
        List<NameDeclaration> result = new ArrayList<>();
        Map<NameDeclaration, List<NameOccurrence>> decls = classScope.getDeclarations();

        for (NameDeclaration decl : decls.keySet()) {
            Node parent = decl.getNode().jjtGetParent().jjtGetParent().jjtGetParent();
            if (parent.hasDescendantOfType(ASTAnnotation.class)
                    && parent.jjtGetChild(1) instanceof ASTFieldDeclaration) {
                if (!"Rule"
                        .equals(parent.getFirstDescendantOfType(ASTMarkerAnnotation.class).jjtGetChild(0).getImage())) {
                    System.out.println(
                            parent.getFirstDescendantOfType(ASTMarkerAnnotation.class).jjtGetChild(0).getImage());
                    continue;
                }

                Node type = parent.getFirstDescendantOfType(ASTReferenceType.class);
                if (!"ExpectedException".equals(type.jjtGetChild(0).getImage())) {
                    System.out.println(type.jjtGetChild(0).getImage());
                    continue;
                }

                result.add(decl);
            }
        }

        return result;
    }
    
    private boolean containsAssert(Node n, boolean assertFound) {
        if (!assertFound) {
            if (n instanceof ASTStatementExpression) {
                if (isAssertOrFailStatement((ASTStatementExpression) n)) {
                    return true;
                }
            }
            if (!assertFound) {
                for (int i = 0; i < n.jjtGetNumChildren() && !assertFound; i++) {
                    Node c = n.jjtGetChild(i);
                    if (containsAssert(c, assertFound)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Tells if the node contains a Test annotation with an expected exception.
     */
    private boolean containsExpect(Node methodParent) {
        List<ASTNormalAnnotation> annotations = methodParent.findDescendantsOfType(ASTNormalAnnotation.class);
        for (ASTNormalAnnotation annotation : annotations) {
            ASTName name = annotation.getFirstChildOfType(ASTName.class);
            if (name != null && ("Test".equals(name.getImage())
                    || name.getType() != null && name.getType().equals(JUNIT4_CLASS))) {
                List<ASTMemberValuePair> memberValues = annotation.findDescendantsOfType(ASTMemberValuePair.class);
                for (ASTMemberValuePair pair : memberValues) {
                    if ("expected".equals(pair.getImage())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Tells if the expression is an assert statement or not.
     */
    private boolean isAssertOrFailStatement(ASTStatementExpression expression) {
        if (expression != null) {
            ASTPrimaryExpression pe = expression.getFirstChildOfType(ASTPrimaryExpression.class);
            if (pe != null) {
                String img = pe.jjtGetChild(0).jjtGetChild(0).getImage();
                if (img != null && (img.startsWith("assert") || img.startsWith("fail")
                        || img.startsWith("Assert.assert") || img.startsWith("Assert.fail"))) {
                    return true;
                }
            }
        }
        return false;
    }
}
