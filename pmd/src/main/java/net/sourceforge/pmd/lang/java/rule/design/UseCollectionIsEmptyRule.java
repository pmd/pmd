/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.design;

import net.sourceforge.pmd.lang.java.rule.AbstractInefficientZeroCheck;
import net.sourceforge.pmd.lang.java.symboltable.NameOccurrence;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Detect structures like "foo.size() == 0" and suggest replacing them with
 * foo.isEmpty(). Will also find != 0 (replaceable with !isEmpty()).
 * 
 * @author Jason Bennett
 */
public class UseCollectionIsEmptyRule extends AbstractInefficientZeroCheck {
    
    public boolean appliesToClassName(String name){
        return CollectionUtil.isCollectionType(name, true);
    }
    
    /**
     * Determine if we're dealing with .size method
     * 
     * @param occ
     *            The name occurrence
     * @return true if it's .length, else false
     */
    public boolean isTargetMethod(NameOccurrence occ) {
        if (occ.getNameForWhichThisIsAQualifier() != null) {
            if (occ.getLocation().getImage().endsWith(".size")) {
                return true;
            }
        }
        return false;
    }
}
