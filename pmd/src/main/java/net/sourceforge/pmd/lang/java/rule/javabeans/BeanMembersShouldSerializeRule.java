/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.javabeans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType;
import net.sourceforge.pmd.lang.java.ast.ASTResultType;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.ClassScope;
import net.sourceforge.pmd.lang.java.symboltable.MethodNameDeclaration;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

public class BeanMembersShouldSerializeRule extends AbstractJavaRule {

    private String prefixProperty;

    private static final StringProperty PREFIX_DESCRIPTOR = new StringProperty("prefix", "A variable prefix to skip, i.e., m_",
	    "", 1.0f);
    
    public BeanMembersShouldSerializeRule() {
	definePropertyDescriptor(PREFIX_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
	prefixProperty = getProperty(PREFIX_DESCRIPTOR);
	super.visit(node, data);
	return data;
    }

    private static String[] imagesOf(List<? extends Node> nodes) {

	String[] imageArray = new String[nodes.size()];

	for (int i = 0; i < nodes.size(); i++) {
	    imageArray[i] = nodes.get(i).getImage();
	}
	return imageArray;
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
	if (node.isInterface()) {
	    return data;
	}

	Map<MethodNameDeclaration, List<NameOccurrence>> methods = node.getScope().getEnclosingScope(ClassScope.class)
		.getMethodDeclarations();
	List<ASTMethodDeclarator> getSetMethList = new ArrayList<ASTMethodDeclarator>(methods.size());
	for (MethodNameDeclaration d : methods.keySet()) {
	    ASTMethodDeclarator mnd = d.getMethodNameDeclaratorNode();
	    if (isBeanAccessor(mnd)) {
		getSetMethList.add(mnd);
	    }
	}

	String[] methNameArray = imagesOf(getSetMethList);

	Arrays.sort(methNameArray);

	Map<NameDeclaration, List<NameOccurrence>> vars = node.getScope().getDeclarations();
	for (NameDeclaration decl : vars.keySet()) {
	    if (!(decl instanceof VariableNameDeclaration)) {
	        continue;
	    }
	    AccessNode accessNodeParent = ((VariableNameDeclaration)decl).getAccessNodeParent();
	    if (vars.get(decl).isEmpty() || accessNodeParent.isTransient()
		    || accessNodeParent.isStatic()) {
		continue;
	    }
	    String varName = trimIfPrefix(decl.getImage());
	    varName = varName.substring(0, 1).toUpperCase() + varName.substring(1, varName.length());
	    boolean hasGetMethod = Arrays.binarySearch(methNameArray, "get" + varName) >= 0
		    || Arrays.binarySearch(methNameArray, "is" + varName) >= 0;
	    boolean hasSetMethod = Arrays.binarySearch(methNameArray, "set" + varName) >= 0;
	    // Note that a Setter method is not applicable to a final variable...
	    if (!hasGetMethod || (!accessNodeParent.isFinal() && !hasSetMethod)) {
		addViolation(data, decl.getNode(), decl.getImage());
	    }
	}
	return super.visit(node, data);
    }

    private String trimIfPrefix(String img) {
	if (prefixProperty != null && img.startsWith(prefixProperty)) {
	    return img.substring(prefixProperty.length());
	}
	return img;
    }

    private boolean isBeanAccessor(ASTMethodDeclarator meth) {

	String methodName = meth.getImage();

	if (methodName.startsWith("get") || methodName.startsWith("set")) {
	    return true;
	}
	if (methodName.startsWith("is")) {
	    ASTResultType ret = ((ASTMethodDeclaration) meth.jjtGetParent()).getResultType();
	    List<ASTPrimitiveType> primitives = ret.findDescendantsOfType(ASTPrimitiveType.class);
	    if (!primitives.isEmpty() && primitives.get(0).isBoolean()) {
		return true;
	    }
	}
	return false;
    }
}
