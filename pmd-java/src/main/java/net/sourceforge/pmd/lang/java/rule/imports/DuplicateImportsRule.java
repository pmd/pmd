/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.imports;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.rule.ImportWrapper;

public class DuplicateImportsRule extends AbstractJavaRule {

    private Set<ImportWrapper> singleTypeImports;
    private Set<ImportWrapper> importOnDemandImports;

    public Object visit(ASTCompilationUnit node, Object data) {
        singleTypeImports = new HashSet<>();
        importOnDemandImports = new HashSet<>();
        super.visit(node, data);

        // this checks for things like:
        // import java.io.*;
        // import java.io.File;
        for (ImportWrapper thisImportOnDemand : importOnDemandImports) {
            for (ImportWrapper thisSingleTypeImport : singleTypeImports) {
                String singleTypeFullName = thisSingleTypeImport.getName(); // java.io.File

                int lastDot = singleTypeFullName.lastIndexOf('.');
                String singleTypePkg = singleTypeFullName.substring(0, lastDot); // java.io
                String singleTypeName = singleTypeFullName.substring(lastDot + 1); // File

                if (thisImportOnDemand.getName().equals(singleTypePkg)
                        && !isDisambiguationImport(node, singleTypePkg, singleTypeName)) {
                    addViolation(data, thisSingleTypeImport.getNode(), singleTypeFullName);
                }
            }
        }
        singleTypeImports.clear();
        importOnDemandImports.clear();
        return data;
    }

    /**
     * Check whether this seemingly duplicate import is actually a
     * disambiguation import.
     * 
     * Example: import java.awt.*; import java.util.*; import java.util.List;
     * //Needed because java.awt.List exists
     */
    private boolean isDisambiguationImport(ASTCompilationUnit node, String singleTypePkg, String singleTypeName) {
        // Loop over .* imports
        for (ImportWrapper thisImportOnDemand : importOnDemandImports) {
            // Skip same package
            if (!thisImportOnDemand.getName().equals(singleTypePkg)) {
                if (!thisImportOnDemand.isStaticOnDemand()) {
                    String fullyQualifiedClassName = thisImportOnDemand.getName() + "." + singleTypeName;
                    if (node.getClassTypeResolver().classNameExists(fullyQualifiedClassName)) {
                        // Class exists in another imported package
                        return true;
                    }
                } else {
                    Class<?> importClass = node.getClassTypeResolver().loadClass(thisImportOnDemand.getName());
                    if (importClass != null) {
                        for (Method m : importClass.getMethods()) {
                            if (Modifier.isStatic(m.getModifiers()) && m.getName().equals(singleTypeName)) {
                                // static method in another imported class
                                return true;
                            }
                        }
                    }
                }
            }
        }

        String fullyQualifiedClassName = "java.lang." + singleTypeName;
        if (node.getClassTypeResolver().classNameExists(fullyQualifiedClassName)) {
            return true; // Class exists in another imported package
        }

        return false; // This really is a duplicate import
    }

    public Object visit(ASTImportDeclaration node, Object data) {
        ImportWrapper wrapper = new ImportWrapper(node.getImportedName(), node.getImportedName(),
                node.getImportedNameNode(), node.isStatic() && node.isImportOnDemand());

        // blahhhh... this really wants to be ASTImportDeclaration to be
        // polymorphic...
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
