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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a custom rulesets configuration.
 */
public class CustomRuleSetSettings implements Serializable {
	/** The serialVersionUID. */
	private final static long serialVersionUID = 8418202279212345678L;
	
	/** Holds value of property ruleSets. */
	private List<String> ruleSets;
	
	/** Holds value of property classPath. */
	private List classPath;
	
	/** Holds value of property includeStdRules. */
	private boolean includeStdRules;
	
	/** Creates a new instance of CustomRuleSetSettings */
	public CustomRuleSetSettings( List<String> ruleSets, List classPath, boolean include) {
		this.ruleSets = ruleSets;
		this.classPath = classPath;
		this.includeStdRules = include;
	}
	
	public CustomRuleSetSettings() {
		this(new ArrayList<String>(), new ArrayList(), true);
	}
	
	/** Getter for property ruleSets.
	 * @return Value of property ruleSets.
	 *
	 */
	public List<String> getRuleSets() {
		return this.ruleSets;
	}
	
	/** Setter for property ruleSets.
	 * @param ruleSets New value of property ruleSets.
	 *
	 */
	public void setRuleSets(List<String> ruleSets) {
		this.ruleSets = ruleSets;
	}
	
	/** Getter for property classPath.
	 * @return Value of property classPath.
	 *
	 */
	public List getClassPath() {
		return this.classPath;
	}
	
	/** Setter for property classPath.
	 * @param classPath New value of property classPath.
	 *
	 */
	public void setClassPath(List classPath) {
		this.classPath = classPath;
	}
	
	/** Getter for property includeStdRules.
	 * @return Value of property includeStdRules.
	 *
	 */
	public boolean isIncludeStdRules() {
		return this.includeStdRules;
	}
	
	/** Setter for property includeStdRules.
	 * @param includeStdRules New value of property includeStdRules.
	 *
	 */
	public void setIncludeStdRules(boolean includeStdRules) {
		this.includeStdRules = includeStdRules;
	}
	
  @Override
  public String toString() {
    return "Custom rulesets";
  }
}