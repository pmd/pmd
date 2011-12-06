package net.sourceforge.pmd.maven;

import net.sourceforge.pmd.build.PmdBuildException;
import net.sourceforge.pmd.build.PmdBuildTools;
import net.sourceforge.pmd.build.RuleSetToDocs;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Says "Hi" to the user.
 * @goal pmd-pre-site
 */
public class PmdPreSite extends AbstractMojo
{
    /**
     * Path to the existing site descriptor
     *
     * @parameter expression="${pmd.siteXml}" default-value="src/site/site.pre.xml"
     */
    private String siteXml;

    /**
     * Path to the existing site descriptor
     *
     * @parameter expression="${pmd.siteXml.target}" default-value="src/site/site.xml"
     */
    private String siteXmlTarget;
    
    /**
     * Path to the existing site descriptor
     *
     * @parameter expression="${pmd.siteTarget}" default-value="${project.build.directory}/generated-xdocs/rules"
     */
    private String target;

    /**
     * Path to the existing site descriptor
     *
     * @parameter expression="${pmd.rulesets}" default-value="src/main/resources/rulesets/
     */
    private String rulesetsDirectory;    
    
    public void execute() throws MojoExecutionException {
        getLog().info("PMD: site generation preparation");
        getLog().debug("- target:" + target);
        getLog().debug("- siteXml:" + siteXml);
        getLog().debug("- rulesets:" + rulesetsDirectory);
        getLog().debug(" -siteXmlTarget" + siteXmlTarget);
        PmdBuildTools tool = new RuleSetToDocs();
		tool.setTargetDirectory(target);
		tool.setSiteXml(siteXml);
		tool.setRulesDirectory(rulesetsDirectory);
		tool.setSiteXmlTarget(siteXmlTarget);
	
		try {
	        	tool.convertRulesets();
	        	tool.preSiteGeneration();
		}
		catch ( PmdBuildException e) {
		    throw new MojoExecutionException(e.getMessage());
		}
    }
}