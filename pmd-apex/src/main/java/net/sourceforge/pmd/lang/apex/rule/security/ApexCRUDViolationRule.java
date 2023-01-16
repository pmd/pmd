/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.security;

import static net.sourceforge.pmd.properties.PropertyFactory.intProperty;
import static net.sourceforge.pmd.properties.PropertyFactory.stringProperty;

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

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.apex.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlDeleteStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlInsertStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlMergeStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlUndeleteStatement;
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
import net.sourceforge.pmd.lang.apex.ast.ASTNewListInitExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTNewListLiteralExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTNewObjectExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTParameter;
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
import net.sourceforge.pmd.properties.PropertyDescriptor;

import com.google.common.base.Objects;
import com.google.common.collect.HashMultimap;

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
    private static final String IS_UNDELETABLE = "isUndeletable";
    private static final String IS_UPDATEABLE = "isUpdateable";
    private static final String IS_MERGEABLE = "isMergeable";
    private static final String IS_ACCESSIBLE = "isAccessible";
    private static final String ANY = "ANY";
    private static final String S_OBJECT_TYPE = "sObjectType";
    private static final String GET_DESCRIBE = "getDescribe";

    private static final String ACCESS_LEVEL = "AccessLevel";

    // ESAPI.accessController().isAuthorizedToView(Lead.sObject, fields)
    private static final String[] ESAPI_ISAUTHORIZED_TO_VIEW = new String[] { "ESAPI", "accessController",
        "isAuthorizedToView", };
    private static final String[] ESAPI_ISAUTHORIZED_TO_CREATE = new String[] { "ESAPI", "accessController",
        "isAuthorizedToCreate", };
    private static final String[] ESAPI_ISAUTHORIZED_TO_UPDATE = new String[] { "ESAPI", "accessController",
        "isAuthorizedToUpdate", };
    private static final String[] ESAPI_ISAUTHORIZED_TO_DELETE = new String[] { "ESAPI", "accessController",
        "isAuthorizedToDelete", };
    // ESAPI doesn't provide support for undelete or merge

    private static final String[] RESERVED_KEYS_FLS = new String[] { "Schema", S_OBJECT_TYPE, };

    private static final Pattern WITH_SECURITY_ENFORCED = Pattern.compile("(?is).*[^']\\s*WITH\\s+SECURITY_ENFORCED\\s*[^']*");

    //Added For USER MODE
    private static final Pattern WITH_USER_MODE = Pattern.compile("(?is).*[^']\\s*WITH\\s+USER_MODE\\s*[^']*");
    //Added For SYSTEM MODE
    private static final Pattern WITH_SYSTEM_MODE = Pattern.compile("(?is).*[^']\\s*WITH\\s+SYSTEM_MODE\\s*[^']*");

    // <operation>AuthMethodPattern config properties; these are string properties instead of regex properties to help
    // ensure that the compiled patterns are case-insensitive vs. requiring the pattern author to use "(?i)"
    private static final PropertyDescriptor<String> CREATE_AUTH_METHOD_PATTERN_DESCRIPTOR = authMethodPatternProperty("create");
    private static final PropertyDescriptor<String> READ_AUTH_METHOD_PATTERN_DESCRIPTOR = authMethodPatternProperty("read");
    private static final PropertyDescriptor<String> UPDATE_AUTH_METHOD_PATTERN_DESCRIPTOR = authMethodPatternProperty("update");
    private static final PropertyDescriptor<String> DELETE_AUTH_METHOD_PATTERN_DESCRIPTOR = authMethodPatternProperty("delete");
    private static final PropertyDescriptor<String> UNDELETE_AUTH_METHOD_PATTERN_DESCRIPTOR = authMethodPatternProperty("undelete");
    private static final PropertyDescriptor<String> MERGE_AUTH_METHOD_PATTERN_DESCRIPTOR = authMethodPatternProperty("merge");

    // <operation>AuthMethodTypeParamIndex config properties
    private static final PropertyDescriptor<Integer> CREATE_AUTH_METHOD_TYPE_PARAM_INDEX_DESCRIPTOR = authMethodTypeParamIndexProperty("create");
    private static final PropertyDescriptor<Integer> READ_AUTH_METHOD_TYPE_PARAM_INDEX_DESCRIPTOR = authMethodTypeParamIndexProperty("read");
    private static final PropertyDescriptor<Integer> UPDATE_AUTH_METHOD_TYPE_PARAM_INDEX_DESCRIPTOR = authMethodTypeParamIndexProperty("update");
    private static final PropertyDescriptor<Integer> DELETE_AUTH_METHOD_TYPE_PARAM_INDEX_DESCRIPTOR = authMethodTypeParamIndexProperty("delete");
    private static final PropertyDescriptor<Integer> UNDELETE_AUTH_METHOD_TYPE_PARAM_INDEX_DESCRIPTOR = authMethodTypeParamIndexProperty("undelete");
    private static final PropertyDescriptor<Integer> MERGE_AUTH_METHOD_TYPE_PARAM_INDEX_DESCRIPTOR = authMethodTypeParamIndexProperty("merge");

    // Auth method config property correlation information
    private static final Map<PropertyDescriptor<String>, PropertyDescriptor<Integer>> AUTH_METHOD_TO_TYPE_PARAM_INDEX_MAP = new HashMap<PropertyDescriptor<String>, PropertyDescriptor<Integer>>() {
        {
            put(CREATE_AUTH_METHOD_PATTERN_DESCRIPTOR, CREATE_AUTH_METHOD_TYPE_PARAM_INDEX_DESCRIPTOR);
            put(READ_AUTH_METHOD_PATTERN_DESCRIPTOR, READ_AUTH_METHOD_TYPE_PARAM_INDEX_DESCRIPTOR);
            put(UPDATE_AUTH_METHOD_PATTERN_DESCRIPTOR, UPDATE_AUTH_METHOD_TYPE_PARAM_INDEX_DESCRIPTOR);
            put(DELETE_AUTH_METHOD_PATTERN_DESCRIPTOR, DELETE_AUTH_METHOD_TYPE_PARAM_INDEX_DESCRIPTOR);
            put(UNDELETE_AUTH_METHOD_PATTERN_DESCRIPTOR, UNDELETE_AUTH_METHOD_TYPE_PARAM_INDEX_DESCRIPTOR);
            put(MERGE_AUTH_METHOD_PATTERN_DESCRIPTOR, MERGE_AUTH_METHOD_TYPE_PARAM_INDEX_DESCRIPTOR);
        }
    };
    private static final Map<PropertyDescriptor<String>, String> AUTH_METHOD_TO_DML_OPERATION_MAP = new HashMap<PropertyDescriptor<String>, String>() {
        {
            put(CREATE_AUTH_METHOD_PATTERN_DESCRIPTOR, IS_CREATEABLE);
            put(READ_AUTH_METHOD_PATTERN_DESCRIPTOR, IS_ACCESSIBLE);
            put(UPDATE_AUTH_METHOD_PATTERN_DESCRIPTOR, IS_UPDATEABLE);
            put(DELETE_AUTH_METHOD_PATTERN_DESCRIPTOR, IS_DELETABLE);
            put(UNDELETE_AUTH_METHOD_PATTERN_DESCRIPTOR, IS_UNDELETABLE);
            put(MERGE_AUTH_METHOD_PATTERN_DESCRIPTOR, IS_MERGEABLE);
        }
    };

    // Compiled pattern cache for configured method name patterns
    private final Map<String, Pattern> compiledAuthMethodPatternCache = new HashMap<>();

    private Map<String, String> varToTypeMapping;
    private HashMultimap<String, String> typeToDMLOperationMapping;
    private Map<String, String> checkedTypeToDMLOperationViaESAPI;
    private HashMultimap<String, String> checkedTypeToDMLOperationsViaAuthPattern;
    private Map<String, ASTMethod> classMethods;
    private String className;

    public ApexCRUDViolationRule() {
        // Register auth method config properties
        for (Map.Entry<PropertyDescriptor<String>, PropertyDescriptor<Integer>> entry : AUTH_METHOD_TO_TYPE_PARAM_INDEX_MAP.entrySet()) {
            PropertyDescriptor<String> authMethodPatternDescriptor = entry.getKey();
            PropertyDescriptor<Integer> authMethodTypeParamIndexDescriptor = entry.getValue();
            definePropertyDescriptor(authMethodPatternDescriptor);
            definePropertyDescriptor(authMethodTypeParamIndexDescriptor);
        }
    }

    @Override
    public void start(RuleContext ctx) {
        // At the start of each rule execution, these member variables need to be fresh. So they're initialized in the
        // .start() method instead of the constructor, since .start() is called before every execution.
        varToTypeMapping = new HashMap<>();
        typeToDMLOperationMapping = HashMultimap.create();
        checkedTypeToDMLOperationViaESAPI = new HashMap<>();
        checkedTypeToDMLOperationsViaAuthPattern = HashMultimap.create();
        classMethods = new WeakHashMap<>();
        className = null;
        super.start(ctx);
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
        if (Helper.isAnyDatabaseMethodCall(node)) {

            if (hasAccessLevelArgument(node)) {
                return data;
            }

            switch (node.getMethodName().toLowerCase(Locale.ROOT)) {
            case "insert":
            case "insertasync":
            case "insertimmediate":
                checkForCRUD(node, data, IS_CREATEABLE);
                break;
            case "update":
            case "updateasync":
            case "updateimmediate":
                checkForCRUD(node, data, IS_UPDATEABLE);
                break;
            case "delete":
            case "deleteasync":
            case "deleteimmediate":
                checkForCRUD(node, data, IS_DELETABLE);
                break;
            case "undelete":
                checkForCRUD(node, data, IS_UNDELETABLE);
                break;
            case "upsert":
                checkForCRUD(node, data, IS_CREATEABLE);
                checkForCRUD(node, data, IS_UPDATEABLE);
                break;
            case "merge":
                checkForCRUD(node, data, IS_MERGEABLE);
                break;
            default:
                break;
            }

        } else {
            collectCRUDMethodLevelChecks(node);
        }

        return data;
    }

    /**
     * Checks whether any parameter is of type "AccessLevel". It doesn't check
     * whether it is "USER_MODE" or "SYSTEM_MODE", because this rule doesn't
     * report a violation for neither.
     *
     * @param node the Database DML method call
     */
    private boolean hasAccessLevelArgument(ASTMethodCallExpression node) {
        for (int i = 0; i < node.getNumChildren(); i++) {
            ApexNode<?> argument = node.getChild(i);
            if (argument instanceof ASTVariableExpression
                    && argument.getFirstChildOfType(ASTReferenceExpression.class) != null) {
                ASTReferenceExpression ref = argument.getFirstChildOfType(ASTReferenceExpression.class);
                List<String> names = ref.getNames();
                if (names.size() == 1 && ACCESS_LEVEL.equalsIgnoreCase(names.get(0))) {
                    return true;
                } else if (names.size() == 2 && "System".equalsIgnoreCase(names.get(0)) && ACCESS_LEVEL.equalsIgnoreCase(names.get(1))) {
                    return true;
                }
            }
        }
        return false;
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
    public Object visit(ASTDmlUndeleteStatement node, Object data) {
        checkForCRUD(node, data, IS_UNDELETABLE);
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
    public Object visit(ASTParameter node, Object data) {
        String type = node.getType();
        addVariableToMapping(Helper.getFQVariableName(node), type);
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

        return super.visit(node, data);
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

            // ESAPI doesn't provide support for undelete or merge

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

        // Check any configured authorization class library patterns
        for (PropertyDescriptor<String> authMethodPatternDescriptor : AUTH_METHOD_TO_TYPE_PARAM_INDEX_MAP.keySet()) {
            extractObjectTypeFromConfiguredMethodPatternInvocation(node, authMethodPatternDescriptor);
        }
    }

    private boolean isLastMethodName(final ASTMethodCallExpression methodNode, final String className,
            final String methodName) {
        final ASTReferenceExpression reference = methodNode.getFirstChildOfType(ASTReferenceExpression.class);
        if (reference != null && !reference.getNames().isEmpty()) {
            if (reference.getNames().get(reference.getNames().size() - 1)
                    .equalsIgnoreCase(className) && Helper.isMethodName(methodNode, methodName)) {
                return true;
            }
        }

        return false;
    }

    private boolean isWithSecurityEnforced(final ApexNode<?> node) {
        return node instanceof ASTSoqlExpression
                && WITH_SECURITY_ENFORCED.matcher(((ASTSoqlExpression) node).getQuery()).matches();
    }

    //For USER_MODE
    private boolean isWithUserMode(final ApexNode<?> node) {
        return node instanceof ASTSoqlExpression
            && WITH_USER_MODE.matcher(((ASTSoqlExpression) node).getQuery()).matches();
    }

    //For System Mode
    private boolean isWithSystemMode(final ApexNode<?> node) {
        return node instanceof ASTSoqlExpression
            && WITH_SYSTEM_MODE.matcher(((ASTSoqlExpression) node).getQuery()).matches();
    }

    private String getType(final ASTMethodCallExpression methodNode) {
        final ASTReferenceExpression reference = methodNode.getFirstChildOfType(ASTReferenceExpression.class);
        if (!reference.getNames().isEmpty()) {
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

        checkInlineObject(node, data, crudMethod);
        checkInlineNonArgsObject(node, data, crudMethod);

        final ASTVariableExpression variable = node.getFirstChildOfType(ASTVariableExpression.class);
        if (variable != null) {
            final String type = varToTypeMapping.get(Helper.getFQVariableName(variable));
            if (type != null) {
                StringBuilder typeCheck = new StringBuilder().append(node.getDefiningType())
                        .append(":").append(type);

                validateCRUDCheckPresent(node, data, crudMethod, typeCheck.toString());
            }
        }

        final ASTNewListLiteralExpression inlineListLiteral = node.getFirstChildOfType(ASTNewListLiteralExpression.class);
        if (inlineListLiteral != null) {
            checkInlineObject(inlineListLiteral, data, crudMethod);
            checkInlineNonArgsObject(inlineListLiteral, data, crudMethod);
        }

        final ASTNewListInitExpression inlineListInit = node.getFirstChildOfType(ASTNewListInitExpression.class);
        if (inlineListInit != null) {
            checkInlineObject(inlineListInit, data, crudMethod);
            checkInlineNonArgsObject(inlineListInit, data, crudMethod);
        }
    }

    private void checkInlineObject(final ApexNode<?> node, final Object data, final String crudMethod) {

        final ASTNewKeyValueObjectExpression newObj = node.getFirstChildOfType(ASTNewKeyValueObjectExpression.class);
        if (newObj != null) {
            final String type = Helper.getFQVariableName(newObj);
            validateCRUDCheckPresent(node, data, crudMethod, type);
        }
    }

    private void checkInlineNonArgsObject(final ApexNode<?> node, final Object data, final String crudMethod) {

        final ASTNewObjectExpression newEmptyObj = node.getFirstChildOfType(ASTNewObjectExpression.class);
        if (newEmptyObj != null) {
            final String type = Helper.getFQVariableName(newEmptyObj);
            validateCRUDCheckPresent(node, data, crudMethod, type);
        }
    }

    private Set<ASTMethodCallExpression> getPreviousMethodCalls(final ApexNode<?> self) {
        final Set<ASTMethodCallExpression> innerMethodCalls = new HashSet<>();
        final ASTMethod outerMethod = self.getFirstParentOfType(ASTMethod.class);
        if (outerMethod != null) {
            final ASTBlockStatement blockStatement = outerMethod.getFirstChildOfType(ASTBlockStatement.class);
            recursivelyEvaluateCRUDMethodCalls(self, innerMethodCalls, blockStatement);

            final List<ASTMethod> constructorMethods = findConstructorMethods();
            for (ASTMethod method : constructorMethods) {
                innerMethodCalls.addAll(method.findDescendantsOfType(ASTMethodCallExpression.class));
            }

            // some methods might be within this class
            mapCallToMethodDecl(self, innerMethodCalls, new ArrayList<>(innerMethodCalls));
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
            } else {
                // If we couldn't resolve it locally, add any calls for configured authorization patterns
                if (isAuthMethodInvocation(node)) {
                    innerMethodCalls.add(node);
                }
            }
        }
    }

    private List<ASTMethod> findConstructorMethods() {
        final List<ASTMethod> ret = new ArrayList<>();
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
        final boolean hasMapping = checkedTypeToDMLOperationViaESAPI.containsKey(typeToCheck);
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

    private boolean validateCRUDCheckPresent(final ApexNode<?> node, final Object data, final String crudMethod,
            final String typeCheck) {
        boolean missingKey = !typeToDMLOperationMapping.containsKey(typeCheck);
        boolean isImproperDMLCheck = !isProperESAPICheckForDML(typeCheck, crudMethod)
                && !isProperAuthPatternBasedCheckForDML(typeCheck, crudMethod);
        boolean noSecurityEnforced = !isWithSecurityEnforced(node);
        boolean noUserMode = !isWithUserMode(node);
        boolean noSystemMode = !isWithSystemMode(node);
        if (missingKey) {
            if (isImproperDMLCheck) {
                if (noSecurityEnforced && noUserMode && noSystemMode) {
                    addViolation(data, node);
                    return true;
                }
            }
        } else {
            boolean properChecksHappened = false;

            Set<String> dmlOperationsChecked = typeToDMLOperationMapping.get(typeCheck);
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
                return true;
            }
        }
        return false;
    }

    private void checkForAccessibility(final ASTSoqlExpression node, Object data) {
        // TODO: This includes sub-relation queries which are incorrectly flagged because you authorize the type
        //  and not the sub-relation name. Should we (optionally) exclude sub-relations until/unless they can be
        //  resolved to the proper SObject type?
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
        boolean violationAdded = false;
        final ASTVariableDeclaration variableDecl = node.getFirstParentOfType(ASTVariableDeclaration.class);
        if (variableDecl != null) {
            String type = variableDecl.getType();
            type = getSimpleType(type);
            StringBuilder typeCheck = new StringBuilder().append(variableDecl.getDefiningType())
                    .append(":").append(type);

            if (typesFromSOQL.isEmpty()) {
                violationAdded = validateCRUDCheckPresent(node, data, ANY, typeCheck.toString());
            } else {
                for (String typeFromSOQL : typesFromSOQL) {
                    violationAdded |= validateCRUDCheckPresent(node, data, ANY, typeFromSOQL);
                }
            }
        }

        // If the node's already in violation, we don't need to keep checking.
        if (violationAdded) {
            return;
        }

        final ASTAssignmentExpression assignment = node.getFirstParentOfType(ASTAssignmentExpression.class);
        if (assignment != null) {
            final ASTVariableExpression variable = assignment.getFirstChildOfType(ASTVariableExpression.class);
            if (variable != null) {
                String variableWithClass = Helper.getFQVariableName(variable);
                if (varToTypeMapping.containsKey(variableWithClass)) {
                    String type = varToTypeMapping.get(variableWithClass);
                    if (typesFromSOQL.isEmpty()) {
                        violationAdded = validateCRUDCheckPresent(node, data, ANY, type);
                    } else {
                        for (String typeFromSOQL : typesFromSOQL) {
                            violationAdded |= validateCRUDCheckPresent(node, data, ANY, typeFromSOQL);
                        }
                    }
                }
            }

        }

        // If the node's already in violation, we don't need to keep checking.
        if (violationAdded) {
            return;
        }

        final ASTReturnStatement returnStatement = node.getFirstParentOfType(ASTReturnStatement.class);
        if (returnStatement != null) {
            if (typesFromSOQL.isEmpty()) {
                violationAdded = validateCRUDCheckPresent(node, data, ANY, returnType);
            } else {
                for (String typeFromSOQL : typesFromSOQL) {
                    violationAdded |= validateCRUDCheckPresent(node, data, ANY, typeFromSOQL);
                }
            }
        }

        // If the node's already in violation, we don't need to keep checking.
        if (violationAdded) {
            return;
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

    // Configured authorization method pattern support

    private static PropertyDescriptor<String> authMethodPatternProperty(String operation) {
        final String propertyName = operation + "AuthMethodPattern";
        return stringProperty(propertyName)
                .desc("A regular expression for one or more custom " + operation + " authorization method name patterns.")
                .defaultValue("")
                .build();
    }

    private static PropertyDescriptor<Integer> authMethodTypeParamIndexProperty(String operation) {
        final String propertyName = operation + "AuthMethodTypeParamIndex";
        return intProperty(propertyName)
                .desc("The 0-based index of the " + S_OBJECT_TYPE + " parameter for the custom " + operation + " authorization method. Defaults to 0.")
                .defaultValue(0)
                .build();
    }

    private boolean isAuthMethodInvocation(final ASTMethodCallExpression methodNode) {
        for (PropertyDescriptor<String> authMethodPatternDescriptor : AUTH_METHOD_TO_TYPE_PARAM_INDEX_MAP.keySet()) {
            if (isAuthMethodInvocation(methodNode, authMethodPatternDescriptor)) {
                return true;
            }
        }
        return false;
    }

    private void extractObjectTypeFromConfiguredMethodPatternInvocation(final ASTMethodCallExpression methodNode, final PropertyDescriptor<String> authMethodPatternDescriptor) {
        if (isAuthMethodInvocation(methodNode, authMethodPatternDescriptor)) {
            // See which parameter index contains the object type expression and try to find that invocation argument
            final PropertyDescriptor<Integer> authMethodTypeParamIndexDescriptor = AUTH_METHOD_TO_TYPE_PARAM_INDEX_MAP.get(authMethodPatternDescriptor);
            final Integer authMethodTypeParamIndex = authMethodTypeParamIndexDescriptor != null ? getProperty(authMethodTypeParamIndexDescriptor) : 0;
            final int numParameters = methodNode.getInputParametersSize();
            if (numParameters > authMethodTypeParamIndex) {
                final List<ASTVariableExpression> parameters = new ArrayList<>(numParameters);
                for (int parameterIndex = 0, numChildren = methodNode.getNumChildren(); parameterIndex < numChildren; parameterIndex++) {
                    final ApexNode<?> childNode = methodNode.getChild(parameterIndex);
                    if (childNode instanceof ASTVariableExpression) {
                        parameters.add((ASTVariableExpression) childNode);
                    }
                }
                // Make sure that it looks like "sObjectType.<objectTypeName>" as VariableExpression > ReferenceExpression
                final ASTVariableExpression sobjectTypeParameterCandidate = parameters.size() > authMethodTypeParamIndex ? parameters.get(authMethodTypeParamIndex) : null;
                if (sobjectTypeParameterCandidate != null && S_OBJECT_TYPE.equalsIgnoreCase(sobjectTypeParameterCandidate.getImage())) {
                    final ASTReferenceExpression objectTypeCandidate = sobjectTypeParameterCandidate.getFirstChildOfType(ASTReferenceExpression.class);
                    if (objectTypeCandidate != null) {
                        final String objectType = objectTypeCandidate.getImage();
                        if (StringUtils.isNotBlank(objectType)) {
                            // Create a (relatively) unique key for this that is prefixed by the current invocation's containing type name
                            final StringBuilder checkedTypeBuilder = new StringBuilder().append(methodNode.getDefiningType())
                                    .append(":").append(objectType);
                            final String checkedType = checkedTypeBuilder.toString();

                            // And get the appropriate DML operation based on this method pattern
                            final String dmlOperation = AUTH_METHOD_TO_DML_OPERATION_MAP.get(authMethodPatternDescriptor);
                            if (StringUtils.isNotBlank(dmlOperation)) {
                                checkedTypeToDMLOperationsViaAuthPattern.put(checkedType, dmlOperation);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isAuthMethodInvocation(final ASTMethodCallExpression methodNode, final PropertyDescriptor<String> authMethodPatternDescriptor) {
        final String authMethodPattern = getProperty(authMethodPatternDescriptor);
        final Pattern compiledAuthMethodPattern = getCompiledAuthMethodPattern(authMethodPattern);
        if (compiledAuthMethodPattern != null) {
            final String fullMethodName = methodNode.getFullMethodName();
            final Matcher authMethodMatcher = compiledAuthMethodPattern.matcher(fullMethodName);
            if (authMethodMatcher.matches()) {
                return true;
            }
        }
        return false;
    }

    private Pattern getCompiledAuthMethodPattern(final String authMethodPattern) {
        Pattern compiledAuthMethodPattern = null;

        if (StringUtils.isNotBlank(authMethodPattern)) {
            // If we haven't previously tried to to compile this pattern, do so now
            if (!compiledAuthMethodPatternCache.containsKey(authMethodPattern)) {
                try {
                    compiledAuthMethodPattern = Pattern.compile(authMethodPattern, Pattern.CASE_INSENSITIVE);
                    compiledAuthMethodPatternCache.put(authMethodPattern, compiledAuthMethodPattern);
                } catch (IllegalArgumentException e) {
                    // Cache a null value so we don't try to compile this particular pattern again
                    compiledAuthMethodPatternCache.put(authMethodPattern, null);
                    throw e;
                }
            } else {
                // Otherwise use the cached value, either the successfully compiled pattern or null if pattern compilation failed
                compiledAuthMethodPattern = compiledAuthMethodPatternCache.get(authMethodPattern);
            }
        }

        return compiledAuthMethodPattern;
    }

    private boolean isProperAuthPatternBasedCheckForDML(final String typeToCheck, final String dmlOperation) {
        final boolean hasMapping = checkedTypeToDMLOperationsViaAuthPattern.containsKey(typeToCheck);
        if (hasMapping) {
            if (ANY.equals(dmlOperation)) {
                return true;
            }

            final Set<String> dmlOperationsChecked = checkedTypeToDMLOperationsViaAuthPattern.get(typeToCheck);
            return dmlOperationsChecked.contains(dmlOperation);
        }

        return false;
    }
}
