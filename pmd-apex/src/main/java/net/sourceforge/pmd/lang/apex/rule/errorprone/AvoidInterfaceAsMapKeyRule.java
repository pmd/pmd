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
            if (index.isProblematicInterfaceKey(usage.keyTypeSimpleName)) {
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
                    usages.add(new MapKeyUsage(Helper.getSimpleTypeName(args.get(0)), field));
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

    private static final class MapKeyUsage {
        final String keyTypeSimpleName;
        final ApexNode<?> reportNode;

        MapKeyUsage(String keyTypeSimpleName, ApexNode<?> reportNode) {
            this.keyTypeSimpleName = keyTypeSimpleName;
            this.reportNode = reportNode;
        }
    }

    /**
     * Pre-indexes the type hierarchy once to enable efficient queries.
     * Builds lookup maps in a single O(N) pass for:
     * - Interfaces in the org
     * - Interface → abstract implementors
     * - Class → direct subclasses
     * - Types that define equals or hashCode
     *
     * <p>ApexLink's TypeSummary API provides "what this type extends/implements" (outgoing),
     * but we need "what extends/implements this type" (incoming). This index inverts
     * those relationships for efficient lookup.
     */
    private static final class TypeHierarchyIndex {
        // Interface name (lowercase) → set of abstract implementors
        private final Map<String, Set<TypeSummary>> interfaceToAbstractImpls = new HashMap<>();
        // Class name (lowercase) → set of direct subclasses
        private final Map<String, Set<TypeSummary>> classToSubclasses = new HashMap<>();
        // Set of types that define equals or hashCode (for quick lookup)
        private final Set<TypeSummary> typesWithEqualsOrHashCode = new HashSet<>();
        // All known interfaces
        private final Set<String> knownInterfaces = new HashSet<>();

        TypeHierarchyIndex(List<TypeSummary> allTypes) {
            // Single pass: build all indexes
            for (TypeSummary summary : allTypes) {
                String nature = summary.nature();
                String typeName = summary.typeName().name().value().toLowerCase(Locale.ROOT);

                if ("interface".equalsIgnoreCase(nature)) {
                    knownInterfaces.add(typeName);
                } else if ("class".equalsIgnoreCase(nature)) {
                    // Index by superclass for subclass lookup
                    scala.Option<TypeName> superOpt = summary.superClass();
                    if (superOpt.isDefined()) {
                        String superName = superOpt.get().name().value().toLowerCase(Locale.ROOT);
                        classToSubclasses.computeIfAbsent(superName, k -> new HashSet<>()).add(summary);
                    }

                    // Index abstract implementors by interface
                    if (isAbstract(summary)) {
                        scala.collection.Iterator<TypeName> ifaces = summary.interfaces().iterator();
                        while (ifaces.hasNext()) {
                            String ifaceName = ifaces.next().name().value().toLowerCase(Locale.ROOT);
                            interfaceToAbstractImpls.computeIfAbsent(ifaceName, k -> new HashSet<>())
                                    .add(summary);
                        }
                    }

                    // Track types with equals/hashCode
                    if (definesEqualsOrHashCode(summary)) {
                        typesWithEqualsOrHashCode.add(summary);
                    }
                }
            }
        }

        /**
         * Checks if using this interface as a Map key is problematic.
         * O(1) interface lookup + O(abstract impls) + O(subclasses per impl)
         */
        boolean isProblematicInterfaceKey(String interfaceSimpleName) {
            String key = interfaceSimpleName.toLowerCase(Locale.ROOT);

            // Must be a known interface
            if (!knownInterfaces.contains(key)) {
                return false;
            }

            // Get abstract implementors
            Set<TypeSummary> abstractImpls = interfaceToAbstractImpls.get(key);
            if (abstractImpls == null || abstractImpls.isEmpty()) {
                return false;
            }

            // Check each abstract implementor and its subclass tree
            for (TypeSummary abstractImpl : abstractImpls) {
                if (typeOrDescendantDefinesEqualsOrHashCode(abstractImpl)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Recursively checks if this type or any descendant defines equals/hashCode.
         */
        private boolean typeOrDescendantDefinesEqualsOrHashCode(TypeSummary type) {
            if (typesWithEqualsOrHashCode.contains(type)) {
                return true;
            }
            String typeName = type.typeName().name().value().toLowerCase(Locale.ROOT);
            Set<TypeSummary> subclasses = classToSubclasses.get(typeName);
            if (subclasses != null) {
                for (TypeSummary subclass : subclasses) {
                    if (typeOrDescendantDefinesEqualsOrHashCode(subclass)) {
                        return true;
                    }
                }
            }
            return false;
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
