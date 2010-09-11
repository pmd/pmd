package net.sourceforge.pmd.eclipse.ui.views.ast;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;

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
	
	public String deriveFrom(AbstractNode node) {
		return null;	// failed to implement!
	}
	
	private static NodeImageDeriver importDeriver = new NodeImageDeriver(ASTImportDeclaration.class) {
		public String deriveFrom(AbstractNode node) {
			AbstractNode nameNode = node.getFirstChildOfType(ASTName.class);
			return nameNode == null ? "??" : nameNode.getImage();
		}
	};
	
	private static NodeImageDeriver methodDeclarationDeriver = new NodeImageDeriver(ASTMethodDeclaration.class) {
		public String deriveFrom(AbstractNode node) {
			return ASTUtil.getMethodLabel((ASTMethodDeclaration)node);
		}
	};
	
	private static NodeImageDeriver fieldDeclarationDeriver = new NodeImageDeriver(ASTFieldDeclaration.class) {
		public String deriveFrom(AbstractNode node) {
			return ASTUtil.getFieldLabel((ASTFieldDeclaration)node);
		}
	};
	
	private static NodeImageDeriver annotationDeriver = new NodeImageDeriver(ASTAnnotation.class) {
		public String deriveFrom(AbstractNode node) {
			return ASTUtil.getAnnotationLabel((ASTAnnotation)node);
		}
	};
	
	private static final NodeImageDeriver[] AllDerivers = new NodeImageDeriver[] {
		importDeriver, methodDeclarationDeriver, fieldDeclarationDeriver, annotationDeriver
		};
	
	private static final Map<Class<?>, NodeImageDeriver>DeriversByType = new HashMap<Class<?>, NodeImageDeriver>(NodeImageDeriver.AllDerivers.length);
	
	static {
		for (NodeImageDeriver deriver : NodeImageDeriver.AllDerivers) {
			DeriversByType.put(deriver.target, deriver);
		}
	}
	
	public static String derivedTextFor(AbstractNode node) {
		
		NodeImageDeriver deriver = DeriversByType.get(node.getClass());
		return deriver == null ? null : deriver.deriveFrom(node);
	}
}
