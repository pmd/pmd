/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.util.Benchmark;
import net.sourceforge.pmd.util.filter.Filter;
import net.sourceforge.pmd.util.filter.Filters;


/**
 * This class represents a collection of rules.
 *
 * @see Rule
 */
//FUTURE Implement Cloneable and clone()
public class RuleSet {

    private List<Rule> rules = new ArrayList<Rule>();
    private String fileName;
    private String name = "";
    private String description = "";
    private Language language;
    private List<String> excludePatterns = new ArrayList<String>(0);
    private List<String> includePatterns = new ArrayList<String>(0);
    private Filter<File> filter;

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
     * Add a new rule by reference to this ruleset.
     *
     * @param ruleSetFileName the ruleset which contains the rule
     * @param rule the rule to be added
     */
    public void addRuleByReference(String ruleSetFileName, Rule rule) {
        if (ruleSetFileName == null) {
            throw new RuntimeException("Adding a rule by reference is not allowed with a null rule set file name.");
        }
        if (rule == null) {
            throw new RuntimeException("Null Rule reference added to a RuleSet; that's a bug somewhere in PMD");
        }
        if (!(rule instanceof RuleReference)) {
        	RuleSetReference ruleSetReference = new RuleSetReference();
        	ruleSetReference.setRuleSetFileName(ruleSetFileName);
	        RuleReference ruleReference = new RuleReference();
	        ruleReference.setRule(rule);
	        ruleReference.setRuleSetReference(ruleSetReference);
	        rule = ruleReference;
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
     * Add all rules by reference from one RuleSet to this RuleSet.  The rules
     * can be added as individual references, or collectively as an all rule
     * reference.
     *
     * @param ruleSet the RuleSet to add
     * @param allRules 
     */
    public void addRuleSetByReference(RuleSet ruleSet, boolean allRules) {
    	if (ruleSet.getFileName() == null) {
            throw new RuntimeException("Adding a rule by reference is not allowed with a null rule set file name.");
    	}
    	RuleSetReference ruleSetReference = new RuleSetReference();
    	ruleSetReference.setRuleSetFileName(ruleSet.getFileName());
    	ruleSetReference.setAllRules(allRules);
    	for(Rule rule: ruleSet.getRules()) {
    		RuleReference ruleReference = new RuleReference();
    		ruleReference.setRule(rule);
    		ruleReference.setRuleSetReference(ruleSetReference);
    		rules.add(ruleReference);
    	}
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
    	// Initialize filter based on patterns
        if (filter == null) {
        	Filter<String> regexFilter = Filters.buildRegexFilterIncludeOverExclude(includePatterns, excludePatterns);
        	filter = Filters.toNormalizedFileFilter(regexFilter);
        }
 
        return file != null ? filter.filter(file) : true;
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

    public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
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

    public List<String> getExcludePatterns() {
        return this.excludePatterns;
    }

    public void addExcludePattern(String excludePattern) {
        this.excludePatterns.add(excludePattern);
    }

    public void addExcludePatterns(List<String> excludePatterns) {
        this.excludePatterns.addAll(excludePatterns);
    }

    public void setExcludePatterns(List<String> excludePatterns) {
    	this.excludePatterns = excludePatterns;
    }

    public List<String> getIncludePatterns() {
        return this.includePatterns;
    }
 
    public void addIncludePattern(String includePattern) {
        this.includePatterns.add(includePattern);
    }
    
    public void addIncludePatterns(List<String> includePatterns) {
        this.includePatterns.addAll(includePatterns);
    }

    public void setIncludePatterns(List<String> includePatterns) {
    	this.includePatterns = includePatterns;
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
