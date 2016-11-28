package net.sourceforge.pmd.lang.apex.rule.security;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import apex.jorje.data.ast.Identifier;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlDeleteStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlInsertStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlMergeStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlUpdateStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlUpsertStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDottedExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTField;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTProperty;
import net.sourceforge.pmd.lang.apex.ast.ASTReferenceExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTSoqlExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableExpression;
import net.sourceforge.pmd.lang.apex.ast.AbstractApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

/**
 * Finding missed CRUD checks.
 * 
 * @author sergey.gorbaty
 *
 */
public class ApexCRUDViolationRule extends AbstractApexRule {
	private final HashMap<String, String> varToTypeMapping = new HashMap<>();
	private final HashMap<String, String> typeToDMLOperationMapping = new HashMap<>();
	private final HashSet<String> esapiCheckedTypes = new HashSet<>();

	private static final String IS_CREATEABLE = "isCreateable";
	private static final String IS_DELETABLE = "isDeletable";
	private static final String IS_UPDATEABLE = "isUpdateable";
	private static final String IS_MERGEABLE = "isMergeable";

	private static final String S_OBJECT_TYPE = "sObjectType";
	private static final String GET_DESCRIBE = "getDescribe";

	// ESAPI.accessController().isAuthorizedToView(Lead.sObject, fields)
	private static final String[] ESAPI_ISAUTHORIZED = new String[] { "ESAPI", "accessController",
			"isAuthorizedToView" };
	private static final String[] RESERVED_KEYS_FLS = new String[] { "Schema", S_OBJECT_TYPE };

	// private static final String FIELDS = "fields";
	// private static final String GET_MAP = "getMap";

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
			if (Helper.isMethodCallChain(node, ESAPI_ISAUTHORIZED)) {
				extractObjectTypeFromESAPI(node);
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
		return data;
	}

	@Override
	public Object visit(ASTDmlMergeStatement node, Object data) {
		checkForCRUD(node, data, IS_MERGEABLE);
		return data;
	}

	@Override
	public Object visit(ASTSoqlExpression node, Object data) {
		final ASTVariableDeclaration variable = node.getFirstParentOfType(ASTVariableDeclaration.class);
		if (variable != null) {
			String type = variable.getNode().getLocalInfo().getType().getApexName();
			StringBuilder sb = new StringBuilder().append(variable.getNode().getDefiningType().getApexName())
					.append(":").append(variable.getNode().getLocalInfo().getName());
			varToTypeMapping.put(sb.toString(), type);
		}
		return data;
	}

	@Override
	public Object visit(final ASTVariableDeclaration node, Object data) {
		String type = node.getNode().getLocalInfo().getType().getApexName();
		StringBuilder sb = new StringBuilder().append(node.getNode().getDefiningType().getApexName()).append(":")
				.append(node.getNode().getLocalInfo().getName());
		varToTypeMapping.put(sb.toString(), type);

		return data;

	}

	@Override
	public Object visit(final ASTProperty node, Object data) {
		ASTField field = node.getFirstChildOfType(ASTField.class);
		if (field != null) {
			String fieldName = field.getNode().getFieldInfo().getName();
			String fieldType = field.getNode().getFieldInfo().getType().getApexName();

			StringBuilder sb = new StringBuilder().append(field.getNode().getDefiningType().getApexName()).append(":")
					.append(fieldName);

			varToTypeMapping.put(sb.toString(), fieldType);

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
			// TODO: add real FLS check
			// if (FIELDS.equalsIgnoreCase(strings.get(++flsIndex +
			// RESERVED_KEYS_FLS.length))) {
			// String fieldName = strings.get(++flsIndex +
			// RESERVED_KEYS_FLS.length);
			// }

		}
	}

	private void checkForCRUD(final AbstractApexNode<?> node, final Object data, final String CRUDMethod) {
		final ASTMethod wrappingMethod = node.getFirstParentOfType(ASTMethod.class);
		final ASTUserClass wrappingClass = node.getFirstParentOfType(ASTUserClass.class);

		if (Helper.isTestMethodOrClass(wrappingClass) || Helper.isTestMethodOrClass(wrappingMethod)) {
			return;
		}

		final ASTVariableExpression variable = node.getFirstChildOfType(ASTVariableExpression.class);
		if (variable != null) {
			StringBuilder sb = new StringBuilder().append(node.getNode().getDefiningType().getApexName()).append(":")
					.append(variable.getNode().getIdentifier().value);

			String type = varToTypeMapping.get(sb.toString());
			if (type != null) {
				StringBuilder typeCheck = new StringBuilder().append(node.getNode().getDefiningType()).append(":")
						.append(type);

				if (!typeToDMLOperationMapping.containsKey(typeCheck.toString())) {
					if (!esapiCheckedTypes.contains(typeCheck.toString())) {
						addViolation(data, node);
					}
				} else {
					boolean properChecksHappened = typeToDMLOperationMapping.get(typeCheck.toString())
							.equalsIgnoreCase(CRUDMethod);
					if (!properChecksHappened) {
						addViolation(data, node);
					}
				}
			}
		}
	}

	private void extractObjectTypeFromESAPI(final ASTMethodCallExpression node) {
		final ASTVariableExpression var = node.getFirstChildOfType(ASTVariableExpression.class);
		if (var != null) {
			final ASTReferenceExpression reference = var.getFirstChildOfType(ASTReferenceExpression.class);
			if (reference != null) {
				List<Identifier> identifiers = reference.getNode().getJadtIdentifiers();
				if (identifiers.size() == 1) {
					StringBuilder sb = new StringBuilder().append(node.getNode().getDefiningType().getApexName())
							.append(":").append(identifiers.get(0).value);
					esapiCheckedTypes.add(sb.toString());
				}

			}
		}

	}
}
