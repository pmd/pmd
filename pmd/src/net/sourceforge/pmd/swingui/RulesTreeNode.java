package net.sourceforge.pmd.swingui;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Enumeration;

/**
 *
 * @author Donald A. Leckie
 * @since August 29, 2002
 * @version $Revision$, $Date$
 */
class RulesTreeNode extends DefaultMutableTreeNode implements IRulesEditingData, IConstants
{
    private RuleSet m_ruleSet;
    private Rule m_rule;
    private String m_className;
    private String m_name;
    private String m_message;
    private String m_description;
    private String m_example;
    private String m_propertyValue;
    private String m_propertyValueType;
    private byte m_flags;

    // Constant
    private static final byte IS_ROOT = 0x01;
    private static final byte IS_RULE_SET = 0x02;
    private static final byte IS_RULE = 0x04;
    private static final byte IS_PROPERTY = 0x08;
    private static final byte INCLUDE = (byte) 0x80;

    /**
     ***************************************************************************
     *
     * @param name
     */
    protected RulesTreeNode(String text)
    {
        super();

        m_name = trim(text);
        m_flags |= IS_ROOT;
        m_flags |= INCLUDE;

        setDisplayName();
    }

    /**
     ***************************************************************************
     *
     * @param name
     */
    protected RulesTreeNode(RuleSet ruleSet)
    {
        super();

        m_name = trim(ruleSet.getName());
        m_description = trim(ruleSet.getDescription());
        m_ruleSet = ruleSet;
        m_flags |= IS_RULE_SET;

        if (ruleSet.include())
        {
            m_flags |= INCLUDE;
        }

        setDisplayName();
    }

    /**
     ***************************************************************************
     *
     * @param name
     */
    protected RulesTreeNode(RulesTreeNode ruleSetNode, Rule rule)
    {
        super();

        m_name = trim(rule.getName());
        m_className = trim(rule.getClass().getName());
        m_message = trim(rule.getMessage());
        m_description = trim(rule.getDescription());
        m_example = trim(rule.getExample());
        m_ruleSet = ruleSetNode.getRuleSet();
        m_rule = rule;
        m_flags |= IS_RULE;

        if (rule.include())
        {
            m_flags |= INCLUDE;
        }

        setDisplayName();
    }

    /**
     ***************************************************************************
     *
     * @param name
     */
    protected RulesTreeNode(RulesTreeNode ruleNode,
                            String propertyName,
                            String propertyValue,
                            String propertyValueType)
    {
        super();

        m_name = trim(propertyName);
        m_propertyValue = trim(propertyValue);
        m_propertyValueType = trim(propertyValueType);
        m_flags |= IS_PROPERTY;
        m_rule = ruleNode.getRule();
        m_ruleSet = ((RulesTreeNode) ruleNode.getParent()).getRuleSet();

        setDisplayName();
    }

    /**
     ***************************************************************************
     *
     * @param childName
     *
     * @return
     */
    protected RulesTreeNode getChildNode(String childName)
    {
        Enumeration children = children();

        while (children.hasMoreElements())
        {
            RulesTreeNode childNode = (RulesTreeNode) children.nextElement();

            if (childNode.getName().equalsIgnoreCase(childName))
            {
                return childNode;
            }
        }

        return null;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public String getClassName()
    {
        return m_className;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public String getDescription()
    {
        return m_description;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public String getExample()
    {
        return m_example;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public String getMessage()
    {
        return m_message;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public String getName()
    {
        return m_name;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public IRulesEditingData getParentRuleData()
    {
        if (isProperty())
        {
            return (IRulesEditingData) getParent();
        }

        return null;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public IRulesEditingData getParentRuleSetData()
    {
        if (isProperty())
        {
            return (IRulesEditingData) getParent().getParent();
        }

        if (isRule())
        {
            return (IRulesEditingData) getParent();
        }

        return null;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public String getPropertyValue()
    {
        return m_propertyValue;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public String getPropertyValueType()
    {
        return m_propertyValueType;
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    public IRulesEditingData getSibling(String name)
    {
        RulesTreeNode parentNode = (RulesTreeNode) getParent();

        if (parentNode != null)
        {
            return parentNode.getChildNode(name);
        }

        return null;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public boolean include()
    {
        return (m_flags & INCLUDE) != 0;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public boolean isProperty()
    {
        return (m_flags & IS_PROPERTY) != 0;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public boolean isRule()
    {
        return (m_flags & IS_RULE) != 0;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public boolean isRuleSet()
    {
        return (m_flags & IS_RULE_SET) != 0;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public boolean isRoot()
    {
        return (m_flags & IS_ROOT) != 0;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    protected Rule getRule()
    {
        return m_rule;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    protected RuleSet getRuleSet()
    {
        return m_ruleSet;
    }

    /**
     **************************************************************************
     *
     * @param newName
     */
    protected void setDisplayName()
    {
        String displayName;

        if (isProperty())
        {
            displayName = m_name + ":" + m_propertyValue;
        }
        else
        {
            displayName = m_name;
        }

        setUserObject(displayName);
    }

    /**
     **************************************************************************
     *
     * @param newName
     */
    public void setName(String newName)
    {
        m_name = trim(newName);

        setDisplayName();
    }

    /**
     **************************************************************************
     *
     * @param newName
     */
    public void setMessage(String newMessage)
    {
        m_message = trim(newMessage);
    }

    /**
     **************************************************************************
     *
     * @param newName
     */
    public void setDescription(String newDescription)
    {
        m_description = trim(newDescription);
    }

    /**
     **************************************************************************
     *
     * @param newName
     */
    public void setExample(String newExample)
    {
    }

    /**
     **************************************************************************
     *
     * @param newName
     */
    public void setPropertyValue(String newValue)
    {
        m_propertyValue = trim(newValue);

        setDisplayName();
    }

    /**
     **************************************************************************
     *
     * @param newName
     */
    public void setPropertyValueType(String newValue)
    {
        m_propertyValueType = trim(newValue);
    }

    /**
     **************************************************************************
     *
     * @param newName
     */
    public void setInclude(boolean include)
    {
        if (include)
        {
            m_flags |= INCLUDE;
        }
        else
        {
            m_flags &= (~INCLUDE);
        }
    }

    /**
     *************************************************************************
     *
     * @param newClass
     */
    public void setClassName(String newClassName)
    {
        m_className = trim(newClassName);
    }

    /**
     *************************************************************************
     *
     */
    protected void saveData()
    {
        if (isRuleSet())
        {
            m_ruleSet.setName(m_name);
            m_ruleSet.setDescription(m_description);
            m_ruleSet.setInclude(include());
        }
        else if (isRule())
        {
            m_rule.setName(m_name);
            m_rule.setMessage(m_message);
            m_rule.setDescription(m_description);
            m_rule.setExample(m_example);
            m_rule.setInclude(include());
        }
        else if (isProperty())
        {
            m_rule.getProperties().setValue(m_name, m_propertyValue);
            m_rule.getProperties().setValueType(m_name, m_propertyValueType);
        }
    }

    /**
     *************************************************************************
     *
     * @param text
     *
     * @return
     */
    private String trim(String text)
    {
        if (text == null)
        {
            text = EMPTY_STRING;
        }
        else
        {
            text = text.trim();

            if (text.length() == 0)
            {
                text = EMPTY_STRING;
            }
        }

        return text;
    }

    /**
     *************************************************************************
     *
     * @return
     */
    protected static final RulesTreeNode createRootNode()
    {
        return new RulesTreeNode("Rules");
    }
}