/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.imports;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.SourceFileScope;

public class UnnecessaryFullyQualifiedNameRule extends AbstractJavaRule {

    private List<ASTImportDeclaration> imports = new ArrayList<>();
    private List<ASTImportDeclaration> matches = new ArrayList<>();

    public UnnecessaryFullyQualifiedNameRule() {
        super.addRuleChainVisit(ASTCompilationUnit.class);
        super.addRuleChainVisit(ASTImportDeclaration.class);
        super.addRuleChainVisit(ASTClassOrInterfaceType.class);
        super.addRuleChainVisit(ASTName.class);
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        imports.clear();
        return data;
    }

    @Override
    public Object visit(ASTImportDeclaration node, Object data) {
        imports.add(node);
        return data;
    }

    @Override
    public Object visit(ASTClassOrInterfaceType node, Object data) {
        checkImports(node, data);
        return data;
    }

    @Override
    public Object visit(ASTName node, Object data) {
        if (!(node.jjtGetParent() instanceof ASTImportDeclaration)
                && !(node.jjtGetParent() instanceof ASTPackageDeclaration)) {
            checkImports(node, data);
        }
        return data;
    }

    private void checkImports(JavaNode node, Object data) {
        String name = node.getImage();
        matches.clear();

        // Find all "matching" import declarations
        for (ASTImportDeclaration importDeclaration : imports) {
            if (importDeclaration.isImportOnDemand()) {
                // On demand import exactly matches the package of the type
                if (name.startsWith(importDeclaration.getImportedName())) {
                    if (name.lastIndexOf('.') == importDeclaration.getImportedName().length()) {
                        matches.add(importDeclaration);
                        continue;
                    }
                }
            } else {
                // Exact match of imported class
                if (name.equals(importDeclaration.getImportedName())) {
                    matches.add(importDeclaration);
                    continue;
                }
                // Match of static method call on imported class
                if (name.startsWith(importDeclaration.getImportedName())) {
                    if (name.lastIndexOf('.') == importDeclaration.getImportedName().length()) {
                        matches.add(importDeclaration);
                        continue;
                    }
                }
            }
        }

        // If there is no direct match, consider if we match the tail end of a
        // direct static import, but also a static method on a class import?
        // For example:
        //
        // import java.util.Arrays;
        // import static java.util.Arrays.asList;
        // static {
        // List list1 = Arrays.asList("foo"); // Array class name not needed!
        // List list2 = asList("foo"); // Preferred, used static import
        // }
        if (matches.isEmpty() && name.indexOf('.') >= 0) {
            for (ASTImportDeclaration importDeclaration : imports) {
                if (importDeclaration.isStatic()) {
                    String[] importParts = importDeclaration.getImportedName().split("\\.");
                    String[] nameParts = name.split("\\.");
                    if (importDeclaration.isImportOnDemand()) {
                        // Name class part matches class part of static import?
                        if (nameParts[nameParts.length - 2].equals(importParts[importParts.length - 1])) {
                            matches.add(importDeclaration);
                        }
                    } else {
                        // Last 2 parts match?
                        if (nameParts[nameParts.length - 1].equals(importParts[importParts.length - 1])
                                && nameParts[nameParts.length - 2].equals(importParts[importParts.length - 2])) {
                            matches.add(importDeclaration);
                        }
                    }
                }
            }
        }

        if (!matches.isEmpty()) {
            ASTImportDeclaration firstMatch = matches.get(0);

            // Could this done to avoid a conflict?
            if (!isAvoidingConflict(node, name, firstMatch)) {
                String importStr = firstMatch.getImportedName() + (firstMatch.isImportOnDemand() ? ".*" : "");
                String type = firstMatch.isStatic() ? "static " : "";

                addViolation(data, node, new Object[] { node.getImage(), importStr, type });
            }
        }

        matches.clear();
    }

    private boolean isAvoidingConflict(final JavaNode node, final String name,
            final ASTImportDeclaration firstMatch) {
        // is it a conflict between different imports?
        if (firstMatch.isImportOnDemand() && firstMatch.isStatic() && name.indexOf('.') != -1) {
            final String methodCalled = name.substring(name.indexOf('.') + 1);

            // Is there any other static import conflictive?
            for (final ASTImportDeclaration importDeclaration : imports) {
                if (importDeclaration != firstMatch && importDeclaration.isStatic()) {
                    if (importDeclaration.getImportedName().startsWith(firstMatch.getImportedName())
                            && importDeclaration.getImportedName().lastIndexOf('.') == firstMatch.getImportedName()
                                    .length()) {
                        // A conflict against the same class is not an excuse,
                        // ie:
                        // import java.util.Arrays;
                        // import static java.util.Arrays.asList;
                        continue;
                    }

                    if (importDeclaration.isImportOnDemand()) {
                        // We need type resolution to make sure there is a
                        // conflicting method
                        if (importDeclaration.getType() != null) {
                            for (final Method m : importDeclaration.getType().getMethods()) {
                                if (m.getName().equals(methodCalled)) {
                                    return true;
                                }
                            }
                        }
                    } else if (importDeclaration.getImportedName().endsWith(methodCalled)) {
                        return true;
                    }
                }
            }
        }

        // Is it a conflict with a class in the same file?
        final String unqualifiedName = name.substring(name.lastIndexOf('.') + 1);
        final int unqualifiedNameLength = unqualifiedName.length();
        final Set<String> qualifiedTypes = node.getScope().getEnclosingScope(SourceFileScope.class)
                .getQualifiedTypeNames().keySet();
        for (final String qualified : qualifiedTypes) {
            int fullLength = qualified.length();
            if (qualified.endsWith(unqualifiedName)
                    && (fullLength == unqualifiedNameLength || qualified.charAt(fullLength - unqualifiedNameLength - 1) == '.')) {
                return true;
            }
        }

        return false;
    }
}
