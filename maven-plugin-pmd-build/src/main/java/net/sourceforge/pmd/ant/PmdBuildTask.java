/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.ant;

import java.net.URL;

import net.sourceforge.pmd.build.PmdBuildException;
import net.sourceforge.pmd.build.PmdBuildTools;
import net.sourceforge.pmd.build.RuleSetToDocs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * @author Romain PELISSE, belaran@gmail.com
 *
 */
public class PmdBuildTask extends Task {

    private String rulesDirectory;
    private String target;
    private String siteXml;
    private String siteXmlTarget;
    private URL[] runtimeClasspath;

    public URL[] getRuntimeClasspath() {
	return runtimeClasspath;
    }
    public void setRuntimeClasspath(URL[] runtimeClasspath) {
	this.runtimeClasspath = runtimeClasspath;
    }
    public String getSiteXml() {
		return siteXml;
	}
	public void setSiteXml(String siteXml) {
		this.siteXml = siteXml;
	}
	
	public String getSiteXmlTarget() {
		return siteXmlTarget;
	}
	public void setSiteXmlTarget(String siteXmlTarget) {
		this.siteXmlTarget = siteXmlTarget;
	}

	private String rulesetToDocs;
    private String mergeRuleset;
    private String rulesIndex;
    private String indexFilename;
    private String mergedRulesetFilename;
    
	/**
     * @return the rulesDirectory
     */
    public String getRulesDirectory() {
        return rulesDirectory;
    }
    /**
     * @param rulesDirectory the rulesDirectory to set
     */
    public void setRulesDirectory(String rulesDirectory) {
        this.rulesDirectory = rulesDirectory;
    }
    /**
     * @return the targetDirectory
     */
    public String getTarget() {
        return target;
    }
    /**
     * @param targetDirectory the targetDirectory to set
     */
    public void setTarget(String targetDirectory) {
        this.target = targetDirectory;
    }

    public void execute() throws BuildException {
		PmdBuildTools tool = validate(new RuleSetToDocs());
		tool.setTargetDirectory(this.target);
		tool.setSiteXml(siteXml);
		tool.setSiteXmlTarget(this.siteXmlTarget);
		tool.setRulesDirectory(this.rulesDirectory);
		tool.setRuntimeClasspath(runtimeClasspath);
	
		try {
	        	tool.convertRulesets();
	        	tool.preSiteGeneration();
		}
		catch ( PmdBuildException e) {
		    throw new BuildException(e);
		}
    }

    private PmdBuildTools validate(RuleSetToDocs tool) throws BuildException {
		// Mandatory attributes
    	if ( this.target == null || "".equals(target) )
		    throw new BuildException("Attribute targetDirectory is not optional");
		if ( this.rulesDirectory == null || "".equals(this.rulesDirectory) )
		    throw new BuildException("Attribute rulesDirectory is not optional");
		if ( this.siteXml == null ||"".equals(siteXml))
		    throw new BuildException("Attribute siteXml is not optional");
		if ( this.runtimeClasspath == null || "".equals(runtimeClasspath)) {
		    throw new BuildException("Attribute pmdClasspath is not optional");
		}
		// Optional Attributes
		if ( this.mergedRulesetFilename != null && ! "".equals(this.mergedRulesetFilename) )
			tool.setMergedRuleSetFilename(this.mergedRulesetFilename);
		if ( this.rulesIndex != null && ! "".equals(this.rulesIndex) )
			tool.getXmlFileTemplater().setGenerateIndexXsl(this.rulesIndex);
		if ( this.rulesetToDocs != null && ! "".equals(this.rulesetToDocs) )
			tool.getXmlFileTemplater().setRulesetToDocsXsl(this.rulesetToDocs);
		if ( this.mergeRuleset != null && ! "".equals(this.mergeRuleset) )
			tool.getXmlFileTemplater().setMergeRulesetXsl(this.mergeRuleset);
		return tool;
    }
    
    
	/**
	 * @return the rulesetToDocs
	 */
	public String getRulesetToDocs() {
		return rulesetToDocs;
	}
	/**
	 * @param rulesetToDocs the rulesetToDocs to set
	 */
	public void setRulesetToDocs(String rulesetToDocs) {
		this.rulesetToDocs = rulesetToDocs;
	}
	/**
	 * @return the mergeRuleset
	 */
	public String getMergeRuleset() {
		return mergeRuleset;
	}
	/**
	 * @param mergeRuleset the mergeRuleset to set
	 */
	public void setMergeRuleset(String mergeRuleset) {
		this.mergeRuleset = mergeRuleset;
	}
	/**
	 * @return the rulesIndex
	 */
	public String getRulesIndex() {
		return rulesIndex;
	}
	/**
	 * @param rulesIndex the rulesIndex to set
	 */
	public void setRulesIndex(String rulesIndex) {
		this.rulesIndex = rulesIndex;
	}

	/**
	 * @return the indexFilename
	 */
	public String getIndexFilename() {
		return indexFilename;
	}
	/**
	 * @param indexFilename the indexFilename to set
	 */
	public void setIndexFilename(String indexFilename) {
		this.indexFilename = indexFilename;
	}
	/**
	 * @return the mergedRulesetFilename
	 */
	public String getMergedRulesetFilename() {
		return mergedRulesetFilename;
	}
	/**
	 * @param mergedRulesetFilename the mergedRulesetFilename to set
	 */
	public void setMergedRulesetFilename(String mergedRulesetFilename) {
		this.mergedRulesetFilename = mergedRulesetFilename;
	}
}
