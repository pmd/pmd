package net.sourceforge.pmd.jerry.ast.xpath.visitor;

import net.sourceforge.pmd.jerry.ast.xpath.ASTAbbrevForwardStep;
import net.sourceforge.pmd.jerry.ast.xpath.ASTAbbrevReverseStep;
import net.sourceforge.pmd.jerry.ast.xpath.ASTAdditiveExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTAndExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTAnyKindTest;
import net.sourceforge.pmd.jerry.ast.xpath.ASTAtomicType;
import net.sourceforge.pmd.jerry.ast.xpath.ASTAttribNameOrWildcard;
import net.sourceforge.pmd.jerry.ast.xpath.ASTAttributeDeclaration;
import net.sourceforge.pmd.jerry.ast.xpath.ASTAttributeName;
import net.sourceforge.pmd.jerry.ast.xpath.ASTAttributeTest;
import net.sourceforge.pmd.jerry.ast.xpath.ASTCastExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTCastableExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTCommentTest;
import net.sourceforge.pmd.jerry.ast.xpath.ASTComparisonExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTContextItemExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTDecimalLiteral;
import net.sourceforge.pmd.jerry.ast.xpath.ASTDocumentTest;
import net.sourceforge.pmd.jerry.ast.xpath.ASTDoubleLiteral;
import net.sourceforge.pmd.jerry.ast.xpath.ASTElementDeclaration;
import net.sourceforge.pmd.jerry.ast.xpath.ASTElementName;
import net.sourceforge.pmd.jerry.ast.xpath.ASTElementNameOrWildcard;
import net.sourceforge.pmd.jerry.ast.xpath.ASTElementTest;
import net.sourceforge.pmd.jerry.ast.xpath.ASTExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTForExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTForwardAxis;
import net.sourceforge.pmd.jerry.ast.xpath.ASTFunctionCall;
import net.sourceforge.pmd.jerry.ast.xpath.ASTIfExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTInstanceofExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTIntegerLiteral;
import net.sourceforge.pmd.jerry.ast.xpath.ASTIntersectExceptExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTItemType;
import net.sourceforge.pmd.jerry.ast.xpath.ASTMultiplicativeExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTNameTest;
import net.sourceforge.pmd.jerry.ast.xpath.ASTNodeTest;
import net.sourceforge.pmd.jerry.ast.xpath.ASTOccurrenceIndicator;
import net.sourceforge.pmd.jerry.ast.xpath.ASTOrExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTPITest;
import net.sourceforge.pmd.jerry.ast.xpath.ASTParenthesizedExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTPathExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTPredicate;
import net.sourceforge.pmd.jerry.ast.xpath.ASTPredicateList;
import net.sourceforge.pmd.jerry.ast.xpath.ASTQuantifiedExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTRangeExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTReverseAxis;
import net.sourceforge.pmd.jerry.ast.xpath.ASTSchemaAttributeTest;
import net.sourceforge.pmd.jerry.ast.xpath.ASTSchemaElementTest;
import net.sourceforge.pmd.jerry.ast.xpath.ASTSequenceType;
import net.sourceforge.pmd.jerry.ast.xpath.ASTSingleType;
import net.sourceforge.pmd.jerry.ast.xpath.ASTSlash;
import net.sourceforge.pmd.jerry.ast.xpath.ASTSlashSlash;
import net.sourceforge.pmd.jerry.ast.xpath.ASTStepExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTStringLiteral;
import net.sourceforge.pmd.jerry.ast.xpath.ASTTextTest;
import net.sourceforge.pmd.jerry.ast.xpath.ASTTreatExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTTypeName;
import net.sourceforge.pmd.jerry.ast.xpath.ASTUnaryExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTUnionExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTVarName;
import net.sourceforge.pmd.jerry.ast.xpath.ASTVarRef;
import net.sourceforge.pmd.jerry.ast.xpath.ASTWildcard;
import net.sourceforge.pmd.jerry.ast.xpath.ASTXPath;
import net.sourceforge.pmd.jerry.ast.xpath.Node;
import net.sourceforge.pmd.jerry.ast.xpath.SimpleNode;
import net.sourceforge.pmd.jerry.ast.xpath.XPath2ParserVisitor;
import net.sourceforge.pmd.jerry.ast.xpath.custom.OperatorNode;
import net.sourceforge.pmd.jerry.xpath.OperatorEnum;

public class CoreXPath2ParserVisitor extends AbstractPrintVisitor implements
		XPath2ParserVisitor {

	private void visitOperatorExpression(OperatorNode node, int operatorIndex,
			Object data) {

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
			if (true)
				throw new IllegalStateException("Not implemented: " + operator);
			break;
		case VALUE_COMPARISION_NOT_EQUAL:
			if (true)
				throw new IllegalStateException("Not implemented: " + operator);
			break;
		case VALUE_COMPARISION_LESSER_THAN:
			if (true)
				throw new IllegalStateException("Not implemented: " + operator);
			break;
		case VALUE_COMPARISION_LESSER_THAN_OR_EQUAL:
			if (true)
				throw new IllegalStateException("Not implemented: " + operator);
			break;
		case VALUE_COMPARISION_GREATER_THAN:
			if (true)
				throw new IllegalStateException("Not implemented: " + operator);
			break;
		case VALUE_COMPARISION_GREATER_THAN_OR_EQUAL:
			if (true)
				throw new IllegalStateException("Not implemented: " + operator);
			break;
		case GENERAL_COMPARISION_EQUAL:
			if (true)
				throw new IllegalStateException("Not implemented: " + operator);
			break;
		case GENERAL_COMPARISION_NOT_EQUAL:
			if (true)
				throw new IllegalStateException("Not implemented: " + operator);
			break;
		case GENERAL_COMPARISION_LESSER_THAN:
			if (true)
				throw new IllegalStateException("Not implemented: " + operator);
			break;
		case GENERAL_COMPARISION_LESSER_THAN_OR_EQUAL:
			if (true)
				throw new IllegalStateException("Not implemented: " + operator);
			break;
		case GENERAL_COMPARISION_GREATER_THAN:
			if (true)
				throw new IllegalStateException("Not implemented: " + operator);
			break;
		case GENERAL_COMPARISION_GREATER_THAN_OR_EQUAL:
			if (true)
				throw new IllegalStateException("Not implemented: " + operator);
			break;
		case NODE_COMPARISION_IS:
			if (true)
				throw new IllegalStateException("Not implemented: " + operator);
			break;
		case NODE_COMPARISION_PRECEEDS:
			if (true)
				throw new IllegalStateException("Not implemented: " + operator);
			break;
		case NODE_COMPARISION_FOLLOWS:
			if (true)
				throw new IllegalStateException("Not implemented: " + operator);
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
			println(postFunction + "(");
			incrementIndent();
		}

		// Start operator function
		println(operatorFunction + "(");
		incrementIndent();

		//
		// 1st operand
		//

		// Convert?
		if (convertExpected != null) {
			println("fs:convert-operand(");
			incrementIndent();
			println("fn:data((");
			incrementIndent();
		}
		if (operator.isBinary()) {
			node.jjtGetChild(operatorIndex).jjtAccept(this, data);
		} else {
			// Not the last, then recurse
			if (operatorIndex != node.getNumOperators() - 1) {
				visitOperatorExpression(node, operatorIndex + 1, data);
			}
			// Last
			else {
				node.jjtGetChild(0).jjtAccept(this, data);
			}
		}
		if (convertExpected != null) {
			decrementIndent();
			println("))");
			println(",");
			println(convertExpected);
			decrementIndent();
			println(")");
		}

		//
		// 2nd operand?
		//
		if (operator.isBinary()) {
			println(",");

			// Convert?
			if (convertExpected != null) {
				println("fs:convert-operand(");
				incrementIndent();
				println("fn:data((");
				incrementIndent();
			}
			// Not the last, then recurse
			if (operatorIndex != node.getNumOperators() - 1) {
				visitOperatorExpression(node, operatorIndex + 1, data);
			}
			// Last
			else {
				node.jjtGetChild(operatorIndex + 1).jjtAccept(this, data);
			}
			if (convertExpected != null) {
				decrementIndent();
				println("))");
				println(",");
				println(convertExpected);
				decrementIndent();
				println(")");
			}
		}

		// End operator function
		decrementIndent();
		println(")");

		// End post function?
		if (postFunction != null) {
			decrementIndent();
			println(")");
		}
	}

	public Object visit(ASTAbbrevForwardStep node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTAbbrevReverseStep node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTAdditiveExpr node, Object data) {
		visitOperatorExpression(node, 0, data);
		return null;
	}

	public Object visit(ASTAndExpr node, Object data) {
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			if (i > 0) {
				println();
				println("and");
			}
			Node child = node.jjtGetChild(i);
			child.jjtAccept(this, data);
		}
		return null;
	}

	public Object visit(ASTAnyKindTest node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTAtomicType node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTAttribNameOrWildcard node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTAttributeDeclaration node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTAttributeName node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTAttributeTest node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTCastableExpr node, Object data) {
		switch (node.jjtGetNumChildren()) {
		case 1:
			node.childrenAccept(this, data);
			break;
		case 2:
			node.jjtGetChild(0).jjtAccept(this, data);
			print(" castable as ");
			node.jjtGetChild(1).jjtAccept(this, data);
			break;
		default:
			throw new IllegalStateException("Cannot have more than 2 children!");
		}
		return null;
	}

	public Object visit(ASTCastExpr node, Object data) {
		switch (node.jjtGetNumChildren()) {
		case 1:
			node.childrenAccept(this, data);
			break;
		case 2:
			node.jjtGetChild(0).jjtAccept(this, data);
			print(" cast as ");
			node.jjtGetChild(1).jjtAccept(this, data);
			break;
		default:
			throw new IllegalStateException("Cannot have more than 2 children!");
		}
		return null;
	}

	public Object visit(ASTCommentTest node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTComparisonExpr node, Object data) {
		// Nothing to do
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTContextItemExpr node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTDecimalLiteral node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTDocumentTest node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTDoubleLiteral node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTElementDeclaration node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTElementName node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTElementNameOrWildcard node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTElementTest node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTExpr node, Object data) {
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			if (i > 0) {
				println(",");
			}
			Node child = node.jjtGetChild(i);
			child.jjtAccept(this, data);
		}
		return null;
	}

	public Object visit(ASTForExpr node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTForwardAxis node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTFunctionCall node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTIfExpr node, Object data) {
		print("if (");
		node.jjtGetChild(0).jjtAccept(this, data);
		println(")");
		println("then");
		incrementIndent();
		node.jjtGetChild(1).jjtAccept(this, data);
		decrementIndent();
		println("else");
		incrementIndent();
		node.jjtGetChild(2).jjtAccept(this, data);
		println();
		decrementIndent();
		return null;
	}

	public Object visit(ASTInstanceofExpr node, Object data) {
		switch (node.jjtGetNumChildren()) {
		case 1:
			node.childrenAccept(this, data);
			break;
		case 2:
			println("typeswitch(");
			incrementIndent();
			node.jjtGetChild(0).jjtAccept(this, data);
			decrementIndent();
			println(")");
			println("case $fs:new");
			incrementIndent();
			print("as ");
			node.jjtGetChild(1).jjtAccept(this, data);
			println("return fn:true()");
			decrementIndent();
			println("default $fs:new");
			incrementIndent();
			println("return fn:false()");
			decrementIndent();
			break;
		default:
			throw new IllegalStateException("Cannot have more than 2 children!");
		}
		return null;
	}

	public Object visit(ASTIntegerLiteral node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTIntersectExceptExpr node, Object data) {
		visitOperatorExpression(node, 0, data);
		return null;
	}

	public Object visit(ASTItemType node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTMultiplicativeExpr node, Object data) {
		visitOperatorExpression(node, 0, data);
		return null;
	}

	public Object visit(ASTNameTest node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTNodeTest node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTOccurrenceIndicator node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTOrExpr node, Object data) {
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			if (i > 0) {
				println();
				println("or");
			}
			Node child = node.jjtGetChild(i);
			child.jjtAccept(this, data);
		}
		return null;
	}

	public Object visit(ASTParenthesizedExpr node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTPathExpr node, Object data) {
		/*
		if (node.isRoot()) {
			// TODO Spec has extra set of parens when there appears to be a
			// RelativePathExpr, is it needed?
			println("(fn:root(self::node()) treat as document-node())");
			if (node.jjtGetNumChildren() > 0) {
				println("/");
			}
			if (node.getNumAxes() == 1) {
				println(node.getAxis(0).toString() + "::node()");
				println("/");
			} else if (node.getNumAxes() > 1) {
				throw new IllegalStateException(
						"Cannot have more than 1 axis specified on a PathExpr!");
			}
		}
		*/
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTPITest node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTPredicate node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTPredicateList node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTQuantifiedExpr node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTRangeExpr node, Object data) {
		switch (node.jjtGetNumChildren()) {
		case 1:
			// Nothing to do
			node.childrenAccept(this, data);
			break;
		case 2:
			// Normalize to the fs:to function
			println("fs:to((");
			incrementIndent();
			node.jjtGetChild(0).jjtAccept(this, data);
			decrementIndent();
			println("), (");
			incrementIndent();
			node.jjtGetChild(1).jjtAccept(this, data);
			decrementIndent();
			println(")");
			break;
		default:
			throw new IllegalStateException("Cannot have more than 2 children!");
		}
		return null;
	}

	public Object visit(ASTReverseAxis node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTSchemaAttributeTest node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTSchemaElementTest node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTSequenceType node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTSingleType node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTSlash node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTSlashSlash node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTStepExpr node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTStringLiteral node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTTextTest node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTTreatExpr node, Object data) {
		switch (node.jjtGetNumChildren()) {
		case 1:
			node.childrenAccept(this, data);
			break;
		case 2:
			println("typeswitch(");
			incrementIndent();
			node.jjtGetChild(0).jjtAccept(this, data);
			decrementIndent();
			println(")");
			println("case $fs:new");
			incrementIndent();
			print("as ");
			node.jjtGetChild(1).jjtAccept(this, data);
			println("return $fs:new");
			decrementIndent();
			println("default $fs:new");
			incrementIndent();
			println("return fn:error()");
			decrementIndent();
			break;
		default:
			throw new IllegalStateException("Cannot have more than 2 children!");
		}
		return null;
	}

	public Object visit(ASTTypeName node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTUnaryExpr node, Object data) {
		visitOperatorExpression(node, 0, data);
		return null;
	}

	public Object visit(ASTUnionExpr node, Object data) {
		visitOperatorExpression(node, 0, data);
		return null;
	}

	public Object visit(ASTVarName node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTVarRef node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTWildcard node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(ASTXPath node, Object data) {
		// TODO How are constructors handled?
		println("{");
		incrementIndent();
		node.childrenAccept(this, data);
		decrementIndent();
		println("}");
		return null;
	}

	public Object visit(SimpleNode node, Object data) {
		TODO(node);
		node.childrenAccept(this, data);
		return null;
	}

}
