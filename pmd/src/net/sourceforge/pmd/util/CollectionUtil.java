package net.sourceforge.pmd.util;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Brian Remedios
 */
public class CollectionUtil {

	private CollectionUtil() {};
	
    /**
     * Method asSet.
     * @param items Object[]
     * @return Set
     */
    public static Set asSet(Object[] items) {
    	
    	Set set = new HashSet(items.length);
    	for (int i=0; i<items.length; i++) {
    		set.add(items[i]);
    	}
    	return set;
    }	
}
