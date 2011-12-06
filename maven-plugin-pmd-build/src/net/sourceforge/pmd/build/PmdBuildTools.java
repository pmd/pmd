package net.sourceforge.pmd.build;


public interface PmdBuildTools {

    /**
     * @return the rulesDirectory
     */
    public abstract String getRulesDirectory();

    /**
     * @param rulesDirectory the rulesDirectory to set
     */
    public abstract void setRulesDirectory(String rulesDirectory);

    /**
     *
     * @throws PmdBuildException
     */
    public abstract void convertRulesets() throws PmdBuildException;

    public abstract void preSiteGeneration() throws PmdBuildException;

    /**
     * @return the targetDirectory
     */
    public abstract String getTargetDirectory();

    /**
     * @param targetDirectory the targetDirectory to set
     */
    public abstract void setTargetDirectory(String targetDirectory);

    /**
     *
     * @param siteXml
     */
    public abstract void setSiteXml(String siteXml);

    /**
     *
     * @param siteXmlTaget
     */
    public abstract void setSiteXmlTarget(String siteXmlTarget);
}
