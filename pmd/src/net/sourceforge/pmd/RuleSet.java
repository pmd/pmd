/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pmd.util.Benchmark;


/**
 * This class represents a collection of rules.
 *
 * @see Rule
 */
public class RuleSet {

    private static final Logger LOG = Logger.getLogger(RuleSet.class.getName());
    private List<Rule> rules = new ArrayList<Rule>();
    private String name = "";
    private String description = "";
    private Language language;
    private List<String> includePatterns = new ArrayList<String>(0);
    private List<String> excludePatterns = new ArrayList<String>(0);
    private List<Matcher> includePatternMatchers;
    private List<Matcher> excludePatternMatchers;

    /**
     * Returns the number of rules in this ruleset
     *
     * @return an int representing the number of rules
     */
    public int size() {
        return rules.size();
    }

    /**
     * Add a new rule to this ruleset
     *
     * @param rule the rule to be added
     */
    public void addRule(Rule rule) {
        if (rule == null) {
            throw new RuntimeException("Null Rule reference added to a RuleSet; that's a bug somewhere in PMD");
        }
        rules.add(rule);
    }

    /**
     * Returns the actual Collection of rules in this ruleset
     *
     * @return a Collection with the rules. All objects are of type {@link Rule}
     */
    public Collection<Rule> getRules() {
        return rules;
    }

    /**
     * @return true if any rule in the RuleSet needs the DFA layer
     */
    public boolean usesDFA() {
        for (Rule r: rules) {
            if (r.usesDFA()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the Rule with the given name
     *
     * @param ruleName the name of the rule to find
     * @return the rule or null if not found
     */
    public Rule getRuleByName(String ruleName) {
        Rule rule = null;
        for (Iterator<Rule> i = rules.iterator(); i.hasNext() && (rule == null);) {
            Rule r = i.next();
            if (r.getName().equals(ruleName)) {
                rule = r;
            }
        }
        return rule;
    }

    /**
     * Add a whole RuleSet to this RuleSet
     *
     * @param ruleSet the RuleSet to add
     */
    public void addRuleSet(RuleSet ruleSet) {
        rules.addAll(rules.size(), ruleSet.getRules());
    }

   /**
    * Check if a given source file should be checked by rules in this RuleSet.  A file
    * should not be checked if there is an <code>exclude</code> pattern which matches
    * the file, unless there is an <code>include</code> pattern which also matches
    * the file.  In other words, <code>include</code> patterns override <code>exclude</code>
    * patterns.
    *
    * @param file the source file to check
    * @return <code>true</code> if the file should be checked, <code>false</code> otherwise
    */
    public boolean applies(File file) {
    	// Initialize matchers based on patterns
        if (includePatternMatchers == null) {
            includePatternMatchers = new ArrayList<Matcher>(includePatterns.size());
            excludePatternMatchers = new ArrayList<Matcher>(excludePatterns.size());
            for (String includePattern : includePatterns) {
                includePatternMatchers.add(Pattern.compile(includePattern.trim()).matcher(""));
            }
            for (String excludePattern : excludePatterns) {
                excludePatternMatchers.add(Pattern.compile(excludePattern).matcher(""));
            }
        }

        // Apply include/exclude matchers
        boolean included = false;
        boolean excluded = false;
        if (file != null) {
            String path = file.getPath();
            // Standardize the paths separators so the same patterns can be used cross platform
            path = path.replace('\\', '/');
            LOG.log(Level.FINE, "path={0}", path);
            for (Matcher matcher : includePatternMatchers) {
                matcher.reset(path);
                included = matcher.matches();
                LOG.log(Level.FINE, "include-pattern={0}", matcher);
                LOG.log(Level.FINE, "included={0}", included);
                if (included) {
                    break;
                }
            }
            if (!included) {
                for (Matcher matcher : excludePatternMatchers) {
                    matcher.reset(path);
                    excluded = matcher.matches();
                    LOG.log(Level.FINE, "exclude-pattern={0}", matcher);
                    LOG.log(Level.FINE, "excluded={0}", excluded);
                    if (excluded) {
                    	break;
                    }
                }
            }
        }
        // Include patterns override exclude patterns.
        return included || !excluded;
    }
    
    public void start(RuleContext ctx) {
        for (Rule rule: rules) {
            rule.start(ctx);
        }
    }

    public void apply(List acuList, RuleContext ctx) {
        long start = System.nanoTime();
        for (Rule rule: rules) {
            if (!rule.usesRuleChain()) {
                rule.apply(acuList, ctx);
                long end = System.nanoTime();
                Benchmark.mark(Benchmark.TYPE_RULE, rule.getName(), end - start, 1);
                start = end;
            }
        }
    }
    
    public void end(RuleContext ctx) {
        for (Rule rule: rules) {
            rule.end(ctx);
        }
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        if ((o == null) || !(o instanceof RuleSet)) {
            return false; // Trivial
        }

        if (this == o) {
            return true; // Basic equality
        }

        RuleSet ruleSet = (RuleSet) o;
        return this.getName().equals(ruleSet.getName()) && this.getRules().equals(ruleSet.getRules());
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return this.getName().hashCode() + 13 * this.getRules().hashCode();
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getIncludePatterns() {
        return this.includePatterns;
    }
 
    public void addIncludePattern(String pattern) {
        this.includePatterns.add(pattern);
    }

    public List<String> getExcludePatterns() {
        return this.excludePatterns;
    }

    public void addExcludePattern(String pattern) {
        this.excludePatterns.add(pattern);
    }

	public boolean usesTypeResolution() {
        for (Rule r: rules) {
            if (r.usesTypeResolution()) {
                return true;
            }
        }
        return false;
	}

}
