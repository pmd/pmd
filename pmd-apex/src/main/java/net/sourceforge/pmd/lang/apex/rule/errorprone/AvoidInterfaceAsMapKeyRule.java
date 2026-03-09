/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.errorprone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.lang.apex.ast.ASTApexFile;
import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclarationStatements;
import net.sourceforge.pmd.lang.apex.ast.ASTParameter;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.multifile.ApexMultifileAnalysis;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.apex.rule.internal.Helper;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

import com.nawforce.apexlink.api.MethodSummary;
import com.nawforce.apexlink.api.ParameterSummary;
import com.nawforce.apexlink.api.TypeSummary;
import com.nawforce.pkgforce.modifiers.Modifier;
import com.nawforce.pkgforce.names.TypeName;

/**
 * Flags use of an interface as Map key when that interface has an implementing class
 * that is abstract and defines equals or hashCode. In Apex, Map operations like
 * containsKey do not dispatch to the correct equals/hashCode in that case.
 *
 * <p>Requires multifile analysis: set the {@code PMD_APEX_ROOT_DIRECTORY} environment variable
 * to the root of your SFDX project (where {@code sfdx-project.json} lives). Without it the
 * rule produces no violations.
 *
 * @see <a href="https://github.com/pmd/pmd/issues/6492">Issue 6492</a>
 */
public class AvoidInterfaceAsMapKeyRule extends AbstractApexRule {

    private static final Logger LOG = LoggerFactory.getLogger(AvoidInterfaceAsMapKeyRule.class);
    private static boolean warnedAboutMissingOrg = false;

    // Cached index - computed once per PMD run, reused across all file visits
    private TypeHierarchyIndex cachedIndex;

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTApexFile.class);
    }

    @Override
    public Object visit(ASTApexFile node, Object data) {
        ApexMultifileAnalysis mfa = node.getMultifileAnalysis();
        if (mfa.isFailed()) {
            if (!warnedAboutMissingOrg) {
                warnedAboutMissingOrg = true;
                LOG.warn("AvoidInterfaceAsMapKey rule not enforced: multifile analysis unavailable. "
                        + "Set PMD_APEX_ROOT_DIRECTORY to enable cross-file type resolution.");
            }
            return data;
        }

        // Build index once per run (lazily on first file visit)
        TypeHierarchyIndex index = getOrCreateIndex(mfa);
        for (MapKeyUsage usage : collectMapKeyUsages(node)) {
            if (index.isProblematicInterfaceKey(usage.keyTypeName)) {
                asCtx(data).addViolation(usage.reportNode);
            }
        }
        return data;
    }

    private TypeHierarchyIndex getOrCreateIndex(ApexMultifileAnalysis mfa) {
        if (cachedIndex == null) {
            cachedIndex = new TypeHierarchyIndex(mfa.getTypeSummaries());
        }
        return cachedIndex;
    }

    private List<MapKeyUsage> collectMapKeyUsages(ASTApexFile root) {
        List<MapKeyUsage> usages = new ArrayList<>();

        for (ASTFieldDeclarationStatements field : root.descendants(ASTFieldDeclarationStatements.class).crossFindBoundaries()) {
            String typeName = field.getTypeName();
            if (typeName != null && typeName.startsWith("Map<")) {
                List<String> args = field.getTypeArguments();
                if (args.size() >= 2) {
                    usages.add(new MapKeyUsage(args.get(0), field));
                }
            }
        }

        for (ASTParameter param : root.descendants(ASTParameter.class).crossFindBoundaries()) {
            String keyType = Helper.getMapKeyType(param.getType());
            if (keyType != null) {
                usages.add(new MapKeyUsage(keyType, param));
            }
        }

        for (ASTVariableDeclaration varDecl : root.descendants(ASTVariableDeclaration.class).crossFindBoundaries()) {
            String keyType = Helper.getMapKeyType(varDecl.getType());
            if (keyType != null) {
                usages.add(new MapKeyUsage(keyType, varDecl));
            }
        }

        return usages;
    }

    private static final class MapKeyUsage {
        final String keyTypeName;  // as written in source (may be simple or qualified)
        final ApexNode<?> reportNode;

        MapKeyUsage(String keyTypeName, ApexNode<?> reportNode) {
            this.keyTypeName = keyTypeName;
            this.reportNode = reportNode;
        }
    }

    /**
     * Pre-indexes the type hierarchy to enable efficient queries.
     * Handles:
     * - Direct and transitive interface implementation (via superclass chain)
     * - Interface inheritance (I extends J)
     * - Abstract classes anywhere in the hierarchy with equals/hashCode
     *
     * <p>ApexLink's TypeSummary API provides "what this type extends/implements" (outgoing),
     * but we need "what extends/implements this type" (incoming). This index inverts
     * those relationships for efficient lookup.
     */
    private static final class TypeHierarchyIndex {
        // TypeSummary lookup by simple name (lowercase) - for resolving type references
        private final Map<String, Set<TypeSummary>> typesBySimpleName = new HashMap<>();
        // TypeSummary lookup by full name (lowercase) - for qualified references
        private final Map<String, TypeSummary> typesByFullName = new HashMap<>();
        // Class name (lowercase) → set of direct subclasses
        private final Map<String, Set<TypeSummary>> classToSubclasses = new HashMap<>();
        // Interface name (lowercase) → set of interfaces that extend it
        private final Map<String, Set<TypeSummary>> interfaceToSubInterfaces = new HashMap<>();
        // Set of interfaces known to be problematic (pre-computed)
        private final Set<String> problematicInterfaces = new HashSet<>();

        TypeHierarchyIndex(List<TypeSummary> allTypes) {
            // Pass 1: Index all types by name and build direct relationships
            Map<String, TypeSummary> interfaces = new HashMap<>();
            Map<String, TypeSummary> classes = new HashMap<>();
            Set<TypeSummary> abstractClasses = new HashSet<>();
            Set<TypeSummary> typesWithEqualsOrHashCode = new HashSet<>();

            for (TypeSummary summary : allTypes) {
                String nature = summary.nature();
                String simpleName = summary.typeName().name().value().toLowerCase(Locale.ROOT);
                String fullName = getFullTypeName(summary).toLowerCase(Locale.ROOT);

                typesBySimpleName.computeIfAbsent(simpleName, k -> new HashSet<>()).add(summary);
                typesByFullName.put(fullName, summary);

                if ("interface".equalsIgnoreCase(nature)) {
                    interfaces.put(fullName, summary);
                    // Track interface inheritance
                    scala.collection.Iterator<TypeName> superIfaces = summary.interfaces().iterator();
                    while (superIfaces.hasNext()) {
                        String superIfaceName = superIfaces.next().name().value().toLowerCase(Locale.ROOT);
                        interfaceToSubInterfaces.computeIfAbsent(superIfaceName, k -> new HashSet<>())
                                .add(summary);
                    }
                } else if ("class".equalsIgnoreCase(nature)) {
                    classes.put(fullName, summary);
                    // Index by superclass for subclass lookup
                    scala.Option<TypeName> superOpt = summary.superClass();
                    if (superOpt.isDefined()) {
                        String superName = superOpt.get().name().value().toLowerCase(Locale.ROOT);
                        classToSubclasses.computeIfAbsent(superName, k -> new HashSet<>()).add(summary);
                    }
                    if (isAbstract(summary)) {
                        abstractClasses.add(summary);
                    }
                    if (definesEqualsOrHashCode(summary)) {
                        typesWithEqualsOrHashCode.add(summary);
                    }
                }
            }

            // Pass 2: For each interface, check if it's problematic
            // An interface is problematic if there exists an abstract class in any
            // implementation path that has equals/hashCode defined in its subtree
            for (TypeSummary iface : interfaces.values()) {
                if (isInterfaceProblematic(iface, abstractClasses, typesWithEqualsOrHashCode, new HashSet<>())) {
                    // Mark this interface and all its simple name variants as problematic
                    String simpleName = iface.typeName().name().value().toLowerCase(Locale.ROOT);
                    String fullName = getFullTypeName(iface).toLowerCase(Locale.ROOT);
                    problematicInterfaces.add(simpleName);
                    problematicInterfaces.add(fullName);
                }
            }
        }

        /**
         * Checks if using this type name as a Map key is problematic.
         * Accepts both simple names (IKey) and qualified names (OuterClass.IKey).
         */
        boolean isProblematicInterfaceKey(String typeName) {
            String key = typeName.toLowerCase(Locale.ROOT);
            // Try exact match first (handles qualified names)
            if (problematicInterfaces.contains(key)) {
                return true;
            }
            // Try simple name (last component)
            String simpleName = getSimpleName(key);
            return problematicInterfaces.contains(simpleName);
        }

        /**
         * Checks if an interface is problematic by examining all classes that
         * implement it (directly or transitively) for abstract classes with equals/hashCode.
         */
        private boolean isInterfaceProblematic(TypeSummary iface,
                                               Set<TypeSummary> abstractClasses,
                                               Set<TypeSummary> typesWithEqualsOrHashCode,
                                               Set<String> visited) {
            String ifaceName = iface.typeName().name().value().toLowerCase(Locale.ROOT);
            if (!visited.add(ifaceName)) {
                return false; // Already checked, avoid cycles
            }

            // Find all classes and check their hierarchies
            for (Set<TypeSummary> types : typesBySimpleName.values()) {
                for (TypeSummary type : types) {
                    if (!"class".equalsIgnoreCase(type.nature())) {
                        continue;
                    }
                    if (classImplementsInterface(type, ifaceName, new HashSet<>())) {
                        // Check if there's an abstract class in this type's hierarchy with equals/hashCode
                        if (hasProblematicAbstractInHierarchy(type, abstractClasses, typesWithEqualsOrHashCode)) {
                            return true;
                        }
                    }
                }
            }

            // Check sub-interfaces (interface inheritance)
            Set<TypeSummary> subInterfaces = interfaceToSubInterfaces.get(ifaceName);
            if (subInterfaces != null) {
                for (TypeSummary subIface : subInterfaces) {
                    if (isInterfaceProblematic(subIface, abstractClasses, typesWithEqualsOrHashCode, visited)) {
                        return true;
                    }
                }
            }

            return false;
        }

        /**
         * Checks if a class implements an interface (directly or via superclass).
         */
        private boolean classImplementsInterface(TypeSummary cls, String ifaceName, Set<String> visited) {
            String clsName = cls.typeName().name().value().toLowerCase(Locale.ROOT);
            if (!visited.add(clsName)) {
                return false; // Avoid cycles
            }

            // Check direct interfaces
            scala.collection.Iterator<TypeName> ifaces = cls.interfaces().iterator();
            while (ifaces.hasNext()) {
                String directIface = ifaces.next().name().value().toLowerCase(Locale.ROOT);
                if (directIface.equals(ifaceName)) {
                    return true;
                }
                // Check if directIface extends ifaceName
                Set<TypeSummary> directIfaceTypes = typesBySimpleName.get(directIface);
                if (directIfaceTypes != null) {
                    for (TypeSummary directIfaceType : directIfaceTypes) {
                        if ("interface".equalsIgnoreCase(directIfaceType.nature())
                                && interfaceExtendsInterface(directIfaceType, ifaceName, new HashSet<>())) {
                            return true;
                        }
                    }
                }
            }

            // Check superclass
            scala.Option<TypeName> superOpt = cls.superClass();
            if (superOpt.isDefined()) {
                String superName = superOpt.get().name().value().toLowerCase(Locale.ROOT);
                Set<TypeSummary> superTypes = typesBySimpleName.get(superName);
                if (superTypes != null) {
                    for (TypeSummary superType : superTypes) {
                        if ("class".equalsIgnoreCase(superType.nature())
                                && classImplementsInterface(superType, ifaceName, visited)) {
                            return true;
                        }
                    }
                }
            }

            return false;
        }

        /**
         * Checks if an interface extends another interface (directly or transitively).
         */
        private boolean interfaceExtendsInterface(TypeSummary iface, String targetIfaceName, Set<String> visited) {
            String ifaceName = iface.typeName().name().value().toLowerCase(Locale.ROOT);
            if (!visited.add(ifaceName)) {
                return false;
            }

            scala.collection.Iterator<TypeName> superIfaces = iface.interfaces().iterator();
            while (superIfaces.hasNext()) {
                String superIfaceName = superIfaces.next().name().value().toLowerCase(Locale.ROOT);
                if (superIfaceName.equals(targetIfaceName)) {
                    return true;
                }
                Set<TypeSummary> superIfaceTypes = typesBySimpleName.get(superIfaceName);
                if (superIfaceTypes != null) {
                    for (TypeSummary superIfaceType : superIfaceTypes) {
                        if ("interface".equalsIgnoreCase(superIfaceType.nature())
                                && interfaceExtendsInterface(superIfaceType, targetIfaceName, visited)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        /**
         * Checks if there's an abstract class in this class's superclass chain
         * that has equals/hashCode defined in its subtree.
         */
        private boolean hasProblematicAbstractInHierarchy(TypeSummary cls,
                                                          Set<TypeSummary> abstractClasses,
                                                          Set<TypeSummary> typesWithEqualsOrHashCode) {
            // Walk up the class hierarchy
            TypeSummary current = cls;
            Set<String> visited = new HashSet<>();

            while (current != null) {
                String currentName = current.typeName().name().value().toLowerCase(Locale.ROOT);
                if (!visited.add(currentName)) {
                    break; // Cycle detection
                }

                if (abstractClasses.contains(current)) {
                    // Found an abstract class - check if it or any descendant has equals/hashCode
                    if (typeOrDescendantHasEqualsOrHashCode(current, typesWithEqualsOrHashCode, new HashSet<>())) {
                        return true;
                    }
                }

                // Move to superclass
                scala.Option<TypeName> superOpt = current.superClass();
                if (superOpt.isDefined()) {
                    String superName = superOpt.get().name().value().toLowerCase(Locale.ROOT);
                    Set<TypeSummary> superTypes = typesBySimpleName.get(superName);
                    current = superTypes != null && !superTypes.isEmpty() ? superTypes.iterator().next() : null;
                } else {
                    current = null;
                }
            }
            return false;
        }

        /**
         * Recursively checks if this type or any descendant defines equals/hashCode.
         */
        private boolean typeOrDescendantHasEqualsOrHashCode(TypeSummary type,
                                                            Set<TypeSummary> typesWithEqualsOrHashCode,
                                                            Set<String> visited) {
            String typeName = type.typeName().name().value().toLowerCase(Locale.ROOT);
            if (!visited.add(typeName)) {
                return false;
            }

            if (typesWithEqualsOrHashCode.contains(type)) {
                return true;
            }

            Set<TypeSummary> subclasses = classToSubclasses.get(typeName);
            if (subclasses != null) {
                for (TypeSummary subclass : subclasses) {
                    if (typeOrDescendantHasEqualsOrHashCode(subclass, typesWithEqualsOrHashCode, visited)) {
                        return true;
                    }
                }
            }
            return false;
        }

        private static String getFullTypeName(TypeSummary summary) {
            // For nested types, this returns the full path like "OuterClass.InnerClass"
            return summary.typeName().toString();
        }

        private static String getSimpleName(String typeName) {
            int lastDot = typeName.lastIndexOf('.');
            return lastDot >= 0 ? typeName.substring(lastDot + 1) : typeName;
        }

        private static boolean isAbstract(TypeSummary summary) {
            scala.collection.Iterator<Modifier> iter = summary.modifiers().iterator();
            while (iter.hasNext()) {
                if ("abstract".equalsIgnoreCase(iter.next().name())) {
                    return true;
                }
            }
            return false;
        }

        private static boolean definesEqualsOrHashCode(TypeSummary summary) {
            scala.collection.Iterator<MethodSummary> iter = summary.methods().iterator();
            while (iter.hasNext()) {
                MethodSummary method = iter.next();
                String methodName = method.name();
                if ("hashCode".equalsIgnoreCase(methodName) && !method.parameters().iterator().hasNext()) {
                    return true;
                }
                if ("equals".equalsIgnoreCase(methodName)) {
                    scala.collection.Iterator<ParameterSummary> params = method.parameters().iterator();
                    if (params.hasNext()) {
                        ParameterSummary param = params.next();
                        String paramTypeName = param.typeName().name().value();
                        // ApexLink may report "Object$" for System.Object
                        if (paramTypeName.toLowerCase(Locale.ROOT).startsWith("object")
                                && !params.hasNext()) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }
}
