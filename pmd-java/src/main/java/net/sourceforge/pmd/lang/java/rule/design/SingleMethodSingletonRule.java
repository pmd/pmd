package net.sourceforge.pmd.lang.java.rule.design;


import java.util.HashSet;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class SingleMethodSingletonRule extends AbstractJavaRule {

	private static Map<String, ASTFieldDeclaration> fieldDecls = new HashMap<String, ASTFieldDeclaration>();
	private static Set<ASTFieldDeclaration> returnset = new HashSet<ASTFieldDeclaration>();
	boolean violation=false;
	private static Set<String> methodset = new HashSet<String>();
	@Override
	public Object visit(ASTFieldDeclaration node, Object data) {
		
		if (node.isStatic() && node.isPrivate()) {
				ASTVariableDeclaratorId varDeclaratorId=node.getFirstDescendantOfType(ASTVariableDeclaratorId.class);
				if(varDeclaratorId!=null){
					String varName=varDeclaratorId.getImage();
					fieldDecls.put(varName, node);
				}
			}
		
		return super.visit(node, data);
	}

	@Override
	public Object visit(ASTCompilationUnit node, Object data){
		violation=false;
		fieldDecls.clear();
		returnset.clear();
		methodset.clear();
		return super.visit(node,data);
	}
	@Override
	public Object visit(ASTMethodDeclaration node, Object data) {

		violation=false;
		if (node.getResultType().isVoid()) {
			return super.visit(node, data);
		}

		if ("getInstance".equals(node.getMethodName())) {

			if(!methodset.add(node.getMethodName())){
				violation=true;
			}
		}
		
		if(violation){
			addViolation(data, node);
		}
		return super.visit(node, data);
	}

	private String getNameFromPrimaryPrefix(ASTPrimaryPrefix pp) {
		if ((pp.jjtGetNumChildren() == 1)
				&& (pp.jjtGetChild(0) instanceof ASTName)) {
			return ((ASTName) pp.jjtGetChild(0)).getImage();
		}
		return null;
	}
}
