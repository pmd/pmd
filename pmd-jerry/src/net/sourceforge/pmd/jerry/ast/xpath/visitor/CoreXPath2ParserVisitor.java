package net.sourceforge.pmd.jerry.ast.xpath.visitor;

import java.io.Reader;
import java.io.StringReader;

import net.sourceforge.pmd.jerry.ast.xpath.ASTAbbrevForwardStep;
import net.sourceforge.pmd.jerry.ast.xpath.ASTAbbrevReverseStep;
import net.sourceforge.pmd.jerry.ast.xpath.ASTAdditiveExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTAndExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTCastExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTComparisonExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTContextItemExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTForExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTForwardAxis;
import net.sourceforge.pmd.jerry.ast.xpath.ASTIfExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTInstanceofExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTIntersectExceptExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTMultiplicativeExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTNodeTest;
import net.sourceforge.pmd.jerry.ast.xpath.ASTOrExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTParenthesizedExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTPathExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTPredicate;
import net.sourceforge.pmd.jerry.ast.xpath.ASTPredicateList;
import net.sourceforge.pmd.jerry.ast.xpath.ASTQuantifiedExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTRangeExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTReverseAxis;
import net.sourceforge.pmd.jerry.ast.xpath.ASTSingleType;
import net.sourceforge.pmd.jerry.ast.xpath.ASTSlash;
import net.sourceforge.pmd.jerry.ast.xpath.ASTSlashSlash;
import net.sourceforge.pmd.jerry.ast.xpath.ASTStepExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTTreatExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTUnaryExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTUnionExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTXPath;
import net.sourceforge.pmd.jerry.ast.xpath.Node;
import net.sourceforge.pmd.jerry.ast.xpath.SimpleNode;
import net.sourceforge.pmd.jerry.ast.xpath.XPath2Parser;
import net.sourceforge.pmd.jerry.ast.xpath.custom.OperatorNode;
import net.sourceforge.pmd.jerry.xpath.AxisEnum;
import net.sourceforge.pmd.jerry.xpath.OperatorEnum;

public class CoreXPath2ParserVisitor extends AbstractXPath2ParserVisitor {

	public static String toCore(String query) {
		// First convert to unabbreviated XPath
		String unabbreviated = PrintXPath2ParserVisitor.unabbreviate(query);

		// Get XPath AST
		Reader reader = new StringReader(unabbreviated);
		XPath2Parser parser = new XPath2Parser(reader);
		ASTXPath xpath = parser.XPath();
		xpath.dump("");

		// Output XPath Core
		CoreXPath2ParserVisitor visitor = new CoreXPath2ParserVisitor();
		xpath.jjtAccept(visitor, null);
		return visitor.getOutput();
	}

	private void visitOperator(OperatorNode node, int operatorIndex, Object data) {

		// If no operators, just visit the children
		if (node.getNumOperators() == 0) {
			node.childrenAccept(this, data);
			return;
		}

		// Otherwise, we have multiple operands
		OperatorEnum operator = node.getOperator(operatorIndex);
		String postFunction = null;
		final String operatorFunction;
		final String convertExpected;
		switch (operator) {
		case ADDITION:
			operatorFunction = "fs:plus";
			convertExpected = "1.0E0";
			break;
		case SUBTRACTION:
			operatorFunction = "fs:minus";
			convertExpected = "1.0E0";
			break;
		case MULTIPLICATION:
			operatorFunction = "fs:times";
			convertExpected = "1.0E0";
			break;
		case DIVISION:
			operatorFunction = "fs:div";
			convertExpected = "1.0E0";
			break;
		case INTEGER_DIVISION:
			// TODO Rules in Formal Semantics spec for "Additive
			// Expressions", state that 'idiv' cast arguments to xs:integer
			// by calling fs:convert-operand with a '1'. However,
			// fs:convert-operand will turn it into an xs:double as the
			// convertExpected value is numeric.
			operatorFunction = "fs:idiv";
			convertExpected = "1";
			break;
		case MODULUS:
			operatorFunction = "fs:mod";
			convertExpected = "1.0E0";
			break;
		case UNARY_PLUS:
			operatorFunction = "fs:unary-plus";
			convertExpected = "1.0E0";
			break;
		case UNARY_MINUS:
			operatorFunction = "fs:unary-minus";
			convertExpected = "1.0E0";
			break;
		case VALUE_COMPARISION_EQUAL:
			operatorFunction = "fs:eq";
			convertExpected = "string";
			break;
		case VALUE_COMPARISION_NOT_EQUAL:
			operatorFunction = "fs:ne";
			convertExpected = "string";
			break;
		case VALUE_COMPARISION_LESSER_THAN:
			operatorFunction = "fs:lt";
			convertExpected = "string";
			break;
		case VALUE_COMPARISION_LESSER_THAN_OR_EQUAL:
			operatorFunction = "fs:le";
			convertExpected = "string";
			break;
		case VALUE_COMPARISION_GREATER_THAN:
			operatorFunction = "fs:gt";
			convertExpected = "string";
			break;
		case VALUE_COMPARISION_GREATER_THAN_OR_EQUAL:
			operatorFunction = "fs:ge";
			convertExpected = "string";
			break;
		case NODE_COMPARISION_IS:
			operatorFunction = "fs:is-same-node";
			convertExpected = null;
			break;
		case NODE_COMPARISION_PRECEEDS:
			operatorFunction = "fs:node-before";
			convertExpected = null;
			break;
		case NODE_COMPARISION_FOLLOWS:
			operatorFunction = "fs:node-after";
			convertExpected = null;
			break;
		case SEQUENCE_UNION:
			postFunction = "fs:apply-ordering-mode";
			operatorFunction = "op:union";
			convertExpected = null;
			break;
		case SEQUENCE_INTERSECT:
			postFunction = "fs:apply-ordering-mode";
			operatorFunction = "op:intersect";
			convertExpected = null;
			break;
		case SEQUENCE_EXCEPT:
			postFunction = "fs:apply-ordering-mode";
			operatorFunction = "op:except";
			convertExpected = null;
			break;
		default:
			throw new IllegalStateException("Unexpected operator: " + operator);
		}

		// Start post function?
		if (postFunction != null) {
			print(postFunction + "(");
		}

		// Start operator function
		print(operatorFunction + "(");

		//
		// 1st operand
		//

		// Convert?
		if (convertExpected != null) {
			print("fs:convert-operand(");
			print("fn:data((");
		}
		if (operator.isBinary()) {
			node.jjtGetChild(operatorIndex).jjtAccept(this, data);
		} else {
			// Not the last, then recurse
			if (operatorIndex != node.getNumOperators() - 1) {
				visitOperator(node, operatorIndex + 1, data);
			}
			// Last
			else {
				node.jjtGetChild(0).jjtAccept(this, data);
			}
		}
		if (convertExpected != null) {
			print("))");
			print(", ");
			print(convertExpected);
			print(")");
		}

		//
		// 2nd operand?
		//
		if (operator.isBinary()) {
			print(", ");

			// Convert?
			if (convertExpected != null) {
				print("fs:convert-operand(");
				print("fn:data((");
			}
			// Not the last, then recurse
			if (operatorIndex != node.getNumOperators() - 1) {
				visitOperator(node, operatorIndex + 1, data);
			}
			// Last
			else {
				node.jjtGetChild(operatorIndex + 1).jjtAccept(this, data);
			}
			if (convertExpected != null) {
				print("))");
				print(", ");
				print(convertExpected);
				print(")");
			}
		}

		// End operator function
		print(")");

		// End post function?
		if (postFunction != null) {
			print(")");
		}
	}

	public Object visit(ASTAbbrevForwardStep node, Object data) {
		if (node.getNumAxes() != 0) {
			throw new IllegalStateException("AST must be unabbreviated!");
		}
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTAbbrevReverseStep node, Object data) {
		throw new IllegalStateException("AST must be unabbreviated!");
	}

	public Object visit(ASTAdditiveExpr node, Object data) {
		visitOperator(node, 0, data);
		return null;
	}

	public Object visit(ASTAndExpr node, Object data) {
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			if (i > 0) {
				print(" and ");
			}
			print("fn:boolean((");
			node.jjtGetChild(i).jjtAccept(this, data);
			print("))");
		}
		return null;
	}

	public Object visit(ASTCastExpr node, Object data) {
		if (node.jjtGetNumChildren() == 2) {
			Node expr = node.jjtGetChild(0);
			ASTSingleType singleType = (ASTSingleType) node.jjtGetChild(1);

			print("let $v as xs:anyAtomicType := fn:data((");
			expr.jjtAccept(this, data);
			print(")) return ");
			if ("?".equals(singleType.getImage())) {
				print("typeswitch ($v)");
				print(" case $fs:new as empty-sequence() return ()");
				print(" default $fs:new return ");
			}
			print("$v cast as ");
			singleType.jjtAccept(this, data);
		} else {
			node.childrenAccept(this, data);
		}
		return null;
	}

	public Object visit(ASTComparisonExpr node, Object data) {
		if (node.jjtGetNumChildren() > 1) {

			OperatorEnum operator = node.getOperator(0);
			if (operator.compareTo(OperatorEnum.GENERAL_COMPARISION_EQUAL) >= 0
					&& operator
							.compareTo(OperatorEnum.GENERAL_COMPARISION_GREATER_THAN_OR_EQUAL) <= 0) {

				print("some $v1 in fn:data((");
				node.jjtGetChild(0).jjtAccept(this, data);
				print(")) satisfies");
				print("some $v2 in fn:data((");
				node.jjtGetChild(1).jjtAccept(this, data);
				print(")) satisfies");
				print("let $u1 := fs:convert-operand($v1, $v2) return");
				print("let $u2 := fs:convert-operand($v2, $v1) return");
				print("fs:");
				switch (operator) {
				case GENERAL_COMPARISION_EQUAL:
					print("eq");
					break;
				case GENERAL_COMPARISION_NOT_EQUAL:
					print("ne");
					break;
				case GENERAL_COMPARISION_LESSER_THAN:
					print("lt");
					break;
				case GENERAL_COMPARISION_LESSER_THAN_OR_EQUAL:
					print("le");
					break;
				case GENERAL_COMPARISION_GREATER_THAN:
					print("gt");
					break;
				case GENERAL_COMPARISION_GREATER_THAN_OR_EQUAL:
					print("ge");
					break;
				default:
					throw new IllegalStateException("Unexpected operator: "
							+ operator);
				}
				print("($u1, $u2)");
			} else {
				visitOperator(node, 0, data);
			}
		} else {
			node.childrenAccept(this, data);
		}
		return null;
	}

	public Object visit(ASTContextItemExpr node, Object data) {
		print("fs:dot");
		return null;
	}

	private void visitForExpr(ASTForExpr node, int varIndex, Object data) {
		// Note: XPath does not have the full FLWOR as XQuery, so normalization
		// is more straight forward.
		if (node.jjtGetNumChildren() > 0) {
			if (varIndex < (node.jjtGetNumChildren() - 1) / 2) {
				print("for $");
				node.jjtGetChild(varIndex * 2).jjtAccept(this, data);
				print(" in ");
				node.jjtGetChild(varIndex * 2 + 1).jjtAccept(this, data);
				print(" ");
				visitForExpr(node, varIndex+1, data);
			} else {
				print("return ");
				node.jjtGetChild(node.jjtGetNumChildren() - 1).jjtAccept(this,
						data);
			}
		}
	}

	public Object visit(ASTForExpr node, Object data) {
		visitForExpr(node, 0, data);
		return null;
	}

	public Object visit(ASTForwardAxis node, Object data) {
		AxisEnum axis = node.getAxis(0);
		switch (axis) {
		case FOLLOWING:
			print("ancestor-or-self::node()/following-sibling::node()/descendant-or-self::");
			break;
		case FOLLOWING_SIBLING:
			print("let $e := . return $e/parent::node()/child::");
			break;
		default:
			print(axis);
			print("::");
			break;
		}
		return null;
	}

	public Object visit(ASTIfExpr node, Object data) {
		print("if ");
		print("(");
		print("fn:boolean((");
		node.jjtGetChild(0).jjtAccept(this, data);
		print("))");
		print(")");
		print(" then ");
		node.jjtGetChild(1).jjtAccept(this, data);
		print(" else ");
		node.jjtGetChild(2).jjtAccept(this, data);
		return null;
	}

	public Object visit(ASTInstanceofExpr node, Object data) {
		switch (node.jjtGetNumChildren()) {
		case 1:
			node.childrenAccept(this, data);
			break;
		case 2:
			print("typeswitch(");
			node.jjtGetChild(0).jjtAccept(this, data);
			print(")");
			print(" case $fs:new as ");
			node.jjtGetChild(1).jjtAccept(this, data);
			print(" return fn:true()");
			print(" default $fs:new return fn:false()");
			break;
		default:
			throw new IllegalStateException("Cannot have more than 2 children!");
		}
		return null;
	}

	public Object visit(ASTIntersectExceptExpr node, Object data) {
		visitOperator(node, 0, data);
		return null;
	}

	public Object visit(ASTMultiplicativeExpr node, Object data) {
		visitOperator(node, 0, data);
		return null;
	}

	public Object visit(ASTOrExpr node, Object data) {
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			if (i > 0) {
				print(" or ");
			}
			print("fn:boolean((");
			node.jjtGetChild(i).jjtAccept(this, data);
			print("))");
		}
		return null;
	}

	public Object visit(ASTParenthesizedExpr node, Object data) {
		print("(");
		node.childrenAccept(this, data);
		print(")");
		return null;
	}

	private void visitPathExpr(ASTPathExpr node, int stepExprIndex, Object data) {
		//
		// Note: The AST needs to be in unabbreviated already, which will cause
		// the following to not be scenarios we need to deal with:
		//
		// /
		// / RelativeExpr
		// // RelativeExpr
		// // RelativeExpr / StepExpr
		//
		if (stepExprIndex == 0) {
			node.jjtGetChild(stepExprIndex).jjtAccept(this, data);
		} else {
			print("fs:apply-ordering-mode(");
			print("fs:distinct-doc-order-or-atomic-sequence(");
			print("let $fs:sequence as node()* := ");
			visitPathExpr(node, stepExprIndex - 1, data);
			print(" return");
			print(" let $fs:last := fn:count($fs:sequence) return");
			print(" for $fs:dot at $fs:position in $fs:sequence return ");
			node.jjtGetChild(stepExprIndex).jjtAccept(this, data);
			print("))");
		}
	}

	public Object visit(ASTPathExpr node, Object data) {
		visitPathExpr(node, node.jjtGetNumChildren() - 1, data);
		return null;
	}

	// TODO Continue from here...
	public Object visit(ASTPredicate node, Object data) {
		// TODO Cannot do Predicate without first doing Step
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	// TODO Not done
	public Object visit(ASTPredicateList node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTQuantifiedExpr node, Object data) {
		for (int i = 0; i < (node.jjtGetNumChildren() - 1) / 2; i++) {
			if (node.isExistential()) {
				print("some ");
			} else {
				print("every ");
			}
			print("$");
			node.jjtGetChild(i * 2).jjtAccept(this, data);
			print(" in ");
			node.jjtGetChild(i * 2 + 1).jjtAccept(this, data);
		}
		print(" satisfies ");
		print("fn:boolean((");
		node.jjtGetChild(node.jjtGetNumChildren() - 1).jjtAccept(this, data);
		print("))");
		return null;
	}

	public Object visit(ASTRangeExpr node, Object data) {
		if (node.jjtGetNumChildren() > 1) {
			print("fs:to((");
			node.jjtGetChild(0).jjtAccept(this, data);
			print("), (");
			node.jjtGetChild(1).jjtAccept(this, data);
			print("))");
		} else {
			node.childrenAccept(this, data);
		}
		return null;
	}

	public Object visit(ASTReverseAxis node, Object data) {
		AxisEnum axis = node.getAxis(0);
		switch (axis) {
		case PRECEDING:
			print("ancestor-or-self::node()/preceding-sibling::node()/descendant-or-self::");
			break;
		case PRECEDING_SIBLING:
			print("let $e := . return $e/parent::node()/child::");
			break;
		default:
			print(axis);
			print("::");
			break;
		}
		return null;
	}

	private Object visitNodeTestReverseAxis(ASTReverseAxis node, Object data) {
		AxisEnum axis = node.getAxis(0);
		switch (axis) {
		case PRECEDING_SIBLING:
			// Goes after the NodeTest
			print("[.<<$e]");
			break;
		default:
			break;
		}
		return null;
	}

	public Object visit(ASTSingleType node, Object data) {
		// Note: Optional indicator handled in CastExpr
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTSlash node, Object data) {
		throw new IllegalStateException("AST must be unabbreviated!");
	}

	public Object visit(ASTSlashSlash node, Object data) {
		throw new IllegalStateException("AST must be unabbreviated!");
	}

	public Object visit(ASTStepExpr node, Object data) {
		// Nothing to do, unabbreviation of the XPath query should handled
		// normalization
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			Node child = node.jjtGetChild(i);
			child.jjtAccept(this, data);
			// Need a post processing step when we encounter a
			// ReverseAxis/NodeTest pairing
			if (child instanceof ASTNodeTest && i > 0
					&& node.jjtGetChild(i - 1) instanceof ASTReverseAxis) {
				visitNodeTestReverseAxis((ASTReverseAxis) node
						.jjtGetChild(i - 1), data);
			}
		}
		return null;
	}

	public Object visit(ASTTreatExpr node, Object data) {
		switch (node.jjtGetNumChildren()) {
		case 1:
			node.childrenAccept(this, data);
			break;
		case 2:
			print("typeswitch(");
			node.jjtGetChild(0).jjtAccept(this, data);
			print(")");
			print(" case $fs:new as ");
			node.jjtGetChild(1).jjtAccept(this, data);
			print(" return $fs:new");
			print(" default $fs:new return fn:error()");
			break;
		default:
			throw new IllegalStateException("Cannot have more than 2 children!");
		}
		return null;
	}

	public Object visit(ASTUnaryExpr node, Object data) {
		visitOperator(node, 0, data);
		return null;
	}

	public Object visit(ASTUnionExpr node, Object data) {
		visitOperator(node, 0, data);
		return null;
	}

	public Object visit(ASTXPath node, Object data) {
		// TODO How are constructors handled?
		print("{");
		node.childrenAccept(this, data);
		print("}");
		return null;
	}

	public Object visit(SimpleNode node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

}
