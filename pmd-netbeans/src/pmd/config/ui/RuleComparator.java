/*
 * RuleComparator.java
 *
 * Created on 11. februar 2003, 18:47
 */

package pmd.config.ui;

import java.util.Comparator;
import net.sourceforge.pmd.Rule;

/**
 *
 * @author  ole martin mørk
 */
public class RuleComparator implements Comparator{
	
	public int compare(Object rule1, Object rule2) {
		int compared = 0;
		if( rule1 instanceof Rule && rule2 instanceof Rule ) {
			compared =  ( (Rule)rule1).getName().compareTo( ( (Rule)rule2).getName() );
		}
		return compared;
	}	
}