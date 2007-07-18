package net.sourceforge.pmd.jerry.env;

import static net.sourceforge.pmd.jerry.env.EnvironmentEnum.DYNAMIC;
import static net.sourceforge.pmd.jerry.env.EnvironmentEnum.STATIC;

public enum EnvironmentComponentEnum {
	/**
	 * TODO Document this
	 */
	XPATH_1_0_COMPATIBILITY(STATIC, "xpath1.0_compatibility"),
	/**
	 * TODO Document this
	 */
	NAMESPACE(STATIC, "namespace"),
	/**
	 * TODO Document this
	 */
	DEFAULT_ELEM_NAMESPACE(STATIC, "default_elem_namespace"),
	/**
	 * TODO Document this
	 */
	DEFAULT_FUNCTION_NAMESPACE(STATIC, "default_function_namespace"),
	/**
	 * TODO Document this
	 */
	TYPE_DEFN(STATIC, "typeDefn"),
	/**
	 * TODO Document this
	 */
	ELEM_DECL(STATIC, "elemDecl"),
	/**
	 * TODO Document this
	 */
	ATTR_DECL(STATIC, "attrDecl"),
	/**
	 * TODO Document this
	 */
	VAR_TYPE(STATIC, "varType"),
	/**
	 * TODO Document this
	 */
	FUNC_TYPE(STATIC, "funcType"),
	/**
	 * TODO Document this
	 */
	COLLATIONS(STATIC, "collations"),
	/**
	 * TODO Document this
	 */
	DEFAULT_COLLATION(STATIC, "defaultCollation"),
	/**
	 * TODO Document this
	 */
	CONSTRUCTION_MODE(STATIC, "constructionMode"),
	/**
	 * TODO Document this
	 */
	ORDERING_MODE(STATIC, "orderingMode"),
	/**
	 * TODO Document this
	 */
	DEFAULT_EMPTY_SEQUENCE_ORDER(STATIC, "defaultEmptySequenceOrder"),
	/**
	 * TODO Document this
	 */
	BOUNDARY_SPACE(STATIC, "boundarySpace"),
	/**
	 * TODO Document this
	 */
	COPY_NAMESPACES_MODE(STATIC, "copyNamespacesMode"),
	/**
	 * TODO Document this
	 */
	BASE_URI(STATIC, "baseURI"),
	/**
	 * TODO Document this
	 */
	DOC_TYPE(STATIC, "docType"),
	/**
	 * TODO Document this
	 */
	COLLECTION_TYPE(STATIC, "collectionType"),
	/**
	 * TODO Document this
	 */
	DEFAULT_COLLECTION_TYPE(STATIC, "defaultCollectionType"),

	/**
	 * TODO Document this
	 */
	VAR_VALUE(DYNAMIC, "varValue"),
	/**
	 * TODO Document this
	 */
	FUNC_DEFN(DYNAMIC, "funcDefn"),
	/**
	 * TODO Document this
	 */
	DATE_TIME(DYNAMIC, "dateTime"),
	/**
	 * TODO Document this
	 */
	TIMEZONE(DYNAMIC, "timezone"),
	/**
	 * TODO Document this
	 */
	DOC_VALUE(DYNAMIC, "docValue"),
	/**
	 * TODO Document this
	 */
	COLLECTION_VALUE(DYNAMIC, "collectionValue"),
	/**
	 * TODO Document this
	 */
	DEFAULT_COLLECTION_VALUE(DYNAMIC, "defaultCollectionValue");

	private final EnvironmentEnum environmentEnum;

	private final String name;

	private EnvironmentComponentEnum(EnvironmentEnum environmentEnum,
			String name) {
		this.environmentEnum = environmentEnum;
		this.name = name;
	}

	public EnvironmentEnum getEnvironmentEnum() {
		return environmentEnum;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return environmentEnum + "." + name;
	}
}
