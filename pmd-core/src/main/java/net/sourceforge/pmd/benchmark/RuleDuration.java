/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.benchmark;

import net.sourceforge.pmd.Rule;

public class RuleDuration implements Comparable<RuleDuration> {

	public Rule rule;
	public long time;

    public RuleDuration(long elapsed, Rule rule) {
        this.rule = rule;
        this.time = elapsed;
    }

	@Override
    public int compareTo(RuleDuration other) {
		if (other.time < time) {
			return -1;
		} else if (other.time > time) {
			return 1;
		}

		return rule.getName().compareTo(other.rule.getName());
	}


}
