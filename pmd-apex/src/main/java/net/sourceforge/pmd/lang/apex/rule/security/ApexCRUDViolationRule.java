/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.security;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.apex.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlDeleteStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlInsertStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlMergeStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlUpdateStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlUpsertStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDottedExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTField;
import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclarationStatements;
import net.sourceforge.pmd.lang.apex.ast.ASTIfElseBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTNewNameValueObjectExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTParameter;
import net.sourceforge.pmd.lang.apex.ast.ASTProperty;
import net.sourceforge.pmd.lang.apex.ast.ASTReferenceExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTSoqlExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableExpression;
import net.sourceforge.pmd.lang.apex.ast.AbstractApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.ast.Node;

import apex.jorje.data.ast.Identifier;
import apex.jorje.data.ast.TypeRef;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * Finding missed CRUD checks for SOQL and DML operations.
 * 
 * @author sergey.gorbaty
 *
 */
public class ApexCRUDViolationRule extends AbstractApexRule {
    private static final Pattern VOID_OR_STRING_PATTERN = Pattern.compile("^(string|void)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern SELECT_FROM_PATTERN = Pattern.compile("^[\\S|\\s]+?FROM[\\s]+?(\\S+)",
            Pattern.CASE_INSENSITIVE);

    private final HashMap<String, String> varToTypeMapping = new HashMap<>();
    private final ListMultimap<String, String> typeToDMLOperationMapping = ArrayListMultimap.create();
    private final HashMap<String, String> checkedTypeToDMLOperationViaESAPI = new HashMap<>();
    private final WeakHashMap<String, ASTMethod> classMethods = new WeakHashMap<>();

    private static final String IS_CREATEABLE = "isCreateable";
    private static final String IS_DELETABLE = "isDeletable";
    private static final String IS_UPDATEABLE = "isUpdateable";
    private static final String IS_MERGEABLE = "isMergeable";
    private static final String IS_ACCESSIBLE = "isAccessible";
    private static final String ANY = "ANY";
    private static final String S_OBJECT_TYPE = "sObjectType";
    private static final String GET_DESCRIBE = "getDescribe";

    // ESAPI.accessController().isAuthorizedToView(Lead.sObject, fields)
    private static final String[] ESAPI_ISAUTHORIZED_TO_VIEW = new String[] { "ESAPI", "accessController",
        "isAuthorizedToView", };
    private static final String[] ESAPI_ISAUTHORIZED_TO_CREATE = new String[] { "ESAPI", "accessController",
        "isAuthorizedToCreate", };
    private static final String[] ESAPI_ISAUTHORIZED_TO_UPDATE = new String[] { "ESAPI", "accessController",
        "isAuthorizedToUpdate", };
    private static final String[] ESAPI_ISAUTHORIZED_TO_DELETE = new String[] { "ESAPI", "accessController",
        "isAuthorizedToDelete", };

    private static final String[] RESERVED_KEYS_FLS = new String[] { "Schema", S_OBJECT_TYPE, };

    public ApexCRUDViolationRule() {
        setProperty(CODECLIMATE_CATEGORIES, new String[] { "Security" });
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 100);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {
        for (ASTMethod n : node.findDescendantsOfType(ASTMethod.class)) {
            StringBuilder sb = new StringBuilder().append(n.getNode().getDefiningType().getApexName()).append(":")
                    .append(n.getNode().getMethodInfo().getCanonicalName()).append(":")
                    .append(n.getNode().getMethodInfo().getParameterTypes().size());
            classMethods.put(sb.toString(), n);
        }

        node.childrenAccept(this, data);
        return data;
    }

    @Override
    public Object visit(ASTMethodCallExpression node, Object data) {
        collectCRUDMethodLevelChecks(node);
        return data;
    }

    @Override
    public Object visit(ASTDmlInsertStatement node, Object data) {
        checkForCRUD(node, data, IS_CREATEABLE);
        return data;
    }

    @Override
    public Object visit(ASTDmlDeleteStatement node, Object data) {
        checkForCRUD(node, data, IS_DELETABLE);
        return data;
    }

    @Override
    public Object visit(ASTDmlUpdateStatement node, Object data) {
        checkForCRUD(node, data, IS_UPDATEABLE);
        return data;
    }

    @Override
    public Object visit(ASTDmlUpsertStatement node, Object data) {
        checkForCRUD(node, data, IS_CREATEABLE);
        checkForCRUD(node, data, IS_UPDATEABLE);
        return data;
    }

    @Override
    public Object visit(ASTDmlMergeStatement node, Object data) {
        checkForCRUD(node, data, IS_MERGEABLE);
        return data;
    }

    @Override
    public Object visit(final ASTAssignmentExpression node, Object data) {
        final ASTSoqlExpression soql = node.getFirstChildOfType(ASTSoqlExpression.class);
        if (soql != null) {
            checkForAccessibility(soql, data);
        }

        return data;
    }

    @Override
    public Object visit(final ASTVariableDeclaration node, Object data) {
        String type = node.getNode().getLocalInfo().getType().getApexName();
        addVariableToMapping(Helper.getFQVariableName(node), type);

        final ASTSoqlExpression soql = node.getFirstChildOfType(ASTSoqlExpression.class);
        if (soql != null) {
            checkForAccessibility(soql, data);
        }

        return data;

    }

    @Override
    public Object visit(final ASTFieldDeclaration node, Object data) {
        ASTFieldDeclarationStatements field = node.getFirstParentOfType(ASTFieldDeclarationStatements.class);
        if (field != null) {
            try {
                TypeRef a = field.getNode().getTypeName();
                Field f = a.getClass().getDeclaredField("className");
                f.setAccessible(true);
                if (f.get(a) instanceof ArrayList<?>) {
                    @SuppressWarnings("unchecked")
                    ArrayList<Identifier> innerField = (ArrayList<Identifier>) f.get(a);
                    if (!innerField.isEmpty()) {
                        String type = innerField.get(0).value;
                        addVariableToMapping(Helper.getFQVariableName(node), type);
                    }
                }

            } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException | SecurityException e) {
            }

        }
        final ASTSoqlExpression soql = node.getFirstChildOfType(ASTSoqlExpression.class);
        if (soql != null) {
            checkForAccessibility(soql, data);
        }

        return data;

    }

    @Override
    public Object visit(final ASTReturnStatement node, Object data) {
        final ASTSoqlExpression soql = node.getFirstChildOfType(ASTSoqlExpression.class);
        if (soql != null) {
            checkForAccessibility(soql, data);
        }

        return data;
    }

    private void addVariableToMapping(final String variableName, final String type) {
        varToTypeMapping.put(variableName, getSimpleType(type));
    }

    private String getSimpleType(final String type) {
        String typeToUse = type;

        Pattern pattern = Pattern.compile("^[list<]?list<(\\S+?)>[>]?$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(typeToUse);

        if (matcher.find()) {
            typeToUse = matcher.group(1);
        }
        return typeToUse;
    }

    @Override
    public Object visit(final ASTProperty node, Object data) {
        ASTField field = node.getFirstChildOfType(ASTField.class);
        if (field != null) {
            String fieldType = field.getNode().getFieldInfo().getType().getApexName();
            addVariableToMapping(Helper.getFQVariableName(field), fieldType);
        }

        return data;

    }

    private void collectCRUDMethodLevelChecks(final ASTMethodCallExpression node) {
        final String method = node.getNode().getMethodName();
        final ASTReferenceExpression ref = node.getFirstChildOfType(ASTReferenceExpression.class);
        if (ref == null) {
            return;
        }

        List<Identifier> a = ref.getNode().getJadtIdentifiers();
        if (!a.isEmpty()) {
            extractObjectAndFields(a, method, node.getNode().getDefiningType().getApexName());
        } else {
            // see if ESAPI
            if (Helper.isMethodCallChain(node, ESAPI_ISAUTHORIZED_TO_VIEW)) {
                extractObjectTypeFromESAPI(node, IS_ACCESSIBLE);
            }

            if (Helper.isMethodCallChain(node, ESAPI_ISAUTHORIZED_TO_CREATE)) {
                extractObjectTypeFromESAPI(node, IS_CREATEABLE);
            }

            if (Helper.isMethodCallChain(node, ESAPI_ISAUTHORIZED_TO_UPDATE)) {
                extractObjectTypeFromESAPI(node, IS_UPDATEABLE);
            }

            if (Helper.isMethodCallChain(node, ESAPI_ISAUTHORIZED_TO_DELETE)) {
                extractObjectTypeFromESAPI(node, IS_DELETABLE);
            }

            // see if getDescribe()
            final ASTDottedExpression dottedExpr = ref.getFirstChildOfType(ASTDottedExpression.class);
            if (dottedExpr != null) {
                final ASTMethodCallExpression nestedMethodCall = dottedExpr
                        .getFirstChildOfType(ASTMethodCallExpression.class);
                if (nestedMethodCall != null) {
                    if (isLastMethodName(nestedMethodCall, S_OBJECT_TYPE, GET_DESCRIBE)) {
                        String resolvedType = getType(nestedMethodCall);
                        typeToDMLOperationMapping.put(resolvedType, method);
                    }
                }
            }

        }
    }

    private boolean isLastMethodName(final ASTMethodCallExpression methodNode, final String className,
            final String methodName) {
        final ASTReferenceExpression reference = methodNode.getFirstChildOfType(ASTReferenceExpression.class);
        if (reference.getNode().getJadtIdentifiers().size() > 0) {
            if (reference.getNode().getJadtIdentifiers().get(reference.getNode().getJadtIdentifiers().size() - 1).value
                    .equalsIgnoreCase(className) && Helper.isMethodName(methodNode, methodName)) {
                return true;
            }
        }

        return false;
    }

    private String getType(final ASTMethodCallExpression methodNode) {
        final ASTReferenceExpression reference = methodNode.getFirstChildOfType(ASTReferenceExpression.class);
        if (reference.getNode().getJadtIdentifiers().size() > 0) {
            return new StringBuilder().append(reference.getNode().getDefiningType().getApexName()).append(":")
                    .append(reference.getNode().getJadtIdentifiers().get(0).value).toString();
        }
        return "";
    }

    private void extractObjectAndFields(final List<Identifier> listIdentifiers, final String method,
            final String definingType) {
        final List<String> strings = listIdentifiers.stream().map(id -> id.value).collect(Collectors.toList());

        int flsIndex = Collections.lastIndexOfSubList(strings, Arrays.asList(RESERVED_KEYS_FLS));
        if (flsIndex != -1) {
            String objectTypeName = strings.get(flsIndex + RESERVED_KEYS_FLS.length);
            typeToDMLOperationMapping.put(definingType + ":" + objectTypeName, method);
        }
    }

    private void checkForCRUD(final AbstractApexNode<?> node, final Object data, final String crudMethod) {
        final HashSet<ASTMethodCallExpression> prevCalls = getPreviousMethodCalls(node);
        for (ASTMethodCallExpression prevCall : prevCalls) {
            collectCRUDMethodLevelChecks(prevCall);
        }

        final ASTMethod wrappingMethod = node.getFirstParentOfType(ASTMethod.class);
        final ASTUserClass wrappingClass = node.getFirstParentOfType(ASTUserClass.class);

        if ((wrappingClass != null && Helper.isTestMethodOrClass(wrappingClass))
                || (wrappingMethod != null && Helper.isTestMethodOrClass(wrappingMethod))) {
            return;
        }

        final ASTNewNameValueObjectExpression newObj = node.getFirstChildOfType(ASTNewNameValueObjectExpression.class);
        if (newObj != null) {
            final String type = Helper.getFQVariableName(newObj);
            validateCRUDCheckPresent(node, data, crudMethod, type);
        }

        final ASTVariableExpression variable = node.getFirstChildOfType(ASTVariableExpression.class);
        if (variable != null) {
            final String type = varToTypeMapping.get(Helper.getFQVariableName(variable));
            if (type != null) {
                StringBuilder typeCheck = new StringBuilder().append(node.getNode().getDefiningType().getApexName())
                        .append(":").append(type);

                validateCRUDCheckPresent(node, data, crudMethod, typeCheck.toString());
            }
        }
    }

    private HashSet<ASTMethodCallExpression> getPreviousMethodCalls(final AbstractApexNode<?> self) {
        final HashSet<ASTMethodCallExpression> innerMethodCalls = new HashSet<>();
        final ASTMethod outerMethod = self.getFirstParentOfType(ASTMethod.class);
        if (outerMethod != null) {
            final ASTBlockStatement blockStatement = outerMethod.getFirstChildOfType(ASTBlockStatement.class);
            recursivelyEvaluateCRUDMethodCalls(self, innerMethodCalls, blockStatement);

            final List<ASTMethod> constructorMethods = findConstructorlMethods(self);
            for (ASTMethod method : constructorMethods) {
                innerMethodCalls.addAll(method.findDescendantsOfType(ASTMethodCallExpression.class));
            }

            // some methods might be within this class
            mapCallToMethodDecl(self, innerMethodCalls, new ArrayList<ASTMethodCallExpression>(innerMethodCalls));
        }

        return innerMethodCalls;
    }

    private void recursivelyEvaluateCRUDMethodCalls(final AbstractApexNode<?> self,
            final HashSet<ASTMethodCallExpression> innerMethodCalls, final ASTBlockStatement blockStatement) {
        if (blockStatement != null) {
            int numberOfStatements = blockStatement.jjtGetNumChildren();
            for (int i = 0; i < numberOfStatements; i++) {
                Node n = blockStatement.jjtGetChild(i);

                if (n instanceof ASTIfElseBlockStatement) {
                    List<ASTBlockStatement> innerBlocks = n.findDescendantsOfType(ASTBlockStatement.class);
                    for (ASTBlockStatement innerBlock : innerBlocks) {
                        recursivelyEvaluateCRUDMethodCalls(self, innerMethodCalls, innerBlock);
                    }
                }

                AbstractApexNode<?> match = n.getFirstDescendantOfType(self.getClass());
                if (match == self) {
                    break;
                }
                ASTMethodCallExpression methodCall = n.getFirstDescendantOfType(ASTMethodCallExpression.class);
                if (methodCall != null) {
                    mapCallToMethodDecl(self, innerMethodCalls, Arrays.asList(methodCall));
                }
            }

        }
    }

    private void mapCallToMethodDecl(final AbstractApexNode<?> self,
            final HashSet<ASTMethodCallExpression> innerMethodCalls, final List<ASTMethodCallExpression> nodes) {
        for (ASTMethodCallExpression node : nodes) {
            if (node == self) {
                break;
            }

            final ASTMethod methodBody = resolveMethodCalls(node);
            if (methodBody != null) {
                innerMethodCalls.addAll(methodBody.findDescendantsOfType(ASTMethodCallExpression.class));
            }

        }
    }

    private List<ASTMethod> findConstructorlMethods(final AbstractApexNode<?> node) {
        final ArrayList<ASTMethod> ret = new ArrayList<>();
        final Set<String> constructors = classMethods.keySet().stream()
                .filter(p -> (p.contains("<init>") || p.contains("<clinit>"))).collect(Collectors.toSet());

        for (String c : constructors) {
            ret.add(classMethods.get(c));
        }

        return ret;
    }

    private ASTMethod resolveMethodCalls(final ASTMethodCallExpression node) {
        StringBuilder sb = new StringBuilder().append(node.getNode().getDefiningType().getApexName()).append(":")
                .append(node.getNode().getMethodName()).append(":").append(node.getNode().getInputParameters().size());
        return classMethods.get(sb.toString());
    }

    private boolean isProperESAPICheckForDML(final String typeToCheck, final String dmlOperation) {
        final boolean hasMapping = checkedTypeToDMLOperationViaESAPI.containsKey(typeToCheck.toString());
        if (hasMapping) {
            if (dmlOperation.equals(ANY)) {
                return true;
            }

            String dmlChecked = checkedTypeToDMLOperationViaESAPI.get(typeToCheck);
            return dmlChecked.equals(dmlOperation);
        }

        return false;
    }

    private void extractObjectTypeFromESAPI(final ASTMethodCallExpression node, final String dmlOperation) {
        final ASTVariableExpression var = node.getFirstChildOfType(ASTVariableExpression.class);
        if (var != null) {
            final ASTReferenceExpression reference = var.getFirstChildOfType(ASTReferenceExpression.class);
            if (reference != null) {
                List<Identifier> identifiers = reference.getNode().getJadtIdentifiers();
                if (identifiers.size() == 1) {
                    StringBuilder sb = new StringBuilder().append(node.getNode().getDefiningType().getApexName())
                            .append(":").append(identifiers.get(0).value);
                    checkedTypeToDMLOperationViaESAPI.put(sb.toString(), dmlOperation);
                }

            }
        }

    }

    private void validateCRUDCheckPresent(final AbstractApexNode<?> node, final Object data, final String crudMethod,
            final String typeCheck) {
        if (!typeToDMLOperationMapping.containsKey(typeCheck)) {
            if (!isProperESAPICheckForDML(typeCheck, crudMethod)) {
                addViolation(data, node);
            }
        } else {
            boolean properChecksHappened = false;

            List<String> dmlOperationsChecked = typeToDMLOperationMapping.get(typeCheck);
            for (String dmlOp : dmlOperationsChecked) {
                if (dmlOp.equalsIgnoreCase(crudMethod)) {
                    properChecksHappened = true;
                    break;
                }
                if (crudMethod.equals(ANY)) {
                    properChecksHappened = true;
                    break;
                }
            }

            if (!properChecksHappened) {
                addViolation(data, node);
            }
        }
    }

    private void checkForAccessibility(final ASTSoqlExpression node, Object data) {
        final boolean isCount = node.getNode().getCanonicalQuery().startsWith("SELECT COUNT()");
        final String typeFromSOQL = getTypeFromSOQLQuery(node);

        final HashSet<ASTMethodCallExpression> prevCalls = getPreviousMethodCalls(node);
        for (ASTMethodCallExpression prevCall : prevCalls) {
            collectCRUDMethodLevelChecks(prevCall);
        }

        boolean isGetter = false;
        String returnType = null;

        final ASTMethod wrappingMethod = node.getFirstParentOfType(ASTMethod.class);
        final ASTUserClass wrappingClass = node.getFirstParentOfType(ASTUserClass.class);

        if (isCount || (wrappingClass != null && Helper.isTestMethodOrClass(wrappingClass))
                || (wrappingMethod != null && Helper.isTestMethodOrClass(wrappingMethod))) {
            return;
        }

        if (wrappingMethod != null) {
            isGetter = isMethodAGetter(wrappingMethod);
            returnType = getReturnType(wrappingMethod);
        }

        final ASTVariableDeclaration variableDecl = node.getFirstParentOfType(ASTVariableDeclaration.class);
        if (variableDecl != null) {
            String type = variableDecl.getNode().getLocalInfo().getType().getApexName();
            type = getSimpleType(type);
            StringBuilder typeCheck = new StringBuilder().append(variableDecl.getNode().getDefiningType().getApexName())
                    .append(":").append(type);

            if (!isGetter) {
                validateCRUDCheckPresent(node, data, ANY, typeFromSOQL == null ? typeCheck.toString() : typeFromSOQL);
            }

        }

        final ASTAssignmentExpression assignment = node.getFirstParentOfType(ASTAssignmentExpression.class);
        if (assignment != null) {
            final ASTVariableExpression variable = assignment.getFirstChildOfType(ASTVariableExpression.class);
            if (variable != null) {
                String variableWithClass = Helper.getFQVariableName(variable);
                if (varToTypeMapping.containsKey(variableWithClass)) {
                    String type = varToTypeMapping.get(variableWithClass);
                    if (!isGetter) {
                        validateCRUDCheckPresent(node, data, ANY, typeFromSOQL == null ? type : typeFromSOQL);
                    }
                }
            }

        }

        final ASTReturnStatement returnStatement = node.getFirstParentOfType(ASTReturnStatement.class);
        if (returnStatement != null) {
            if (!isGetter) {
                String retType = typeFromSOQL == null ? returnType : typeFromSOQL;
                validateCRUDCheckPresent(node, data, ANY, retType == null ? "" : retType);
            }
        }
    }

    private String getTypeFromSOQLQuery(final ASTSoqlExpression node) {
        final String canonQuery = node.getNode().getCanonicalQuery();

        Matcher m = SELECT_FROM_PATTERN.matcher(canonQuery);
        while (m.find()) {
            return new StringBuffer().append(node.getNode().getDefiningType().getApexName()).append(":")
                    .append(m.group(1)).toString();
        }
        return null;
    }

    private String getReturnType(final ASTMethod method) {
        return new StringBuilder().append(method.getNode().getDefiningType().getApexName()).append(":")
                .append(method.getNode().getMethodInfo().getEmitSignature().getReturnType().getApexName()).toString();
    }

    private boolean isMethodAGetter(final ASTMethod method) {
        final boolean startsWithGet = method.getNode().getMethodInfo().getCanonicalName().startsWith("get");
        final boolean voidOrString = VOID_OR_STRING_PATTERN
                .matcher(method.getNode().getMethodInfo().getEmitSignature().getReturnType().getApexName()).matches();
        final boolean noParams = method.findChildrenOfType(ASTParameter.class).isEmpty();

        return (startsWithGet && noParams && !voidOrString);
    }
}
