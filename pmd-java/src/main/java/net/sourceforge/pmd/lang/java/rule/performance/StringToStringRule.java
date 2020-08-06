/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArguments;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodReference;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.JavaNameOccurrence;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.symboltable.ScopedNode;

/**
 *  Finds toString() call on String object.
 *
 *  <b>Note:</b> due to an issue with type resolution, this implementation doesn't detect cases when toString()
 *  call is chained to a method returning String which is not declared in the class having the call or the call
 *  arguments are not of the exact same type as method parameters are, excluding the case when method name and
 *  number of it's parameters is enough to identify the method. Example:
 *    <pre>{@code
 *    class A {
 *         public String str() {
 *            return "exampleStr";
 *         }
 *    }
 *    class B {
 *        public void foo() {
 *            String s = new A().str().toString(); // not detected because str() is from another class
 *            s = getString().toString(); // detected
 *            s = getData(new FileInputStream()).toString(); // detected because of argument type
 *            s = getData(new Integer(4), new Integer(5)).toString(); // detected because of unique args count
 *        }
 *        public String getString() {
 *            return "exampleStr";
 *        }
 *        public String getData(InputStream is) {
 *            return "argsResolutionIssueExample";
 *        }
 *        public int getData(String s) {
 *            return 0;
 *        }
 *        public String getData(Number a, Number b) {
 *            return "uniqueArgsCountExample";
 *        }
 *    }
 *    }</pre>
 */
public class StringToStringRule extends AbstractJavaRule {

    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER_MAP;

    private final Set<ASTMethodDeclaration> declaredMethods = new HashSet<>();

    static {
        Map<Class<?>, Class<?>> primitiveToWrapper = new HashMap<>();
        primitiveToWrapper.put(Byte.TYPE, Byte.class);
        primitiveToWrapper.put(Short.TYPE, Short.class);
        primitiveToWrapper.put(Character.TYPE, Character.class);
        primitiveToWrapper.put(Integer.TYPE, Integer.class);
        primitiveToWrapper.put(Long.TYPE, Long.class);
        primitiveToWrapper.put(Float.TYPE, Float.class);
        primitiveToWrapper.put(Double.TYPE, Double.class);
        primitiveToWrapper.put(Boolean.TYPE, Boolean.class);
        PRIMITIVE_TO_WRAPPER_MAP = primitiveToWrapper;
    }

    @Override
    public Object visit(ASTClassOrInterfaceBody body, Object data) {
        clearStateIfNewClass(body);
        List<ASTMethodDeclaration> methodDeclarations = body.findDescendantsOfType(ASTMethodDeclaration.class);
        declaredMethods.addAll(methodDeclarations);
        return super.visit(body, data);
    }

    private void clearStateIfNewClass(ASTClassOrInterfaceBody body) {
        if (isBodyOfOuterClass(body)) {
            declaredMethods.clear();
        }
    }

    private boolean isBodyOfOuterClass(ASTClassOrInterfaceBody body) {
        return body.getFirstParentOfType(ASTClassOrInterfaceBody.class) == null;
    }

    @Override
    public Object visit(ASTVariableDeclaratorId varId, Object data) {
        if (isStringVariableDeclarator(varId)) {
            for (NameOccurrence varUsage : varId.getUsages()) {
                NameOccurrence qualifier = getVarUsageQualifier(varUsage);
                if (isToStringOnStringCall(varId, qualifier)) {
                    addViolation(data, varUsage.getLocation());
                }
            }
        }
        return data;
    }

    private boolean isStringVariableDeclarator(ASTVariableDeclaratorId varDeclaratorId) {
        VariableNameDeclaration varNameDeclaration = varDeclaratorId.getNameDeclaration();
        return varNameDeclaration != null
                && TypeHelper.isExactlyAny(varNameDeclaration, String.class, String[].class);
    }

    private NameOccurrence getVarUsageQualifier(NameOccurrence varUsage) {
        JavaNameOccurrence jVarUsage = (JavaNameOccurrence) varUsage;
        return jVarUsage.getNameForWhichThisIsAQualifier();
    }

    private boolean isToStringOnStringCall(ASTVariableDeclaratorId varDeclaratorId, NameOccurrence qualifier) {
        if (qualifier != null) {
            return isNotAMethodReference(qualifier) && isNotAnArrayField(varDeclaratorId, qualifier)
                    && isToString(qualifier.getImage());
        }
        return false;
    }

    private boolean isNotAnArrayField(ASTVariableDeclaratorId varDeclaratorId, NameOccurrence qualifier) {
        return !varDeclaratorId.hasArrayType() || isNotAName(qualifier);
    }

    private boolean isNotAMethodReference(NameOccurrence qualifier) {
        return isNotA(qualifier, ASTMethodReference.class);
    }

    private boolean isNotAName(NameOccurrence qualifier) {
        return isNotA(qualifier, ASTName.class);
    }

    private boolean isNotA(NameOccurrence qualifier, Class<? extends JavaNode> type) {
        ScopedNode location = qualifier.getLocation();
        return location == null || !type.isAssignableFrom(location.getClass());
    }

    @Override
    public Object visit(ASTPrimaryExpression primaryExpr, Object data) {
        if (hasChainedMethods(primaryExpr)) {
            for (int callIndex = 2; callIndex < primaryExpr.getNumChildren(); callIndex++) {
                JavaNode methodCall = primaryExpr.getChild(callIndex);
                if (isToStringMethodCall(methodCall)) {
                    JavaNode prevMethodCall = primaryExpr.getChild(callIndex - 2);
                    JavaNode prevMethodCallArgs = primaryExpr.getChild(callIndex - 1);
                    if (calledMethodReturnsString(prevMethodCall, prevMethodCallArgs)) {
                        addViolation(data, methodCall);
                    }
                }
            }
        }
        return super.visit(primaryExpr, data);
    }

    private boolean hasChainedMethods(ASTPrimaryExpression primaryExpr) {
        return primaryExpr.getNumChildren() >= 4;
    }

    private boolean isToStringMethodCall(JavaNode methodCall) {
        String methodName = getCalledMethodName(methodCall);
        return isToString(methodName);
    }

    private boolean isToString(String methodName) {
        return "toString".equals(methodName);
    }

    private boolean calledMethodReturnsString(JavaNode methodCall, JavaNode methodCallArgs) {
        String returnTypeName = getCalledMethodReturnTypeName(methodCall, methodCallArgs);
        return "String".equals(returnTypeName);
    }

    private String getCalledMethodReturnTypeName(JavaNode methodCall, JavaNode methodCallArgs) {
        String calledMethodName = getCalledMethodName(methodCall);
        ASTArguments arguments = methodCallArgs.getFirstDescendantOfType(ASTArguments.class);
        return calledMethodName != null && arguments != null
                ? findMethodReturnTypeName(calledMethodName, arguments)
                : null;
    }

    private String getCalledMethodName(JavaNode methodCall) {
        ASTName name = methodCall.getFirstDescendantOfType(ASTName.class);
        return name != null ? name.getImage() : methodCall.getImage();
    }

    private String findMethodReturnTypeName(String methodName, ASTArguments args) {
        List<ASTMethodDeclaration> candidateMethods = getMethodsByNameAndArgsCount(methodName, args.size());
        if (candidateMethods.size() > 1) {
            ASTArgumentList argsList = args.getFirstChildOfType(ASTArgumentList.class);
            for (ASTMethodDeclaration candidateMethod : candidateMethods) {
                ASTFormalParameters methodParams = candidateMethod.getFormalParameters();
                if (argsMatchMethodParamsByType(argsList, methodParams)) {
                    return getReturnTypeName(candidateMethod);
                }
            }
        }
        return getFirstMethodReturnTypeNameIfPresent(candidateMethods);
    }

    private List<ASTMethodDeclaration> getMethodsByNameAndArgsCount(String name, int argsCount) {
        List<ASTMethodDeclaration> matchingMethods = new ArrayList<>();
        for (ASTMethodDeclaration method : declaredMethods) {
            if (name.equals(method.getName()) && method.getArity() == argsCount) {
                matchingMethods.add(method);
            }
        }
        return matchingMethods;
    }

    private boolean argsMatchMethodParamsByType(ASTArgumentList argsList, ASTFormalParameters methodParams) {
        for (int paramIndex = 0; paramIndex < methodParams.size(); paramIndex++) {
            ASTFormalParameter methodParam = (ASTFormalParameter) methodParams.getChild(paramIndex);
            Class<?> typeOfParam = methodParam.getType();
            ASTExpression arg = (ASTExpression) argsList.getChild(paramIndex);
            if (typeOfParam != null
                    && !TypeHelper.isA(arg, typeOfParam)
                    && !isPrimitiveWrapperMatch(arg, typeOfParam)) {
                return false;
            }
        }
        return true;
    }

    private boolean isPrimitiveWrapperMatch(TypeNode arg, Class<?> typeOfParam) {
        if (arg.getType() == null) {
            return false;
        }
        Class<?> argType = toWrapperClassIfPrimitive(arg.getType());
        Class<?> paramType = toWrapperClassIfPrimitive(typeOfParam);
        return paramType.isAssignableFrom(argType);
    }

    private Class<?> toWrapperClassIfPrimitive(Class<?> type) {
        if (PRIMITIVE_TO_WRAPPER_MAP.containsKey(type)) {
            return PRIMITIVE_TO_WRAPPER_MAP.get(type);
        }
        return type;
    }

    private String getFirstMethodReturnTypeNameIfPresent(List<ASTMethodDeclaration> methods) {
        if (!methods.isEmpty()) {
            ASTMethodDeclaration firstMethod = methods.get(0);
            return getReturnTypeName(firstMethod);
        }
        return null;
    }

    private String getReturnTypeName(ASTMethodDeclaration method) {
        ASTType returnType = method.getResultType().getFirstDescendantOfType(ASTType.class);
        if (returnType != null) {
            Class<?> type = returnType.getType();
            return type != null ? type.getSimpleName() : returnType.getTypeImage();
        }
        return null;
    }
}
