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

import org.openide.options.SystemOption;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * Options for PMD netbeans
 *
 * @author Ole-Martin Mørk
 * @created 24. oktober 2002
 */
public class PMDOptionsSettings extends SystemOption {

	/** The serialVersionUID. Don't change! */
	private final static long serialVersionUID = 8418202279282091070L;

	/** The constant for the rulesets property */
	public final static String PROP_RULES = "rules";

	public final static String PROP_CLASSPATH = "classpath";
	
	public final static String PROP_RULESETS = "rulesets";
	
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

	/** Getter for property classpath.
	 * @return Value of property classpath.
	 *
	 */
	public String getClasspath() {
		return (String)getProperty( PROP_CLASSPATH );
	}	
	
	/** Setter for property classpath.
	 * @param classpath New value of property classpath.
	 *
	 */
	public void setClasspath(String classpath) {
		putProperty( PROP_CLASSPATH, classpath, true );
	}
	
	/** Getter for property rulesets.
	 * @return Value of property rulesets.
	 *
	 */
	public String getRulesets() {
		return (String)getProperty( PROP_RULESETS );
	}
	
	/** Setter for property rulesets.
	 * @param rulesets New value of property rulesets.
	 *
	 */
	public void setRulesets(String rulesets) {
		putProperty( PROP_RULESETS, rulesets, true );
	}
	
}
