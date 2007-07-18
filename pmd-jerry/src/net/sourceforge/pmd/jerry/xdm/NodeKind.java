package net.sourceforge.pmd.jerry.xdm;

/**
 * See the XQuery 1.0 and XPath 2.0 Data Model (XDM) specification for details
 * on Node types/kinds. {@link http://www.w3.org/TR/xpath-datamodel/#Node}
 */
public enum NodeKind {
	/**
	 * Document node. See XDM spec
	 * {@link http://www.w3.org/TR/xpath-datamodel/#DocumentNode}
	 */
	DOCUMENT("document"),

	/**
	 * Element node. See XDM spec
	 * {@link http://www.w3.org/TR/xpath-datamodel/#ElementNode}
	 */
	ELEMENT("element"),

	/**
	 * Attribute node. See XDM spec
	 * {@link http://www.w3.org/TR/xpath-datamodel/#AttributeNode}
	 */
	ATTRIBUTE("attribute"),

	/**
	 * Text node. See XDM spec
	 * {@link http://www.w3.org/TR/xpath-datamodel/#TextNode}
	 */
	TEXT("text"),

	/**
	 * Namespace node. See XDM spec
	 * {@link http://www.w3.org/TR/xpath-datamodel/#NamespaceNode}
	 */
	NAMESPACE("namespace"),

	/**
	 * ProcessingInstruction node. See XDM spec
	 * {@link http://www.w3.org/TR/xpath-datamodel/#ProcessingInstructionNode}
	 */
	PROCESSING_INSTRUCTION("processing_instruction"),

	/**
	 * Comment node. See XDM spec
	 * {@link http://www.w3.org/TR/xpath-datamodel/#CommentNode}
	 */
	COMMENT("comment");

	private final String name;

	NodeKind(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}
}
