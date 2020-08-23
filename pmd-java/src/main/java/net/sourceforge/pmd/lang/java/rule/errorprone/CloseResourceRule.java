/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;
import static net.sourceforge.pmd.properties.PropertyFactory.stringListProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jaxen.JaxenException;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFinallyStatement;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTResourceSpecification;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
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

    private static final String WRAPPING_TRY_WITH_RES_VAR_MESSAGE = "it is recommended to wrap resource in try-with-resource declaration directly";
    private static final String CLOSE_IN_FINALLY_BLOCK_MESSAGE = "'' is not closed within a finally block, thus might not be closed at all in case of exceptions";

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
        Map<ASTVariableDeclarator, TypeNode> resVars = getResourceVariables(methodOrConstructor);
        for (Map.Entry<ASTVariableDeclarator, TypeNode> resVarEntry : resVars.entrySet()) {
            ASTVariableDeclarator resVar = resVarEntry.getKey();
            TypeNode resVarType = resVarEntry.getValue();
            if (isWrappingResourceSpecifiedInTry(resVar)) {
                reportedVarNames.add(resVar.getVariableId().getName());
                addViolationWithMessage(data, resVar, WRAPPING_TRY_WITH_RES_VAR_MESSAGE);
            } else if (shouldVarOfTypeBeClosedInMethod(resVar, resVarType, methodOrConstructor)) {
                reportedVarNames.add(resVar.getVariableId().getName());
                addCloseResourceViolation(resVar.getVariableId(), resVarType, data);
            }
        }
    }

    private Map<ASTVariableDeclarator, TypeNode> getResourceVariables(ASTMethodOrConstructorDeclaration method) {
        List<ASTVariableDeclarator> vars = method.findDescendantsOfType(ASTVariableDeclarator.class);
        Map<ASTVariableDeclarator, TypeNode> resVars = new HashMap<>();
        for (ASTVariableDeclarator var : vars) {
            TypeNode varType = getTypeOfVariable(var);
            if (isResourceTypeOrSubtype(varType)) {
                resVars.put(var, wrappedResourceTypeOrReturn(var, varType));
            }
        }
        return resVars;
    }

    private TypeNode getTypeOfVariable(ASTVariableDeclarator var) {
        TypeNode runtimeType = getRuntimeTypeOfVariable(var);
        return runtimeType != null ? runtimeType : getDeclaredTypeOfVariable(var);
    }

    private TypeNode getDeclaredTypeOfVariable(ASTVariableDeclarator var) {
        ASTLocalVariableDeclaration localVar = (ASTLocalVariableDeclaration) var.getParent();
        return localVar.getTypeNode();
    }

    private TypeNode getRuntimeTypeOfVariable(ASTVariableDeclarator var) {
        ASTExpression initExpr = initializerExpressionOf(var);
        return isRuntimeType(initExpr) ? initExpr : null;
    }

    private boolean isRuntimeType(ASTExpression expr) {
        return expr != null && isNotMethodCall(expr) && expr.getType() != null;
    }

    private TypeNode wrappedResourceTypeOrReturn(ASTVariableDeclarator var, TypeNode defaultVal) {
        TypeNode wrappedResType = getWrappedResourceType(var);
        return wrappedResType != null ? wrappedResType : defaultVal;
    }

    private TypeNode getWrappedResourceType(ASTVariableDeclarator var) {
        ASTExpression initExpr = initializerExpressionOf(var);
        if (initExpr != null) {
            ASTAllocationExpression resAlloc = getLastResourceAllocation(initExpr);
            if (resAlloc != null) {
                ASTExpression firstArgRes = getFirstArgumentVariableIfResource(resAlloc);
                return firstArgRes != null ? firstArgRes : resAlloc;
            }
        }
        return null;
    }

    private ASTExpression initializerExpressionOf(ASTVariableDeclarator var) {
        return var.hasInitializer()
                ? var.getInitializer().getFirstChildOfType(ASTExpression.class)
                : null;
    }

    private ASTAllocationExpression getLastResourceAllocation(ASTExpression expr) {
        List<ASTAllocationExpression> allocations = expr.findDescendantsOfType(ASTAllocationExpression.class);
        int lastAllocIndex = allocations.size() - 1;
        for (int allocIndex = lastAllocIndex; allocIndex >= 0; allocIndex--) {
            ASTAllocationExpression allocation = allocations.get(allocIndex);
            if (isResourceTypeOrSubtype(allocation)) {
                return allocation;
            }
        }
        return null;
    }

    private ASTExpression getFirstArgumentVariableIfResource(ASTAllocationExpression allocation) {
        ASTArgumentList argsList = allocation.getFirstDescendantOfType(ASTArgumentList.class);
        if (argsList != null) {
            ASTExpression firstArg = argsList.getFirstChildOfType(ASTExpression.class);
            return firstArg != null && isNotMethodCall(firstArg) && isResourceTypeOrSubtype(firstArg)
                    ? firstArg
                    : null;
        }
        return null;
    }

    private boolean isNotMethodCall(ASTExpression expr) {
        return !isMethodCall(expr);
    }

    private boolean isMethodCall(ASTExpression expression) {
        if (expression != null) {
            ASTPrimaryExpression primaryExpression = expression.getFirstChildOfType(ASTPrimaryExpression.class);
            return primaryExpression != null && primaryExpression.getFirstChildOfType(ASTPrimarySuffix.class) != null;
        }
        return false;
    }

    private boolean isWrappingResourceSpecifiedInTry(ASTVariableDeclarator var) {
        String wrappedVarName = getWrappedVariableName(var);
        if (wrappedVarName != null) {
            List<ASTTryStatement> tryContainers = var.getParentsOfType(ASTTryStatement.class);
            for (ASTTryStatement tryContainer : tryContainers) {
                if (isTryWithResourceSpecifyingVariable(tryContainer, wrappedVarName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean shouldVarOfTypeBeClosedInMethod(ASTVariableDeclarator var, TypeNode type,
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

    private boolean isNotWrappingResourceMethodParameter(ASTVariableDeclarator var,
            ASTMethodOrConstructorDeclaration method) {
        return !isWrappingResourceMethodParameter(var, method);
    }

    /**
     * Checks whether the variable is resource and initialized from a method parameter.
     * @param var the resource variable that is being initialized
     * @param method the method or constructor in which the variable is declared
     * @return <code>true</code> if the variable is resource and initialized from a method parameter. <code>false</code>
     *         otherwise.
     */
    private boolean isWrappingResourceMethodParameter(ASTVariableDeclarator var, ASTMethodOrConstructorDeclaration method) {
        String wrappedVarName = getWrappedVariableName(var);
        if (wrappedVarName != null) {
            ASTFormalParameters methodParams = method.getFirstDescendantOfType(ASTFormalParameters.class);
            if (methodParams != null) {
                List<ASTVariableDeclaratorId> ids = methodParams.findDescendantsOfType(ASTVariableDeclaratorId.class);
                for (ASTVariableDeclaratorId id : ids) {
                    if (id.hasImageEqualTo(wrappedVarName) && isResourceTypeOrSubtype(id)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private String getWrappedVariableName(ASTVariableDeclarator var) {
        if (var.hasInitializer()) {
            ASTName varName = var.getInitializer().getFirstDescendantOfType(ASTName.class);
            return varName != null ? varName.getImage() : null;
        }
        return null;
    }

    private boolean isResourceTypeOrSubtype(TypeNode refType) {
        return refType.getType() != null
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
        ASTClassOrInterfaceType type = refType.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
        return type != null && isResourceTypeName(type.getImage()) && !type.isReferenceToClassSameCompilationUnit();
    }

    private boolean isResourceTypeName(String typeName) {
        String simpleTypeName = toSimpleType(typeName);
        return types.contains(typeName) || simpleTypes.contains(simpleTypeName);
    }

    private boolean isResourceVariableUnclosed(ASTVariableDeclarator var) {
        return !isResourceVariableClosed(var);
    }

    private boolean isResourceVariableClosed(ASTVariableDeclarator var) {
        Node methodOfVar = getMethodOfNode(var);
        return hasTryStatementClosingResourceVariable(methodOfVar, var)
                || isReturnedByMethod(var.getName(), methodOfVar);
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

    private boolean hasTryStatementClosingResourceVariable(Node node, ASTVariableDeclarator var) {
        List<ASTTryStatement> tryStatements = node.findDescendantsOfType(ASTTryStatement.class, true);
        for (ASTTryStatement tryStatement : tryStatements) {
            if (tryStatementClosesResourceVariable(tryStatement, var)) {
                return true;
            }
        }
        return false;
    }

    private boolean tryStatementClosesResourceVariable(ASTTryStatement tryStatement, ASTVariableDeclarator var) {
        if (tryStatement.getBeginLine() >= var.getBeginLine() && noneCriticalStatementsBetween(var, tryStatement)) {
            if (isTryWithResourceSpecifyingVariable(tryStatement, var.getName())) {
                return true;
            }
            if (hasFinallyClause(tryStatement)) {
                ASTBlock finallyBody = tryStatement.getFinallyClause().getBody();
                return blockClosesResourceVariable(finallyBody, var.getName());
            }
        }
        return false;
    }

    private boolean noneCriticalStatementsBetween(ASTVariableDeclarator var, ASTTryStatement tryStatement) {
        return !anyCriticalStatementBetween(var, tryStatement);
    }

    private boolean anyCriticalStatementBetween(ASTVariableDeclarator var, ASTTryStatement tryStatement) {
        ASTBlockStatement varBlockStatement = var.getFirstParentOfType(ASTBlockStatement.class);
        ASTBlockStatement tryBlockStatement = tryStatement.getFirstParentOfType(ASTBlockStatement.class);
        if (isNotNullInitialized(var) && areStatementsOfSameBlock(varBlockStatement, tryBlockStatement)) {
            for (ASTBlockStatement bsBetween : getBlockStatementsBetween(varBlockStatement, tryBlockStatement)) {
                if (isCriticalStatement(bsBetween)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isNotNullInitialized(ASTVariableDeclarator var) {
        return !hasNullInitializer(var);
    }

    private boolean hasNullInitializer(ASTVariableDeclarator var) {
        if (var.hasInitializer()) {
            ASTPrimaryPrefix primaryPrefix = var.getInitializer().getFirstDescendantOfType(ASTPrimaryPrefix.class);
            return primaryPrefix != null && primaryPrefix.hasDescendantOfType(ASTNullLiteral.class);
        }
        return false;
    }

    private boolean areStatementsOfSameBlock(ASTBlockStatement bs0, ASTBlockStatement bs1) {
        return bs0.getParent() == bs1.getParent();
    }

    private List<ASTBlockStatement> getBlockStatementsBetween(ASTBlockStatement top, ASTBlockStatement bottom) {
        List<ASTBlockStatement> blockStatements = top.getParent().findChildrenOfType(ASTBlockStatement.class);
        int topBSIndex = blockStatements.indexOf(top);
        int bottomBSIndex = blockStatements.indexOf(bottom);
        return blockStatements.subList(topBSIndex + 1, bottomBSIndex);
    }

    private boolean isCriticalStatement(ASTBlockStatement blockStatement) {
        boolean isVarDeclaration = blockStatement.hasDescendantOfType(ASTLocalVariableDeclaration.class);
        boolean isAssignmentOperator = blockStatement.hasDescendantOfType(ASTAssignmentOperator.class);
        return !isVarDeclaration && !isAssignmentOperator;
    }

    private boolean isTryWithResourceSpecifyingVariable(ASTTryStatement tryStatement, String varName) {
        return tryStatement.isTryWithResources() && isVariableSpecifiedInTryWithResource(varName, tryStatement);
    }

    private boolean isVariableSpecifiedInTryWithResource(String varName, ASTTryStatement tryWithResource) {
        List<JavaNode> specifiedResources = getResourcesSpecifiedInTryWith(tryWithResource);
        for (JavaNode res : specifiedResources) {
            if (res.hasImageEqualTo(varName)) {
                return true;
            }
        }
        return false;
    }

    private List<JavaNode> getResourcesSpecifiedInTryWith(ASTTryStatement tryWithResource) {
        ASTResourceSpecification resSpecification = tryWithResource.getFirstChildOfType(ASTResourceSpecification.class);
        List<ASTVariableDeclaratorId> initializedVars = resSpecification
                .findDescendantsOfType(ASTVariableDeclaratorId.class);
        List<ASTName> specifiedVars = resSpecification.findDescendantsOfType(ASTName.class);
        return combineNodeLists(initializedVars, specifiedVars);
    }

    private List<JavaNode> combineNodeLists(List<? extends JavaNode> list0, List<? extends JavaNode> list1) {
        List<JavaNode> nodeList = new ArrayList<>(list0);
        nodeList.addAll(list1);
        return nodeList;
    }

    private boolean hasFinallyClause(ASTTryStatement tryStatement) {
        return tryStatement.getFinallyClause() != null;
    }

    private boolean blockClosesResourceVariable(ASTBlock block, String variableToClose) {
        return hasNotConditionalCloseCallOnVariable(block, variableToClose)
                || hasMethodCallClosingResourceVariable(block, variableToClose);
    }

    private boolean hasNotConditionalCloseCallOnVariable(ASTBlock block, String variableToClose) {
        List<ASTName> operations = block.findDescendantsOfType(ASTName.class);
        for (ASTName operation : operations) {
            if (isCloseCallOnVariable(operation, variableToClose)
                    && isNotConditional(block, operation, variableToClose)) {
                return true;
            }
        }
        return false;
    }

    private boolean isCloseCallOnVariable(ASTName op, String variableToClose) {
        String closedVar = getVariableClosedByMethodCall(op);
        return variableToClose.equals(closedVar);
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
    private boolean isNotConditional(ASTBlock enclosingBlock, Node node, String varName) {
        ASTIfStatement ifStatement = findIfStatement(enclosingBlock, node);
        if (ifStatement != null) {
            try {
                // find expressions like: varName != null or null != varName
                List<?> nodes = ifStatement.findChildNodesWithXPath("Expression/EqualityExpression[@Image='!=']"
                        + "  [PrimaryExpression/PrimaryPrefix/Name[@Image='" + varName + "']]"
                        + "  [PrimaryExpression/PrimaryPrefix/Literal/NullLiteral]");
                return !nodes.isEmpty();
            } catch (JaxenException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    private ASTIfStatement findIfStatement(ASTBlock enclosingBlock, Node node) {
        ASTIfStatement ifStatement = node.getFirstParentOfType(ASTIfStatement.class);
        List<ASTIfStatement> allIfStatements = enclosingBlock.findDescendantsOfType(ASTIfStatement.class);
        if (ifStatement != null && allIfStatements.contains(ifStatement)) {
            return ifStatement;
        }
        return null;
    }

    private boolean hasMethodCallClosingResourceVariable(ASTBlock block, String variableToClose) {
        List<ASTPrimaryExpression> expressions = block.findDescendantsOfType(ASTPrimaryExpression.class, true);
        for (ASTPrimaryExpression expression : expressions) {
            if (isMethodCallClosingResourceVariable(expression, variableToClose)) {
                return true;
            }
        }
        return false;
    }

    private boolean isMethodCallClosingResourceVariable(ASTPrimaryExpression expression, String variableToClose) {
        ASTPrimaryPrefix prefix = expression.getFirstDescendantOfType(ASTPrimaryPrefix.class);
        ASTPrimarySuffix suffix = expression.getFirstDescendantOfType(ASTPrimarySuffix.class);
        if (prefix != null && suffix != null) {
            return (isCloseTargetMethodCall(prefix, suffix) || hasChainedCloseTargetMethodCall(expression))
                    && variableIsPassedToMethod(variableToClose, expression);
        }
        return false;
    }

    private boolean isCloseTargetMethodCall(ASTPrimaryPrefix prefix, ASTPrimarySuffix suffix) {
        String methodCall = getMethodCallStr(prefix, suffix);
        return methodCall != null && closeTargets.contains(methodCall);
    }

    private String getMethodCallStr(ASTPrimaryPrefix prefix, ASTPrimarySuffix suffix) {
        if (prefix.getImage() == null) {
            ASTName name = prefix.getFirstDescendantOfType(ASTName.class);
            return name != null ? name.getImage() : null;
        } else if (suffix.getImage() != null) {
            return prefix.getImage() + "." + suffix.getImage();
        }
        return null;
    }

    private boolean hasChainedCloseTargetMethodCall(ASTPrimaryExpression expr) {
        List<ASTPrimarySuffix> methodCalls = expr.findDescendantsOfType(ASTPrimarySuffix.class, true);
        for (ASTPrimarySuffix methodCall : methodCalls) {
            if (closeTargets.contains(methodCall.getImage())) {
                return true;
            }
        }
        return false;
    }

    private boolean variableIsPassedToMethod(String varName, ASTPrimaryExpression methodCall) {
        List<ASTName> methodCallArgs = methodCall.findDescendantsOfType(ASTName.class, true);
        for (ASTName methodCallArg : methodCallArgs) {
            if (isMethodCallArgument(methodCallArg) && methodCallArg.hasImageEqualTo(varName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isMethodCallArgument(ASTName varName) {
        return varName.getFirstParentOfType(ASTArgumentList.class) != null;
    }

    private boolean isReturnedByMethod(String varName, Node method) {
        List<ASTReturnStatement> returns = method.findDescendantsOfType(ASTReturnStatement.class, true);
        for (ASTReturnStatement returnStatement : returns) {
            ASTName name = returnStatement.getFirstDescendantOfType(ASTName.class);
            if (name != null && name.hasImageEqualTo(varName)) {
                return true;
            }
        }
        return false;
    }

    private void addCloseResourceViolation(ASTVariableDeclaratorId id, TypeNode type, Object data) {
        String resTypeName = getResourceTypeName(id, type);
        addViolation(data, id, resTypeName);
    }

    private String getResourceTypeName(ASTVariableDeclaratorId varId, TypeNode type) {
        Class<?> typeClass = type.getType();
        if (typeClass == null) {
            ASTLocalVariableDeclaration localVarDecl = varId.getFirstParentOfType(ASTLocalVariableDeclaration.class);
            return localVarDecl != null && localVarDecl.getTypeNode() != null
                    ? localVarDecl.getTypeNode().getTypeImage()
                    : varId.getName();
        }
        return typeClass.getSimpleName();
    }

    @Override
    public Object visit(ASTPrimaryPrefix prefix, Object data) {
        if (!getProperty(DETECT_CLOSE_NOT_IN_FINALLY)) {
            return super.visit(prefix, data);
        }

        ASTName methodCall = prefix.getFirstChildOfType(ASTName.class);
        if (methodCall != null && isNodeInstanceOfResourceType(methodCall)) {
            String closedVar = getVariableClosedByMethodCall(methodCall);
            if (closedVar != null && isNotInFinallyBlock(prefix) && !reportedVarNames.contains(closedVar)) {
                String violationMsg = closeInFinallyBlockMessageForVar(closedVar);
                addViolationWithMessage(data, prefix, violationMsg);
            }
        }
        return super.visit(prefix, data);
    }

    private String getVariableClosedByMethodCall(ASTName methodCall) {
        String[] callParts = getMethodCallParts(methodCall);
        if (callParts != null) {
            String varName = callParts[0];
            String methodName = callParts[1];
            return closeTargets.contains(methodName) ? varName : null;
        }
        return null;
    }

    private String[] getMethodCallParts(ASTName methodCall) {
        String methodCallStr = methodCall.getImage();
        return methodCallStr != null && methodCallStr.contains(".")
                ? methodCallStr.split("\\.")
                : null;
    }

    private boolean isNotInFinallyBlock(ASTPrimaryPrefix prefix) {
        return prefix.getFirstParentOfType(ASTFinallyStatement.class) == null;
    }

    private String closeInFinallyBlockMessageForVar(String var) {
        return "''" + var + CLOSE_IN_FINALLY_BLOCK_MESSAGE;
    }
}
