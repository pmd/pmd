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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.openide.options.SystemOption;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * Settings for the PMD-NetBeans module.
 */
public class PMDOptionsSettings extends SystemOption {

	/** The serialVersionUID. Don't change! */
	private final static long serialVersionUID = 8418202279282091070L;

	/** The constant for the rules property. The String value of this property is a comma-separated list of
	 * names of currently enabled rules. The names refer to the rule definitions in all rulesets returned by
	 * {@link RuleSetFactory#getRegisteredRuleSets}.
	 */
	public final static String PROP_RULES = "rules";

	/** The constant for the rule properties property. Please excuse the name! The value of this property is
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
	
	/** The constant for the interval property. The value of this property is the interval at which
	 * source code in the active editor document should be automatically PMD-scanned, in seconds.
	 */
	public final static String PROP_SCAN_INTERVAL = "interval";
	
	/** The constant for the EnableScan property. This property defines whether automatic PMD source code
	 * scanning is enabled or not.
	 */
	public final static String PROP_ENABLE_SCAN = "EnableScan";
	
	/** Default interval for scanning, two seconds. **/
	public static final int DEFAULT_SCAN_INTERVAL = 2;
	
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

	// No constructor please!

	/** Sets the default rulesets and initializes the option */
	protected void initialize() {
		super.initialize();
		setRules(DEFAULT_RULES);
		// OK to initialize with an empty map, we'll never expose it (always return a HashMap copy)
		setRuleProperties(Collections.EMPTY_MAP);
		setRulesets(new CustomRuleSetSettings());
		setScanEnabled(Boolean.FALSE);
		setScanInterval(new Integer(DEFAULT_SCAN_INTERVAL));
	}


	/**
	 * Returns the displayName of these options
	 *
	 * @return the displayname
	 */
	public String displayName() {
		return NbBundle.getMessage( PMDOptionsSettings.class, "LBL_settings" );
	}


	/**
	 * Returns the default help
	 *
	 * @return The helpCtx value
	 */
	public HelpCtx getHelpCtx() {
		return HelpCtx.DEFAULT_HELP;
	}


	/**
	 * Default instance of this system option, for the convenience of associated
	 * classes.
	 *
	 * @return The default value
	 */
	public static PMDOptionsSettings getDefault() {
		return ( PMDOptionsSettings )findObject( PMDOptionsSettings.class, true );
	}


	/**
	 * Returns the rulesets property
	 *
	 * @return the rulesets property
	 */
	public String getRules() {
		return ( String )getProperty( PROP_RULES );
	}

	/**
	 * Sets the rulesets property
	 *
	 * @param rules The new rules value
	 */
	public void setRules( String rules ) {
		putProperty( PROP_RULES, rules, true );
	}

	/**
	 * Returns the rule properties property (sorry). See {@link #PROP_RULE_PROPERTIES}.
	 * Note: this returns a non-live <em>deep copy</em> of the rule properties map;
	 * changes to the map or its contents will not affect the PMD settings until you
	 * call {@link #setRuleProperties} with the modified map.
	 * <p>
	 * This is my nice naive way to observe correct property change event handling. It
	 * gets inefficient as the set of rule properties gets large ... but the set of
	 * rule properties generally <em>doesn't</em> get large because it really only
	 * contains <em>overrides</em> of the PMD default rule properties.
	 * <p>
	 * IMPLEMENTATION NOTE: the deep copy operation recurses into all Map and Collection
	 * values, so circular references will kill it. Make sure you put only simple data
	 * in the rule properties! Also, note that non-Map, non-Collection values are not
	 * cloned; the assumption is that the leaves of this hierarchy (actual rule property
	 * values) are always immutable objects, most likely just strings.
	 *
	 * @return the rule properties, not null.
	 */
	public Map getRuleProperties() {
		return deepMapCopy((Map)getProperty(PROP_RULE_PROPERTIES));
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
	public void setRuleProperties(Map ruleProperties) {
		putProperty( PROP_RULE_PROPERTIES, ruleProperties, true );
	}

	/** Getter for property rulesets.
	 * @return Value of property rulesets.
	 *
	 */
	public CustomRuleSetSettings getRulesets() {
		return (CustomRuleSetSettings)getProperty( PROP_RULESETS );
	}
	
	/** Setter for property rulesets.
	 * @param rulesets New value of property rulesets.
	 *
	 */
	public void setRulesets(CustomRuleSetSettings rulesets) {
		putProperty( PROP_RULESETS, rulesets, true );
	}
	
	/** Getter for property scanEnabled.
	 * @return Value of property scanEnabled.
	 *
	 */
	public Boolean isScanEnabled() {
		return (Boolean)getProperty( PROP_ENABLE_SCAN );
	}
	
	/** Setter for property scanEnabled.
	 * @param scanEnabled New value of property scanEnabled.
	 *
	 */
	public void setScanEnabled(Boolean scanEnabled) {
		putProperty( PROP_ENABLE_SCAN, scanEnabled, true );
	}
	
	/** Getter for property scanInterval.
	 * @return Value of property scanInterval.
	 *
	 */
	public Integer getScanInterval() {
		return (Integer)getProperty( PROP_SCAN_INTERVAL);
	}
	
	/** Setter for property scanInterval.
	 * @param scanInterval New value of property scanInterval.
	 *
	 */
	public void setScanInterval(Integer scanInterval) {
		putProperty( PROP_SCAN_INTERVAL, scanInterval );
	}
	
	/**
	 * Performs a deep-copy operation on the given map, recursing into all values that are Maps or
	 * Collections. Note that this is risky; if the map/collection hierarchy contains circular references,
	 * then this will recurse infinitely and terminate in a StackOverflowError. So, uh, don't put circular
	 * references here :)
	 *
	 * @param map the Map to copy, not null.
	 */
	private Map deepMapCopy(Map map) {
		HashMap copy = new HashMap(map.size() * 2 + 2);
		Iterator iterator = map.entrySet().iterator();
		Map.Entry entry;
		Object key, value;
		while(iterator.hasNext()) {
			entry = (Map.Entry)iterator.next();
			key = entry.getKey();
			value = entry.getValue();
			if(value instanceof Map) {
				copy.put(key, deepMapCopy((Map)value));
			} else if(value instanceof Collection) {
				copy.put(key, deepCollectionCopy((Collection)value));
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
	private Collection deepCollectionCopy(Collection coll) {
		Collection copy;
		if(coll instanceof Set) {
			copy = new HashSet(coll.size() * 2 + 2);
		} else {
			copy = new ArrayList(coll.size());
		}
		Iterator iterator = coll.iterator();
		Object elem;
		while(iterator.hasNext()) {
			elem = iterator.next();
			if(elem instanceof Map) {
				copy.add(deepMapCopy((Map)elem));
			} else if(elem instanceof Collection) {
				copy.add(deepCollectionCopy((Collection)elem));
			} else {
				copy.add(elem);
			}
		}
		return copy;
	}
	
}
