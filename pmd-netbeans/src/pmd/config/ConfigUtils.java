/*
 * ConfigUtils.java
 *
 * Created on 25. november 2002, 23:35
 */

package pmd.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.sourceforge.pmd.Rule;
import pmd.config.ui.AvailableListModel;

/**
 *
 * @author  ole martin mørk
 */
public class ConfigUtils {
	
	
	public static List createRuleList( String rules ) {
		Iterator iterator = AvailableListModel.getInstance().getData().iterator();
		List list = new ArrayList();
		while( iterator.hasNext() ) {
			Rule rule = ( Rule )iterator.next();
			if( rules.indexOf( rule.getName() + ", " ) > -1 ) {
				list.add( rule );
			}
		}
		return list;
	}
	
	/** Returns the list as text
	 * @param value The list to be presented as text
	 * @return A string containing all the values in the list
	 */
	public static String getValueAsText( List value ) {
		StringBuffer buffer = new StringBuffer();
		if( value != null ) {
			Iterator iterator = value.iterator();
			while( iterator.hasNext() ) {
				Rule rule = ( Rule )iterator.next();
				buffer.append( rule.getName() ).append( ", " );
			}
		}
		return String.valueOf( buffer );
	}
}
