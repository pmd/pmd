package net.sourceforge.pmd.eclipse.ui.preferences;

import net.sourceforge.pmd.RuleSet;

/**
 * This class implements a content provider for the rule table of
 * the PMD Preference page
 * 
 * @author Philippe Herlin
 *
 */
public class RuleSetContentProvider extends AbstractStructuredContentProvider {

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
}
