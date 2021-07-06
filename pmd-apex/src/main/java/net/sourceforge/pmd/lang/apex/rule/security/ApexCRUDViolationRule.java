/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import net.sourceforge.pmd.lang.apex.ast.ASTField;
import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclarationStatements;
import net.sourceforge.pmd.lang.apex.ast.ASTForEachStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTIfElseBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTNewKeyValueObjectExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTProperty;
import net.sourceforge.pmd.lang.apex.ast.ASTReferenceExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTSoqlExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableExpression;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.apex.rule.internal.Helper;
import net.sourceforge.pmd.lang.ast.Node;

import com.google.common.base.Objects;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * Finding missed CRUD checks for SOQL and DML operations.
 *
 * @author sergey.gorbaty
 *
 */
public class ApexCRUDViolationRule extends AbstractApexRule {
    private static final Pattern SELECT_FROM_PATTERN = Pattern.compile("[\\S|\\s]+?FROM[\\s]+?(\\w+)",
            Pattern.CASE_INSENSITIVE);

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

    private static final Pattern WITH_SECURITY_ENFORCED = Pattern.compile("(?is).*[^']\\s*WITH\\s+SECURITY_ENFORCED\\s*[^']*");

    private final Map<String, String> varToTypeMapping = new HashMap<>();
    private final ListMultimap<String, String> typeToDMLOperationMapping = ArrayListMultimap.create();
    private final Map<String, String> checkedTypeToDMLOperationViaESAPI = new HashMap<>();
    private final Map<String, ASTMethod> classMethods = new WeakHashMap<>();
    private String className;

    public ApexCRUDViolationRule() {
        setProperty(CODECLIMATE_CATEGORIES, "Security");
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 100);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {
        if (Helper.isTestMethodOrClass(node) || Helper.isSystemLevelClass(node)) {
            return data; // stops all the rules
        }

        className = node.getImage();

        for (ASTMethod n : node.findDescendantsOfType(ASTMethod.class)) {
            StringBuilder sb = new StringBuilder().append(n.getDefiningType()).append(":")
                    .append(n.getCanonicalName()).append(":")
                    .append(n.getArity());
            classMethods.put(sb.toString(), n);
        }

        return super.visit(node, data);
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
        String type = node.getType();
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
            String namesString = field.getTypeName();

            switch (namesString.toLowerCase(Locale.ROOT)) {
            case "list":
            case "map":
                for (String typeArg : field.getTypeArguments()) {
                    varToTypeMapping.put(Helper.getFQVariableName(node), typeArg);
                }
                break;
            default:
                varToTypeMapping.put(Helper.getFQVariableName(node), getSimpleType(namesString));
                break;
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

    @Override
    public Object visit(final ASTForEachStatement node, Object data) {
        final ASTSoqlExpression soql = node.getFirstChildOfType(ASTSoqlExpression.class);
        if (soql != null) {
            checkForAccessibility(soql, data);
        }

        return data;
    }

    private void addVariableToMapping(final String variableName, final String type) {
        switch (type.toLowerCase(Locale.ROOT)) {
        case "list":
        case "map":
            break;
        default:
            varToTypeMapping.put(variableName, getSimpleType(type));
            break;
        }
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
            String fieldType = field.getType();
            addVariableToMapping(Helper.getFQVariableName(field), fieldType);
        }

        return data;

    }

    private void collectCRUDMethodLevelChecks(final ASTMethodCallExpression node) {
        final String method = node.getMethodName();
        final ASTReferenceExpression ref = node.getFirstChildOfType(ASTReferenceExpression.class);
        if (ref == null) {
            return;
        }

        List<String> a = ref.getNames();
        if (!a.isEmpty()) {
            extractObjectAndFields(a, method, node.getDefiningType());
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
            final ASTMethodCallExpression nestedMethodCall = ref
                    .getFirstChildOfType(ASTMethodCallExpression.class);
            if (nestedMethodCall != null) {
                if (isLastMethodName(nestedMethodCall, S_OBJECT_TYPE, GET_DESCRIBE)) {
                    String resolvedType = getType(nestedMethodCall);
                    if (!typeToDMLOperationMapping.get(resolvedType).contains(method)) {
                        typeToDMLOperationMapping.put(resolvedType, method);
                    }
                }
            }

        }
    }

    private boolean isLastMethodName(final ASTMethodCallExpression methodNode, final String className,
            final String methodName) {
        final ASTReferenceExpression reference = methodNode.getFirstChildOfType(ASTReferenceExpression.class);
        if (reference != null && reference.getNames().size() > 0) {
            if (reference.getNames().get(reference.getNames().size() - 1)
                    .equalsIgnoreCase(className) && Helper.isMethodName(methodNode, methodName)) {
                return true;
            }
        }

        return false;
    }

    private boolean isWithSecurityEnforced(final ApexNode<?> node) {
        if (node instanceof ASTSoqlExpression) {
            return WITH_SECURITY_ENFORCED.matcher(((ASTSoqlExpression) node).getQuery()).matches();
        }
        return false;
    }

    private String getType(final ASTMethodCallExpression methodNode) {
        final ASTReferenceExpression reference = methodNode.getFirstChildOfType(ASTReferenceExpression.class);
        if (reference.getNames().size() > 0) {
            return new StringBuilder().append(reference.getDefiningType()).append(":")
                    .append(reference.getNames().get(0)).toString();
        }
        return "";
    }

    private void extractObjectAndFields(final List<String> listIdentifiers, final String method,
            final String definingType) {

        int flsIndex = Collections.lastIndexOfSubList(listIdentifiers, Arrays.asList(RESERVED_KEYS_FLS));
        if (flsIndex != -1) {
            String objectTypeName = listIdentifiers.get(flsIndex + RESERVED_KEYS_FLS.length);
            if (!typeToDMLOperationMapping.get(definingType + ":" + objectTypeName).contains(method)) {
                typeToDMLOperationMapping.put(definingType + ":" + objectTypeName, method);
            }
        }
    }

    private void checkForCRUD(final ApexNode<?> node, final Object data, final String crudMethod) {
        final Set<ASTMethodCallExpression> prevCalls = getPreviousMethodCalls(node);
        for (ASTMethodCallExpression prevCall : prevCalls) {
            collectCRUDMethodLevelChecks(prevCall);
        }

        final ASTMethod wrappingMethod = node.getFirstParentOfType(ASTMethod.class);
        final ASTUserClass wrappingClass = node.getFirstParentOfType(ASTUserClass.class);

        if (wrappingClass != null && Helper.isTestMethodOrClass(wrappingClass)
                || wrappingMethod != null && Helper.isTestMethodOrClass(wrappingMethod)) {
            return;
        }

        final ASTNewKeyValueObjectExpression newObj = node.getFirstChildOfType(ASTNewKeyValueObjectExpression.class);
        if (newObj != null) {
            final String type = Helper.getFQVariableName(newObj);
            validateCRUDCheckPresent(node, data, crudMethod, type);
        }

        final ASTVariableExpression variable = node.getFirstChildOfType(ASTVariableExpression.class);
        if (variable != null) {
            final String type = varToTypeMapping.get(Helper.getFQVariableName(variable));
            if (type != null) {
                StringBuilder typeCheck = new StringBuilder().append(node.getDefiningType())
                        .append(":").append(type);

                validateCRUDCheckPresent(node, data, crudMethod, typeCheck.toString());
            }
        }
    }

    private Set<ASTMethodCallExpression> getPreviousMethodCalls(final ApexNode<?> self) {
        final Set<ASTMethodCallExpression> innerMethodCalls = new HashSet<>();
        final ASTMethod outerMethod = self.getFirstParentOfType(ASTMethod.class);
        if (outerMethod != null) {
            final ASTBlockStatement blockStatement = outerMethod.getFirstChildOfType(ASTBlockStatement.class);
            recursivelyEvaluateCRUDMethodCalls(self, innerMethodCalls, blockStatement);

            final List<ASTMethod> constructorMethods = findConstructorlMethods();
            for (ASTMethod method : constructorMethods) {
                innerMethodCalls.addAll(method.findDescendantsOfType(ASTMethodCallExpression.class));
            }

            // some methods might be within this class
            mapCallToMethodDecl(self, innerMethodCalls, new ArrayList<ASTMethodCallExpression>(innerMethodCalls));
        }

        return innerMethodCalls;
    }

    private void recursivelyEvaluateCRUDMethodCalls(final ApexNode<?> self,
            final Set<ASTMethodCallExpression> innerMethodCalls, final ASTBlockStatement blockStatement) {
        if (blockStatement != null) {
            int numberOfStatements = blockStatement.getNumChildren();
            for (int i = 0; i < numberOfStatements; i++) {
                Node n = blockStatement.getChild(i);

                if (n instanceof ASTIfElseBlockStatement) {
                    List<ASTBlockStatement> innerBlocks = n.findDescendantsOfType(ASTBlockStatement.class);
                    for (ASTBlockStatement innerBlock : innerBlocks) {
                        recursivelyEvaluateCRUDMethodCalls(self, innerMethodCalls, innerBlock);
                    }
                }

                ApexNode<?> match = n.getFirstDescendantOfType(self.getClass());
                if (Objects.equal(match, self)) {
                    break;
                }
                ASTMethodCallExpression methodCall = n.getFirstDescendantOfType(ASTMethodCallExpression.class);
                if (methodCall != null) {
                    mapCallToMethodDecl(self, innerMethodCalls, Arrays.asList(methodCall));
                }
            }

        }
    }

    private void mapCallToMethodDecl(final ApexNode<?> self,
            final Set<ASTMethodCallExpression> innerMethodCalls, final List<ASTMethodCallExpression> nodes) {
        for (ASTMethodCallExpression node : nodes) {
            if (Objects.equal(node, self)) {
                break;
            }

            final ASTMethod methodBody = resolveMethodCalls(node);
            if (methodBody != null) {
                innerMethodCalls.addAll(methodBody.findDescendantsOfType(ASTMethodCallExpression.class));
            }

        }
    }

    private List<ASTMethod> findConstructorlMethods() {
        final ArrayList<ASTMethod> ret = new ArrayList<>();
        final Set<String> constructors = classMethods.keySet().stream()
                .filter(p -> p.contains("<init>") || p.contains("<clinit>")
                        || p.startsWith(className + ":" + className + ":")).collect(Collectors.toSet());

        for (String c : constructors) {
            ret.add(classMethods.get(c));
        }

        return ret;
    }

    private ASTMethod resolveMethodCalls(final ASTMethodCallExpression node) {
        StringBuilder sb = new StringBuilder().append(node.getDefiningType()).append(":")
                .append(node.getMethodName()).append(":").append(node.getInputParametersSize());
        return classMethods.get(sb.toString());
    }

    private boolean isProperESAPICheckForDML(final String typeToCheck, final String dmlOperation) {
        final boolean hasMapping = checkedTypeToDMLOperationViaESAPI.containsKey(typeToCheck.toString());
        if (hasMapping) {
            if (ANY.equals(dmlOperation)) {
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
                List<String> identifiers = reference.getNames();
                if (identifiers.size() == 1) {
                    StringBuilder sb = new StringBuilder().append(node.getDefiningType())
                            .append(":").append(identifiers.get(0));
                    checkedTypeToDMLOperationViaESAPI.put(sb.toString(), dmlOperation);
                }

            }
        }

    }


    private void validateCRUDCheckPresent(final ApexNode<?> node, final Object data, final String crudMethod,
            final String typeCheck) {
        boolean missingKey = !typeToDMLOperationMapping.containsKey(typeCheck);
        boolean isImproperDMLCheck = !isProperESAPICheckForDML(typeCheck, crudMethod);
        boolean noSecurityEnforced = !isWithSecurityEnforced(node);
        if (missingKey) {
            //if condition returns true, add violation, otherwise return.
            if (isImproperDMLCheck && noSecurityEnforced) {
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
                if (ANY.equals(crudMethod)) {
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
        final Set<String> typesFromSOQL = getTypesFromSOQLQuery(node);

        final Set<ASTMethodCallExpression> prevCalls = getPreviousMethodCalls(node);
        for (ASTMethodCallExpression prevCall : prevCalls) {
            collectCRUDMethodLevelChecks(prevCall);
        }

        String returnType = null;

        final ASTMethod wrappingMethod = node.getFirstParentOfType(ASTMethod.class);
        final ASTUserClass wrappingClass = node.getFirstParentOfType(ASTUserClass.class);

        if (wrappingClass != null && Helper.isTestMethodOrClass(wrappingClass)
            || wrappingMethod != null && Helper.isTestMethodOrClass(wrappingMethod)) {
            return;
        }

        if (wrappingMethod != null) {
            returnType = getReturnType(wrappingMethod);
        }

        final ASTVariableDeclaration variableDecl = node.getFirstParentOfType(ASTVariableDeclaration.class);
        if (variableDecl != null) {
            String type = variableDecl.getType();
            type = getSimpleType(type);
            StringBuilder typeCheck = new StringBuilder().append(variableDecl.getDefiningType())
                    .append(":").append(type);

            if (typesFromSOQL.isEmpty()) {
                validateCRUDCheckPresent(node, data, ANY, typeCheck.toString());
            } else {
                for (String typeFromSOQL : typesFromSOQL) {
                    validateCRUDCheckPresent(node, data, ANY, typeFromSOQL);
                }
            }

        }

        final ASTAssignmentExpression assignment = node.getFirstParentOfType(ASTAssignmentExpression.class);
        if (assignment != null) {
            final ASTVariableExpression variable = assignment.getFirstChildOfType(ASTVariableExpression.class);
            if (variable != null) {
                String variableWithClass = Helper.getFQVariableName(variable);
                if (varToTypeMapping.containsKey(variableWithClass)) {
                    String type = varToTypeMapping.get(variableWithClass);
                    if (typesFromSOQL.isEmpty()) {
                        validateCRUDCheckPresent(node, data, ANY, type);
                    } else {
                        for (String typeFromSOQL : typesFromSOQL) {
                            validateCRUDCheckPresent(node, data, ANY, typeFromSOQL);
                        }
                    }
                }
            }

        }

        final ASTReturnStatement returnStatement = node.getFirstParentOfType(ASTReturnStatement.class);
        if (returnStatement != null) {
            if (typesFromSOQL.isEmpty()) {
                validateCRUDCheckPresent(node, data, ANY, returnType);
            } else {
                for (String typeFromSOQL : typesFromSOQL) {
                    validateCRUDCheckPresent(node, data, ANY, typeFromSOQL);
                }
            }
        }

        final ASTForEachStatement forEachStatement = node.getFirstParentOfType(ASTForEachStatement.class);
        if (forEachStatement != null) {
            if (typesFromSOQL.isEmpty()) {

                final ASTVariableDeclaration variableDeclFor = forEachStatement.getFirstParentOfType(ASTVariableDeclaration.class);
                if (variableDeclFor != null) {
                    String type = variableDeclFor.getType();
                    type = getSimpleType(type);
                    StringBuilder typeCheck = new StringBuilder().append(variableDeclFor.getDefiningType())
                            .append(":").append(type);

                    validateCRUDCheckPresent(node, data, ANY, typeCheck.toString());
                }
                
            } else {
                for (String typeFromSOQL : typesFromSOQL) {
                    validateCRUDCheckPresent(node, data, ANY, typeFromSOQL);
                }
            }
        }
    }

    private Set<String> getTypesFromSOQLQuery(final ASTSoqlExpression node) {
        final Set<String> retVal = new HashSet<>();
        final String canonQuery = node.getCanonicalQuery();

        Matcher m = SELECT_FROM_PATTERN.matcher(canonQuery);
        while (m.find()) {
            retVal.add(new StringBuffer().append(node.getDefiningType()).append(":")
                    .append(m.group(1)).toString());
        }
        return retVal;
    }

    private String getReturnType(final ASTMethod method) {
        return new StringBuilder().append(method.getDefiningType()).append(":")
                .append(method.getReturnType()).toString();
    }
}
