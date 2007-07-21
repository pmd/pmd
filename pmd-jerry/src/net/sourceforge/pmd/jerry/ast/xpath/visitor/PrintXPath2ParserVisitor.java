package net.sourceforge.pmd.jerry.ast.xpath.visitor;

import java.io.Reader;
import java.io.StringReader;

import net.sourceforge.pmd.jerry.ast.xpath.ASTAbbrevForwardStep;
import net.sourceforge.pmd.jerry.ast.xpath.ASTAbbrevReverseStep;
import net.sourceforge.pmd.jerry.ast.xpath.ASTAdditiveExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTAndExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTAnyKindTest;
import net.sourceforge.pmd.jerry.ast.xpath.ASTAttributeTest;
import net.sourceforge.pmd.jerry.ast.xpath.ASTCastExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTComparisonExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTContextItemExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTDocumentTest;
import net.sourceforge.pmd.jerry.ast.xpath.ASTExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTForExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTForwardAxis;
import net.sourceforge.pmd.jerry.ast.xpath.ASTFunctionCall;
import net.sourceforge.pmd.jerry.ast.xpath.ASTIfExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTInstanceofExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTIntersectExceptExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTItemType;
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
import net.sourceforge.pmd.jerry.ast.xpath.ASTSchemaAttributeTest;
import net.sourceforge.pmd.jerry.ast.xpath.ASTSequenceType;
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

public class PrintXPath2ParserVisitor extends AbstractXPath2ParserVisitor {

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
		PrintXPath2ParserVisitor visitor = new PrintXPath2ParserVisitor(
				printMode);
		// xpath.dump("");
		xpath.jjtAccept(visitor, null);
		return visitor.getOutput();
	}

	public PrintXPath2ParserVisitor(PrintModeEnum printMode) {
		this.printMode = printMode;
	}

	private Object visitOperator(OperatorNode node, Object data) {
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			if (i > 0) {
				print(" ");
				print(node.getOperator((i - 1) / 2));
				print(" ");
			}
			node.jjtGetChild(i).jjtAccept(this, data);
		}
		return null;
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
				Object testNode = node.jjtGetChild(0).jjtGetChild(0);
				if (testNode instanceof ASTAttributeTest
						|| testNode instanceof ASTSchemaAttributeTest) {
					axis = AxisEnum.ATTRIBUTE;
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
		return null;
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
		return null;
	}

	public Object visit(ASTAdditiveExpr node, Object data) {
		visitOperator(node, data);
		return null;
	}

	public Object visit(ASTAndExpr node, Object data) {
		visitSeparator(node, data, " and ");
		return null;
	}

	public Object visit(ASTCastExpr node, Object data) {
		visitSeparator(node, data, " cast as ");
		return null;
	}

	public Object visit(ASTComparisonExpr node, Object data) {
		visitOperator(node, data);
		return null;
	}

	public Object visit(ASTContextItemExpr node, Object data) {
		print(".");
		return null;
	}

	public Object visit(ASTForExpr node, Object data) {
		print("for");
		for (int i = 0; i < (node.jjtGetNumChildren() - 1) / 2; i++) {
			if (i > 0) {
				print(",");
			}
			print(" $");
			node.jjtGetChild(i * 2).jjtAccept(this, data);
			print(" in ");
			node.jjtGetChild(i * 2 + 1).jjtAccept(this, data);
		}
		print(" return ");
		node.jjtGetChild(node.jjtGetNumChildren() - 1).jjtAccept(this, data);
		return null;
	}

	public Object visit(ASTForwardAxis node, Object data) {
		AxisEnum axis = node.getAxis(0);
		switch (printMode) {
		case ABBREVIATE:
			switch (axis) {
			case CHILD:
				break;
			case ATTRIBUTE:
				Node sibling = node.jjtGetParent().jjtGetChild(1);
				if (sibling instanceof ASTNodeTest
						&& (sibling.jjtGetChild(0) instanceof ASTAttributeTest || sibling
								.jjtGetChild(0) instanceof ASTSchemaAttributeTest)) {
					// No explicit attribute axis
				} else {
					print("@");
				}
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
		return null;
	}

	public Object visit(ASTIfExpr node, Object data) {
		print("if ");
		print("(");
		node.jjtGetChild(0).jjtAccept(this, data);
		print(")");
		print(" then ");
		node.jjtGetChild(1).jjtAccept(this, data);
		print(" else ");
		node.jjtGetChild(2).jjtAccept(this, data);
		return null;
	}

	public Object visit(ASTInstanceofExpr node, Object data) {
		visitSeparator(node, data, " instance of ");
		return null;
	}

	public Object visit(ASTIntersectExceptExpr node, Object data) {
		visitOperator(node, data);
		return null;
	}

	public Object visit(ASTMultiplicativeExpr node, Object data) {
		visitOperator(node, data);
		return null;
	}

	public Object visit(ASTOrExpr node, Object data) {
		visitSeparator(node, data, " or ");
		return null;
	}

	private boolean isRoot(Node node) {
		//
		// Do we have the structure for an unabbreviated / ?
		// Looking for: (fn:root(self::node()) treat as document-node())
		//
		boolean root = false;
		if (node instanceof ASTStepExpr) {
			node = node.jjtGetChild(0);
		}
		if (node instanceof ASTParenthesizedExpr) {
			Node n = findOnlyOne(node, ASTExpr.class);
			n = findOnlyOne(n, ASTTreatExpr.class);
			ASTTreatExpr treatExpr = (ASTTreatExpr) n;
			if (treatExpr != null) {

				// Check the SequenceType branch first, it is shortest
				n = findOnly(treatExpr, ASTSequenceType.class, 2);
				n = findOnlyOne(n, ASTItemType.class);
				n = findOnlyOne(n, ASTDocumentTest.class);
				if (n != null) {

					// Now check the CastableExpr branch
					n = findOnly(treatExpr, ASTStepExpr.class, 2);
					n = findOnlyOne(n, ASTFunctionCall.class);
					ASTFunctionCall functionCall = (ASTFunctionCall) n;
					// Call to fn:root() with one argument?
					if (functionCall != null
							&& "fn:root".equals(functionCall.getImage())
							&& functionCall.jjtGetNumChildren() == 1) {
						n = findOnlyOne(n, ASTStepExpr.class);
						// Must be on 'self' axis
						ASTForwardAxis forwardAxis = (ASTForwardAxis) findOnly(
								n, ASTForwardAxis.class, 2);
						if (forwardAxis != null
								&& AxisEnum.SELF == forwardAxis.getAxis(0)) {
							n = findOnly(n, ASTNodeTest.class, 2);
							n = findOnlyOne(n, ASTAnyKindTest.class);
							if (n != null) {
								root = true;
							}
						}
					}
				}
			}
		}
		return root;
	}

	public Object visit(ASTParenthesizedExpr node, Object data) {
		switch (printMode) {
		case ABBREVIATE:
			boolean rootStructure = isRoot(node);
			if (rootStructure) {
				print("/");
			} else {
				print("(");
				node.childrenAccept(this, data);
				print(")");
			}
			break;
		case UNABBREVIATE:
			print("(");
			node.childrenAccept(this, data);
			print(")");
			break;
		default:
			throw new IllegalStateException("Unknown PrintModeEnum: "
					+ printMode);
		}
		return null;
	}

	public Object visit(ASTPathExpr node, Object data) {
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			Node child = node.jjtGetChild(i);
			if (i > 0) {
				Node priorChild = node.jjtGetChild(i - 1);
				if (printMode == PrintModeEnum.UNABBREVIATE
						|| (!(priorChild instanceof ASTSlash
								|| priorChild instanceof ASTSlashSlash || isRoot(priorChild)) && !(child instanceof ASTSlash
								|| child instanceof ASTSlashSlash || isRoot(child)))) {
					print("/");
				}
			}
			if (i == 0 && printMode == PrintModeEnum.UNABBREVIATE
					&& child instanceof ASTSlashSlash) {
				print("(fn:root(self::node()) treat as document-node())/");
			}
			child.jjtAccept(this, data);
		}
		return null;
	}

	public Object visit(ASTPredicate node, Object data) {
		print("[");
		node.childrenAccept(this, data);
		print("]");
		return null;
	}

	public Object visit(ASTPredicateList node, Object data) {
		// Nothing to do
		node.childrenAccept(this, data);
		return null;
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
		return null;
	}

	public Object visit(ASTRangeExpr node, Object data) {
		visitSeparator(node, data, " to ");
		return null;
	}

	public Object visit(ASTReverseAxis node, Object data) {
		print(node.getAxis(0));
		print("::");
		return null;
	}

	public Object visit(ASTSingleType node, Object data) {
		node.childrenAccept(this, data);
		if (node.getImage() != null) {
			print("?");
		}
		return null;
	}

	public Object visit(ASTSlash node, Object data) {
		switch (printMode) {
		case ABBREVIATE:
			print("/");
			break;
		case UNABBREVIATE:
			print("(fn:root(self::node()) treat as document-node())");
			break;
		default:
			throw new IllegalStateException("Unknown PrintModeEnum: "
					+ printMode);
		}
		return null;
	}

	public Object visit(ASTSlashSlash node, Object data) {
		switch (printMode) {
		case ABBREVIATE:
			print("//");
			break;
		case UNABBREVIATE:
			print("descendant-or-self::node()");
			break;
		default:
			throw new IllegalStateException("Unknown PrintModeEnum: "
					+ printMode);
		}
		return null;
	}

	public Object visit(ASTStepExpr node, Object data) {
		switch (printMode) {
		case ABBREVIATE:
			if (node.jjtGetNumChildren() == 2) {
				// Abbreviate: descendent-self::node()
				if (node.jjtGetChild(0) instanceof ASTForwardAxis) {
					if (((ASTForwardAxis) node.jjtGetChild(0)).getAxis(0) == AxisEnum.DESCENDANT_OR_SELF) {
						if (node.jjtGetChild(1) instanceof ASTNodeTest) {
							if (node.jjtGetChild(1).jjtGetChild(0) instanceof ASTAnyKindTest) {
								break;
							}
						}
					}
				}
				// Abbreviate: parent::node()
				if (node.jjtGetChild(0) instanceof ASTReverseAxis) {
					if (((ASTReverseAxis) node.jjtGetChild(0)).getAxis(0) == AxisEnum.PARENT) {
						if (node.jjtGetChild(1) instanceof ASTNodeTest) {
							if (node.jjtGetChild(1).jjtGetChild(0) instanceof ASTAnyKindTest) {
								print("..");
								break;
							}
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
		return null;
	}

	public Object visit(ASTTreatExpr node, Object data) {
		visitSeparator(node, data, " treat as ");
		return null;
	}

	public Object visit(ASTUnaryExpr node, Object data) {
		for (int i = 0; i < node.getNumOperators(); i++) {
			print(node.getOperator(i));
		}
		node.childrenAccept(this, data);
		return null;
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
		return null;
	}

	public Object visit(ASTXPath node, Object data) {
		// Nothing to do
		node.childrenAccept(this, data);
		return null;
	}

	public Object visit(SimpleNode node, Object data) {
		// Nothing to do
		throw new IllegalStateException("Should not be here!");
	}
}
