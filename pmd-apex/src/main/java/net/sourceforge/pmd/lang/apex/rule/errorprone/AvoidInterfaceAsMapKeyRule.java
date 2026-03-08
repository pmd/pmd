/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.errorprone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.apex.ast.ASTApexFile;
import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclarationStatements;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTParameter;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserInterface;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.multifile.ApexMultifileAnalysis;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.apex.rule.internal.Helper;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

/**
 * Flags use of an interface as Map key when that interface has an implementing class
 * that is abstract and defines equals or hashCode. In Apex, Map operations like
 * containsKey do not dispatch to the correct equals/hashCode in that case.
 *
 * @see <a href="https://github.com/pmd/pmd/issues/6492">Issue 6492</a>
 */
public class AvoidInterfaceAsMapKeyRule extends AbstractApexRule {

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTApexFile.class);
    }

    @Override
    public Object visit(ASTApexFile node, Object data) {
        List<MapKeyUsage> mapKeyUsages = collectMapKeyUsages(node);
        ApexMultifileAnalysis mfa = node.getMultifileAnalysis();

        if (!mfa.isFailed()) {
            // Org is loaded: use cross-file type hierarchy for accurate detection.
            for (MapKeyUsage usage : mapKeyUsages) {
                String keySimpleName = usage.keyTypeSimpleName;
                if (mfa.isInterfaceInOrg(keySimpleName)
                        && mfa.hasAbstractImplementorWithEqualsOrHashCode(keySimpleName)) {
                    asCtx(data).addViolation(usage.reportNode);
                }
            }
        } else {
            // No org available: fall back to single-file analysis.
            Set<String> interfaceSimpleNames = getInterfaceSimpleNamesInFile(node);
            Map<String, ASTUserClass> classesBySimpleName = getClassesBySimpleName(node);

            for (MapKeyUsage usage : mapKeyUsages) {
                String keySimpleName = usage.keyTypeSimpleName;
                if (!interfaceSimpleNames.contains(keySimpleName)) {
                    continue;
                }
                Set<ASTUserClass> implementors = findImplementorsOf(keySimpleName, classesBySimpleName);
                if (hasAbstractImplementorWithEqualsOrHashCode(implementors, classesBySimpleName)) {
                    asCtx(data).addViolation(usage.reportNode);
                }
            }
        }
        return data;
    }

    private List<MapKeyUsage> collectMapKeyUsages(ASTApexFile root) {
        List<MapKeyUsage> usages = new ArrayList<>();

        for (ASTFieldDeclarationStatements field : root.descendants(ASTFieldDeclarationStatements.class).crossFindBoundaries()) {
            String typeName = field.getTypeName();
            if (typeName != null && typeName.startsWith("Map<")) {
                List<String> args = field.getTypeArguments();
                if (args.size() >= 2) {
                    String keyType = args.get(0);
                    usages.add(new MapKeyUsage(Helper.getSimpleTypeName(keyType), field));
                }
            }
        }

        for (ASTParameter param : root.descendants(ASTParameter.class).crossFindBoundaries()) {
            String keyType = Helper.getMapKeyType(param.getType());
            if (keyType != null) {
                usages.add(new MapKeyUsage(Helper.getSimpleTypeName(keyType), param));
            }
        }

        for (ASTVariableDeclaration varDecl : root.descendants(ASTVariableDeclaration.class).crossFindBoundaries()) {
            String keyType = Helper.getMapKeyType(varDecl.getType());
            if (keyType != null) {
                usages.add(new MapKeyUsage(Helper.getSimpleTypeName(keyType), varDecl));
            }
        }

        return usages;
    }

    private Set<String> getInterfaceSimpleNamesInFile(ASTApexFile root) {
        Set<String> names = new HashSet<>();
        for (ASTUserInterface iface : root.descendants(ASTUserInterface.class).crossFindBoundaries()) {
            names.add(iface.getSimpleName());
        }
        return names;
    }

    private Map<String, ASTUserClass> getClassesBySimpleName(ASTApexFile root) {
        Map<String, ASTUserClass> map = new HashMap<>();
        for (ASTUserClass clazz : root.descendants(ASTUserClass.class).crossFindBoundaries()) {
            map.put(clazz.getSimpleName(), clazz);
        }
        return map;
    }

    /**
     * Find all classes in the file that implement the given interface (directly or via superclass).
     */
    private Set<ASTUserClass> findImplementorsOf(String interfaceSimpleName, Map<String, ASTUserClass> classesBySimpleName) {
        Set<ASTUserClass> implementors = new HashSet<>();
        for (ASTUserClass clazz : classesBySimpleName.values()) {
            if (directlyImplementsInterface(clazz, interfaceSimpleName)) {
                implementors.add(clazz);
            }
        }
        // Add classes that extend an implementor (transitive)
        boolean changed;
        do {
            changed = false;
            for (ASTUserClass clazz : classesBySimpleName.values()) {
                if (implementors.contains(clazz)) {
                    continue;
                }
                String superName = clazz.getSuperClassName();
                String superSimple = Helper.getSimpleTypeName(superName);
                ASTUserClass superClass = classesBySimpleName.get(superSimple);
                if (superClass != null && implementors.contains(superClass)) {
                    implementors.add(clazz);
                    changed = true;
                }
            }
        } while (changed);
        return implementors;
    }

    private boolean directlyImplementsInterface(ASTUserClass clazz, String interfaceSimpleName) {
        for (String iface : clazz.getInterfaceNames()) {
            if (Helper.getSimpleTypeName(iface).equals(interfaceSimpleName)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasAbstractImplementorWithEqualsOrHashCode(
            Set<ASTUserClass> implementors,
            Map<String, ASTUserClass> classesBySimpleName) {
        for (ASTUserClass clazz : implementors) {
            if (abstractClassDefinesEqualsOrHashCodeInChain(clazz, classesBySimpleName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Walk up the class chain; if any class in the chain is abstract and defines equals or hashCode, return true.
     */
    private boolean abstractClassDefinesEqualsOrHashCodeInChain(
            ASTUserClass clazz,
            Map<String, ASTUserClass> classesBySimpleName) {
        ASTUserClass current = clazz;
        Set<ASTUserClass> visited = new HashSet<>();
        while (current != null && visited.add(current)) {
            if (current.getModifiers() != null && current.getModifiers().isAbstract() && definesEqualsOrHashCode(current)) {
                return true;
            }
            String superName = current.getSuperClassName();
            if (superName.isEmpty()) {
                break;
            }
            current = classesBySimpleName.get(Helper.getSimpleTypeName(superName));
        }
        return false;
    }

    private boolean definesEqualsOrHashCode(ASTUserClass clazz) {
        for (ASTMethod method : clazz.children(ASTMethod.class)) {
            if (isEquals(method) || isHashCode(method)) {
                return true;
            }
        }
        return false;
    }

    private boolean isEquals(ASTMethod method) {
        if (!"equals".equalsIgnoreCase(method.getImage())) {
            return false;
        }
        int paramCount = 0;
        String paramType = null;
        for (int i = 0; i < method.getNumChildren(); i++) {
            ApexNode<?> child = method.getChild(i);
            if (child instanceof ASTParameter) {
                paramCount++;
                paramType = ((ASTParameter) child).getType();
            }
        }
        return paramCount == 1 && "Object".equalsIgnoreCase(Helper.getSimpleTypeName(paramType != null ? paramType : ""));
    }

    private boolean isHashCode(ASTMethod method) {
        if (!"hashCode".equalsIgnoreCase(method.getImage())) {
            return false;
        }
        int paramCount = 0;
        for (int i = 0; i < method.getNumChildren(); i++) {
            if (method.getChild(i) instanceof ASTParameter) {
                paramCount++;
            }
        }
        return paramCount == 0;
    }

    private static final class MapKeyUsage {
        final String keyTypeSimpleName;
        final ApexNode<?> reportNode;

        MapKeyUsage(String keyTypeSimpleName, ApexNode<?> reportNode) {
            this.keyTypeSimpleName = keyTypeSimpleName;
            this.reportNode = reportNode;
        }
    }
}
