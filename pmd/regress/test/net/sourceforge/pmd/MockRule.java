/*
 * User: tom
 * Date: Jun 28, 2002
 * Time: 11:05:13 AM
 */
package test.net.sourceforge.pmd;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.reports.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;

import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.Iterator;

public class MockRule implements Rule {

    private String name;
    private String desc;
    private Set violations = new HashSet();

    public MockRule() {}

    public MockRule(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public void addViolation( RuleViolation violation ) {
	violations.add( violation );
    }

    public String getName() {return name;}
    public String getDescription() {return desc;}

    public void apply(List astCompilationUnits, RuleContext ctx) {
	Report report = ctx.getReport();

	Iterator vs = violations.iterator();
	while (vs.hasNext()) {
	    report.addRuleViolation( (RuleViolation) vs.next() );
	}
    }
}
