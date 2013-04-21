package net.sourceforge.pmd.eclipse.ui.views.ast;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

/**
 * 
 * @author Brian Remedios
 */
public class ASTLabelProvider implements ILabelProvider {

	public void addListener(ILabelProviderListener listener) {	}

	public void dispose() {	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) { }

	public Image getImage(Object element) {
		return null;
	}

	public String getText(Object element) {
		return null;
//		AbstractNode node = (AbstractNode)element;
//		String extra = node.getImage();
//		
//		return extra == null ? 
//			node.toString() :
//			node.toString() + ": " + extra;
	}

}
