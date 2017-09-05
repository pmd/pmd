/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class represents a reference to RuleSet.
 */
public class RuleSetReference {
    private final String ruleSetFileName;
    private final boolean allRules;
    private final Set<String> excludes;

    public RuleSetReference(final String theFilename, final boolean allRules, final Set<String> excludes) {
        ruleSetFileName = theFilename;
        this.allRules = allRules;
        this.excludes = Collections.unmodifiableSet(new LinkedHashSet<>(excludes));
    }

    public RuleSetReference(final String theFilename, final boolean allRules) {
        ruleSetFileName = theFilename;
        this.allRules = allRules;
        this.excludes = Collections.<String>emptySet();
    }

    public RuleSetReference(final String theFilename) {
        this(theFilename, false);
    }

    public String getRuleSetFileName() {
        return ruleSetFileName;
    }

    public boolean isAllRules() {
        return allRules;
    }

    public Set<String> getExcludes() {
        return excludes;
    }
}
