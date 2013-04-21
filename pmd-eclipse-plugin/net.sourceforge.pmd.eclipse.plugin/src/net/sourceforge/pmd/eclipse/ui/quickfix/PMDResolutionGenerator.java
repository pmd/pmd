/*
 * <copyright>
 *  Copyright 1997-2003 PMD for Eclipse Development team
 *  under sponsorship of the Defense Advanced Research Projects
 *  Agency (DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED "AS IS" WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 *
 * </copyright>
 */
package net.sourceforge.pmd.eclipse.ui.quickfix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.runtime.builder.MarkerUtil;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import net.sourceforge.pmd.util.StringUtil;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;

/**
 * Implementation of a resolution generator to bring the quick fixes feature
 * of Eclipse to PMD
 *
 * @author Philippe Herlin, Brian Remedios
 *
 * TODO 
 * 		resource bundles are read-only, migrate to a persistence mechanism 
 * 		that allows for updates to the fixes associated with the rules.
 */
public class PMDResolutionGenerator implements IMarkerResolutionGenerator {

	private static final Map<String, Fix[]> fixersByRuleName = new HashMap<String, Fix[]>();
	
	private static final Set<String> missingFixes = new HashSet<String>();
	private static final Map<String, String> brokenFixes = new HashMap<String, String>();
	
	private static String QUICKFIX_BUNDLE = "properties.QuickFix"; // NOPMD
	 
	public static final IMarkerResolution[] EMPTY_RESOLUTIONS = new IMarkerResolution[0];
	
	public static Class<Fix> fixClassFor(String className, String ruleName) {
		
		if (StringUtil.isEmpty(className)) return null;
		
		try {
			Class<?> cls = Class.forName(className);
			if (Fix.class.isAssignableFrom(cls)) {
				return (Class<Fix>)cls;
				} else {
					brokenFixes.put(ruleName, className);
					return null;
				}
			} catch (ClassNotFoundException ex) {
				return null;
			}
	}
	
	private static void add(String ruleName, Fix fix) {
		
		if (fixersByRuleName.containsKey(ruleName)) {
			Fix[] existingFixers = fixersByRuleName.get(ruleName);
			Fix[] newFixers = new Fix[existingFixers.length+1];
			System.arraycopy(existingFixers, 0, newFixers, 0, existingFixers.length);
			newFixers[newFixers.length-1] = fix;
			fixersByRuleName.put(ruleName, newFixers);
		} else {
			fixersByRuleName.put(ruleName, new Fix[] { fix });
		}
	}
	
	public static int fixCountFor(Rule rule) {
		
		String ruleName = rule.getName();
		if (missingFixes.contains(ruleName)) return 0;
		
		loadFixesFor(ruleName);
		
		if (!fixersByRuleName.containsKey(ruleName)) return 0;
		return fixersByRuleName.get(ruleName).length;	
	}
	
	public static void saveFixesFor(String ruleName) {
		// TODO
	}
	
	private static void loadFixesFor(String ruleName) {
		
		ResourceBundle bundle = ResourceBundle.getBundle(QUICKFIX_BUNDLE);
		if (!bundle.containsKey(ruleName)) {
			missingFixes.add(ruleName);
			return;
		}
		
		String fixClassNameSet = bundle.getString(ruleName);
		String[] fixClassNames = fixClassNameSet.split(",");
		
		for (String fixClassName : fixClassNames) {
			if (StringUtil.isEmpty(fixClassName)) continue;
			Class<Fix> fixClass = fixClassFor(fixClassName.trim(), ruleName);
			if (fixClass != null) {
				Fix fix = fixFor(ruleName, fixClass);
				if (fix != null) {
					add(ruleName, fix);
					}
				}
		}
		
		if (!fixersByRuleName.containsKey(ruleName)) missingFixes.add(ruleName);
	}
	
	public static boolean hasFixesFor(Rule rule) {
		
		String ruleName = rule.getName();
		if (fixersByRuleName.containsKey(ruleName)) return true;
		
		if (missingFixes.contains(ruleName)) return false;
		if (brokenFixes.containsKey(ruleName)) return false;
		
		loadFixesFor(ruleName);
		
		return fixersByRuleName.containsKey(ruleName);
	}
	
	private static Fix fixFor(String ruleName, Class<Fix> fixClass) {
		
		try {
			return fixClass.newInstance();
			} catch (Exception ex) {
				brokenFixes.put(ruleName, fixClass.getName());
				return null;
			}
	}
	
	public static Fix[] fixesFor(Rule rule) {		
		return fixersByRuleName.get(rule.getName());
	}
		
	public static void fixesFor(Rule rule, Fix[] fixes) {		
		fixersByRuleName.put(rule.getName(), fixes);
	}
	
    /**
     * @see org.eclipse.ui.IMarkerResolutionGenerator#getResolutions(org.eclipse.core.resources.IMarker)
     */
    public IMarkerResolution[] getResolutions(IMarker marker) {
		
        final List<PMDResolution> markerResolutionList = new ArrayList<PMDResolution>();
        try {
            final String ruleName = MarkerUtil.ruleNameFor(marker);
            if (ruleName != null) {
                final RuleSet ruleSet = PMDPlugin.getDefault().getPreferencesManager().getRuleSet();
                final Rule rule = ruleSet.getRuleByName(ruleName);
                if (rule == null || !hasFixesFor(rule)) return EMPTY_RESOLUTIONS;
                
                Fix[] fixes = fixesFor(rule);
                for (Fix fix : fixes)  markerResolutionList.add( new PMDResolution(fix) );
            }
        } catch (RuntimeException e) {
            PMDPlugin.getDefault().showError(PMDPlugin.getDefault().getStringTable().getString(StringKeys.ERROR_RUNTIME_EXCEPTION), e);
        }

        return markerResolutionList.toArray(new IMarkerResolution[markerResolutionList.size()]);
    }

}
