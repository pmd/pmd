package net.sourceforge.pmd.lang.apex.rule.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import apex.jorje.semantic.ast.expression.VariableExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTBinaryExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTLiteralExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableExpression;
import net.sourceforge.pmd.lang.apex.ast.AbstractApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

/**
 * Detects if variables in Database.query(variable) is escaped with
 * String.escapeSingleQuotes
 * 
 * @author sergey.gorbaty
 *
 */
public class ApexSOQLInjectionRule extends AbstractApexRule {
	private static final String JOIN = "join";
	private static final String ESCAPE_SINGLE_QUOTES = "escapeSingleQuotes";
	private static final String STRING = "String";
	private final static String DATABASE = "Database";
	private final static String QUERY = "query";
	final private Set<String> safeVariables = new HashSet<>();

	public ApexSOQLInjectionRule() {
		setProperty(CODECLIMATE_CATEGORIES, new String[] { "Security" });
		setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 100);
		setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
	}

	private void findSanitizedVariables(AbstractApexNode<?> m) {
		final ASTVariableExpression left = m.getFirstChildOfType(ASTVariableExpression.class);
		final ASTLiteralExpression literal = m.getFirstChildOfType(ASTLiteralExpression.class);
		final ASTMethodCallExpression right = m.getFirstChildOfType(ASTMethodCallExpression.class);

		if (literal != null) {
			if (left != null) {
				final VariableExpression l = left.getNode();
				StringBuilder sb = new StringBuilder().append(l.getDefiningType()).append(":")
						.append(l.getIdentifier().value);
				safeVariables.add(sb.toString());
			}
		}

		if (right != null) {
			if (Helper.isMethodName(right, STRING, ESCAPE_SINGLE_QUOTES)) {
				if (left != null) {
					final VariableExpression l = left.getNode();
					StringBuilder sb = new StringBuilder().append(l.getDefiningType()).append(":")
							.append(l.getIdentifier().value);
					safeVariables.add(sb.toString());
				}
			}
		}
	}

	private void reportChildren(ASTMethodCallExpression m, Object data) {
		final List<ASTBinaryExpression> binaryExpr = m.findChildrenOfType(ASTBinaryExpression.class);
		for (ASTBinaryExpression b : binaryExpr) {
			List<ASTVariableExpression> vars = b.findDescendantsOfType(ASTVariableExpression.class);
			for (ASTVariableExpression v : vars) {
				final VariableExpression l = v.getNode();
				StringBuilder sb = new StringBuilder().append(l.getDefiningType()).append(":")
						.append(l.getIdentifier().value);

				if (safeVariables.contains(sb.toString())) {
					continue;
				}
				ASTMethodCallExpression parentCall = v.getFirstParentOfType(ASTMethodCallExpression.class);
				boolean isSafeMethod = Helper.isMethodName(parentCall, STRING, ESCAPE_SINGLE_QUOTES)
						|| Helper.isMethodName(parentCall, STRING, JOIN);

				if (!isSafeMethod) {
					addViolation(data, v);
				}
			}
		}
	}

	@Override
	public Object visit(ASTUserClass node, Object data) {

		if (Helper.isTestMethodOrClass(node)) {
			return data;
		}

		// baz = String.escapeSignleQuotes(...);
		final List<ASTAssignmentExpression> assignmentCalls = node.findDescendantsOfType(ASTAssignmentExpression.class);

		for (ASTAssignmentExpression a : assignmentCalls) {
			findSanitizedVariables(a);
		}

		final List<ASTFieldDeclaration> fieldExpr = node.findDescendantsOfType(ASTFieldDeclaration.class);
		for (ASTFieldDeclaration a : fieldExpr) {
			findSanitizedVariables(a);
		}

		// String foo = String.escapeSignleQuotes(...);
		final List<ASTVariableDeclaration> variableDecl = node.findDescendantsOfType(ASTVariableDeclaration.class);
		for (ASTVariableDeclaration a : variableDecl) {
			findSanitizedVariables(a);
		}

		// Database.query(...)
		final List<ASTMethodCallExpression> potentialDbQueryCalls = node
				.findDescendantsOfType(ASTMethodCallExpression.class);

		for (ASTMethodCallExpression m : potentialDbQueryCalls) {

			if (!Helper.isTestMethodOrClass(m) && Helper.isMethodName(m, DATABASE, QUERY)) {
				reportChildren(m, data);
			}
		}

		return data;
	}

}
