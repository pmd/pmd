/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.strings;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAdditiveExpression;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.JavaNameOccurrence;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

/**
 * This rule finds concurrent calls to StringBuffer/Builder.append where String literals
 * are used It would be much better to make these calls using one call to
 * .append
 * <p/>
 * example:
 * <p/>
 * <pre>
 * StringBuilder buf = new StringBuilder();
 * buf.append(&quot;Hello&quot;);
 * buf.append(&quot; &quot;).append(&quot;World&quot;);
 * </pre>
 * <p/>
 * This would be more eloquently put as:
 * <p/>
 * <pre>
 * StringBuilder buf = new StringBuilder();
 * buf.append(&quot;Hello World&quot;);
 * </pre>
 * <p/>
 * The rule takes one parameter, threshold, which defines the lower limit of
 * consecutive appends before a violation is created. The default is 1.
 */
public class ConsecutiveLiteralAppendsRule extends AbstractJavaRule {

	private final static Set<Class<?>> BLOCK_PARENTS;

	static {
		BLOCK_PARENTS = new HashSet<Class<?>>();
		BLOCK_PARENTS.add(ASTForStatement.class);
		BLOCK_PARENTS.add(ASTWhileStatement.class);
		BLOCK_PARENTS.add(ASTDoStatement.class);
		BLOCK_PARENTS.add(ASTIfStatement.class);
		BLOCK_PARENTS.add(ASTSwitchStatement.class);
		BLOCK_PARENTS.add(ASTMethodDeclaration.class);
	}

	private static final IntegerProperty THRESHOLD_DESCRIPTOR = new IntegerProperty("threshold", "Max consecutive appends", 1, 10, 1, 1.0f);

	private int threshold = 1;

	public ConsecutiveLiteralAppendsRule() {
		definePropertyDescriptor(THRESHOLD_DESCRIPTOR);
	}

	@Override
	public Object visit(ASTVariableDeclaratorId node, Object data) {

		if (!isStringBuffer(node)) {
			return data;
		}
		threshold = getProperty(THRESHOLD_DESCRIPTOR);

		int concurrentCount = checkConstructor(node, data);
		Node lastBlock = getFirstParentBlock(node);
		Node currentBlock = lastBlock;
		Map<VariableNameDeclaration, List<NameOccurrence>> decls = node.getScope().getDeclarations(VariableNameDeclaration.class);
		Node rootNode = null;
		// only want the constructor flagged if it's really containing strings
		if (concurrentCount >= 1) {
			rootNode = node;
		}
		for (List<NameOccurrence> decl : decls.values()) {
			for (NameOccurrence no : decl) {
			    JavaNameOccurrence jno = (JavaNameOccurrence)no;
				Node n = jno.getLocation();

				currentBlock = getFirstParentBlock(n);

				if (!InefficientStringBufferingRule.isInStringBufferOperation(n, 3, "append")) {
					if (!jno.isPartOfQualifiedName()) {
						checkForViolation(rootNode, data, concurrentCount);
						concurrentCount = 0;
					}
					continue;
				}
				ASTPrimaryExpression s = n.getFirstParentOfType(ASTPrimaryExpression.class);
				int numChildren = s.jjtGetNumChildren();
				for (int jx = 0; jx < numChildren; jx++) {
					Node sn = s.jjtGetChild(jx);
					if (!(sn instanceof ASTPrimarySuffix) || sn.getImage() != null) {
						continue;
					}

					// see if it changed blocks
					if (currentBlock != null && lastBlock != null && !currentBlock.equals(lastBlock)
							|| currentBlock == null ^ lastBlock == null) {
						checkForViolation(rootNode, data, concurrentCount);
						concurrentCount = 0;
					}

					// if concurrent is 0 then we reset the root to report from
					// here
					if (concurrentCount == 0) {
						rootNode = sn;
					}
					if (isAdditive(sn)) {
						concurrentCount = processAdditive(data, concurrentCount, sn, rootNode);
						if (concurrentCount != 0) {
							rootNode = sn;
						}
					} else if (!isAppendingStringLiteral(sn)) {
						checkForViolation(rootNode, data, concurrentCount);
						concurrentCount = 0;
					} else {
						concurrentCount++;
					}
					lastBlock = currentBlock;
				}
			}
		}
		checkForViolation(rootNode, data, concurrentCount);
		return data;
	}

	/**
	 * Determine if the constructor contains (or ends with) a String Literal
	 *
	 * @param node
	 * @return 1 if the constructor contains string argument, else 0
	 */
	private int checkConstructor(ASTVariableDeclaratorId node, Object data) {
		Node parent = node.jjtGetParent();
		if (parent.jjtGetNumChildren() >= 2) {
			ASTArgumentList list = parent.jjtGetChild(1).getFirstDescendantOfType(ASTArgumentList.class);
			if (list != null) {
				ASTLiteral literal = list.getFirstDescendantOfType(ASTLiteral.class);
				if (!isAdditive(list) && literal != null && literal.isStringLiteral()) {
					return 1;
				}
				return processAdditive(data, 0, list, node);
			}
		}
		return 0;
	}

	private int processAdditive(Object data, int concurrentCount, Node sn, Node rootNode) {
		ASTAdditiveExpression additive = sn.getFirstDescendantOfType(ASTAdditiveExpression.class);
		// The additive expression must of be type String to count
		if (additive == null || additive.getType() != null && !TypeHelper.isA(additive, String.class)) {
			return 0;
		}
		// check for at least one string literal
		List<ASTLiteral> literals = additive.findDescendantsOfType(ASTLiteral.class);
		boolean stringLiteralFound = false;
		for (ASTLiteral l : literals) {
		    if (l.isCharLiteral() || l.isStringLiteral()) {
		        stringLiteralFound = true;
		        break;
		    }
		}
		if (!stringLiteralFound) {
		    return 0;
		}

		int count = concurrentCount;
		boolean found = false;
		for (int ix = 0; ix < additive.jjtGetNumChildren(); ix++) {
			Node childNode = additive.jjtGetChild(ix);
			if (childNode.jjtGetNumChildren() != 1 || childNode.hasDescendantOfType(ASTName.class)) {
				if (!found) {
					checkForViolation(rootNode, data, count);
					found = true;
				}
				count = 0;
			} else {
				count++;
			}
		}

		// no variables appended, compiler will take care of merging all the
		// string concats, we really only have 1 then
		if (!found) {
			count = 1;
		}

		return count;
	}

	/**
	 * Checks to see if there is string concatenation in the node.
	 *
	 * This method checks if it's additive with respect to the append method
	 * only.
	 *
	 * @param n
	 *            Node to check
	 * @return true if the node has an additive expression (i.e. "Hello " +
	 *         Const.WORLD)
	 */
	private boolean isAdditive(Node n) {
		List<ASTAdditiveExpression> lstAdditive = n.findDescendantsOfType(ASTAdditiveExpression.class);
		if (lstAdditive.isEmpty()) {
			return false;
		}
		// if there are more than 1 set of arguments above us we're not in the
		// append
		// but a sub-method call
		for (int ix = 0; ix < lstAdditive.size(); ix++) {
			ASTAdditiveExpression expr = lstAdditive.get(ix);
			if (expr.getParentsOfType(ASTArgumentList.class).size() != 1) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Get the first parent. Keep track of the last node though. For If
	 * statements it's the only way we can differentiate between if's and else's
	 * For switches it's the only way we can differentiate between switches
	 *
	 * @param node The node to check
	 * @return The first parent block
	 */
	private Node getFirstParentBlock(Node node) {
		Node parentNode = node.jjtGetParent();

		Node lastNode = node;
		while (parentNode != null && !BLOCK_PARENTS.contains(parentNode.getClass())) {
			lastNode = parentNode;
			parentNode = parentNode.jjtGetParent();
		}
		if (parentNode instanceof ASTIfStatement) {
			parentNode = lastNode;
		} else if (parentNode instanceof ASTSwitchStatement) {
			parentNode = getSwitchParent(parentNode, lastNode);
		}
		return parentNode;
	}

	/**
	 * Determine which SwitchLabel we belong to inside a switch
	 *
	 * @param parentNode The parent node we're looking at
	 * @param lastNode   The last node processed
	 * @return The parent node for the switch statement
	 */
	private Node getSwitchParent(Node parentNode, Node lastNode) {
		int allChildren = parentNode.jjtGetNumChildren();
		ASTSwitchLabel label = null;
		for (int ix = 0; ix < allChildren; ix++) {
			Node n = parentNode.jjtGetChild(ix);
			if (n instanceof ASTSwitchLabel) {
				label = (ASTSwitchLabel) n;
			} else if (n.equals(lastNode)) {
				parentNode = label;
				break;
			}
		}
		return parentNode;
	}

	/**
	 * Helper method checks to see if a violation occurred, and adds a
	 * RuleViolation if it did
	 */
	private void checkForViolation(Node node, Object data, int concurrentCount) {
		if (concurrentCount > threshold) {
			String[] param = { String.valueOf(concurrentCount) };
			addViolation(data, node, param);
		}
	}

	private boolean isAppendingStringLiteral(Node node) {
		Node n = node;
		while (n.jjtGetNumChildren() != 0 && !(n instanceof ASTLiteral)) {
			n = n.jjtGetChild(0);
		}
		return n instanceof ASTLiteral;
	}

	private static boolean isStringBuffer(ASTVariableDeclaratorId node) {

		if (node.getType() != null) {
			//return node.getType().equals(StringBuffer.class);
			return TypeHelper.isEither(node, StringBuffer.class, StringBuilder.class);
		}
		Node nn = node.getTypeNameNode();
		if (nn == null || nn.jjtGetNumChildren() == 0) {
			return false;
		}
		return TypeHelper.isEither((TypeNode) nn.jjtGetChild(0), StringBuffer.class, StringBuilder.class);
	}
}