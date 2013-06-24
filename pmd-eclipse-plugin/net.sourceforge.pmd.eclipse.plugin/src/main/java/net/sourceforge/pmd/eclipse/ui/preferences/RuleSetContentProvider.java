package net.sourceforge.pmd.eclipse.ui.preferences;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.eclipse.util.Util;

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
        
        if (inputElement instanceof RuleSet) {
            RuleSet ruleSet = (RuleSet) inputElement;
            return ruleSet.getRules().toArray();
        }
        
        return Util.EMPTY_ARRAY;
    }
}
