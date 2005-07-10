/*
 *  Copyright (c) 2002-2003, the pmd-netbeans team
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import org.openide.ErrorManager;
import pmd.config.ui.RuleComparator;
import pmd.custom.RuleClassLoader;

/**
 * Configuration utilities for PMD module.
 */
public abstract class ConfigUtils {
	
	/**
	 * Extra ruleset factories added by calling {@link #addRuleSetFactory}.
	 * May be null (equivalent to empty).
	 */
	private static ArrayList extraFactories = null;
	
	/**
	 * Registers extra rules that are available.
	 *
	 * @param rules Collection of Rule objects.
	 */
	public static synchronized void addRuleSetFactory(RuleSetFactory fact) {
		if (extraFactories == null) {
			extraFactories = new ArrayList();
		}
		extraFactories.add(fact);
	}
	
	/**
	 * Unregisters extra rules previously registered.
	 *
	 * @param rules Collection of Rule objects.
	 */
	public static synchronized void removeRuleSetFactory(RuleSetFactory fact) {
		if (extraFactories != null) {
			extraFactories.remove(fact);
		}
	}
	
	/**
	 * Determines the list of rules to use. This is done by iterating over all
	 * known rules and, for each one, checking whether its name appears in the
	 * given string followed by a comma and a space. If it does appear, then it
	 * is added to the list of rules to use, after setting any properties whose
	 * defaults have been configured to be overridden, in
	 * {@link PMDOptionsSettings}.{@link PMDOptionsSettings#getDefault getDefault()}.{@link PMDOptionsSettings#getRuleProperties getRuleProperties()}.
	 *
	 * @param rules a string containing the names of the rules to use, with a comma-and-space after each one (including the last).
	 * @return a list containing the rules to use. Each element of the list is an instance of {@link Rule}.
	 */
	public static List createRuleList( String rules ) {
		Iterator iterator = getAllAvailableRules().iterator();
		Map propOverrides = PMDOptionsSettings.getDefault().getRuleProperties();
		List list = new ArrayList();
		while( iterator.hasNext() ) {
			Rule rule = ( Rule )iterator.next();
			if( rules.indexOf( rule.getName() + ", " ) > -1 ) {
				// add it, but first check for property overrides.
				Map rulePropOverrides = (Map)propOverrides.get( rule.getName() );
				if(rulePropOverrides != null) {
					Iterator iter = rulePropOverrides.entrySet().iterator();
					while( iter.hasNext() ) {
						Map.Entry entry = (Map.Entry)iter.next();
						rule.addProperty( (String)entry.getKey(), (String)entry.getValue() );
					}
				}
				list.add( rule );
			}
		}
		Collections.sort( list, new RuleComparator() );
		return list;
	}
	
	
	/**
	 * Determines the list of rules to use.
	 * This just delegates to {@link #createRuleList} with the argument
	 * {@link PMDOptionsSettings}.{@link PMDOptionsSettings#getDefault getDefault()}.{@link PMDOptionsSettings#getRules getRules()}.
	 *
	 * @return a list containing the rules to use. Each element of the list is an instance of {@link Rule}.
	 */
	public static List getRuleList() {
		return createRuleList( PMDOptionsSettings.getDefault().getRules() );
	}
	
	
	/**
	 * Returns a particular string representation of the given list of PMD rules.
	 * The representation consists of the names of the rules, each one
	 * (including the last) followed by a comma and a space.
	 *
	 * @param ruleList The list of rules to be presented as text
	 * @return A string containing all the values in the list
	 */
	public static String getValueAsText(List ruleList) {
		StringBuffer buffer = new StringBuffer();
		if( ruleList != null ) {
			Iterator iterator = ruleList.iterator();
			while( iterator.hasNext() ) {
				Rule rule = ( Rule )iterator.next();
				buffer.append( rule.getName() ).append( ", " );
			}
		}
		return String.valueOf( buffer );
	}
	
	
	/**
	 * Gets a list of all PMD rules known to the environment.
	 * This includes those shipped as part of PMD, and those configured in custom rulesets.
	 *
	 * @return a list of all known PMD rules, not null. Each element is an instance of {@link Rule}.
	 */
	public static List getAllAvailableRules() {
		List list = new ArrayList();
		CustomRuleSetSettings settings = PMDOptionsSettings.getDefault().getRulesets();
		RuleSetFactory ruleSetFactory = new RuleSetFactory();
		try {
			if( settings.isIncludeStdRules() ) {
				try {
					Iterator iterator = ruleSetFactory.getRegisteredRuleSets();
					while( iterator.hasNext() ) {
						RuleSet ruleset = ( RuleSet )iterator.next();
						list.addAll( ruleset.getRules() );
					}
					synchronized(ConfigUtils.class) {
						if (extraFactories != null) {
							iterator = extraFactories.iterator();
							while (iterator.hasNext() ) {
								ruleSetFactory = (RuleSetFactory)iterator.next();
								Iterator iter = ruleSetFactory.getRegisteredRuleSets();
								while( iter.hasNext() ) {
									RuleSet ruleset = ( RuleSet )iter.next();
									list.addAll( ruleset.getRules() );
								}
							}
						}
					}
				}
				catch( RuleSetNotFoundException e ) {
					ErrorManager.getDefault().notify(e);
				}
			}
			Iterator rulesets = settings.getRuleSets().iterator();
			while( rulesets.hasNext() ) {
				String ruleSetXml = (String)rulesets.next();
				try {
					RuleSet ruleset = ruleSetFactory.createRuleSet(
					new FileInputStream( ruleSetXml ),
                                                // PENDING: perhaps can get ClassLoader from Lookup 
					new RuleClassLoader( ConfigUtils.class.getClassLoader() ) ); 
					list.addAll( ruleset.getRules() );
				}
				catch( RuntimeException e ) {
					ErrorManager.getDefault().notify(e);
				}
			}
		}
		catch( FileNotFoundException e ) {
			throw new RuntimeException( e.getMessage() );
		}
		Collections.sort( list, new RuleComparator() );
		return list;
	}
}
