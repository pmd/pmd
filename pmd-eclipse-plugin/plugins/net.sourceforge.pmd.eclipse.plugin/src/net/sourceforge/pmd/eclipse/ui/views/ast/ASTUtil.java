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
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType;
import net.sourceforge.pmd.lang.java.ast.ASTResultType;

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
	
	public static String getMethodLabel(ASTMethodDeclaration pmdMethod) {
		
		String returnType = returnType(pmdMethod);
		
		StringBuilder sb = new StringBuilder(pmdMethod.getMethodName());
		sb.append('(');
		sb.append(parameterTypes(pmdMethod));
		sb.append(')');
		if (returnType == null) return sb.toString();
		
		sb.append(" : ").append(returnType);
		return sb.toString();
	}
	
	public static String getFieldLabel(ASTFieldDeclaration pmdField) {
			
		List<String> modifiers = new ArrayList<String>();
		if (pmdField.isPublic()) {
			modifiers.add("public");
			} else {
				if (pmdField.isProtected()) {
					modifiers.add("protected");
				} else {
					if (pmdField.isPrivate()) {
						modifiers.add("private");
					}
				}
			}				
		
		if (pmdField.isAbstract()) 	modifiers.add("abstract");
		if (pmdField.isStatic()) 	modifiers.add("static");
		if (pmdField.isFinal()) 	modifiers.add("final");
		if (pmdField.isTransient()) modifiers.add("transient");
		if (pmdField.isVolatile()) 	modifiers.add("volatile");
		if (pmdField.isSynchronized()) modifiers.add("synchronized");
		if (pmdField.isNative()) 	modifiers.add("native");
		if (pmdField.isStrictfp()) 	modifiers.add("strictfp");
		
		StringBuilder sb = new StringBuilder(modifiers.get(0));
		for (int i=1; i<modifiers.size(); i++) {
			sb.append(' ').append(modifiers.get(i));
		}
		// TODO add variable type
		// TODO add variableName
		
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
}
