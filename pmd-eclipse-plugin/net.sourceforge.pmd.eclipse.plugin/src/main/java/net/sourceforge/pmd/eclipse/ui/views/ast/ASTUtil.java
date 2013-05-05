package net.sourceforge.pmd.eclipse.ui.views.ast;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType;
import net.sourceforge.pmd.lang.java.ast.ASTResultType;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaAccessNode;

/**
 * 
 * @author Brian Remedios
 */
public class ASTUtil {

	private ASTUtil() {}
	
	public static final Comparator<ASTMethodDeclaration> MethodComparator = new Comparator<ASTMethodDeclaration>() {
		public int compare(ASTMethodDeclaration m1, ASTMethodDeclaration m2) {
			return m1.getMethodName().compareTo(m2.getMethodName());
		}
	};
	
	public static String getAnnotationLabel(ASTAnnotation annotation) {
		
		AbstractNode name = annotation.getFirstChildOfType(ASTName.class);
		return name == null ? "??" : name.getImage();
	}
	
	public static String getMethodLabel(ASTMethodDeclaration pmdMethod, boolean includeModifiers) {
		
		String returnType = returnType(pmdMethod);
		
		StringBuilder sb = new StringBuilder();
		
		if (includeModifiers) {
			addModifiers(pmdMethod, sb);
			sb.append(' ');
		}
		
		sb.append(pmdMethod.getMethodName());
		sb.append('(').append(parameterTypes(pmdMethod)).append(')');
		if (returnType == null) return sb.toString();
		
		sb.append(" : ").append(returnType);
		return sb.toString();
	}
	
	private static List<String> modifiersFor(AbstractJavaAccessNode node) {
		
		List<String> modifiers = new ArrayList<String>();
		if (node.isPublic()) {
			modifiers.add("public");
			} else {
				if (node.isProtected()) {
					modifiers.add("protected");
				} else {
					if (node.isPrivate()) {
						modifiers.add("private");
					}
				}
			}				
		
		if (node.isAbstract()) 	modifiers.add("abstract");
		if (node.isStatic()) 	modifiers.add("static");
		if (node.isFinal()) 	modifiers.add("final");
		if (node.isTransient()) modifiers.add("transient");
		if (node.isVolatile()) 	modifiers.add("volatile");
		if (node.isSynchronized()) modifiers.add("synchronized");
		if (node.isNative()) 	modifiers.add("native");
		if (node.isStrictfp()) 	modifiers.add("strictfp");
		return modifiers;
	}
	
	private static void addModifiers(AbstractJavaAccessNode node, StringBuilder sb) {
		
		List<String> modifiers = modifiersFor(node);
		if (modifiers.isEmpty()) return;
		
		sb.append(modifiers.get(0));
		for (int i=1; i<modifiers.size(); i++) {
			sb.append(' ').append(modifiers.get(i));
		}
	}
	
	public static String getFieldLabel(ASTFieldDeclaration pmdField) {
			
		StringBuilder sb = new StringBuilder();
		addModifiers(pmdField, sb);
		
		ASTType type = pmdField.getFirstChildOfType(ASTType.class);
		if (type != null) sb.append(' ').append(type.getTypeImage());
		
		sb.append(' ').append(pmdField.getVariableName());
				
		return sb.toString();
	}
	
	public static String parameterTypes(ASTMethodDeclaration node) {
		
		StringBuilder sb = new StringBuilder();
		
		for (int ix = 0; ix < node.jjtGetNumChildren(); ix++) {
		    Node sn = node.jjtGetChild(ix);
	    	if (sn instanceof ASTMethodDeclarator) {
	    		List<ASTFormalParameter> allParams = ((ASTMethodDeclarator) sn).findDescendantsOfType(ASTFormalParameter.class);
	    		for (ASTFormalParameter formalParam : allParams) {
	    		    AbstractNode param = formalParam.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
	    		    if (param == null) {
	    		    	param = formalParam.getFirstDescendantOfType(ASTPrimitiveType.class);
	    		    	}
	    		    if (param == null) continue;
	    	    	sb.append( param.getImage() ).append(", ");
	    		}	
	    	}
		}
		
		int length = sb.length();
		return length == 0 ? "" : sb.toString().substring(0, length-2);
	}

	public static String returnType(ASTMethodDeclaration node) {
				
		for (int ix = 0; ix < node.jjtGetNumChildren(); ix++) {
		    Node sn = node.jjtGetChild(ix);
	    	if (sn instanceof ASTResultType) {
	    		ASTResultType resultType = (ASTResultType)sn;
	    		    AbstractNode param = resultType.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
	    		    if (param == null) {
	    		    	param = resultType.getFirstDescendantOfType(ASTPrimitiveType.class);
	    		    	}
	    		    if (param == null) continue;
	    	    	return param.getImage();
	    		}	
	    	}
		return null;
	}

	public static String getLocalVarDeclarationLabel(ASTLocalVariableDeclaration node) {

		StringBuilder sb = new StringBuilder();
		addModifiers(node, sb);
		
		ASTType type = node.getTypeNode();
		sb.append(' ').append(type.getTypeImage());
		
		for (int i=0; i<node.getArrayDepth(); i++) sb.append("[]");
		
		sb.append(' ').append(node.getVariableName());
		
		return sb.toString();
	}
}
