/*
 *  Copyright (c) 2002-2003, Ole-Martin Mørk
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *  LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 *  OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *  DAMAGE.
 */
package pmd.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import pmd.config.ui.RuleComparator;

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
		Collections.sort( list, new RuleComparator() );
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
		Collections.sort( list, new RuleComparator() );
		return list;
	}
}
