/*
 *  User: tom
 *  Date: Jul 9, 2002
 *  Time: 1:18:38 PM
 */
package net.sourceforge.pmd.jedit;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSets;

import org.gjt.sp.jedit.jEdit;

import javax.swing.JCheckBox;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;


/**
 *  Description of the Class
 *
 * @author     jiger.p
 * @created    April 22, 2003
 */
public class SelectedRules
{

	// Rule -> JCheckBox
	private final Map rules = new TreeMap(
							new Comparator()
							{
								public int compare(Object o1, Object o2)
								{
									Rule r1 = (Rule)o1;
									Rule r2 = (Rule)o2;
									return r1.getName().compareTo(r2.getName());
								}
							});


	/**
	 *  Constructor for the SelectedRules object
	 *
	 * @exception  RuleSetNotFoundException  Description of the Exception
	 */
	public SelectedRules() throws RuleSetNotFoundException
	{
		RuleSetFactory rsf = new RuleSetFactory();
		for(Iterator i = rsf.getRegisteredRuleSets(); i.hasNext(); )
		{
			RuleSet rs = (RuleSet)i.next();
			System.out.println("Added RuleSet " + rs.getName() + " descriprion "+ rs.getDescription() +" language "+ rs.getLanguage());
			addRuleSet2Rules(rs);
		}

		//Load custom RuleSets if any.

		String customRuleSetPath = jEdit.getProperty("pmd.customRulesPath");

		if(!(customRuleSetPath == null))
		{
			StringTokenizer strtok = new StringTokenizer(customRuleSetPath, ",");
			while(strtok.hasMoreTokens())
			{
				RuleSet rs = rsf.createRuleSet(strtok.nextToken());
				addRuleSet2Rules(rs);
			}
		}
	}


	/**
	 *  Description of the Method
	 *
	 * @return    Description of the Return Value
	 */
	public int size()
	{
		return rules.size();
	}


	/**
	 *  Gets the rule attribute of the SelectedRules object
	 *
	 * @param  candidate  Description of the Parameter
	 * @return            The rule value
	 */
	public Rule getRule(JCheckBox candidate)
	{
		for(Iterator i = rules.keySet().iterator(); i.hasNext(); )
		{
			Rule rule = (Rule)i.next();
			JCheckBox box = (JCheckBox)rules.get(rule);
			if(box.equals(candidate))
			{
				return rule;
			}
		}
		throw new RuntimeException("Couldn't find a rule that mapped to the passed in JCheckBox " + candidate);
	}


	/**
	 *  Description of the Method
	 *
	 * @param  key  Description of the Parameter
	 * @return      Description of the Return Value
	 */
	public JCheckBox get(Object key)
	{
		return (JCheckBox)rules.get(key);
	}


	/**
	 *  Gets the allBoxes attribute of the SelectedRules object
	 *
	 * @return    The allBoxes value
	 */
	public Object[] getAllBoxes()
	{
		Object[] foo = new Object[rules.size()];
		int idx = 0;
		for(Iterator i = rules.values().iterator(); i.hasNext(); )
		{
			foo[idx] = i.next();
			idx++;
		}
		return foo;
	}


	/**  Description of the Method */
	public void save()
	{
		for(Iterator i = rules.keySet().iterator(); i.hasNext(); )
		{
			Rule rule = (Rule)i.next();
			jEdit.setBooleanProperty(PMDJEditPlugin.OPTION_RULES_PREFIX + rule.getName(), get(rule).isSelected());
		}
	}


	/**
	 *  Gets the selectedRules attribute of the SelectedRules object
	 *
	 * @return    The selectedRules value
	 */
	public RuleSets getSelectedRules()
	{
		RuleSets newRuleSets = new RuleSets();
		
		Map rulesetmap = new HashMap();
		
		for(Iterator i = rules.keySet().iterator(); i.hasNext(); )
		{
			Rule rule = (Rule)i.next();
			if(get(rule).isSelected())
			{
				RuleSet rs = ((JEditPMDRule)rule).getRs();
				if(rulesetmap.get(rs.getName()) != null)
				{
					RuleSet existingRs = (RuleSet) rulesetmap.get(rs.getName());
					existingRs.addRule(rule);
				}
				else
				{
					//Create a new RuleSet and register in RuleSets and the Map. We copy critical attributes such as name, language which will be used for processing by PMD.
					RuleSet newrs = new RuleSet();
					newrs.setName(rs.getName());
					newrs.setDescription(rs.getDescription());
					newrs.setLanguage(rs.getLanguage());
					
					newrs.addRule(rule);
					rulesetmap.put(rs.getName(), newrs);
					newRuleSets.addRuleSet(newrs);
				}
			}
		}
		return newRuleSets;
	}


	/**
	 *  Description of the Method
	 *
	 * @param  name  Description of the Parameter
	 * @return       Description of the Return Value
	 */
	private JCheckBox createCheckBox(String name)
	{
		JCheckBox box = new JCheckBox(name);
		box.setSelected(jEdit.getBooleanProperty(PMDJEditPlugin.OPTION_RULES_PREFIX + name, true));
		return box;
	}


	/**
	 *  Adds a feature to the RuleSet2Rules attribute of the SelectedRules object
	 *
	 * @param  rs  The feature to be added to the RuleSet2Rules attribute
	 */
	private void addRuleSet2Rules(RuleSet rs)
	{
		for(Iterator j = rs.getRules().iterator(); j.hasNext(); )
		{
			Rule rule = (Rule)j.next();
			rules.put(new JEditPMDRule(rule, rs), createCheckBox(rule.getName()));
		}
	}
}

