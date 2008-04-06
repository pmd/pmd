package net.sourceforge.pmd.jdeveloper;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.swing.JCheckBox;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;


public class SelectedRules {

    // Rule -> JCheckBox
    private final transient Map rules = new TreeMap(new Comparator() {
                public int compare(final Object obj1, final Object obj2) {
                    final Rule rul1 = (Rule)obj1;
                    final Rule rul2 = (Rule)obj2;
                    return rul1.getName().compareTo(rul2.getName());
                }
            });

    public SelectedRules(final SettingsStorage settings) throws RuleSetNotFoundException {
        final RuleSetFactory rsf = new RuleSetFactory();
        for (final Iterator i = rsf.getRegisteredRuleSets(); i.hasNext(); ) {
            final RuleSet rset = (RuleSet)i.next();
            for (final Iterator j = rset.getRules().iterator(); j.hasNext(); ) {
                final Rule rule = (Rule)j.next();
                rules.put(rule, createCheckBox(rule.getName(), settings));
            }
        }
    }

    public int size() {
        return rules.size();
    }

    public Rule getRule(final JCheckBox candidate) {
        for (final Iterator i = rules.keySet().iterator(); i.hasNext(); ) {
            final Rule rule = (Rule)i.next();
            final JCheckBox box = (JCheckBox)rules.get(rule);
            if (box.equals(candidate)) {
                return rule;
            }
        }
        throw new RuntimeException("Couldn't find a rule that mapped to the passed in JCheckBox " + 
                                   candidate);
    }

    public JCheckBox get(final Object key) {
        return (JCheckBox)rules.get(key);
    }

    public Object[] getAllBoxes() {
        Object[] foo = new Object[rules.size()];
        int idx = 0;
        for (final Iterator i = rules.values().iterator(); i.hasNext(); ) {
            foo[idx] = i.next();
            idx++;
        }
        return foo;
    }

    public void save(final SettingsStorage settings) throws SettingsException {
        final Properties properties = new Properties();
        for (final Iterator i = rules.keySet().iterator(); i.hasNext(); ) {
            final Rule rule = (Rule)i.next();
            properties.setProperty("pmd.rule." + rule.getName(), 
                                   String.valueOf(get(rule).isSelected()));
        }
        settings.save(properties);
    }

    public RuleSet getSelectedRules() {
        final RuleSet newRuleSet = new RuleSet();
        for (final Iterator i = rules.keySet().iterator(); i.hasNext(); ) {
            final Rule rule = (Rule)i.next();
            if (get(rule).isSelected()) {
                newRuleSet.addRule(rule);
            }
        }
        return newRuleSet;
    }

    private JCheckBox createCheckBox(final String name, final SettingsStorage settings) {
        final JCheckBox box = new JCheckBox(name);
        try {
            box.setSelected(load(settings, name));
        } catch (SettingsException se) {
            System.out.println("Can't load settings so this rule will not be enabled");
        }
        return box;
    }

    private boolean load(final SettingsStorage settings, 
                         final String name) throws SettingsException {
        return Boolean.valueOf(settings.load("pmd.rule." + 
                                             name)).booleanValue();
    }

}
