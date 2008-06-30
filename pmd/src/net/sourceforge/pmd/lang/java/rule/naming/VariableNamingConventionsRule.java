/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.naming;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;

public class VariableNamingConventionsRule extends AbstractJavaRule {

    private boolean checkMembers;
    private boolean checkLocals;
    private boolean checkParameters;
    private String[] staticPrefixes;
    private String[] staticSuffixes;
    private String[] memberPrefixes;
    private String[] memberSuffixes;
    private String[] localPrefixes;
    private String[] localSuffixes;
    private String[] parameterPrefixes;
    private String[] parameterSuffixes;

    private static final PropertyDescriptor checkMembersDescriptor = new BooleanProperty("checkMembers",
	    "Check member variables", true, 1.0f);

    private static final PropertyDescriptor checkLocalsDescriptor = new BooleanProperty("checkLocals",
	    "Check local variables", true, 2.0f);

    private static final PropertyDescriptor checkParametersDescriptor = new BooleanProperty("checkParameters",
	    "Check constructor and method parameter variables", true, 3.0f);

    private static final PropertyDescriptor staticPrefixesDescriptor = new StringProperty("staticPrefix",
	    "Static member prefixes", new String[] { "" }, 4.0f, ',');

    private static final PropertyDescriptor staticSuffixesDescriptor = new StringProperty("staticSuffix",
	    "Static member suffixes", new String[] { "" }, 5.0f, ',');

    private static final PropertyDescriptor memberPrefixesDescriptor = new StringProperty("memberPrefix",
	    "Instance member prefixes", new String[] { "" }, 6.0f, ',');

    private static final PropertyDescriptor memberSuffixesDescriptor = new StringProperty("memberSuffix",
	    "Instance member suffixes", new String[] { "" }, 7.0f, ',');

    private static final PropertyDescriptor localPrefixesDescriptor = new StringProperty("localPrefix",
	    "Local variable prefixes", new String[] { "" }, 8.0f, ',');

    private static final PropertyDescriptor localSuffixesDescriptor = new StringProperty("localSuffix",
	    "Local variable suffixes", new String[] { "" }, 9.0f, ',');

    private static final PropertyDescriptor parameterPrefixesDescriptor = new StringProperty("parameterPrefix",
	    "Formal parameter prefixes", new String[] { "" }, 10.0f, ',');

    private static final PropertyDescriptor parameterSuffixesDescriptor = new StringProperty("parameterSuffix",
	    "Formal parameter suffixes", new String[] { "" }, 11.0f, ',');

    private static final Map<String, PropertyDescriptor> propertyDescriptorsByName = asFixedMap(new PropertyDescriptor[] {
	    checkMembersDescriptor, checkLocalsDescriptor, checkParametersDescriptor, staticPrefixesDescriptor,
	    staticSuffixesDescriptor, memberPrefixesDescriptor, memberSuffixesDescriptor, localPrefixesDescriptor,
	    localSuffixesDescriptor, parameterPrefixesDescriptor, parameterSuffixesDescriptor, });

    /**
     * @return Map
     */
    protected Map<String, PropertyDescriptor> propertiesByName() {
	return propertyDescriptorsByName;
    }

    public Object visit(ASTCompilationUnit node, Object data) {
	init();
	return super.visit(node, data);
    }

    protected void init() {
	checkMembers = getBooleanProperty(checkMembersDescriptor);
	checkLocals = getBooleanProperty(checkLocalsDescriptor);
	checkParameters = getBooleanProperty(checkParametersDescriptor);
	staticPrefixes = getStringProperties(staticPrefixesDescriptor);
	staticSuffixes = getStringProperties(staticSuffixesDescriptor);
	memberPrefixes = getStringProperties(memberPrefixesDescriptor);
	memberSuffixes = getStringProperties(memberSuffixesDescriptor);
	localPrefixes = getStringProperties(localPrefixesDescriptor);
	localSuffixes = getStringProperties(localSuffixesDescriptor);
	parameterPrefixes = getStringProperties(parameterPrefixesDescriptor);
	parameterSuffixes = getStringProperties(parameterSuffixesDescriptor);
    }

    public Object visit(ASTFieldDeclaration node, Object data) {
	if (!checkMembers) {
	    return data;
	}
	boolean isStatic = node.isStatic();
	boolean isFinal = node.isFinal();
	// Anything from an interface is necessarily static and final
	if (node.jjtGetParent().jjtGetParent().jjtGetParent() instanceof ASTClassOrInterfaceDeclaration
		&& ((ASTClassOrInterfaceDeclaration) node.jjtGetParent().jjtGetParent().jjtGetParent()).isInterface()) {
	    isStatic = true;
	    isFinal = true;
	}
	return checkVariableDeclarators(node.isStatic() ? staticPrefixes : memberPrefixes, isStatic ? staticSuffixes
		: memberSuffixes, node, isStatic, isFinal, data);
    }

    public Object visit(ASTLocalVariableDeclaration node, Object data) {
	if (!checkLocals) {
	    return data;
	}
	return checkVariableDeclarators(localPrefixes, localSuffixes, node, false, node.isFinal(), data);
    }

    public Object visit(ASTFormalParameters node, Object data) {
	if (!checkParameters) {
	    return data;
	}
	for (ASTFormalParameter formalParameter : node.findChildrenOfType(ASTFormalParameter.class)) {
	    for (ASTVariableDeclaratorId variableDeclaratorId : formalParameter
		    .findChildrenOfType(ASTVariableDeclaratorId.class)) {
		checkVariableDeclaratorId(parameterPrefixes, parameterSuffixes, node, false, formalParameter.isFinal(),
			variableDeclaratorId, data);
	    }
	}
	return data;
    }

    private Object checkVariableDeclarators(String[] prefixes, String[] suffixes, Node root, boolean isStatic,
	    boolean isFinal, Object data) {
	for (ASTVariableDeclaratorId variableDeclaratorId : root.findDescendantsOfType(ASTVariableDeclaratorId.class)) {
	    checkVariableDeclaratorId(prefixes, suffixes, root, isStatic, isFinal, variableDeclaratorId, data);
	}
	return data;
    }

    private Object checkVariableDeclaratorId(String[] prefixes, String[] suffixes, Node root, boolean isStatic,
	    boolean isFinal, ASTVariableDeclaratorId variableDeclaratorId, Object data) {

	// Get the variable name
	String varName = variableDeclaratorId.getImage();

	// Skip serialVersionUID
	if (varName.equals("serialVersionUID")) {
	    return data;
	}

	// Static finals should be uppercase
	if (isStatic && isFinal) {
	    if (!varName.equals(varName.toUpperCase())) {
		addViolationWithMessage(data, variableDeclaratorId,
			"Variables that are final and static should be in all caps.");
	    }
	    return data;
	} else if (!isFinal) {
	    String normalizedVarName = normalizeVariableName(varName, prefixes, suffixes);

	    if (normalizedVarName.indexOf('_') >= 0) {
		addViolationWithMessage(data, variableDeclaratorId,
			"Variables that are not final should not contain underscores (except for underscores in standard prefix/suffix).");
	    }
	    if (Character.isUpperCase(varName.charAt(0))) {
		addViolationWithMessage(data, variableDeclaratorId, "Variables should start with a lowercase character");
	    }
	}
	return data;
    }

    private String normalizeVariableName(String varName, String[] prefixes, String[] suffixes) {
	return stripSuffix(stripPrefix(varName, prefixes), suffixes);
    }

    private String stripSuffix(String varName, String[] suffixes) {
	if (suffixes != null) {
	    for (int i = 0; i < suffixes.length; i++) {
		if (varName.endsWith(suffixes[i])) {
		    varName = varName.substring(0, varName.length() - suffixes[i].length());
		    break;
		}
	    }
	}
	return varName;
    }

    private String stripPrefix(String varName, String[] prefixes) {
	if (prefixes != null) {
	    for (int i = 0; i < prefixes.length; i++) {
		if (varName.startsWith(prefixes[i])) {
		    return varName.substring(prefixes[i].length());
		}
	    }
	}
	return varName;
    }
}
