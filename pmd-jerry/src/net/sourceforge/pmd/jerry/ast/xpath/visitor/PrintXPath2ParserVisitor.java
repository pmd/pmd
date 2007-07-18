package net.sourceforge.pmd.jerry.ast.xpath.visitor;

import java.io.Reader;
import java.io.StringReader;

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
import net.sourceforge.pmd.jerry.ast.xpath.ASTAxisStep;
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
import net.sourceforge.pmd.jerry.ast.xpath.ASTExprSingle;
import net.sourceforge.pmd.jerry.ast.xpath.ASTFilterExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTForExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTForwardAxis;
import net.sourceforge.pmd.jerry.ast.xpath.ASTForwardStep;
import net.sourceforge.pmd.jerry.ast.xpath.ASTFunctionCall;
import net.sourceforge.pmd.jerry.ast.xpath.ASTGeneralComp;
import net.sourceforge.pmd.jerry.ast.xpath.ASTIfExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTInstanceofExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTIntegerLiteral;
import net.sourceforge.pmd.jerry.ast.xpath.ASTIntersectExceptExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTItemType;
import net.sourceforge.pmd.jerry.ast.xpath.ASTKindTest;
import net.sourceforge.pmd.jerry.ast.xpath.ASTLiteral;
import net.sourceforge.pmd.jerry.ast.xpath.ASTMultiplicativeExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTNameTest;
import net.sourceforge.pmd.jerry.ast.xpath.ASTNodeComp;
import net.sourceforge.pmd.jerry.ast.xpath.ASTNodeTest;
import net.sourceforge.pmd.jerry.ast.xpath.ASTNumericLiteral;
import net.sourceforge.pmd.jerry.ast.xpath.ASTOccurrenceIndicator;
import net.sourceforge.pmd.jerry.ast.xpath.ASTOrExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTPITest;
import net.sourceforge.pmd.jerry.ast.xpath.ASTParenthesizedExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTPathExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTPredicate;
import net.sourceforge.pmd.jerry.ast.xpath.ASTPredicateList;
import net.sourceforge.pmd.jerry.ast.xpath.ASTPrimaryExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTQuantifiedExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTRangeExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTRelativePathExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTReverseAxis;
import net.sourceforge.pmd.jerry.ast.xpath.ASTReverseStep;
import net.sourceforge.pmd.jerry.ast.xpath.ASTSchemaAttributeTest;
import net.sourceforge.pmd.jerry.ast.xpath.ASTSchemaElementTest;
import net.sourceforge.pmd.jerry.ast.xpath.ASTSequenceType;
import net.sourceforge.pmd.jerry.ast.xpath.ASTSimpleForClause;
import net.sourceforge.pmd.jerry.ast.xpath.ASTSingleType;
import net.sourceforge.pmd.jerry.ast.xpath.ASTStepExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTStringLiteral;
import net.sourceforge.pmd.jerry.ast.xpath.ASTTextTest;
import net.sourceforge.pmd.jerry.ast.xpath.ASTTreatExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTTypeName;
import net.sourceforge.pmd.jerry.ast.xpath.ASTUnaryExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTUnionExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTValueComp;
import net.sourceforge.pmd.jerry.ast.xpath.ASTValueExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTVarName;
import net.sourceforge.pmd.jerry.ast.xpath.ASTVarRef;
import net.sourceforge.pmd.jerry.ast.xpath.ASTWildcard;
import net.sourceforge.pmd.jerry.ast.xpath.ASTXPath;
import net.sourceforge.pmd.jerry.ast.xpath.SimpleNode;
import net.sourceforge.pmd.jerry.ast.xpath.XPath2Parser;
import net.sourceforge.pmd.jerry.ast.xpath.XPath2ParserVisitor;
import net.sourceforge.pmd.jerry.ast.xpath.custom.OperatorNode;
import net.sourceforge.pmd.jerry.xpath.AxisEnum;

public class PrintXPath2ParserVisitor extends AbstractPrintVisitor implements
		XPath2ParserVisitor {

	private final PrintModeEnum printMode;

	public static enum PrintModeEnum {
		ABBREVIATE(), UNABBREVIATE();
	}
	
	public static String abbreviate(String query) {
		return visit(query, PrintModeEnum.ABBREVIATE);
	}
	public static String unabbreviate(String query) {
		return visit(query, PrintModeEnum.UNABBREVIATE);
	}
	private static String visit(String query, PrintModeEnum printMode) {
		Reader reader = new StringReader(query);
		XPath2Parser parser = new XPath2Parser(reader);
		ASTXPath xpath = parser.XPath();
		PrintXPath2ParserVisitor visitor = new PrintXPath2ParserVisitor(printMode);
		xpath.jjtAccept(visitor, null);
		return visitor.getOutput();
	}

	public PrintXPath2ParserVisitor(PrintModeEnum printMode) {
		this.printMode = printMode;
	}

	private Object visitSeparator(SimpleNode node, Object data, String separator) {
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			if (i > 0) {
				print(separator);
			}
			node.jjtGetChild(i).jjtAccept(this, data);
		}
		return data;
	}

	private Object visitOperator(OperatorNode node, Object data,
			boolean skipOperator) {
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			if (i > 0) {
				// Skip the operator child nodes? Those are the even position
				// nodes
				if (skipOperator && i % 2 == 1) {
					continue;
				}
				print(" ");
				print(node.getOperator((i - 1) / 2));
				print(" ");
			}
			node.jjtGetChild(i).jjtAccept(this, data);
		}
		return data;
	}

	public Object visit(ASTAbbrevForwardStep node, Object data) {
		if (node.getNumAxes() == 1) {
			switch (printMode) {
			case ABBREVIATE:
				print("@");
				break;
			case UNABBREVIATE:
				print(AxisEnum.ATTRIBUTE);
				print("::");
				break;
			default:
				throw new IllegalStateException("Unknown PrintModeEnum: "
						+ printMode);
			}
		} else {
			switch (printMode) {
			case ABBREVIATE:
				break;
			case UNABBREVIATE:
				AxisEnum axis = AxisEnum.CHILD;
				if (node.jjtGetChild(0).jjtGetChild(0) instanceof ASTKindTest) {
					Object testNode = node.jjtGetChild(0).jjtGetChild(0)
							.jjtGetChild(0);
					if (testNode instanceof ASTAttributeTest
							|| testNode instanceof ASTSchemaAttributeTest) {
						axis = AxisEnum.ATTRIBUTE;
					}
				}
				print(axis);
				print("::");
				break;
			default:
				throw new IllegalStateException("Unknown PrintModeEnum: "
						+ printMode);
			}
		}
		node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTAbbrevReverseStep node, Object data) {
		switch (printMode) {
		case ABBREVIATE:
			print("..");
			break;
		case UNABBREVIATE:
			print(AxisEnum.PARENT);
			print("::node()");
			break;
		default:
			throw new IllegalStateException("Unknown PrintModeEnum: "
					+ printMode);
		}
		return data;
	}

	public Object visit(ASTAdditiveExpr node, Object data) {
		visitOperator(node, data, false);
		return data;
	}

	public Object visit(ASTAndExpr node, Object data) {
		visitSeparator(node, data, " and ");
		return data;
	}

	public Object visit(ASTAnyKindTest node, Object data) {
		print("node()");
		return data;
	}

	public Object visit(ASTAtomicType node, Object data) {
		print(node.getImage());
		return data;
	}

	public Object visit(ASTAttribNameOrWildcard node, Object data) {
		if (node.jjtGetNumChildren() > 0) {
			node.childrenAccept(this, data);
		} else {
			print("*");
		}
		return data;
	}

	public Object visit(ASTAttributeDeclaration node, Object data) {
		// Nothing to do
		node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTAttributeName node, Object data) {
		print(node.getImage());
		return data;
	}

	public Object visit(ASTAttributeTest node, Object data) {
		print("attribute(");
		visitSeparator(node, data, ", ");
		print(")");
		return data;
	}

	public Object visit(ASTAxisStep node, Object data) {
		// Nothing to do
		node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTCastableExpr node, Object data) {
		visitSeparator(node, data, " castable as ");
		return data;
	}

	public Object visit(ASTCastExpr node, Object data) {
		visitSeparator(node, data, " cast as ");
		return data;
	}

	public Object visit(ASTCommentTest node, Object data) {
		print("comment()");
		return data;
	}

	public Object visit(ASTComparisonExpr node, Object data) {
		visitOperator(node, data, true);
		return data;
	}

	public Object visit(ASTContextItemExpr node, Object data) {
		print(".");
		return data;
	}

	public Object visit(ASTDecimalLiteral node, Object data) {
		print(node.getImage());
		return data;
	}

	public Object visit(ASTDocumentTest node, Object data) {
		print("document-node(");
		node.childrenAccept(this, data);
		print(")");
		return data;
	}

	public Object visit(ASTDoubleLiteral node, Object data) {
		print(node.getImage());
		return data;
	}

	public Object visit(ASTElementDeclaration node, Object data) {
		// Nothing to do
		node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTElementName node, Object data) {
		print(node.getImage());
		return data;
	}

	public Object visit(ASTElementNameOrWildcard node, Object data) {
		if (node.jjtGetNumChildren() > 0) {
			node.childrenAccept(this, data);
		} else {
			print("*");
		}
		return data;
	}

	public Object visit(ASTElementTest node, Object data) {
		print("element(");
		if (node.jjtGetNumChildren() > 0) {
			node.jjtGetChild(0).jjtAccept(this, data);
		}
		if (node.jjtGetNumChildren() > 1) {
			print(", ");
			node.jjtGetChild(1).jjtAccept(this, data);
			if (node.getImage() != null) {
				print("?");
			}
		}
		print(")");
		return data;
	}

	public Object visit(ASTExpr node, Object data) {
		visitSeparator(node, data, ", ");
		return data;
	}

	public Object visit(ASTExprSingle node, Object data) {
		// Nothing to do
		node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTFilterExpr node, Object data) {
		// Nothing to do
		node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTForExpr node, Object data) {
		node.jjtGetChild(0).jjtAccept(this, data);
		print(" return ");
		node.jjtGetChild(1).jjtAccept(this, data);
		return data;
	}

	public Object visit(ASTForwardAxis node, Object data) {
		AxisEnum axis = node.getAxis(0);
		switch (printMode) {
		case ABBREVIATE:
			switch (axis) {
			case CHILD:
				break;
			case ATTRIBUTE:
				print("@");
				break;
			default:
				print(axis);
				print("::");
			}
			break;
		case UNABBREVIATE:
			print(axis);
			print("::");
			break;
		default:
			throw new IllegalStateException("Unknown PrintModeEnum: "
					+ printMode);
		}
		return data;
	}

	public Object visit(ASTForwardStep node, Object data) {
		switch (printMode) {
		case ABBREVIATE:
			if (node.jjtGetNumChildren() == 2) {
				if (((ASTForwardAxis) node.jjtGetChild(0)).getAxis(0) == AxisEnum.DESCENDANT_OR_SELF) {
					if (node.jjtGetChild(1).jjtGetChild(0) instanceof ASTKindTest) {
						if (node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0) instanceof ASTAnyKindTest) {
							break;
						}
					}
				}
			}
			node.childrenAccept(this, data);
			break;
		case UNABBREVIATE:
			node.childrenAccept(this, data);
			break;
		default:
			throw new IllegalStateException("Unknown PrintModeEnum: "
					+ printMode);
		}
		return data;
	}

	public Object visit(ASTFunctionCall node, Object data) {
		print(node.getImage());
		print("(");
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			if (i > 0) {
				print(", ");
			}
			node.jjtGetChild(i).jjtAccept(this, data);
		}
		print(")");
		return data;
	}

	public Object visit(ASTGeneralComp node, Object data) {
		// Nothing to do
		throw new IllegalStateException("Should not be here!");
	}

	public Object visit(ASTIfExpr node, Object data) {
		// "if" "(" Expr ")" "then" ExprSingle "else" ExprSingle

		print("if ");
		print("(");
		node.jjtGetChild(0).jjtAccept(this, data);
		print(")");
		print(" then ");
		node.jjtGetChild(1).jjtAccept(this, data);
		print(" else ");
		node.jjtGetChild(2).jjtAccept(this, data);
		return data;
	}

	public Object visit(ASTInstanceofExpr node, Object data) {
		visitSeparator(node, data, " instance of ");
		return data;
	}

	public Object visit(ASTIntegerLiteral node, Object data) {
		print(node.getImage());
		node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTIntersectExceptExpr node, Object data) {
		visitOperator(node, data, false);
		return data;
	}

	public Object visit(ASTItemType node, Object data) {
		if (node.jjtGetNumChildren() == 0) {
			print("item()");
		} else {
			node.childrenAccept(this, data);
		}
		return data;
	}

	public Object visit(ASTKindTest node, Object data) {
		// Nothing to do
		node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTLiteral node, Object data) {
		// Nothing to do
		node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTMultiplicativeExpr node, Object data) {
		visitOperator(node, data, false);
		return data;
	}

	public Object visit(ASTNameTest node, Object data) {
		if (node.jjtGetNumChildren() == 0) {
			print(node.getImage());
		} else {
			node.childrenAccept(this, data);
		}
		return data;
	}

	public Object visit(ASTNodeComp node, Object data) {
		// Nothing to do
		throw new IllegalStateException("Should not be here!");
	}

	public Object visit(ASTNodeTest node, Object data) {
		// Nothing to do
		node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTNumericLiteral node, Object data) {
		// Nothing to do
		node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTOccurrenceIndicator node, Object data) {
		print(node.getImage());
		return data;
	}

	public Object visit(ASTOrExpr node, Object data) {
		visitSeparator(node, data, " or ");
		return data;
	}

	public Object visit(ASTParenthesizedExpr node, Object data) {
		print("(");
		node.childrenAccept(this, data);
		print(")");
		return data;
	}

	public Object visit(ASTPathExpr node, Object data) {
		if (node.isRoot()) {
			switch (printMode) {
			case ABBREVIATE:
				print("/");
				break;
			case UNABBREVIATE:
				print("fn:root(");
				print(AxisEnum.SELF);
				print("::node()) treat as document-node()");
				if (node.jjtGetNumChildren() > 0) {
					print("/");
				}
				break;
			default:
				throw new IllegalStateException("Unknown PrintModeEnum: "
						+ printMode);
			}
			if (node.getNumAxes() > 0) {
				switch (printMode) {
				case ABBREVIATE:
					print("/");
					break;
				case UNABBREVIATE:
					print(AxisEnum.DESCENDANT_OR_SELF);
					print("::node()/");
					break;
				default:
					throw new IllegalStateException("Unknown PrintModeEnum: "
							+ printMode);
				}
			}
		}
		node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTPITest node, Object data) {
		print("processing-instruction(");
		if (node.getImage() != null) {
			print(node.getImage());
		} else {
			node.childrenAccept(this, data);
		}
		print(")");
		return data;
	}

	public Object visit(ASTPredicate node, Object data) {
		print("[");
		node.childrenAccept(this, data);
		print("]");
		return data;
	}

	public Object visit(ASTPredicateList node, Object data) {
		// Nothing to do
		node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTPrimaryExpr node, Object data) {
		// Nothing to do
		node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTQuantifiedExpr node, Object data) {
		if (node.isExistential()) {
			print("some ");
		} else {
			print("every ");
		}
		for (int i = 0; i < (node.jjtGetNumChildren() - 1) / 2; i++) {
			if (i > 0) {
				print(", ");
			}
			print("$");
			node.jjtGetChild(i * 2).jjtAccept(this, data);
			print(" in ");
			node.jjtGetChild(i * 2 + 1).jjtAccept(this, data);
		}
		print(" satisfies ");
		node.jjtGetChild(node.jjtGetNumChildren() - 1).jjtAccept(this, data);
		return data;
	}

	public Object visit(ASTRangeExpr node, Object data) {
		visitSeparator(node, data, " to ");
		return data;
	}

	public Object visit(ASTRelativePathExpr node, Object data) {
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			if (i > 0) {
				print("/");
				AxisEnum axisEnum = node.getAxis(i - 1);
				if (axisEnum != null && axisEnum != AxisEnum.DESCENDANT_OR_SELF) {
					throw new IllegalStateException("Unexpected axis: "
							+ axisEnum);
				}
				if (axisEnum != null) {
					switch (printMode) {
					case ABBREVIATE:
						print("/");
						break;
					case UNABBREVIATE:
						print(axisEnum);
						print("::node()/");
						break;
					default:
						throw new IllegalStateException(
								"Unknown PrintModeEnum: " + printMode);
					}
				}
			}
			node.jjtGetChild(i).jjtAccept(this, data);
		}
		return data;
	}

	public Object visit(ASTReverseAxis node, Object data) {
		print(node.getAxis(0));
		print("::");
		return data;
	}

	public Object visit(ASTReverseStep node, Object data) {
		switch (printMode) {
		case ABBREVIATE:
			if (node.jjtGetNumChildren() == 2) {
				if (((ASTReverseAxis) node.jjtGetChild(0)).getAxis(0) == AxisEnum.PARENT) {
					if (node.jjtGetChild(1).jjtGetChild(0) instanceof ASTKindTest) {
						if (node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0) instanceof ASTAnyKindTest) {
							print("..");
							break;
						}
					}
				}
			}
			node.childrenAccept(this, data);
			break;
		case UNABBREVIATE:
			node.childrenAccept(this, data);
			break;
		default:
			throw new IllegalStateException("Unknown PrintModeEnum: "
					+ printMode);
		}
		return data;
	}

	public Object visit(ASTSchemaAttributeTest node, Object data) {
		print("schema-attribute(");
		node.childrenAccept(this, data);
		print(")");
		return data;
	}

	public Object visit(ASTSchemaElementTest node, Object data) {
		print("schema-element(");
		node.childrenAccept(this, data);
		print(")");
		return data;
	}

	public Object visit(ASTSequenceType node, Object data) {
		if (node.jjtGetNumChildren() == 0) {
			print("empty-sequence()");
		} else {
			node.childrenAccept(this, data);
		}
		return data;
	}

	public Object visit(ASTSimpleForClause node, Object data) {
		print("for");
		for (int i = 0; i < node.jjtGetNumChildren() / 2; i++) {
			if (i > 0) {
				print(",");
			}
			print(" $");
			node.jjtGetChild(i * 2).jjtAccept(this, data);
			print(" in ");
			node.jjtGetChild(i * 2 + 1).jjtAccept(this, data);
		}
		return data;
	}

	public Object visit(ASTSingleType node, Object data) {
		node.childrenAccept(this, data);
		if (node.getImage() != null) {
			print("?");
		}
		return data;
	}

	public Object visit(ASTStepExpr node, Object data) {
		// Nothing to do
		node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTStringLiteral node, Object data) {
		print(node.getImage());
		return data;
	}

	public Object visit(ASTTextTest node, Object data) {
		print("text()");
		return data;
	}

	public Object visit(ASTTreatExpr node, Object data) {
		visitSeparator(node, data, " treat as ");
		return data;
	}

	public Object visit(ASTTypeName node, Object data) {
		print(node.getImage());
		return data;
	}

	public Object visit(ASTUnaryExpr node, Object data) {
		for (int i = 0; i < node.getNumOperators(); i++) {
			print(node.getOperator(i));
		}
		node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTUnionExpr node, Object data) {
		String separator;
		switch (printMode) {
		case ABBREVIATE:
			separator = " | ";
			break;
		case UNABBREVIATE:
			separator = " union ";
			break;
		default:
			throw new IllegalStateException("Unknown PrintModeEnum: "
					+ printMode);
		}
		visitSeparator(node, data, separator);
		return data;
	}

	public Object visit(ASTValueComp node, Object data) {
		// Nothing to do
		throw new IllegalStateException("Should not be here!");
	}

	public Object visit(ASTValueExpr node, Object data) {
		// Nothing to do
		node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTVarName node, Object data) {
		print(node.getImage());
		return data;
	}

	public Object visit(ASTVarRef node, Object data) {
		print("$");
		node.childrenAccept(this, data);
		return data;
	}

	public Object visit(ASTWildcard node, Object data) {
		if (node.getImage() != null && node.isPrefix()) {
			print(node.getImage());
			print("::");
		}
		print("*");
		if (node.getImage() != null && !node.isPrefix()) {
			print("::");
			print(node.getImage());
		}
		return data;
	}

	public Object visit(ASTXPath node, Object data) {
		// Nothing to do
		node.childrenAccept(this, data);
		return data;
	}

	public Object visit(SimpleNode node, Object data) {
		// Nothing to do
		throw new IllegalStateException("Should not be here!");
	}
}
