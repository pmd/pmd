/*
 * CustomRuleSetSettings.java
 *
 * Created on 21. februar 2003, 21:24
 */

package pmd.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author  ole-martin mørk
 */
public class CustomRuleSetSettings implements Serializable {
	/** The serialVersionUID. Don't change! */
	private final static long serialVersionUID = 8418202279212345678L;
	
	/** Holds value of property ruleSets. */
	private List ruleSets;
	
	/** Holds value of property classPath. */
	private List classPath;
	
	/** Holds value of property includeStdRules. */
	private boolean includeStdRules;
	
	/** Creates a new instance of CustomRuleSetSettings */
	public CustomRuleSetSettings( List ruleSets, List classPath, boolean include) {
		this.ruleSets = ruleSets;
		this.classPath = classPath;
		this.includeStdRules = includeStdRules;
	}
	
	public CustomRuleSetSettings() {
		ruleSets = new ArrayList();
		classPath = new ArrayList();
		includeStdRules = true;
	}
	
	/** Getter for property ruleSets.
	 * @return Value of property ruleSets.
	 *
	 */
	public List getRuleSets() {
		return this.ruleSets;
	}
	
	/** Setter for property ruleSets.
	 * @param ruleSets New value of property ruleSets.
	 *
	 */
	public void setRuleSets(List ruleSets) {
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
	
	public String toString() {
		return "Custom rulesets";
	}
}