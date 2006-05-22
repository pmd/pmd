package net.sourceforge.pmd.ui.preferences;

import net.sourceforge.pmd.RuleSet;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * This class implements a content provider for the rule table of
 * the PMD Preference page
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2006/05/22 21:23:38  phherlin
 * Refactor the plug-in architecture to better support future evolutions
 *
 * Revision 1.1  2003/06/30 20:16:06  phherlin
 * Redesigning plugin configuration
 *
 *
 */
public class RuleSetContentProvider implements IStructuredContentProvider {

    /**
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(Object)
     */
    public Object[] getElements(Object inputElement) {
        Object[] elements = new Object[0];
        
        if (inputElement instanceof RuleSet) {
            RuleSet ruleSet = (RuleSet) inputElement;
            elements = ruleSet.getRules().toArray();
        }
        
        return elements;
    }

    /**
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose() {
    }

    /**
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(Viewer, Object, Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

}
