package net.sourceforge.pmd.swingui;

/**
 *
 * @author Donald A. Leckie
 * @since August 29, 2002
 * @version $Revision$, $Date$
 */
public interface IRulesEditingData
{

    /**
     *******************************************************************************
     *
     * @return
     */
    public String getClassName();

    /**
     *******************************************************************************
     *
     * @return
     */
    public String getDescription();

    /**
     *******************************************************************************
     *
     * @return
     */
    public String getExample();

    /**
     *******************************************************************************
     *
     * @return
     */
    public String getMessage();

    /**
     *******************************************************************************
     *
     * @return
     */
    public String getName();

    /**
     *******************************************************************************
     *
     * @return
     */
    public IRulesEditingData getParentRuleData();

    /**
     *******************************************************************************
     *
     * @return
     */
    public IRulesEditingData getParentRuleSetData();

    /**
     *******************************************************************************
     *
     * @return
     */
    public String getPropertyValue();

    /**
     *******************************************************************************
     *
     * @return
     */
    public String getPropertyValueType();

    /**
     *******************************************************************************
     *
     * @return
     */
    public IRulesEditingData getSibling(String name);

    /**
     *******************************************************************************
     *
     * @return
     */
    public boolean include();

    /**
     *******************************************************************************
     *
     * @return
     */
    public boolean isRule();

    /**
     *******************************************************************************
     *
     * @return
     */
    public boolean isRuleSet();

    /**
     *******************************************************************************
     *
     * @return
     */
    public boolean isProperty();

    /**
     *******************************************************************************
     *
     * @return
     */
    public void setClassName(String className);

    /**
     *******************************************************************************
     *
     * @return
     */
    public void setDescription(String description);

    /**
     *******************************************************************************
     *
     * @return
     */
    public void setExample(String example);

    /**
     *******************************************************************************
     *
     * @return
     */
    public void setMessage(String message);

    /**
     *******************************************************************************
     *
     * @return
     */
    public void setName(String name);

    /**
     *******************************************************************************
     *
     * @return
     */
    public void setPropertyValue(String value);

    /**
     *******************************************************************************
     *
     * @return
     */
    public void setPropertyValueType(String value);

    /**
     *******************************************************************************
     *
     * @return
     */
    public void setInclude(boolean include);
}