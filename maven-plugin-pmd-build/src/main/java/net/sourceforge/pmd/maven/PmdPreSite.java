package net.sourceforge.pmd.maven;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.build.PmdBuildException;
import net.sourceforge.pmd.build.PmdBuildTools;
import net.sourceforge.pmd.build.RuleSetToDocs;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

/**
 * Generates xdoc sites for each ruleset.
 */
@Mojo( name = "pmd-pre-site", requiresDependencyResolution = ResolutionScope.RUNTIME )
public class PmdPreSite extends AbstractMojo {
    /**
     * Path to the existing site descriptor
     */
    @Parameter(property = "pmd.siteXml", defaultValue = "src/site/site.pre.xml")
    private String siteXml;

    /**
     * Path to the existing site descriptor
     */
    @Parameter(property = "pmd.siteXml.target", defaultValue="src/site/site.xml")
    private String siteXmlTarget;

    /**
     * Path to the existing site descriptor
     */
    @Parameter(property = "pmd.siteTarget", defaultValue="${project.build.directory}/generated-xdocs/rules")
    private String target;

    /**
     * Path to the existing site descriptor
     */
    @Parameter(property = "pmd.rulesets", defaultValue="src/main/resources/rulesets/")
    private String rulesetsDirectory;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    public void execute() throws MojoExecutionException {
	List<URL> runtimeClasspath = determineRuntimeClasspath();

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
	tool.setRuntimeClasspath(runtimeClasspath.toArray(new URL[runtimeClasspath.size()]));

	try {
	    tool.convertRulesets();
	    tool.preSiteGeneration();
	}
	catch ( PmdBuildException e) {
	    throw new MojoExecutionException(e.getMessage());
	}
    }

    private List<URL> determineRuntimeClasspath() {
	List<URL> runtimeClasspath;
	try {
	    runtimeClasspath = new ArrayList<URL>();
	    runtimeClasspath.add(new File(project.getBuild().getOutputDirectory()).toURI().toURL());
	    Set<Artifact> runtimeArtifacts = project.getArtifacts();
	    for (Artifact a : runtimeArtifacts) {
		if (Artifact.SCOPE_COMPILE.equals(a.getScope()) || Artifact.SCOPE_RUNTIME.equals(a.getScope()) ) {
		    runtimeClasspath.add(a.getFile().toURI().toURL());
		}
	    }
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
	return runtimeClasspath;
    }
}