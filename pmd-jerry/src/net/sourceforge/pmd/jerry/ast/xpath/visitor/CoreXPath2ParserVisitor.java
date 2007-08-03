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
import net.sourceforge.pmd.jerry.ast.xpath.ASTDecimalLiteral;
import net.sourceforge.pmd.jerry.ast.xpath.ASTDoubleLiteral;
import net.sourceforge.pmd.jerry.ast.xpath.ASTForExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTForwardAxis;
import net.sourceforge.pmd.jerry.ast.xpath.ASTFunctionCall;
import net.sourceforge.pmd.jerry.ast.xpath.ASTIfExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTInstanceofExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTIntegerLiteral;
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

	// StepExpr types
	private static enum StepExprTypeEnum {
		FORWARD_STEP, REVERSE_STEP, PRIMARY_EXP;
	}

	// Predicate types
	private static enum PredicateTypeEnum {
		NUMERIC_LITERAL, LAST, EXPR;
	}

	public static String toCore(String query) {
		// First convert to unabbreviated XPath
		String unabbreviated = PrintXPath2ParserVisitor.unabbreviate(query);

		// Get XPath AST
		Reader reader = new StringReader(unabbreviated);
		XPath2Parser parser = new XPath2Parser(reader);
		ASTXPath xpath = parser.XPath();
		//xpath.dump("");

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
				print(" some $v2 in fn:data((");
				node.jjtGetChild(1).jjtAccept(this, data);
				print(")) satisfies");
				print(" let $u1 := fs:convert-operand($v1, $v2) return");
				print(" let $u2 := fs:convert-operand($v2, $v1) return");
				print(" fs:");
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
				visitForExpr(node, varIndex + 1, data);
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
			// Visit steps in reverse order
			visitPathExpr(node, stepExprIndex - 1, data);
			print(" return");
			print(" let $fs:last := fn:count($fs:sequence) return");
			print(" for $fs:dot at $fs:position in $fs:sequence return ");
			node.jjtGetChild(stepExprIndex).jjtAccept(this, data);
			print("))");
		}
	}

	public Object visit(ASTPathExpr node, Object data) {
		// Visit last step first
		visitPathExpr(node, node.jjtGetNumChildren() - 1, data);
		return null;
	}

	public Object visit(ASTPredicate node, Object data) {
		// Nothing to do
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTPredicateList node, Object data) {
		// Processing of the StepExpr should handle the PredicateList
		throw new IllegalStateException(
				"Explicit visitation of PredicateList is should not occur.");
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
		print(" satisfies");
		print(" fn:boolean((");
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

	private void visitNodeTestReverseAxis(ASTReverseAxis node) {
		AxisEnum axis = node.getAxis(0);
		switch (axis) {
		case PRECEDING_SIBLING:
			// Goes after the NodeTest
			print("[.<<$e]");
			break;
		default:
			break;
		}
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
		// Sanity checks
		if (node.jjtGetNumChildren() < 1) {
			throw new IllegalArgumentException(
					"Expecting at least 1 child node.");
		}
		if (node.jjtGetNumChildren() > 3) {
			throw new IllegalArgumentException(
					"Expecting at most 3 child nodes.");
		}

		//
		// Normalization depends upon what the predicates are being applied.
		//
		Node firstChild = node.jjtGetChild(0);
		final StepExprTypeEnum stepExprTypeEnum;
		// 1) ForwardStep
		if (firstChild instanceof ASTForwardAxis
				|| firstChild instanceof ASTAbbrevForwardStep) {
			stepExprTypeEnum = StepExprTypeEnum.FORWARD_STEP;
		}
		// 2) ReverseStep
		else if (firstChild instanceof ASTReverseAxis
				|| firstChild instanceof ASTAbbrevReverseStep) {
			stepExprTypeEnum = StepExprTypeEnum.REVERSE_STEP;
		}
		// 3) PrimaryExpr
		else {
			stepExprTypeEnum = StepExprTypeEnum.PRIMARY_EXP;
		}

		//
		// We need to visit the Predicates in the PredicateList in reverse
		// order to normalize.
		//
		int predicateIndex = -1;
		ASTPredicateList predicateList = null;
		Node lastChild = node.jjtGetChild(node.jjtGetNumChildren() - 1);
		if (lastChild instanceof ASTPredicateList) {
			predicateList = (ASTPredicateList) lastChild;
			predicateIndex = lastChild.jjtGetNumChildren() - 1;
		}
		visitStepExpr(node, data, stepExprTypeEnum, predicateList,
				predicateIndex);
		return null;
	}

	private void visitStepExpr(ASTStepExpr node, Object data,
			StepExprTypeEnum stepExprTypeEnum, ASTPredicateList predicateList,
			int predicateIndex) {
		// Predicate
		if (predicateIndex >= 0) {
			ASTPredicate predicate = (ASTPredicate) predicateList
					.jjtGetChild(predicateIndex);

			//
			// What kind of predicate is this? Normalization provides extra
			// details for static typing for certain predicates.
			//
			// 1) NumericLiteral
			// 2) fn:last()
			// 3) other Expr
			final PredicateTypeEnum predicateTypeEnum;
			Node firstChild = predicate.jjtGetChild(0);
			if (firstChild instanceof ASTIntegerLiteral
					|| firstChild instanceof ASTDecimalLiteral
					|| firstChild instanceof ASTDoubleLiteral) {
				predicateTypeEnum = PredicateTypeEnum.NUMERIC_LITERAL;
			} else if (firstChild instanceof ASTFunctionCall
					&& firstChild.jjtGetNumChildren() == 0
					&& "fn:last".equals(((ASTFunctionCall) firstChild)
							.getImage())) {
				predicateTypeEnum = PredicateTypeEnum.LAST;
			} else {
				predicateTypeEnum = PredicateTypeEnum.EXPR;
			}

			// Pre recursion visit
			switch (stepExprTypeEnum) {
			case FORWARD_STEP:
			case REVERSE_STEP:
				switch (predicateTypeEnum) {
				case NUMERIC_LITERAL:
				case LAST:
				case EXPR:
					print("let $fs:sequence := fs:apply-ordering-mode(fs:distinct-doc-order(");
					break;
				default:
					throw new IllegalStateException(
							"Unexpected predicateTypeEnum: "
									+ predicateTypeEnum);
				}
				break;
			case PRIMARY_EXP:
				switch (predicateTypeEnum) {
				case NUMERIC_LITERAL:
				case LAST:
				case EXPR:
					print("let $fs:sequence := ");
					break;
				default:
					throw new IllegalStateException(
							"Unexpected predicateTypeEnum: "
									+ predicateTypeEnum);
				}
				break;
			default:
				throw new IllegalStateException("Unexpected stepExprTypeEnum: "
						+ stepExprTypeEnum);
			}

			// Recurse to next Predicate
			visitStepExpr(node, data, stepExprTypeEnum, predicateList,
					predicateIndex - 1);

			// Pre Predicate visit
			switch (stepExprTypeEnum) {
			case FORWARD_STEP:
				switch (predicateTypeEnum) {
				case NUMERIC_LITERAL:
					print(")) return fn:subsequence($fs:sequence, ");
					break;
				case LAST:
					print(")) return let $fs:last := fn:count($fs:sequence) return fn:subsequence($fs:sequence, 1)");
					break;
				case EXPR:
					print(" return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return if (");
					break;
				default:
					throw new IllegalStateException(
							"Unexpected predicateTypeEnum: "
									+ predicateTypeEnum);
				}
				break;
			case REVERSE_STEP:
				switch (predicateTypeEnum) {
				case NUMERIC_LITERAL:
					print(")) return let $fs:last := fn:count($fs:sequence) return let $fs:position := fs:plus(1, fs:minus($fs:last, ");
					break;
				case LAST:
					print(")) return fn:subsequence($fs:sequence, 1");
					break;
				case EXPR:
					print(")) return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:new in $fs:sequence return let $fs:position := fs:plus(1, fs:minus($fs:last, $fs:new)) return if (");
					break;
				default:
					throw new IllegalStateException(
							"Unexpected predicateTypeEnum: "
									+ predicateTypeEnum);
				}
				break;
			case PRIMARY_EXP:
				switch (predicateTypeEnum) {
				case NUMERIC_LITERAL:
					print(" return fn:subsequence($fs:sequence, ");
					break;
				case LAST:
					print(" return fn:subsequence($fs:sequence, $fs:last, 1)");
					break;
				case EXPR:
					print(" return let $fs:last := fn:count($fs:sequence) return for $fs:dot at $fs:position in $fs:sequence return if(");
					break;
				default:
					throw new IllegalStateException(
							"Unexpected predicateTypeEnum: "
									+ predicateTypeEnum);
				}
				break;
			default:
				throw new IllegalStateException("Unexpected stepExprTypeEnum: "
						+ stepExprTypeEnum);
			}

			// Visit Predicate
			if (!((StepExprTypeEnum.REVERSE_STEP == stepExprTypeEnum && PredicateTypeEnum.LAST == predicateTypeEnum)
					|| (StepExprTypeEnum.FORWARD_STEP == stepExprTypeEnum && PredicateTypeEnum.LAST == predicateTypeEnum) || (StepExprTypeEnum.PRIMARY_EXP == stepExprTypeEnum && PredicateTypeEnum.LAST == predicateTypeEnum))) {
				predicateList.jjtGetChild(predicateIndex).jjtAccept(this, data);
			}

			// Post visit
			switch (stepExprTypeEnum) {
			case FORWARD_STEP:
				switch (predicateTypeEnum) {
				case NUMERIC_LITERAL:
					print(", 1)");
					break;
				case LAST:
					// Nothing to do
					break;
				case EXPR:
					print(") then $fs:dot else ()");
					break;
				default:
					throw new IllegalStateException(
							"Unexpected predicateTypeEnum: "
									+ predicateTypeEnum);
				}
				break;
			case REVERSE_STEP:
				switch (predicateTypeEnum) {
				case NUMERIC_LITERAL:
					print(")) return fn:subsequence($fs: sequence, $fs:position, 1)");
					break;
				case LAST:
					print(", 1)");
					break;
				case EXPR:
					print(") then $fs:dot else ()");
					break;
				default:
					throw new IllegalStateException(
							"Unexpected predicateTypeEnum: "
									+ predicateTypeEnum);
				}
				break;
			case PRIMARY_EXP:
				switch (predicateTypeEnum) {
				case NUMERIC_LITERAL:
					print(", 1)");
					break;
				case LAST:
					// Nothing to do
					break;
				case EXPR:
					print(") then $fs:dot else ()");
					break;
				default:
					throw new IllegalStateException(
							"Unexpected predicateTypeEnum: "
									+ predicateTypeEnum);
				}
				break;
			default:
				throw new IllegalStateException("Unexpected stepExprTypeEnum: "
						+ stepExprTypeEnum);
			}
		}
		// Standalone (no Predicates left)
		else {
			// Pre visit
			switch (stepExprTypeEnum) {
			case FORWARD_STEP:
			case REVERSE_STEP:
				print("fs:apply-ordering-mode(");
				break;
			case PRIMARY_EXP:
				// Nothing to do
				break;
			default:
				throw new IllegalStateException("Unexpected stepExprTypeEnum: "
						+ stepExprTypeEnum);
			}

			// Visit everything except the PredicateList
			for (int i = 0; i < node.jjtGetNumChildren(); i++) {
				Node child = node.jjtGetChild(i);
				if (child instanceof ASTPredicateList) {
					break;
				}
				child.jjtAccept(this, data);
				// Need a post processing step when we encounter a
				// ReverseAxis/NodeTest pairing
				if (child instanceof ASTNodeTest && i > 0
						&& node.jjtGetChild(i - 1) instanceof ASTReverseAxis) {
					visitNodeTestReverseAxis((ASTReverseAxis) node
							.jjtGetChild(i - 1));
				}
			}

			// Post visit
			switch (stepExprTypeEnum) {
			case FORWARD_STEP:
			case REVERSE_STEP:
				print(")");
				break;
			case PRIMARY_EXP:
				// Nothing to do
				break;
			default:
				throw new IllegalStateException("Unexpected stepExprTypeEnum: "
						+ stepExprTypeEnum);
			}
		}
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
