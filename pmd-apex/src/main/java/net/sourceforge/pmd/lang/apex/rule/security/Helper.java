package net.sourceforge.pmd.lang.apex.rule.security;

import java.util.Arrays;
import java.util.List;

import apex.jorje.semantic.ast.expression.MethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlDeleteStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlInsertStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlMergeStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlUndeleteStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlUpdateStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlUpsertStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDottedExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;
import net.sourceforge.pmd.lang.apex.ast.ASTReferenceExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTSoqlExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTSoslExpression;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;

public final class Helper {
	private Helper() {
		throw new AssertionError("Can't instantiate helper classes");
	}

	static boolean isTestMethodOrClass(final ApexNode<?> node) {
		final List<ASTModifierNode> modifierNode = node.findChildrenOfType(ASTModifierNode.class);
		for (final ASTModifierNode m : modifierNode) {
			if (m.getNode().getModifiers().isTest()) {
				return true;
			}
		}
		return false;
	}

	static boolean foundAnySOQLorSOSL(final ApexNode<?> node) {
		final List<ASTSoqlExpression> dmlSoqlExpression = node.findDescendantsOfType(ASTSoqlExpression.class);
		final List<ASTSoslExpression> dmlSoslExpression = node.findDescendantsOfType(ASTSoslExpression.class);

		if (dmlSoqlExpression.isEmpty() && dmlSoslExpression.isEmpty()) {
			return false;
		}

		return true;
	}

	/**
	 * Finds DML operations in a given node descendants' path
	 * 
	 * @param node
	 * 
	 * @return true if found DML operations in node descendants
	 */
	static boolean foundAnyDML(final ApexNode<?> node) {

		final List<ASTDmlUpsertStatement> dmlUpsertStatement = node.findDescendantsOfType(ASTDmlUpsertStatement.class);
		final List<ASTDmlUpdateStatement> dmlUpdateStatement = node.findDescendantsOfType(ASTDmlUpdateStatement.class);
		final List<ASTDmlUndeleteStatement> dmlUndeleteStatement = node
				.findDescendantsOfType(ASTDmlUndeleteStatement.class);
		final List<ASTDmlMergeStatement> dmlMergeStatement = node.findDescendantsOfType(ASTDmlMergeStatement.class);
		final List<ASTDmlInsertStatement> dmlInsertStatement = node.findDescendantsOfType(ASTDmlInsertStatement.class);
		final List<ASTDmlDeleteStatement> dmlDeleteStatement = node.findDescendantsOfType(ASTDmlDeleteStatement.class);

		if (dmlUpsertStatement.isEmpty() && dmlUpdateStatement.isEmpty() && dmlUndeleteStatement.isEmpty()
				&& dmlMergeStatement.isEmpty() && dmlInsertStatement.isEmpty() && dmlDeleteStatement.isEmpty()) {
			return false;
		}

		return true;
	}

	static boolean isMethodName(final ASTMethodCallExpression methodNode, final String className,
			final String methodName) {
		final ASTReferenceExpression reference = methodNode.getFirstChildOfType(ASTReferenceExpression.class);
		if (reference.getNode().getJadtIdentifiers().size() == 1) {
			if (reference.getNode().getJadtIdentifiers().get(0).value.equalsIgnoreCase(className)
					&& isMethodName(methodNode, methodName)) {
				return true;
			}
		}

		return false;
	}

	static boolean isMethodName(final ASTMethodCallExpression m, final String methodName) {
		return isMethodName(m.getNode(), methodName);
	}

	static boolean isMethodName(final MethodCallExpression m, final String methodName) {
		return m.getMethodName().equalsIgnoreCase(methodName);
	}

	static boolean isMethodCallChain(final ASTMethodCallExpression methodNode, final String... methodNames) {
		String methodName = methodNames[methodNames.length - 1];
		if (Helper.isMethodName(methodNode, methodName)) {
			final ASTReferenceExpression reference = methodNode.getFirstChildOfType(ASTReferenceExpression.class);
			if (reference != null) {
				final ASTDottedExpression dottedExpression = reference.getFirstChildOfType(ASTDottedExpression.class);
				if (dottedExpression != null) {
					final ASTMethodCallExpression nestedMethod = dottedExpression
							.getFirstChildOfType(ASTMethodCallExpression.class);
					if (nestedMethod != null) {
						String[] newMethodNames = Arrays.copyOf(methodNames, methodNames.length - 1);
						return isMethodCallChain(nestedMethod, newMethodNames);
					} else {
						String[] newClassName = Arrays.copyOf(methodNames, methodNames.length - 1);
						if (newClassName.length == 1) {
							return Helper.isMethodName(methodNode, newClassName[0], methodName);
						}
					}
				}

			}
		}

		return false;
	}

}
