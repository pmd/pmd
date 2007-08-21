package net.sourceforge.pmd.rules.imports;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTImportDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class DontImportJavaLang extends AbstractRule {
    
    private Set<Package> skipPackages = new HashSet<Package>(Arrays.asList(new Package[] { Package.getPackage("java.lang.ref"), Package.getPackage("java.lang.reflect"), Package.getPackage("java.lang.annotation"),
            Package.getPackage("java.lang.instrument"), Package.getPackage("java.lang.management"), Package.getPackage("java.lang.Thread") }));

    private Package javaLangPackage = Package.getPackage("java.lang");

    public Object visit(ASTImportDeclaration node, Object data) {
        
        if (node.isStatic()) {
            return data;
        }

        if (node.getPackage() != null) {
            if(node.getPackage().equals(javaLangPackage) && !skipPackages.contains(node.getPackage())){
                addViolation(data, node);
            }
        } else {
            String img = ((SimpleNode) node.jjtGetChild(0)).getImage();
            if (img.startsWith("java.lang")) {
                if (img.startsWith("java.lang.ref")
                        || img.startsWith("java.lang.reflect")
                        || img.startsWith("java.lang.annotation")
                        || img.startsWith("java.lang.instrument")
                        || img.startsWith("java.lang.management")
                        || img.startsWith("java.lang.Thread.")) {
                    return data;
                }
                addViolation(data, node);
            }
        }
        return data;
    }
}
