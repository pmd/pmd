package net.sourceforge.pmd.eclipse.ui.views.ast;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.Comment;

/**
 * For nodes higher in the tree that don't have any identifying information
 * we can walk their children and derive some in Java-like form. The idea is
 * to help keep the number of child nodes that need to be visible to a minimum.
 * 
 * @author Brian Remedios
 */
public class NodeImageDeriver {
	
	public final Class<?> target;
	
	public NodeImageDeriver(Class<?> theASTClass) {
		target = theASTClass;
	}
	
	public String deriveFrom(Node node) {
		return null;	// failed to implement!
	}

	private static void dumpComments(ASTCompilationUnit node) {
		
		for (Comment comment : node.getComments()) {
			System.out.println(comment.getClass().getName());
			System.out.println(comment.getImage());
		}
	}
	
	private static NodeImageDeriver compilationUnitDeriver = new NodeImageDeriver(ASTCompilationUnit.class) {
		public String deriveFrom(Node node) {
			dumpComments((ASTCompilationUnit)node);
			return "Comments: " + ((ASTCompilationUnit)node).getComments().size();
		}
	};
	
	private static NodeImageDeriver importDeriver = new NodeImageDeriver(ASTImportDeclaration.class) {
		public String deriveFrom(Node node) {
			// TODO show package name as well?
			return ((ASTImportDeclaration)node).getImportedName();
		}
	};
	
	private static NodeImageDeriver methodDeclarationDeriver = new NodeImageDeriver(ASTMethodDeclaration.class) {
		public String deriveFrom(Node node) {
			return ASTUtil.getMethodLabel((ASTMethodDeclaration)node, true);
		}
	};
	
	private static NodeImageDeriver throwStatementDeriver = new NodeImageDeriver(ASTThrowStatement.class) {
		public String deriveFrom(Node node) {
			return ((ASTThrowStatement)node).getFirstClassOrInterfaceTypeImage();
		}
	};
	
	private static NodeImageDeriver fieldDeclarationDeriver = new NodeImageDeriver(ASTFieldDeclaration.class) {
		public String deriveFrom(Node node) {
			return ASTUtil.getFieldLabel((ASTFieldDeclaration)node);
		}
	};
	
	private static NodeImageDeriver localVariableDeclarationDeriver = new NodeImageDeriver(ASTLocalVariableDeclaration.class) {
		public String deriveFrom(Node node) {
			return ASTUtil.getLocalVarDeclarationLabel((ASTLocalVariableDeclaration)node);
		}
	};
	
	private static NodeImageDeriver annotationDeriver = new NodeImageDeriver(ASTAnnotation.class) {
		public String deriveFrom(Node node) {
			return ASTUtil.getAnnotationLabel((ASTAnnotation)node);
		}
	};
	
	private static final NodeImageDeriver[] AllDerivers = new NodeImageDeriver[] {
		importDeriver, methodDeclarationDeriver, localVariableDeclarationDeriver, fieldDeclarationDeriver, annotationDeriver,
		compilationUnitDeriver, throwStatementDeriver
		};
	
	private static final Map<Class<?>, NodeImageDeriver>DeriversByType = new HashMap<Class<?>, NodeImageDeriver>(NodeImageDeriver.AllDerivers.length);
	
	static {
		for (NodeImageDeriver deriver : NodeImageDeriver.AllDerivers) {
			DeriversByType.put(deriver.target, deriver);
		}
	}
	
	public static String derivedTextFor(Node node) {
		
		NodeImageDeriver deriver = DeriversByType.get(node.getClass());
		return deriver == null ? null : deriver.deriveFrom(node);
	}
}
