/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTArrayAccess;
import net.sourceforge.pmd.lang.java.ast.ASTArrayAllocation;
import net.sourceforge.pmd.lang.java.ast.ASTArrayDimExpr;
import net.sourceforge.pmd.lang.java.ast.ASTArrayInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTArrayTypeDim;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTList;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.InvocationNode;
import net.sourceforge.pmd.lang.java.ast.ModifierOwner.Visibility;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.AssignmentEntry;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.DataflowResult;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.ReachingDefinitionSet;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFormalParamSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JTypeVar;
import net.sourceforge.pmd.lang.java.types.OverloadSelectionResult;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * Flags methods that expose an internal array through their return value.
 *
 * <p>The rule traces array provenance backwards from each return. Local
 * aliases are resolved with {@link DataflowPass}. Calls are followed only
 * when their generic declaration connects an input type variable to the
 * result, which covers arbitrary container and accessor chains without an API
 * allowlist. Exact helpers in the current class are summarized from their
 * return expressions, so a helper that returns a clone is distinguished from
 * one that returns the field itself.
 *
 * <p>The analysis is a may-analysis: if any reaching definition or value
 * branch carries an internal array, the return is reported. Heap mutation,
 * dynamically dispatched helpers, and non-generic calls across class
 * boundaries are not modelled.
 */
public class MethodReturnsInternalArrayRule extends AbstractJavaRulechainRule {

    public MethodReturnsInternalArrayRule() {
        super(ASTMethodDeclaration.class);
    }

    @Override
    public Object visit(ASTMethodDeclaration method, Object data) {
        RuleContext ctx = (RuleContext) data;

        if (!method.getResultTypeNode().getTypeMirror().isArray()
            || method.getVisibility() == Visibility.V_PRIVATE) {
            return null;
        }

        DataflowResult dataflow = DataflowPass.getDataflowResult(method.getRoot());
        InternalArrayEscapeAnalysis analysis = new InternalArrayEscapeAnalysis(
            dataflow, method.getEnclosingType().getSymbol());

        for (ASTReturnStatement returnStmt : method.descendants(ASTReturnStatement.class)) {
            if (JavaAstUtils.getReturnTarget(returnStmt) != method) {
                continue;
            }
            ASTNamedReferenceExpr source = analysis.findEscapingField(returnStmt.getExpr());
            if (source != null) {
                ctx.addViolation(returnStmt, source.getName());
            }
        }
        return null;
    }

    private static boolean isInternalArrayReference(ASTNamedReferenceExpr reference) {
        if (!reference.getTypeMirror().isArray()) {
            return false;
        }
        if (JavaAstUtils.isRefToFieldOfThisInstance(reference)) {
            return true;
        }
        JVariableSymbol symbol = reference.getReferencedSym();
        if (symbol instanceof JFieldSymbol) {
            JFieldSymbol field = (JFieldSymbol) symbol;
            return field.isStatic() && isInternal(field) && !isZeroLengthArrayConstant(field);
        }
        return false;
    }

    private static boolean isInternal(JFieldSymbol field) {
        return !Modifier.isPublic(field.getModifiers())
            && !field.isUnresolved();
    }

    private static boolean isZeroLengthArrayConstant(JFieldSymbol sym) {
        return sym.isFinal()
                && NodeStream.of(sym.tryGetNode())
                         .map(ASTVariableId::getInitializer)
                         .filter(MethodReturnsInternalArrayRule::isZeroLengthArrayExpr)
                         .nonEmpty();
    }

    private static boolean isZeroLengthArrayExpr(ASTExpression expr) {
        if (expr instanceof ASTArrayInitializer) {
            return ((ASTArrayInitializer) expr).length() == 0;
        } else if (expr instanceof ASTArrayAllocation) {
            ASTArrayInitializer init = ((ASTArrayAllocation) expr).getArrayInitializer();
            if (init != null) {
                return init.length() == 0;
            }
            ASTArrayTypeDim lastChild = ((ASTArrayAllocation) expr).getTypeNode().getDimensions().getLastChild();
            if (lastChild instanceof ASTArrayDimExpr) {
                return JavaAstUtils.isLiteralInt(((ASTArrayDimExpr) lastChild).getLengthExpression(), 0);
            }
        }
        return false;
    }

    private static boolean isScalar(JTypeMirror type) {
        return type.isPrimitive()
            || type.isBoxedPrimitive()
            || TypeTestUtil.isExactlyA(String.class, type)
            || TypeTestUtil.isExactlyA(Class.class, type);
    }

    private static boolean isFreshArrayProducer(ASTMethodCall call) {
        String name = call.getMethodName();
        if ("clone".equals(name)) {
            ASTExpression qualifier = call.getQualifier();
            return qualifier != null
                && qualifier.getTypeMirror().isArray()
                && ASTList.sizeOrZero(call.getArguments()) == 0
                && call.getTypeMirror().isArray();
        }
        return ("copyOf".equals(name) || "copyOfRange".equals(name))
            && TypeTestUtil.isDeclaredInClass(java.util.Arrays.class, call.getMethodType());
    }

    private static final class InternalArrayEscapeAnalysis {

        private final DataflowResult dataflow;
        private final JClassSymbol rootClass;

        private InternalArrayEscapeAnalysis(DataflowResult dataflow, JClassSymbol rootClass) {
            this.dataflow = dataflow;
            this.rootClass = rootClass;
        }

        @Nullable
        ASTNamedReferenceExpr findEscapingField(@Nullable ASTExpression expression) {
            return findEscapingField(expression, AnalysisContext.root());
        }

        private @Nullable ASTNamedReferenceExpr findEscapingField(@Nullable ASTExpression expression,
                                                                  AnalysisContext context) {
            if (expression == null || isScalar(expression.getTypeMirror())) {
                return null;
            }

            if (expression instanceof ASTNamedReferenceExpr) {
                return fromNamedReference((ASTNamedReferenceExpr) expression, context);
            } else if (expression instanceof ASTCastExpression) {
                return findEscapingField(((ASTCastExpression) expression).getOperand(), context);
            } else if (expression instanceof ASTConditionalExpression) {
                ASTConditionalExpression conditional = (ASTConditionalExpression) expression;
                ASTNamedReferenceExpr source = findEscapingField(conditional.getThenBranch(), context);
                return source != null ? source : findEscapingField(conditional.getElseBranch(), context);
            } else if (expression instanceof ASTSwitchExpression) {
                for (ASTExpression yield : ((ASTSwitchExpression) expression).getYieldExpressions()) {
                    ASTNamedReferenceExpr source = findEscapingField(yield, context);
                    if (source != null) {
                        return source;
                    }
                }
            } else if (expression instanceof ASTAssignmentExpression) {
                return findEscapingField(((ASTAssignmentExpression) expression).getRightOperand(), context);
            } else if (expression instanceof ASTArrayAccess) {
                return findEscapingField(((ASTArrayAccess) expression).getQualifier(), context);
            } else if (expression instanceof ASTArrayAllocation) {
                return fromArrayAllocation((ASTArrayAllocation) expression, context);
            } else if (expression instanceof ASTMethodCall) {
                return fromMethodCall((ASTMethodCall) expression, context);
            } else if (expression instanceof ASTConstructorCall) {
                return fromGenericSignature((ASTConstructorCall) expression, null, context);
            }
            return null;
        }

        private @Nullable ASTNamedReferenceExpr fromNamedReference(ASTNamedReferenceExpr reference,
                                                                    AnalysisContext context) {
            if (isInternalArrayReference(reference)) {
                return reference;
            }

            JVariableSymbol symbol = reference.getReferencedSym();
            if (symbol == null || symbol instanceof JFieldSymbol
                || !context.visitingVariables.add(symbol)) {
                return null;
            }

            try {
                ReachingDefinitionSet reaching = dataflow.getReachingDefinitions(reference);
                List<AssignmentEntry> definitions = new ArrayList<>(reaching.getReaching());
                Collections.sort(definitions);
                for (AssignmentEntry definition : definitions) {
                    ASTExpression rhs = definition.getRhsAsExpression();
                    if (definition.isForeachVar()) {
                        rhs = definition.getVarId()
                                        .ancestors(ASTForeachStatement.class)
                                        .firstOrThrow()
                                        .getIterableExpr();
                    }
                    ASTNamedReferenceExpr source = rhs == null
                                                   ? context.entryValue(symbol)
                                                   : findEscapingField(rhs, context);
                    if (source != null) {
                        return source;
                    }
                }
                return reaching.isNotFullyKnown() ? context.entryValue(symbol) : null;
            } finally {
                context.visitingVariables.remove(symbol);
            }
        }

        private @Nullable ASTNamedReferenceExpr fromArrayAllocation(ASTArrayAllocation allocation,
                                                                     AnalysisContext context) {
            ASTArrayInitializer initializer = allocation.getArrayInitializer();
            if (initializer != null) {
                for (ASTExpression element : initializer) {
                    ASTNamedReferenceExpr source = findEscapingField(element, context);
                    if (source != null) {
                        return source;
                    }
                }
            }
            return null;
        }

        private @Nullable ASTNamedReferenceExpr fromMethodCall(ASTMethodCall call,
                                                                AnalysisContext context) {
            if (isFreshArrayProducer(call)) {
                return null;
            }

            ASTMethodDeclaration helper = getSummarizableHelper(call);
            if (helper != null) {
                return summarizeHelper(call, helper, context);
            }
            return fromGenericSignature(call, call.getQualifier(), context);
        }

        private @Nullable ASTMethodDeclaration getSummarizableHelper(ASTMethodCall call) {
            OverloadSelectionResult overload = call.getOverloadSelectionInfo();
            if (overload.isFailed()) {
                return null;
            }

            JExecutableSymbol executable = overload.getMethodType().getSymbol();
            if (!(executable instanceof JMethodSymbol)
                || !rootClass.equals(executable.getEnclosingClass())) {
                return null;
            }

            int modifiers = executable.getModifiers();
            boolean exactDispatch = executable.isStatic()
                || Modifier.isPrivate(modifiers)
                || Modifier.isFinal(modifiers)
                || rootClass.isFinal();
            if (!exactDispatch
                || !executable.isStatic() && !JavaAstUtils.isCallOnThisInstance(call).isTrue()) {
                return null;
            }

            ASTMethodDeclaration declaration = ((JMethodSymbol) executable).tryGetNode();
            return declaration != null && declaration.getBody() != null ? declaration : null;
        }

        private @Nullable ASTNamedReferenceExpr summarizeHelper(ASTMethodCall call,
                                                                 ASTMethodDeclaration helper,
                                                                 AnalysisContext callerContext) {
            JExecutableSymbol executable = call.getMethodType().getSymbol();
            if (!callerContext.visitingExecutables.add(executable)) {
                return null;
            }

            try {
                AnalysisContext helperContext = callerContext.forCall(bindArguments(call, executable,
                                                                                    callerContext));
                for (ASTReturnStatement returnStmt : helper.descendants(ASTReturnStatement.class)) {
                    if (JavaAstUtils.getReturnTarget(returnStmt) == helper) {
                        ASTNamedReferenceExpr source = findEscapingField(returnStmt.getExpr(), helperContext);
                        if (source != null) {
                            return source;
                        }
                    }
                }
                return null;
            } finally {
                callerContext.visitingExecutables.remove(executable);
            }
        }

        private Map<JVariableSymbol, ASTNamedReferenceExpr> bindArguments(InvocationNode invocation,
                                                                          JExecutableSymbol executable,
                                                                          AnalysisContext context) {
            Map<JVariableSymbol, ASTNamedReferenceExpr> result = new HashMap<>();
            List<JFormalParamSymbol> formals = executable.getFormalParameters();
            int argumentCount = ASTList.sizeOrZero(invocation.getArguments());
            boolean varargs = invocation.getOverloadSelectionInfo().isVarargsCall() && !formals.isEmpty();
            int fixedCount = varargs ? formals.size() - 1 : formals.size();
            for (int i = 0; i < fixedCount && i < argumentCount; i++) {
                ASTNamedReferenceExpr source = findEscapingField(invocation.getArguments().get(i), context);
                if (source != null) {
                    result.put(formals.get(i), source);
                }
            }
            if (varargs) {
                for (int i = fixedCount; i < argumentCount; i++) {
                    ASTNamedReferenceExpr source = findEscapingField(invocation.getArguments().get(i), context);
                    if (source != null) {
                        result.put(formals.get(formals.size() - 1), source);
                        break;
                    }
                }
            }
            return result;
        }

        private @Nullable ASTNamedReferenceExpr fromGenericSignature(InvocationNode invocation,
                                                                      @Nullable ASTExpression receiver,
                                                                      AnalysisContext context) {
            OverloadSelectionResult overload = invocation.getOverloadSelectionInfo();
            if (overload.isFailed()) {
                return null;
            }

            JMethodSig generic = overload.getMethodType().getSymbol().getGenericSignature();
            Set<JTypeVar> resultVariables = resultTypeVariables(generic);
            if (resultVariables.isEmpty()) {
                return null;
            }

            if (receiver != null && generic.getSymbol().hasReceiver()
                && TypeOps.mentionsAny(generic.getDeclaringType(), resultVariables)) {
                ASTNamedReferenceExpr source = findEscapingField(receiver, context);
                if (source != null) {
                    return source;
                }
            }

            int argumentCount = ASTList.sizeOrZero(invocation.getArguments());
            boolean varargs = overload.isVarargsCall();
            for (int i = 0; i < argumentCount && (i < generic.getArity() || varargs); i++) {
                if (TypeOps.mentionsAny(generic.ithFormalParam(i, varargs), resultVariables)) {
                    ASTNamedReferenceExpr source = findEscapingField(invocation.getArguments().get(i), context);
                    if (source != null) {
                        return source;
                    }
                }
            }
            return null;
        }

        private Set<JTypeVar> resultTypeVariables(JMethodSig generic) {
            Set<JTypeVar> candidates = new LinkedHashSet<>(generic.getTypeParameters());
            JTypeMirror declaringType = generic.getDeclaringType();
            while (declaringType instanceof JClassType) {
                JClassType declaringClass = (JClassType) declaringType;
                candidates.addAll(declaringClass.getFormalTypeParams());
                declaringType = declaringClass.getEnclosingType();
            }
            candidates.removeIf(typeVar -> !TypeOps.mentionsAny(generic.getReturnType(),
                                                                 Collections.singleton(typeVar)));
            return candidates;
        }

        private static final class AnalysisContext {

            private final Map<JVariableSymbol, ASTNamedReferenceExpr> entryValues;
            private final Set<JVariableSymbol> visitingVariables;
            private final Set<JExecutableSymbol> visitingExecutables;

            private AnalysisContext(Map<JVariableSymbol, ASTNamedReferenceExpr> entryValues,
                                    Set<JVariableSymbol> visitingVariables,
                                    Set<JExecutableSymbol> visitingExecutables) {
                this.entryValues = entryValues;
                this.visitingVariables = visitingVariables;
                this.visitingExecutables = visitingExecutables;
            }

            private static AnalysisContext root() {
                return new AnalysisContext(new HashMap<>(), new HashSet<>(), new HashSet<>());
            }

            private AnalysisContext forCall(Map<JVariableSymbol, ASTNamedReferenceExpr> parameters) {
                Map<JVariableSymbol, ASTNamedReferenceExpr> values = new HashMap<>(entryValues);
                values.putAll(parameters);
                return new AnalysisContext(values, visitingVariables, visitingExecutables);
            }

            private @Nullable ASTNamedReferenceExpr entryValue(JVariableSymbol symbol) {
                return entryValues.get(symbol);
            }
        }
    }
}
