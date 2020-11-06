/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl.visitors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorReducedAdapter;
import net.sourceforge.pmd.lang.java.symboltable.ClassScope;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.Scope;


/**
 * Returns the map of method names to the set of local attributes accessed when visiting a class.
 *
 * @author Clément Fournier
 * @since 6.0.0
 * @deprecated Is internal API, will be moved in 7.0.0
 */
@Deprecated
@InternalApi
public class TccAttributeAccessCollector extends JavaParserVisitorReducedAdapter {

    private final ASTAnyTypeDeclaration exploredClass;


    /** The name of the current method. */
    private String currentMethodName;

    private Map<String, Set<String>> methodAttributeAccess;


    public TccAttributeAccessCollector(ASTAnyTypeDeclaration exploredClass) {
        this.exploredClass = exploredClass;
    }


    /**
     * Collects the attribute accesses by method into a map.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Set<String>> start() {
        return (Map<String, Set<String>>) this.visit(exploredClass, new HashMap<String, Set<String>>());
    }


    @Override
    public Object visit(ASTAnyTypeDeclaration node, Object data) {
        if (Objects.equals(node, exploredClass)) {
            methodAttributeAccess = new HashMap<>();
            super.visit(node, data);
        } else if (node instanceof ASTClassOrInterfaceDeclaration
                && ((ASTClassOrInterfaceDeclaration) node).isLocal()) {
            super.visit(node, data);
        }
        return methodAttributeAccess;
    }


    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {

        if (!node.isAbstract()) {
            if (node.getFirstParentOfType(ASTAnyTypeDeclaration.class) == exploredClass) {
                currentMethodName = node.getQualifiedName().getOperation();
                methodAttributeAccess.put(currentMethodName, new HashSet<String>());

                super.visit(node, data);
                currentMethodName = null;
            } else {
                super.visit(node, data);
            }
        }

        return null;
    }


    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        return data; // we're only looking for method pairs
    }


    /**
     * The primary expression node is used to detect access
     * to attributes and method calls. If the access is not for a
     * foreign class, then the {@link #methodAttributeAccess}
     * map is updated for the current method.
     */
    @Override
    public Object visit(ASTPrimaryExpression node, Object data) {
        if (currentMethodName != null) {
            Set<String> methodAccess = methodAttributeAccess.get(currentMethodName);
            String variableName = getVariableName(node);
            if (isLocalAttributeAccess(variableName, node.getScope())) {
                methodAccess.add(variableName);
            }
        }

        return super.visit(node, data);
    }


    private String getVariableName(ASTPrimaryExpression node) {
        ASTPrimaryPrefix prefix = node.getFirstDescendantOfType(ASTPrimaryPrefix.class);

        if (prefix.usesThisModifier()) {
            List<ASTPrimarySuffix> suffixes = node.findChildrenOfType(ASTPrimarySuffix.class);
            if (suffixes.size() > 1) {
                if (!suffixes.get(1).isArguments()) { // not a method call
                    return suffixes.get(0).getImage();
                }
            }
        }

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
                    if (currentScope instanceof ClassScope
                            && ((ClassScope) currentScope).getClassDeclaration().getNode() == exploredClass) {
                        return true;
                    }
                }
            }
            currentScope = currentScope.getParent(); // WARNING doesn't consider inherited fields or static imports
        }

        return false;
    }

}
