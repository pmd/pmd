package net.sourceforge.pmd.jedit;

import java.util.List;
import java.util.Properties;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.PropertyDescriptor;


public class JEditPMDRule implements Rule
{
	private final Rule rule;
	private final RuleSet rs;

	public JEditPMDRule(Rule rule, RuleSet rs)
	{
		this.rule = rule;
		this.rs = rs;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.pmd.Rule#addProperties(java.util.Properties)
	 */
	public void addProperties(Properties properties)
	{
		rule.addProperties(properties);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.pmd.Rule#addProperty(java.lang.String, java.lang.String)
	 */
	public void addProperty(String name, String property)
	{
		rule.addProperty(name, property);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.pmd.Rule#apply(java.util.List, net.sourceforge.pmd.RuleContext)
	 */
	public void apply(List astCompilationUnits, RuleContext ctx)
	{
		rule.apply(astCompilationUnits, ctx);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.pmd.Rule#getBooleanProperty(java.lang.String)
	 */
	public boolean getBooleanProperty(String name)
	{
		return rule.getBooleanProperty(name);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.pmd.Rule#getDescription()
	 */
	public String getDescription()
	{
		return rule.getDescription();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.pmd.Rule#getDoubleProperty(java.lang.String)
	 */
	public double getDoubleProperty(String name)
	{
		return rule.getDoubleProperty(name);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.pmd.Rule#getExternalInfoUrl()
	 */
	public String getExternalInfoUrl()
	{
		return rule.getExternalInfoUrl();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.pmd.Rule#getIntProperty(java.lang.String)
	 */
	public int getIntProperty(String name)
	{
		return rule.getIntProperty(name);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.pmd.Rule#getMessage()
	 */
	public String getMessage()
	{
		return rule.getMessage();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.pmd.Rule#getName()
	 */
	public String getName()
	{
		return rule.getName();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.pmd.Rule#getPriority()
	 */
	public int getPriority()
	{
		return rule.getPriority();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.pmd.Rule#getPriorityName()
	 */
	public String getPriorityName()
	{
		return rule.getPriorityName();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.pmd.Rule#getProperties()
	 */
	public Properties getProperties()
	{
		return rule.getProperties();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.pmd.Rule#getRuleSetName()
	 */
	public String getRuleSetName()
	{
		return rule.getRuleSetName();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.pmd.Rule#getStringProperty(java.lang.String)
	 */
	public String getStringProperty(String name)
	{
		return rule.getStringProperty(name);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.pmd.Rule#hasProperty(java.lang.String)
	 */
	public boolean hasProperty(String name)
	{
		return rule.hasProperty(name);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.pmd.Rule#include()
	 */
	public boolean include()
	{
		return rule.include();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.pmd.Rule#setDescription(java.lang.String)
	 */
	public void setDescription(String description)
	{
		rule.setDescription(description);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.pmd.Rule#setExternalInfoUrl(java.lang.String)
	 */
	public void setExternalInfoUrl(String url)
	{
		rule.setExternalInfoUrl(url);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.pmd.Rule#setInclude(boolean)
	 */
	public void setInclude(boolean include)
	{
		rule.setInclude(include);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.pmd.Rule#setMessage(java.lang.String)
	 */
	public void setMessage(String message)
	{
		rule.setMessage(message);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.pmd.Rule#setName(java.lang.String)
	 */
	public void setName(String name)
	{
		rule.setName(name);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.pmd.Rule#setPriority(int)
	 */
	public void setPriority(int priority)
	{
		rule.setPriority(priority);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.pmd.Rule#setRuleSetName(java.lang.String)
	 */
	public void setRuleSetName(String name)
	{
		rule.setRuleSetName(name);
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.pmd.Rule#setUsesDFA()
	 */
	public void setUsesDFA()
	{
		rule.setUsesDFA();
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.pmd.Rule#usesDFA()
	 */
	public boolean usesDFA()
	{
		return rule.usesDFA();
	}

	/**
	 * @return Returns the rs.
	 */
	public RuleSet getRs()
	{
		return rs;
	}
	
	public boolean usesTypeResolution()
	{
		return rule.usesTypeResolution();
	}
	
	public void setUsesTypeResolution()
	{
		rule.setUsesTypeResolution();
	}
	
	public PropertyDescriptor propertyDescriptorFor(String propDesc)
	{
		return rule.propertyDescriptorFor(propDesc);
	}

	public void addExample(String example)
	{
		rule.addExample(example);
	}

	public void addRuleChainVisit(String astNodeName)
	{
		rule.addRuleChainVisit(astNodeName);
	}

	public List<String> getExamples()
	{
		return rule.getExamples();
	}

	public List<String> getRuleChainVisits()
	{
		return rule.getRuleChainVisits();
	}

	public boolean usesRuleChain()
	{
		return rule.usesRuleChain();
	
	}
	/**
	 * @deprecated - use getExamples() since we support multiple examples
	 */
	public String getExample()
	{
		return rule.getExample();
	}
}

