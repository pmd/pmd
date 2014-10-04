/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.naming;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;
import net.sourceforge.pmd.lang.rule.properties.StringMultiProperty;
import net.sourceforge.pmd.util.CollectionUtil;

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

    private static final BooleanProperty CHECK_MEMBERS_DESCRIPTOR = new BooleanProperty("checkMembers",
	    "Check member variables", true, 1.0f);

    private static final BooleanProperty CHECK_LOCALS_DESCRIPTOR = new BooleanProperty("checkLocals",
	    "Check local variables", true, 2.0f);

    private static final BooleanProperty CHECK_PARAMETERS_DESCRIPTOR = new BooleanProperty("checkParameters",
	    "Check constructor and method parameter variables", true, 3.0f);

    private static final StringMultiProperty STATIC_PREFIXES_DESCRIPTOR = new StringMultiProperty("staticPrefix",
	    "Static variable prefixes", new String[] { "" }, 4.0f, ',');

    private static final StringMultiProperty STATIC_SUFFIXES_DESCRIPTOR = new StringMultiProperty("staticSuffix",
	    "Static variable suffixes", new String[] { "" }, 5.0f, ',');

    private static final StringMultiProperty MEMBER_PREFIXES_DESCRIPTOR = new StringMultiProperty("memberPrefix",
	    "Member variable prefixes", new String[] { "" }, 6.0f, ',');

    private static final StringMultiProperty MEMBER_SUFFIXES_DESCRIPTOR = new StringMultiProperty("memberSuffix",
	    "Member variable suffixes", new String[] { "" }, 7.0f, ',');

    private static final StringMultiProperty LOCAL_PREFIXES_DESCRIPTOR = new StringMultiProperty("localPrefix",
	    "Local variable prefixes", new String[] { "" }, 8.0f, ',');

    private static final StringMultiProperty LOCAL_SUFFIXES_DESCRIPTOR = new StringMultiProperty("localSuffix",
	    "Local variable suffixes", new String[] { "" }, 9.0f, ',');

    private static final StringMultiProperty PARAMETER_PREFIXES_DESCRIPTOR = new StringMultiProperty("parameterPrefix",
	    "Method parameter variable prefixes", new String[] { "" }, 10.0f, ',');

    private static final StringMultiProperty PARAMETER_SUFFIXES_DESCRIPTOR = new StringMultiProperty("parameterSuffix",
	    "Method parameter variable suffixes", new String[] { "" }, 11.0f, ',');

    public VariableNamingConventionsRule() {
	definePropertyDescriptor(CHECK_MEMBERS_DESCRIPTOR);
	definePropertyDescriptor(CHECK_LOCALS_DESCRIPTOR);
	definePropertyDescriptor(CHECK_PARAMETERS_DESCRIPTOR);
	definePropertyDescriptor(STATIC_PREFIXES_DESCRIPTOR);
	definePropertyDescriptor(STATIC_SUFFIXES_DESCRIPTOR);
	definePropertyDescriptor(MEMBER_PREFIXES_DESCRIPTOR);
	definePropertyDescriptor(MEMBER_SUFFIXES_DESCRIPTOR);
	definePropertyDescriptor(LOCAL_PREFIXES_DESCRIPTOR);
	definePropertyDescriptor(LOCAL_SUFFIXES_DESCRIPTOR);
	definePropertyDescriptor(PARAMETER_PREFIXES_DESCRIPTOR);
	definePropertyDescriptor(PARAMETER_SUFFIXES_DESCRIPTOR);
    }

    public Object visit(ASTCompilationUnit node, Object data) {
	init();
	return super.visit(node, data);
    }

    protected void init() {
	checkMembers = getProperty(CHECK_MEMBERS_DESCRIPTOR);
	checkLocals = getProperty(CHECK_LOCALS_DESCRIPTOR);
	checkParameters = getProperty(CHECK_PARAMETERS_DESCRIPTOR);
	staticPrefixes = getProperty(STATIC_PREFIXES_DESCRIPTOR);
	staticSuffixes = getProperty(STATIC_SUFFIXES_DESCRIPTOR);
	memberPrefixes = getProperty(MEMBER_PREFIXES_DESCRIPTOR);
	memberSuffixes = getProperty(MEMBER_SUFFIXES_DESCRIPTOR);
	localPrefixes = getProperty(LOCAL_PREFIXES_DESCRIPTOR);
	localSuffixes = getProperty(LOCAL_SUFFIXES_DESCRIPTOR);
	parameterPrefixes = getProperty(PARAMETER_PREFIXES_DESCRIPTOR);
	parameterSuffixes = getProperty(PARAMETER_SUFFIXES_DESCRIPTOR);
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
	for (ASTVariableDeclarator variableDeclarator : root.findChildrenOfType(ASTVariableDeclarator.class)) {
	    for (ASTVariableDeclaratorId variableDeclaratorId : variableDeclarator
		    .findChildrenOfType(ASTVariableDeclaratorId.class)) {
		checkVariableDeclaratorId(prefixes, suffixes, root, isStatic, isFinal, variableDeclaratorId, data);
	    }
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
			"Variables that are final and static should be all capitals, ''{0}'' is not all capitals.",
			new Object[] { varName });
	    }
	    return data;
	} else if (!isFinal) {
	    String normalizedVarName = normalizeVariableName(varName, prefixes, suffixes);

	    if (normalizedVarName.indexOf('_') >= 0) {
		addViolationWithMessage(
			data,
			variableDeclaratorId,
			"Only variables that are final should contain underscores (except for underscores in standard prefix/suffix), ''{0}'' is not final.",
			new Object[] { varName });
	    }
	    if (Character.isUpperCase(varName.charAt(0))) {
		addViolationWithMessage(data, variableDeclaratorId,
			"Variables should start with a lowercase character, ''{0}'' starts with uppercase character.",
			new Object[] { varName });
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
    
	public boolean hasPrefixesOrSuffixes() {
		
		for (PropertyDescriptor<?> desc : getPropertyDescriptors()) {
			if (desc instanceof StringMultiProperty) {
				String[] values = getProperty((StringMultiProperty)desc);
				if (CollectionUtil.isNotEmpty(values)) return true;				
			}
		}
		return false;	
	}
	
	public String dysfunctionReason() {
		return hasPrefixesOrSuffixes() ?
				null :
				"No prefixes or suffixes specified";
	}
		
}
