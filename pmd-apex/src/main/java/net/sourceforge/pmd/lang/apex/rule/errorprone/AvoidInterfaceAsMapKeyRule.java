/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.errorprone;

import java.util.ArrayList;
import java.util.List;

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
        List<TypeSummary> allTypes = mfa.getTypeSummaries();
        for (MapKeyUsage usage : collectMapKeyUsages(node)) {
            TypeHierarchyChecker checker = new TypeHierarchyChecker(usage.keyTypeSimpleName, allTypes);
            if (checker.isViolation()) {
                asCtx(data).addViolation(usage.reportNode);
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
     * Checks if a given interface name triggers the Map key dispatch bug.
     * The bug occurs when:
     * 1. The key type is an interface
     * 2. There's an abstract class implementing that interface
     * 3. Either the abstract class OR any class extending it defines equals/hashCode
     */
    private static final class TypeHierarchyChecker {
        private final String interfaceSimpleName;
        private final List<TypeSummary> allTypes;

        TypeHierarchyChecker(String interfaceSimpleName, List<TypeSummary> allTypes) {
            this.interfaceSimpleName = interfaceSimpleName;
            this.allTypes = allTypes;
        }

        /**
         * Returns true if using this interface as a Map key would trigger the dispatch bug.
         */
        boolean isViolation() {
            // Step 1: Verify it's actually an interface
            if (!isInterfaceInOrg()) {
                return false;
            }

            // Step 2: Find all abstract classes implementing this interface
            List<TypeSummary> abstractImplementors = findAbstractImplementors();
            if (abstractImplementors.isEmpty()) {
                return false;
            }

            // Step 3: For each abstract implementor, check if it or any subclass defines equals/hashCode
            for (TypeSummary abstractImpl : abstractImplementors) {
                if (definesEqualsOrHashCode(abstractImpl)) {
                    return true;
                }
                // Check all classes that extend this abstract class
                if (anySubclassDefinesEqualsOrHashCode(abstractImpl)) {
                    return true;
                }
            }
            return false;
        }

        private boolean isInterfaceInOrg() {
            for (TypeSummary summary : allTypes) {
                if ("interface".equalsIgnoreCase(summary.nature())
                        && summary.typeName().name().value().equalsIgnoreCase(interfaceSimpleName)) {
                    return true;
                }
            }
            return false;
        }

        private List<TypeSummary> findAbstractImplementors() {
            List<TypeSummary> result = new ArrayList<>();
            for (TypeSummary summary : allTypes) {
                if ("class".equalsIgnoreCase(summary.nature())
                        && isAbstract(summary)
                        && implementsInterface(summary)) {
                    result.add(summary);
                }
            }
            return result;
        }

        private boolean anySubclassDefinesEqualsOrHashCode(TypeSummary abstractClass) {
            String abstractClassName = abstractClass.typeName().name().value();
            for (TypeSummary summary : allTypes) {
                if (!"class".equalsIgnoreCase(summary.nature())) {
                    continue;
                }
                if (extendsClass(summary, abstractClassName) && definesEqualsOrHashCode(summary)) {
                    return true;
                }
            }
            return false;
        }

        private boolean extendsClass(TypeSummary summary, String superClassName) {
            // Check direct superclass - superClass() returns Scala Option
            scala.Option<TypeName> superTypeOpt = summary.superClass();
            if (superTypeOpt.isDefined()) {
                TypeName superType = superTypeOpt.get();
                if (superType.name().value().equalsIgnoreCase(superClassName)) {
                    return true;
                }
            }
            // For transitive extends, we'd need to walk the hierarchy - for now check direct only
            // This handles the common case of Interface -> Abstract -> Concrete
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

        private boolean implementsInterface(TypeSummary summary) {
            scala.collection.Iterator<TypeName> iter = summary.interfaces().iterator();
            while (iter.hasNext()) {
                if (iter.next().name().value().equalsIgnoreCase(interfaceSimpleName)) {
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
                        if (paramTypeName.toLowerCase(java.util.Locale.ROOT).startsWith("object")
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
