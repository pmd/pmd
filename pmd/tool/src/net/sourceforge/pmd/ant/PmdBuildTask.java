/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.ant;

import javax.xml.transform.TransformerException;

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
	PmdBuildTools tool = new RuleSetToDocs();
	validate();
	tool.setTargetDirectory(this.target);
	tool.setRulesDirectory(this.rulesDirectory);

	try {
        	tool.convertRulesets();
        	tool.generateRulesIndex();
        	tool.createPomForJava4("pom.xml","pmd-jdk14-pom.xml");
	}
	catch ( PmdBuildException e) {
	    throw new BuildException(e);
	} catch (TransformerException e) {
	    throw new BuildException(e);
	}
    }

    private void validate() throws BuildException {
	if ( this.target == null || "".equals(target) )
	    throw new BuildException("Attribute targetDirectory is not optionnal");
	if ( this.rulesDirectory == null || "".equals(this.rulesDirectory) )
	    throw new BuildException("Attribute rulesDirectory is not optionnal");
    }
}
