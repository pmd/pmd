package net.sourceforge.pmd.eclipse.ui.preferences;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.eclipse.util.Util;

/**
 * This class implements a content provider for the rule properties table of
 * the PMD Preference page
 *
 * @author Philippe Herlin
 * @deprecated
 */
public class RulePropertiesContentProvider extends AbstractStructuredContentProvider {

    /**
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(Object)
     */
    public Object[] getElements(Object inputElement) {
      
//        if (inputElement instanceof Rule) {
//            Rule rule = (Rule) inputElement;
//            Enumeration<String> keys = rule.getProperties().keys();
//            List<RuleProperty> propertyList = new ArrayList<RuleProperty>();
//            while (keys.hasMoreElements()) {
//                propertyList.add(new RuleProperty(rule, keys.nextElement()));
//            }
//            return propertyList.toArray();
//        }

        return Util.EMPTY_ARRAY;
    }
}
