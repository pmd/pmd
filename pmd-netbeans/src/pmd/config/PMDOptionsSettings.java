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

import java.util.HashMap;
import java.util.Map;
import org.openide.options.SystemOption;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * Options for PMD netbeans
 *
 * @author Ole-Martin Mørk
 * @author Gunnlaugur Þór Briem
 * @created 24. oktober 2002
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
	
	/** Default interval for scanning, 10 seconds. **/
	public static final int DEFAULT_SCAN_INTERVAL = 10;
	
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
		setRules( DEFAULT_RULES );
		setRuleProperties( new HashMap() );
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
	 *
	 * @return the rule properties, not null.
	 */
	public Map getRuleProperties() {
		return ( Map )getProperty( PROP_RULE_PROPERTIES );
	}

	/**
	 * Sets the rule properties property (sorry). See {@link #PROP_RULE_PROPERTIES}.
	 *
	 * @param ruleProperties The new rule properties, not null.
	 */
	public void setRuleProperties( Map ruleProperties ) {
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
		putProperty( PROP_ENABLE_SCAN, scanEnabled );
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
	
}
