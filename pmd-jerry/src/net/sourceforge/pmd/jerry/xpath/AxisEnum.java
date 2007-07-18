package net.sourceforge.pmd.jerry.xpath;

public enum AxisEnum {
	/**
	 * TODO Document
	 */
	CHILD("child", true),
	/**
	 * TODO Document
	 */
	DESCENDANT("descendant", true),
	/**
	 * TODO Document
	 */
	ATTRIBUTE("attribute", true),
	/**
	 * TODO Document
	 */
	SELF("self", true),
	/**
	 * TODO Document
	 */
	DESCENDANT_OR_SELF("descendant-or-self", true),
	/**
	 * TODO Document
	 */
	FOLLOWING_SIBLING("following-sibling", true),
	/**
	 * TODO Document
	 */
	FOLLOWING("following", true),
	/**
	 * TODO Document
	 */
	NAMESPACE("namespace", true),

	/**
	 * TODO Document
	 */
	PARENT("parent", false),
	/**
	 * TODO Document
	 */
	ANCESTOR("ancestor", false),
	/**
	 * TODO Document
	 */
	PRECEDING_SIBLINGS("preceding-siblings", false),
	/**
	 * TODO Document
	 */
	PRECEDING("preceding", false),
	/**
	 * TODO Document
	 */
	ANCESTOR_OR_SELF("ancestor-or-self", false);

	private final String name;

	private final boolean forward;

	AxisEnum(String name, boolean forward) {
		this.name = name;
		this.forward = forward;
	}

	public boolean isForward() {
		return forward;
	}

	public boolean isReverse() {
		return !forward;
	}

	public String toString() {
		return name;
	}
}
