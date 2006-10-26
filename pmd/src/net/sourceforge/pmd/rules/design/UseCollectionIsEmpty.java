/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.rules.AbstractInefficientZeroCheck;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Detect structures like "foo.size() == 0" and suggest replacing them with
 * foo.isEmpty(). Will also find != 0 (replacable with !isEmpty()).
 * 
 * @author Jason Bennett
 */
public class UseCollectionIsEmpty extends AbstractInefficientZeroCheck {
    
    public boolean appliesToClassName(String name){
        return CollectionUtil.isCollectionType(name, true);
    }
    
    /**
     * Determine if we're dealing with .size method
     * 
     * @param occ
     *            The name occurance
     * @return true if it's .length, else false
     */
    public boolean isTargetMethod(NameOccurrence occ) {
        if (occ.getNameForWhichThisIsAQualifier() != null) {
            if (((SimpleNode) occ.getLocation()).getImage().endsWith(".size")) {
                return true;
            }
        }
        return false;
    }
}
