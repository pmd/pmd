/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;
import static net.sourceforge.pmd.properties.PropertyFactory.stringListProperty;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpressionStatement;
import net.sourceforge.pmd.lang.java.ast.ASTFinallyClause;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTTypeExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.types.InvocationMatcher;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;

/**
 * Makes sure you close your database connections. It does this by looking for
 * code patterned like this:
 *
 * <pre>
 *  Connection c = X;
 *  try {
 *   // do stuff, and maybe catch something
 *  } finally {
 *   c.close();
 *  }
 * </pre>
 *
 *  @author original author unknown
 *  @author Contribution from Pierre Mathien
 */
public class CloseResourceRule extends AbstractJavaRule {

    private static final String WRAPPING_TRY_WITH_RES_VAR_MESSAGE = "it is recommended to wrap resource ''{0}'' in try-with-resource declaration directly";
    private static final String REASSIGN_BEFORE_CLOSED_MESSAGE = "''{0}'' is reassigned, but the original instance is not closed";
    private static final String CLOSE_IN_FINALLY_BLOCK_MESSAGE = "''{0}'' is not closed within a finally block, thus might not be closed at all in case of exceptions";

    private static final PropertyDescriptor<List<String>> CLOSE_TARGETS_DESCRIPTOR =
            stringListProperty("closeTargets")
                           .desc("Methods which may close this resource")
                           .emptyDefaultValue()
                           .delim(',').build();

    private static final PropertyDescriptor<List<String>> TYPES_DESCRIPTOR =
            stringListProperty("types")
                    .desc("Affected types")
                    .defaultValues("java.lang.AutoCloseable", "java.sql.Connection", "java.sql.Statement", "java.sql.ResultSet")
                    .delim(',').build();

    private static final PropertyDescriptor<Boolean> USE_CLOSE_AS_DEFAULT_TARGET =
            booleanProperty("closeAsDefaultTarget")
                    .desc("Consider 'close' as a target by default").defaultValue(true).build();

    private static final PropertyDescriptor<List<String>> ALLOWED_RESOURCE_TYPES =
            stringListProperty("allowedResourceTypes")
            .desc("Exact class names that do not need to be closed")
            .defaultValues("java.io.ByteArrayOutputStream", "java.io.ByteArrayInputStream", "java.io.StringWriter",
                    "java.io.CharArrayWriter", "java.util.stream.Stream", "java.util.stream.IntStream", "java.util.stream.LongStream",
                    "java.util.stream.DoubleStream")
            .build();

    private static final PropertyDescriptor<Boolean> DETECT_CLOSE_NOT_IN_FINALLY =
            booleanProperty("closeNotInFinally")
                .desc("Detect if 'close' (or other closeTargets) is called outside of a finally-block").defaultValue(false).build();

    private final Set<String> types = new HashSet<>();
    private final Set<String> simpleTypes = new HashSet<>();
    private final Set<String> closeTargets = new HashSet<>();

    // keeps track of already reported violations to avoid duplicated violations for the same variable
    private final Set<String> reportedVarNames = new HashSet<>();

    public CloseResourceRule() {
        definePropertyDescriptor(CLOSE_TARGETS_DESCRIPTOR);
        definePropertyDescriptor(TYPES_DESCRIPTOR);
        definePropertyDescriptor(USE_CLOSE_AS_DEFAULT_TARGET);
        definePropertyDescriptor(ALLOWED_RESOURCE_TYPES);
        definePropertyDescriptor(DETECT_CLOSE_NOT_IN_FINALLY);
    }

    @Override
    public void start(RuleContext ctx) {
        closeTargets.clear();
        simpleTypes.clear();
        types.clear();

        if (getProperty(CLOSE_TARGETS_DESCRIPTOR) != null) {
            closeTargets.addAll(getProperty(CLOSE_TARGETS_DESCRIPTOR));
        }
        if (getProperty(USE_CLOSE_AS_DEFAULT_TARGET)) {
            closeTargets.add("close");
        }
        if (getProperty(TYPES_DESCRIPTOR) != null) {
            types.addAll(getProperty(TYPES_DESCRIPTOR));
            for (String type : getProperty(TYPES_DESCRIPTOR)) {
                simpleTypes.add(toSimpleType(type));
            }
        }
    }

    private static String toSimpleType(String fullyQualifiedClassName) {
        int lastIndexOf = fullyQualifiedClassName.lastIndexOf('.');
        if (lastIndexOf > -1) {
            return fullyQualifiedClassName.substring(lastIndexOf + 1);
        } else {
            return fullyQualifiedClassName;
        }
    }

    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        checkForResources(node, data);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        checkForResources(node, data);
        return super.visit(node, data);
    }

    private void checkForResources(ASTMethodOrConstructorDeclaration methodOrConstructor, Object data) {
        reportedVarNames.clear();
        Map<ASTVariableDeclaratorId, TypeNode> resVars = getResourceVariables(methodOrConstructor);
        for (Map.Entry<ASTVariableDeclaratorId, TypeNode> resVarEntry : resVars.entrySet()) {
            ASTVariableDeclaratorId resVar = resVarEntry.getKey();
            TypeNode runtimeType = resVarEntry.getValue();
            TypeNode resVarType = wrappedResourceTypeOrReturn(resVar, runtimeType);

            if (isWrappingResourceSpecifiedInTry(resVar)) {
                reportedVarNames.add(resVar.getName());
                addViolationWithMessage(data, resVar, WRAPPING_TRY_WITH_RES_VAR_MESSAGE,
                        new Object[] { resVar.getName() });
            } else if (shouldVarOfTypeBeClosedInMethod(resVar, resVarType, methodOrConstructor)) {
                reportedVarNames.add(resVar.getName());
                addCloseResourceViolation(resVar, runtimeType, data);
            } else if (isNotAllowedResourceType(resVarType)) {
                ASTExpressionStatement reassigningStatement = getFirstReassigningStatementBeforeBeingClosed(resVar, methodOrConstructor);
                if (reassigningStatement != null) {
                    reportedVarNames.add(resVar.getName());
                    addViolationWithMessage(data, reassigningStatement, REASSIGN_BEFORE_CLOSED_MESSAGE,
                            new Object[] { resVar.getName() });
                }
            }
        }
    }

    private Map<ASTVariableDeclaratorId, TypeNode> getResourceVariables(ASTMethodOrConstructorDeclaration method) {
        Map<ASTVariableDeclaratorId, TypeNode> resVars = new HashMap<>();

        if (method.getBody() == null) {
            return resVars;
        }

        List<ASTVariableDeclaratorId> vars = method.getBody().descendants(ASTVariableDeclaratorId.class)
            .filterNot(ASTVariableDeclaratorId::isFormalParameter)
            .filterNot(ASTVariableDeclaratorId::isExceptionBlockParameter)
            .filter(this::isVariableNotSpecifiedInTryWithResource)
            .filter(var -> isResourceTypeOrSubtype(var) || isNodeInstanceOfResourceType(getTypeOfVariable(var)))
            .filterNot(var -> var.isAnnotationPresent("lombok.Cleanup"))
            .toList();

        for (ASTVariableDeclaratorId var : vars) {
            TypeNode varType = getTypeOfVariable(var);
            resVars.put(var, varType);
        }
        return resVars;
    }

    private TypeNode getTypeOfVariable(ASTVariableDeclaratorId var) {
        TypeNode runtimeType = getRuntimeTypeOfVariable(var);
        return runtimeType != null ? runtimeType : var.getTypeNode();
    }

    private TypeNode getRuntimeTypeOfVariable(ASTVariableDeclaratorId var) {
        ASTExpression initExpr = var.getInitializer();
        return var.isTypeInferred() || isRuntimeType(initExpr) ? initExpr : null;
    }

    private boolean isRuntimeType(ASTExpression expr) {
        if (expr == null || isMethodCall(expr) || expr instanceof ASTNullLiteral) {
            return false;
        }

        @Nullable
        JTypeDeclSymbol symbol = expr.getTypeMirror().getSymbol();
        return symbol != null && !symbol.isUnresolved();
    }

    private TypeNode wrappedResourceTypeOrReturn(ASTVariableDeclaratorId var, TypeNode defaultVal) {
        TypeNode wrappedResType = getWrappedResourceType(var);
        return wrappedResType != null ? wrappedResType : defaultVal;
    }

    private TypeNode getWrappedResourceType(ASTVariableDeclaratorId var) {
        ASTExpression initExpr = initializerExpressionOf(var);
        if (initExpr != null) {
            ASTConstructorCall resAlloc = getLastResourceAllocation(initExpr);
            if (resAlloc != null) {
                ASTExpression firstArgRes = getFirstArgumentVariableIfResource(resAlloc);
                return firstArgRes != null ? firstArgRes : resAlloc;
            }
        }
        return null;
    }

    private ASTExpression initializerExpressionOf(ASTVariableDeclaratorId var) {
        return var.getInitializer();
    }

    private ASTConstructorCall getLastResourceAllocation(ASTExpression expr) {
        List<ASTConstructorCall> allocations = expr.descendantsOrSelf().filterIs(ASTConstructorCall.class).toList();
        int lastAllocIndex = allocations.size() - 1;
        for (int allocIndex = lastAllocIndex; allocIndex >= 0; allocIndex--) {
            ASTConstructorCall allocation = allocations.get(allocIndex);
            if (isResourceTypeOrSubtype(allocation)) {
                return allocation;
            }
        }
        return null;
    }

    private ASTExpression getFirstArgumentVariableIfResource(ASTConstructorCall allocation) {
        ASTArgumentList argsList = allocation.getArguments();
        if (argsList != null && argsList.size() > 0) {
            ASTExpression firstArg = argsList.get(0);
            return isNotMethodCall(firstArg) && isResourceTypeOrSubtype(firstArg)
                    ? firstArg
                    : null;
        }
        return null;
    }

    private boolean isNotMethodCall(ASTExpression expr) {
        return !isMethodCall(expr);
    }

    private boolean isMethodCall(ASTExpression expression) {
        return expression instanceof ASTMethodCall;
    }

    private boolean isWrappingResourceSpecifiedInTry(ASTVariableDeclaratorId var) {
        ASTVariableAccess wrappedVarName = getWrappedVariableName(var);
        if (wrappedVarName != null) {
            ASTVariableDeclaratorId referencedVar = wrappedVarName.getReferencedSym().tryGetNode();
            if (referencedVar != null) {
                List<ASTTryStatement> tryContainers = referencedVar.ancestors(ASTTryStatement.class).toList();
                for (ASTTryStatement tryContainer : tryContainers) {
                    if (isTryWithResourceSpecifyingVariable(tryContainer, referencedVar)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean shouldVarOfTypeBeClosedInMethod(ASTVariableDeclaratorId var, TypeNode type,
            ASTMethodOrConstructorDeclaration method) {
        return isNotAllowedResourceType(type) && isNotWrappingResourceMethodParameter(var, method)
                && isResourceVariableUnclosed(var);
    }

    private boolean isNotAllowedResourceType(TypeNode varType) {
        return !isAllowedResourceType(varType);
    }

    private boolean isAllowedResourceType(TypeNode refType) {
        List<String> allowedResourceTypes = getProperty(ALLOWED_RESOURCE_TYPES);
        if (allowedResourceTypes != null) {
            for (String type : allowedResourceTypes) {
                // the check here must be a exact type match, since subclasses may override close()
                // and actually require closing
                if (TypeTestUtil.isExactlyA(type, refType)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isNotWrappingResourceMethodParameter(ASTVariableDeclaratorId var,
            ASTMethodOrConstructorDeclaration method) {
        return !isWrappingResourceMethodParameter(var, method);
    }

    /**
     * Checks whether the variable is a resource and initialized from a method parameter.
     * @param var the resource variable that is being initialized
     * @param method the method or constructor in which the variable is declared
     * @return <code>true</code> if the variable is a resource and initialized from a method parameter. <code>false</code>
     *         otherwise.
     */
    private boolean isWrappingResourceMethodParameter(ASTVariableDeclaratorId var, ASTMethodOrConstructorDeclaration method) {
        ASTVariableAccess wrappedVarName = getWrappedVariableName(var);
        if (wrappedVarName != null) {
            ASTFormalParameters methodParams = method.getFormalParameters();
            for (ASTFormalParameter param : methodParams) {
                if ((isResourceTypeOrSubtype(param) || wrappedVarName.getParent() instanceof ASTVariableDeclarator
                        || wrappedVarName.getParent() instanceof ASTAssignmentExpression)
                    && JavaAstUtils.isReferenceToVar(wrappedVarName, param.getVarId().getSymbol())) {
                    return true;
                }
            }
        }
        return false;
    }

    private ASTVariableAccess getWrappedVariableName(ASTVariableDeclaratorId var) {
        ASTExpression initializer = var.getInitializer();
        if (initializer != null) {
            return var.getInitializer().descendantsOrSelf().filterIs(ASTVariableAccess.class)
                    .filter(usage -> !(usage.getParent() instanceof ASTMethodCall)).first();
        }
        return null;
    }

    private boolean isResourceTypeOrSubtype(TypeNode refType) {
        @Nullable
        JTypeDeclSymbol symbol = refType.getTypeMirror().getSymbol();
        return symbol != null && !symbol.isUnresolved()
                ? isNodeInstanceOfResourceType(refType)
                : nodeHasReferenceToResourceType(refType);
    }

    private boolean isNodeInstanceOfResourceType(TypeNode refType) {
        for (String resType : types) {
            if (TypeTestUtil.isA(resType, refType)) {
                return true;
            }
        }
        return false;
    }

    private boolean nodeHasReferenceToResourceType(TypeNode refType) {
        @Nullable
        JTypeDeclSymbol symbol = refType.getTypeMirror().getSymbol();
        if (symbol != null) {
            String simpleTypeName = symbol.getSimpleName();
            return isResourceTypeName(simpleTypeName);
        }
        return false;
    }

    private boolean isResourceTypeName(String typeName) {
        String simpleTypeName = toSimpleType(typeName);
        return types.contains(typeName) || simpleTypes.contains(simpleTypeName);
    }

    private boolean isResourceVariableUnclosed(ASTVariableDeclaratorId var) {
        return !isResourceVariableClosed(var);
    }

    private boolean isResourceVariableClosed(ASTVariableDeclaratorId var) {
        Node methodOfVar = getMethodOfNode(var);
        return hasTryStatementClosingResourceVariable(methodOfVar, var)
                || isReturnedByMethod(var, methodOfVar);
    }

    private Node getMethodOfNode(Node node) {
        Node parent = node.getParent();
        while (isNotMethod(parent)) {
            parent = parent.getParent();
        }
        return parent;
    }

    private boolean isNotMethod(Node node) {
        return !(node instanceof ASTBlock || node instanceof ASTConstructorDeclaration);
    }

    private boolean hasTryStatementClosingResourceVariable(Node node, ASTVariableDeclaratorId var) {
        List<ASTTryStatement> tryStatements = node.descendants(ASTTryStatement.class).crossFindBoundaries().toList();
        for (ASTTryStatement tryStatement : tryStatements) {
            if (tryStatementClosesResourceVariable(tryStatement, var)) {
                return true;
            }
        }
        return false;
    }

    private boolean tryStatementClosesResourceVariable(ASTTryStatement tryStatement, ASTVariableDeclaratorId var) {
        if (tryStatement.getBeginLine() >= var.getBeginLine() && noneCriticalStatementsBetween(var, tryStatement)) {
            if (isTryWithResourceSpecifyingVariable(tryStatement, var)) {
                return true;
            }
            if (hasFinallyClause(tryStatement)) {
                ASTBlock finallyBody = tryStatement.getFinallyClause().getBody();
                return blockClosesResourceVariable(finallyBody, var);
            }
        }
        return false;
    }

    private boolean noneCriticalStatementsBetween(ASTVariableDeclaratorId var, ASTTryStatement tryStatement) {
        return !anyCriticalStatementBetween(var, tryStatement);
    }

    private boolean anyCriticalStatementBetween(ASTVariableDeclaratorId var, ASTTryStatement tryStatement) {
        ASTStatement varStatement = var.ancestors(ASTStatement.class).first();
        if (isNotNullInitialized(var) && areStatementsOfSameBlock(varStatement, tryStatement)) {
            for (ASTStatement bsBetween : getBlockStatementsBetween(varStatement, tryStatement)) {
                if (isCriticalStatement(bsBetween)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isNotNullInitialized(ASTVariableDeclaratorId var) {
        return !hasNullInitializer(var);
    }

    private boolean hasNullInitializer(ASTVariableDeclaratorId var) {
        return var.getInitializer() instanceof ASTNullLiteral;
    }

    private boolean areStatementsOfSameBlock(ASTStatement bs0, ASTStatement bs1) {
        return bs0.getParent() == bs1.getParent();
    }

    private List<ASTStatement> getBlockStatementsBetween(ASTStatement top, ASTStatement bottom) {
        List<ASTStatement> blockStatements = top.getParent().children(ASTStatement.class).toList();
        int topIndex = blockStatements.indexOf(top);
        int bottomIndex = blockStatements.indexOf(bottom);
        return blockStatements.subList(topIndex + 1, bottomIndex);
    }

    private boolean isCriticalStatement(ASTStatement blockStatement) {
        boolean isVarDeclaration = blockStatement.descendantsOrSelf().filterIs(ASTLocalVariableDeclaration.class).nonEmpty();
        boolean isAssignmentOperator = blockStatement.descendantsOrSelf().filterIs(ASTAssignmentExpression.class).nonEmpty();
        return !isVarDeclaration && !isAssignmentOperator;
    }

    private boolean isTryWithResourceSpecifyingVariable(ASTTryStatement tryStatement, ASTVariableDeclaratorId varId) {
        return tryStatement.isTryWithResources() && isVariableSpecifiedInTryWithResource(varId, tryStatement);
    }

    private boolean isVariableNotSpecifiedInTryWithResource(ASTVariableDeclaratorId varId) {
        @Nullable
        ASTTryStatement tryStatement = varId.ancestors(ASTTryStatement.class)
            .filter(ASTTryStatement::isTryWithResources)
            .first();
        return tryStatement == null || !isVariableSpecifiedInTryWithResource(varId, tryStatement);
    }

    private boolean isVariableSpecifiedInTryWithResource(ASTVariableDeclaratorId varId, ASTTryStatement tryWithResource) {
        // skip own resources - these are definitively closed
        if (tryWithResource.getResources().descendants(ASTVariableDeclaratorId.class).toList().contains(varId)) {
            return true;
        }

        List<ASTVariableAccess> usedVars = getResourcesSpecifiedInTryWith(tryWithResource);
        for (ASTVariableAccess res : usedVars) {
            if (JavaAstUtils.isReferenceToVar(res, varId.getSymbol())) {
                return true;
            }
        }
        return false;
    }

    private List<ASTVariableAccess> getResourcesSpecifiedInTryWith(ASTTryStatement tryWithResource) {
        return tryWithResource.getResources().descendantsOrSelf().filterIs(ASTVariableAccess.class).toList();
    }

    private boolean hasFinallyClause(ASTTryStatement tryStatement) {
        return tryStatement.getFinallyClause() != null;
    }

    private boolean blockClosesResourceVariable(ASTBlock block, ASTVariableDeclaratorId variableToClose) {
        return hasNotConditionalCloseCallOnVariable(block, variableToClose)
                || hasMethodCallClosingResourceVariable(block, variableToClose);
    }

    private boolean hasNotConditionalCloseCallOnVariable(ASTBlock block, ASTVariableDeclaratorId variableToClose) {
        List<ASTMethodCall> methodCallsOnVariable = block.descendants(ASTMethodCall.class)
            .filter(call -> isMethodCallOnVariable(call, variableToClose))
            .toList();

        for (ASTMethodCall call : methodCallsOnVariable) {
            if (isCloseTargetMethodCall(call) && isNotConditional(block, call, variableToClose)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isMethodCallOnVariable(ASTExpression expr, ASTVariableDeclaratorId variable) {
        if (expr instanceof ASTMethodCall) {
            ASTMethodCall methodCall = (ASTMethodCall) expr;
            return JavaAstUtils.isReferenceToVar(methodCall.getQualifier(), variable.getSymbol());
        }
        return false;
    }

    /**
     * Checks, whether the given node is inside an if condition, and if so,
     * whether this is a null check for the given varName.
     *
     * @param enclosingBlock
     *            where to search for if statements
     * @param node
     *            the node, where the call for the close is done
     * @param varName
     *            the variable, that is maybe null-checked
     * @return <code>true</code> if no if condition is involved or if the if
     *         condition is a null-check.
     */
    private boolean isNotConditional(ASTBlock enclosingBlock, Node node, ASTVariableDeclaratorId var) {
        ASTIfStatement ifStatement = findIfStatement(enclosingBlock, node);
        if (ifStatement != null) {
            // find expressions like: varName != null or null != varName
            if (ifStatement.getCondition() instanceof ASTInfixExpression) {
                ASTInfixExpression equalityExpr = (ASTInfixExpression) ifStatement.getCondition();
                if (BinaryOp.NE == equalityExpr.getOperator()) {
                    ASTExpression left = equalityExpr.getLeftOperand();
                    ASTExpression right = equalityExpr.getRightOperand();

                    if (JavaAstUtils.isReferenceToVar(left, var.getSymbol()) && isNullLiteral(right)
                            || JavaAstUtils.isReferenceToVar(right, var.getSymbol()) && isNullLiteral(left)) {
                        return true;
                    }
                }
            }

            // find method call Objects.nonNull(varName)
            return isObjectsNonNull(ifStatement.getCondition(), var);
        }
        return true;
    }

    private boolean isObjectsNonNull(ASTExpression expression, ASTVariableDeclaratorId var) {
        InvocationMatcher matcher = InvocationMatcher.parse("java.util.Objects#nonNull(_)");
        if (matcher.matchesCall(expression)) {
            ASTMethodCall methodCall = (ASTMethodCall) expression;
            return JavaAstUtils.isReferenceToVar(methodCall.getArguments().get(0), var.getSymbol());
        }

        return false;
    }

    private boolean isNullLiteral(JavaNode node) {
        return node instanceof ASTNullLiteral;
    }

    private ASTIfStatement findIfStatement(ASTBlock enclosingBlock, Node node) {
        ASTIfStatement ifStatement = node.ancestors(ASTIfStatement.class).first();
        List<ASTIfStatement> allIfStatements = enclosingBlock.descendants(ASTIfStatement.class).toList();
        if (ifStatement != null && allIfStatements.contains(ifStatement)) {
            return ifStatement;
        }
        return null;
    }

    private boolean hasMethodCallClosingResourceVariable(ASTBlock block, ASTVariableDeclaratorId variableToClose) {
        List<ASTMethodCall> methodCalls = block.descendants(ASTMethodCall.class).crossFindBoundaries().toList();
        for (ASTMethodCall call : methodCalls) {
            if (isMethodCallClosingResourceVariable(call, variableToClose)) {
                return true;
            }
        }
        return false;
    }

    private boolean isMethodCallClosingResourceVariable(ASTExpression expr, ASTVariableDeclaratorId variableToClose) {
        if (!(expr instanceof ASTMethodCall)) {
            return false;
        }
        ASTMethodCall call = (ASTMethodCall) expr;
        return (isCloseTargetMethodCall(call) || hasChainedCloseTargetMethodCall(call))
                && variableIsPassedToMethod(variableToClose, call);
    }

    private boolean isCloseTargetMethodCall(ASTMethodCall methodCall) {
        String fullName = methodCall.getMethodName();
        if (methodCall.getQualifier() instanceof ASTTypeExpression) {
            fullName = methodCall.getQualifier().getText() + "." + fullName;
        }
        return closeTargets.contains(fullName);
    }

    private boolean hasChainedCloseTargetMethodCall(ASTMethodCall start) {
        ASTExpression walker = start;
        while (walker instanceof ASTMethodCall) {
            ASTMethodCall methodCall = (ASTMethodCall) walker;
            if (isCloseTargetMethodCall(methodCall)) {
                return true;
            }
            walker = methodCall.getQualifier();
        }
        return false;
    }

    private boolean variableIsPassedToMethod(ASTVariableDeclaratorId varName, ASTMethodCall methodCall) {
        List<ASTNamedReferenceExpr> usedRefs = methodCall.getArguments().descendants(ASTNamedReferenceExpr.class).toList();
        for (ASTNamedReferenceExpr ref : usedRefs) {
            if (varName.getSymbol().equals(ref.getReferencedSym())) {
                return true;
            }
        }
        return false;
    }

    private boolean isReturnedByMethod(ASTVariableDeclaratorId variable, Node method) {
        return method
                .descendants(ASTReturnStatement.class).crossFindBoundaries()
                .descendants(ASTVariableAccess.class)
                .filter(access -> !(access.getParent() instanceof ASTMethodCall))
                .filter(access -> JavaAstUtils.isReferenceToVar(access, variable.getSymbol()))
                .nonEmpty();
    }

    private void addCloseResourceViolation(ASTVariableDeclaratorId id, TypeNode type, Object data) {
        String resTypeName = getResourceTypeName(id, type);
        addViolation(data, id, resTypeName);
    }

    private String getResourceTypeName(ASTVariableDeclaratorId varId, TypeNode type) {
        if (type instanceof ASTType) {
            return PrettyPrintingUtil.prettyPrintType((ASTType) type);
        }
        @Nullable
        JTypeDeclSymbol symbol = type.getTypeMirror().getSymbol();
        if (symbol != null) {
            return symbol.getSimpleName();
        }
        @Nullable
        ASTLocalVariableDeclaration localVarDecl = varId.ancestors(ASTLocalVariableDeclaration.class).first();
        if (localVarDecl != null && localVarDecl.getTypeNode() != null) {
            return PrettyPrintingUtil.prettyPrintType(localVarDecl.getTypeNode());
        }
        return varId.getName();
    }

    @Override
    public Object visit(ASTMethodCall node, Object data) {
        if (!getProperty(DETECT_CLOSE_NOT_IN_FINALLY)) {
            return super.visit(node, data);
        }

        if (isCloseTargetMethodCall(node) && node.getQualifier() instanceof ASTVariableAccess) {
            ASTVariableAccess closedVar = (ASTVariableAccess) node.getQualifier();
            if (isNotInFinallyBlock(closedVar) && !reportedVarNames.contains(closedVar.getName())) {
                addViolationWithMessage(data, closedVar, CLOSE_IN_FINALLY_BLOCK_MESSAGE,
                        new Object[] { closedVar.getName() });
            }
        }

        return super.visit(node, data);
    }

    private boolean isNotInFinallyBlock(ASTVariableAccess closedVar) {
        return closedVar.ancestors(ASTFinallyClause.class).isEmpty();
    }

    private ASTExpressionStatement getFirstReassigningStatementBeforeBeingClosed(ASTVariableDeclaratorId variable, ASTMethodOrConstructorDeclaration methodOrConstructor) {
        List<ASTExpressionStatement> statements = methodOrConstructor.descendants(ASTExpressionStatement.class).toList();
        boolean variableClosed = false;
        boolean isInitialized = !hasNullInitializer(variable);
        ASTExpression initializingExpression = initializerExpressionOf(variable);
        for (ASTExpressionStatement statement : statements) {
            if (isClosingVariableStatement(statement, variable)) {
                variableClosed = true;
            }

            if (isAssignmentForVariable(statement, variable)) {
                ASTAssignmentExpression assignment = (ASTAssignmentExpression) statement.getFirstChild();
                if (isInitialized && !variableClosed) {
                    if (initializingExpression != null && !inSameIfBlock(statement, initializingExpression)
                            && notInNullCheckIf(statement, variable)
                            && isNotSelfAssignment(assignment)) {
                        return statement;
                    }
                }

                if (variableClosed) {
                    variableClosed = false;
                } 
                if (!isInitialized) {
                    isInitialized = true;
                    initializingExpression = statement.getExpr();
                }
            }
        }
        return null;
    }

    private boolean isNotSelfAssignment(ASTAssignmentExpression assignment) {
        return assignment.getRightOperand().descendantsOrSelf().filterIs(ASTVariableAccess.class).filter(access -> {
            return JavaAstUtils.isReferenceToSameVar(access, assignment.getLeftOperand());
        }).isEmpty();
    }

    private boolean notInNullCheckIf(ASTExpressionStatement statement, ASTVariableDeclaratorId variable) {
        Node grandparent = statement.ancestors().get(1);
        if (grandparent instanceof ASTIfStatement) {
            ASTIfStatement ifStatement = (ASTIfStatement) grandparent;
            if (JavaRuleUtil.isNullCheck(ifStatement.getCondition(), variable.getSymbol())) {
                return false;
            }
        }
        return true;
    }

    private boolean inSameIfBlock(ASTExpressionStatement statement1, ASTExpression statement2) {
        List<ASTIfStatement> parents1 = statement1.ancestors(ASTIfStatement.class).toList();
        List<ASTIfStatement> parents2 = statement2.ancestors(ASTIfStatement.class).toList();
        parents1.retainAll(parents2);
        return !parents1.isEmpty();
    }

    private boolean isClosingVariableStatement(ASTExpressionStatement statement, ASTVariableDeclaratorId variable) {
        return isMethodCallClosingResourceVariable(statement.getExpr(), variable)
                || isMethodCallOnVariable(statement.getExpr(), variable);
    }

    private boolean isAssignmentForVariable(ASTExpressionStatement statement, ASTVariableDeclaratorId variable) {
        if (statement == null || variable == null || !(statement.getExpr() instanceof ASTAssignmentExpression)) {
            return false;
        }

        ASTAssignmentExpression assignment = (ASTAssignmentExpression) statement.getExpr();
        return JavaAstUtils.isReferenceToVar(assignment.getLeftOperand(), variable.getSymbol());
    }
}
