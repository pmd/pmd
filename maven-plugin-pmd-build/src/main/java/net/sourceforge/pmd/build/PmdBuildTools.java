/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.build;

import java.net.URL;

public interface PmdBuildTools {

    /**
     * @return the rulesDirectory
     */
    String getRulesDirectory();

    /**
     * @param rulesDirectory
     *            the rulesDirectory to set
     */
    void setRulesDirectory(String rulesDirectory);

    /**
     *
     * @throws PmdBuildException
     */
    void convertRulesets() throws PmdBuildException;

    void preSiteGeneration() throws PmdBuildException;

    /**
     * @return the targetDirectory
     */
    String getTargetDirectory();

    /**
     * @param targetDirectory
     *            the targetDirectory to set
     */
    void setTargetDirectory(String targetDirectory);

    /**
     *
     * @param siteXml
     */
    void setSiteXml(String siteXml);

    /**
     *
     * @param siteXmlTaget
     */
    void setSiteXmlTarget(String siteXmlTarget);

    /**
     * Configures the classpath to use to analyze the properties of rules.
     * 
     * @param runtimeClasspath
     * @see RuntimeRulePropertiesAnalyzer
     */
    void setRuntimeClasspath(URL[] runtimeClasspath);

}
