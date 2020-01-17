/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTNameList;
import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.SourceFileScope;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.symboltable.Scope;

public class UnnecessaryFullyQualifiedNameRule extends AbstractJavaRule {

    private List<ASTImportDeclaration> imports = new ArrayList<>();
    private String currentPackage;

    public UnnecessaryFullyQualifiedNameRule() {
        super.addRuleChainVisit(ASTPackageDeclaration.class);
        super.addRuleChainVisit(ASTImportDeclaration.class);
        super.addRuleChainVisit(ASTClassOrInterfaceType.class);
        super.addRuleChainVisit(ASTName.class);
    }

    @Override
    public void start(final RuleContext ctx) {
        imports.clear();
        currentPackage = null;
    }

    @Override
    public Object visit(ASTPackageDeclaration node, Object data) {
        currentPackage = node.getPackageNameImage();
        return data;
    }

    @Override
    public Object visit(ASTImportDeclaration node, Object data) {
        imports.add(node);
        return data;
    }

    @Override
    public Object visit(ASTClassOrInterfaceType node, Object data) {
        // This name has no qualification, it can't be unnecessarily qualified
        if (node.getImage().indexOf('.') < 0) {
            return data;
        }
        checkImports(node, data);
        return data;
    }

    @Override
    public Object visit(ASTName node, Object data) {
        if (!(node.getParent() instanceof ASTImportDeclaration)
                && !(node.getParent() instanceof ASTPackageDeclaration)) {
            // This name has no qualification, it can't be unnecessarily qualified
            if (node.getImage().indexOf('.') < 0) {
                return data;
            }
            checkImports(node, data);
        }
        return data;
    }


    /**
     * Returns true if the name could be imported by this declaration.
     * The name must be fully qualified, the import is either on-demand
     * or static, that is its {@link ASTImportDeclaration#getImportedName()}
     * is the enclosing package or type name of the imported type or static member.
     */
    private boolean declarationMatches(ASTImportDeclaration decl, String name) {
        return name.startsWith(decl.getImportedName())
                && name.lastIndexOf('.') == decl.getImportedName().length();
    }

    private boolean couldBeMethodCall(JavaNode node) {
        if (node.getNthParent(2) instanceof ASTPrimaryExpression && node.getNthParent(1) instanceof ASTPrimaryPrefix) {
            int nextSibling = node.getParent().getIndexInParent() + 1;
            if (node.getNthParent(2).getNumChildren() > nextSibling) {
                return node.getNthParent(2).getChild(nextSibling) instanceof ASTPrimarySuffix;
            }
        }
        return false;
    }

    private void checkImports(TypeNode node, Object data) {
        final String name = node.getImage();

        // variable names shadow everything else
        // If the first segment is a variable, then all
        // the following are field accesses and it's not an FQCN
        if (isVariable(node.getScope(), name)) {
            return;
        }

        List<ASTImportDeclaration> matches = new ArrayList<>();

        // Find all "matching" import declarations
        for (ASTImportDeclaration importDeclaration : imports) {
            if (!importDeclaration.isImportOnDemand()) {
                // Exact match of imported class
                if (name.equals(importDeclaration.getImportedName())) {
                    matches.add(importDeclaration);
                    continue;
                }
            }
            // On demand import exactly matches the package of the type
            // Or match of static method call on imported class
            if (declarationMatches(importDeclaration, name)) {
                matches.add(importDeclaration);
            }
        }

        // If there is no direct match, consider if we match the tail end of a
        // direct static import, but also a static method on a class import.
        // For example:
        //
        // import java.util.Arrays;
        // import static java.util.Arrays.asList;
        // static {
        // List list1 = Arrays.asList("foo"); // Array class name not needed!
        // List list2 = asList("foo"); // Preferred, used static import
        // }
        //
        // Or: The usage of a FQN is correct, if there is another import with the same class.
        // Example
        // import foo.String;
        // static {
        // java.lang.String s = "a";
        // }
        if (matches.isEmpty()) {
            for (ASTImportDeclaration importDeclaration : imports) {
                String[] importParts = importDeclaration.getImportedName().split("\\.");
                String[] nameParts = name.split("\\.");
                if (importDeclaration.isStatic()) {
                    if (importDeclaration.isImportOnDemand()) {
                        // Name class part matches class part of static import?
                        if (nameParts[nameParts.length - 2].equals(importParts[importParts.length - 1])) {
                            matches.add(importDeclaration);
                        }
                    } else {
                        // Last 2 parts match? Class + Method name
                        if (nameParts[nameParts.length - 1].equals(importParts[importParts.length - 1])
                                && nameParts[nameParts.length - 2].equals(importParts[importParts.length - 2])) {
                            matches.add(importDeclaration);
                        }
                    }
                } else if (!importDeclaration.isImportOnDemand()) {
                    // last part matches?
                    if (nameParts[nameParts.length - 1].equals(importParts[importParts.length - 1])) {
                        matches.add(importDeclaration);
                    } else if (couldBeMethodCall(node)
                            && nameParts.length > 1 && nameParts[nameParts.length - 2].equals(importParts[importParts.length - 1])) {
                        // maybe the Name is part of a method call, then the second two last part needs to match
                        matches.add(importDeclaration);
                    }
                }
            }
        }

        if (matches.isEmpty()) {
            if (isJavaLangImplicit(node)) {
                addViolation(data, node, new Object[] { node.getImage(), "java.lang.*", "implicit "});
            } else if (isSamePackage(node, name)) {
                addViolation(data, node, new Object[] { node.getImage(), currentPackage + ".*", "same package "});
            }
        } else {
            ASTImportDeclaration firstMatch = findFirstMatch(matches);

            if (!isReferencingInnerNonStaticClass(name, firstMatch)
                    && !isAvoidingConflict(node, name, firstMatch)) {

                String importStr = firstMatch.getImportedName() + (firstMatch.isImportOnDemand() ? ".*" : "");
                String type = firstMatch.isStatic() ? "static " : "";

                addViolation(data, node, new Object[] { node.getImage(), importStr, type });
            }
        }
    }

    private ASTImportDeclaration findFirstMatch(List<ASTImportDeclaration> imports) {
        // first search only static imports
        ASTImportDeclaration result = null;
        for (ASTImportDeclaration importDeclaration : imports) {
            if (importDeclaration.isStatic()) {
                result = importDeclaration;
                break;
            }
        }

        // then search all non-static, if needed
        if (result == null) {
            for (ASTImportDeclaration importDeclaration : imports) {
                if (!importDeclaration.isStatic()) {
                    result = importDeclaration;
                    break;
                }
            }
        }

        return result;
    }

    private boolean isVariable(Scope scope, String name) {
        String firstSegment = name.substring(0, name.indexOf('.'));

        while (scope != null) {

            for (Entry<VariableNameDeclaration, List<NameOccurrence>> entry : scope.getDeclarations(VariableNameDeclaration.class).entrySet()) {
                if (entry.getKey().getName().equals(firstSegment)) {
                    return true;
                }
            }

            scope = scope.getParent();
        }

        return false;
    }

    private boolean isSamePackage(TypeNode node, String name) {
        if (node.getType() != null) {
            // with type resolution we can do an exact package match
            Package packageOfType = node.getType().getPackage();
            if (packageOfType != null) {

                // get "package" candidate from name
                int i = name.lastIndexOf('.');
                if (i > 0) {
                    name = name.substring(0, i);
                }

                return node.getType().getPackage().getName().equals(currentPackage)
                        && name.equals(currentPackage);
            }
        }

        int i = name.lastIndexOf('.');
        if (i > 0) {
            name = name.substring(0, i);
            if (name.equals(currentPackage)) {
                return true;
            }
        }
        // if it is a name used inside a primary prefix, then it is ambiguous, whether it references
        // a type or a field or a type inside a subpackage
        // we assume here, it won't be a subpackage the name references a field, e.g.
        // package a;
        // name: a.b.c.d(); -> we assume, b is a class, c is a field, d is a method.
        // but it could very well be, that: a.b is a package and c is a class, d is a (static) method.
        if (node.getParent() instanceof ASTPrimaryPrefix
                || node.getParent() instanceof ASTNameList
                || node instanceof ASTClassOrInterfaceType) {
            return currentPackage != null && name.startsWith(currentPackage);
        }

        return false;
    }

    private boolean isJavaLangImplicit(TypeNode node) {
        String name = node.getImage();
        boolean isJavaLang = name != null && name.startsWith("java.lang.");

        if (isJavaLang && node.getType() != null && node.getType().getPackage() != null) {
            // valid would be ProcessBuilder.Redirect.PIPE but not java.lang.ProcessBuilder.Redirect.PIPE
            String packageName = node.getType().getPackage() // package might be null, if type is an array type...
                    .getName();
            return "java.lang".equals(packageName);
        } else if (isJavaLang) {
            // only java.lang.* is implicitly imported, but not e.g. java.lang.reflection.*
            return StringUtils.countMatches(name, '.') == 2;
        }
        return false;
    }

    private boolean isReferencingInnerNonStaticClass(final String name, final ASTImportDeclaration firstMatch) {
        if (firstMatch.isImportOnDemand() && firstMatch.isStatic() && firstMatch.getType() != null) {
            String[] nameParts = name.split("\\.");
            String[] importParts = firstMatch.getImportedName().split("\\.");

            if (nameParts.length == 2 && importParts[importParts.length - 1].equals(nameParts[0])) {
                Class<?>[] declaredClasses = firstMatch.getType().getDeclaredClasses();
                for (Class<?> innerClass : declaredClasses) {
                    if (nameParts[1].equals(innerClass.getSimpleName()) && (innerClass.getModifiers() & Modifier.STATIC) != Modifier.STATIC) {
                        // the referenced inner class is not static, therefore the static import on demand doesn't match
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isAvoidingConflict(final TypeNode node, final String name,
                                       final ASTImportDeclaration firstMatch) {
        // is it a conflict between different imports?
        if (firstMatch.isImportOnDemand() && firstMatch.isStatic()) {
            final String methodCalled = name.substring(name.indexOf('.') + 1);

            // Is there any other static import conflictive?
            for (final ASTImportDeclaration importDeclaration : imports) {
                if (!Objects.equals(importDeclaration, firstMatch) && importDeclaration.isStatic()) {
                    if (declarationMatches(firstMatch, importDeclaration.getImportedName())) {
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

        final String unqualifiedName = name.substring(name.lastIndexOf('.') + 1);
        final int unqualifiedNameLength = unqualifiedName.length();

        // There could be a conflict between an import on demand and another import, e.g.
        // import One.*;
        // import Two.Problem;
        // Where One.Problem is a legitimate qualification
        if (firstMatch.isImportOnDemand() && !firstMatch.isStatic()) {
            for (ASTImportDeclaration importDeclaration : imports) {
                if (importDeclaration != firstMatch     // NOPMD
                        && !importDeclaration.isStatic()
                        && !importDeclaration.isImportOnDemand()) {

                    // Duplicate imports are legal
                    if (!importDeclaration.getPackageName().equals(firstMatch.getPackageName())
                            && importDeclaration.getImportedSimpleName().equals(unqualifiedName)) {
                        return true;
                    }
                }
            }
        }

        // There could be a conflict between an import of a class with the same name as the FQN
        String importName = firstMatch.getImportedName();
        String importUnqualified = importName.substring(importName.lastIndexOf('.') + 1);
        if (!firstMatch.isImportOnDemand() && !firstMatch.isStatic()) {
            // the package is different, but the unqualified name is same
            if (!firstMatch.getImportedName().equals(name) && importUnqualified.equals(unqualifiedName)) {
                return true;
            }
        }

        // There could be a conflict between an import of a class with the same name as the FQN, which
        // could be a method call:
        // import x.y.Thread;
        // valid qualification (node): java.util.Thread.currentThread()
        if (couldBeMethodCall(node)) {
            String[] nameParts = name.split("\\.");
            String fqnName = name.substring(0, name.lastIndexOf('.'));
            // seems to be a static method call on a different FQN
            if (!fqnName.equals(importName) && !firstMatch.isStatic() && !firstMatch.isImportOnDemand()
                    && nameParts.length > 1 && nameParts[nameParts.length - 2].equals(importUnqualified)) {
                return true;
            }
        }

        // Is it a conflict with a class in the same file?
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
