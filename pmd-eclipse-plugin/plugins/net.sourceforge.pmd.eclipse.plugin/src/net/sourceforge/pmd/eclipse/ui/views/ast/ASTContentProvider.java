package net.sourceforge.pmd.eclipse.ui.views.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.Comment;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

/**
 * 
 * @author Brian Remedios
 */
public class ASTContentProvider implements ITreeContentProvider {

	private boolean includeImports;
	private boolean includeComments;
//	private Set<Class<?>> hiddenNodeTypes;
	
	private static final Comparator<Node> ByLineNumber = new Comparator<Node>() {
		public int compare(Node a, Node b) {
			return a.getBeginLine() - b.getBeginLine();
		}
	};
	
	public void includeImports(boolean flag) { includeImports = flag; }
	public void includeComments(boolean flag) { includeComments = flag; }
	
	public ASTContentProvider(boolean includeImportsFlag, boolean includeCommentsFlag) {
		this(Collections.EMPTY_SET);
		
		includeImports = includeImportsFlag;
		includeComments = includeCommentsFlag;
	}
	
	public ASTContentProvider(Set<Class<?>> theHiddenNodeTypes) {
	//	hiddenNodeTypes = theHiddenNodeTypes;
	}

	public void dispose() {	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	private List<Node> withoutHiddenOnes(Object parent) {
				
		List<Node> kids = new ArrayList<Node>();
		
		if (includeComments && parent instanceof ASTCompilationUnit) {
		//	if (!hiddenNodeTypes.contains(Comment.class)) {

				List<Comment> comments = ((ASTCompilationUnit)parent).getComments();
				kids.addAll(comments);	
		//		}
		}
				
		AbstractNode node = (AbstractNode)parent;
		int kidCount = node.jjtGetNumChildren();
		for (int i=0; i<kidCount; i++) {
			Node kid = node.jjtGetChild(i);
//			if (hiddenNodeTypes.contains(kid.getClass())) continue;
			if (!includeImports && kid instanceof ASTImportDeclaration) {
				continue;
			}
			if (!includeComments && kid instanceof Comment) {
				continue;
			}
			kids.add(kid);
		}
		
		Collections.sort(kids, ByLineNumber);
		
		return kids;
	}
	
	public Object[] getElements(Object inputElement) {
		
		AbstractNode parent = (AbstractNode)inputElement;		
		return withoutHiddenOnes(parent).toArray();
	}

	public Object[] getChildren(Object parentElement) {
		
		AbstractNode parent = (AbstractNode)parentElement;		
		return withoutHiddenOnes(parent).toArray();
	}

	public Object getParent(Object element) {
		
		AbstractNode parent = (AbstractNode)element;
		return parent.jjtGetParent();
	}

	public boolean hasChildren(Object element) {
		
		AbstractNode parent = (AbstractNode)element;
		return parent.jjtGetNumChildren() > 0;
	}

	
	public static void setupSorter(TableViewer viewer) {
		
	}
}
