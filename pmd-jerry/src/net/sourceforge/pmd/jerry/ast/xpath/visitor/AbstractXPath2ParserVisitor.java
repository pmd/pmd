package net.sourceforge.pmd.jerry.ast.xpath.visitor;

import net.sourceforge.pmd.jerry.ast.xpath.ASTAnyKindTest;
import net.sourceforge.pmd.jerry.ast.xpath.ASTAtomicType;
import net.sourceforge.pmd.jerry.ast.xpath.ASTAttribNameOrWildcard;
import net.sourceforge.pmd.jerry.ast.xpath.ASTAttributeDeclaration;
import net.sourceforge.pmd.jerry.ast.xpath.ASTAttributeName;
import net.sourceforge.pmd.jerry.ast.xpath.ASTAttributeTest;
import net.sourceforge.pmd.jerry.ast.xpath.ASTCastableExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTCommentTest;
import net.sourceforge.pmd.jerry.ast.xpath.ASTDecimalLiteral;
import net.sourceforge.pmd.jerry.ast.xpath.ASTDocumentTest;
import net.sourceforge.pmd.jerry.ast.xpath.ASTDoubleLiteral;
import net.sourceforge.pmd.jerry.ast.xpath.ASTElementDeclaration;
import net.sourceforge.pmd.jerry.ast.xpath.ASTElementName;
import net.sourceforge.pmd.jerry.ast.xpath.ASTElementNameOrWildcard;
import net.sourceforge.pmd.jerry.ast.xpath.ASTElementTest;
import net.sourceforge.pmd.jerry.ast.xpath.ASTExpr;
import net.sourceforge.pmd.jerry.ast.xpath.ASTFunctionCall;
import net.sourceforge.pmd.jerry.ast.xpath.ASTIntegerLiteral;
import net.sourceforge.pmd.jerry.ast.xpath.ASTItemType;
import net.sourceforge.pmd.jerry.ast.xpath.ASTNameTest;
import net.sourceforge.pmd.jerry.ast.xpath.ASTNodeTest;
import net.sourceforge.pmd.jerry.ast.xpath.ASTOccurrenceIndicator;
import net.sourceforge.pmd.jerry.ast.xpath.ASTPITest;
import net.sourceforge.pmd.jerry.ast.xpath.ASTSchemaAttributeTest;
import net.sourceforge.pmd.jerry.ast.xpath.ASTSchemaElementTest;
import net.sourceforge.pmd.jerry.ast.xpath.ASTSequenceType;
import net.sourceforge.pmd.jerry.ast.xpath.ASTStringLiteral;
import net.sourceforge.pmd.jerry.ast.xpath.ASTTextTest;
import net.sourceforge.pmd.jerry.ast.xpath.ASTTypeName;
import net.sourceforge.pmd.jerry.ast.xpath.ASTVarName;
import net.sourceforge.pmd.jerry.ast.xpath.ASTVarRef;
import net.sourceforge.pmd.jerry.ast.xpath.ASTWildcard;
import net.sourceforge.pmd.jerry.ast.xpath.Node;
import net.sourceforge.pmd.jerry.ast.xpath.SimpleNode;
import net.sourceforge.pmd.jerry.ast.xpath.XPath2ParserVisitor;

public abstract class AbstractXPath2ParserVisitor extends AbstractPrintVisitor
		implements XPath2ParserVisitor {

	protected void TODO(Node node) {
		StringBuffer buf = new StringBuffer(100);
		buf.append("Visit for ");
		buf.append(node);
		if (node.jjtGetParent() != null) {
			buf.append(" w/ parent ");
			buf.append(node.jjtGetParent());
		}
		System.out.println(buf);
	}

	protected Object visitSeparator(SimpleNode node, Object data,
			String separator) {
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			if (i > 0) {
				print(separator);
			}
			node.jjtGetChild(i).jjtAccept(this, data);
		}
		return null;
	}

	protected Node findOnlyOne(Node node, Class nodeType) {
		return findOnly(node, nodeType, 1);
	}

	// Find the 1st child node of the given type, but only if the child count is
	// exactly as expected.
	protected Node findOnly(Node node, Class nodeType, int childCount) {
		Node found = null;
		if (node != null && node.jjtGetNumChildren() == childCount) {
			for (int i = 0; i < node.jjtGetNumChildren(); i++) {
				if (nodeType.isAssignableFrom(node.jjtGetChild(i).getClass())) {
					found = node.jjtGetChild(i);
				}
			}
		}
		return found;
	}

	public final Object visit(ASTAnyKindTest node, Object data) {
		print("node()");
		return null;
	}

	public final Object visit(ASTAtomicType node, Object data) {
		print(node.getImage());
		return null;
	}

	public final Object visit(ASTAttribNameOrWildcard node, Object data) {
		if (node.jjtGetNumChildren() > 0) {
			node.childrenAccept(this, data);
		} else {
			print("*");
		}
		return null;
	}

	public final Object visit(ASTAttributeDeclaration node, Object data) {
		// Nothing to do
		node.childrenAccept(this, data);
		return null;
	}

	public final Object visit(ASTAttributeName node, Object data) {
		print(node.getImage());
		return null;
	}

	public final Object visit(ASTAttributeTest node, Object data) {
		print("attribute(");
		visitSeparator(node, data, ", ");
		print(")");
		return null;
	}

	public final Object visit(ASTCastableExpr node, Object data) {
		visitSeparator(node, data, " castable as ");
		return null;
	}

	public final Object visit(ASTCommentTest node, Object data) {
		print("comment()");
		return null;
	}

	public final Object visit(ASTElementDeclaration node, Object data) {
		// Nothing to do
		node.childrenAccept(this, data);
		return null;
	}

	public final Object visit(ASTDecimalLiteral node, Object data) {
		print(node.getImage());
		return null;
	}

	public final Object visit(ASTDocumentTest node, Object data) {
		print("document-node(");
		node.childrenAccept(this, data);
		print(")");
		return null;
	}

	public final Object visit(ASTDoubleLiteral node, Object data) {
		print(node.getImage());
		return null;
	}

	public final Object visit(ASTElementName node, Object data) {
		print(node.getImage());
		return null;
	}

	public final Object visit(ASTElementNameOrWildcard node, Object data) {
		if (node.jjtGetNumChildren() > 0) {
			node.childrenAccept(this, data);
		} else {
			print("*");
		}
		return null;
	}

	public final Object visit(ASTElementTest node, Object data) {
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
		return null;
	}

	public final Object visit(ASTExpr node, Object data) {
		visitSeparator(node, data, ", ");
		return null;
	}

	public final Object visit(ASTFunctionCall node, Object data) {
		print(node.getImage());
		print("(");
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			if (i > 0) {
				print(", ");
			}
			node.jjtGetChild(i).jjtAccept(this, data);
		}
		print(")");
		return null;
	}

	public final Object visit(ASTIntegerLiteral node, Object data) {
		print(node.getImage());
		return null;
	}

	public final Object visit(ASTItemType node, Object data) {
		if (node.jjtGetNumChildren() == 0) {
			print("item()");
		} else {
			node.childrenAccept(this, data);
		}
		return null;
	}

	public final Object visit(ASTNameTest node, Object data) {
		if (node.jjtGetNumChildren() == 0) {
			print(node.getImage());
		} else {
			node.childrenAccept(this, data);
		}
		return null;
	}

	public final Object visit(ASTNodeTest node, Object data) {
		// Nothing to do
		node.childrenAccept(this, data);
		return null;
	}

	public final Object visit(ASTOccurrenceIndicator node, Object data) {
		print(node.getImage());
		return null;
	}

	public final Object visit(ASTPITest node, Object data) {
		print("processing-instruction(");
		if (node.getImage() != null) {
			print(node.getImage());
		} else {
			node.childrenAccept(this, data);
		}
		print(")");
		return null;
	}

	public final Object visit(ASTSchemaAttributeTest node, Object data) {
		print("schema-attribute(");
		node.childrenAccept(this, data);
		print(")");
		return null;
	}

	public final Object visit(ASTSchemaElementTest node, Object data) {
		print("schema-element(");
		node.childrenAccept(this, data);
		print(")");
		return null;
	}

	public final Object visit(ASTSequenceType node, Object data) {
		if (node.jjtGetNumChildren() == 0) {
			print("empty-sequence()");
		} else {
			node.childrenAccept(this, data);
		}
		return null;
	}

	public final Object visit(ASTStringLiteral node, Object data) {
		print(node.getImage());
		return null;
	}

	public final Object visit(ASTTextTest node, Object data) {
		print("text()");
		return null;
	}

	public final Object visit(ASTTypeName node, Object data) {
		print(node.getImage());
		return null;
	}

	public final Object visit(ASTVarName node, Object data) {
		print(node.getImage());
		return null;
	}

	public final Object visit(ASTVarRef node, Object data) {
		print("$");
		node.childrenAccept(this, data);
		return null;
	}

	public final Object visit(ASTWildcard node, Object data) {
		if (node.getImage() != null && node.isPrefix()) {
			print(node.getImage());
			print("::");
		}
		print("*");
		if (node.getImage() != null && !node.isPrefix()) {
			print("::");
			print(node.getImage());
		}
		return null;
	}
}
