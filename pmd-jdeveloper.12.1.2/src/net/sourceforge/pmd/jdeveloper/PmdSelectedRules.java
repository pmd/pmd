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


public class PmdSelectedRules {

    // Rule -> JCheckBox
    private final transient Map<Rule, JCheckBox> rules = 
        new TreeMap<Rule, JCheckBox>(new Comparator<Rule>() {
                public int compare(final Rule obj1, final Rule obj2) {
                    return obj1.getName().compareTo(obj2.getName());
                }
            });

    public PmdSelectedRules(final SettingsStorage settings) throws RuleSetNotFoundException {
        final RuleSetFactory rsf = new RuleSetFactory();
        for (final Iterator<RuleSet> iter = rsf.getRegisteredRuleSets(); 
             iter.hasNext(); ) {
            final RuleSet rset = iter.next();
            for (Rule rule: rset.getRules()) {
                rules.put(rule, createCheckBox(rule.getName(), settings));
            }
        }
    }

    public int size() {
        return rules.size();
    }

    public Rule getRule(final JCheckBox candidate) {
        for (Rule rule: rules.keySet()) {
            final JCheckBox box = rules.get(rule);
            if (box.equals(candidate)) {
                return rule;
            }
        }
        final SettingsException exc = 
            new SettingsException("Couldn't find a rule that mapped to the passed in JCheckBox " + 
                                  candidate);
        Util.showError(exc, PmdAddin.PMD_TITLE);
        return null;
    }

    public JCheckBox get(final Object key) {
        return rules.get(key);
    }

    public JCheckBox[] getAllBoxes() {
        return rules.values().toArray(new JCheckBox[rules.size()]);
    }

    public void save(final SettingsStorage settings) throws SettingsException {
        final Properties properties = new Properties();
        for (Rule rule: rules.keySet()) {
            properties.setProperty("pmd.rule." + rule.getName(), 
                                   String.valueOf(get(rule).isSelected()));
        }
        settings.save(properties);
    }

    public RuleSet getSelectedRules() {
        final RuleSet newRuleSet = new RuleSet();
        for (Rule rule: rules.keySet()) {
            if (get(rule).isSelected()) {
                newRuleSet.addRule(rule);
            }
        }
        return newRuleSet;
    }

    private JCheckBox createCheckBox(final String name, 
                                     final SettingsStorage settings) {
        final JCheckBox box = new JCheckBox(name);
        try {
            box.setSelected(load(settings, name));
        } catch (SettingsException se) {
            Util.logMessage(se.getStackTrace());
            Util.showError(se, PmdAddin.PMD_TITLE);
        }
        return box;
    }

    private boolean load(final SettingsStorage settings, 
                         final String name) throws SettingsException {
        return Boolean.valueOf(settings.load("pmd.rule." + name));
    }
}
