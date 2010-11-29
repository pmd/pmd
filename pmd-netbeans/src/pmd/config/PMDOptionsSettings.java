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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetFactory;
import org.openide.util.NbPreferences;

/**
 * Settings for the PMD-NetBeans module.
 */
public class PMDOptionsSettings {

	/** The constant for the rules property. The String value of this property is a comma-separated list of
	 * names of currently enabled rules. The names refer to the rule definitions in all rulesets returned by
	 * {@link RuleSetFactory#getRegisteredRuleSets}.
	 */
	public final static String PROP_RULES = "rules";

	/** The constant for the rule properties property. The value of this property is
	 * a <code>Map</code>, whose keys are Strings (rule names) and whose values are instances of
	 * <code>Map</code> containing rule properties (keys and values are <code>String</code>s). These rule
	 * properties override the rules configured for a given rule in its ruleset definition. This is to
	 * enable the NetBeans user to set rule properties within NetBeans, since the ruleset definitions
	 * themselves are locked inside a jar somewhere.
	 * <p>
	 * This property does not show up in the standard beans property editor in NetBeans Options dialog;
	 * rather, it is set by the custom property editor for {@link #PROP_RULES}.
	 */
	public final static String PROP_RULE_PROPERTIES = "ruleproperties";

	/** The constant for the rulesetz property. The value of this property is an instance of
	 * {@link CustomRuleSetSettings}, representing the custom ruleset settings currently in effect.
	 */
	public final static String PROP_RULESETS = "rulesetz";
	
	/** The constant for the EnableScan property. This property defines whether automatic PMD source code
	 * scanning is enabled or not.
	 */
	public final static String PROP_ENABLE_SCAN = "EnableScan";
	
	/** The default rules.*/
	private static final String DEFAULT_RULES =
		"AvoidDuplicateLiterals, StringToString, StringInstantiation, JUnitStaticSuite, " +
		"JUnitSpelling, ForLoopsMustUseBracesRule, IfElseStmtsMustUseBracesRule, " +
		"WhileLoopsMustUseBracesRule, IfStmtsMustUseBraces, EmptyCatchBlock, EmptyIfStmt, " +
		"EmptyWhileStmt, JumbledIncrementer, UnnecessaryConversionTemporaryRule, " +
		"OverrideBothEqualsAndHashcodeRule, EmptyTryBlock, EmptySwitchStatements, " +
		"EmptyFinallyBlock, UnusedLocalVariable, UnusedPrivateField, UnusedFormalParameter, " +
		"UnnecessaryConstructorRule, UnusedPrivateMethod, SwitchStmtsShouldHaveDefault, " +
		"SimplifyBooleanReturnsRule, LooseCouplingRule, AvoidDeeplyNestedIfStmts, " +
		"AvoidReassigningParametersRule, OnlyOneReturn, UseSingletonRule, " +
		"DontImportJavaLang, UnusedImports, DuplicateImports, ";

    /** Name of key for storing part of custom rule-set settings. */
    private static String PROP_INCLUDE_STD_RULES = "includeStdRules";
    
    private static final String NODE_RULESETS = "rulesets";
    private static final String NODE_CLASSPATH = "classpath";
    
    private static PMDOptionsSettings INSTANCE = new PMDOptionsSettings();

    /**
     * Default instance of this system option, for the convenience of associated
     * classes.
     *
     * @return The default value
     */
    public static PMDOptionsSettings getDefault() {
            return INSTANCE;
    }

    private transient PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    /* Sets the default rulesets and initializes the option */
    private PMDOptionsSettings() {
    }

    private final String getProperty(String key) {
        return NbPreferences.forModule(PMDOptionsSettings.class).get(key, null);
    }
    
    private final void putProperty(String key, String value) {
        Preferences pref = NbPreferences.forModule(PMDOptionsSettings.class);
        if (value != null) {
            pref.put(key, value);
        } else {
            NbPreferences.forModule(PMDOptionsSettings.class).remove(key);
        }
    }
    
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }


    /**
     * Returns the rulesets property
     *
     * @return the rulesets property
     */
    public String getRules() {
        return NbPreferences.forModule(PMDOptionsSettings.class).get(PROP_RULES, DEFAULT_RULES);
    }

	/**
	 * Sets the rulesets property
	 *
	 * @param rules The new rules value
	 */
	public void setRules( String rules ) {
            putProperty( PROP_RULES, rules );
            pcs.firePropertyChange(PROP_RULES, null, null);
	}

        /**
         * Determines the list of rules to use.
         * This just delegates to {@link #createRuleList} with the argument
         * {@link PMDOptionsSettings}.{@link PMDOptionsSettings#getDefault getDefault()}.{@link PMDOptionsSettings#getRules getRules()}.
         *
         * @return a list containing the rules to use. Each element of the list is an instance of {@link Rule}.
         */
        public List getRuleList() {
            return ConfigUtils.createRuleList( getRules(), getRuleProperties());
        }
        
	
    /**
     * Returns the rule properties property. See {@link #PROP_RULE_PROPERTIES}.
     * Note: this returns a non-live <em>deep copy</em> of the rule properties map;
     * changes to the map or its contents will not affect the PMD settings until you
     * call {@link #setRuleProperties} with the modified map.
     * <p>
     * IMPLEMENTATION NOTE: the deep copy operation recurses into all Map and Collection
     * values, so circular references will kill it. Make sure you put only simple data
     * in the rule properties! Also, note that non-Map, non-Collection values are not
     * cloned; the assumption is that the leaves of this hierarchy (actual rule property
     * values) are always immutable objects, most likely just strings.
     *
     * @return the rule properties, not null.
     */
    public Map<String, Map<String, String>> getRuleProperties() {
        Map<String, Map<String, String>> ruleProps = new HashMap<String, Map<String, String>>();
        try {
            Preferences prefs = NbPreferences.forModule(PMDOptionsSettings.class);
            // 1. delete all old properties that are no longer valid
            for (String keyName: prefs.keys()) {
                if (!keyName.startsWith(PROP_RULE_PROPERTIES+'.'))
                    continue;
                int idx = keyName.indexOf(".", PROP_RULE_PROPERTIES.length()+2);
                if (idx == -1)
                    continue;
                String ruleName = keyName.substring(PROP_RULE_PROPERTIES.length()+1, idx);
                Map<String, String> props = ruleProps.get(ruleName);
                if (props == null) { 
                    props = new HashMap<String, String>();
                    ruleProps.put(ruleName, props);
                }
                String propName = keyName.substring(idx+1);
                props.put(propName, prefs.get(keyName, null));
            }
        } catch (BackingStoreException bse) {
            Logger.getLogger(PMDOptionsSettings.class.getName()).log(Level.INFO, "Error when storing preferences", bse);
        }
        return ruleProps;
    }

    /**
     * Sets the rule properties property (sorry). See {@link #PROP_RULE_PROPERTIES}.
     * See also the constraints on rule property values and the ban on circular references,
     * in the documentation for {@link #getRuleProperties}.
     *
     * @param ruleProperties The new rule properties map, not null. In this Map, each key must be
     * a String, the name of a PMD rule, and each value must be a Map, specifying the properties
     * for that rule.
     */
    public void setRuleProperties(Map<String, Map<String, String>> ruleProperties) {
        try {
            Preferences prefs = NbPreferences.forModule(PMDOptionsSettings.class);
            // 1. delete all old properties that are no longer valid
            for (String keyName: prefs.keys()) {
                if (!keyName.startsWith(PROP_RULE_PROPERTIES+'.'))
                    continue;
                int idx = keyName.indexOf(".", PROP_RULE_PROPERTIES.length()+2);
                if (idx == -1)
                    continue;
                String ruleName = keyName.substring(PROP_RULE_PROPERTIES.length()+1, idx);
                String propName = keyName.substring(idx+1);
                if (ruleProperties.get(ruleName) != null && ruleProperties.get(ruleName).get(propName) == null) {
                    prefs.remove(keyName);
                }
            }

            // 2. set current ones
            for (Map.Entry<String, Map<String, String>> ruleProp: ruleProperties.entrySet()) {
                String ruleName = ruleProp.getKey();
                for (Map.Entry<String, String> prop: ruleProp.getValue().entrySet()) {
                    prefs.put(PROP_RULE_PROPERTIES+'.'+ruleName+'.'+prop.getKey(), prop.getValue());
                }
            }
            pcs.firePropertyChange(PROP_RULE_PROPERTIES, null, null);
        } catch (BackingStoreException bse) {
            Logger.getLogger(PMDOptionsSettings.class.getName()).log(Level.INFO, "Error when storing preferences", bse);
        }
    }

    /** Getter for property rulesets.
     * @return Value of property rulesets.
     *
     */
    public CustomRuleSetSettings getRulesets() {
        CustomRuleSetSettings crss = new CustomRuleSetSettings();
        try {
            Preferences prefs = NbPreferences.forModule(PMDOptionsSettings.class);
            boolean inclStdRueset = 
                    prefs.getBoolean(PROP_INCLUDE_STD_RULES, true);
            crss.setIncludeStdRules(inclStdRueset);
            
            if (prefs.nodeExists(NODE_RULESETS)) {
                List<String> rulesets = Arrays.asList(prefs.node(NODE_RULESETS).keys());
                crss.setRuleSets(rulesets);
            }
            
            if (prefs.nodeExists(NODE_CLASSPATH)) {
                List<String> cp = Arrays.asList(prefs.node(NODE_CLASSPATH).keys());
                crss.setClassPath(cp);
            }
            
        } catch (BackingStoreException bse) {
            Logger.getLogger(PMDOptionsSettings.class.getName()).log(Level.INFO, "Error when reading preferences", bse);
        }
        return crss;
    }

    /** Setter for property rulesets.
     * @param rulesets New value of property rulesets.
     *
     */
    public void setRulesets(CustomRuleSetSettings rulesets) {
        try {
            putProperty( PROP_INCLUDE_STD_RULES, Boolean.toString(rulesets.isIncludeStdRules()) );

            List<String> r = rulesets.getRuleSets();
            Preferences prefs = NbPreferences.forModule(PMDOptionsSettings.class);
            if (r.isEmpty() && prefs.nodeExists(NODE_RULESETS)) {
                prefs.node(NODE_RULESETS).removeNode();
            }
            else {
                Preferences rsPref = prefs.node(NODE_RULESETS);
                for (String key: rsPref.keys()) {
                    rsPref.remove(key);
                }
                for(String s: r) {
                    rsPref.put(s, s);
                }
            }
            
            @SuppressWarnings("unchecked")
            List<String> cp = rulesets.getClassPath();
            if (cp.isEmpty() && prefs.nodeExists(NODE_CLASSPATH)) {
                prefs.node(NODE_CLASSPATH).removeNode();
            }
            else {
                Preferences rsPref = prefs.node(NODE_CLASSPATH);
                for (String key: rsPref.keys()) {
                    rsPref.remove(key);
                }
                for(String s: cp) {
                    rsPref.put(s, s);
                }
            }
            
        } catch (BackingStoreException bse) {
            Logger.getLogger(PMDOptionsSettings.class.getName()).log(Level.INFO, "Error when storing preferences", bse);
        }
        pcs.firePropertyChange(PROP_RULESETS, null, null);
    }
	
    /** Getter for property scanEnabled.
     * @return Value of property scanEnabled.
     *
     */
    public Boolean isScanEnabled() {
        return NbPreferences.forModule(PMDOptionsSettings.class).getBoolean(PROP_ENABLE_SCAN, false);
    }
    
    /** Setter for property scanEnabled.
     * @param scanEnabled New value of property scanEnabled.
     *
     */
    public void setScanEnabled(Boolean scanEnabled) {
        NbPreferences.forModule(PMDOptionsSettings.class).putBoolean(PROP_ENABLE_SCAN, scanEnabled);
        pcs.firePropertyChange(PROP_ENABLE_SCAN, null, null);
    }
    
	/**
	 * Performs a deep-copy operation on the given map, recursing into all values that are Maps or
	 * Collections. Note that this is risky; if the map/collection hierarchy contains circular references,
	 * then this will recurse infinitely and terminate in a StackOverflowError. So, uh, don't put circular
	 * references here :)
	 *
	 * @param map the Map to copy, not null.
	 */
	private <K, V> Map<K, V> deepMapCopy(Map<K, V> map) {
		HashMap<K, V> copy = new HashMap<K, V>(map.size() * 2 + 2);
        for (Map.Entry<K, V> entry: map.entrySet()) {
			K key = entry.getKey();
			V value = entry.getValue();
			if(value instanceof Map) {
				copy.put(key, (V)deepMapCopy((Map)value));
			} else if(value instanceof Collection) {
				copy.put(key, (V)deepCollectionCopy((Collection)value));
			} else {
				copy.put(key, value);
			}
		}
		return copy;
	}
	
	/**
	 * Performs a deep-copy operation on the given collection, recursing into all values that are Maps or
	 * Collections. Note that this is risky; if the map/collection hierarchy contains circular references,
	 * then this will recurse infinitely and terminate in a StackOverflowError. So, uh, don't put circular
	 * references here :)
	 *
	 * @param coll the Collection to copy, not null.
	 */
	private <M> Collection<M> deepCollectionCopy(Collection<M> coll) {
		Collection<M> copy;
		if(coll instanceof Set) {
			copy = new HashSet<M>(coll.size() * 2 + 2);
		} else {
			copy = new ArrayList<M>(coll.size());
		}
		for (M elem: coll) {
			if(elem instanceof Map) {
				copy.add((M)deepMapCopy((Map)elem));
			} else if(elem instanceof Collection) {
				copy.add((M)deepCollectionCopy((Collection)elem));
			} else {
				copy.add(elem);
			}
		}
		return copy;
	}
	
}
