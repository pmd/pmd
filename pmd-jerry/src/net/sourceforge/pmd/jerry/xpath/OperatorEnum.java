package net.sourceforge.pmd.jerry.xpath;

public enum OperatorEnum {

	/**
	 * Arithmetic addition operator.
	 */
	ADDITION("+", true),

	/**
	 * Arithmetic subtraction operator.
	 */
	SUBTRACTION("-", true),

	/**
	 * Arithmetic multiplication operator.
	 */
	MULTIPLICATION("*", true),

	/**
	 * Arithmetic division operator.
	 */
	DIVISION("div", true),

	/**
	 * Arithmetic integer division operator.
	 */
	INTEGER_DIVISION("idiv", true),

	/**
	 * Arithmetic modulus operator.
	 */
	MODULUS("mod", true),

	/**
	 * Arithmetic unary positive operator.
	 */
	UNARY_PLUS("+", false),

	/**
	 * Arithmetic unary negative operator.
	 */
	UNARY_MINUS("-", false),

	/**
	 * Value comparision equal operator.
	 */
	VALUE_COMPARISION_EQUAL("eq", true),

	/**
	 * Value comparision not equal operator.
	 */
	VALUE_COMPARISION_NOT_EQUAL("ne", true),

	/**
	 * Value comparision lesser than operator.
	 */
	VALUE_COMPARISION_LESSER_THAN("lt", true),

	/**
	 * Value comparision lesser than or equal operator.
	 */
	VALUE_COMPARISION_LESSER_THAN_OR_EQUAL("le", true),

	/**
	 * Value comparision greater than operator.
	 */
	VALUE_COMPARISION_GREATER_THAN("gt", true),

	/**
	 * Value comparision greater than or equal operator.
	 */
	VALUE_COMPARISION_GREATER_THAN_OR_EQUAL("ge", true),

	/**
	 * General comparision equal operator.
	 */
	GENERAL_COMPARISION_EQUAL("=", true),

	/**
	 * General comparision not equal operator.
	 */
	GENERAL_COMPARISION_NOT_EQUAL("!=", true),

	/**
	 * General comparision lesser than operator.
	 */
	GENERAL_COMPARISION_LESSER_THAN("<", true),

	/**
	 * General comparision lesser than or equal operator.
	 */
	GENERAL_COMPARISION_LESSER_THAN_OR_EQUAL("<=", true),

	/**
	 * General comparision greater than operator.
	 */
	GENERAL_COMPARISION_GREATER_THAN(">", true),

	/**
	 * General comparision greater than or equal operator.
	 */
	GENERAL_COMPARISION_GREATER_THAN_OR_EQUAL("<=", true),

	/**
	 * Node comparision is operator.
	 */
	NODE_COMPARISION_IS("is", true),

	/**
	 * Node comparision preceeds operator.
	 */
	NODE_COMPARISION_PRECEEDS("<<", true),

	/**
	 * Node comparision follows operator.
	 */
	NODE_COMPARISION_FOLLOWS(">>", true),

	/**
	 * Sequence union operator.
	 */
	SEQUENCE_UNION("union", true),

	/**
	 * Sequence intersect operator.
	 */
	SEQUENCE_INTERSECT("intersect", true),

	/**
	 * Sequence except operator.
	 */
	SEQUENCE_EXCEPT("except", true);

	private final String name;
	private final boolean binary;

	private OperatorEnum(String name, boolean binary) {
		this.name = name;
		this.binary = binary;
	}

	public boolean isBinary() {
		return binary;
	}

	public String toString() {
		return name;
	}
}
