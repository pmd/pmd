package net.sourceforge.pmd.swingui;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.swingui.event.RulesTreeModelEvent;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;

/**
 *
 * @author Donald A. Leckie
 * @since August 29, 2002
 * @version $Revision$, $Date$
 */
public class RulesTreeNode extends DefaultMutableTreeNode implements Constants
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
    private int m_type;
    private boolean m_include;
    private int m_priority;

    // Constant
    private static final int IS_ROOT = 0x01;
    private static final int IS_RULE_SET = 0x02;
    private static final int IS_RULE = 0x04;
    private static final int IS_PROPERTY = 0x08;

    /**
     ***************************************************************************
     *
     * @param name
     */
    protected RulesTreeNode(String text)
    {
        super();

        m_name = trim(text);
        m_type = IS_ROOT;
        m_include = true;

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
        m_type = IS_RULE_SET;
        m_include = ruleSet.include();
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
        m_type = IS_RULE;
        m_include = rule.include();
        m_priority = rule.getPriority();
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
        m_type = IS_PROPERTY;
        m_rule = ruleNode.getRule();
        m_ruleSet = ((RulesTreeNode) ruleNode.getParent()).getRuleSet();
        m_include = true;
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
    protected String getClassName()
    {
        return m_className;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    protected String getDescription()
    {
        return m_description;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    protected String getExample()
    {
        return m_example;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    protected String getMessage()
    {
        return m_message;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    protected String getName()
    {
        return m_name;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    protected RulesTreeNode getParentRuleData()
    {
        if (isProperty())
        {
            return (RulesTreeNode) getParent();
        }

        return null;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    protected RulesTreeNode getParentRuleSetData()
    {
        if (isProperty())
        {
            return (RulesTreeNode) getParent().getParent();
        }

        if (isRule())
        {
            return (RulesTreeNode) getParent();
        }

        return null;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    protected String getPropertyValue()
    {
        return m_propertyValue;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    protected String getPropertyValueType()
    {
        return m_propertyValueType;
    }

    /**
     *******************************************************************************
     *
     * @return
     */
    protected RulesTreeNode getSibling(String name)
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
    protected boolean include()
    {
        return m_include;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    protected boolean includeAncestor()
    {
        boolean include = true;

        if (include)
        {
            if (isRule())
            {
                RulesTreeNode ruleSetNode;

                ruleSetNode = (RulesTreeNode) getParent();
                include = ruleSetNode.include();
            }
            else if (isProperty())
            {
                RulesTreeNode ruleNode = (RulesTreeNode) getParent();

                if (ruleNode.include())
                {
                    RulesTreeNode ruleSetNode;

                    ruleSetNode = (RulesTreeNode) ruleNode.getParent();
                    include = ruleSetNode.include();
                }
            }
        }

        return include;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    protected boolean isProperty()
    {
        return m_type == IS_PROPERTY;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    protected boolean isRule()
    {
        return m_type == IS_RULE;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    protected boolean isRuleSet()
    {
        return m_type == IS_RULE_SET;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public boolean isRoot()
    {
        return m_type == IS_ROOT;
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
     ***************************************************************************
     *
     * @return
     */
    protected int getPriority()
    {
        return m_priority;
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
    protected void setName(String newName)
    {
        m_name = trim(newName);

        setDisplayName();
    }

    /**
     **************************************************************************
     *
     * @param newName
     */
    protected void setMessage(String newMessage)
    {
        m_message = trim(newMessage);
    }

    /**
     **************************************************************************
     *
     * @param newName
     */
    protected void setDescription(String newDescription)
    {
        m_description = trim(newDescription);
    }

    /**
     **************************************************************************
     *
     * @param newName
     */
    protected void setExample(String newExample)
    {
    }

    /**
     **************************************************************************
     *
     * @param newName
     */
    protected void setPropertyValue(String newValue)
    {
        m_propertyValue = trim(newValue);

        setDisplayName();
    }

    /**
     **************************************************************************
     *
     * @param newName
     */
    protected void setPropertyValueType(String newValue)
    {
        m_propertyValueType = trim(newValue);
    }

    /**
     **************************************************************************
     *
     * @param newName
     */
    protected void setInclude(boolean include)
    {
        m_include = include;
    }

    /**
     **************************************************************************
     *
     * @param priority
     */
    protected void setPriority(int priority)
    {
        m_priority = priority;
    }

    /**
     *************************************************************************
     *
     * @param newClass
     */
    protected void setClassName(String newClassName)
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
            m_ruleSet.setInclude(m_include);
        }
        else if (isRule())
        {
            m_rule.setName(m_name);
            m_rule.setMessage(m_message);
            m_rule.setDescription(m_description);
            m_rule.setExample(m_example);
            m_rule.setInclude(m_include);
            m_rule.setPriority(m_priority);
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
     ***************************************************************************
     *
     * @param event
     */
    protected void sortChildren()
    {
        int childCount = getChildCount();
        RulesTreeNode[] treeNodes = new RulesTreeNode[childCount];
        boolean needToSort = false;

        for (int n = 0; n < childCount; n++)
        {
            treeNodes[n] = (RulesTreeNode) getChildAt(n);

            if ((n > 0) && (needToSort == false))
            {
                String previousNodeName = treeNodes[n - 1].getName();
                String currentNodeName = treeNodes[n].getName();

                if (currentNodeName.compareToIgnoreCase(previousNodeName) < 0)
                {
                    needToSort = true;
                }
            }
        }

        if (needToSort)
        {
            Arrays.sort(treeNodes, new SortComparator());
            removeAllChildren();

            for (int n = 0; n < treeNodes.length; n++)
            {
                add(treeNodes[n]);
            }

            RulesTreeModelEvent.notifyReload(this, this);
        }

        for (int n = 0; n < treeNodes.length; n++)
        {
            treeNodes[n] = null;
        }
    }

    /**
     *******************************************************************************
     *******************************************************************************
     *******************************************************************************
     */
    private class SortComparator implements Comparator
    {

        /**
         ***************************************************************************
         *
         * @param object1
         * @param object2
         *
         * @return
         */
        public int compare(Object object1, Object object2)
        {
            String name1 = ((RulesTreeNode) object1).getName();
            String name2 = ((RulesTreeNode) object2).getName();

            return name1.compareToIgnoreCase(name2);
        }

        /**
         ***************************************************************************
         *
         * @param object
         *
         * @return
         */
        public boolean equals(Object object)
        {
            return object == this;
        }
    }
}
