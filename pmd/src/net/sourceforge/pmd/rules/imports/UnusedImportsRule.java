/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.imports;

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.AbstractJavaRule;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTImportDeclaration;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.DummyJavaNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.rules.ImportWrapper;

public class UnusedImportsRule extends AbstractJavaRule {

    protected Set<ImportWrapper> imports = new HashSet<ImportWrapper>();

    public Object visit(ASTCompilationUnit node, Object data) {
        imports.clear();
        super.visit(node, data);
        for (ImportWrapper wrapper : imports) {
            addViolation(data, wrapper.getNode(), wrapper.getFullName());
        }
        return data;
    }

    public Object visit(ASTImportDeclaration node, Object data) {
        if (!node.isImportOnDemand()) {
            ASTName importedType = (ASTName) node.jjtGetChild(0);
            String className;
            if (isQualifiedName(importedType)) {
                int lastDot = importedType.getImage().lastIndexOf('.') + 1;
                className = importedType.getImage().substring(lastDot);
            } else {
                className = importedType.getImage();
            }
            imports.add(new ImportWrapper(importedType.getImage(), className, node));
        }

        return data;
    }

    public Object visit(ASTClassOrInterfaceType node, Object data) {
        check(node);
        return super.visit(node, data);
    }

    public Object visit(ASTName node, Object data) {
        check(node);
        return data;
    }

    protected void check(Node node) {
        if (imports.isEmpty()) {
            return;
        }
        ImportWrapper candidate = getImportWrapper(node);
        if (imports.contains(candidate)) {
            imports.remove(candidate);
        }
    }

    protected ImportWrapper getImportWrapper(Node node) {
        String name;
        if (!isQualifiedName(node)) {
            name = node.getImage();
        } else {
            name = node.getImage().substring(0, node.getImage().indexOf('.'));
        }
        ImportWrapper candidate = new ImportWrapper(node.getImage(), name, new DummyJavaNode(-1));
        return candidate;
    }
}
