/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.pcollections.PSet;

import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr;
import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTFieldAccess;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.InvocationNode;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil;
import net.sourceforge.pmd.lang.java.symbols.JAccessibleElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JVariableSig;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * @since 7.28.0
 */
public class InternalApiUsageRule extends AbstractJavaRulechainRule {

    public enum ApiStatus {
        INTERNAL("INTERNAL"),
        DEPRECATED("DEPRECATED", "OBSOLETE"),
        EXPERIMENTAL("EXPERIMENTAL"),
        OVERRIDE_ONLY("OVERRIDEONLY");

        final List<String> aliases;

        ApiStatus(String... aliases) {
            this.aliases = Arrays.asList(aliases);
        }
    }

    private static final PropertyDescriptor<List<String>> OWN_PACKAGES =
            PropertyFactory.stringListProperty("ownPackages")
                    .desc("Packages whose internal APIs may be used.")
                    .defaultValue(Collections.emptyList())
                    .build();
    private static final PropertyDescriptor<List<ApiStatus>> REPORTED_STATUSES =
            PropertyFactory.conventionalEnumListProperty("reportedStatuses", ApiStatus.class)
                    .desc("List of API statuses that should not be called")
                    .defaultValue(Arrays.asList(ApiStatus.INTERNAL, ApiStatus.OVERRIDE_ONLY))
                    .build();
    private final List<String> ownPackages = new ArrayList<>();
    private final List<ApiStatus> reportedStatuses = new ArrayList<>();
    private String basePackage = "";

    public InternalApiUsageRule() {
        super(
                ASTPackageDeclaration.class,
                ASTMethodCall.class,
                ASTConstructorCall.class,
                ASTFieldAccess.class,
                ASTVariableAccess.class);
        definePropertyDescriptor(OWN_PACKAGES);
        definePropertyDescriptor(REPORTED_STATUSES);
    }

    @Override
    public void start(RuleContext ctx) {
        super.start(ctx);
        if (getProperty(OWN_PACKAGES) != null) {
            ownPackages.addAll(getProperty(OWN_PACKAGES));
        }
        if (getProperty(REPORTED_STATUSES) != null) {
            reportedStatuses.addAll(getProperty(REPORTED_STATUSES));
        }
    }

    @Override
    public Object visit(ASTPackageDeclaration node, Object data) {
        basePackage = node.getName().replaceFirst("^([^.]+\\.[^.]+\\.[^.]+).*$", "$1");
        return null;
    }

    @Override
    public Object visit(ASTMethodCall node, Object data) {
        RuleContext ctx = asCtx(data);
        visitCall(node, ctx, "Method");
        return null;
    }

    @Override
    public Object visit(ASTConstructorCall node, Object data) {
        RuleContext ctx = asCtx(data);
        visitCall(node, ctx, "Constructor");
        return null;
    }

    @Override
    public Object visit(ASTFieldAccess node, Object data) {
        RuleContext ctx = asCtx(data);
        visitFieldOrVariable(node, node.getReferencedSym(), ctx);
        return null;
    }

    @Override
    public Object visit(ASTVariableAccess node, Object data) {
        JVariableSymbol variableSymbol = node.getReferencedSym();
        if (variableSymbol instanceof JFieldSymbol) {
            RuleContext ctx = asCtx(data);
            visitFieldOrVariable(node, (JFieldSymbol) variableSymbol, ctx);
        }
        return null;
    }

    private void visitFieldOrVariable(ASTAssignableExpr.ASTNamedReferenceExpr node, JFieldSymbol fieldSymbol, RuleContext ctx) {
        ASTClassDeclaration parentClass = node.ancestors(ASTClassDeclaration.class).first();
        JVariableSig signature = node.getSignature();
        if (signature != null) {
            JTypeMirror declaringType = signature.getDeclaringType();
            boolean sameUnit = areInSameEnclosingType(declaringType, parentClass);
            checkAnnotationsWithParent(ctx, node, "Field", fieldSymbol, node.getName(),
                    signature.getDeclaringType(), sameUnit);
        }
    }

    private void visitCall(InvocationNode node, RuleContext data, String nodeType) {
        JMethodSig methodType = node.getMethodType();
        ASTClassDeclaration parentClass = node.ancestors(ASTClassDeclaration.class).first();
        JTypeMirror declaringType = methodType.getDeclaringType();
        boolean sameUnit = areInSameEnclosingType(declaringType, parentClass);
        JExecutableSymbol methodSymbol = methodType.getSymbol();
        String methodName = methodType.getName();
        checkAnnotationsWithParent(data, node, nodeType, methodSymbol, methodName, declaringType, sameUnit);
    }

    private void checkAnnotationsWithParent(RuleContext ctx, TypeNode node, String nodeType,
                                  JAccessibleElementSymbol methodSymbol, String methodName,
                                  JTypeMirror declaringType,
                                  boolean sameUnit) {
        checkAnnotations(ctx, node, nodeType, methodSymbol, methodName, declaringType, sameUnit);
        JTypeDeclSymbol classSymbol = declaringType.getSymbol();
        if (classSymbol != null) {
            checkAnnotations(ctx, node, "Class", classSymbol,
                    classSymbol.getSimpleName(), declaringType, sameUnit);
        }
    }

    private void checkAnnotations(RuleContext ctx, TypeNode node, String nodeType,
                                  JAccessibleElementSymbol methodSymbol, String methodName,
                                  JTypeMirror declaringType,
                                  boolean sameUnit) {
        ASTClassDeclaration parentClass = node.ancestors(ASTClassDeclaration.class).first();
        PSet<SymbolicValue.SymAnnot> annotations = methodSymbol.getDeclaredAnnotations();
        for (SymbolicValue.SymAnnot annotation: annotations) {
            if ("TestOnly".equals(annotation.getSimpleName())
                    || !sameUnit && "VisibleForTesting".equals(annotation.getSimpleName())) {
                if (!node.ancestors(ASTClassDeclaration.class).any(TestFrameworksUtil::isTestClass)) {
                    ctx.addViolation(node, nodeType, methodName, "tests");
                }
                return;
            }
            if (!sameUnit && "org.apiguardian.api.API".equals(annotation.getBinaryName())) {
                String statusFullName = String.valueOf(annotation.getAttribute("status"));
                SymbolicValue consumers = annotation.getAttribute("consumers");
                String statusName = statusFullName.substring(statusFullName.indexOf('#') + 1);
                if (isStatusAllowed(statusName)) {
                    continue;
                }
                if (consumers instanceof SymbolicValue.SymArray && !((SymbolicValue.SymArray) consumers).containsValue("*")) {
                    SymbolicValue.SymArray consumerArray = (SymbolicValue.SymArray) consumers;
                    String packageName = parentClass == null ? null : parentClass.getPackageName();
                    if (packageName != null
                            && !consumerArray.anyMatch(v -> v.valueEquals(packageName))) {
                        ctx.addViolation(node, nodeType, methodName, "specific packages");
                    }
                } else if (isExternal(declaringType)) {
                    ctx.addViolation(node, nodeType, methodName, "declaring library");
                }
            }
            if (!sameUnit && annotation.getBinaryName().startsWith("org.jetbrains.annotations.ApiStatus$")) {
                String statusName = annotation.getBinaryName().substring("org.jetbrains.annotations.ApiStatus$".length());
                if (!isStatusAllowed(statusName) && isExternal(declaringType)) {
                    ctx.addViolation(node, nodeType, methodName, "declaring library");
                }
            }
        }
    }

    private boolean isStatusAllowed(String statusName) {
        for (ApiStatus reported: reportedStatuses) {
            if (reported.aliases.contains(statusName.toUpperCase(Locale.ROOT))) {
                return false;
            }
        }
        return true;
    }

    private boolean isExternal(JTypeMirror declaringType) {
        String packageName = declaringType.getSymbol().getPackageName();
        if (!ownPackages.isEmpty()) {
            return ownPackages.stream().noneMatch(packageName::startsWith);
        }
        return !packageName.startsWith(basePackage);
    }

    private boolean areInSameEnclosingType(JTypeMirror start, ASTClassDeclaration other) {
        if (!(start instanceof JClassType) || other == null) {
            return false;
        }
        JClassSymbol t = ((JClassType) start).getSymbol();
        JClassSymbol otherRoot = other.getTypeMirror().getSymbol();
        while (otherRoot.getEnclosingClass() != null) {
            otherRoot = otherRoot.getEnclosingClass();
        }
        do {
            if (t.equals(otherRoot)) {
                return true;
            }
            t = t.getEnclosingClass();
        } while (t != null);
        return false;
    }

}
