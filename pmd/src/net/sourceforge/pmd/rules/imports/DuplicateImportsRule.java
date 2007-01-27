/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.imports;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTImportDeclaration;
import net.sourceforge.pmd.rules.ImportWrapper;

import java.util.HashSet;
import java.util.Set;

public class DuplicateImportsRule extends AbstractRule {

    private Set<ImportWrapper> singleTypeImports;
    private Set<ImportWrapper> importOnDemandImports;

    public Object visit(ASTCompilationUnit node, Object data) {
        singleTypeImports = new HashSet<ImportWrapper>();
        importOnDemandImports = new HashSet<ImportWrapper>();
        super.visit(node, data);

        // this checks for things like:
        // import java.io.*;
        // import java.io.File;
        for (ImportWrapper thisImportOnDemand : importOnDemandImports) {
            for (ImportWrapper thisSingleTypeImport : singleTypeImports) {
                String singleTypePkg = thisSingleTypeImport.getName().substring(0, thisSingleTypeImport.getName().lastIndexOf('.'));
                if (thisImportOnDemand.getName().equals(singleTypePkg)) {
                    addViolation(data, thisSingleTypeImport.getNode(), thisSingleTypeImport.getName());
                }
            }
        }
        singleTypeImports.clear();
        importOnDemandImports.clear();
        return data;
    }

    public Object visit(ASTImportDeclaration node, Object data) {
        ImportWrapper wrapper = new ImportWrapper(node.getImportedName(), node.getImportedName(), node.getImportedNameNode());

        // blahhhh... this really wants to be ASTImportDeclaration to be polymorphic...
        if (node.isImportOnDemand()) {
            if (importOnDemandImports.contains(wrapper)) {
                addViolation(data, node.getImportedNameNode(), node.getImportedNameNode().getImage());
            } else {
                importOnDemandImports.add(wrapper);
            }
        } else {
            if (singleTypeImports.contains(wrapper)) {
                addViolation(data, node.getImportedNameNode(), node.getImportedNameNode().getImage());
            } else {
                singleTypeImports.add(wrapper);
            }
        }
        return data;
    }

}
