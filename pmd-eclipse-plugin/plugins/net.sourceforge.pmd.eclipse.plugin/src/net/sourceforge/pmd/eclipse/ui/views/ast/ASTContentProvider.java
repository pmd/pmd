package net.sourceforge.pmd.eclipse.ui.views.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.AbstractNode;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

/**
 * 
 * @author Brian Remedios
 */
public class ASTContentProvider implements ITreeContentProvider {

	private Set<Class<?>> hiddenNodeTypes;
	
	public ASTContentProvider() {
		this(Collections.EMPTY_SET);
	}
	
	public ASTContentProvider(Set<Class<?>> theHiddenNodeTypes) {
		hiddenNodeTypes = theHiddenNodeTypes;
	}

	public void dispose() {	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	private List<Object> withoutHiddenOnes(AbstractNode parent) {
				
		int kidCount = parent.jjtGetNumChildren();
		List<Object> kids = new ArrayList<Object>(kidCount);
		
		for (int i=0; i<kidCount; i++) {
			Object kid = parent.jjtGetChild(i);
			if (hiddenNodeTypes.contains(kid.getClass())) continue;
			kids.add(kid);
		}
		
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
