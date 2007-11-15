/*
 *  Copyright (c) 2002-2006, the pmd-netbeans team
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
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import pmd.NbRuleSetFactory;
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
    private static ArrayList<RuleSetFactory> extraFactories;
	
    static {
        extraFactories = new ArrayList<RuleSetFactory>();
        extraFactories.add(NbRuleSetFactory.getDefault ());
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
     * @return a list containing the rules to use.
     */
    public static List<Rule> createRuleList( String rules, Map<String, Map<String, String>> propOverrides ) {
        List<Rule> list = new ArrayList<Rule>();
        for (Rule rule: getAllAvailableRules()) {
            if( rules.contains( rule.getName() + ", " )) {
                // add it, but first check for property overrides.
                Map<String, String> rulePropOverrides = propOverrides.get( rule.getName() );
                if(rulePropOverrides != null) {
                    for (Map.Entry<String, String> entry: rulePropOverrides.entrySet()) {
                        rule.addProperty( entry.getKey(), entry.getValue() );
                    }
                }
                list.add( rule );
            }
        }
        Collections.sort( list, new RuleComparator() );
        return list;
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
	public static List<Rule> getAllAvailableRules() {
		List<Rule> list = new ArrayList<Rule>();
		CustomRuleSetSettings settings = PMDOptionsSettings.getDefault().getRulesets();
		RuleSetFactory ruleSetFactory = new RuleSetFactory();
		try {
			if( settings.isIncludeStdRules() ) {
				try {
					Iterator<RuleSet> iterator = ruleSetFactory.getRegisteredRuleSets();
					while( iterator.hasNext() ) {
						RuleSet ruleset = iterator.next();
						list.addAll( ruleset.getRules() );
					}
					synchronized(ConfigUtils.class) {
						if (extraFactories != null) {
                            for (RuleSetFactory ruleSetFact: extraFactories) {
								Iterator<RuleSet> iter = ruleSetFact.getRegisteredRuleSets();
								while( iter.hasNext() ) {
									list.addAll( iter.next().getRules() );
								}
							}
						}
					}
				}
				catch( RuleSetNotFoundException e ) {
					ErrorManager.getDefault().notify(e);
				}
			}
                        Iterator<String> rulesets = settings.getRuleSets().iterator();
                        while( rulesets.hasNext() ) {
                            String ruleSetXml = rulesets.next();
                            try {
                                Method m = RuleSetFactory.class.getDeclaredMethod("createRuleSet", InputStream.class, ClassLoader.class);
                                m.setAccessible(true);
                                Object o = m.invoke(ruleSetFactory, new FileInputStream( ruleSetXml ),
                                        new RuleClassLoader(ConfigUtils.class.getClassLoader()));
                                RuleSet ruleset = (RuleSet)o;
                                    /*
                                        RuleSet ruleset = ruleSetFactory.createRuleSet(
                                        new FileInputStream( ruleSetXml ),
                                        new RuleClassLoader( ConfigUtils.class.getClassLoader() ) );
                                     */
                                list.addAll( ruleset.getRules() );
                            } catch( RuntimeException e ) {
                                ErrorManager.getDefault().notify(e);
                            } catch (NoSuchMethodException e) {
                                ErrorManager.getDefault().notify(e);
                            } catch (IllegalAccessException e) {
                                ErrorManager.getDefault().notify(e);
                            } catch (InvocationTargetException e) {
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
