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
    private Map<Rule, JCheckBox> rules = 
        new TreeMap<Rule, JCheckBox>(new Comparator<Rule>() {
                public int compare(Rule o1, Rule o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });

    public SelectedRules(SettingsStorage settings) throws RuleSetNotFoundException {
        RuleSetFactory rsf = new RuleSetFactory();
        for (Iterator<RuleSet> itt = rsf.getRegisteredRuleSets(); 
             itt.hasNext(); ) {
            RuleSet rs = itt.next();
            for (Rule rule: rs.getRules()) {
                rules.put(rule, createCheckBox(rule.getName(), settings));
            }
        }
    }

    public int size() {
        return rules.size();
    }

    public Rule getRule(JCheckBox candidate) {
        for (Rule rule: rules.keySet()) {
            JCheckBox box = rules.get(rule);
            if (box.equals(candidate)) {
                return rule;
            }
        }
        throw new RuntimeException("Couldn't find a rule that mapped to the passed in JCheckBox " + 
                                   candidate);
    }

    public JCheckBox get(Object key) {
        return rules.get(key);
    }

    public JCheckBox[] getAllBoxes() {
        return rules.values().toArray(new JCheckBox[rules.size()]);
    }

    public void save(SettingsStorage settings) throws SettingsException {
        Properties properties = new Properties();
        for (Rule rule: rules.keySet()) {
            properties.setProperty("pmd.rule." + rule.getName(), 
                                   String.valueOf(get(rule).isSelected()));
        }
        settings.save(properties);
    }

    public RuleSet getSelectedRules() {
        RuleSet newRuleSet = new RuleSet();
        for (Rule rule: rules.keySet()) {
            if (get(rule).isSelected()) {
                newRuleSet.addRule(rule);
            }
        }
        return newRuleSet;
    }

    private JCheckBox createCheckBox(String name, SettingsStorage settings) {
        JCheckBox box = new JCheckBox(name);
        try {
            box.setSelected(load(settings, name));
        } catch (SettingsException se) {
            System.out.println("Can't load settings so this rule will not be enabled");
        }
        return box;
    }

    private boolean load(SettingsStorage settings, 
                         String name) throws SettingsException {
        return Boolean.valueOf(settings.load("pmd.rule." + name));
    }
}
