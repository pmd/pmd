package net.sourceforge.pmd.lang.apex.rule.security;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import apex.jorje.semantic.ast.expression.VariableExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTBinaryExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTLiteralExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableExpression;
import net.sourceforge.pmd.lang.apex.ast.AbstractApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

/**
 * Insecure HTTP endpoints passed to (req.setEndpoint)
 * req.setHeader('Authorization') should use named credentials
 * 
 * @author sergey.gorbaty
 *
 */
public class ApexInsecureEndpointRule extends AbstractApexRule {
	private static final String SET_ENDPOINT = "setEndpoint";
	private static final Pattern PATTERN = Pattern.compile("^http://.+?$", Pattern.CASE_INSENSITIVE);

	private static final Set<String> httpEndpointStrings = new HashSet<>();

	public ApexInsecureEndpointRule() {
		setProperty(CODECLIMATE_CATEGORIES, new String[] { "Security" });
		setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 100);
		setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
	}

	@Override
	public Object visit(ASTAssignmentExpression node, Object data) {
		findInsecureEndpoints(node, data);
		return data;
	}

	@Override
	public Object visit(ASTVariableDeclaration node, Object data) {
		findInsecureEndpoints(node, data);
		return data;
	}

	@Override
	public Object visit(ASTFieldDeclaration node, Object data) {
		findInsecureEndpoints(node, data);
		return data;
	}

	private void findInsecureEndpoints(AbstractApexNode<?> node, Object data) {
		ASTVariableExpression variableNode = node.getFirstChildOfType(ASTVariableExpression.class);
		findInnerInsecureEndpoints(node, variableNode);

		ASTBinaryExpression binaryNode = node.getFirstChildOfType(ASTBinaryExpression.class);
		if (binaryNode != null) {
			
			findInnerInsecureEndpoints(binaryNode, variableNode);
		}

	}

	private void findInnerInsecureEndpoints(AbstractApexNode<?> node, ASTVariableExpression variableNode) {
		ASTLiteralExpression literalNode = node.getFirstChildOfType(ASTLiteralExpression.class);
		
		if (literalNode != null && variableNode != null) {
			Object o = literalNode.getNode().getLiteral();
			if (o instanceof String) {
				String literal = (String) o;
				if (PATTERN.matcher(literal).matches()) {
					VariableExpression varExpression = variableNode.getNode();
					StringBuilder sb = new StringBuilder().append(varExpression.getDefiningType()).append(":")
							.append(varExpression.getIdentifier().value);
					httpEndpointStrings.add(sb.toString());
				}
			}
		}
	}

	@Override
	public Object visit(ASTMethodCallExpression node, Object data) {
		processInsecureEndpoint(node, data);
		return data;
	}

	private void processInsecureEndpoint(ASTMethodCallExpression node, Object data) {
		if (!Helper.isMethodName(node, SET_ENDPOINT)) {
			return;
		}

		ASTBinaryExpression binaryNode = node.getFirstChildOfType(ASTBinaryExpression.class);
		if (binaryNode != null) {
			runChecks(binaryNode, data);
		}

		runChecks(node, data);

	}

	private void runChecks(AbstractApexNode<?> node, Object data) {
		ASTLiteralExpression literalNode = node.getFirstChildOfType(ASTLiteralExpression.class);
		if (literalNode != null) {
			Object o = literalNode.getNode().getLiteral();
			if (o instanceof String) {
				String literal = (String) o;
				if (PATTERN.matcher(literal).matches()) {
					addViolation(data, literalNode);
				}
			}
		}

		ASTVariableExpression variableNode = node.getFirstChildOfType(ASTVariableExpression.class);
		if (variableNode != null) {
			VariableExpression varExpression = variableNode.getNode();
			StringBuffer sb = new StringBuffer().append(varExpression.getDefiningType()).append(":")
					.append(varExpression.getIdentifier().value);
			if (httpEndpointStrings.contains(sb.toString())) {
				addViolation(data, variableNode);
			}

		}
	}

}
