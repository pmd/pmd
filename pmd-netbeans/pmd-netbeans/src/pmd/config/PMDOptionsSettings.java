/*
 * Copyright (c) 2002, Ole-Martin Mørk
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */
package pmd.config;

import org.openide.options.SystemOption;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *  Options for PMD netbeans
 *
 * @author  Ole-Martin Mørk
 * @created  24. oktober 2002
 */
public class PMDOptionsSettings extends SystemOption
{

	// private static final long serialVersionUID = ...;

    /**
     * The constant for the rulesets property
     **/
	public static final String PROP_RULESETS = "rulesets";

	// No constructor please!

    /**
     * Sets the default rulesets and initializes the option
     */
	protected void initialize()
	{
		super.initialize();
		setRulesets( "rulesets/basic.xml,rulesets/imports.xml,rulesets/unusedcode.xml,rulesets/braces.xml" );
	}
    
    /**
     * Returns the displayName of these options
     * @return the displayname
     */
	public String displayName()
	{
		return NbBundle.getMessage( PMDOptionsSettings.class, "LBL_settings" );
	}

    /**
     * Returns the default help
     */
	public HelpCtx getHelpCtx()
	{
		return HelpCtx.DEFAULT_HELP;
	}

	/**
	 *  Default instance of this system option, for the convenience of associated
	 *  classes.
	 *
	 * @return  The default value
	 */
	public static PMDOptionsSettings getDefault()
	{
		return ( PMDOptionsSettings )findObject( PMDOptionsSettings.class, true );
	}

    /**
     * Returns the rulesets property
     * @return the rulesets property
     */
	public String getRulesets()
	{
		return ( String )getProperty( PROP_RULESETS );
	}

    /**
     * Sets the rulesets property
     * @param rulesets the rulesets value to set
     */
	public void setRulesets( String rulesets )
	{
		putProperty( PROP_RULESETS, rulesets, true );
	}
}