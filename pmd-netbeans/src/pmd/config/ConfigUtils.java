/*
 *  ConfigUtils.java
 *
 *  Created on 25. november 2002, 23:35
 */
package pmd.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;

/**
 * @author ole martin mørk
 * @created 26. november 2002
 */
public class ConfigUtils {

	/**
	 * Description of the Method
	 *
	 * @param rules Description of the Parameter
	 * @return Description of the Return Value
	 */
	public static List createRuleList( String rules ) {
		Iterator iterator = getAllAvailableRules().iterator();
		List list = new ArrayList();
		while( iterator.hasNext() ) {
			Rule rule = ( Rule )iterator.next();
			if( rules.indexOf( rule.getName() + ", " ) > -1 ) {
				list.add( rule );
			}
		}
		return list;
	}


	/**
	 * Returns the list as text
	 *
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


	/**
	 * Gets the allAvailableRules attribute of the ConfigUtils class
	 *
	 * @return The allAvailableRules value
	 */
	public static List getAllAvailableRules() {
		List list = new ArrayList();
		try {
			RuleSetFactory ruleSetFactory = new RuleSetFactory();
			Iterator iterator = ruleSetFactory.getRegisteredRuleSets();
			while( iterator.hasNext() ) {
				RuleSet ruleset = ( RuleSet )iterator.next();
				list.addAll( ruleset.getRules() );
			}
		}
		catch( RuleSetNotFoundException e ) {
			e.printStackTrace();
		}
		return list;
	}
}
