/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTImportDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DuplicateImportsRule extends AbstractRule {

    private Set singleTypeImports;
    private Set importOnDemandImports;

    public Object visit(ASTCompilationUnit node, Object data) {
        singleTypeImports = new HashSet();
        importOnDemandImports = new HashSet();
        super.visit(node, data);

        // this checks for things like:
        // import java.io.*;
        // import java.io.File;
        for (Iterator i = importOnDemandImports.iterator(); i.hasNext();) {
            ImportWrapper thisImportOnDemand = (ImportWrapper) i.next();
            for (Iterator j = singleTypeImports.iterator(); j.hasNext();) {
                ImportWrapper thisSingleTypeImport = (ImportWrapper) j.next();
                String singleTypePkg = thisSingleTypeImport.getName().substring(0, thisSingleTypeImport.getName().lastIndexOf("."));
                if (thisImportOnDemand.getName().equals(singleTypePkg)) {
                    addViolation((RuleContext) data, thisSingleTypeImport.getNode(), thisSingleTypeImport.getName());
                }
            }
        }
        singleTypeImports.clear();
        importOnDemandImports.clear();
        return data;
    }

    public Object visit(ASTImportDeclaration node, Object data) {
        ImportWrapper wrapper = new ImportWrapper(node.getImportedNameNode().getImage(), node.getImportedNameNode().getImage(), node.getImportedNameNode());

        // blahhhh... this really wants to be ASTImportDeclaration to be polymorphic...
        if (node.isImportOnDemand()) {
            if (importOnDemandImports.contains(wrapper)) {
                addViolation((RuleContext) data, node.getImportedNameNode(), node.getImportedNameNode().getImage());
            } else {
                importOnDemandImports.add(wrapper);
            }
        } else {
            if (singleTypeImports.contains(wrapper)) {
                addViolation((RuleContext) data, node.getImportedNameNode(), node.getImportedNameNode().getImage());
            } else {
                singleTypeImports.add(wrapper);
            }
        }
        return data;
    }

}
