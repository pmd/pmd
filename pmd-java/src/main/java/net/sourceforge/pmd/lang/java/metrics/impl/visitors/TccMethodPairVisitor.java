/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl.visitors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorReducedAdapter;
import net.sourceforge.pmd.lang.java.symboltable.ClassScope;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.Scope;

/**
 * Returns the map of method names to the set local attributes accessed when visiting a class.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class TccMethodPairVisitor extends JavaParserVisitorReducedAdapter {

    /**
     * Collects for each method of the current class, which local attributes are accessed.
     */
    Stack<Map<String, Set<String>>> methodAttributeAccess = new Stack<>();

    /** The name of the current method. */
    private String currentMethodName;


    @Override
    public Object visit(ASTAnyTypeDeclaration node, Object data) {
        methodAttributeAccess.push(new HashMap<String, Set<String>>());
        super.visit(node, data);

        methodAttributeAccess.peek().remove(null);
        return methodAttributeAccess.pop();
    }


    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {

        if (!node.isAbstract()) {
            currentMethodName = node.getQualifiedName().getOperation();
            methodAttributeAccess.peek().put(currentMethodName, new HashSet<String>());

            super.visit(node, data);

            currentMethodName = null;
        }

        return null;
    }


    /**
     * The primary expression node is used to detect access to attributes and method calls. If the access is not for a
     * foreign class, then the {@link #methodAttributeAccess} map is updated for the current method.
     */
    @Override
    public Object visit(ASTPrimaryExpression node, Object data) {
        if (currentMethodName != null) {
            Set<String> methodAccess = methodAttributeAccess.peek().get(currentMethodName);
            String variableName = getVariableName(node);
            if (isLocalAttributeAccess(variableName, node.getScope())) {
                methodAccess.add(variableName);
            }
        }


        return super.visit(node, data);
    }


    private String getVariableName(ASTPrimaryExpression node) {
        ASTPrimaryPrefix prefix = node.getFirstDescendantOfType(ASTPrimaryPrefix.class);
        ASTName name = prefix.getFirstDescendantOfType(ASTName.class);

        String variableName = null;

        if (name != null) {
            int dotIndex = name.getImage().indexOf(".");
            if (dotIndex == -1) {
                variableName = name.getImage();
            } else {
                variableName = name.getImage().substring(0, dotIndex);
            }
        }

        return variableName;
    }


    private boolean isLocalAttributeAccess(String varName, Scope scope) {
        Scope currentScope = scope;

        while (currentScope != null) {
            for (VariableNameDeclaration decl : currentScope.getDeclarations(VariableNameDeclaration.class).keySet()) {
                if (decl.getImage().equals(varName)) {
                    if (currentScope instanceof ClassScope) {
                        return true;
                    }
                }
            }
            currentScope = currentScope.getParent(); // WARNING doesn't consider inherited fields or static imports
        }

        return false;
    }

}
