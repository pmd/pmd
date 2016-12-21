/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.security;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.apex.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlDeleteStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlInsertStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlMergeStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlUpdateStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlUpsertStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDottedExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTField;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTNewNameValueObjectExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTProperty;
import net.sourceforge.pmd.lang.apex.ast.ASTReferenceExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTSoqlExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableExpression;
import net.sourceforge.pmd.lang.apex.ast.AbstractApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

import apex.jorje.data.ast.Identifier;
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

    private final HashMap<String, String> varToTypeMapping = new HashMap<>();
    private final ListMultimap<String, String> typeToDMLOperationMapping = ArrayListMultimap.create();
    private final HashMap<String, String> checkedTypeToDMLOperationViaESAPI = new HashMap<>();

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
    public Object visit(ASTMethodCallExpression node, Object data) {
        final String method = node.getNode().getMethodName();
        final ASTReferenceExpression ref = node.getFirstChildOfType(ASTReferenceExpression.class);
        if (ref == null) {
            return data;
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
        final ASTSoqlExpression soql = node.getFirstChildOfType(ASTSoqlExpression.class);
        if (soql != null) {
            checkForAccessibility(soql, data);
        }

        String type = node.getNode().getLocalInfo().getType().getApexName();
        StringBuilder sb = new StringBuilder().append(node.getNode().getDefiningType().getApexName()).append(":")
                .append(node.getNode().getLocalInfo().getName());
        addVariableToMapping(sb.toString(), type);

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
            String fieldName = field.getNode().getFieldInfo().getName();
            String fieldType = field.getNode().getFieldInfo().getType().getApexName();

            StringBuilder sb = new StringBuilder().append(field.getNode().getDefiningType().getApexName()).append(":")
                    .append(fieldName);

            addVariableToMapping(sb.toString(), fieldType);

        }

        return data;

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
                StringBuilder typeCheck = new StringBuilder().append(node.getNode().getDefiningType().getApexName()).append(":")
                        .append(type);

                validateCRUDCheckPresent(node, data, crudMethod, typeCheck.toString());
            }
        }
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

    private void checkForAccessibility(final AbstractApexNode<?> node, Object data) {
        boolean isGetter = false;
        String returnType = null;

        final ASTMethod wrappingMethod = node.getFirstParentOfType(ASTMethod.class);
        final ASTUserClass wrappingClass = node.getFirstParentOfType(ASTUserClass.class);

        if ((wrappingClass != null && Helper.isTestMethodOrClass(wrappingClass))
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
                validateCRUDCheckPresent(node, data, ANY, typeCheck.toString());
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
                        validateCRUDCheckPresent(node, data, ANY, type);
                    }
                }
            }

        }

        final ASTReturnStatement returnStatement = node.getFirstParentOfType(ASTReturnStatement.class);
        if (returnStatement != null) {
            if (!isGetter) {
                validateCRUDCheckPresent(node, data, ANY, returnType == null ? "" : returnType);
            }
        }
    }

    private String getReturnType(final ASTMethod method) {
        return new StringBuilder().append(method.getNode().getDefiningType().getApexName()).append(":")
                .append(method.getNode().getMethodInfo().getEmitSignature().getReturnType().getApexName()).toString();
    }

    private boolean isMethodAGetter(final ASTMethod method) {
        final boolean startsWithGet = method.getNode().getMethodInfo().getCanonicalName().startsWith("get");
        final boolean voidOrString = VOID_OR_STRING_PATTERN
                .matcher(method.getNode().getMethodInfo().getEmitSignature().getReturnType().getApexName()).matches();

        return (startsWithGet && !voidOrString);
    }
}
