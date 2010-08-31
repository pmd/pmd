package net.sourceforge.pmd.eclipse.ui.views.ast;

import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.ast.Node;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * 
 * @author Brian Remedios
 */
public class ASTContentProvider implements ITreeContentProvider {

	public ASTContentProvider() {
	}

	public void dispose() {	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	public Object[] getElements(Object inputElement) {
		
		AbstractNode parent = (AbstractNode)inputElement;
		
		Node[] kids = new Node[ parent.jjtGetNumChildren()];
		for (int i=0; i<kids.length; i++) {
			kids[i] = parent.jjtGetChild(i);
		}
		
		return kids;
	}

	public Object[] getChildren(Object parentElement) {

		AbstractNode parent = (AbstractNode)parentElement;
		
		Node[] kids = new Node[ parent.jjtGetNumChildren()];
		for (int i=0; i<kids.length; i++) {
			kids[i] = parent.jjtGetChild(i);
		}
		
		return kids;
	}

	public Object getParent(Object element) {
		
		AbstractNode parent = (AbstractNode)element;
		return parent.jjtGetParent();
	}

	public boolean hasChildren(Object element) {
		
		AbstractNode parent = (AbstractNode)element;
		return parent.jjtGetNumChildren() > 0;
	}

}
